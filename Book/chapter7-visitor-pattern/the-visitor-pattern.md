# The Visitor Pattern
---
## Defining The Interface
The Visitor interface establishes a contract: any implementing class must provide implementations for all four visit methods.
- We use a generic type parameter `R` to allow flexible return types each visitor implementation can specify its own return type while maintaining the same structure.

### Defining Interface Methods
- Each `Visitor` must implement a `visitBinaryExpr(this)` method as part of the visitor pattern contract
- This design enables multiple visitor implementations to interact with our expression classes. When an expression invokes `visitBinaryExpr(this)`, it calls the specific implementation defined in the current visitor
- The visitor pattern allows for diverse behaviors while keeping the expression classes unchanged. Different visitors (like `AstPrinter` or `Interpreter`) can provide their own unique implementations
- For example, `visitBinaryExpr(this)` in the `AstPrinter` visitor will handle the expression differently than the same method in the `Interpreter` visitor
```interface Visitor<R> 
{
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
}
```
### Defining The Visitor
- When implementing the Visitor interface in classes like `AstPrinter`, we must provide concrete implementations for all four visitor methods
- The return type can be customized based on the visitor's purpose
- while one visitor might return strings for printing, another could return numerical values for evaluation
```   
class AstPrinter implements Expr.Visitor<String>
{
   @Override
    public String visitBinaryExpr(Expr.Binary expr)
    {
        return parenthesize(expr.operator.lexeme,
                expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr)
    {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr)
    {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr)
    {
        return parenthesize(expr.operator.lexeme, expr.right);
    }
}
```

### The abstract `accept()`
```abstract <R> T accept(Visitor<R> visitor);```
- The abstract `accept()` method serves as a contract that all expression subclasses must implement
- This method utilizes generic typing with type parameter `R` for flexible return types across different visitor implementations
- The method signature takes a single parameter:
    - A visitor object of type `Visitor<R>`
    - The generic parameter `R` is shared between the method and visitor interface
    - This enables type-safe visitor pattern implementations

### Implementing accept() in a subclass
- The Binary class implements accept() by calling visitor.visitBinaryExpr(this).
- This dispatches the call to the correct method on the visitor, passing the current instance (this).
- Actual behavior is defined in the visitor implementation, not in the expression class.
- Promotes separation of concerns: expressions hold data, visitors define behavior.
```
class Binary extends Expr 
{
final Expr left;
final Token operator;
final Expr right;

    Binary(Expr left, Token operator, Expr right)
     {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    <T> T accept(Visitor<T> visitor) 
    {
        return visitor.visitBinaryExpr(this);
    }
}
```

### Invoking the Visitor
- Let’s look at how we invoke the visitor using the `RPNAstPrinter` class.
- First, we create a `Binary` expression that contains other sub-expressions.
- The line `System.out.println(new RPNAstPrinter().print(expression));` is the most important one to focus on.
- Let’s step into the code to understand what happens inside the `RPNAstPrinter.print(expression)` method.
- Take note: we're passing an `Expr` object as the argument to the `print()` method.
- We may know it’s actually an `Expr.Binary` at runtime, but the method only cares that the argument is of type `Expr`.
```java
public static void main(String[] args)
{
    Expr expression = new Expr.Binary(
        new Expr.Binary(
            new Expr.Literal(1),
            new Token(TokenType.PLUS, "+", null, 1),
            new Expr.Literal(2)
        ),
        new Token(TokenType.STAR, "*", null, 1),
        new Expr.Binary(
            new Expr.Literal(4),
            new Token(TokenType.MINUS, "-", null, 1),
            new Expr.Literal(3)
        )
    );
    System.out.println(new RPNAstPrinter().print(expression));
}
```

### Pattern Matching via Visitor
- Notice that the `print()` method takes an `Expr` parameter named `expr`.
- This is where the **Dependency Inversion Principle** really shines.
- We don’t need to know the concrete type of `expr`.
- All we care about is that it’s some subclass of `Expr`.
- Why? Because any subclass of `Expr` must implement the `accept()` method.
- At runtime, the actual type of `expr` is resolved.
- So when we call `print(expression)` with an instance of `Expr.Binary`, that object handles the responsibility of calling its own `accept()` method.
- That `accept()` method then calls the appropriate `visit` method on the visitor (`RPNAstPrinter`), enabling double dispatch and behavior resolution based on the actual expression type.
- The power of the visitor pattern lies in this dynamic dispatch mechanism.
- We're programming against an interface (`Expr`), not a specific implementation, which allows flexible and extensible design.
```java
String print(Expr expr) 
{
    return expr.accept(this);
}
```
---