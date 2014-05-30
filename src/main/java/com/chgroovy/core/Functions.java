package com.chgroovy.core;

import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * Contains the one function, groovy()
 */
public class Functions {

    @api
    public static class groovy extends AbstractFunction{

        public ExceptionType[] thrown() {
            return new ExceptionType[]{

            };
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return null;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String script = args[0].val();
            CArray env = new CArray(t);
            CArray toReturn = new CArray(t);
            if(args.length > 1){
                env = Static.getArray(args[1], t);
            }
            if(args.length > 2){
                toReturn = Static.getArray(args[2], t);
            }
            Binding binding = new Binding();
            try{
                for(String key : env.stringKeySet()){
                    binding.setVariable(key, Construct.GetPOJO(env.get(key, t)));
                }
                GroovyShell shell = new GroovyShell(binding);
                shell.evaluate(script);
            } catch(Exception ex){
                throw new ConfigRuntimeException(ex.getMessage(), ExceptionType.PluginInternalException, t);
            }
            CArray ret = CArray.GetAssociativeArray(t);
            for(String key : toReturn.stringKeySet()){
                Object var = binding.getVariable(toReturn.get(key, t).val());
                ret.set(toReturn.get(key, t).val(), Construct.GetConstruct(var), t);
            }
            return ret;
        }

        public String getName() {
            return "groovy";
        }

        public Integer[] numArgs() {
            return new Integer[]{1, 2, 3};
        }

        public String docs() {
            return "array {script, environment, toReturn} Runs a groovy script. The script can set variables beforehand with the environment"
                    + " variable, which should be an associative array mapping variable names to values. Arrays are not directly supported,"
                    + " as everything is simply passed in as a string. Values can be returned from the script, by giving a list of named values"
                    + " to toReturn, which will cause those values to be returned as a part of the associative array returned.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }

    }
}
