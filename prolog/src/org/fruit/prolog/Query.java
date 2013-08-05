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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.PrologUtils;
import org.fruit.alayer.Utils;
import com.googlecode.prolog_cafe.compiler.Compiler;
import com.googlecode.prolog_cafe.lang.BufferingPrologControl;
import com.googlecode.prolog_cafe.lang.Term;
import com.googlecode.prolog_cafe.lang.VariableTerm;
import com.igormaznitsa.prologparser.PrologParser;
import com.igormaznitsa.prologparser.terms.AbstractPrologTerm;
import com.igormaznitsa.prologparser.terms.PrologStructure;
import com.igormaznitsa.prologparser.terms.PrologVariable;

public final class Query implements IQuery {
	static final String userPackageName = "org.fruit.prolog.user";
	static final long serialVersionUID = 8046977434322767968L;
	transient String head, func;
	transient ArrayList<String> vars = new ArrayList<String>();
	transient File objFilesDir;
	transient PredLoader predLoader;
	transient BufferingPrologControl pl;
	transient HashMap<String, Integer> varIdxs;
	String body;
	IFacts facts;
	
	public Query(String queryString){
		this(queryString, null);
	}
	
	public Query(String queryString, IFacts facts){
		Utils.CheckNull(queryString);
		this.facts = facts;
		initialize(queryString);
	}
	
	void initialize(String queryString){
		List<String> tokens = createQueryHead(queryString, vars);
		func = tokens.get(0);
		head = tokens.get(1);
		body = queryString;
	}
	
	List<String> createQueryHead(String string, List<String> vars) {
		try {			
			PrologParser parser = new PrologParser(null);
			LinkedHashSet<String> varSet = new LinkedHashSet<String>();
			getVars(parser.nextSentence(string), varSet);
			for(String s : varSet)
				vars.add(s);
			
			String f = "query" + Math.abs(string.hashCode());

			StringBuilder h = new StringBuilder();
			h.append(f);
			if(!varSet.isEmpty()){
				h.append('(');
				for(String s : varSet){
					h.append(s).append(',');
				}
		    	h.deleteCharAt(h.length() - 1);
	
				h.append(')');
			}
			ArrayList<String> ret = new ArrayList<String>();
			ret.add(f); ret.add(h.toString());
			return ret;
		}catch(Exception unexpected){
			throw new RuntimeException(unexpected);
		}
	}
	
	void getVars(AbstractPrologTerm apt, Set<String> vars){
		switch(apt.getType()){
		case STRUCT:
			PrologStructure pls = (PrologStructure) apt;
			apt.getType();
			for(int i = 0; i < pls.getArity(); i++)
				getVars(pls.getElement(i), vars);
			break;
		case VAR:
			if(!"_".equals(apt.getText()))
				vars.add(((PrologVariable)apt).getText());
			break;
		}
	}
	
	public int arity(){ return vars.size(); }
	public String head(){ return head; }
	public String functor(){ return func; }
	public String getBody(){ return body; }
	public String getText() { return head + ":-" + body; }
	public String toString(){ return getText(); }
	public IFacts getFacts() { return facts; }
	public int hashCode(){ return head.hashCode() + body.hashCode(); }
	public List<String> vars(){ return vars; }
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		
		if(o instanceof Query){
			Query qo = (Query) o;
			return head.equals(qo.head) && body.equals(qo.body);
		}
		return false;
	}

	public void compile() {
		try{
			if(predLoader == null){
				File plFile = Utils.CreateTempFile(getText() + "\n");
				plFile.deleteOnExit();
				File objFilesDir = Utils.CreateTempDir();
				objFilesDir.deleteOnExit();
				new Compiler().prologToJavaSource(plFile.getAbsolutePath(), objFilesDir.getAbsolutePath());
				
				if(facts == null){
					PrologUtils.CompileJava(objFilesDir, System.getProperty("java.class.path"));
					predLoader = new PredLoader(objFilesDir);					
				}else{
					PrologUtils.CompileJava(objFilesDir, facts.objFilesDir().getAbsolutePath() + ":" + System.getProperty("java.class.path"));
					predLoader = new PredLoader(objFilesDir, facts.getClassLoader());
				}
			}
		}catch(Exception e){
			throw new RuntimeException("Unable to compile Prolog query!", e);
		}
		
	}
	
	public QueryResult run(IContext context, boolean all) {
		compile();
		Term[] args = new VariableTerm[arity()];
		for(int i = 0; i < args.length; i++)
			args[i] = new VariableTerm();
		
		if(pl == null){
			pl = new BufferingPrologControl();
			pl.setPrologClassLoader(predLoader);
		}
		
		if(varIdxs == null){
			varIdxs = new HashMap<String, Integer>();
			List<String> vars = vars();
			for(int i = 0; i < vars.size(); i++)
				varIdxs.put(vars.get(i), i);
		}
		
		pl.getEngine().setDynamicDataBase(context);
		
		if(all){
			return new QueryResult(pl.all(userPackageName, functor(), args), varIdxs, vars);
		}else{
			Term t[] = pl.once(userPackageName, functor(), args);
			ArrayList<Term[]> results = new ArrayList<Term[]>();
			results.add(t);
			return new QueryResult(results, varIdxs, vars);
		}
	}	
}
