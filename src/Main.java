import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {
    private static final ExpressionAnalyzer analyzer = new ExpressionAnalyzer();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String currentExpression = null;
            Map<String, Double> variables = new HashMap<>();
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
                    case "2" -> {
                        System.out.print("Ingrese la asignación de variable (ej. a=3): ");
                        String assignment = scanner.nextLine().trim();
                        if (!assignment.contains("=")) {
                            System.out.println("Formato inválido. Use: nombre=valor");
                            break;
                        }
                        String[] parts = assignment.split("=", 2);
                        String varName = parts[0].trim();
                        String varValue = parts[1].trim();
                        if (varName.isEmpty() || !Character.isLetter(varName.charAt(0))) {
                            System.out.println("Nombre de variable inválido.");
                            break;
                        }
                        try {
                            double value = Double.parseDouble(varValue);
                            variables.put(varName, value);
                            System.out.println("Variable " + varName + " = " + value);
                        } catch (NumberFormatException e) {
                            System.out.println("Valor numérico inválido: " + varValue);
                        }
                    }
                    case "3" -> {
                        if (variables.isEmpty()) {
                            System.out.println("No hay variables asignadas.");
                        } else {
                            System.out.println("Variables:");
                            variables.forEach((name, value) ->
                                    System.out.println("  " + name + " = " + value));
                        }
                    }
                    case "4" -> requireExpression(currentExpression, expression -> {
                        var result = analyzer.validateParentheses(expression);
                        System.out.println(result.message());
                    });
                    case "5" -> requireExpression(currentExpression, expression -> {
                        var result = analyzer.validateExpression(expression);
                        System.out.println(result.message());
                    });
                    case "6" -> requireExpression(currentExpression, expression -> {
                        String postfix = analyzer.toPostfix(expression);
                        System.out.println("Expresión postfija: " + postfix);
                    });
                    case "7" -> requireExpression(currentExpression, expression -> {
                        try {
                            double value = analyzer.evaluateExpression(expression, variables);
                            System.out.println("Resultado: " + value);
                        } catch (IllegalArgumentException ex) {
                            System.out.println(ex.getMessage());
                        }
                    });
                    case "8" -> requireExpression(currentExpression, expression -> {
                        analyzer.traceInfixToPostfix(expression).forEach(System.out::println);
                    });
                    case "9" -> {
                        currentExpression = null;
                        System.out.println("Expresión limpiada.");
                    }
                    case "10" -> {
                        variables.clear();
                        System.out.println("Todas las variables fueron limpiadas.");
                    }
                    case "11" -> running = false;
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
        System.out.println(" 2. Asignar valor a variable");
        System.out.println(" 3. Mostrar valores de variables");
        System.out.println(" 4. Validar paréntesis");
        System.out.println(" 5. Validar estructura de la expresión");
        System.out.println(" 6. Convertir de infijo a postfijo");
        System.out.println(" 7. Evaluar expresión");
        System.out.println(" 8. Mostrar operaciones de la pila paso a paso");
        System.out.println(" 9. Limpiar expresión");
        System.out.println("10. Limpiar variables");
        System.out.println("11. Salir");
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
