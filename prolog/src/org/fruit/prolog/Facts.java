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

import org.fruit.alayer.PrologUtils;
import org.fruit.alayer.Utils;
import com.googlecode.prolog_cafe.compiler.Compiler;

public final class Facts implements IFacts {
	private static final long serialVersionUID = 38257494988567520L;
	transient PredLoader predLoader;
	final String facts;
	
	public Facts(String pl){
		Utils.CheckNull(pl);
		facts = pl;
	}

	public Facts(File pl){
		Utils.CheckNull(pl);
		if(!pl.exists())
			throw new IllegalArgumentException("File does not exist!");
		facts = Utils.ReadFile(pl) + "\n";
	}

	public String getText() { return facts; }

	public void compile() {
		try{
			if(predLoader == null){
				File plFile = Utils.CreateTempFile(facts);
				plFile.deleteOnExit();
				File objFileDir = Utils.CreateTempDir();
				objFileDir.deleteOnExit();
				new Compiler().prologToJavaSource(plFile.getAbsolutePath(), objFileDir.getAbsolutePath());
				PrologUtils.CompileJava(objFileDir, System.getProperty("java.class.path"));
				predLoader = new PredLoader(objFileDir);
			}
		}catch(Exception e){
			throw new RuntimeException("Unable to compile Prolog fact database!", e);
		}
	}

	public PredLoader getClassLoader() {
		compile();
		return predLoader;
	}
	
	public String toString(){ return getText(); }

	public File objFilesDir() {
		return getClassLoader().objFilesDir();
	}
}
