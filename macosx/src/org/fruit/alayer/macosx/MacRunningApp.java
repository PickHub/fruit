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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.fruit.alayer.IProperty;
import org.fruit.alayer.InvalidSystemStateException;
import org.fruit.alayer.Tags;
import org.fruit.alayer.SystemStartException;
import org.fruit.alayer.SystemStopException;
import org.fruit.alayer.Utils;
import org.fruit.alayer.macosx.AX;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.AWTMouse;

public final class MacRunningApp implements IMacSystem{
	
	public static class ActivateException extends Exception {
		private static final long serialVersionUID = -7797424127481093948L;
		public ActivateException(String message){ this(message, null); }
		public ActivateException(String message, Throwable cause){ super(message, cause); }
		public ActivateException(Throwable cause){ super(cause); }
	}

    private long[] pids;
    private final Map<IProperty, Object> props;

    public MacRunningApp(long pid, long[] additionalPIDs){
        if(additionalPIDs != null){
            pids = new long[additionalPIDs.length + 1];

            for(int i = 1; i < additionalPIDs.length + 1; i++)
               pids[i] = additionalPIDs[i-1]; 
        }else{
            pids = new long[1];
        }

        pids[0] = pid;
        this.props = new HashMap<IProperty, Object>();

        try{
        	props.put(Tags.StandardMouse, new AWTMouse());
        	props.put(Tags.StandardKeyboard, new AWTKeyboard());
        }catch(Exception e){
        	throw new RuntimeException(e);
        }
    }

    public String toString(){
    	return AX.getAppName(pids[0]);
    }

    public long[] getPIDs(){
        return pids;
    }

	public void start() throws SystemStartException{
    }

	public void stop() throws SystemStopException{
    }

    public boolean isRunning(){
        return AX.kill(pids[0], 0) == 0;
    } 

    public String getProcessName(long pid){
        return null;
    }

	@SuppressWarnings("unchecked")
	public <T> T get(IProperty p) {
		return (T) props.get(p);
	}

	public Set<IProperty> propertySet() {
		return Collections.unmodifiableSet(props.keySet());
	}
	
	public static long[] GetRunningProcesses(){
		return AX.runningApplications();
	}
	
	public static String GetProcessName(long pid){
		return AX.getAppName(pid);
	}
	
	public static void Kill(long pid){
		AX.kill(pid, AX.SIGKILL);
	}
	
	public static void MakeForeground(long pid) throws ActivateException{

		if(AX.active(pid) > 0)
			return;

		final int TRIES = 3;
		final double RETRY_TIME = .5;

		int i = 0;
		while(i < TRIES){
			AX.activate(pid);
			double start = Utils.GetTime();
			i++;
			
			while((AX.active(pid) <= 0) && (Utils.GetTime() - start < RETRY_TIME))
				Utils.Sleep(0.1);
		}
		
		if(AX.active(pid) <= 0)
			throw new ActivateException("Cannot activate process " + pid + ".");
	}

	public void ensureValidity() throws InvalidSystemStateException {
		if(!isRunning())
			throw new InvalidSystemStateException("System is not running!");
	}
}
