package com.craftinginterpreters.lox;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String>
{
    private int level = 0;
    public void print(List<Stmt> statements)
    {
        int counter = 0;
        for(Stmt stmt: statements)
        {
            level = 0; // Reset for each top-level statement
            System.out.println("Statement: " + counter);
            System.out.println(stmt.accept(this));
            counter++;
        }
    }

    public String formatStmt(Stmt stmt)
    {
         return stmt.accept(this);
    }

    public String formatExpr(Expr expr)
    {
        return expr.accept(this);
    }

    // Hey it really is like a mini program
    @Override
    public String visitBlockStmt(Stmt.Block stmt)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BlockStmt\n{\n");
        for(Stmt statement: stmt.statements)
        {
            builder.append(statement.accept(this));
            builder.append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt)
    {
         return builder("ExpressionStmt", stmt.expression);
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt)
    {
        return builder("PrintStmt", stmt.expression);
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt)
    {
        return builder("VarStmt", stmt.initializer);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr)
    {
        return builder("AssignExpr", expr.name, expr.value);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr)
    {
        return builder("BinaryExpr", expr.left, expr.operator, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr)
    {
        return builder("GroupingExpr", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr)
    {
        return builder("LiteralExpr", expr.value);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr)
    {
        return builder("UnaryExpr ", expr.operator, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr)
    {
        return builder("VariableExpr", expr.name);
    }

// Helper funcitons

    public String builder(String name, Object... objects)
    {
        // Create a new StringBuilder object to efficiently build the resulting string
        StringBuilder builder = new StringBuilder();

        builder.append(indent(level)).append(name).append("\n");
        builder.append(indent(level)).append("(\n");

        level++; // Go one level deeper for the children inside our node
        for(Object object : objects)
        {
            // Use the same indent level for all children inside our node
            if(object instanceof Stmt)
            {
                builder.append(formatStmt( (Stmt) object)).append("\n"); // Already handles the indentation no need to call indent again
            }
            else if(object instanceof Expr)
            {
                builder.append(formatExpr( (Expr) object)).append("\n"); // Already handles the indentation no need to call indent again
            }
            else if(object instanceof Token)
            {
                builder.append(indent(level)).append("Token: ").append(((Token) object).lexeme).append("\n");
            }
            else
            {
                builder.append(indent(level)).append(object.toString()).append("\n");
            }
        }
        level--; // Decrease indent to close node

        builder.append(indent(level)).append(")");
        return builder.toString();
    }

    // This is our helper function for recursive printing
    private String indent(int level)
    {
        // Each level deeper will cause the tab button to showcase our structure
        return "\t".repeat(level);
    }


    private String stringify(Object object)
    {
        if(object == null) return "nil";

        if(object instanceof Double)
        {
            String text = object.toString();
            if(text.endsWith(".0"))
            {
                text = text.substring(0,text.length()- 2);
            }
            return text;
        }
        return object.toString();
    }

    public static void main(String[] args)
    {
        String path = "src/com/craftinginterpreters/lox/LoxASTPrinter.txt";
        try {
            // We pass in our source file
            String source = new String(Files.readAllBytes(Paths.get(path)));
            // Our scanner is created with said source file
            Scanner scanner = new Scanner(source);
            // We do some LEXING! MUHAHAHAHA and create our list of tokens
            List<Token> tokens = scanner.scanTokens();

            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();

            new AstPrinter().print(statements);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }

//        List<Stmt> statements = new ArrayList<>();
//        Stmt stmt1 = new Stmt.Var
//                (
//                    new Token(TokenType.IDENTIFIER, "Animal Girl", null, 1),
//                    new Expr.Literal("Catgirl")
//                );
//        statements.add(stmt1);
//        new AstPrinter().print(statements);
    }
}