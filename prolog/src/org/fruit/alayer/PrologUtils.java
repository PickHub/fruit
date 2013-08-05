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
package org.fruit.alayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import com.googlecode.prolog_cafe.compiler.CompileException;
import org.fruit.alayer.Utils;

public final class PrologUtils {

	private PrologUtils(){}


	public static void CompileJava(File dir, String classPath) throws IOException, CompileException {		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new CompileException("JDK required (running inside of JRE)");

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		try {
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Utils.GetAllFiles(dir, ".java"));
			ArrayList<String> options = new ArrayList<String>();

			if(classPath != null)
				options.addAll(Arrays.asList("-classpath", classPath));

			JavaCompiler.CompilationTask task = compiler.getTask(
					null,
					fileManager,
					diagnostics,
					options,
					null,
					compilationUnits);
			if (!task.call()) {
				throw new CompileException("compile errors");
			}
		} finally {
			fileManager.close();
		}
	}

}
