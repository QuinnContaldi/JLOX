# JLOX


# Chapter 4

## File Processing Steps
Let's take our first real step up the language mountain by finding the trail we'll hike — this trail begins with processing the source file. Once we've read in the source, we can begin creating lexemes and tokens, the foundational pieces of any language.

1. **File Path Resolution**
   The first step is locating our source file. Think of the "finding a trail analogy," the file path is the trail. We use `Paths.get(path)` to create a Path object that represents the file's location in the file system.

2. **Reading the File**
   Once we have the path, we read the entire file into memory using `Files.readAllBytes(Path)`. This method efficiently loads all the file contents at once, rather than reading it piece by piece. For our interpreter's purposes, this approach is more efficient as we need access to the entire program during analysis.

3. **Converting to Text**
   After reading the raw bytes, we need to convert them into a format we can work with. We create a String using the system's default character encoding (typically UTF-8). This conversion transforms the raw bytes into readable text that contains our source code.

4. **Preparing for Lexical Analysis**
   With our source code now in string format, we pass it to the `run` method. This begins the actual interpretation process, starting with lexical analysis (or "lexing"), where our scanner breaks down the source code into tokens. MAKE SURE TO READ VERY CARFULLY. We are creating lexems and tokens at the same time, instead of doing two pass throughs.

The entire process transforms our source file from raw bytes into tokens that our interpreter can understand and process. This is main point of chapter 4, creating meaningful tokens from raw characters.
```C#
{
byte[] bytes = Files.readAllBytes(Paths.get(path));

run(new String(bytes, Charset.defaultCharset()));
}
```

## Creating Our Lexical Analyzer
Our next exciting adventure is turning our source code into meaningful pieces that our interpreter can understand. We'll break this down into two main steps: creating lexemes and then converting them into tokens.

### The Process
1. We have our trusty `runFile(String path)` method, which handles our source file. Along the way, we have a helpful companion called `hadError` - a boolean variable in our Lox class that stops us if something goes wrong. 
2. The real excitement begins when we create our Scanner object: We're creating a new Scanner and handing it our source code. It's going to examine every character and figure out what it means. Breaking down our source code into meaningful pieces that our interpreter can understand.
3. Finally, we print out our tokens to make sure everything looks right. It's like a final inspection before sending our tokens on their way to the parser.

```java
Scanner scanner = new Scanner(source);
```
```java
private static void run(String source)
{
    if(hadError) System.exit(65);
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    for (Token token : tokens)
    {
        System.out.println(token);
    }
}
```
## scanTokens Method The first step into Lexeme and Tokenization

### three tracking variables used for character count for Lexemes 

```
// Starting character of the lexeme
private int start = 0;
// Current character of the lexeme being evaluated
private int current = 0;
// Current line used for error reporting
private int line = 1;
```

## Notes on `scanTokens()` from *Crafting Interpreters*
1. **Returning a List of Tokens** 
It's important to remember that this function ultimately returns a list of tokens. These tokens represent the smallest meaningful elements (lexemes) of the source code — such as identifiers, literals, operators, etc. This list will be consumed later by the parser.
2. **Control Flow with the `while` Loop**
3. The `while` loop controls how we move through the source code. It continues scanning tokens until we reach the end of the file. Here's the `isAtEnd()` function that helps determine when to stop:
4. ```java
   private boolean isAtEnd()
   {
       return current >= source.length();
   }```
5. **Tracking Lexeme Boundaries with `start = current;`**
   The line `start = current;` updates the `start` index to match `current`, marking the beginning of a new lexeme. This update happens at the start of each loop iteration, before scanning the next token. Tracking `start` and `current` separately helps identify the substring for the current token. It’s easy to get confused when these are updated in different places, so I’ll make sure to point out exactly when and why that happens as we go forward.
6. **Calling `scanToken()`**
   The `scanToken()` function is called every iteration of the loop. This function is where the actual work of identifying and creating tokens occurs — it examines the current character(s) and builds the appropriate `Token` object based on what it sees. We'll take a deep dive into `scanToken()` shortly, but for now, just know that it’s the core engine of the scanner.
7. **Adding the End-of-File Token**
   Once we've scanned all tokens and exited the loop, we add an End-of-File (EOF) token:
   ```
   tokens.add(new Token(EOF, "", null, line));
   ```
   This token is used to signal that no more tokens remain in the source. Parsers rely on this to know when they’ve reached the end of input.
8. **Returning the Token List**
   Finally, we return the complete list of tokens back to the caller — typically a `run()` or `interpret()` function that kicks off the rest of the compilation process (parsing, interpretation, etc.).
9. **Next Step: Diving into `scanToken()`**
   We’re making steady progress along the lexing and parsing path. The next big step is to dive into the `scanToken()` function itself. This is where all the real action happens — identifying keywords, numbers, operators, string literals, and more.
   Here’s the full `scanTokens()` function for reference:
   ```java
   List<Token> scanTokens()
   {
       while (!isAtEnd())
       {
           start = current;
           scanToken();
       }
       tokens.add(new Token(EOF, "", null, line));
       return tokens;
   }
   ```
## scanToken and some important helper functions
`char c = advance();` Is the very first thing that happens with in our scan `scanToken` function. Lets take a look at this little helper function. In fact I want you to think of this function as the steps on the trail. Every advance gets us one more step closer to finishing the lexical and tokenization trail.
### advance()
1. `source.charAt(current)`:
    - Takes a snapshot of the character at our current position
    - Think of it like placing your finger on a specific letter in a book
    - This is the exact character that is returned before we increment our position

2. `current++`:
    - Increments our position counter
    - Like moving your finger to the next letter

3. `return c`:
    - Returns the character we found
    - This is the character we'll analyze in our scanner

4. Dont get confused we are not incrementing then examining the character we return the character then increment

The complete reference code
```java
private char advance() 
{
    char c = source.charAt(current);
    current++;
    return c;
}
```
### Single character Lexeme
- Single character Lexemes are relatively easy. Remember we are not just creating lexemes. We identify a lexeme and create a token by assigning the lexeme a type. 
- Remember `Token(TokenType type, String lexeme, Object literal, int line)` Dont be to concerend about the other paramaters as of now. The main point being, that we are finding the token type using the cases
  - Naturally we need to continue our journy by looking at how the `addToken()` function works. This leads to some intresting behavior like java overloading.
    ```
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
        while (peek() != '\n' && !isAtEnd()) advance();
    }
    else
    {
        addToken(SLASH);
    }
    break;
    ```
    
### addToken() Function
1. This is called a method override if only token type is passed then the literal is null. This is because These tokens have meaning through their type alone, and don't need to store any additional value.
2. The actual function `addToken()` and its invocation type is determined during compile time based on the arguemnts that are passed to the function.
3. If we call the first `addToken()` which will happen for the above cases, we pass in the type and a null literal

```java
private void addToken(TokenType type)
{
    addToken(type, null);
}
```
1. This function can be invoked one of two ways. The wrapper function of the single argument `addToken()` or by a multi argument `addToken()`
2. We create string representations of the tokens from one large string, which is our source code generated from the file our via the REPL
3. `String text = source.substring(start, current);` for example '(' would be parsed as 'start' = 0 'current' = 1 since `substring()` since the second index of substring is exclusive this creates a new string around 0,0 which is '('
4. `tokens.add(new Token(type, text, literal, line));` `tokens` is a list thus the `add` function adds a new token to the list
5. `new Token` creates a new token from four values we just assigned during our previous functions
6. `type` Was determined via the cases, which were chosen via the scanner using a character to determine the case
7. `text` Is the string we just created, in fact text is a lexeme the smallest understandable part for our interpreture.
8. `literal` This is what gives the lexeme meaning premoting it to token status. Remember we are creating lexemes then turning them into tokens in a single pass, efficency
9. `line` Is for error reporting not a lot of intresting stuff here
The full function of addToken()
```java
private void addToken(TokenType type, Object literal)
{
    // '(' would be start = 0 current = 1 so substring would be from 0-0 creating "("
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
}
```

### Lexemes consisting of two characters. 
- `>=` is not two tokens `> =` but actually one token consisting of two characters. There also exist a single token `>` and `=`. This leaves a key question how do we handle these cases approprately
- Ergo the `match()` function. If the next character is not a match then we have a since instance of our token a `>`, but if the next symbol is a match we have a `>=`
    ```
    case '!':
    addToken(match('=') ? BANG_EQUAL : BANG); break;
    case '=':
    addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
    case '<':
    addToken(match('=') ? LESS_EQUAL : LESS); break;
    case '>':
    addToken(match('=') ? GREATER_EQUAL : GREATER); break;
  ```

### match() function
- The `match()` function is the method that allows us to determine if we have a single or multicharacter lexeme. 
- Keep in mind the varible `current` already incremented upon the completion of the advance function so this looks at the next character
1. `private boolean match(char expected)` We return a boolean value which is returned to `match('=') ? GREATER_EQUAL : GREATER`. This is just some syntactic sugar for an if statment. The argument expected is the character we are looking for
2. `if (isAtEnd()) return false;` We should check if we are at the end `isAtEnd()` function was previously explained. If we are at the end of the file we must have a single character lexeme
3. This is where the actual matching occurs if the character we are currently at in our source file(**Remember we have already incremented current so we are at the next symbol**) Matches our expected. We have correctly identified our multi character lexeme.
4. `current++` Is what consumes the symbol and allows us to start the next lexeme. See this is not a token quite yer until we invoke the `addToken()` method
5. `return true;` If we find the expected symbol then
```java
private boolean match(char expected)
{
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;
    current++;
    return true;
}
```
### Symbols that we can ignore
- **Remember** if we do not envoke the `addToken()` method our symbol will be consumed without creating a token. This happens until we return a `EOF` token. 
- The while loop will keep invoking the `scanToken()` function, which first calls the `advance()` function incrementing our current
- This means that the cases that do not invoke `addToken()` will simply disregard the character as we do not create a lexeme from it.
1. `case ' ':` Is for encountering white space, we simply consume the symbol.
2. `case `\r`:` This symbol is used to print special symbols. Thus we should not create lexeme when this symbol is encounterd. 
3. `case '\t'` Tabs dont mean anything to us thus we can disregard it.
```
case ' ': case '\r': case '\t':
    break;
```

### The Cases Involving Strings
- `case '"': string(); break;` the case "" may seem confusing is it '''' or "" or '"' believe it or not they are all different.
- The `' " '` had the space dramatized to show what this actually means. We have a case to detect the begining of a string. Thus `''` is required to wrap a character.
- Since strings begin with `"` we then create `'"'` and that denotes a case in which we encounter the first qoutes `"`
1. while (peek() != '"' && !isAtEnd()) while the next character is not the closing qoutations `'"'` and we are not at the end we can continue to loop
2. The `peek()` function simply returns the next character with out consuming the symbol. This is used to look for the ending of the string  
```
private char peek()
{
  if (isAtEnd()) return '\0';
  return source.charAt(current);
} 
```
3. `if (peek() == '\n') line++;` If we encounter a new line symbol simply increment which line we are on
4. `advance();` strings dont contain lexems, they are a lexeme. So just continue to advance through the symbol
5. If our string does not end our user forgot to finish the string. Lets make sure to give them a helpful hint 
`if (isAtEnd())
        {
            Lox.error(line, "Unterminated string.");
            return;
        }`
6. `advance();` Once the while group breaks and we know the next character is the end of the string. Lets advance to that symbol
7. `String value = source.substring(start + 1, current - 1);` We now create our string from our source file preparing it to be tokenized.
```
// If source contains: "hello"
//                     ^    ^
//                start    current
// start = position of first quote
// current = position after last quote
// With start = 0 and current = 7:
// substring(1, 6) gives us just "hello" without the quotes
```
8. `addToken(STRING, value);` Now we promote our lexeme to a token! nice!
Full code
``` java
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
   ```
### The Default Case
The default case does a number of checks to determine if the lexeme is a keyword.
```
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
```
#### `isDigit(c)` Function Explored
`return c >= '0' && c <= '9';` we ensure that the numerical character is a 0-9 digit
```   
 private boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }
```
#### `number();` Function
This function is far more intresting as we actually assign a literal value when creating a token from the string
1. `while (isDigit(peek())) advance();` `peek()` looks at the current character without consuming it (like looking ahead)
2. `isDigit(peek())` checks if that character is a number (0-9) While this condition is true:
3. `advance()` consumes (moves past) the current digit
4. `if (peek() == '.' && isDigit(peekNext()))` Look for a fractional part. We ensure that we still are encountering a digit after the dot using the `peekNext()`
5. We use peekNext() to look at the next character without consuming the symbol
```
private char peekNext()
{
   if (current + 1 >= source.length()) return '\0';
   return source.charAt(current + 1);
}
```
6. `advance();` Is used to consume the "." for our fractional number
7. `while (isDigit(peek())) advance();` We continue to `advance()` through the characters until we hit a non digit
8. `addToken(NUMBER, Double.parseDouble(source.substring(start, current)));` Every number in lox is a float, since we can repersent every number that way "mostly".
   1. We cast the return type as a `Double`
   2. We `parseDouble` from the string we are going to create
   3. We are creating this string from `source`
   4. `substring` is how we select our string from `source` remember we updated our `start` and `current` index from our `advance()` function
   5. As a result we create the proper string and add the literal number(The value), and the `NUMBER` token type to our brand new number
```
  private void number()
  {

  while (isDigit(peek())) advance();

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
  ```
#### `identifier()`
function handles the lexical analysis of identifiers and keywords in our language. An identifier is any sequence of alphanumeric characters (including underscores) that begins with a letter or underscore.
1. **Character Consumption** `while (isAlphaNumeric(peek())) advance();`
   - Continues reading characters as long as they are letters, digits, or underscores
   - For example, in a variable name like `counter1`, it will consume all characters until it hits a non-alphanumeric character
2. **Lexeme Extraction** `String text = source.substring(start, current)`;
   - Extracts the complete identifier text from the source code
   - Example: For input `counter1 = 5`, it would extract `"counter1"`
3. **Keyword Detection**TokenType `type = keywords.get(text);  if (type == null) type = IDENTIFIER;`
   - Checks if the extracted text is a reserved keyword (like `if`, `while`, `for`)
   - If it's not a keyword, marks it as a regular IDENTIFIER
   - Example:
      - `while` → Recognized as WHILE token type
      - `counter1` → Recognized as IDENTIFIER token type
The function enables our scanner to properly handle both user-defined identifiers (like variable names) and language keywords, distinguishing between them based on the predefined keyword map.
   - Below is the helpful HashMap we use to compare our keys to find
```
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
```    
#### Everything else
Any other characters result in undefined behavior and should be reported to the user
```
   else
   {
       Lox.error(line, "Unexpected character.");
   }
   break;
```