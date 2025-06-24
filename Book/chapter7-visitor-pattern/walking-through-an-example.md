# Walking Through An Example
---
## Step-by-Step Breakdown of Visitor Invocation
1. We begin by declaring an **abstract `accept()` method** in the base `Expr` class. Every subclass of `Expr` **must** implement this method.
2. Any class that implements the `Visitor<R>` interface is required to define all of its visit methods—one for each kind of expression we support (e.g., binary, literal, grouping, etc.).
3. To build our string printer, we create a class (`AstPrinter` or `RPNAstPrinter`) that **implements** the `Visitor<String>` interface. This class defines how each expression type is converted into a string.
4. When the `print(expr)` method is called, it triggers a recursive process. It starts by invoking the `accept()` method on the top-level expression.
5. Inside the `print()` method, we delegate the actual work to the visitor by calling `expr.accept(this)`:
```java
String print(Expr expr) {
    return expr.accept(this);  // 'this' refers to the current AstPrinter instance
}
```
6. The `accept()` method then calls the appropriate `visit` method for the specific expression type. This is made possible by **runtime polymorphism**—Java resolves the method based on the actual subclass of `Expr`.
7. For example, here's the `accept()` method implementation inside a `Binary` expression:
```java
@Override
<R> R accept(Visitor<R> visitor) {
    return visitor.visitBinaryExpr(this);
}
```
8. The corresponding visit method in the visitor handles the logic for converting this expression into a string. It typically calls a helper function like `parenthesize()`:
```java
@Override
public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
}
```
9. The `parenthesize()` function is the heart of the recursion. It processes sub-expressions by recursively calling `accept()` on them:
```java
private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();
    builder.append("(").append(name);

    for (Expr expr : exprs) {
        builder.append(" ");
        builder.append(expr.accept(this));  // Recursively visits sub-expressions
    }

    builder.append(")");
    return builder.toString();
}
```
10. The **recursive behavior** here is key: every time we encounter a sub-expression, we call its `accept()` method, which invokes the appropriate `visit` method and continues the cycle.
11. This recursion continues until all nested expressions have been processed, and the full expression tree is converted into a string.
12. It can be a bit of a journey to wrap your head around this pattern at first. If you're struggling to trace what's happening, that’s completely normal. Walk through the call stack step-by-step—it gets easier with practice.
13. Once you understand the flow of the **Visitor Pattern**, you’ll find that it’s not as complex as it first seems. In fact, it’s a clean and powerful way to separate behavior from data in a statically typed language like Java.

### Seeing The Calls in Action

```
print(BinaryExpr[*, UnaryExpr[-123], LiteralExpr[45.67]]);
```

This represents the expression: `(-123) * 45.67` and its step-by-step call flow
1. **Top-Level Call**
   `print(expression)` is called, where `expression` is a `Binary` object with:
    * `left`: `UnaryExpr[-123]`
    * `operator`: `*`
    * `right`: `LiteralExpr[45.67]`

2. **Inside `print()`**
   ```
   String print(Expr expr) 
   {
       return expr.accept(this);
   }
   ```
    * `expr` is a `Binary`, so the call becomes:
      `Binary.accept(this)`

3. **Inside `Binary.accept()`**
   ```
   return visitor.visitBinaryExpr(this);
   ```
    * The `AstPrinter`'s `visitBinaryExpr()` is invoked.

4. **Inside `visitBinaryExpr()`**
   ```
   return parenthesize("*", expr.left, expr.right);
   ```
    * `parenthesize("*", UnaryExpr[-123], LiteralExpr[45.67])` is called.

5. **Inside `parenthesize()` (First Call)**
    * Adds `"(*"` to the result.
    * Now needs to process the left expression: `UnaryExpr[-123]`
    * Calls `UnaryExpr.accept(this)`

6. **Inside `Unary.accept()`**
    * Calls: `visitUnaryExpr(this)`

7. **Inside `visitUnaryExpr()`**
   ```
   return parenthesize("-", expr.right);
   ```
    * Adds `"(-"` to the result.
    * Processes `LiteralExpr[123]` → calls `Literal.accept(this)`

8. **Inside `visitLiteralExpr()`**
    * Returns `"123"` as a string.
    * Finishes inner `parenthesize("- 123")`, returns `"(-123)"`

9. **Back to First `parenthesize()` Call**
    * Adds `"(-123)"` after `"(*"`
    * Now processes the right expression: `LiteralExpr[45.67]`
    * Calls `visitLiteralExpr()` → returns `"45.67"`

10. **Final Assembly**
    * Full string becomes: `"(* (-123) 45.67)"`
---
### 🧩 Key Takeaways

* Every `accept()` call delegates to the correct `visit` method based on the runtime type of the expression.
* Complex expressions (like `Binary`, `Unary`) **recursively** call `accept()` on their sub-expressions.
* Literal expressions serve as the **base case**—they don’t recurse further.
* The `parenthesize()` function assembles the final string by recursively evaluating and formatting each sub-expression.

### 📦 A Box Analogy

Think of the visitor pattern like **unpacking nested boxes**:
* The outer box is the `Binary` expression: it contains two smaller boxes.
* The left box is a `Unary` expression, which itself contains a `Literal`.
* The right box is another `Literal`.
* We open each box recursively, process its contents, and assemble the result in the correct order.

---
### AST a far mor simplified repersentation of our expression structure (Dont Worry We Will Get To That) 
```
print(BinaryExpr[*, UnaryExpr[-123], LiteralExpr[45.67]])
    → BinaryExpr.accept(AstPrinter)
     → visitBinaryExpr(BinaryExpr)
      → parenthesize("*", UnaryExpr[-123], LiteralExpr[45.67])
       → UnaryExpr[-123].accept(AstPrinter)
        → visitUnaryExpr(UnaryExpr)
         → parenthesize("-", LiteralExpr[123])
          → LiteralExpr[123].accept(AstPrinter)
           → visitLiteralExpr(LiteralExpr)
            → returns "123"
         → returns "(-123)"
     → LiteralExpr[45.67].accept(AstPrinter)
      → visitLiteralExpr(LiteralExpr)
       → returns "45.67"
    → returns "(* (-123) 45.67)"
```