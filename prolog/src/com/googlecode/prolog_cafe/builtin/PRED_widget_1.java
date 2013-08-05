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
import org.fruit.alayer.Utils;

import com.googlecode.prolog_cafe.lang.JavaObjectTerm;
import com.googlecode.prolog_cafe.lang.Operation;
import com.googlecode.prolog_cafe.lang.Predicate;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.PrologException;
import com.googlecode.prolog_cafe.lang.Term;
import com.googlecode.prolog_cafe.lang.VariableTerm;

public final class PRED_widget_1 extends Predicate.P1 {
	static final Operation fail_0 = com.googlecode.prolog_cafe.lang.Failure.FAIL_0;
	final Term arg1;
	final Operation cont;
	final Iterator iter = new Iterator();
	final Comparator comp = new Comparator();
	
	public PRED_widget_1(Term a1, Operation cont) {
		this.arg1 = a1;
		this.cont = cont;
	}

	public Operation exec(Prolog engine) {
		engine.areg1 = arg1;
		engine.cont = cont;
		engine.setB0();

		Object oc = engine.getDynamicDataBase();
		if(!(oc instanceof ALayerContext))
			return fail_0;
		ALayerContext cont = (ALayerContext)oc;

		if(cont.state().size() == 0)
			return fail_0;
		
		IState state = cont.state();

		iter.it = state.iterator();
		iter.i = 0;
		iter.max = state.size();
		comp.s = state;
		
		Term targ = engine.areg1.dereference();
		if(targ.isVariable()){
			return iter;
		}else if(targ.isJavaObject()){
			return comp;
		}
		return fail_0;
	}
	
	static final class Comparator extends Operation{
		IState s;
		public Operation exec(Prolog engine) throws PrologException {
			Term arg = engine.areg1.dereference();
			Operation cont = engine.cont;
			try{
				boolean b = Utils.IsMember(s, (IWidget)arg.toJava());
				return b ? cont : engine.fail();
			}catch(Exception e){
				return com.googlecode.prolog_cafe.lang.Failure.FAIL_0;
			}
		}		
	}
	
	static final class Iterator extends Operation{
		java.util.Iterator<IWidget> it;
		int i = 0;
		int max = 0;
		final Binder binder = new Binder();

		public Operation exec(Prolog engine){
			binder.w = it.next();
			if(i == 0){
				i++;
				return engine.jtry1(binder, this);
			}else if(i < max - 1){
				i++;
				return engine.retry(binder, this);
			}else if(i == max - 1){
				return engine.trust(binder);
			}
			return null;
		}
	}

	static final class Binder extends Operation{
		IWidget w;		
		public Operation exec(Prolog engine) {
			Operation cont = engine.cont;
			Term arg = engine.areg1.dereference();
			((VariableTerm) arg).bind(new JavaObjectTerm(w), engine.trail);
			return cont;
		}
	}
}