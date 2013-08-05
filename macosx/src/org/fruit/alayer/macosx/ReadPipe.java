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

import java.io.IOException;
import java.io.InputStream;

public final class ReadPipe extends InputStream {

	private final long pipe;
	byte[] buffer;
	
	public ReadPipe(long pipe){
		this.pipe = pipe;
		this.buffer = null;
		long flags = AX.fcntl(pipe, AX.F_GETFL, 0);
		AX.fcntl(pipe, AX.F_SETFL, flags | AX.O_NONBLOCK);		
	}
	
	public int read() throws IOException {
		if(buffer != null){
			int ret = buffer[0] & 0xff;
			buffer = null;
			return ret;
		}
		
		byte[] ret = AX.read(pipe, 1);
		if(ret == null)
			throw new IOException();
		if(ret.length == 0)
			return -1;
		return ret[0] & 0xff;
	}
	
	public int available(){
		if(buffer != null)
			return 1;
		buffer = AX.read(pipe, 1);
		if(buffer == null){
			return 0;
		}

		if(buffer.length == 0){
			buffer = null;
			return 0;
		}
		
		return 1;
	}
	

}
