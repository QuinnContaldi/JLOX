package com.craftinginterpreters.lox;

import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

// Remember you walk the grammar rules (parse tree shape) but build AST nodes along the way.
/*
expression     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary
               | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil"
| Grammar Notation | Code Representation               |
|------------------|-----------------------------------|
| Terminal         | Code to match and consume a token |
| Nonterminal      | Calls to that rule's function     |
| \|               | if or switch statement            |
| * or +           | while or for loop                 |
| ?                | if statment                       |
 */
class Parser
{
    private static class ParseError extends RuntimeException{}
    // We read in a sequence of tokens instead of a sequence of characters
    private final List<Token> tokens;
    // Keeps track of the current token we are at
    private int current = 0;
    // Used to assign the list of tokens to parse through
    Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    Expr parse()
    {
        try{ return expression();}
        catch (ParseError error){ return null;}
    }
    // Translating grammar rules into lovely Java code
    // Expression     → equality; a pretty straight forward translation
    private Expr expression()
    {
        return equality();
    }

    // equality       → comparison ( ( "!=" | "==" ) comparison )* ; Is more complex
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

    private Expr comparison()
    {
        // Going down THE GRAMMAR TREEEEEEEEEEEEEEEEEEEEEE
        Expr expr = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL))
        {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term()
    {
        // Yeah you can see the pattern here, we descend once more
        Expr expr = factor();

        while(match(MINUS, PLUS))
        {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor()
    {
        // Yeah you can see the pattern here, we descend once more
        Expr expr = unary();

        while(match(SLASH, STAR))
        {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary()
    {
        // This is the | statement in our grammar. Do we have more imbeded statements or is it just a primary
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary()
    {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if(match(NUMBER, STRING)) { return new Expr.Literal(previous().literal); }

        if(match(LEFT_PAREN))
        {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }
    /*
    The (...) loop works in tandem with the while loop. We can pass in as many tokens as we need
    Remember * is a while loop? zero or more? nyatastic so we keep looping until we find a != or ==
    If we dont see any of those operators then we are done with our sequence of equality opperators
    * */
    private boolean match(TokenType... types)
    {
        for(TokenType type : types)
        {
            if (check(type))
            {
                // Just checking to see if the token is of the correct type
                advance();
                return true;
            }
        }
        // Do not consume the token since its not the right type. Just leave it alone dawg
        return false;
    }

    private Token consume(TokenType type, String message)
    {
        if(check (type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(TokenType type)
    {
        // If we are at the end always return false since there is not symbol to return.
        if(isAtEnd()) return false;
        // We return a boolean value based on a token type check without consuming the symbol.
        return peek().type == type;
    }

    private Token advance()
    {
        // If we are not at the end of the file then you are allowed to advance
        if(!isAtEnd()) current++;
        // This method moves the cursor forward, but returns the token that was just consumed, not the one you're now pointing at.
        return previous();
    }

    private boolean isAtEnd()
    {
        return peek().type == EOF;
    }

    private Token peek()
    {
        return tokens.get(current);
    }

    private Token previous()
    {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message)
    {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }

}