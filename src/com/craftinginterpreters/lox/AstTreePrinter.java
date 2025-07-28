package com.craftinginterpreters.lox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AstTreePrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    public void print(List<Stmt> statements)
    {
        int counter = 0;
        for(Stmt stmt: statements)
        {
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

    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("BlockStmt\n");
        int size = stmt.statements.size();

        for (int i = 0; i < size; i++) {
            Stmt statement = stmt.statements.get(i);
            boolean last = (i == size - 1);

            // Choose branch prefix for current statement
            String branch = last ? "└── " : "├── ";
            // Prefix for nested lines of the child
            String childPrefix = last ? "    " : "│   ";

            // Get child's string representation
            String childString = statement.accept(this);

            // Indent all lines of childString except the first
            childString = indentChildLines(childString, childPrefix);

            // Append branch + first line + indented rest
            builder.append(branch).append(childString).append("\n");
        }

        return builder.toString();
    }

    // Helper function to indent all but first line with prefix
    private String indentChildLines(String str, String prefix)
    {
        String[] lines = str.split("\n", -1);
        if (lines.length <= 1) return str; // nothing to indent

        StringBuilder sb = new StringBuilder(lines[0]).append("\n");
        for (int i = 1; i < lines.length; i++) {
            sb.append(prefix).append(lines[i]);
            if (i < lines.length - 1) sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt)
    {
        return treeBuilder("ExpressionStmt", stmt.expression);
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt)
    {
        return treeBuilder("PrintStmt", stmt.expression);
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt)
    {
        return treeBuilder("VarStmt", stmt.initializer);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr)
    {
        return treeBuilder("AssignExpr", expr.name, expr.value);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr)
    {
        return treeBuilder("BinaryExpr", expr.left, expr.operator, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr)
    {
        return treeBuilder("GroupingExpr", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr)
    {
        return treeBuilder("LiteralExpr", expr.value);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr)
    {
        return treeBuilder("UnaryExpr ", expr.operator, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr)
    {
        return treeBuilder("VariableExpr", expr.name);
    }

// Helper funcitons

    // Children: an array of objects which count be statements or Expr or Tokens or other values
    // prefix a string that holds the current indentation and vertical lines | to visually repersent the tree
    // isLast is a boolean that indicates whether the current node is the last child of its parent
    private String buildTree(Object[] children, String prefix, boolean isLast)
    {
        // You already know, this is to efficiently build out string
        StringBuilder builder = new StringBuilder();

        // Stores the number of children
        int len = children.length;

        // Loop over each child object to process them one by one
        for (int i = 0; i < len; i++)
        {
            // Get the current child object at the index i
            Object obj = children[i];
            // We are simply checking if its the last child, since we started at zero we decrease count by one
            boolean lastChild = (i == len - 1);
            // Branch stores the prefix string for this node’s line in the tree:
                // If lastChild is true, use └── (corner branch) to indicate end of siblings.
                // Otherwise, use ├── (tee branch) to indicate more siblings follow.
            String branch = lastChild ? "└── " : "├── ";
            // ChildPrefix is the prefix to use for child nodes of the current node:
                // If this node is the last child, child lines get " " (spaces) — no vertical continuation line.
                // Otherwise, child lines get "│ " — vertical bar to continue the tree visually downwards.
            String childPrefix = lastChild ? "    " : "│   ";

            if (obj instanceof Stmt) {
                builder.append(prefix) // Adds the current indentation and vertical bars from above levels.
                        .append(branch)// Adds the branch symbol (├── or └──) to this line.
                        .append(((Stmt) obj)
                                .accept(this) // Calls the accept method on the statement with the current visitor (this), which returns a string representing the subtree rooted at this statement.
                                .replaceAll("\n", "\n" + prefix + childPrefix));
                                // The subtree string may contain newlines (because it’s multi-line).
                                // For each newline, this replaces it with a newline + the prefix for this node + the child prefix, so the vertical bars and spacing line up properly on the following lines.
            } else if (obj instanceof Expr) {
                builder.append(prefix).append(branch).append(((Expr) obj).accept(this).replaceAll("\n", "\n" + prefix + childPrefix));
            } else if (obj instanceof Token) {
                builder.append(prefix).append(branch).append("Token: ").append(((Token) obj).lexeme).append("\n");
            } else {
                builder.append(prefix).append(branch).append(stringify(obj)).append("\n");
            }
        }

        return builder.toString();
    }

    // A helper method to start the tree printing for a root node named name, with variable number of children.
    public String treeBuilder(String name, Object... children)
    {
        // Creates a new StringBuilder for the whole tree string.
        StringBuilder result = new StringBuilder();
        // Adds the root node label and a newline.
        result.append(name).append("\n");
        // Calls buildTree for the children with:
            // Empty prefix "" (start with no indentation),
            // True for isLast because the root node is considered last at the top level.
        result.append(buildTree(children, "", true));
        return result.toString();
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

            new AstTreePrinter().print(statements);
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }

    }
}