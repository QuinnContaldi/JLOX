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



statement      → exprStmt
| ifStmt
| printStmt
| block ;

ifStmt         → "if" "(" expression ")" statement
( "else" statement )? ;