package com.craftinginterpreters.lox;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// This allows us to import the static values and methods directly without having to reference the class they belong too
import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner
{

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    // We create a list for our tokens and we bettern not reassign this list and lose our tokens! Hence, the final keyword.
    private final List<Token> tokens = new ArrayList<>();

    // We store the source code as a string. This can be from a file or the REPL
    private final String source;
    // Start and current are offsets into the string meaning it's not just the index of the character in the source code but the index of the character in each lexeme.
    // Think about it this way, Start is the index of the first character in the lexeme.
    // Also remember that the tokens are stored linearly meaning we read through them one at a time.
    private int start = 0;

    private int current = 0;
    // Line keeps track of the current line we are on in the source file to keep track of the line number so we can report errors.
    private int line = 1;

    Scanner (String source)
    {
        // We store the source code as a string we have a list ready to fill with tokens we created!
        this.source = source;
    }

    // We are going to create a method that will scan over the source code and return a list of tokens.
    List<Token> scanTokens()
    {
        while(!isAtEnd())
        {
            // we are at the beginning of the next lexeme
            start = current;
            scanToken();
        }
        // This is the last token we created, we add it to the list of tokens.
        tokens.add(new Token(EOF, "", null, line));
        return tokens;  // Add this line
    }



    // This is the real heart of our scanner. This is what
    private void scanToken()
    {
        // We grab the next character
        char c = advance();
        switch (c)
        {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/':
                if (match('/'))
                {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                else
                {
                    addToken(SLASH);
                }
                break;
            case 'o':
                if (match('r'))
                {
                    addToken(OR);
                }
                break;
            case ' ': case '\r': case '\t':
            // Ignore whitespace.
            break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c))
                {
                    number();
                }
                else if (isAlpha(c))
                {
                    identifier();
                }
                else
                {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }

    }

    // These are helper methods to make the scanner more readable.
    // This is called a method override if only token type is passed then the literal is null
    private void addToken(TokenType type)
    {
        addToken(type, null);
    }
    // This function is then called by the previous wrapper function. It takes a token and the object literal
    private void addToken(TokenType type, Object literal)
    {
        // We are creating string representations of the tokens from are one large source string
        // '(' would be start = 0 current = 1 so substring would be from 0-0 creating "("
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    // We are going to increment advance to grab the next character.
    private char advance()
    {
        // this is better looking source.charAt(current++), but lets make it more explicit return
        char c = source.charAt(current);
        current++;
        return c;
    }

    // This helps us for comments as we continue to read the comment disregarding useless symbols, until we hit a \n aka new line
    private char peek()
    {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext()
    {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        // remember current already incremented upon the completion of the advance function so this looks at the next character
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    // Our wonderful little helper function that lets us know when we have reached the end of our source code
    private boolean isAtEnd()
    {
        return current >= source.length();
    }

    private void identifier()
    {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void number()
    {
        //1. `peek()` looks at the current character without consuming it (like looking ahead)
        //2. `isDigit(peek())` checks if that character is a number (0-9)
        //3. While this condition is true:
        //    - `advance()` consumes (moves past) the current digit
        //    - Then loops to check the next character
        while (isDigit(peek())) advance();

        // Look for a fractional part. ensure that we still are encountering a digit after the dot.
        if (peek() == '.' && isDigit(peekNext()))
        {
            // Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }
        // Finally creates the numberical literal and adds it to the list of tokens. This would use the second version of the addToken method.
        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void string()
    {
        while (peek() != '"' && !isAtEnd())
        {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd())
        {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c)
    {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c)
    {
        return isAlpha(c) || isDigit(c);
    }

}