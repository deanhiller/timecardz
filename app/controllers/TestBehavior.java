package controllers;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import play.Logger;
import play.classloading.enhancers.Enhancer;
import play.classloading.enhancers.Enhancer.ApplicationClassesClasspath;
import bytecodeparser.analysis.stack.StackAnalyzer;
import bytecodeparser.analysis.stack.StackAnalyzer.Frames;
import bytecodeparser.utils.Utils;

public class TestBehavior {

	/**
	 * @param args
	 * @throws BadBytecode 
	 * @throws NotFoundException 
	 * @throws CannotCompileException 
	 */
	public static void main(String[] args) throws BadBytecode, NotFoundException, CannotCompileException {
		ClassPool pool = ClassPool.getDefault();
		CtClass ctClass = pool.get("controllers.Register");
		
		for(CtBehavior behavior : ctClass.getDeclaredMethods()) {
            if(behavior.isEmpty() || behavior.getMethodInfo().getCodeAttribute() == null || Utils.getLocalVariableAttribute(behavior) == null) {
                CtField signature = CtField.make("public static String[] $" + behavior.getName() + "0 = new String[0];", ctClass);
                ctClass.addField(signature);
                continue;
            }

            StackAnalyzer parser = new StackAnalyzer(behavior);

            // first, compute hash for parameter names
            CtClass[] signatureTypes = behavior.getParameterTypes();
            int memberShift = Modifier.isStatic(behavior.getModifiers()) ? 0 : 1;

            if(signatureTypes.length > parser.context.localVariables.size() - memberShift) {
                Logger.debug("ignoring method: %s %s (local vars numbers differs : %s != %s)", Modifier.toString(behavior.getModifiers()), behavior.getLongName(), signatureTypes.length, parser.context.localVariables.size() - memberShift);
                continue;
            }

            StringBuffer signatureNames;
            if(signatureTypes.length == 0)
                signatureNames = new StringBuffer("new String[0];");
            else {
                signatureNames = new StringBuffer("new String[] {");

                for(int i = memberShift; i < signatureTypes.length + memberShift; i++) {
                    if(i > memberShift)
                        signatureNames.append(",");

                    signatureNames.append("\"").append(parser.context.localVariables.get(i).name).append("\"");
                }
                signatureNames.append("};");
            }

            CtField signature = CtField.make("public static String[] $" + behavior.getName() + computeMethodHash(signatureTypes) + " = " + signatureNames.toString(), ctClass);
            ctClass.addField(signature);
            
			Frames frames = parser.analyze();
		}
	}

    private static Integer computeMethodHash(CtClass[] parameters) {
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            names[i] = parameters[i].getName();
        }
        return computeMethodHash(names);
    }

    public static Integer computeMethodHash(Class<?>[] parameters) {
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> param = parameters[i];
            names[i] = "";
            if (param.isArray()) {
                int level = 1;
                param = param.getComponentType();
                // Array of array
                while (param.isArray()) {
                    level++;
                    param = param.getComponentType();
                }
                names[i] = param.getName();
                for (int j = 0; j < level; j++) {
                    names[i] += "[]";
                }
            } else {
                names[i] = param.getName();
            }
        }
        return computeMethodHash(names);
    }

    public static Integer computeMethodHash(String[] parameters) {
        StringBuffer buffer = new StringBuffer();
        for (String param : parameters) {
            buffer.append(param);
        }
        Integer hash = buffer.toString().hashCode();
        if (hash < 0) {
            return -hash;
        }
        return hash;
    }
}
