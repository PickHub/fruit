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

public class MacApp implements IMacSystem{

	public enum StreamMode{
		IGNORE, BUFFER, DIRECT;
	}
	
    String executable;
    String[] args;
    long[] pids;
    double startUpTime;
    double shutDownTime;
    Map<IProperty, Object> props;
    StreamMode stdInMode;
    StreamMode stdOutMode;
    StreamMode stdErrMode;
    
    public MacApp(String executable, String[] args, double startUpTime, double shutDownTime, long[] additionalPIDs, StreamMode stdInMode, StreamMode stdOutMode, StreamMode stdErrMode){
    	
    	if(executable == null || args == null || startUpTime < 0 || shutDownTime < 0)
    		throw new IllegalArgumentException("Illegal parameter values for the MacApp constructor");

    	this.stdInMode = stdInMode;
    	this.stdOutMode = stdOutMode;
    	this.stdErrMode = stdErrMode;
    	
        if(additionalPIDs != null){
            pids = new long[additionalPIDs.length + 1];

            for(int i = 1; i < additionalPIDs.length + 1; i++)
               pids[i] = additionalPIDs[i-1]; 
        }else{
            pids = new long[1];
        }

        pids[0] = 0;
        this.executable = executable;
        this.args = args;
        this.startUpTime = startUpTime;
        this.shutDownTime = shutDownTime;
        this.props = new HashMap<IProperty, Object>();

        try{
        	props.put(Tags.StandardMouse, new AWTMouse());
        	props.put(Tags.StandardKeyboard, new AWTKeyboard());
        }catch(Exception e){}
    }

    public String toString(){
        return String.valueOf(executable);
    }

    public long[] getPIDs(){
        return pids;
    }

	public void start() throws SystemStartException{
//		long[] outp = AX.pipe();
//		long[] errp = AX.pipe();
//        long pid = AX.execve(executable, args, new String[]{}, new long[]{0, 0, outp[0], outp[1], errp[0], errp[1]});
        long pid = AX.execve(executable, args, new String[]{}, new long[]{0, 0, 1, 1, 2, 2});
//		AX.close(outp[1]);
//		AX.close(errp[1]);

//        props.put(Props.StdOut, new ReadPipe(outp[0]));
//        props.put(Props.StdErr, new ReadPipe(errp[0]));
                
        if(pid < 1)
            throw new RuntimeException("Application '" + executable + "' could not be started!");
        while(AX.finishedLaunching(pid) != 1)
            Utils.Sleep(0.010);
        pids[0] = pid;
        Utils.Sleep(startUpTime);
        	
        //focus
        for(int i = 0; i < 3; i++){
            AX.activate(pid);
            Utils.Sleep(1);
            if(AX.active(pid) == 1)
            	return;
        }

        throw new RuntimeException("Unable to activate application '" + executable + "'!");
    }

	public void stop() throws SystemStopException{
		if(!isRunning())
			return;
		
        long pid = pids[0];

        AX.terminate(pid);
        
        double start = Utils.GetTime();

        while(isRunning() && (Utils.GetTime() - start < shutDownTime));

        if(!isRunning()){
        	pids[0] = 0;
            return;
        }

        AX.forceTerminate(pid);
        Utils.Sleep(1);

        if(!isRunning()){
        	pids[0] = 0;
        	return;
        }

        AX.kill(pid, AX.SIGKILL);   // or SIGTERM first??
        Utils.Sleep(1);

        if(!isRunning()){
        	pids[0] = 0;
        	return;
		}

        throw new RuntimeException("Unable to stop application!");
    }

    public boolean isRunning(){
    	
    	if(pids[0] == 0)
    		return false;
    	
    	long[] status = AX.waitpid(pids[0], AX.WNOHANG);
    	
    	if(status[0] == 0)
    		return true;
    	
    	if(status[0] < 0)
    		return false;
    	
    	return false;
        //return (pids[0] == 0) ? false : AX.kill(pids[0], 0) == 0;
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

	public void ensureValidity() throws InvalidSystemStateException {
		if(!isRunning())
			throw new InvalidSystemStateException("System is not running!");
	}
}
