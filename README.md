# Primer Estudio de Caso — Analizador de Expresiones Aritméticas

Una aplicación de consola interactiva en Java que analiza, valida, convierte y evalúa expresiones aritméticas en notación infija. Implementa una pila basada en listas enlazadas simples como estructura de datos fundamental.

## Funcionalidades

- **Validación de paréntesis** — verifica que los paréntesis estén balanceados correctamente.
- **Validación de estructura** — comprueba la gramática de la expresión (operadores entre operandos, paréntesis bien ubicados, etc.).
- **Conversión infijo → postfijo** — aplica el algoritmo Shunting-yard para transformar una expresión infija a notación polaca inversa (RPN).
- **Evaluación numérica** — evalúa expresiones exclusivamente numéricas (sin variables) y devuelve el resultado.
- **Traza paso a paso** — muestra el estado de la pila y la salida en cada paso de la conversión infijo → postfijo.

Operadores soportados: `+` (suma), `-` (resta), `*` (multiplicación), `/` (división), `^` (potencia).

## Requisitos

- **Java 26** — el proyecto utiliza características _preview_ de Java 26.
- No requiere Maven, Gradle ni dependencias externas.

## Compilación y ejecución

```bash
# Compilar
javac --enable-preview --source 26 -d out src/Main.java

# Ejecutar
java --enable-preview -cp out Main
```

## Uso

Al ejecutar la aplicación se muestra un menú interactivo:

```
=== Arithmetic Expression Stack Analyzer ===
Current expression: <none>
1. Enter expression
2. Validate parentheses
3. Validate expression structure
4. Convert infix to postfix
5. Evaluate numeric expression
6. Show stack operations step by step
7. Clear expression
8. Exit
```

1. Ingrese una expresión (ej. `3 + 4 * (2 - 1)`).
2. Seleccione la operación deseada del menú.
3. Los resultados se muestran en la consola.

## Estructura del proyecto

```
src/
├── Main.java                   — Interfaz de consola y menú interactivo
├── ExpressionAnalyzer.java     — Tokenización, validación, conversión y evaluación
├── Pila.java                   — Implementación de pila (stack) con lista enlazada
└── Nodo.java                   — Nodo de la lista enlazada simple
```

### Componentes

- **`Main`** — bucle principal con menú de 8 opciones. Gestiona la expresión activa y delega en `ExpressionAnalyzer`.
- **`ExpressionAnalyzer`** — núcleo del análisis. Incluye:
  - Tokenización de la expresión en números, variables, operadores y paréntesis.
  - Validación sintáctica y estructural.
  - Algoritmo Shunting-yard para conversión a postfijo.
  - Evaluación de expresiones postfijo con una pila `ArrayDeque<Double>`.
  - Traza detallada del proceso de conversión.
- **`Pila`** — implementación propia de pila genérica para caracteres, usando una lista enlazada simple de `Nodo`.
- **`Nodo`** — nodo con un valor `char` y una referencia al siguiente nodo.

## Propósito educativo

Este proyecto fue desarrollado como caso de estudio para explorar:

- Estructuras de datos: pilas y listas enlazadas simples.
- Algoritmos clásicos: Shunting-yard y evaluación de expresiones en RPN.
- Análisis léxico básico (tokenización).
- Validación sintáctica de expresiones matemáticas.
- Java 26 con características.
