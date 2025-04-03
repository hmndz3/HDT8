/**
 * Interfaz que define las operaciones para una cola con prioridad
 * @param <E> Tipo de elementos que se almacenarán en la cola con prioridad
 *           debe implementar Comparable
 */
public interface PriorityQueue<E extends Comparable<E>> {
    
    /**
     * Añade un elemento a la cola con prioridad
     * @param elemento El elemento a añadir
     */
    void add(E elemento);
    
    /**
     * Obtiene y elimina el elemento con mayor prioridad
     * @return El elemento con mayor prioridad
     * @throws IllegalStateException si la cola está vacía
     */
    E remove();
    
    /**
     * Obtiene pero no elimina el elemento con mayor prioridad
     * @return El elemento con mayor prioridad
     * @throws IllegalStateException si la cola está vacía
     */
    E peek();
    
    /**
     * Verifica si la cola está vacía
     * @return true si la cola está vacía, false en caso contrario
     */
    boolean isEmpty();
    
    /**
     * Obtiene el número de elementos en la cola
     * @return número de elementos
     */
    int size();
}