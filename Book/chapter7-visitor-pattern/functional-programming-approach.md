# Functional Programming Approach
---
![Functional Programming Table](../src/com/craftinginterpreters/Images/table.png)
Functional Programming (FP) is a paradigm that emphasizes immutability, pure functions, and organizing code around behavior (functions) rather than data (objects).
In contrast to Object-Oriented Programming (OOP), where behavior is encapsulated within objects, FP prefers separating data from behavior and operating on that data through standalone functions.
In the context of building an interpreter, this difference becomes especially clear.

Traditionally, with OOP, we define expression classes like `Binary`, `Literal`, and `Grouping`, and embed behavior such as `interpret()`, `print()`, or `serialize()` directly inside these classes.
This is known as **grouping by type**. Each expression knows how to interpret, print, and maybe even serialize itself.
However, in FP, we **group by functionality**. Instead of scattering behavior across many types, we centralize it.

You might define an `Interpreter` class that handles evaluation, an `AstPrinter` class that handles printing, and so on.
Each of these classes contains logic that pattern-matches on the type of expression it's operating on and handles it accordingly.
The expression classes themselves remain as plain data—no behavior attached.

This aligns closely with the **visitor pattern** in OOP.
The visitor pattern is often described as a way to “fake” functional programming within an object-oriented environment.
It allows us to group behavior (e.g., evaluation or printing) outside the data classes while still maintaining type safety and extensibility.

## Grouping by Type (OOP Style)
This structure places all behaviors inside the expression classes themselves.
Grouping by Type is further explored below this and shows the comparison against FP.
Each type is self-contained, but adding a new behavior (e.g., type-checking) means touching every subclass:

```text
Expr
 ├── Binary
 │    ├── interpret()
 │    ├── print()
 │    └── serialize()
 ├── Literal
 │    ├── interpret()
 │    ├── print()
 │    └── serialize()
```

## Grouping by Function (FP Style)
This layout reflects an **FP-style architecture** where logic is modular, and adding new behaviors is as easy as writing a new function.
You're trading easier *vertical* extensibility (adding new behaviors) for more difficult *horizontal* extensibility (adding new types).
Here, behavior is grouped separately from data. The expressions are simple data structures, and logic is handled elsewhere:

```text
Binary
Literal
Grouping
...

Interpreter
 └── interpret(Binary)
 └── interpret(Literal)
AstPrinter
 └── print(Binary)
 └── print(Literal)
Serializer
 └── serialize(Binary)
 └── serialize(Literal)
```

### Before – Grouped by Type (Rows)
![Classes By Rows](../src/com/craftinginterpreters/Images/rows.png)

Each row (type) owns all the operations that can be done on it.
This is great when you're constantly adding new expression types, but makes it harder to add new behaviors.

### After – Grouped by Function (Columns)

![Classes By Columns](../src/com/craftinginterpreters/Images/columns.png)

Each column (operation) owns all the logic for that operation across all expression types.
This makes it easy to extend the interpreter with new behaviors without touching the expression classes themselves.
This column-style grouping is a core idea in functional design—and the **Visitor Pattern** lets us approximate it within object-oriented languages like Java.

### Programming Functionally in an OOP Language
You might wonder: *How do we write code in a functional way using an object-oriented language like Java?*
This is exactly where the **Visitor Pattern** shines.
It lets you write code that behaves like FP—grouping logic by behavior—while still conforming to Java’s OOP structure.
It's not *true* functional programming, but it's a powerful hybrid that brings many of FP’s benefits into your interpreter's architecture.
---