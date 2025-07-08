# Chapter 8: Statements and State
>“We now support actions, not just values.”

## Updated Grammar
```
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;

statement      → exprStmt
               | printStmt
               | block ;

block          → "{" declaration* "}" ;

exprStmt       → expression ";" ;

printStmt      → "print" expression ";" ;

varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;

expression     → assignment ;

assignment     → IDENTIFIER "=" assignment
               | equality ;

equality       → comparison ( ( "!=" | "==" ) comparison )* ;

comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

term           → factor ( ( "-" | "+" ) factor )* ;

factor         → unary ( ( "/" | "*" ) unary )* ;

unary          → ( "!" | "-" ) unary
               | primary ;

primary        → NUMBER
               | STRING
               | "true"
               | "false"
               | "nil"
               | IDENTIFIER
               | "(" expression ")" ;
```
## Grammar Features And some simple reminders 
| **Grammar Feature**                                  | **New Code Concept**                                                                     |
| ---------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| `statement → ...`                                    | Each rule like `printStmt()` returns a `Stmt`                                            |
| `block → "{" declaration* "}"`                       | A recursive method that returns a `Stmt.Block` node containing a list of `Stmt` children |
| `varDecl → "var" IDENTIFIER ( "=" expression )? ";"` | Uses `if (match(EQUAL))` to check optional initializer, or `null` if missing             |
| \`assignment → IDENTIFIER "=" assignment             | equality\`                                                                               |
| `EOF`                                                | Use `check(EOF)` or `isAtEnd()` in `parse()`                                             |
## Some Key Reminders 
| **Grammar Notation** | **Code Representation**                                             | **Example**                                                            |
| -------------------- | ------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| `Terminal`           | `match(TokenType.X)` and `consume(TokenType.X, "...")`              | `match(SEMICOLON)`, `consume(IDENTIFIER, "...")`                       |
| `Nonterminal`        | Call to the parsing method for that rule                            | `expression()`, `statement()`, `primary()`, `varDecl()`                |
| \`                   | \` (alternation)                                                    | `if/else if`, or `switch` blocks                                       |
| `*` (zero or more)   | `while` or `for` loop over that rule                                | `while (match(PLUS, MINUS))` in `term()`                               |
| `+` (one or more)    | Same as `*`, but ensure at least one before loop                    | Not commonly used directly in grammar so far                           |
| `?` (optional)       | Simple `if` statement with `match(...)`                             | `if (match(EQUAL)) { ... }` in `assignment()`                          |
| `→`                  | Maps to method body logic                                           | `expression → assignment` → method `expression()` calls `assignment()` |
| `declaration*`       | `List<Stmt> statements = new ArrayList<>();` + `while (!isAtEnd())` | In `parse()` and `block()`                                             |

# Statements The Two Simplest Kinds
>"State and statements go hand in hand. Since statements, by definition, don’t evaluate to a value, they need to do something else to be useful. That something is called a side effect."

## Expression Statements
- lets you place an expression where a statement is expected. 
- evaluate expressions that have side effects. 
- any function or method call followed by a `;` is an expression statement

### Example Function
- Functions return values
- Since functions return a value, they must appear where a value is expected. 
- Thus functions can only be called where an expression is expected.
- Like inside an expression assignment, math, or an argument. 
```
var x = add(1, 2); // valid, used as expression

add(1,2) // would be invalid 
add(1,2) ; // Remember that the ; permotes it to statement hood! so this is valid! wont do much, but its valid
```
### Procedures
- Procedures cannot return a value 
- There is a statement form for calling a procedure
- We dont have any interesting procedures yet, so keep this on the back burner

## Print Statement 
- Evaluates expression prints results to the user
- We bake that shit right into our language hell yeah. 

### New syntax new grammar
- "The __top level__ of a script is simply a list of statements"
- *program* repersents a complete Lox script
- A program is a list of statements follwed by an EOF token
- EOF makes sure we get the entire file parsed. 
- *statement* only has two cases for our two kinds of statments
- we get to have more in later chapters
- cool fucking stufff you 
```
program        → statement* EOF ;

statement      → exprStmt
               | printStmt ;

exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
```
### statement, exprStmt, expression
- At this stage of the grammar, we support only two kinds of statements: `print` and expression statements.
- Expression statements (`exprStmt`) wrap expressions, allowing us to evaluate expressions for their side effects.
- This is useful for function calls and other expressions that don’t need to return a value we care about.
- Think about it, side effects like modifying a variable in a data structure does not need its returned value captured.
- Later chapters will expand the kinds of statements (e.g., blocks, if, while, etc.).

### Statement syntax trees
- No place where both expressions and statements are allowed. 
- they are two disjoint syntax so we can split this up into two classes
- The expression class which we already have.
- The statement class which we are going to be building

## Parsing Statements
- Each element in the `List<Stmt>` is a top level node for a __statement__ *AST*
- Thats right our program is a sequential order of statements read from the top down.
- This is just declaring our list of statements that will be returned.
- `List<Stmt> statements = new ArrayList<>();`
- We keep adding statements to our list As long as we dont encounter the `EOF` token.
- `while (!isAtEnd()) { statements.add(statement()); }`
- Once we hit the end of our file we return all of our statements
- `return statements;`
- Below is our wonderful `Parse()`
```
  List<Stmt> parse() 
  {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) 
    {
      statements.add(statement());
    }

    return statements; 
  }
```

## Two Types of Statements
- We want to find which specific statement we have. 
- `if(match(PRINT)) return printStatement();`
- If it's a print statement, [Notes](#print-statement).
- We determine this by looking at the current token
- If its not a known kind of statement
- Then we fall back to an expression statement [Notes](#expression-statement)
```    private Stmt statement(){
        if(match(PRINT)) return printStatement();

        return expressionStatement();
    }
```

### Print Statement Method
- It is required for Recursive Decent Parsers to call the next method of the current grammar rule
- `Expr value = expression();`
- We already matched the print token
- So we can safely consume the semicolon
- Then evaluate our expression!
- `consume(SEMICOLON, "Expect ';' after value.");`
-  When Stmt.Print is created, it wraps this expression as a print statement in the AST.
- `Stmt.Print(value);`
```    private Stmt printStatement()
    {
        Expr value = expression();
        consume(SEMICOLON,"Expect ';' after value.");
        return new Stmt.Print(value);
    }
```

#### Example
- printStmt → "print" expression ";"
- So we find a print statement and expect and expression
- Expression() parses `1 + 2` and returns a `Expr.Binary` AST node
- This happens because of the `evaluate` method
- Our goal is to wrap this expression into a `printStmt`
- value is the parsed expression after print (e.g., 1 + 2, "hello", or a variable). 
- It's used to create the Stmt.Print node so it can be interpreted later and printed to the console.

### Expression Statement Method
- Very similar to the first method.
- We parse an expression followed by a semicolon
- Then we wrap that `Expr` in a nice little `Stmt`
- Then we return our present, well wrapped expressionStatement.
```
private Stmt expressionStatement()
{
  Expr expr = expression();
  consume(SEMICOLON, "Expect ';' after expression.");
  return new Stmt.Expression(expr);
}
```

## Working through the front end
- We add the `Stmt.Visitor<Void>`
- remember statements do not return a value, which mean it returns... can you guess it.... *Void*
- well an uppercase void because Java is wierd

### Visit Expression Statement
- We evaluate the inner expression using the `evaluate()` Method
- pssst this just calls the `accept()` method for our visitor pattern.
- Well we just discard the value and return null. 
- If the qoute below does not make sense. Please, do not panic! It is a tough chapter. 
- "Appropriately enough, we discard the value returned by evaluate() by placing that call inside a Java expression statement"
- Remember at the beginning we talked about how `add(1,2)` would not work but `add(1,2);` would
- If you dont, no problem! Just go read it again above. Its fundemental to understanding why we have expression statements. 
```
@Override
public Void visitExpressionStmt(Stmt.Expression stmt) 
{
  evaluate(stmt.expression);
  return null;
}
```

### Visit Print Statement
- Not very differnt except for one key difference
- Instead of discarind the expressions value right away. 
- We convert it to a string using `stringify()`
- The we print the results to stdout. 
``` 
@Override
public Void visitPrintStmt(Stmt.Print stmt) 
{
  Object value = evaluate(stmt.expression);
  System.out.println(stringify(value));
  return null;
}

```

### Back To The Interpret Method
- First it needs to be able to accept a list of statements
- We try to execute each statement with in our list of statements!
- This uses the [execute()](#execute) helper method
- If we cant execute we through an error!
- Simple stuff, right?
```
  void interpret(List<Stmt> statements) 
  {
    try 
    {
      for (Stmt statement : statements) 
      {
        execute(statement);
      }
    } 
    catch (RuntimeError error) 
    {
      Lox.runtimeError(error);
    }
  }
```
### The Current Pipeline
>Here is the overview so far

#### Scanning
- A scanner is passed a source(A flat sequence of raw characters).
- `Scanner scanner = new Scanner(source);`
- Tokenization takes place which returns a list of tokens (A flat sequence of Tokens).
- `List<Token> tokens = scanner.scanTokens();`

#### Parsing
- The parser takes the list of tokens (A flat sequence of Tokens). 
- We created a list of tokens(A flat sequence of Tokens) from the scanner.
- `Parser parser = new Parser(tokens);`
- The parser then returns a list of statements(A sequence of statement tree nodes).
- `List<Stmt> statements = parser.parse();`

#### Interpreting 
- The interpreter then takes in a list of statements(A sequence of statement tree nodes).
- `interpreter.interpret(statements);`
- The list of tree nodes are statement AST
- We decent down each AST evaluating the statements and expressions in sequential order.
- Thus Lox is a single pass compiler executing from top down. 

# Variables

## Variable Declaration Statement
> Brings a new variable into the world! var kemonomimi = catgirl;
> This creates a new binding that associates a name (here “kemonomimi”) with a value (here, the string "catgirl").

## Variable Expression Statement
> Meowtastic, the identifier kemonomimi is used as an expression. It looks up the value bound to that name and returns it.

## Statement Precedence Levels
- Statement Precedence is different from Expression Precedence 
- Expression Percedence is your order of operations the `*` before the `+`
- Statement Precedence is about where you are allowed to place different types of statements
- Where can you place a Variable Declaration statement?
- Where can you use a Variable Expression Statement? 

### High Level Precedence
- Expression statements, print, block
- These can appear anywhere, including in places like the body of an if, while, etc.
- Statments like this are higher level since they can go anywhere in our [precedence chain.](#precedence-chain)
- expressionStatement 
- printStatement 
- blockStatement

### Low Level Precedence
- Declarations like var, fun,
- Only allowed at the top level
- Also allowed in nested blocks

### Precedence Chain
```
program
└── declaration*   ← accepts both varDecl and statement
    ├── varDecl    ← only valid in this context or inside a block
    └── statement  ← safe everywhere (like in if/while bodies)
        ├── printStmt
        ├── expressionStmt
        └── block
```

### Variable Syntax 
- Ask yourself why one of these statements is okay, but not the other.
- `if (kemonomimi) print "MEOWWWWWWWWWW?";` OKAY
- `if (catgirl) var kemonomimi = "Meow";` NOT OKAY
- Allowing the variable declaration in a if statement raises a couple questions
- What would be the scope of this statement?
- We disallow it for said reason, its just weird code.

### Blocks are Parentheses for expressions
- A block is itself in the “higher” precedence level.
- Can be used anywhere, like in the clauses of an if statement
- The statements it contains can be lower precedence 
- You can declare variables and other names inside the block
- The curlies let you leave the place where only some statements are allowed.
- So you can go back to the full statement grammar 

### Example 
- In parentheses we can override precedence 
- Similarly with curlies `{....}` Thats such a cute name hahaha curlies <3
- We can drop down into a place where all statements are allowed. 
- Remember they are higher level precedence statements.
- This means they can contain lower level precedence statements like declarations
- Thus by explicitly stating the scope of the varible using our cute curlies `{...}`
- We get ride of ambiguity and show the life time of the varible 
```
((1 +2) * 3) 
if(catgirl)
{
  kemonomimi = catgirl;
}
```

## Some New Grammar 
- Right now declaration is only variables
- We do get to add some cool functions and classes later on!
- any place where declaration is allowed.
- We are also allowed non-declaring statements. 
- This means it will produce statement by going down the grammar chain
```
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;

statement      → exprStmt
               | printStmt ;
```

### Decalring Varibles 
- `varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;`
- `var` is the key word token
- Then our identifier token!
- We could have an optional initializer expression
- We then put our wonderful semicolon

### A new primary expression
- To access this variable, we need a new type of primary expression
- IDENTIFIER matches one token which is the name of the variable being accessed.
```
primary        → "true" | "false" | "nil"
               | NUMBER | STRING
               | "(" expression ")"
               | IDENTIFIER ;
```

### Parsing Variables
- We need to adhere to our grammar rules
- So we will Keep parsing top-level declarations like `varDecl` and `statement` until `EOF`
- `while (!isAtEnd()) {statements.add(declaration()); }`
- Our `declaration()` method brings us to another intresting part of the code 
```
List<Stmt> parse()
 {
  List<Stmt> statements = new ArrayList<>();
  while (!isAtEnd())
  { 
    statements.add(declaration());
  }
  
  return statements; 
}
```
#### `declaration()` 
1. If our token is a VAR parse `varDecl`.
   - `if (match(VAR)) return varDeclaration();`
2. Else we return a `statement()`
   - `return statement();`
3. This is all in a try catch
4. If there is an error we `synchronize()`
5. then we return null
```
try {
  if (match(VAR)) return varDeclaration();  // try a declaration
  return statement();                        // try a statement
} catch (ParseError error) {
  synchronize();                             // skip to next likely good token
  return null;                               // return null for broken code
}
```

### `varDeclaration()`
- Begins parsing after matching the var keyword. 
- Confirms we have the correct identifier token using consume():
  - `Token name = consume(IDENTIFIER, "Expect variable name.");`
  - consume() ensures the current token is an IDENTIFIER. 
  - If it's not, a ParseError is thrown. 
- Design choice: if there’s no initializer, default it to null. 
  - `Expr initializer = null;`
- If the next token is of type `EQUAL`:
  - We consume it with match(EQUAL)
  - Then parse the initializer expression:
  - `if (match(EQUAL)) { initializer = expression(); }`
- We finish by requiring a ; at the end:
  - `consume(SEMICOLON, "Expect ';' after variable declaration.");`
- Wrap this up into a Stmt.Var AST node:
  - `return new Stmt.Var(name, initializer);`
- "All this gets wrapped in a Stmt.Var syntax tree node and we’re groovy." – Robert Nystrom 
- Groovy is such a fun word. I had to add this qoute from him
```
private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Var(name, initializer);
  }
```

primary()
- This method is the base case for expressions
- it handles literal values and identifiers. 
- If the current token is an IDENTIFIER, we:
  - Consume it (via match()), 
  - And return a variable expression:
  - `if (match(IDENTIFIER)) { return new Expr.Variable(previous());}`
- For Example lets use our wonderful **kemonomimi** IDENTIFIER:
  - Remember kemonomimi = "catgirl"
  - `print kemonomimi;`
  - "catgirl"
- We parses kinonomi as an identifier → becomes Expr.Variable.

# Environments
> The bindings that associate variables to values need to be stored somewhere. 
> Ever since the Lisp folks invented parentheses, this data structure has been called an environment.

## Key and The Map
- The Identifier Tokens has a lexem. This lexem is a string. This string is the name of our identifier
- That name will be the key to our map which contains the associated values.
```
class Environment 
{
  private final Map<String, Object> values = new HashMap<>();
}
```

### Defining variables
- We dont check if the key is already in the map. 
- This means we overwrite any values redefining the var.
- When in doubt about variable scooping do what Scheme does. 
```
void define(String name, Object value)
{
  values.put(name, value);
}
```

### Looking Up Variables
- `Environment.get(Token name)` checks if a variable exists in the current scope. 
- If found: return the value. 
- If not: throw a **RuntimeError** — Lox does **not** allow the use of undefined variables.
```
Object get(Token name)
 {
  if (values.containsKey(name.lexeme)) {return values.get(name.lexeme);}
  throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
}
```

### What Should Happen if a Variable Isn't Found?
> We have **three** semantic options:
1. **Syntax error (compile-time error)**: e.g., variable must be defined before use.
2. **Runtime error**: error thrown only when the value is actually accessed.
3. **Default value (e.g., `nil`)**: too permissive and allows accidental bugs to go unnoticed.

### **Lox chooses option 2**
- Throw a runtime error when you try to evaluate an undefined variable. 
- so why Not a Syntax Error?
- **Referencing** a variable is not the same as **evaluating** it. 
- Static error (like in C or Java) would prevent **recursive** or **mutually recursive** functions from working.
- Making variable reference a **static error** would **break** mutual recursion patterns
```lox
fun isOdd(n) 
{
  if (n == 0) return false;
  return isEven(n - 1); // Refers to isEven before it's defined
}
fun isEven(n) 
{
  if (n == 0) return true;
  return isOdd(n - 1);
}
```

### Language Design Trade-Off
- **Java/C#** solve this with "top-level declarations" — all functions are declared before any bodies are checked.
- **C/Pascal** use **forward declarations** (e.g., prototypes).
- **Lox** takes a **dynamic approach**: name references are fine __until actually evaluated at runtime__.

### Example: Why It Matters
- Variable exists only **after** declaration line runs. 
- Evaluation of the reference before that line triggers the error.
```lox
print a;           // Runtime error: undefined variable
var a = "Meow!";   // Too late
```

## Global Variables
- Our interpreter class gets a new instance of the encironment class
- `private Environment environment = new Environment();`
- This is so variables stay in memory as long as the interpreter is still running
- With two new AST Nodes we must define visitor methods for them.

### These two nodes reflect the two roles variables play:
- Stmt.Var = declaring a variable 
- Expr.Variable = accessing a variable’s value

### Stmt.Var
- We check if our statement has an initalizer `if (stmt.initializer != null)`
  - ```
  Var(Token name, Expr initializer)
  {
    this.name = name;
    this.initializer = initializer;
  }```
- If the initializer exist we then proced to evluate
  - `{value = evaluate(stmt.initializer);}`
- If we dont find a value
  - We define a new variable and initialize it with null
- For example 
> var a; 
> print a; // "nil".
```
@Override
public Void visitVarStmt(Stmt.Var stmt)
{
  Object value = null;
  if (stmt.initializer != null) {value = evaluate(stmt.initializer);}

  environment.define(stmt.name.lexeme, value);
  return null;
}
```

### Expr.Variable
- We forward the environment to ensure the variable is defined 
- We use the variable name as a key to obtain the value associated with the varible name from our enviorment
- MEOWTASTIC we know have basic variables!
> var sound1 = "meow";
> var sound2 = " purrrr";
> print sound1 + sound2;
> "meow purrrr"; 
```
@Override
public Object visitVariableExpr(Expr.Variable expr) 
{
  return environment.get(expr.name);
}
```

# Assignment
> 8.4 Lox is an imperative language

## Updated Grammar
- Remember that expression sits at the lowest precedence level in the expression hierarchcy
- It is the outermost wrapper.
- We must fall through to comparison, terms, and the rest
- We want the syntax tree to be organized so that `l-value` isn’t evaluated as an expression. 
- This is why Expr.Assign node has a Token for the left-hand side, not an Expr
```
expression → assignment
assignment → IDENTIFIER "=" assignment | equality
equality   → comparison ( ( "!=" | "==" ) comparison )*
comparison → term ...
```
### expression -> assignment ->  IDENTIFIER "=" assignment
- Assignment is right associated
- we can have a single assignment 
- `a = 6`
- We have in built recursion IDENTIFIER "=" assignment -> IDENTIFIER "=" IDENTIFIER "=" assignment
- `a = b = 9`

### expression -> assignment -> equality
- If we did not match the IDENTIFIER pattern
- Then we need to fall through to the rest of our grammar 
- If the parser doesn’t see IDENTIFIER =.
- It just continues parsing what looks like an equality.
- (like x == 3) or anything higher in precedence (like 3 + 2, !true, ("hi"), etc.). 
- Because equality → comparison → term → ... → primary.
- It includes all non-assignment expressions.
- Recursion, heckers yeah. 

### The new expression method
- Not anything to crazy on the surface we just return our assignment per the language grammar.
- We have a problem here, take a look at the statement
  - `kemonomimi = "fox girl";`
  - Our parser sees the identifier `kemonomimi`
  - We may think that this is an identifier because we lack the `var` keyword `var kemonomimi`
  - After `kemonomimi` we see the token `=` which means this is not an `identifier`, but an `assignment`
  - The parser already treated the left-hand side as an expression.
  - Now we realize it must be an *l-value*, something you assign an expression too. 
- So now we have two statements we have to deal with 
  - *l-value* `kemonomimi = "fox girl";`
  - *r-value* `print kemonomimi + makeSound`;
```
private Expr expression() 
{
  return assignment();
}
```

### L Value 
- Things you assign values too
- We do not evaluate the variable on the left hand side
- We dont care what it holds, just where to assign the value
- `a = 5;`
- `b = 2;`

### R Value
- Things that evaluate to values.
- `print a + b;`

### More Complicated Ideas
- kemonomimi.makesound = "MEOW"
- print kemonomimi.makesound(); 
- the entire thing looks like a regular expression
- A function call then the property accesses
- However once we hit the `=` then we can tell the difference
- The entire expression is not being *evaluated*, but assigned
- How can we solve this when you need to scan __n__ number of tokens ahead?
- The next token function will not satisfy this requirement.

### Concept Summary
| Concept                     | Meaning                                                                                                 |
| --------------------------- | ------------------------------------------------------------------------------------------------------- |
| **l-value**                 | Expression referring to a *location* you can assign into (e.g., `a`, `obj.field`)                       |
| **r-value**                 | Expression evaluating to a value (e.g., `"catgirl"`, `3 + 2`)                                           |
| **Assignment is special**   | Because the left-hand side must be an **l-value**, not just any expression                              |
| **Parsing issue**           | Parser can't know it's an assignment until it's too late — so we parse normally, then check and convert |
| **Why not more lookahead?** | Recursive descent parsers avoid lookahead beyond 1-2 tokens to keep things simple and efficient         |

### A Walk Through, Often the best teacher
> kemonomimi = "catgirl";
1. Parse the expression on the left handside
   - `Expr expr = equality();`
   - Looks at the next token: `kemonomimi`
   - Recognizes it's an identifier
   - At this point [equality()](#equality), expr holds the subtree for just kemonomimi.
   - Remember since we dont have BANG_EQUAL, EQUAL_EQUAL, we dont enter the while loop.
```
kemonomimi = "catgirl";
^
```
2. Checking for the `=` sign
   - if(match(EQUAL))
   - Current token is =, match(EQUAL) returns `true` and consumes it.
   - We move forward to "catgirl"
```
kemonomimi = "catgirl";
           ^
```
3. Capture the `=` token
   - `Token equals = previous();`
   - The `match(EQUAL)` just consumed the `=` so we must go back to the previous token
   - We need this just in case we have to handle error reporting
4. Parse the right hand section recursively
   - We recersively call the `Expr value = assignment()` creating a right associative operator
```
"catgirl";
^
```

5. check if left hand side is a valid assignment target
   - `if(expr instanceof Expr.variable)`
   - expr was built from `kemonommi` which is of type `expr.Variable` so this is true
6. Construct assignment node
   - `Token name = ( (Expr.Varaible) expr ).name;`
   - `return new Expr.Assign(name, value);`
   - We cast as explicit value to access name.
   - Converts the Left hand side `Expr.Variable` into an assignment node
```
Expr.Assign(
  name = "kemonomimi",
  value = Expr.Literal("catgirl")
)
```

### Recursion 
> a = b = "catgirl";
-  IDENTIFIER "=" assignment -> IDENTIFIER "=" IDENTIFIER "=" assignment
- We would then recursively build our statement
  - assignment → IDENTIFIER "=" assignment
  - IDENTIFIER "=" assignment → IDENTIFIER "=" assignment
  - IDENTIFIER "=" IDENTIFIER "=" assignment
  - *remember assignment -> will continue up the grammar hierarchy until we produce literal catgirl
```
Expr.Assign(
    "a",
        Expr.Assign(
            "b",
                Expr.Literal("catgirl")
)
```

### The Trick
- Parse the left-hand side as if it were just a normal expression (an r-value), 
- Then retroactively reinterpret it as an l-value only if you find an =.
- This is possible because simple assignment targets like kemonomimi, or even complex ones like kemonomimi.makesound(), are also valid expressions.
- Once we go to the equality production, we should not encounter an =
- Because when we are on the = token we should return all the way up to this rule expression → assignment ;

### The Full code 
```
  private Expr assignment()
{
  Expr expr = equality();
  
  if (match(EQUAL)) 
  {
    Token equals = previous();
    Expr value = assignment();
    
    if (expr instanceof Expr.Variable) 
    {
      Token name = ((Expr.Variable)expr).name;
      return new Expr.Assign(name, value);
    }
    
    error(equals, "Invalid assignment target."); 
  }
  
  return expr;
}
```

## Assignment semantics
- Take in a Expr.Assign
- Evaluate the expression that we just stored
- Instead of calling define()
- We call a nee method called asign
- psssst this meowtastic function is how we reassign values to variables using the environment.
```
  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    Object value = evaluate(expr.value);
    environment.assign(expr.name, value);
    return value;
  }
```

### The Assign method
- We check to see if the environment contains the key.
- This key is the variable name.
- if it does, we place the value well ummm as the value for that key.
- If we don't find it, we then throw an error.
- Assign() is not allowed to create a new variable
- only the define() method is

```
void assign(Token name, Object value)
{
    if (values.containsKey(name.lexeme))
    {
        values.put(name.lexeme, value);
        return;
    }
throw new RuntimeError(name,
    "Undefined variable '" + name.lexeme + "'.");
}
```

# Scope
> "A scope defines a region where a name maps to a certain entity. Multiple scopes enable the same name to refer to different things in different contexts." 
> "In my house, “Bob” usually refers to me. But maybe in your town you know a different Bob. Same name, but different dudes based on where you say it."

## Lexical Scope
> "When you see an expression that uses some variable, you can figure out which variable declaration it refers to just by statically reading the code.
- The text of the program itself shows where the scope begins and ends
- Variables are considered lexically scoped. 

###

















# Helper Functions 
### evaluate
```
   private Object evaluate(Expr expr)
    {
        return expr.accept(this);
    }
```
### Stmt.Print
```
public Void visitPrintStmt(Stmt.Print stmt)
{
    Object value = evaluate(stmt.expression);
    System.out.println(stringify(value));
    return null;
}
```
### Consume

```
private Token consume(TokenType type, String message)
{
    if(check (type)) return advance();
    throw error(peek(), message);
}
```

### execute 
- This calls the accept method on our expressions passing them the interpreter visitor
- Think of this as evluating the expressions and returning the result 
- Our parser builds the AST, our interpreter evaluates the AST
```
private void execute(Stmt stmt)
{
  stmt.accept(this);
}
```

### Equality

```
    private Expr equality()
    {
        // Remember we are climbing up our grammar tree going to the more important expressions
        // We call the comparison() method and store its results in a local varible, remember we are returning AST's after all
        Expr expr = comparison();
        while(match(BANG_EQUAL, EQUAL_EQUAL))
        {
            Token operator = previous();
            // This will increment our token btw as we descend down our grammar we will eventually find the rule that matches this token and consume it
            Expr right = comparison();
            // We wrap our expressions on the left hand side, tough stuff walk through it really understand it. this is the heart of recursive descent parsars
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
```