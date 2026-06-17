import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

final class ExpressionAnalyzer {
    ValidationResult validateParentheses(String expression) {
        List<Token> tokens = tokenize(expression);
        Pila stack = new Pila();

        for (Token token : tokens) {
            if (token.type() == TokenType.LEFT_PAREN) {
                stack.push('(');
            } else if (token.type() == TokenType.RIGHT_PAREN) {
                if (stack.isEmpty()) {
                    return ValidationResult.invalid("Invalid expression: closing parenthesis without matching opening parenthesis.");
                }
                stack.pop();
            }
        }

        if (!stack.isEmpty()) {
            return ValidationResult.invalid("Invalid expression: there are unmatched opening parentheses.");
        }

        return ValidationResult.valid("Parentheses are balanced.");
    }

    ValidationResult validateExpression(String expression) {
        List<Token> tokens = tokenize(expression);
        if (tokens.isEmpty()) {
            return ValidationResult.invalid("Invalid expression: empty input.");
        }

        Pila parentheses = new Pila();
        boolean expectingOperand = true;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (token.type() == TokenType.NUMBER || token.type() == TokenType.VARIABLE) {
                if (!expectingOperand) {
                    return ValidationResult.invalid("Invalid expression: two operands cannot appear together.");
                }
                expectingOperand = false;
                continue;
            }

            if (token.type() == TokenType.LEFT_PAREN) {
                if (!expectingOperand) {
                    return ValidationResult.invalid("Invalid expression: missing operator before '('.");
                }
                parentheses.push('(');
                expectingOperand = true;
                continue;
            }

            if (token.type() == TokenType.RIGHT_PAREN) {
                if (expectingOperand) {
                    return ValidationResult.invalid("Invalid expression: empty group or operator before ')'.");
                }
                if (parentheses.isEmpty()) {
                    return ValidationResult.invalid("Invalid expression: closing parenthesis without matching opening parenthesis.");
                }
                parentheses.pop();
                expectingOperand = false;
                continue;
            }

            if (token.type() == TokenType.OPERATOR) {
                if (expectingOperand) {
                    return ValidationResult.invalid("Invalid expression: operator found where an operand was expected.");
                }
                if (i == tokens.size() - 1) {
                    return ValidationResult.invalid("Invalid expression: cannot end with an operator.");
                }
                expectingOperand = true;
            }
        }

        if (expectingOperand) {
            return ValidationResult.invalid("Invalid expression: expression cannot end with an operator.");
        }

        if (!parentheses.isEmpty()) {
            return ValidationResult.invalid("Invalid expression: there are unmatched opening parentheses.");
        }

        return ValidationResult.valid("Expression structure is valid.");
    }

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
                        throw new IllegalArgumentException("Invalid expression: mismatched parentheses.");
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
                throw new IllegalArgumentException("Invalid expression: mismatched parentheses.");
            }
            output.add(String.valueOf(top));
        }

        return String.join(" ", output);
    }

    double evaluateExpression(String expression) {
        List<Token> tokens = tokenize(expression);
        if (tokens.stream().anyMatch(token -> token.type() == TokenType.VARIABLE)) {
            throw new IllegalArgumentException("Evaluation requires a numeric expression without variables.");
        }

        String postfix = toPostfix(expression);
        return evaluatePostfix(postfix);
    }

    List<String> traceInfixToPostfix(String expression) {
        ValidationResult result = validateExpression(expression);
        if (!result.valid()) {
            throw new IllegalArgumentException(result.message());
        }

        List<Token> tokens = tokenize(expression);
        Pila operators = new Pila();
        List<String> output = new ArrayList<>();
        List<String> trace = new ArrayList<>();

        trace.add("Tracing infix to postfix conversion:");

        for (Token token : tokens) {
            String action;
            switch (token.type()) {
                case NUMBER, VARIABLE -> {
                    output.add(token.text());
                    action = "append operand";
                }
                case LEFT_PAREN -> {
                    operators.push('(');
                    action = "push '('";
                }
                case RIGHT_PAREN -> {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        output.add(String.valueOf(operators.pop()));
                    }
                    operators.pop();
                    action = "pop until '('";
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
                    action = "push operator";
                }
                default -> throw new IllegalStateException("Unexpected token type.");
            }

            trace.add(String.format("token=%s | action=%s | stack=%s | output=%s",
                    token.text(), action, operators.snapshot(), String.join(" ", output)));
        }

        while (!operators.isEmpty()) {
            output.add(String.valueOf(operators.pop()));
            trace.add(String.format("finalize | stack=%s | output=%s", operators.snapshot(), String.join(" ", output)));
        }

        trace.add("Postfix result: " + String.join(" ", output));
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
                    throw new IllegalArgumentException("Invalid postfix expression.");
                }
                double right = stack.pop();
                double left = stack.pop();
                stack.push(applyOperator(left, right, token.charAt(0)));
                continue;
            }

            throw new IllegalArgumentException("Evaluation requires numeric operands only.");
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression.");
        }

        return stack.pop();
    }

    private double applyOperator(double left, double right, char operator) {
        return switch (operator) {
            case '+' -> left + right;
            case '-' -> left - right;
            case '*' -> left * right;
            case '/' -> {
                if (right == 0.0d) {
                    throw new IllegalArgumentException("Division by zero.");
                }
                yield left / right;
            }
            case '^' -> Math.pow(left, right);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
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
                    throw new IllegalArgumentException("Invalid number token: " + number);
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

            throw new IllegalArgumentException("Unsupported character: " + ch);
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
