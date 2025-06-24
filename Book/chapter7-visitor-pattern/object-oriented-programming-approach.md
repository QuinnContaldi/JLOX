# Object-Oriented Programming (OOP) Approach
---
Using this approach risks violating the S.O.L.I.D. principles of software design.
In particular, it compromises the Open/Closed Principle, as adding new behaviors would require modifying every subclass.
To stay aligned with S.O.L.I.D. and keep our code extensible and maintainable, we instead delegate the behavior to a separate visitor object.
Below is a brief review of the S.O.L.I.D. principles and how they relate to our interpreter's design.

## The SOLID Principles (Quick Overview)
- Single Responsibility Principle
    - Each class should have one reason to change. Expression classes should only represent syntax not also include evaluation behavior.
- Open/Closed Principle
    - Classes should be open for extension but closed for modification. You should be able to add new behavior (like pretty-printing) without editing every expression class.
- Liskov Substitution Principle
    - Subtypes must be substitutable for their base types. If we rely on Expr objects, we should be able to use any subclass like Binary, Literal, or Unary without breaking anything.
- Interface Segregation Principle
    - Classes shouldn’t be forced to implement interfaces they don’t use. We keep our Visitor interface small and focused — each visitor class handles exactly what it needs to.
- Dependency Inversion Principle
    - High-level modules (like the interpreter) should depend on abstractions, not concrete classes. This allows us to plug in different visitors (like printers or type checkers) without changing the expression classes.

### Look At The Single Responsibility Principle (SRP)
The Single Responsibility Principle states that a class should have only one reason to change it should focus on a single responsibility and do that one thing well.
When we start cramming multiple behaviors and responsibilities into a single class, things can quickly spiral out of control.
You’ve probably seen this in practice: a massive, monolithic class bloated with responsibilities from different parts of the system.
Over time, it becomes tightly coupled to everything, and maintaining it feels like performing surgery with duct tape.
Every new feature becomes a hack, and the codebase grows increasingly fragile.
A classic example of this mistake is when we declare abstract behavior like interpret() in a base class such as Expr, and then force every subclass to implement it.
At first glance, this might seem convenient, but it violates SRP by mixing concerns.
Each expression class ends up handling not just its structure but also its behavior, making the system harder to manage and extend.
```java
  abstract class Expr
{
    abstract String print();
    abstract Object interpret();
    abstract String toJson();
   static class Binary extends Expr
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
        Interpret()
        {
            // Binary specific behavior 
        }
    }
}
```
#### Bad Example (violates SRP):
So lets use this BinaryExpr class as an example.
You can see that we have to define these methods behaviors not only for the BinaryExpr class but we would have to do that for all other Expr classes
So that would be four currently `Binary`, `Unary`, `Grouping`, `Literal`, Now thats 4 classes and 3 abstract methods we would have to define.
What about when we make a Function Call Expr class.
Now we would have to define all 3 methods for that class as well, and then if we want to impliment a new method say ASTPrint() we would have to define that method in all 5 Expr classes, Yikes.
 ```
class BinaryExpr
{
Token operator;
Expr left;
Expr right;

    String print()
    {
        // We do the printing
    }
    Object interpret()
    {
        // We do the interpreting
    }
    String toJson()
    {
        // We do the ToJson
    }
}
```

#### A Pretty Good Example:
In this design, responsibilities are cleanly separated across different components. In action—each class has a focused role.:
- BinaryExpr holds the structure of the expression.
- AstPrinter handles printing.
- Interpreter handles interpretation.

### Open/Closed Principle (OCP)
The Open/Closed Principle states that software should be open for extension but closed for modification.
In other words, we should be able to add new functionality without altering existing code.
Now imagine you want to add a new behavior—like type checking.
Without a proper design, you'd have to modify every subclass of Expr to add a new typeCheck() method.
This quickly becomes error-prone and fragile, especially as your codebase grows.

Every change increases the risk of introducing bugs in otherwise stable code.
This is where the Visitor Pattern becomes valuable.
By centralizing behaviors like interpretation, printing, and type-checking into visitor classes, you can extend functionality simply by creating a new visitor—without touching your existing Expr subclasses.

#### Good Example (with Visitor):
```java 
// You define a new visitor:

class TypeChecker implements Expr.Visitor<Type> 
{
    Type visitBinaryExpr(Binary expr) 
    { 
        // Some method definition 
    }
    Type visitLiteralExpr(Literal expr) 
    {
        // Some method definition 
    }
}
```
### Liskov Substitution Principle (LSP)
Objects of a superclass should be replaceable with objects of a subclass without breaking the program.
In short: Subclasses should behave like their parents  no surprises.
For example If a subclass of Expr throws an exception when accept() is called, but the base class expects it to return something, your program breaks.
That really means all subclasses of Expr correctly implement accept(visitor) and return results as expected.
I cant really think of a programming example for this one.
I guess I could show you a debug statement or error log for incompatable types

### Interface Segregation Principle (ISP)
Clients shouldn’t be forced to depend on methods they don’t use.
This is about making interfaces small and focused.
In our bad example we would combind all of our visitor methods into one monolithic visitor.
A MEGA VISITOR.
Again the bueaty of the visitor pattern is it allows us to program in a functional paradigm, which we will get to after this section.
As a result it is very easy to create classes around a single function.
Then we can create many different types of visitors to handle the types of functions that would be abstract methods in our Expr classes.
This sounds weird I know, it took me a while to understand it as well.
Stick with it, I promise it will eventually make sense.

#### Bad Example:
```
// Now all expression classes must implement methods they don’t care about.
interface GiantVisitor 
{
    void visitBinaryExpr(Binary expr);
    void visitLiteralExpr(Literal expr);
    void visitAstAsJson(Ast expr);
    void visitInterpreterState(Interpreter interpreter);
}
```
#### Good Example:
```
// Keep your Visitor interface focused:
interface Visitor<R> 
{
    R visitBinaryExpr(Binary expr);
    R visitLiteralExpr(Literal expr);
}
```

### Dependency Inversion Principle (DIP)
The Dependency Inversion Principle is one of my personal favorites.
It emphasizes a powerful idea: depend on abstractions, not concretions.
High-level modules like your interpreter should not be tightly coupled to low-level implementation details.
Instead of directly depending on specific classes, we define interfaces or abstract classes that describe the required behavior.

The high-level code relies on these abstractions, and the concrete implementations plug into them.
This decouples the system, making it more flexible, testable, and easier to maintain.
If you have some time to explore further, I highly recommend looking into the Strategy Design Pattern.
It’s a great practical example of this principle in action.
#### Bad Example:

```
class Interpreter 
{
    // Hardcoded and pretty whack 
    AstPrinter printer = new AstPrinter();
}
```

#### Good Example:
```
// Now Interpreter depends on an interface, not a specific implementation — which makes your code more modular and testable.
class Interpreter 
{
    Visitor<String> printer;

    Interpreter(Visitor<String> printer)
    {
        this.printer = printer;
    }
}
```
### Some Last Notes
That wraps up our review of the S.O.L.I.D. principles.
But before we move on, it’s important to clarify something: these principles are guidelines—not hard rules that define what good code must look like.
Rigidly applying them without considering context can be just as harmful as ignoring them entirely.

As developers, especially when we’re still learning, we often swing between extremes.
At first, we may disregard S.O.L.I.D. as idealistic or impractical.
Then, once we see the benefits, we might go too far and treat them as law.
True mastery, however, lies in understanding "the why" behind these principles—and knowing when it's appropriate to follow them, and when it's okay to bend or even break them for the greater good of your codebase.

A good starting habit is to scan your code for S.O.L.I.D. violations and reflect on whether refactoring would bring meaningful benefits.
Ask yourself: Is the current design holding me back? Would applying a principle help long-term maintainability?
These aren’t always easy questions, but they’re part of becoming a thoughtful developer.
I know this isn’t groundbreaking wisdom—but hey, I’m still a junior dev, so cut me some slack.
--- 