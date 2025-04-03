import java.util.Vector;

/**
 * Implementación de una cola con prioridad usando un Heap
 * @param <E> Tipo de elementos que se almacenarán en la cola con prioridad
 *           debe implementar Comparable
 */
public class VectorHeap<E extends Comparable<E>> implements PriorityQueue<E> {
    
    // Vector que almacena los elementos del heap
    protected Vector<E> data;
    
    /**
     * Constructor por defecto
     */
    public VectorHeap() {
        data = new Vector<E>();
    }
    
    /**
     * Constructor que inicializa el heap con un vector existente
     * @param v Vector con los elementos iniciales
     */
    public VectorHeap(Vector<E> v) {
        data = new Vector<E>(v.size());
        for (E elemento : v) {
            add(elemento);
        }
    }
    
    /**
     * Obtiene el índice del padre de un nodo
     * @param i Índice del nodo
     * @return Índice del padre
     */
    protected int parent(int i) {
        return (i - 1) / 2;
    }
    
    /**
     * Obtiene el índice del hijo izquierdo de un nodo
     * @param i Índice del nodo
     * @return Índice del hijo izquierdo
     */
    protected int left(int i) {
        return 2 * i + 1;
    }
    
    /**
     * Obtiene el índice del hijo derecho de un nodo
     * @param i Índice del nodo
     * @return Índice del hijo derecho
     */
    protected int right(int i) {
        return 2 * i + 2;
    }
    
    /**
     * Mueve un elemento hacia arriba en el heap hasta que se satisface
     * la propiedad de ordenación
     * @param leaf Índice del nodo a mover
     */
    protected void percolateUp(int leaf) {
        int parent = parent(leaf);
        E value = data.get(leaf);
        
        while (leaf > 0 && value.compareTo(data.get(parent)) < 0) {
            data.set(leaf, data.get(parent));
            leaf = parent;
            parent = parent(leaf);
        }
        
        data.set(leaf, value);
    }
    
    /**
     * Mueve un elemento hacia abajo en el heap hasta que se satisface
     * la propiedad de ordenación
     * @param root Índice del nodo a mover
     */
    protected void pushDownRoot(int root) {
        int heapSize = data.size();
        E value = data.get(root);
        while (root < heapSize) {
            int childPos = left(root);
            if (childPos < heapSize) {
                // Si hay hijo derecho y tiene mayor prioridad que el izquierdo
                if (right(root) < heapSize && 
                    data.get(childPos + 1).compareTo(data.get(childPos)) < 0) {
                    childPos++;
                }
                // Si el hijo tiene mayor prioridad que el elemento actual
                if (data.get(childPos).compareTo(value) < 0) {
                    data.set(root, data.get(childPos));
                    root = childPos;
                } else {
                    data.set(root, value);
                    return;
                }
            } else {
                data.set(root, value);
                return;
            }
        }
    }
    
    /**
     * Añade un elemento a la cola con prioridad
     * @param elemento El elemento a añadir
     */
    @Override
    public void add(E elemento) {
        data.add(elemento);
        percolateUp(data.size() - 1);
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
        
        E minValue = data.get(0);
        data.set(0, data.get(data.size() - 1));
        data.setSize(data.size() - 1);
        
        if (data.size() > 0) {
            pushDownRoot(0);
        }
        
        return minValue;
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
        
        return data.get(0);
    }
    
    /**
     * Verifica si la cola está vacía
     * @return true si la cola está vacía, false en caso contrario
     */
    @Override
    public boolean isEmpty() {
        return data.size() == 0;
    }
    
    /**
     * Obtiene el número de elementos en la cola
     * @return número de elementos
     */
    @Override
    public int size() {
        return data.size();
    }
    
    /**
     * Devuelve una representación en texto del heap
     * @return representación en texto del heap
     */
    @Override
    public String toString() {
        return data.toString();
    }
}