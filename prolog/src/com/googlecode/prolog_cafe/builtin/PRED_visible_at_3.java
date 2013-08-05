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
import com.googlecode.prolog_cafe.lang.Term;
import com.googlecode.prolog_cafe.lang.VariableTerm;
import com.googlecode.prolog_cafe.lang.DoubleTerm;

public final class PRED_visible_at_3 extends Predicate.P3 {
	static final Operation fail_0 = com.googlecode.prolog_cafe.lang.Failure.FAIL_0;
	final Iterator iter = new Iterator();
	final Term arg1, arg2, arg3;
	final Operation cont;
	
	public PRED_visible_at_3(Term a1, Term a2, Term a3, Operation cont) {
		this.arg1 = a1;
		this.arg2 = a2;
		this.arg3 = a3;
		this.cont = cont;
	}

	public Operation exec(Prolog engine) {		
		engine.areg1 = arg1;
		engine.areg2 = arg2;
		engine.areg3 = arg3;
		engine.cont = cont;
		engine.setB0();

		Object oc = engine.getDynamicDataBase();
		if(!(oc instanceof ALayerContext))
			return fail_0;
		ALayerContext c = (ALayerContext)oc;

		IState state = c.state();
		
		if(state.size() == 0)
			return fail_0;

		Term targ1 = engine.areg1.dereference();
		Term targ2 = engine.areg2.dereference();
		Term targ3 = engine.areg3.dereference();

		if(targ2.isDouble() && targ3.isDouble()){
			
			double x = ((DoubleTerm) targ2).value();
			double y = ((DoubleTerm) targ3).value();
			
			if(targ1.isVariable()){
				iter.it = state.iterator();
				iter.i = 0;
				iter.max = state.size();
				iter.s = state;
				iter.x = x;
				iter.y = y;
				iter.first = true;
				iter.w1 = iter.getNextVisibleAt();
				if(iter.w1 == null){
					return engine.fail();
				}
				iter.w2 = iter.getNextVisibleAt();
				return iter;
			}else if(targ1.isJavaObject()){
				Object jo = targ1.toJava();

				if(jo instanceof IWidget){
					try{
						boolean b = Utils.VisibleAt((IWidget)jo, x, y);
						return b ? cont : engine.fail();
					}catch(Exception e){
						return fail_0;
					}
				}
			}
		}
		return fail_0;
	}

	static final class Iterator extends Operation{
		final Binder binder = new Binder();
		java.util.Iterator<IWidget> it;
		IState s;
		int i = 0, max = 0;
		IWidget w1, w2;
		double x, y;
		boolean first;

		IWidget getNextVisibleAt(){
			IWidget ret;
			while(i < max){
				ret = it.next();
				i++;
				if(Utils.VisibleAt(ret, x, y))
					return ret;
			}
			return null;
		}

		public Operation exec(Prolog engine){
			
			if(!first){
				w1 = w2;
				w2 = getNextVisibleAt();
			}

			binder.w = w1;

			if(w1 == null){
				return engine.fail();
			}else{
				if(w2 == null){
					if(first)
						return binder;
					else
						return engine.trust(binder);
				}
				if(first){
					first = false;
					return engine.jtry1(binder, this);
				}
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