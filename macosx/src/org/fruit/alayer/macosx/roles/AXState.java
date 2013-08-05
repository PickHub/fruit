/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.macosx.roles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.fruit.alayer.macosx.AXProps;
import org.fruit.alayer.macosx.AXRoles;
import org.fruit.alayer.IProperty;
import org.fruit.alayer.IRect;
import org.fruit.alayer.IState;
import org.fruit.alayer.IWidget;
import org.fruit.alayer.Point;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Utils;
import org.fruit.alayer.macosx.AX;

public final class AXState implements IState {

	private static final long serialVersionUID = 5536233442604263388L;
	final AXNode root;
	final Rect viewPort;
	final Map<IntPoint, AXNode> widgetMap = new HashMap<IntPoint, AXNode>();
	transient final HashMap<CFTypeRef, AXNode> refMap = new HashMap<CFTypeRef, AXNode>();
	final ArrayList<AXNode> nodes = new ArrayList<AXNode>();
	final ArrayDeque<AXNode> currentApps = new ArrayDeque<AXNode>();
	final ArrayDeque<AXNode> currentWnds = new ArrayDeque<AXNode>();
	final ArrayDeque<AXNode> currentMenus = new ArrayDeque<AXNode>();
	final ArrayDeque<AXNode> currentSheets = new ArrayDeque<AXNode>();	
	private static final ArrayList<AXNode> EMPTY_ARRAY_LIST = new ArrayList<AXNode>(0);
	
	private final class IntPoint{
		final int x, y;
		public IntPoint(double x, double y){
			this.x = (int) x;
			this.y = (int) y;
		}
		public int hashCode(){ return x + (int)viewPort.width() * y; }
		public boolean equals(Object o){
			if(this == o)
				return true;
			IntPoint oip = (IntPoint) o;  // instance of check not necessary here (we assume only IntPoints in the hashmap)!
			return oip.x == x && oip.y == y;
		}
	}
	
	public AXState(long sysRef, long... appRefs){
        
		// get main monitor (for now we only support 1 monitor!! )
        long mainScreen = AX.createMainScreen();
        if(mainScreen == 0)
            throw new RuntimeException("Main Screen not accessible!");
        float[] screenFrame = AX.getScreenFrame(mainScreen);
        AX.CFRelease(mainScreen);
        viewPort = new Rect(screenFrame[0], -screenFrame[1],  screenFrame[2], screenFrame[3]);

        AX.CFRetain(sysRef);
		registerNode(root = new AXNode(this, AXRoles.AXSystemWide, new CFTypeRef(sysRef)));

		for(long ar : appRefs){
			AX.CFRetain(ar);
			AXNode app = new AXNode(this, AXRoles.AXApplication, new CFTypeRef(ar));
			root.addChild(app);
			registerNode(app);
		}
		firstPass(root);
		secondPass(root);
	}
		
	public AXNode getVisibleWidgetAt(double x, double y){
		if(!viewPort.contains(x, y))
			return null;
		
		IntPoint ip = new IntPoint(x, y);
		if(widgetMap.containsKey(ip))
			return widgetMap.get(ip);
		
		AXNode ret = null;
		Long ref = root.getRefAt(x, y);
		if(ref != null){
			ret = lookUp(new CFTypeRef(ref));
			AX.CFRelease(ref);
		}

		widgetMap.put(ip, ret);
		return ret;
	}

	public IWidget getRoot() { return root; }
	public IRect getViewPort() { return viewPort; }
	public IWidget getParent(IWidget w) { return check(w).parent; }
	public IWidget getChild(IWidget w, int i) {	return check(w).getChild(i); }
	public int childCount(IWidget w) { return check(w).childCount(); }

	@SuppressWarnings("unchecked")
	public final <T> T get(IWidget w, IProperty p) {
		if(p.equals(Tags.VisibleWidgetMap))
			return (T)new VisibleWidgetMap(this);
		return check(w).get(p);
	}
	
	public Iterable<IProperty> properties(IWidget w) { return null;	}

	private AXNode check(IWidget w){
		try{
			Utils.CheckNull(w);
			AXNode axw = (AXNode) w;
			if(axw.state != this)
				throw new IllegalArgumentException("The widget does not belong to this state!");
			return axw;
		}catch(ClassCastException cce){
			throw new IllegalArgumentException("The widget does not belong to this state!");
		}
	}
	
	private void firstPass(AXNode n){
		
		switch(n.role){
		case AXSystemWide:
			n.focusedApp = yield(n, AX.kAXFocusedApplicationAttribute, AXRoles.AXApplication);
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));			
			break;
		
		case AXApplication:
			currentApps.push(n);
			n.mainWnd = yield(n, AX.kAXMainWindowAttribute, null);
			batchYield(n, AX.kAXWindowsAttribute, AXRoles.AXWindow);
			n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));
			n.activeWnd = yield(n, AX.kAXFocusedWindowAttribute, null);
			if(n.activeWnd != null && n.activeWnd.role == AXRoles.AXSheet)
				n.activeWnd = yield(n.activeWnd, AX.kAXWindowAttribute, AXRoles.AXWindow);
			currentApps.pop();
			break;
		
		case AXWindow:
			currentWnds.push(n);
			n.defaultBtn = yield(n, AX.kAXDefaultButtonAttribute, AXRoles.AXButton);
			n.cancelBtn = yield(n, AX.kAXCancelButtonAttribute, AXRoles.AXButton);
			n.closeBtn = yield(n, AX.kAXCloseButtonAttribute, AXRoles.AXButton);
			n.minimizeBtn = yield(n, AX.kAXMinimizeButtonAttribute, AXRoles.AXButton);
			n.zoomBtn = yield(n, AX.kAXZoomButtonAttribute, AXRoles.AXButton);		
			n.growArea = yield(n, AX.kAXGrowAreaAttribute, AXRoles.AXGrowArea);
			n.proxy = yield(n, AX.kAXProxyAttribute, null);
			
			Boolean modal = n.axBoolean(AX.kAXModalAttribute);
			if(!Utils.True(modal))
				modal = (n.defaultBtn != null) || (n.cancelBtn != null) || 
					(n.closeBtn == null && n.minimizeBtn == null && n.zoomBtn == null && 
					(n == currentApps.peek().mainWnd || "AXDialog".equals(n.axString(AX.kAXSubroleAttribute))));
			n.modal = modal;
			if(n.modal)
				currentApps.peek().addModal(n);
			n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));			
			currentWnds.pop(); 
			break;
		
		case AXMenuBarItem:
			Boolean selected = n.axBoolean(AX.kAXSelectedAttribute);
			if(Utils.True(selected)){
				n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
				for(int i = 0; i < n.childCount(); i++)
					firstPass(n.getChild(i));				
			}
			break;

		case AXTable:
			
			boolean hasVisibleRows = false;
			for(AXNode cn : batchYieldIfOrphaned(n, AX.kAXVisibleRowsAttribute, AXRoles.AXRow)){
				n.addChild(cn);
				hasVisibleRows = true;
			}
			
			boolean hasVisibleCells = false;
			for(AXNode cn : batchYieldIfOrphaned(n, AX.kAXVisibleCellsAttribute, AXRoles.AXCell)){
				n.addChild(cn);
				hasVisibleCells = true;
			}
			
			for(AXNode cn : batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null)){
				if(cn.role == AXRoles.AXRow){
					if(!hasVisibleRows)	n.addChild(cn);
				}else if(cn.role == AXRoles.AXCell){
					if(!hasVisibleCells) n.addChild(cn);
				}else{
					n.addChild(cn);
				}
			}
			
			for(int i = 0; i < n.childCount(); i++){
				firstPass(n.getChild(i));			
			}
			break;
			
		case AXMenuItem:
			currentMenus.push(n);
			currentApps.peek().hasOpenMenus = true;			
			//selected = n.axBoolean(AX.kAXSelectedAttribute);
			//if(Utils.True(selected)){
				n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
				for(int i = 0; i < n.childCount(); i++)
					firstPass(n.getChild(i));				
			//}else{
			//	n.setChildren(batchYieldIfOrphaned(n, AX.kAXVisibleChildrenAttribute, null));
			//	for(int i = 0; i < n.childCount(); i++)
			//		firstPass(n.getChild(i));								
			//}
			currentMenus.pop();
			break;
			
		case AXMenu:
			currentMenus.push(n);
			
			boolean visible = false;
			Point p = n.getPos();
			if(p != null)
				visible = Utils.True(root.isUnder(n, p.x() + 1, p.y() + 1));
			n.rendered = visible;
			if(visible){
				currentApps.peek().hasOpenMenus = true;
				n.setChildren(batchYieldIfOrphaned(n, AX.kAXVisibleChildrenAttribute, null));
				if(n.children == null)
					n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
				for(int i = 0; i < n.childCount(); i++)
					firstPass(n.getChild(i));
			}
			currentMenus.pop();
			break;
		
		case AXSheet:
			currentSheets.push(n);
			if(!currentWnds.isEmpty())
				currentWnds.peek().hasSheet = true;
			n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));				
			currentSheets.pop(); 
			break;
		
		case AXSlider:
			String orientation = n.axString(AX.kAXOrientationAttribute);
			if("AXHorizontalOrientation".equals(orientation))
				n.properties.put(Tags.Angle, 0.0);
			else if("AXVerticalOrientation".equals(orientation))
				n.properties.put(Tags.Angle, -90.0);
			n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));							
			break;
		
		case AXTabGroup:
			ArrayList<AXNode> tabs = batchYield(n, AX.kAXTabsAttribute, null);
			if(tabs != null){
				IWidget[] atabs = new IWidget[tabs.size()];
				tabs.toArray(atabs);
				n.properties.put(AXProps.Tabs, atabs);
			}
			n.activeTab = yield(n, AX.kAXValueAttribute, null);
			n.properties.put(AXProps.Value, n.activeTab);
			n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));							
			break;			
			
		case AXScrollBar:
			orientation = n.axString(AX.kAXOrientationAttribute);
			if("AXHorizontalOrientation".equals(orientation))
				n.properties.put(Tags.Angle, 0.0);
			else if("AXVerticalOrientation".equals(orientation))
				n.properties.put(Tags.Angle, -90.0);
			n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));
			break;
			
		case AXValueIndicator:
			if(n.parent != null && (n.parent.role == AXRoles.AXSlider || n.parent.role == AXRoles.AXScrollBar)){
				n.parent.thumb = n;
				refMap.put(n.ref, n.parent);
			}
			break;
			
		case AXButton:
			if(n.parent != null && (n.parent.role == AXRoles.AXSlider || n.parent.role == AXRoles.AXScrollBar) && 
			("increment page button".equals(n.get(AXProps.RoleDescription)) || "decrement page button".equals(n.get(AXProps.RoleDescription)))){
				refMap.put(n.ref, n.parent);
			}
			break;
			
		default:
			n.setChildren(batchYieldIfOrphaned(n, AX.kAXChildrenAttribute, null));
			for(int i = 0; i < n.childCount(); i++)
				firstPass(n.getChild(i));
			break;
		}
	}
		
	private void secondPass(AXNode n){
		switch(n.role){
		case AXSystemWide:
			n.foreground = true;
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));
			break;
		
		case AXApplication: 
			currentApps.push(n);
			n.foreground = root.focusedApp == n;
			n.rendered = n.foreground || !Utils.True(n.axBoolean(AX.kAXHiddenAttribute));
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));
			currentApps.pop();
			break;
		
		case AXWindow:
			currentWnds.push(n);
			AXNode ca = currentApps.peek();
			n.foreground = ca.activeWnd == n && currentApps.peek().foreground;
			n.blocked = (/*!n.foreground &&*/ !n.modal && ca.hasModals()) || ca.hasOpenMenus || (!n.foreground && ca.hasModals() && ca.activeWnd != null);
			n.rendered = ca.rendered && (n.foreground || !Utils.True(n.axBoolean(AX.kAXMinimizedAttribute)));
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));
			currentWnds.pop();
			break;
		
		case AXMenuBarItem:
			n.foreground = true;
			n.rendered = true;
			n.blocked = isBlocked();
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));			
			break;
		case AXMenuItem:
			currentMenus.push(n);
			n.foreground = true;
			n.rendered = true;
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));							
			currentMenus.pop();
			break;

		case AXMenu:
			currentMenus.push(n);
			n.foreground = true;
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));							
			currentMenus.pop();
			break;
					
		case AXSheet:
			currentSheets.push(n);
			n.foreground = isForeground();
			n.blocked = isBlocked();
			n.rendered = isRendered();
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));				
			currentSheets.pop(); 
			break;

		case AXButton:
			if(n.parent != null && (n.parent.role == AXRoles.AXSlider || n.parent.role == AXRoles.AXScrollBar)){
				n.foreground = false;
				n.rendered = false;
				n.blocked = false;
				n.properties.put(Tags.VisibleShape, null);
				n.properties.put(Tags.Shape, null);
			}else{
				n.foreground = isForeground();
				n.blocked = isBlocked();
				n.rendered = isRendered();				
			}
			break;

		default:
			n.foreground = isForeground();
			n.blocked = isBlocked();
			n.rendered = isRendered();
			for(int i = 0; i < n.childCount(); i++)
				secondPass(n.getChild(i));
			break;
		}
	}
	
	
	
	private boolean insideWnd(){ return !currentWnds.isEmpty(); }
	private boolean menusOpened(){ return currentApps.peek().hasOpenMenus; }
	private boolean existModals(){ return currentApps.peek().hasModals(); }
	private boolean outsideOfOpenSheet(){
		AXNode wnd = currentWnds.peek();
		return wnd == null ? false : (wnd.hasSheet && currentSheets.isEmpty());
	}
	
	private boolean outsideOfOpenMenu(){
		return menusOpened() && currentMenus.isEmpty();
	}
	
	private boolean outsideOfModalWnd(){
		AXNode activeWnd = currentApps.peek().activeWnd;
		AXNode currentWnd = currentWnds.peek();
		return insideWnd() && existModals() && ((!currentWnd.modal /* && activeWnd != currentWnd */) || (activeWnd != currentWnd && activeWnd != null && activeWnd.modal));
	}
	
	private boolean isBlocked(){ return outsideOfModalWnd() || outsideOfOpenMenu() || outsideOfOpenSheet(); }
	private boolean isForeground(){ 
		boolean ret = currentApps.peek().foreground;
		AXNode wnd = currentWnds.peek();
		ret &= (wnd == null) || (wnd.foreground && !menusOpened()) || !currentMenus.isEmpty();
		return ret;
	}
	private boolean isRendered(){ return currentApps.peek().rendered; }
		
	private ArrayList<AXNode> batchYieldIfOrphaned(AXNode parentNode, String kaxAttr, AXRoles role){
		ArrayList<AXNode> ret = batchYield(parentNode, kaxAttr, role);
		
		if(ret != null){
			ArrayList<AXNode> candidates = new ArrayList<AXNode>();
			for(AXNode n : ret){
				if(n.parent == null)
					candidates.add(n);
			}
			return candidates;
		}
		return EMPTY_ARRAY_LIST;
	}
	
	private ArrayList<AXNode> batchYield(AXNode parentNode, String kaxAttr, AXRoles role){
		long[] refs = AX.AXUIElementCopyReferenceArrayAttributeValue(parentNode.ref.raw(), kaxAttr);
		
		if(refs != null){
			ArrayList<AXNode> ret = new ArrayList<AXNode>(refs.length);
			for(int i = 0; i < refs.length; i++)
				ret.add(yield(new CFTypeRef(refs[i]), role));
			return ret;
		}
		return null;
	}
	
	private AXNode yield(AXNode parentNode, String kaxAttr, AXRoles role){
		long ref = AX.AXUIElementCopyReferenceAttributeValue(parentNode.ref.raw(), kaxAttr);
		if(ref > 0)
			return yield(new CFTypeRef(ref), role);
		return null;
	}
	
	private AXNode yieldIfOrphaned(AXNode parentNode, String kaxAttr, AXRoles role){
		AXNode ret = yield(parentNode, kaxAttr, role);
		if(ret != null && ret.parent == null)
			return ret;
		return null;
	}
	
	private AXNode yield(CFTypeRef ref, AXRoles role){
		AXNode ret = refMap.get(ref);
		return ret == null ? createNode(ref, role) : ret;
	}

	private AXNode createNode(CFTypeRef ref, AXRoles role){
		if(role == null)
			role = AXRoles.get(AX.AXUIElementCopyStringAttributeValue(ref.raw(), AX.kAXRoleAttribute));
		AXNode ret = new AXNode(this, role, ref);
		registerNode(ret);
		return ret;		
	}

	private void registerNode(AXNode n){
		nodes.add(n);
		refMap.put(n.ref, n);
	}
	
	private AXNode lookUp(CFTypeRef r){
		if(refMap != null)
			return refMap.get(r);
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<IWidget> iterator() { return (Iterator)nodes.iterator(); }
	
	public void finalize(){ release(); }

	public void release(){
		for(AXNode n : nodes)
			n.ref.release();
	}

	public int size() {	return nodes.size(); }

	public boolean isMember(IWidget w) {
		if(w != null && w instanceof AXNode)
			return ((AXNode) w).state == this;
		return false;
	}
}