# The Visitor Pattern
> "Visitor is a behavioral design pattern that lets you separate algorithms from the objects on which they operate" - [The Visitory Pattern By Refactoring.Guru ](https://refactoring.guru/design-patterns/visitor)
> Another great resource provided by the author on the [Interpreter Pattern](https://en.wikipedia.org/wiki/Interpreter_pattern).
> If you are more of a video person here is a [Interpreter Pattern Video](https://www.youtube.com/watch?v=hmX2s3pe_qk).
---

## The Problem: How Do We Interpret Expressions?
A good starting point in understanding the interpreter design is to ask: why use the Visitor pattern at all? As we begin implementing our interpreter, we quickly encounter a key design challenge.
Each kind of expression such as literals, binary operations, and groupings must behave differently when evaluated.
That’s the core reason we tokenize: to assign meaning to different lexemes and handle them accordingly.
From an object-oriented programming perspective, one approach could be to define an abstract interpret() method in the base class and override it in each subclass.

So, how do we write code in a functional style within an object-oriented language like Java?
This is exactly where the Visitor Pattern comes into play.
It gives us the flexibility of FP (grouping by function) while staying within the structure of OOP.
If you’re interested in seeing this comparison in action, check out this excellent video: [FP VS OOP](https://www.youtube.com/watch?v=08CWw_VD45w).
Welcome to one of the most important patterns in our interpreter design — the Visitor Pattern.

In this chapter, you'll learn:
- [Object-Oriented Programming Approach](object-oriented-programming-approach.md)
- [Functional Programming Approach](functional-programming-approach.md)
- [Understanding the Visitor Pattern](the-visitor-pattern.md)
- [Walking Through an Example](walking-through-an-example.md)
