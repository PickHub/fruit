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

import java.util.List;
import java.util.Map;
import org.fruit.alayer.Utils;
import com.googlecode.prolog_cafe.lang.Term;

public final class QueryResult implements IQueryResult {
	private final List<Term[]> terms;
	private final Map<String, Integer> varIdxs;
	private final List<String> varNames;
	
	public QueryResult(List<Term[]> terms, Map<String, Integer> varIdxs, List<String> varNames){
		Utils.CheckNull(terms);
		this.terms = terms;
		this.varIdxs = varIdxs;
		this.varNames = varNames;
	}
	
	public boolean isTrue(){ return !terms.isEmpty(); }
	public int size() {	return terms.size(); }
	public ITerm[] getTuple(int idx) { return terms.get(idx); }
	public String varName(int idx) { return varNames.get(idx); }
	public int varIdx(String varName){
		Integer ret = varIdxs.get(varName);
		return ret == null ? -1 : ret;
	}
}
