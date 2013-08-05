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
package com.googlecode.prolog_cafe.builtin;

import com.googlecode.prolog_cafe.lang.Operation;
import com.googlecode.prolog_cafe.lang.Predicate;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.Term;

public final class PRED_starts_with_2 extends Predicate.P2 {
	static final Operation fail_0 = com.googlecode.prolog_cafe.lang.Failure.FAIL_0;
	final Term arg1, arg2;
	final Operation cont;

	public PRED_starts_with_2(Term a1, Term a2, Operation cont) {
		this.arg1 = a1;
		this.arg2 = a2;
		this.cont = cont;
	}

	public Operation exec(Prolog engine) {		
		engine.areg1 = arg1;
		engine.areg2 = arg2;
		engine.cont = cont;
		engine.setB0();
		
		Term targ1 = engine.areg1.dereference();
		Term targ2 = engine.areg2.dereference();
		
		if(!targ1.isSymbol() || !targ2.isSymbol())
			return fail_0;
		
		String sName1 = targ1.name();
		String sName2 = targ2.name();
		
		if(sName1 == null || sName2 == null){
			if(sName1 == sName2)
				return cont;
		}else{
			if(sName1.startsWith(sName2))
				return cont;
		}
		
		return engine.fail();
	}	
}