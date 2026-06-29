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
                        System.out.print("Ingrese una expresión aritmética: ");
                        currentExpression = scanner.nextLine().trim();
                        System.out.println("Expresión guardada.");
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
                        System.out.println("Expresión postfija: " + postfix);
                    });
                    case "5" -> requireExpression(currentExpression, expression -> {
                        try {
                            double value = analyzer.evaluateExpression(expression);
                            System.out.println("Resultado: " + value);
                        } catch (IllegalArgumentException ex) {
                            System.out.println(ex.getMessage());
                        }
                    });
                    case "6" -> requireExpression(currentExpression, expression -> {
                        analyzer.traceInfixToPostfix(expression).forEach(System.out::println);
                    });
                    case "7" -> {
                        currentExpression = null;
                        System.out.println("Expresión limpiada.");
                    }
                    case "8" -> running = false;
                    default -> System.out.println("Opción inválida.");
                }
            }
        }
    }

    private static void printMenu(String currentExpression) {
        System.out.println();
        System.out.println("=== Analizador de Expresiones Aritméticas con Pila ===");
        System.out.println("Expresión actual: " + (currentExpression == null ? "<ninguna>" : currentExpression));
        System.out.println(" 1. Ingresar expresión");
        System.out.println(" 2. Validar paréntesis");
        System.out.println(" 3. Validar estructura de la expresión");
        System.out.println(" 4. Convertir de infijo a postfijo");
        System.out.println(" 5. Evaluar expresión");
        System.out.println(" 6. Mostrar operaciones de la pila paso a paso");
        System.out.println(" 7. Limpiar expresión");
        System.out.println(" 8. Salir");
        System.out.print("Elija una opción: ");
    }

    private static void requireExpression(String expression, Consumer<String> action) {
        if (expression == null || expression.isBlank()) {
            System.out.println("No hay una expresión cargada.");
            return;
        }
        action.accept(expression);
    }
}
