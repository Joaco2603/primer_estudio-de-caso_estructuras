final class Pila {
    private Nodo top;
    private int size;

    void push(char value) {
        Nodo node = new Nodo(value);
        node.setNext(top);
        top = node;
        size++;
    }

    char pop() {
        if (isEmpty()) {
            throw new IllegalStateException("The stack is empty.");
        }

        char value = top.getValue();
        top = top.getNext();
        size--;
        return value;
    }

    char peek() {
        if (isEmpty()) {
            throw new IllegalStateException("The stack is empty.");
        }
        return top.getValue();
    }

    boolean isEmpty() {
        return top == null;
    }

    int size() {
        return size;
    }

    void clear() {
        top = null;
        size = 0;
    }

    String snapshot() {
        if (isEmpty()) {
            return "[empty]";
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
