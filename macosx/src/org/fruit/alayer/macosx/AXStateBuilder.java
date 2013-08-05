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

import java.util.HashMap;
import java.util.Map;

import org.fruit.alayer.IState;
import org.fruit.alayer.IStateBuilder;
import org.fruit.alayer.ISystem;
import org.fruit.alayer.macosx.roles.AXState;

public final class AXStateBuilder implements IStateBuilder {

	long sysRef;
	Map<Long, Long> appRefs = new HashMap<Long, Long>();   // maps PIDs to application references
	
	public AXStateBuilder(){
        if(!AX.AXAPIEnabled())
            throw new RuntimeException("MacOSX Accessibility API not enabled! Please activate it in your system's settings panel!");
	}
	
	public IState build(ISystem system) throws Exception {
		
		if(sysRef == 0)
			sysRef = AX.AXUIElementCreateSystemWide();
		
		IMacSystem macSystem = (IMacSystem) system; 
		
		long[] pids = macSystem.getPIDs();
		long[] apps = new long[pids.length];
		for(int i = 0; i < pids.length; i++){
			if(!appRefs.containsKey(pids[i]))
				appRefs.put(pids[i], AX.AXUIElementCreateApplication(pids[i]));
			apps[i] = appRefs.get(pids[i]);
		}
		
		AXState state = new AXState(sysRef, apps);
		//state.build();
		
		return state;
	}
	
	private void release(){
		if(sysRef != 0){
			AX.CFRelease(sysRef);
			sysRef = 0;
		}
		for(Long ref : appRefs.values())
			AX.CFRelease(ref);
		appRefs.clear();
	}
	
	public void finalize(){
		release();
	}
}
