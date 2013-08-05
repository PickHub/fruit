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
package org.fruit.prolog;

import java.io.File;

public class Main {
	public static void main(String[] args){

		IFacts f = new Facts("bla(X):-widget(X).");
		IQuery q = new Query("bla(X).", f);
		
		double sum = 0.0;
		for(int i = 0; i < 1000; i++){
			IQueryResult qr = q.run(null, true);
			sum += qr.size();
		}

		long start = System.currentTimeMillis();
		sum = 0.0;
		for(int i = 0; i < 100; i++){
			IQueryResult qr = q.run(null, true);
			sum += qr.size();
			for(int j = 0; j < qr.size(); j++){
				ITerm[] ts = qr.getTuple(j);
//				for(int k = 0; k < ts.length; k++)
//					System.out.print(qr.varName(k) + ":  " + ts[k].name() + "(" + ts[k].isJavaObject() + ")" + ", ");
//				System.out.println();
			}
		}
		
		long stop = System.currentTimeMillis();
		System.out.println((((double)(stop - start)) / 1000.0) + " seconds   " + sum);
	}

}
