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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.fruit.alayer.Utils;

import com.googlecode.prolog_cafe.lang.PrologClassLoader;

public final class PredLoader extends PrologClassLoader  {
	private final File javaObjDir;
	private final Map<String, Class> cache = new HashMap<String, Class>();

	public PredLoader(File javaObjDir){
		Utils.CheckNull(javaObjDir);
		if(!javaObjDir.exists())
			throw new IllegalArgumentException("File does not exist!");
		this.javaObjDir = javaObjDir;
	}
	
	public PredLoader(File javaObjDir, ClassLoader parent){
		super(parent);
		Utils.CheckNull(javaObjDir);
		if(!javaObjDir.exists())
			throw new IllegalArgumentException("File does not exist!");
		this.javaObjDir = javaObjDir;
	}

	public File objFilesDir(){ return javaObjDir; }

	public Class<?> loadClass(String name) throws ClassNotFoundException {    	    	
		if (name.startsWith("org.fruit.prolog.user")){
			Class<?> ret = cache.get(name);
			if(ret != null)
				return ret;
			
			ret = getClass(name);
			if(ret != null){
				cache.put(name, ret);
				return ret;
			}else if(this.getParent() instanceof PredLoader){
				return this.getParent().loadClass(name);
			}
		}
		return super.loadClass(name);
	}

	private byte[] loadClassData(File name) throws IOException {
		InputStream stream = new BufferedInputStream(new FileInputStream(name));
		int size = stream.available();
		byte buff[] = new byte[size];
		DataInputStream in = new DataInputStream(stream);
		in.readFully(buff);
		in.close();
		return buff;
	}

	private Class<?> getClass(String name) throws ClassNotFoundException {
		File file = new File(javaObjDir + File.separator + name.replace('.', File.separatorChar) + ".class");
		if(!file.exists())
			return null;
		byte[] b = null;
		try {
			b = loadClassData(file);
			Class<?> c = defineClass(name, b, 0, b.length);
			resolveClass(c);
			return c;
		} catch (IOException e) {
			return null;
		}
	}
}