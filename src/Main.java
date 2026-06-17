import java.util.Scanner;
import java.util.function.Consumer;

public class Main {
    private static final ExpressionAnalyzer analyzer = new ExpressionAnalyzer();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String currentExpression = null;
            boolean running = true;

            while (running) {
                printMenu(currentExpression);
                String option = scanner.nextLine().trim();

                switch (option) {
                    case "1" -> {
                        System.out.print("Enter an arithmetic expression: ");
                        currentExpression = scanner.nextLine().trim();
                        System.out.println("Expression saved.");
                    }
                    case "2" -> requireExpression(currentExpression, expression -> {
                        var result = analyzer.validateParentheses(expression);
                        System.out.println(result.message());
                    });
                    case "3" -> requireExpression(currentExpression, expression -> {
                        var result = analyzer.validateExpression(expression);
                        System.out.println(result.message());
                    });
                    case "4" -> requireExpression(currentExpression, expression -> {
                        String postfix = analyzer.toPostfix(expression);
                        System.out.println("Postfix expression: " + postfix);
                    });
                    case "5" -> requireExpression(currentExpression, expression -> {
                        try {
                            double value = analyzer.evaluateExpression(expression);
                            System.out.println("Result: " + value);
                        } catch (IllegalArgumentException ex) {
                            System.out.println(ex.getMessage());
                        }
                    });
                    case "6" -> requireExpression(currentExpression, expression -> {
                        analyzer.traceInfixToPostfix(expression).forEach(System.out::println);
                    });
                    case "7" -> {
                        currentExpression = null;
                        System.out.println("Expression cleared.");
                    }
                    case "8" -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            }
        }
    }

    private static void printMenu(String currentExpression) {
        System.out.println();
        System.out.println("=== Arithmetic Expression Stack Analyzer ===");
        System.out.println("Current expression: " + (currentExpression == null ? "<none>" : currentExpression));
        System.out.println("1. Enter expression");
        System.out.println("2. Validate parentheses");
        System.out.println("3. Validate expression structure");
        System.out.println("4. Convert infix to postfix");
        System.out.println("5. Evaluate numeric expression");
        System.out.println("6. Show stack operations step by step");
        System.out.println("7. Clear expression");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }

    private static void requireExpression(String expression, Consumer<String> action) {
        if (expression == null || expression.isBlank()) {
            System.out.println("No expression loaded.");
            return;
        }
        action.accept(expression);
    }
}
