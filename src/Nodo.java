/**
 * Represents a single node in a singly linked list used by {@link Pila}.
 * <p>
 * Each node stores a text value and a reference to the next node,
 * forming the backbone of the LIFO stack structure.
 */
final class Nodo {
    private final String value;
    private Nodo next;

    /**
     * Creates a node with the given value.
     *
     * @param value the text to store in this node
     */
    Nodo(String value) {
        this.value = value;
    }

    /**
     * Returns the text stored in this node.
     *
     * @return the text value
     */
    String getValue() {
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
