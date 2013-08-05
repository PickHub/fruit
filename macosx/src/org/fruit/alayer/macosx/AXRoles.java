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
package org.fruit.alayer.macosx;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.fruit.alayer.IRole;
import org.fruit.alayer.Roles;

public enum AXRoles implements IRole {
	
	AXApplication, AXSystemWide, AXWindow,
	AXSheet, AXDrawer, AXGrowArea, AXImage, AXUnknown, AXButton(Roles.Button), AXRadioButton, AXCheckBox,
	AXPopUpButton, AXMenuButton, AXTabGroup, AXTable, AXColumn, AXRow, AXOutline,
	AXBrowser, AXScrollArea, AXScrollBar, AXRadioGroup, AXList, AXGroup, AXValueIndicator,
	AXComboBox, AXSlider, AXIncrementor, AXBusyIndicator, AXProgressIndicator, AXRelevanceIndicator,
	AXToolbar, AXDisclosureTriangle, AXTextField, AXTextArea, AXStaticText, AXMenuBar,
	AXMenuBarItem, AXMenu, AXMenuItem, AXSplitGroup, AXSplitter, AXColorWell, AXTimeField,
	AXDateField, AXHelpTag, AXMatteRole, AXDockItem, AXRuler, AXRulerMarker, AXGrid,
	AXLevelIndicator, AXCell, AXLayoutArea, AXLayoutItem, AXHandle, AXWebArea, AXView,
	AXExpandingGrid, AXLink,
	;
	
	public static AXRoles get(String r){
		try{
			return valueOf(r);
		}catch(Exception e){
			return AXUnknown; 
		}
	}
	
//    // subroles
//    public enum AXSubRole{
//        // standard subroles
//        AXCloseButton, AXMinimizeButton, AXZoomButton, AXToolbarButton, AXSecureTextField,
//        AXTableRow, AXOutlineRow, AXUnknown, 
//        
//        // new subroles
//        AXStandardWindow, AXDialog, AXSystemDialog, AXFloatingWindow, AXSystemFloatingWindow,
//        AXIncrementArrow, AXDecrementArrow, AXIncrementPage, AXDecrementPage, AXSortButton,
//        AXSearchField, AXTimeline, AXRatingIndicator, AXContentList, AXDefinitionList,
//
//        // dock subroles
//        AXSRApplicationDockItem, AXSRDocumentDockItem, AXSRFolderDockItem, AXSRMinimizedWindowDockItem,
//        AXSRURLDockItem, AXSRDockExtraDockItem, AXSRTrashDockItem, AXSRProcessSwitcherList,
//
//        AXOther
//    } 


	final Set<IRole> ancestors;
	final Set<IRole> parents;
	
	AXRoles(IRole... parents){
		this.parents = new HashSet<IRole>();
		this.ancestors = new HashSet<IRole>();
		for(IRole r : parents){
			this.parents.add(r);
			this.ancestors.add(r);
			this.ancestors.addAll(r.getAncestors());
		}
	}
	public Set<IRole> getDirectAncestors() { return Collections.unmodifiableSet(parents); }
	public Set<IRole> getAncestors() { return Collections.unmodifiableSet(ancestors); }
	public boolean isA(IRole other) { return this == other || ancestors.contains(other); }
	public String getName() { return this.name(); }
}