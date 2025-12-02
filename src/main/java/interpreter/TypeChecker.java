package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Type checker for 417 lang
 * 
 * Parses JSON from the parser and computes
 * types for expressions
 * 
 * @author Warren Long (wclong4)
 */
public class TypeChecker {

    /**
     * Parses JSON as a map
     * @param json json to parse
     * @return a mapified version of the json string
     * @throws Exception
     */
    public static Map<String, Object> parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // get a map which is similar to JSON
        Map<String, Object> map = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        return map;
    }

    /**
     * Compute the type of an expression under the initial environment.
     * Returns either a base Type or an ArrowType.
     */
    public static Object typeOf(Map<String, Object> exp) {
        return typeOf(exp, initialEnv());
    }

    /**
     * Sets up the initial environment
     * @return a list of maps to string->object
     */
    private static List<Map<String, Object>> initialEnv() {
        Map<String, Object> env0 = new HashMap<>();
        // integers bound in initial environment
        env0.put("i", Type.INT);
        env0.put("v", Type.INT);
        env0.put("x", Type.INT);
        env0.put("l", Type.INT);
        env0.put("c", Type.INT);
        env0.put("d", Type.INT);
        env0.put("m", Type.INT);
        env0.put("author", Type.STR);

        // arithmetic stuff
        env0.put("add", new ArrowType(listOf(Type.INT, Type.INT), Type.INT));
        env0.put("sub", new ArrowType(listOf(Type.INT, Type.INT), Type.INT));
        env0.put("mul", new ArrowType(listOf(Type.INT, Type.INT), Type.INT));
        env0.put("div", new ArrowType(listOf(Type.INT, Type.INT), Type.INT));
        env0.put("expt", new ArrowType(listOf(Type.INT, Type.INT), Type.INT));
        env0.put("%", new ArrowType(listOf(Type.INT, Type.INT), Type.INT));
        env0.put("pow", new ArrowType(listOf(Type.INT, Type.INT), Type.INT));
        env0.put("sqrt", new ArrowType(listOf(Type.INT), Type.INT));

        // comparisons
        env0.put("==", new ArrowType(listOf(Type.INT, Type.INT), Type.BOOL));
        env0.put(">", new ArrowType(listOf(Type.INT, Type.INT), Type.BOOL));
        env0.put("<", new ArrowType(listOf(Type.INT, Type.INT), Type.BOOL));

        // print, accepts any number of args and returns UNIT
        env0.put("print", new ArrowType(null, Type.UNIT, true));
        env0.put("for", new ArrowType(null, Type.UNIT, true));
        env0.put("while", new ArrowType(null, Type.UNIT, true));

        List<Map<String, Object>> env = new ArrayList<>();
        env.add(env0);
        return env;
    }

    /**
     * Looks up a type
     * @param env the env to look in
     * @param name the name of the type
     * @return the Type
     */
    private static Object lookupType(List<Map<String, Object>> env, String name) {
        for (int i = env.size() - 1; i >= 0; i--) {
            Map<String, Object> frame = env.get(i);
            if (frame.containsKey(name)) {
                return frame.get(name);
            }
        }
        throw new InterpretationException("417 error: undefined identifier " + name);
    }

    /**
     * Extends the env
     * @param env the env to extend
     * @param name the name to add
     * @param type the type to add
     */
    private static void extendEnv(List<Map<String, Object>> env, String name, Object type) {
        env.get(env.size() - 1).put(name, type);
    }

    /**
     * Creates a new frame to add to env
     * @param env the env to create a new frame for.
     * @return the new env frame
     */
    private static List<Map<String, Object>> extendEnvNewFrame(List<Map<String, Object>> env) {
        List<Map<String, Object>> newEnv = new ArrayList<>(env);
        newEnv.add(new HashMap<>());
        return newEnv;
    }

    /**
     * Parses the type
     * 
     * It can be a regular type, like the Type enum
     * 
     * Or it can be an arrow type
     * 
     * @param jsonType the object to check the type of
     * @return the type, either a Type or ArrowType
     */
    @SuppressWarnings("unchecked")
    private static Object parseType(Object jsonType) {
        if (jsonType instanceof String) {
            String s = (String) jsonType;
            if (s.equals(":int")) {
                return Type.INT;
            } else if (s.equals(":bool")) {
                return Type.BOOL;
            } else if (s.equals(":str")) {
                return Type.STR;
            } else if (s.equals(":unit")) {
                return Type.UNIT;
            } else  {
                throw new InterpretationException("Invalid base type: " + s);
            }
        } else if (jsonType instanceof List) {
            List<Object> arr = (List<Object>) jsonType;
            if (arr.isEmpty()) {
                throw new InterpretationException("Empty arrow type :(");
            }
            // all but last are parameter types
            List<Object> params = new ArrayList<>();
            for (int i = 0; i < arr.size() - 1; i++) {
                params.add(parseType(arr.get(i)));
            }
            Object ret = parseType(arr.get(arr.size() - 1));
            return new ArrowType(params, ret);
        } else {
            throw new InterpretationException("Invalid type expression: " + String.valueOf(jsonType));
        }
    }

    /**
     * Checks the compatibility of the two types
     * @param t1 the first type to check
     * @param t2 the second one to check
     * @return whether or not they are compatible
     */
    private static boolean compatible(Object t1, Object t2) {
        if (t1 instanceof Type && t2 instanceof Type) {
            return t1 == t2;
        }
        if (t1 instanceof ArrowType && t2 instanceof ArrowType) {
            ArrowType a1 = (ArrowType) t1;
            ArrowType a2 = (ArrowType) t2;

            if (!compatible(a1.getReturnType(), a2.getReturnType())) {
                return false;
            }

            if (a1.isVariadicAny() || a2.isVariadicAny()) {
                return true; // mostly for application
            }
            
            // get our param types
            List<Object> p1 = a1.getParamTypes();
            List<Object> p2 = a2.getParamTypes();

            if (p1 == null || p2 == null)  {
                return p1 == p2; // both null is ok
            }

            if (p1.size() != p2.size()) {
                return false;
            }

            for (int i = 0; i < p1.size(); i++) {
                if (!compatible(p1.get(i), p2.get(i))) return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Gets the type of exp, kinda like the interp function
     * @param exp the expression to check the type of
     * @param env the environment to look in
     * @return the type of exp
     */
    @SuppressWarnings("unchecked")
    private static Object typeOf(Map<String, Object> exp, List<Map<String, Object>> env) {
        if (exp.containsKey("Number")) {
            return Type.INT;
        } 
        if (exp.containsKey("String")) {
            return Type.STR;
        }
        if (exp.containsKey("Boolean")) {
            return Type.BOOL; // parser encodes as true or false
        }

        Object id = exp.get("Identifier");

        if (id instanceof String) {
            return lookupType(env, (String) id);
        }

        Object app = exp.get("Application");
        if (app instanceof Map) {
            Map<String, Object> obj = (Map<String, Object>) app;
            Map<String, Object> rator = (Map<String, Object>) obj.get("Operator");
            List<Object> rands = (List<Object>) obj.get("Operands");
            return typeOfApplication(rator, rands, env);
        }

        if (exp.containsKey("Cond")) {
            return typeOfCond((List<Object>) exp.get("Cond"), env);
        }

        if (exp.containsKey("Block")) {
            return typeOfBlock((List<Object>) exp.get("Block"), env);
        }

        Object letObj = exp.get("Let");
        if (letObj instanceof Map) {
            Map<String, Object> obj = (Map<String, Object>) letObj;
            Map<String, Object> parm = (Map<String, Object>) obj.get("Parameter");
            Map<String, Object> rhs = (Map<String, Object>) obj.get("Expression");
            List<Object> body = (List<Object>) obj.get("Block");
            return typeOfLet(parm, rhs, body, env);
        }

        Object lam = exp.get("Lambda");
        if (lam instanceof Map) {
            Map<String, Object> obj = (Map<String, Object>) lam;
            List<Map<String, Object>> parms = (List<Map<String, Object>>) obj.get("Parameters");
            Object type = obj.get("Type");
            List<Object> body = (List<Object>) obj.get("Block");
            return typeOfFunction(parms, type, body, env);
        }

        Object assign = exp.get("Assignment");
        if (assign instanceof Map) {
            Map<String, Object> obj = (Map<String, Object>) assign;
            String name = (String) obj.get("Identifier");
            Map<String, Object> rhs = (Map<String, Object>) obj.get("Expression");
            return typeOfAssignment(name, rhs, env);
        }

        Object def = exp.get("Def");
        if (def instanceof Map) {
            Map<String, Object> obj = (Map<String, Object>) def;
            Map<String, Object> parm = (Map<String, Object>) obj.get("Parameter");
            Map<String, Object> rhs = (Map<String, Object>) obj.get("Expression");
            return typeOfDefinition(parm, rhs, env);
        }

        throw new InterpretationException("Not a valid expression");
    }

    /**
     * Checks the types of a definition by checking the declared and rhs types
     * @param parm the parameter
     * @param rhs the right hand side
     * @param env the env
     * @return UNIT hopefully
     */
    private static Object typeOfDefinition(Map<String, Object> parm, Map<String, Object> rhs, List<Map<String, Object>> env) {
        String name = (String) parm.get("Name");
        Object declared = parseType(parm.get("Type"));
        Object rhsType = typeOf(rhs, env);
        if (!compatible(declared, rhsType)) {
            throw new InterpretationException("Defined id type does not match rhs value type\n" + "Name: " + name.toString() + 
                "\nRHS: " + rhsType.toString() + 
                "\nDeclared: " + declared.toString());
        }
        // extend current env frame
        extendEnv(env, name, declared);
        return Type.UNIT;
    }

    /**
     * Checks the types of a definition by checking the declared and rhs types
     * @param parm the parameter
     * @param rhs the right hand side
     * @param env the env
     * @return UNIT hopefully
     */
    private static Object typeOfAssignment(String name, Map<String, Object> rhs, List<Map<String, Object>> env) {
        Object idType = lookupType(env, name);
        Object rhsType = typeOf(rhs, env);
        if (!compatible(idType, rhsType)) {
            throw new InterpretationException("Identifier type does not match assigned value type");
        }
        return Type.UNIT;
    }

    /**
     * Checks the types of an application by checking the types
     * of the operator and operands
     * @param rator the operator
     * @param rands the operands
     * @param env the env
     * @return some type or arrowtype
     */
    @SuppressWarnings("unchecked")
    private static Object typeOfApplication(Map<String, Object> rator, List<Object> rands, List<Map<String, Object>> env) {
        Object ratorType = typeOf(rator, env);
        if (!(ratorType instanceof ArrowType)) {
            throw new InterpretationException("Operator does not have arrow type");
        }
        ArrowType at = (ArrowType) ratorType;
        if (at.isVariadicAny()) {
            // ignore operand types, loophole
            return at.getReturnType();
        }
        List<Object> paramTypes = at.getParamTypes();
        if (paramTypes == null) paramTypes = new ArrayList<>();
        if (paramTypes.size() != rands.size()) {
            throw new InterpretationException("Invalid operand type");
        }
        for (int i = 0; i < rands.size(); i++) {
            Object argType = typeOf((Map<String, Object>) rands.get(i), env);
            if (!compatible(argType, paramTypes.get(i))) {
                throw new InterpretationException("Invalid operand type");
            }
        }
        return at.getReturnType();
    }

    /**
     * Gets the type of let
     * @param parm the parameter
     * @param rhs the right hand side
     * @param body the body of the let
     * @param env the environment
     * @return some type or arrowtype
     */
    private static Object typeOfLet(Map<String, Object> parm, Map<String, Object> rhs, List<Object> body,
                                    List<Map<String, Object>> env) {
        Object declared = parseType(parm.get("Type"));
        String name = (String) parm.get("Name");
        Object rhsType = typeOf(rhs, env);
        if (!compatible(declared, rhsType)) {
            throw new InterpretationException("Type of let expression does not match declared type");
        }
        List<Map<String, Object>> localEnv = extendEnvNewFrame(env);
        extendEnv(localEnv, name, declared);
        return typeOfBlock(body, localEnv);
    }

    /**
     * Gets the type of a function
     * @param parms the parameters
     * @param returnTypeAnnot the return type annotation
     * @param body the body of the function
     * @param env the environment
     * @return ArrowType hopefully
     */
    private static Object typeOfFunction(List<Map<String, Object>> parms, Object returnTypeAnnot, List<Object> body,
                                         List<Map<String, Object>> env) {
        List<String> names = new ArrayList<>();
        List<Object> parmTypes = new ArrayList<>();
        for (Map<String, Object> p : parms) {
            names.add((String) p.get("Name"));
            parmTypes.add(parseType(p.get("Type")));
        }
        Object declaredReturn = parseType(returnTypeAnnot);
        List<Map<String, Object>> localEnv = extendEnvNewFrame(env);
        for (int i = 0; i < names.size(); i++) {
            extendEnv(localEnv, names.get(i), parmTypes.get(i));
        }
        Object bodyType = typeOfBlock(body, localEnv);
        if (!compatible(bodyType, declaredReturn)) {
            throw new InterpretationException("Function body type does not match declared type");
        }
        return new ArrowType(parmTypes, declaredReturn);
    }

    /**
     * Gets the type of the conditional
     * @param clauses the clauses of the conditional
     * @param env the environment
     * @return UNIT hopefully
     */
    @SuppressWarnings("unchecked")
    private static Object typeOfCond(List<Object> clauses, List<Map<String, Object>> env) {
        List<Object> testTypes = new ArrayList<>();
        List<Object> conseqTypes = new ArrayList<>();
        for (Object clauseObj : clauses) {
            Map<String, Object> clause = (Map<String, Object>) clauseObj;
            Map<String, Object> test = (Map<String, Object>) clause.get("Test");
            Map<String, Object> cons = (Map<String, Object>) clause.get("Consequent");
            testTypes.add(typeOf(test, env));
            conseqTypes.add(typeOf(cons, env));
        }
        for (Object tt : testTypes) {
            if (!(tt instanceof Type) || tt != Type.BOOL) {
                throw new InterpretationException("Test in conditional not a boolean type");
            }
        }
        if (!conseqTypes.isEmpty()) {
            Object first = conseqTypes.get(0);
            for (int i = 1; i < conseqTypes.size(); i++) {
                if (!compatible(first, conseqTypes.get(i))) {
                    throw new InterpretationException("Consequents in conditional have incompatible types");
                }
            }
            return first;
        }
        return Type.UNIT;
    }

    /**
     * Gets the type of a block
     * @param exps the expressions in the block
     * @param env the environment 
     * @return the last type of the block
     */
    @SuppressWarnings("unchecked")
    private static Object typeOfBlock(List<Object> exps, List<Map<String, Object>> env) {
        // Extend with types of all defs in block for mutual recursion
        List<Map<String, Object>> blockEnv = extendEnvNewFrame(env);
        for (Object expObj : exps) {
            Map<String, Object> exp = (Map<String, Object>) expObj;
            if (exp.containsKey("Def")) {
                Map<String, Object> def = (Map<String, Object>) exp.get("Def");
                Map<String, Object> parm = (Map<String, Object>) def.get("Parameter");
                String name = (String) parm.get("Name");
                Object t = parseType(parm.get("Type"));
                extendEnv(blockEnv, name, t);
            }
        }
        Object lastType = Type.UNIT;
        for (Object expObj : exps) {
            lastType = typeOf((Map<String, Object>) expObj, blockEnv);
        }
        return lastType;
    }

    /**
     * Takes in a bunch of objects and turns them into a list
     * @param elems the elements to take in
     * @return a list of the elements
     */
    private static List<Object> listOf(Object... elems) {
        List<Object> list = new ArrayList<>();
        for (Object e : elems) {
            list.add(e);
        }
        return list;
    }
}

