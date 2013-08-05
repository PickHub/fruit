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

import org.fruit.alayer.ALayerContext;
import org.fruit.alayer.IState;
import org.fruit.alayer.IWidget;
import com.googlecode.prolog_cafe.lang.JavaObjectTerm;
import com.googlecode.prolog_cafe.lang.Operation;
import com.googlecode.prolog_cafe.lang.Predicate;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.Term;
import com.googlecode.prolog_cafe.lang.VariableTerm;

public final class PRED_parent_2 extends Predicate.P2 {
	static final Operation fail_0 = com.googlecode.prolog_cafe.lang.Failure.FAIL_0;
	final Term arg1, arg2;
	final Operation cont;
	Iterator iter = new Iterator();

	public PRED_parent_2(Term a1, Term a2, Operation cont) {
		this.arg1 = a1;
		this.arg2 = a2;
		this.cont = cont;
	}

	public Operation exec(Prolog engine) {		
		engine.areg1 = arg1;
		engine.areg2 = arg2;
		engine.cont = cont;
		engine.setB0();

		Object oc = engine.getDynamicDataBase();
		if(!(oc instanceof ALayerContext))
			return fail_0;
		IState state = ((ALayerContext) oc).state();
		
		Term targ1 = engine.areg1.dereference();
		Term targ2 = engine.areg2.dereference();
		
		if(!targ1.isJavaObject() && !targ2.isJavaObject())
			return fail_0;
		

		if(targ2.isVariable()){
			IWidget parent = (IWidget) targ1.toJava();
			int childCount = parent.childCount();
			
			if(childCount == 0)
				return engine.fail();
			
			if(childCount == 1){
				((VariableTerm) targ2).bind(new JavaObjectTerm(parent.getChild(0)), engine.trail);
				return this.cont;
			}
						
			iter.i = 0;
			iter.max = childCount;
			iter.s = state;
			iter.parent = parent;
			return iter;
		}else if(targ1.isVariable()){
			
			IWidget child = (IWidget) targ2.toJava();
			IWidget parent = child.getParent();
			
			if(parent == null)
				return engine.fail();
			
			((VariableTerm) targ1).bind(new JavaObjectTerm(parent), engine.trail);
			return this.cont;
		}
		return fail_0;
	}	
		
	static final class Iterator extends Predicate.P2{
		final Binder binder = new Binder();
		IState s;
		int i = 0, max = 0;
		IWidget parent;

		public Operation exec(Prolog engine){
			binder.w = parent.getChild(i);
			if(i == 0){
				i++;
				return engine.jtry2(binder, this);
			}else if(i < max - 1){
				i++;
				return engine.retry(binder, this);
			}else if(i == max - 1){
				return engine.trust(binder);
			}
			return null;
		}
	}

	static final class Binder extends Predicate.P2{
		IWidget w;
		public Operation exec(Prolog engine) {
			Operation cont = engine.cont;
			Term arg = engine.areg2.dereference();
			((VariableTerm) arg).bind(new JavaObjectTerm(w), engine.trail);
			return cont;
		}
	}

}