package com.craftinginterpreters.lox;

class AstPrinter implements Expr.Visitor<String>
{
    String print(Expr expr)
    {
        return expr.accept(this);
    }

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

// This method takes a name string and variable number of Expr objects as parameters
// The varargs syntax (Expr...) allows passing multiple Expr objects as separate arguments
private String parenthesize(String name, Expr... exprs)
{
    // Create a new StringBuilder object to efficiently build the resulting string
    StringBuilder builder = new StringBuilder();

    // Add an opening parenthesis and the name parameter to the builder
    // Example: if name is "+" then builder now contains "(+"
    builder.append("(").append(name);

    // Iterate through each expression in the varargs parameter
    for (Expr expr : exprs)
    {
        // Add a space before each expression for formatting
        builder.append(" ");
        
        // Call accept(this) on the expression and append its result
        // This uses the Visitor pattern to convert the expression to a string
        builder.append(expr.accept(this));
    }
    
    // Add the closing parenthesis to complete the expression
    builder.append(")");

    // Convert the StringBuilder to a String and return it
    return builder.toString();
}

    public static void main(String[] args)
    {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));

        System.out.println(new AstPrinter().print(expression));
    }
}