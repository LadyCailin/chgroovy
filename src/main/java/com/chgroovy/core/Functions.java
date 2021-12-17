package com.chgroovy.core;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.ArgumentValidation;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREPluginInternalException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * Contains the one function, groovy()
 */
public class Functions {

	@api
	public static class groovy extends AbstractFunction {

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{
				CREPluginInternalException.class
			};
		}

		@Override
		public boolean isRestricted() {
			return true;
		}

		@Override
		public Boolean runAsync() {
			return null;
		}

		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			String script = args[0].val();
			CArray env = new CArray(t);
			CArray toReturn = new CArray(t);
			if(args.length > 1){
				env = ArgumentValidation.getArray(args[1], t);
			}
			if(args.length > 2){
				toReturn = ArgumentValidation.getArray(args[2], t);
			}
			Binding binding = new Binding();
			try{
				for(String key : env.stringKeySet()){
					binding.setVariable(key, Construct.GetPOJO(env.get(key, t)));
				}
				GroovyShell shell = new GroovyShell(binding);
				shell.evaluate(script);
			} catch(Throwable ex){
				throw new CREPluginInternalException(ex.getMessage(), t, ex);
			}
			CArray ret = CArray.GetAssociativeArray(t);
			for(String key : toReturn.stringKeySet()){
				Object var = binding.getVariable(toReturn.get(key, t).val());
				ret.set(toReturn.get(key, t).val(), Construct.GetConstruct(var), t);
			}
			return ret;
		}

		@Override
		public String getName() {
			return "groovy";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1, 2, 3};
		}

		@Override
		public String docs() {
			return "array {script, environment, toReturn} Runs a groovy script. The script can set variables beforehand with the environment"
					+ " variable, which should be an associative array mapping variable names to values. Arrays are not directly supported,"
					+ " as everything is simply passed in as a string. Values can be returned from the script, by giving a list of named values"
					+ " to toReturn, which will cause those values to be returned as a part of the associative array returned.";
		}

		@Override
		public Version since() {
			return MSVersion.V3_3_1;
		}
	}
}
