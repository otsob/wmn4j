# wmn4j Coding Conventions

The coding style in wmn4j is not very strict, but there are a couple of basic
conventions to ensure that code style stays clean and consistent.
The `build-resources` directory of the repository contains the Checkstyle
configuration and formatter settings (for Eclipse) that should take care of most of the formatting.

## Code Formatting
* Indentation uses tabs
* No trailing whitespace
* Files end in a newline character
* Line length is at most 120
* Constant names are all upper case
* Braces start on the same line
* All `if`, `while`, and `for` blocks use braces
* For `else`statements the brace that closes the previous block is on the same line

## Documentation
* All packages are documented using a `package-info.java` file
* All public constants have Javadocs
* All public methods have Javadocs
    * All parameters are documented
    * return type is documented
    * Javadoc starts with a sentence that ends in a period

## Code style in general
* Generally avoid the use of abbreviations unless their meaning is unambiguous
* Redundant modifiers are not used (e.g. public for methods in interfaces)
* All fields and classes that should be `final` are `final`
* Collections of static utility methods have a private constructor to disable instantiation
* Modified order follows the one suggested in [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html)
