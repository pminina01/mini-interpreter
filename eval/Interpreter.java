package eval;
import lama.Absyn.*;
import lama.Yylex;

import java.util.HashMap;
import java.util.LinkedList;
import java.io.*;

public class Interpreter {
	// Class for interpreting the program

	private Env env;

	public Interpreter() {
		env = new Env();
	}

    public void interpret(Program p) {
		// Create empty environment of a program and execute statements
		Prog prog = (Prog)p;
		for (Stm s : prog.liststm_) {
			execStm(s, env);
		}
    }

    private static abstract class Value {
		// Class for storing information about the value

		public boolean isInt() { 
			// Return boolean if value is integer
			// Default is false, because it is "value"
			return false; 
		}

		public boolean isDouble() { 
			// Return boolean if value is double
			// Default is false, because it is "value"
			return false; 
		}

		public boolean isArr() { 
			return false; 
		}

		public LinkedList<Value> getArray() { 
			throw new RuntimeException(this + " is not an array."); 
		}

		public Integer getInt() { 
			// Throw an exception as "value" is not "integer"
			throw new RuntimeException(this + " is not an integer."); 
		}

		public Double getDouble() { 
			// Throw an exception as "value" is not "double"
			throw new RuntimeException(this + " is not a double."); 
		}

		public Boolean getBool() { 
			// Throw an exception as "value" is not "bool"
			throw new RuntimeException(this + " is not a bool."); 
		}

		public static class Undefined extends Value {
			// Class for storing undefined value
			// Undefined value is a value with only name of identifier

			public Undefined() {
				// Constructor 
			}

			public String toString() { 
				// For printing
				return "undefined"; 
			}
		}

		public static class IntValue extends Value {
			// Class for storing integer value

			private Integer i;

			public IntValue(Integer i) { 
				// Constructor
				this.i = i; 
			}

			public boolean isInt() { 
				// Return boolean if value is integer
				// As this is integer class, then isInt() is true
				return true; 
			}

			public Integer getInt() {
				// Return int value itself 
				return i; 
			}

			public Double getDouble() {
				// Return double value (cast int value to double)
				double d = i; 
				return d; 
			}

			public String toString() { 
				// For printing
				return i.toString(); 
			}
		}

		public static class DoubleValue extends Value {
			// Class for storing double value

			private Double d;

			public DoubleValue(Double d) { 
				// Constructor
				this.d = d; 
			}

			public boolean isDouble() { 
				// Return boolean if value is double
				// As this is double class, then isDouble() is true
				return true; 
			}

			public Double getDouble() { 
				// Return double value itself
				return d; 
			}

			public String toString() { 
				// For printing
				return d.toString(); 
			}
		}

		public static class BoolValue extends Value {
			// Class for storing bool value

			private Boolean b;

			public BoolValue(Bool b) { 
				// Constructor
				if (b instanceof lama.Absyn.Bool_false) {
					this.b = false;
				} else {
					this.b = true;
				}			 
			}
			public BoolValue(Boolean b) { 
				// Constructor
				this.b = b;			 
			}

			public Boolean getBool() { 
				// Return bool value itself
				return b; 
			}

			public String toString() { 
				// For printing
				return b.toString(); 
			}
		}

		public static class ArrayValue extends Value {
			// Class for storing bool value

			private LinkedList<Value> elements;

			public ArrayValue(LinkedList<Value> elements) { 
				// Constructor
				this.elements = elements;			 
			}

			public boolean isArr() { 
				// Return bool value itself
				return true; 
			}

			public LinkedList<Value> getArray() { 
				// Return bool value itself
				return elements; 
			}

			public String toString() { 
				// For printing
				return elements.toString(); 
			}
		}
    }

    private static class Env { 
		// Class for storing environment variable of program

		private LinkedList<HashMap<String,Value>> scopes;

		public Env() {
			// Environment for storing context of scopes in linked list.
			// Structure: [{'ident':'value','s2':'c2',...},{'s3':'c3',...},...]
			scopes = new LinkedList<HashMap<String,Value>>();
			enterScope();
		}

		public Value lookupVar(String x) {
			// Looking up inside whole environment for an identifier
			// If identifier is found then return Value object
			// Otherwise throw an exception
			for (HashMap<String,Value> scope : scopes) {
				Value v = scope.get(x);
				if (v != null)
					return v;
			}
			throw new RuntimeException("Unknown variable " + x + " in " + scopes);
		}

		public void addVar(String x) {
			// Add variable to the environment scope
			// Form is: (variable_name, undefined_value_object)
			scopes.getFirst().put(x,new Value.Undefined());
		}

		public void setVar(String x, Value v) {
			// Find variable in the environment and set to new value
			for (HashMap<String,Value> scope : scopes) {
				if (scope.containsKey(x)) {
					scope.put(x,v);
					return;
				}
			}
		}

		public void enterScope() {
			// Adds empty hash map of scope at the begining of list
			scopes.addFirst(new HashMap<String,Value>());
		}

		public void leaveScope() {
			// Remove context of scope == exiting the scope
			scopes.removeFirst();
		}
    }

    private void execStm(Stm st, Env env) {
		// Execute statement using StmExecuter class
		// 'accept' is method for using a visitor and returning a value
		st.accept(new StmExecuter(), env);
    }

    private class StmExecuter implements Stm.Visitor<Object,Env> {
		// Class for visiting the corresponding statement and execute it

		public Object visit(lama.Absyn.SExp p, Env env) {
			// Expression of any type
			// Just evaluate it
			evalExp(p.exp_, env);
			return null;
		}

		public Object visit(lama.Absyn.SDecl p, Env env) {
			// Declaration: int i;
			// Add variable to the scope as undentified
			env.addVar(p.ident_);
			return null;
		}

		public Object visit(lama.Absyn.SAss p, Env env) {
			// Assignment: i = 9 + j;
			// Evaluate right hand side expression
			// Then set left hand side variable to this result
			env.setVar(p.ident_, evalExp(p.exp_, env));
			return null;
		}

		public Object visit(lama.Absyn.SInit p, Env env) {
			// Initialisation: int i = 9 + j;
			// Add variable to the scope
			// Evaluate right hand side expression
			// Then set left hand side variable to this result
			env.addVar(p.ident_);
			env.setVar(p.ident_, evalExp(p.exp_, env));
			return null;
		}

		public Object visit(lama.Absyn.SBlock p, Env env) {
			// Block: {...}
			// Enter the scope (add scope to environment), execute all statements inside
			// then leave the scope (delete scope from the environment)
			env.enterScope();
			for (Stm st : p.liststm_) {
				execStm(st, env);
			}
			env.leaveScope();
			return null;
		}

		public Object visit(lama.Absyn.SFun p, Env env) {
			env.addVar(p.ident_1);

			env.enterScope();
			env.addVar(p.ident_2);

			for (Stm st : p.liststm_) {
				execStm(st, env);
			}
			Value v = evalExp(p.exp_, env);
			env.leaveScope();
			env.setVar(p.ident_1, v);
			return v;
		}

		public Object visit(lama.Absyn.SPrint p, Env env) {
			// Print: print 9;
			// Evaluate expression after print and print it to console
			Value v = evalExp(p.exp_, env);
			System.err.println(v.toString());
			return null;
		}

		public Object visit(lama.Absyn.SWhile p, Env env) {
			// While: while (i > 1) ... ;
			// The condition expression is evaluated first. 
			// If the value is true, the body is interpreted in the resulting environment, 
			// and the whilestatement is executed again. Otherwise, exit the loop.
			// The body of a while statements needs to be interpreted in a fresh context 
			// block even if it is just a single statement. 
			Value condition = evalExp(p.exp_, env);
			while (condition.getBool() == true) {
				env.enterScope();
				execStm(p.stm_, env);
				env.leaveScope();
				condition = evalExp(p.exp_, env);
			}
			return null;
		}

		public Object visit(lama.Absyn.SIfElse p, Env env) {
			// IfElse: if (i > 1) {...}
			//         else {...};
			// The condition expression is first evaluated.
			// If the value is `true`, the then-branch (statement before `else`) is interpreted. 
			// If the value is `false`, the else-branch (statement after `else`) is interpreted.
			// The branches of the `if` statement are fresh scopes and need 
			// to be evaluated with new environment blocks. 
			Value condition = evalExp(p.exp_, env);
			if (condition.getBool() == true) {
				env.enterScope();
				execStm(p.stm_1, env);
				env.leaveScope();
			} else {
				env.enterScope();
				execStm(p.stm_2, env);
				env.leaveScope();
			}
			return null;
		}

		
		public Object visit(lama.Absyn.SImp p, Env env) {

			lama.Yylex l_imp = null;
			try {
				l_imp = new lama.Yylex(new FileReader(p.string_)); // Lexer
				lama.parser p_imp = new lama.parser(l_imp); // Parser
				lama.Absyn.Program parse_tree_imp = p_imp.pProgram(); // Parse tree
				Interpreter imp = new Interpreter(); // Interpret
				imp.interpret(parse_tree_imp);
				Env env_imp = imp.env;
				HashMap<String,Value> scopes_imp = env_imp.scopes.get(0);
				for (String key : scopes_imp.keySet()) {
					env.addVar(key);
					env.setVar(key, scopes_imp.get(key));
				}							
			} catch (TypeException e) {
				// Type error
				System.out.println("TYPE ERROR");
				System.err.println(e.toString());
				System.exit(1);
			} catch (RuntimeException e) {
				// Runtime error
				System.out.println("RUNTIME ERROR");
				System.err.println(e.toString());
				System.exit(1);
			} catch (java.io.IOException e) {
				// IO error
				System.err.println(e.toString());
				System.exit(1);
			} catch (Throwable e) {
				// Syntax error
				System.out.println("SYNTAX ERROR");
				System.out.println("At line " + String.valueOf(l_imp.line_num()) 
						+ ", near \"" + l_imp.buff() + "\" :");
				System.out.println("     " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}			
			return null;
		}
    }

    private Value evalExp(Exp e, Env env) {
		// Evaluate expression using ExpEvaluator class
		// 'accept' is method for using a visitor and returning a value
		return e.accept(new ExpEvaluator(), env);
    }

    private class ExpEvaluator implements Exp.Visitor<Value,Env> {
		// Class for evaluating expression

		public Value visit(lama.Absyn.EVar p, Env env) {
			// Variable (identifier): i
			// Search for ident in environment and return Value object with info
			return env.lookupVar(p.ident_);
		}

		public Value visit(lama.Absyn.EInt p, Env env) {
			// Integer: int i
			// Return IntValue object
			return new Value.IntValue(p.integer_);
		}

		public Value visit(lama.Absyn.EDouble p, Env env) {
			// Double: double d
			// Return DoubleValue object
			return new Value.DoubleValue(p.double_);
		}

		public Value visit(lama.Absyn.EBool p, Env env) {
			// Bool: bool b
			// Return BoolValue object
			return new Value.BoolValue(p.bool_);
		}

		public Value visit(lama.Absyn.Array p, Env env) {
			// Array: [int] [1,2,3]
			// Return ArrayValue object
			LinkedList<Value> elements = new LinkedList<>();
			for (Exp ex : p.listexp_) {
				elements.add(evalExp(ex, env));
			}
			return new Value.ArrayValue(elements);
		}

		public Value visit(lama.Absyn.Append p, Env env) {
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (!v1.isArr()) {
				throw new RuntimeException(p.exp_1 + " expected to be an Array but got " 
				+ v1);
			} 
			LinkedList<Value> elements = v1.getArray();
			elements.add(v2);
			return new Value.ArrayValue(elements);
		}

		public Value visit(lama.Absyn.Head p, Env env) {
			Value v = p.exp_.accept(this, env);
			if (!v.isArr()) {
				throw new RuntimeException(p.exp_ + " expected to be an Array but got " 
				+ v);
			} 
			LinkedList<Value> elements = v.getArray();
			return elements.getFirst();
		}

		public Value visit(lama.Absyn.IsEmpty p, Env env) {
			Value v = p.exp_.accept(this, env);
			if (!v.isArr()) {
				throw new RuntimeException(p.exp_ + " expected to be an Array but got " 
				+ v);
			} 
			LinkedList<Value> elements = v.getArray();
			return new Value.BoolValue(elements.isEmpty());
		}

		public Value visit(lama.Absyn.Last p, Env env) {
			//return the last element of the array
			Value v = p.exp_.accept(this, env);
			if (!v.isArr()) {
				throw new RuntimeException(p.exp_ + " expected to be an Array but got " 
				+ v);
			} 
			LinkedList<Value> elements = v.getArray();
			if (elements.isEmpty()) {throw new RuntimeException(p.exp_ + " Array is empty ");};
			return elements.getLast();
		}

		public Value visit(lama.Absyn.EAdd p, Env env) {
			// Addition: i + 3 
			// Check if type of first variables is int 
			// => return new IntValue object as result of addition
			// Otherwise return new DoubleValue object as result of addition
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.IntValue(v1.getInt() + v2.getInt());
			} else {
				return new Value.DoubleValue(v1.getDouble() + v2.getDouble());
			}
		}

		public Value visit(lama.Absyn.ESub p, Env env) {
			// Subtraction: i - 3 
			// Check if type of first variables is int 
			// => return new IntValue object as result of subtraction
			// Otherwise return new DoubleValue object as result of subtraction
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.IntValue(v1.getInt() - v2.getInt());
			} else {
				return new Value.DoubleValue(v1.getDouble() - v2.getDouble());
			}
		}

		public Value visit(lama.Absyn.EMul p, Env env) {
			// Multiplication: i * 3 
			// return new IntValue object if both multipliers are of type int
			// Otherwise return new DoubleValue object as result of multiplication
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt() && v2.isInt()) {
				return new Value.IntValue(v1.getInt() * v2.getInt());
			} else {
				return new Value.DoubleValue(v1.getDouble() * v2.getDouble());
			}
		}

		public Value visit(lama.Absyn.EDiv p, Env env) {
			// Division: i * 3 
			// return new DoubleValue object as result of division
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			return new Value.DoubleValue(v1.getDouble() / v2.getDouble());
		}

		public Value visit(lama.Absyn.EPostIncr p, Env env) {
			// Post Increment: i ++  
			// Return the unchanged value of the variable
			// Then set the incremented value to the variable
			Value tmp = env.lookupVar(p.ident_);
			Value v = new Value.IntValue(tmp.getInt() + 1);
			env.setVar(p.ident_, v);
			return tmp;
		}

		public Value visit(lama.Absyn.EPostDecr p, Env env) {
			// Post Decrement: i -- 
			// Return the unchanged value of the variable
			// Then set the decremented value to the variable
			Value tmp = env.lookupVar(p.ident_);
			Value v = new Value.IntValue(tmp.getInt() - 1);
			env.setVar(p.ident_, v);
			return tmp;
		}

		public Value visit(lama.Absyn.EPreIncr p, Env env) {
			// Pre Increment: ++ i   
			// Set the incremented value to the variable
			// Then return this value
			Value tmp = env.lookupVar(p.ident_);
			Value v = new Value.IntValue(tmp.getInt() + 1);
			env.setVar(p.ident_, v);
			return v;
		}

		public Value visit(lama.Absyn.EPreDecr p, Env env) {
			// Pre Decrement: -- i 
			// Set the decremented value to the variable
			// Then return this value
			Value tmp = env.lookupVar(p.ident_);
			Value v = new Value.IntValue(tmp.getInt() - 1);
			env.setVar(p.ident_, v);
			return v;
		}

		public Value visit(lama.Absyn.ELess p, Env env) {
			// Less Than: i < 3 
			// Compare two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.BoolValue(v1.getInt() < v2.getInt());
			} else {
				return new Value.BoolValue(v1.getDouble() < v2.getDouble());
			}
		}

		public Value visit(lama.Absyn.EGreater p, Env env) {
			// Greater Than: i < 3 
			// Compare two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.BoolValue(v1.getInt() > v2.getInt());
			} else {
				return new Value.BoolValue(v1.getDouble() > v2.getDouble());
			}
		}

		public Value visit(lama.Absyn.ELEq p, Env env) {
			// Less Than or Equal: i <= 3 
			// Compare two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.BoolValue(v1.getInt() <= v2.getInt());
			} else {
				return new Value.BoolValue(v1.getDouble() <= v2.getDouble());
			}
		}

		public Value visit(lama.Absyn.EGEq p, Env env) {
			// Greater Than or Equal: i >= 3 
			// Compare two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.BoolValue(v1.getInt() >= v2.getInt());
			} else {
				return new Value.BoolValue(v1.getDouble() >= v2.getDouble());
			}
		}

		public Value visit(lama.Absyn.EEq p, Env env) {
			// Equal: i == 3 
			// Compare two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.BoolValue(v1.getInt() == v2.getInt());
			} else if (v1.isDouble()) {
				return new Value.BoolValue(v1.getDouble().equals(v2.getDouble()));
			} else {
				return new Value.BoolValue(v1.getBool() == v2.getBool());
			}
		}

		public Value visit(lama.Absyn.ENEq p, Env env) {
			// Not equal: i != 3 
			// Compare two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			if (v1.isInt()) {
				return new Value.BoolValue(v1.getInt() != v2.getInt());
			} else if (v1.isDouble()) {
				return new Value.BoolValue(!v1.getDouble().equals(v2.getDouble()));
			} else {
				return new Value.BoolValue(v1.getBool() != v2.getBool());
			}
		}

		public Value visit(lama.Absyn.EAnd p, Env env) {
			// And: true && false
			// Conjunction two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			return new Value.BoolValue(v1.getBool() && v2.getBool());
		}

		public Value visit(lama.Absyn.EOr p, Env env) {
			// Or: true || false
			// Disjunction two values and return the answer
			// Return new BoolValue object as result of comparison
			Value v1 = p.exp_1.accept(this, env);
			Value v2 = p.exp_2.accept(this, env);
			return new Value.BoolValue(v1.getBool() || v2.getBool());
		}
		
    }
}
