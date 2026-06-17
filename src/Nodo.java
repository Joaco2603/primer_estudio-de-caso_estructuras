final class Nodo {
    private final char value;
    private Nodo next;

    Nodo(char value) {
        this.value = value;
    }

    char getValue() {
        return value;
    }

    Nodo getNext() {
        return next;
    }

    void setNext(Nodo next) {
        this.next = next;
    }
}
