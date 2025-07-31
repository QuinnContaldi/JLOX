# [Crafting Interpreters By Robert Nystrom](https://craftinginterpreters.com/)
---
## 🌟 Why This Book Matters to Me
*Crafting Interpreters* by Robert Nystrom has had a profound impact on my growth as a programmer. I was fortunate that my mentor recommended it to me — an act that truly changed my direction in life and deepened my appreciation for programming languages.
> At first, I was too overwhelmed with classes and projects to give the book the attention it deserved. But after completing my senior projects, I finally had a free summer, and I knew it was the perfect time to dive in.
> I was preparing to continue my work as a research assistant in academic programming languages and take a graduate-level compilers course in the fall. What better way to prepare than by learning from one of the most beloved books on the subject?
> I won’t lie I was nervous. Compilers have a reputation, especially among undergrads. At my university, the compilers course hadn’t been offered in six years until my mentor decided to revive it.
> Instead, most students (including myself) took **Automata and Formal Languages**, a class infamous for its difficulty. It was tough: 60% of the grade came from 10 in-person quizzes, and the remaining 40% was the final exam. Many of us were just hoping for a passing grade.
> But my friends and I welcomed the challenge we had already taken **Game Engine Architecture**, another one of the toughest classes offered. It was in that class that I met my mentor. So, we dove in headfirst, and to our surprise, we walked away with A’s and B’s.
> It was during *Automata* that I began to develop a deep respect for programming language theory. It was like finishing a marathon brutal, but incredibly rewarding. And I knew I wanted to keep running.
> Reading *Crafting Interpreters* felt like the next natural step. The book helped me understand not just the technical side of interpreters, but the elegance of language design.
> This repository documents my learning. It’s filled with notes, explanations, and code as I explore and implement my own interpreter based on Robert Nystrom's JLOX. It's also a way to share what I’ve learned with others who might be on a similar journey.
> However, this is more than just a study guide it’s a showcase of my dedication, curiosity, and commitment to growth as a programmer. If you're a recruiter visiting from my resume, I hope these notes give you a glimpse into the wonderful world of programming languages.
> I also hope this repository is like a glass of fresh water after all the calculator apps and ChatGPT wrapper projects you may have seen. Not that those projects are wrong we all start somewhere, but a little language from a great book is exciting.
> I really hope this repository inspires you to look deeper into the languages we use every day. Regardless, I’m proud of what I’ve built here. It’s been the highlight of my summer and one of the most rewarding programming experiences I’ve had so far.
---
## 💌 A Message to the Author
> To Robert Nystrom — thank you. Your work has inspired me deeply. I was so grateful for the free web version of the book that I bought a physical copy to support you. You’ve created something truly special, and I hope more developers find it and are inspired as I have been.
> If anyone reading this has enjoyed these notes or learned from them, I strongly encourage you to visit the author’s website:
👉 [https://craftinginterpreters.com](https://craftinginterpreters.com)
---
## Extra Resources That Are Also Used
1. \:green\_book: [Engineering a Compiler, 3rd Edition](https://www.amazon.com/Engineering-Compiler-Keith-D-Cooper-dp-0128154128/dp/0128154128/ref=dp_ob_title_bk)
    * A modern compiler construction textbook used in my graduate compilers course. It goes deeper into code generation, optimization, and runtime systems, helping bridge the gap between interpreters (as covered in *Crafting Interpreters*) and full compiler pipelines.
    * Topics include semantic elaboration, control flow translation, code shape, and register allocation — essential for understanding how high-level syntax becomes executable machine code.

2. \:blue\_book: [An Introduction to Formal Languages and Automata, 7th Edition](https://www.jblearning.com/catalog/productdetails/9781284231601?srsltid=AfmBOor_LG5CsmjrTsjRq3gkfZp7SCaGOGg8Zn_uVAMoXPEpZ-AiiiMr)
    * A foundational theory textbook that expands on the automata, grammars, and parsing theory briefly touched upon in *Crafting Interpreters*.
    * It covers topics like DFA/NFA, regular languages, context-free grammars, and parsing — especially useful for understanding the formal structures behind lexical analysis and syntax trees.

3. \:orange\_book: [Programming Language Pragmatics, 5th Edition](https://mlscott14627.github.io/PLP5e_online/)
    * A comprehensive book on programming language design and implementation. It explores how different language features are expressed and executed, reinforcing the connection between theory and practice.
    * Useful for understanding semantics, type systems, concurrency, runtime memory models, and design tradeoffs. Shows how other real-world languages handle similar constructs.
---

# JLOX - Lox Interpreter in Java

An implementation of the Lox programming language from the book *Crafting Interpreters* by Bob Nystrom. This project includes:
- A full Lox interpreter (`Lox.java`) Currently on chapter 9 
- An AST (Abstract Syntax Tree) printer (`AstTreePrinter.java`) for visualizing syntax trees

## 📦 Requirements

- **Java Development Kit (JDK) 17+**
- **Git** (for cloning the repository)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/QuinnContaldi/JLOX.git
cd JLOX
```
#### 2. Compile the Source Code

From the root directory, compile the source files:
```
javac src/com/craftinginterpreters/lox/*.java
```
#### 3. Running the Interpreter

Run the interpreter with a sample Lox file:
```bash
java -cp src com.craftinginterpreters.lox.Lox src/com/craftinginterpreters/lox/LoxProgram.txt
```
This will execute the contents of LoxProgram.txt, which contains a basic Lox script.
👉 View LoxProgram.txt

#### 4. Viewing the AST

You can also visualize the syntax tree of a sample Lox expression:

java -cp src com.craftinginterpreters.lox.AstTreePrinter

No input file is required; simply type your JLox code directly in AstTreePrinter.java.



