/**
 * A LIFO (Last-In, First-Out) stack implementation using a singly linked list
 * of {@link Nodo} nodes.
 * <p>
 * Supports stack operations for operators and numeric values used by the
 * expression analyzer, plus {@link #snapshot()} to inspect the current stack
 * state for tracing and debugging.
 * <p>
 * All operations run in O(1) time except {@link #snapshot()} which runs
 * in O(n) to traverse and display the full stack.
 */
final class Pila {
    private Nodo top;
    private int size;

    /**
     * Pushes a character onto the top of the stack.
     *
     * @param value the character to push
     */
    void push(char value) {
        pushValue(String.valueOf(value));
    }

    /**
     * Pushes a numeric value onto the top of the stack.
     *
     * @param value the numeric value to push
     */
    void push(double value) {
        pushValue(String.valueOf(value));
    }

    private void pushValue(String value) {
        Nodo node = new Nodo(value);
        node.setNext(top);
        top = node;
        size++;
    }

    /**
     * Removes and returns the character at the top of the stack.
     *
     * @return the character at the top of the stack
     * @throws IllegalStateException if the stack is empty
     */
    char pop() {
        if (isEmpty()) {
            System.out.println("La pila está vacía.");
            return '\0';
        }

        char value = top.getValue().charAt(0);
        top = top.getNext();
        size--;
        return value;
    }

    /**
     * Removes and returns the numeric value at the top of the stack.
     *
     * @return the numeric value at the top of the stack
     */
    double popDouble() {
        if (isEmpty()) {
            System.out.println("La pila está vacía.");
            return 0.0d;
        }

        double value = Double.parseDouble(top.getValue());
        top = top.getNext();
        size--;
        return value;
    }

    /**
     * Returns the character at the top of the stack without removing it.
     *
     * @return the character at the top of the stack
     * @throws IllegalStateException if the stack is empty
     */
    char peek() {
        if (isEmpty()) {
            System.out.println("La pila está vacía.");
            return '\0';
        }
        return top.getValue().charAt(0);
    }

    /**
     * Returns whether the stack has no elements.
     *
     * @return {@code true} if the stack is empty, {@code false} otherwise
     */
    boolean isEmpty() {
        return top == null;
    }

    /**
     * Returns the number of elements in the stack.
     *
     * @return the current stack size
     */
    int size() {
        return size;
    }

    /**
     * Removes all elements from the stack.
     */
    void clear() {
        top = null;
        size = 0;
    }

    /**
     * Returns a string representation of the current stack contents
     * from top to bottom, useful for tracing and debugging.
     * <p>
     * Example output: {@code [ + -> * -> ( ]}
     *
     * @return a snapshot string, or {@code [empty]} if the stack is empty
     */
    String snapshot() {
        if (isEmpty()) {
            return "[vacía]";
        }

        StringBuilder builder = new StringBuilder();
        Nodo current = top;
        while (current != null) {
            if (builder.length() > 0) {
                builder.append(" -> ");
            }
            builder.append(current.getValue());
            current = current.getNext();
        }
        return builder.toString();
    }
}
