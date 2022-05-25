package eval;
import lama.Absyn.*;
import lama.PrettyPrinter;

import java.util.HashMap;
import java.util.LinkedList;

public class TypeChecker {
	// Class for checking the type correctness of a program

    private static abstract class TypeCode { 
		// Store type codes
		String tcode;

		public boolean isARR() { 
			return false; 
		}
		public TypeCode typeOfElements() { 
			throw new TypeException(this + " is not an array."); 
		}

		public static class Undefined extends TypeCode {
			// Class for storing integer type code

			public Undefined() {
				// Constructor 
				this.tcode = "undefined";
			}
		}

		public static class INT extends TypeCode {
			// Class for storing integer type code

			public INT() {
				// Constructor 
				this.tcode = "int";
			}
		}
		public static class DOUBLE extends TypeCode {
			// Class for storing integer type code

			public DOUBLE() {
				// Constructor 
				this.tcode = "double";
			}
		}
		public static class BOOL extends TypeCode {
			// Class for storing integer type code

			public BOOL() {
				// Constructor 
				this.tcode = "bool";
			}
		}
		public static class ARR extends TypeCode {
			// Class for storing integer type code
			private TypeCode el_tcode;

			public ARR(TypeCode t) {
				// Constructor 
				this.tcode = "arr of " + t.tcode;
				this.el_tcode = t;
			}
			public TypeCode typeOfElements() {
				// Constructor 
				return this.el_tcode;
			}
			public boolean isARR() { 
				return true; 
			}
		}
		public static class FUNC extends TypeCode {
			// Class for storing integer type code
			private LinkedList<TypeCode> args ;
			private TypeCode val ;

			public FUNC(){
				this.args = new LinkedList<TypeCode>();
			}
			public void addArg(TypeCode t){
				this.args.add(t);
			}
			public void setVal(TypeCode t){
				this.val = t;
			}
			public void emptyArgs(){
				this.args = new LinkedList<TypeCode>();
			}
		}
    }

    private static class Env { 
		// Class for storing environment variables

		private LinkedList<HashMap<String,TypeCode>> scopes;
		private TypeCode lastFuncType;
		private String lastFuncId;
		private Boolean returnExists;

		public Env() {
			// Environment for storing context of scopes in linked list.
			// Structure: [{'ident':'type','s2':'c2',...},{'s3':'c3',...},...]
			scopes = new LinkedList<HashMap<String,TypeCode>>();
			enterScope();
		}

		public TypeCode lookupVar(String x) {
			// Looking up inside whole environment for an identifier
			// If identifier is not found then throw an exception
			for (HashMap<String,TypeCode> scope : scopes) {
				TypeCode t = scope.get(x);
				if (t!=null)
					return t;
			}
			throw new TypeException("Unknown variable " + x + ".");
		}

		public void addVar(String x, TypeCode t) {
			// Add variable to the environment scope if it is not exist yet
			// Otherwise throw a type exception
			if (scopes.getFirst().containsKey(x))
				throw new TypeException("Variable " + x 
							+ " is already declared in this scope.");
			scopes.getFirst().put(x,t);
		}

		public void enterScope() {
			// Adds empty hash map of scope at the begining of list
			scopes.addFirst(new HashMap<String,TypeCode>());
		}

		public void leaveScope() {
			// Remove context of scope from evironment == exiting the scope
			scopes.removeFirst();
		}
    }

    public void typecheck(Program p) {
		// Entry point
		// Create environment(context) and check each statement
		Prog prog = (Prog)p;
		Env env = new Env();
		for (Def d : prog.listdef_) {
			checkDef(d, env);
		}
    }

	private void checkDef(Def d, Env env) {
		// Check statement using StmChecker class
		// 'accept' is method for using a visitor and returning a value
		d.accept(new DefChecker(), env);
    }

	public class DefChecker implements Def.Visitor<Object,Env>
  	{
	    public Object visit(FunDef p, Env env) {
			env.enterScope();
			env.lastFuncType = typeCode(p.type_);
			env.lastFuncId = p.ident_;

			for (Arg arg: p.listarg_){
				checkArg(arg, env);
			}
			for (Stm st: p.liststm_) {
				if(checkStm(st, env)) {
					env.returnExists = true;
					break;
				};
			}
			env.returnExists = false;
			env.leaveScope();
			return null;
		}
	}

	private void checkArg(Arg arg, Env env) {
		// Check statement using StmChecker class
		// 'accept' is method for using a visitor and returning a value
		arg.accept(new ArgChecker(), env);
    }

	public class ArgChecker implements Arg.Visitor<Object, Env> {
	    public Void visit(Argument arg, Env env) {
			TypeCode tc = typeCode(arg.type_);
			env.addVar( arg.ident_, tc);
			return null;
	    }
	}

    private boolean checkStm(Stm st, Env env) {
		// Check statement using StmChecker class
		// 'accept' is method for using a visitor and returning a value
		return st.accept(new StmChecker(), env);
    }

    private class StmChecker implements Stm.Visitor<Boolean,Env> {
		// Class for visiting the corresponding statement and check its type

		public Boolean visit(SExp p, Env env) {
			// Any expression
			// Check it
			inferExp(p.exp_, env);
			return false;
		}

		public Boolean visit(SDecl p, Env env) {
			// Declaration: int i;
			// Add variable to the scope
			env.addVar(p.ident_, typeCode(p.type_));
			return false;
		}

		public Boolean visit(SAss p, Env env) {
			// Assignment: i = 9 + j;
			// Lookup for type of variable at left hand side
			// Then checks right hand side expression
			TypeCode t = env.lookupVar(p.ident_);
			//System.out.println("before check has type " + t.tcode);
			checkExp(p.exp_, t, env);
			return false;
		}

		public Boolean visit(SInit p, Env env) {
			// Initialisation: int i = 9 + j;
			// Add variable to the scope
			// Then check right hand side expression
			TypeCode t = typeCode(p.type_);
			env.addVar(p.ident_, t);
			checkExp(p.exp_, t, env);
			return false;
		}

		public Boolean visit(SBlock p, Env env) {
			// Block: {...}
			// Enter the scope (add scope to environment), check all statements inside
			// then leave the scope (delete scope from the environment)
			env.enterScope();
			for (Stm st : p.liststm_) {
				checkStm(st, env);
			}
			env.leaveScope();
			return false;
		}

		public Boolean visit(SWhile p, Env env) {
			// While: while (i > 1) ... ;
			// Check that the expression in parentheses have type bool.
			// The body of a while statements needs to be interpreted in a fresh 
			// context block even if it is just a single statement. 
			// So, enter the scope (add scope to environment), check all statements inside
			// then leave the scope (delete scope from the environment)
			checkExp(p.exp_, new TypeCode.BOOL(), env);
			env.enterScope();
			checkStm(p.stm_, env);
			env.leaveScope();
			return false;
		}

		public Boolean visit(SPrint p, Env env) {
			// Print: print 9;
			// We don't care what the type is, just that there is one
			inferExp(p.exp_, env);
			return false;
		}

		public Boolean visit(SReturn p, Env env) {
			TypeCode t = inferExp(p.exp_, env);
			if( env.lastFuncType != t ) {
				throw new TypeException("Return stm: "+ env.lastFuncId+" of type "+ 
				env.lastFuncType +" doesnt match return type " + t);   
			} 
			return true;
		}

		public Boolean visit(SIfElse p, Env env) {
			// IfElse: if (i > 1) {...}
			//         else {...};
			// Check that the expression in parentheses have type bool.
			// The branches of the `if` statement are fresh scopes and need 
			// to be evaluated with new environment blocks. 
			// Enter the scope (add scope to environment), check `if (true)` branch
			// then leave the scope (delete scope from the environment) and make the 
			// same with `else` branch.
			checkExp(p.exp_, new TypeCode.BOOL(), env);

			env.enterScope();
			checkStm(p.stm_1, env);
			env.leaveScope();

			env.enterScope();
			checkStm(p.stm_2, env);
			env.leaveScope();
			
			return false;
		}
    }

    private void checkExp(Exp e, TypeCode t, Env env) {
		// Check correctness of an expression type 
		// and comparing it to the expected type t
		//System.out.println("Inside checkExp, type expected: " + t.tcode);
		TypeCode et = inferExp(e,env);
		//System.out.println("Inside checkExp, type given: " + et.tcode);
		if (!et.tcode.equals(t.tcode)) {
			throw new TypeException(PrettyPrinter.print(e) 
						+ " has type " + et.tcode 
						+ " expected " + t.tcode);
		}
    }

    private TypeCode inferExp(Exp e, Env env) {
		// Infer type of an expression using class TypeInferrer
		return e.accept(new TypeInferrer(), env);
    }

    private class TypeInferrer implements Exp.Visitor<TypeCode,Env> {
		// Class for infering the type of and expression and check its correctness

		public TypeCode visit(EVar p, Env env) {
			// Variable (identifier): i
			// Find type of variable in the environment
			return env.lookupVar(p.ident_);
		}

		public TypeCode visit(EInt p, Env env) {
			// Integer: int i
			return new TypeCode.INT();
		}

		public TypeCode visit(EDouble p, Env env) {
			// Double: double d
			return new TypeCode.DOUBLE();
		}

		public TypeCode visit(EBool p, Env env) {
			// Bool: bool b
			return new TypeCode.BOOL();
		}

		public TypeCode visit(Array p, Env env) {
			// And: true || false 
			// Check if type of both variables is bool
			// Otherwise throw an exception
			// If first expression is false then return result of type bool (second expression)
			// Otherwise - return true
			/*TypeCode et = new TypeCode.Undefined();
			for (Exp ex : p.listexp_) {
				et = inferExp(ex, env);
				break;
			}*/
			TypeCode et = typeCode(p.type_);
			//System.out.println("et in Array: " + et.tcode);

			for (Exp ex : p.listexp_) {
				checkExp(ex, et, env);
			}
			return new TypeCode.ARR(et);
		}

		public TypeCode visit(Append p, Env env) {
			// TypeCode of array:
			TypeCode et = inferExp(p.exp_1, env);
			// check that it is exactly ARRAY:
			if (!et.isARR()) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) 
						+ " has type " + et.tcode 
						+ " array type expected ");
			}
			// Type of Array elements:
			TypeCode el_tcode = et.typeOfElements();
			// Type of added element should be the same:
			checkExp(p.exp_2, el_tcode, env);
			return et;
		}

		public TypeCode visit(Head p, Env env) {
			// TypeCode of array:
			TypeCode et = inferExp(p.exp_, env);
			// check that it is exactly ARRAY:
			if (!et.isARR()) {
				throw new TypeException(PrettyPrinter.print(p.exp_) 
						+ " has type " + et.tcode 
						+ " array type expected ");
			}
			// Type of Array elements:
			TypeCode el_tcode = et.typeOfElements();
			return el_tcode;
		}

		public TypeCode visit(IsEmpty p, Env env) {
			// TypeCode of array:
			TypeCode et = inferExp(p.exp_, env);
			// check that it is exactly ARRAY:
			if (!et.isARR()) {
				throw new TypeException(PrettyPrinter.print(p.exp_) 
						+ " has type " + et.tcode 
						+ " array type expected ");
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(Last p, Env env) {
			// TypeCode of array:
			TypeCode et = inferExp(p.exp_, env);
			// check that it is exactly ARRAY:
			if (!et.isARR()) {
				throw new TypeException(PrettyPrinter.print(p.exp_) 
						+ " has type " + et.tcode 
						+ " array type expected ");
			}
			// Type of Array elements:
			TypeCode el_tcode = et.typeOfElements();
			return el_tcode;
		}

		public TypeCode visit(EAdd p, Env env) {
			// Addition: i + 3 
			// Check if type of both variables is the same
			// If true then return type of first variable
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"addition operation written in " 
							+ p + 
							" does not support bool parameters");
			}
			return t1;
		}

		public TypeCode visit(ESub p, Env env) {
			// Subtraction: i - 3 
			// Check if type of both variables is the same
			// If true then return type of first variable
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"subtraction operation written in " 
							+ p + 
							" does not support bool parameters");
			}
			return t1;
		}

		public TypeCode visit(EMul p, Env env) {
			// Multiplication: i * 3 
			// Check if type of both variables is numeric
			// If true then return [INT if int*int] 
			// or [DOUBLE if at least one of the multipliers 
			// is of type duoble]
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);

			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"multiplication operation written in " 
							+ p + 
							" does not support bool parameters");
			}
			if (t2.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_2) + 
							"multiplication operation written in " 
							+ p + 
							" does not support bool parameters");
			}
			if (t1.tcode.equals("double") || t2.tcode.equals("double")){
				return new TypeCode.DOUBLE();
			}
			return t1;
		}

		public TypeCode visit(EDiv p, Env env) {
			// Division: i / 3 
			// Check if type of both variables is numeric
			// If true then return type DOUBLE
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"division operation written in " 
							+ p + 
							" does not support bool parameters");
			}
			if (t2.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_2) + 
							"division operation written in " 
							+ p + 
							" does not support bool parameters");
			}
			return new TypeCode.DOUBLE();
		}

		public TypeCode visit(EPostIncr p, Env env) {
			// Post Increment: i ++  
			// Check if type of the variable is int
			// If true then return type INT
			// Otherwise throw an exception
			TypeCode t = env.lookupVar(p.ident_);
			if (!t.tcode.equals("int")) {
				throw new TypeException("post increment written in " 
							+ p + 
							" supports only int parameters, but "
							+ t.tcode + " was given");
			}
			return new TypeCode.INT();
		}

		public TypeCode visit(EPostDecr p, Env env) {
			// Post Decrement: i -- 
			// Check if type of the variable is int
			// If true then return type INT
			// Otherwise throw an exception
			TypeCode t = env.lookupVar(p.ident_);
			if (!t.tcode.equals("int")) {
				throw new TypeException("post decrement written in " 
							+ p + 
							" supports only int parameters, but "
							+ t.tcode + " was given");
			}
			return new TypeCode.INT();
		}

		public TypeCode visit(EPreIncr p, Env env) {
			// Pre Increment: ++ i   
			// Check if type of the variable is int
			// If true then return type INT
			// Otherwise throw an exception
			TypeCode t = env.lookupVar(p.ident_);
			if (!t.tcode.equals("int")) {
				throw new TypeException("pre increment written in " 
							+ p + 
							" supports only int parameters, but "
							+ t + " was given");
			}
			return new TypeCode.INT();
		}

		public TypeCode visit(EPreDecr p, Env env) {
			// Pre Decrement: -- i 
			// Check if type of the variable is int
			// If true then return type INT
			// Otherwise throw an exception
			TypeCode t = env.lookupVar(p.ident_);
			if (!t.tcode.equals("int")) {
				throw new TypeException("pre decrement written in " 
							+ p + 
							" supports only int parameters, but "
							+ t.tcode + " was given");
			}
			return new TypeCode.INT();
		}

		public TypeCode visit(ELess p, Env env) {
			// Less Than: i < 3 
			// Check if type of both variables is the same (the numeric one)
			// If true then return BOOL type
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"less operator written in " 
							+ p + 
							" does not support bool parameters");
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(EGreater p, Env env) {
			// Greater Than: i > 3 
			// Check if type of both variables is the same (the numeric one)
			// If true then return result of type bool 
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"greater operator written in " 
							+ p + 
							" does not support bool parameters");
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(ELEq p, Env env) {
			// Less Than or Equal: i <= 3 
			// Check if type of both variables is the same (the numeric one)
			// If true then return result of type bool 
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"less or equal operator written in " 
							+ p + 
							" does not support bool parameters");
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(EGEq p, Env env) {
			// Greater Than or Equal: i >= 3 
			// Check if type of both variables is the same (the numeric one)
			// If true then return result of type bool 
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			if (t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							"greater or equal operator written in " 
							+ p + 
							" does not support bool parameters");
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(EEq p, Env env) {
			// Equal: i == 3 
			// Check if type of both variables is the same
			// If true then return result of type bool 
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(ENEq p, Env env) {
			// Not equal: i != 3 
			// Check if type of both variables is the same
			// If true then return result of type bool 
			// Otherwise throw an exception
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals(t2.tcode)) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but " + PrettyPrinter.print(p.exp_2)
							+ " has type " + t2.tcode);
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(EAnd p, Env env) {
			// And: true && false 
			// Check if type of both variables is bool
			// Otherwise throw an exception
			// If first expression is true then return result of type bool (second expression)
			// Otherwise - return false
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but expected bool");
			}
			if (!t2.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_2) + 
							" has type " + t2.tcode
							+ " but expected bool");
			}
			return new TypeCode.BOOL();
		}

		public TypeCode visit(EOr p, Env env) {
			// And: true || false 
			// Check if type of both variables is bool
			// Otherwise throw an exception
			// If first expression is false then return result of type bool (second expression)
			// Otherwise - return true
			TypeCode t1 = p.exp_1.accept(this, env);
			TypeCode t2 = p.exp_2.accept(this, env);
			if (!t1.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_1) + 
							" has type " + t1.tcode
							+ " but expected bool");
			}
			if (!t2.tcode.equals("bool")) {
				throw new TypeException(PrettyPrinter.print(p.exp_2) + 
							" has type " + t2.tcode
							+ " but expected bool");
			}
			return new TypeCode.BOOL();
		}

    }

    private TypeCode typeCode(Type t) {
		// Return type code in string format
		return t.accept(new TypeCoder(), null);
    }

    private class TypeCoder implements Type.Visitor<TypeCode,Object> {
		// Class for returning the type code in string format

		public TypeCode visit(TInt t, Object arg) {
			// Integer
			return new TypeCode.INT();
		}

		public TypeCode visit(TDouble t, Object arg) {
			// Double
			return new TypeCode.DOUBLE();
		}

		public TypeCode visit(TBool t, Object arg) {
			// Bool
			return new TypeCode.BOOL();
		}

		public TypeCode visit(TArray t, Object arg) {
			// Array
			TypeCode nested_type = typeCode(t.type_);
			return new TypeCode.ARR(nested_type);
		}
    }

}
