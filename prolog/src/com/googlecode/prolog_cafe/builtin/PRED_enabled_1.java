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

public final class PRED_enabled_1 extends Predicate.P1 {
	static final Operation fail_0 = com.googlecode.prolog_cafe.lang.Failure.FAIL_0;
	final Iterator iter = new Iterator();
	Comparator comp = new Comparator();
	final Term arg1;
	final Operation cont;

	public PRED_enabled_1(Term a1, Operation cont) {
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
		ALayerContext cont = (ALayerContext) oc;
		IState state = cont.state();

		if(state.size() == 0)
			return fail_0;
		
		iter.it = state.iterator();
		iter.i = 0;
		iter.max = state.size();
		iter.s = state;
		comp.s = state;
		
		Term targ = engine.areg1.dereference();
		if(targ.isVariable())
			return iter;
		else if(targ.isJavaObject())
			return comp;
		return fail_0;
	}

	static final class Comparator extends Operation{
		IState s;
		public Operation exec(Prolog engine) throws PrologException {
			Term arg = engine.areg1.dereference();
			Operation cont = engine.cont;
			try{
				boolean b = Utils.Enabled((IWidget)arg.toJava());
				return b ? cont : engine.fail();
			}catch(Exception e){
				return com.googlecode.prolog_cafe.lang.Failure.FAIL_0;
			}
		}		
	}
	
	static final class Iterator extends Operation{
		final Binder binder = new Binder();
		java.util.Iterator<IWidget> it;
		IState s;
		int i = 0, max = 0;
		IWidget w1, w2;

		IWidget getNextEnabled(){
			IWidget ret;
			while(i < max){
				ret = it.next();
				i++;
				if(Utils.Enabled(ret))
					return ret;
			}
			return null;
		}

		public Operation exec(Prolog engine){
			int prevI = i;

			if(i == 0){
				w1 = getNextEnabled();
				w2 = getNextEnabled();
			}else{
				w1 = w2;
				w2 = getNextEnabled();
			}

			binder.w = w1;
			if(w1 == null){
				return engine.fail();
			}else{
				if(w2 == null)
					return engine.trust(binder);
				if(prevI == 0)
					return engine.jtry1(binder, this);
				return engine.retry(binder, this);
			}		
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