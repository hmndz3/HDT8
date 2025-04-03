import java.util.PriorityQueue;

/**
 * Adaptador de Java PriorityQueue para que implemente nuestra interfaz PriorityQueue
 * @param <E> Tipo de elementos que se almacenarán en la cola con prioridad
 */
public class ColaPrioridadJava<E extends Comparable<E>> implements PriorityQueue<E> {
    
    // Cola con prioridad de Java
    private java.util.PriorityQueue<E> cola;
    
    /**
     * Constructor por defecto
     */
    public ColaPrioridadJava() {
        cola = new java.util.PriorityQueue<>();
    }
    
    /**
     * Añade un elemento a la cola con prioridad
     * @param elemento El elemento a añadir
     */
    @Override
    public void add(E elemento) {
        cola.add(elemento);
    }
    
    /**
     * Obtiene y elimina el elemento con mayor prioridad
     * @return El elemento con mayor prioridad
     * @throws IllegalStateException si la cola está vacía
     */
    @Override
    public E remove() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return cola.poll();
    }
    
    /**
     * Obtiene pero no elimina el elemento con mayor prioridad
     * @return El elemento con mayor prioridad
     * @throws IllegalStateException si la cola está vacía
     */
    @Override
    public E peek() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return cola.peek();
    }
    
    /**
     * Verifica si la cola está vacía
     * @return true si la cola está vacía, false en caso contrario
     */
    @Override
    public boolean isEmpty() {
        return cola.isEmpty();
    }
    
    /**
     * Obtiene el número de elementos en la cola
     * @return número de elementos
     */
    @Override
    public int size() {
        return cola.size();
    }
}