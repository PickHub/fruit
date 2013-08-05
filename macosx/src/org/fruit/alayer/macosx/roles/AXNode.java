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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.fruit.alayer.IProperty;
import org.fruit.alayer.IWidget;
import org.fruit.alayer.Point;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Rect;
import org.fruit.alayer.macosx.AX;
import org.fruit.alayer.macosx.AXProps;
import org.fruit.alayer.macosx.AXRoles;

public final class AXNode implements IWidget, Serializable{
			
	private static final long serialVersionUID = 4113878499050219671L;
	final AXRoles role;
	final transient CFTypeRef ref;
	final AXState state;
	boolean blocked, foreground, rendered;
	AXNode parent, defaultParent;
	ArrayList<AXNode> children;
	boolean hasSheet, hasOpenMenus, link, modal;
	ArrayList<AXNode> modals;
	AXNode activeWnd, mainWnd, focusedApp, thumb;
	AXNode defaultBtn, cancelBtn, closeBtn, minimizeBtn, zoomBtn, growArea, activeTab, proxy;
	
	final HashMap<IProperty, Object> properties = new HashMap<IProperty, Object>();
	
	public AXNode(AXState state, AXRoles role, CFTypeRef ref){
		this.role = role;
		this.ref = ref;
		this.state = state;
	}
	
	public void addChild(AXNode c){
		if(children == null)
			children = new ArrayList<AXNode>();
		children.add(c); 
		c.parent = this;
	}
	
	public void addModal(AXNode m){ if(modals == null) modals = new ArrayList<AXNode>(); modals.add(m); }
	public int childCount(){ return children == null ? 0 : children.size(); }
	public void setChildren(ArrayList<AXNode> children){
		this.children = children;
		for(int i = 0; i < childCount(); i++)
			children.get(i).parent = this;
	}
	public AXNode getChild(int i){ return children.get(i); }
	public AXNode getParent(){ return parent; }
	public boolean hasModals(){ return modals != null && !modals.isEmpty(); }
	
	public long[] axReferences(String attr){
		if(ref != null)
			return AX.AXUIElementCopyReferenceArrayAttributeValue(ref.raw(), attr);
		return null;
	}
	public Long axReference(String attr){
		if(ref != null){
			long val = AX.AXUIElementCopyReferenceAttributeValue(ref.raw(), attr);
			return val > 0 ? val : null;
		}
		return null;
	}
	public Boolean axBoolean(String attr){
		if(ref != null){
			int val = AX.AXUIElementCopyBooleanAttributeValue(ref.raw(), attr);
			return val < 0 ? null : val > 0;
		}
		return null;
	}
	public String axString(String attr){
		if(ref != null)
			return AX.AXUIElementCopyStringAttributeValue(ref.raw(), attr);
		return null;
	}
	public Long axLong(String attr){
		if(ref != null)
			return AX.AXUIElementCopyReferenceAttributeValue(ref.raw(), attr);
		return null;
	}
	public Rect axRect(){
		if(ref != null){
			double r[] = AX.getRect(ref.raw());
			if(r != null)
				return Rect.Make(r);
		}
		return null;
	}
	public Point getPos(){
		if(ref != null){
			double p[] = AX.getPos(ref.raw());
			if(p != null)
				return new Point(p[0], p[1]);
		}
		return null;		
	}
	public Point getSize(){
		if(ref != null){
			double p[] = AX.getSize(ref.raw());
			if(p != null)
				return new Point(p[0], p[1]);
		}
		return null;		
	}
	public Long getRefAt(double x, double y){  // only invoke on AXApplication and AXSystemWide
		if(ref != null){
			long r = AX.AXUIElementCopyElementAtPosition(ref.raw(), (float)x, (float)y);
			if(r > 0)
				return r;
		}
		return null;
	}
	public Boolean isAt(AXNode n, double x, double y){ // only invoke on AXApplication and AXSystemWide
		if(ref != null && n.ref != null){
			long r = AX.AXUIElementCopyElementAtPosition(ref.raw(), (float)x, (float)y);
			if(r > 0){
				boolean ret = AX.CFEqual(r, n.ref.raw());
				AX.CFRelease(r);
				return ret;
			}
		}
		return null;
	}
	public Boolean isUnder(AXNode n, double x, double y){  // only invoke on AXApplication and AXSystemWide
		if(ref != null && n.ref != null){
			Long currentRef = getRefAt(x, y);
			Long temp;
			
			while(currentRef != null){
				if(AX.CFEqual(currentRef, n.ref.raw())){
					AX.CFRelease(currentRef);
					return true;
				}else{
					temp = currentRef;
					currentRef = AX.AXUIElementCopyReferenceAttributeValue(currentRef, AX.kAXParentAttribute);
					if(currentRef <= 0)
						currentRef = null;
					AX.CFRelease(temp);
				}
			}
			return false;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public final <T> T get(IProperty p) {
		Object ret = properties.get(p);
		if(ret != null)
			return (T) ret;
		
		if(properties.containsKey(p))
			return null;
		
		if(p instanceof Tags)
			ret = fetchCommon((Tags)p);
		else if(p instanceof AXProps)
			ret = fetchAX((AXProps)p);

		properties.put(p, ret);
		return (T)ret;		
	}
	
	public Object fetchCommon(Tags p){
		
		switch(p){
		case Role: return role;
		case Shape: 
			if(role == AXRoles.AXValueIndicator) 
				return null;
			return axRect();
		case VisibleShape:
			if(role == AXRoles.AXValueIndicator) 
				return null;
			Rect r = axRect();
			if(r != null)
				return new LazyShape(this, state, r);
			return null;
		case Blocked: return blocked;
		case Enabled:
			Boolean v = axBoolean(AX.kAXEnabledAttribute);
			Boolean ret = v == null ? true : v;
			if(thumb == null)
				return ret;
			Boolean thumbEnabled = thumb.axBoolean(AX.kAXEnabledAttribute);
			if(thumbEnabled == null)
				thumbEnabled = true;
			return ret && thumbEnabled;
		case KeyboardFocus: return false;
		case Desc: return role.name();
		case Rendered: return rendered;
		case Foreground: return foreground;
		case Checked: return axBoolean(AX.kAXValueAttribute);
		case Title: return axString(AX.kAXTitleAttribute);
		case ThumbShape: 
			if(thumb != null)
				return thumb.axRect();
			return null;
		case TrackShape: 
			if(thumb != null)
				return axRect();
			return null;
		}
		return null;
	}

	public Object fetchAX(AXProps p){
		switch(p){
		case Description: return axString(AX.kAXDescriptionAttribute);
		case Help: return axString(AX.kAXHelpAttribute);
		case RoleDescription: return axString(AX.kAXRoleDescriptionAttribute);
		case Title: return axString(AX.kAXTitleAttribute);
		case Role: return role.name();
		case SubRole: return axString(AX.kAXSubroleAttribute);
		case URL: return axString(AX.kAXURLAttribute);
		case FileName: return axString(AX.kAXFilenameAttribute);
		case Rect: return axRect();
		case Selected: return axBoolean(AX.kAXSelectedAttribute);
		case Enabled: return axBoolean(AX.kAXEnabledAttribute);
		case Modal: return axBoolean(AX.kAXModalAttribute);
		case SelectedText: return axBoolean(AX.kAXSelectedTextAttribute);
		case DefaultButton: return defaultBtn;
		case CancelButton: return cancelBtn;
		case CloseButton: return closeBtn;
		case MinimizeButton: return minimizeBtn;
		case ZoomButton: return zoomBtn;
		case GrowArea: return growArea;
		case Proxy: return proxy;
		case Identifier: return axString(AX.kAXIdentifierAttribute);
		}
		return null;
	}

}