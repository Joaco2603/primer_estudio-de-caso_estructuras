/**
 * Represents a single node in a singly linked list used by {@link Pila}.
 * <p>
 * Each node stores a character value and a reference to the next node,
 * forming the backbone of the LIFO stack structure.
 */
final class Nodo {
    private final char value;
    private Nodo next;

    /**
     * Creates a node with the given character value.
     *
     * @param value the character to store in this node
     */
    Nodo(char value) {
        this.value = value;
    }

    /**
     * Returns the character stored in this node.
     *
     * @return the character value
     */
    char getValue() {
        return value;
    }

    /**
     * Returns the next node in the linked list.
     *
     * @return the next node, or {@code null} if this is the last node
     */
    Nodo getNext() {
        return next;
    }

    /**
     * Sets the reference to the next node.
     *
     * @param next the node to link to, or {@code null} to terminate the list
     */
    void setNext(Nodo next) {
        this.next = next;
    }
}
