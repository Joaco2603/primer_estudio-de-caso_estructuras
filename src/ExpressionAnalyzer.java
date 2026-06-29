import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes, validates, and evaluates arithmetic expressions in infix notation.
 * <p>
 * Provides four core capabilities:
 * <ul>
 *   <li><b>Tokenization</b> — breaks an expression string into tokens (numbers, variables, operators, parentheses)</li>
 *   <li><b>Validation</b> — checks parentheses balance and overall expression structure grammar</li>
 *   <li><b>Infix to Postfix</b> — converts infix expressions to Reverse Polish Notation using the Shunting-yard algorithm</li>
 *   <li><b>Evaluation</b> — evaluates postfix expressions numerically, resolving supported constants directly</li>
 * </ul>
 * <p>
 * Operates on a custom {@link Pila} (stack) implementation to demonstrate
 * linked-list-based stack operations throughout the analysis pipeline.
 */
final class ExpressionAnalyzer {
    /**
     * Validates that all parentheses in the expression are properly balanced.
     * <p>
     * Uses a {@link Pila} to track opening parentheses: each '(' is pushed,
     * each ')' pops a matching '(' from the stack.
     *
     * @param expression the infix expression to validate
     * @return a {@link ValidationResult} indicating whether parentheses are balanced
     */
    ValidationResult validateParentheses(String expression) {
        List<Token> tokens = tokenize(expression);
        Pila stack = new Pila();

        for (Token token : tokens) {
            if (token.type() == TokenType.LEFT_PAREN) {
                stack.push('(');
            } else if (token.type() == TokenType.RIGHT_PAREN) {
                if (stack.isEmpty()) {
                    return ValidationResult.invalid("Expresión inválida: hay un paréntesis de cierre sin su paréntesis de apertura correspondiente.");
                }
                stack.pop();
            }
        }

        if (!stack.isEmpty()) {
            return ValidationResult.invalid("Expresión inválida: hay paréntesis de apertura sin cerrar.");
        }

        return ValidationResult.valid("Los paréntesis están balanceados.");
    }

    /**
     * Validates the full grammatical structure of an expression, including
     * operator placement, operand ordering, and parentheses grouping.
     *
     * @param expression the infix expression to validate
     * @return a {@link ValidationResult} describing whether the structure is valid
     */
    ValidationResult validateExpression(String expression) {
        List<Token> tokens = tokenize(expression);
        if (tokens.isEmpty()) {
            return ValidationResult.invalid("Expresión inválida: la entrada está vacía.");
        }

        Pila parentheses = new Pila();
        boolean expectingOperand = true;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (token.type() == TokenType.NUMBER || token.type() == TokenType.VARIABLE) {
                if (!expectingOperand) {
                    return ValidationResult.invalid("Expresión inválida: no pueden aparecer dos operandos seguidos.");
                }
                expectingOperand = false;
                continue;
            }

            if (token.type() == TokenType.LEFT_PAREN) {
                if (!expectingOperand) {
                    return ValidationResult.invalid("Expresión inválida: falta un operador antes de '('.");
                }
                parentheses.push('(');
                expectingOperand = true;
                continue;
            }

            if (token.type() == TokenType.RIGHT_PAREN) {
                if (expectingOperand) {
                    return ValidationResult.invalid("Expresión inválida: hay un grupo vacío o un operador antes de ')'.");
                }
                if (parentheses.isEmpty()) {
                    return ValidationResult.invalid("Expresión inválida: hay un paréntesis de cierre sin su paréntesis de apertura correspondiente.");
                }
                parentheses.pop();
                expectingOperand = false;
                continue;
            }

            if (token.type() == TokenType.OPERATOR) {
                if (expectingOperand) {
                    return ValidationResult.invalid("Expresión inválida: se encontró un operador donde se esperaba un operando.");
                }
                if (i == tokens.size() - 1) {
                    return ValidationResult.invalid("Expresión inválida: no puede terminar con un operador.");
                }
                expectingOperand = true;
            }
        }

        if (expectingOperand) {
            return ValidationResult.invalid("Expresión inválida: no puede terminar con un operador.");
        }

        if (!parentheses.isEmpty()) {
            return ValidationResult.invalid("Expresión inválida: hay paréntesis de apertura sin cerrar.");
        }

        return ValidationResult.valid("La estructura de la expresión es válida.");
    }

    /**
     * Converts an infix expression to postfix notation (Reverse Polish Notation)
     * using the Shunting-yard algorithm.
     * <p>
     * Operands are appended directly to the output; operators are managed on a
     * {@link Pila} according to precedence and associativity rules.
     *
     * @param expression the infix expression to convert
     * @return the expression in postfix notation, tokens separated by spaces
     * @throws IllegalArgumentException if the expression is invalid
     */
    String toPostfix(String expression) {
        ValidationResult result = validateExpression(expression);
        if (!result.valid()) {
            throw new IllegalArgumentException(result.message());
        }

        List<Token> tokens = tokenize(expression);
        Pila operators = new Pila();
        List<String> output = new ArrayList<>();

        for (Token token : tokens) {
            switch (token.type()) {
                case NUMBER, VARIABLE -> output.add(token.text());
                case LEFT_PAREN -> operators.push('(');
                case RIGHT_PAREN -> {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        output.add(String.valueOf(operators.pop()));
                    }
                    if (operators.isEmpty()) {
                        throw new IllegalArgumentException("Expresión inválida: los paréntesis no coinciden.");
                    }
                    operators.pop();
                }
                case OPERATOR -> {
                    char current = token.text().charAt(0);
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        char top = operators.peek();
                        if (hasHigherPrecedence(top, current) || (hasSamePrecedence(top, current) && isLeftAssociative(current))) {
                            output.add(String.valueOf(operators.pop()));
                        } else {
                            break;
                        }
                    }
                    operators.push(current);
                }
            }
        }

        while (!operators.isEmpty()) {
            char top = operators.pop();
            if (top == '(') {
                throw new IllegalArgumentException("Expresión inválida: los paréntesis no coinciden.");
            }
            output.add(String.valueOf(top));
        }

        return String.join(" ", output);
    }

    /**
     * Evaluates a numeric expression, including supported constants.
     *
     * @param expression the infix expression to evaluate
     * @return the numeric result
     * @throws IllegalArgumentException if the expression is invalid, contains
     *                                  an unknown constant, or contains a division by zero
     */
    double evaluateExpression(String expression) {
        String postfix = toPostfix(expression);
        return evaluatePostfix(postfix);
    }

    /**
     * Generates a step-by-step trace of the infix-to-postfix conversion,
     * showing the token being processed, the action taken, and the state of
     * both the operator stack and the output list after each step.
     *
     * @param expression the infix expression to trace
     * @return a list of formatted trace lines
     * @throws IllegalArgumentException if the expression is invalid
     */
    List<String> traceInfixToPostfix(String expression) {
        ValidationResult result = validateExpression(expression);
        if (!result.valid()) {
            throw new IllegalArgumentException(result.message());
        }

        List<Token> tokens = tokenize(expression);
        Pila operators = new Pila();
        List<String> output = new ArrayList<>();
        List<String> trace = new ArrayList<>();

        trace.add("Trazando la conversión de infijo a postfijo:");

        for (Token token : tokens) {
            String action;
            switch (token.type()) {
                case NUMBER, VARIABLE -> {
                    output.add(token.text());
                    action = "agregar operando";
                }
                case LEFT_PAREN -> {
                    operators.push('(');
                    action = "apilar '('";
                }
                case RIGHT_PAREN -> {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        output.add(String.valueOf(operators.pop()));
                    }
                    operators.pop();
                    action = "desapilar hasta '('";
                }
                case OPERATOR -> {
                    char current = token.text().charAt(0);
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        char top = operators.peek();
                        if (hasHigherPrecedence(top, current) || (hasSamePrecedence(top, current) && isLeftAssociative(current))) {
                            output.add(String.valueOf(operators.pop()));
                        } else {
                            break;
                        }
                    }
                    operators.push(current);
                    action = "apilar operador";
                }
                default -> throw new IllegalStateException("Tipo de token inesperado.");
            }

            trace.add(String.format("token=%s | acción=%s | pila=%s | salida=%s",
                    token.text(), action, operators.snapshot(), String.join(" ", output)));
        }

        while (!operators.isEmpty()) {
            output.add(String.valueOf(operators.pop()));
            trace.add(String.format("finalizar | pila=%s | salida=%s", operators.snapshot(), String.join(" ", output)));
        }

        trace.add("Resultado postfijo: " + String.join(" ", output));
        return trace;
    }

    private double evaluatePostfix(String postfix) {
        ArrayDeque<Double> stack = new ArrayDeque<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }

            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
                continue;
            }

            if (token.length() == 1 && isOperator(token.charAt(0))) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Expresión postfija inválida.");
                }
                double right = stack.pop();
                double left = stack.pop();
                stack.push(applyOperator(left, right, token.charAt(0)));
                continue;
            }

            stack.push(resolveConstant(token));
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Expresión postfija inválida.");
        }

        return stack.pop();
    }

    private double resolveConstant(String name) {
        return switch (name) {
            case "pi" -> Math.PI;
            case "e" -> Math.E;
            default -> throw new IllegalArgumentException("Constante no definida: " + name);
        };
    }

    private double applyOperator(double left, double right, char operator) {
        return switch (operator) {
            case '+' -> left + right;
            case '-' -> left - right;
            case '*' -> left * right;
            case '/' -> {
                if (right == 0.0d) {
                    throw new IllegalArgumentException("División entre cero.");
                }
                yield left / right;
            }
            case '^' -> Math.pow(left, right);
            default -> throw new IllegalArgumentException("Operador no soportado: " + operator);
        };
    }

    private List<Token> tokenize(String expression) {
        String input = expression == null ? "" : expression.replaceAll("\\s+", "");
        List<Token> tokens = new ArrayList<>();
        int index = 0;

        while (index < input.length()) {
            char ch = input.charAt(index);

            if (Character.isDigit(ch) || ch == '.') {
                int start = index;
                boolean dotSeen = ch == '.';
                index++;
                while (index < input.length()) {
                    char current = input.charAt(index);
                    if (Character.isDigit(current)) {
                        index++;
                    } else if (current == '.' && !dotSeen) {
                        dotSeen = true;
                        index++;
                    } else {
                        break;
                    }
                }

                String number = input.substring(start, index);
                if (number.equals(".") || number.endsWith(".")) {
                    throw new IllegalArgumentException("Token numérico inválido: " + number);
                }
                tokens.add(new Token(number, TokenType.NUMBER));
                continue;
            }

            if (Character.isLetter(ch) || ch == '_') {
                int start = index;
                index++;
                while (index < input.length()) {
                    char current = input.charAt(index);
                    if (Character.isLetterOrDigit(current) || current == '_') {
                        index++;
                    } else {
                        break;
                    }
                }
                tokens.add(new Token(input.substring(start, index), TokenType.VARIABLE));
                continue;
            }

            if (ch == '(') {
                tokens.add(new Token("(", TokenType.LEFT_PAREN));
                index++;
                continue;
            }

            if (ch == ')') {
                tokens.add(new Token(")", TokenType.RIGHT_PAREN));
                index++;
                continue;
            }

            if (isOperator(ch)) {
                tokens.add(new Token(String.valueOf(ch), TokenType.OPERATOR));
                index++;
                continue;
            }

            throw new IllegalArgumentException("Carácter no soportado: " + ch);
        }

        return tokens;
    }

    private boolean isNumber(String token) {
        return token.matches("\\d+(\\.\\d+)?|\\.\\d+");
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
    }

    private boolean hasHigherPrecedence(char stackOp, char currentOp) {
        return precedence(stackOp) > precedence(currentOp);
    }

    private boolean hasSamePrecedence(char stackOp, char currentOp) {
        return precedence(stackOp) == precedence(currentOp);
    }

    private boolean isLeftAssociative(char operator) {
        return operator != '^';
    }

    private int precedence(char operator) {
        return switch (operator) {
            case '^' -> 3;
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            default -> 0;
        };
    }

    private record Token(String text, TokenType type) {
    }

    private enum TokenType {
        NUMBER,
        VARIABLE,
        OPERATOR,
        LEFT_PAREN,
        RIGHT_PAREN
    }

    record ValidationResult(boolean valid, String message) {
        static ValidationResult valid(String message) {
            return new ValidationResult(true, message);
        }

        static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }
    }
}
