package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox
{
    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1)
        {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }
    // private static method allows uss to call the Lox class with out it being instanciated throw IO means we may get an error
    // string path takes a file path as input where Lox source code is located
    private static void runFile(String path) throws IOException
    {
        // Paths.get(Path) creates a path "object" which repersents the files location
        // Files.readAllBytes(Path) Reads the whole file into memory as an array of bytes
        // We do this on order to read the entire file into memory as an array of bytes
        // this happens all are once rather then streaming it. This is more efficient.
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        // We now have to convert the bytes to a string, we have to make sense of it some how.
        // Make sure to use the systems default character encoding.This typically is UTF-8.
        // This string now contains the entire source file
        // We now pass the source code string to our run method
        // The run method creates a scanner to do LEXING ahheeeem tokenize the source code
        // we then convert the source into tokens and print the tokens
        run(new String(bytes, Charset.defaultCharset()));
    }

    // This is our cute little interactive prompt
    private static void runPrompt() throws IOException
    {
        // We create a buffered reader to read from the console
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        // We loop forever until we want stop the terminal which means we hit enter on the console
        for (;;)
        {
            // Cute little syntax sugar
            System.out.print("> ");
            // We read a line from our dear user
            String line = reader.readLine();
            // We check if the line is null, if it is we break out of the loop
            if (line == null) break;
            // This is really just a loose wrapper around the run method
            run(line);
            // Reset the error flag so we can run the prompt more then once
            hadError = false;
        }
    }

    // This is the main man or well main wizard? That does the actual LEXING and PARSING
    private static void run(String source)
    {
        // If we have an error we should exit the program
        if(hadError) System.exit(65);
        // We create a scanner to capture the string being placed
        Scanner scanner = new Scanner(source);
        //
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens.
        for (Token token : tokens)
        {
            System.out.println(token);
        }
    }

    /*
        A short tangent on why would want to use the static method here. Static ties the method to the class, not an instance of the class.
        This means there is only one "copy" of the method in memory, not one for each instance of the class, pretty efficent!
        We use this function for error reporting so it does not rely on instance specific data we just need to know what line the error occured on.
    */
    // Our error report message
    static void error(int line, String message)
    {
        // We report the line number and our message! pretty cool
        report(line,"",message);
    }

    private static void report(int line, String where, String message)
    {
        System.err.println("[line" + line +"] Error" + where + ": " + message);
        hadError = true;
    }
}