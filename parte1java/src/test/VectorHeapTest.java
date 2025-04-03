import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase VectorHeap
 */
public class VectorHeapTest {
    
    private VectorHeap<Integer> heap;
    
    @BeforeEach
    public void setUp() {
        heap = new VectorHeap<>();
    }
    
    @Test
    public void testAddAndPeek() {
        // Verificar que la cola esté vacía inicialmente
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        
        // Agregar elementos
        heap.add(5);
        heap.add(3);
        heap.add(7);
        
        // Verificar el tamaño
        assertEquals(3, heap.size());
        assertFalse(heap.isEmpty());
        
        // Verificar que peek devuelve el menor elemento (mayor prioridad) sin eliminarlo
        assertEquals(Integer.valueOf(3), heap.peek());
        assertEquals(3, heap.size());
    }
    
    @Test
    public void testRemove() {
        // Agregar elementos
        heap.add(5);
        heap.add(3);
        heap.add(7);
        heap.add(1);
        heap.add(9);
        
        // Verificar que los elementos se eliminan en orden de prioridad
        assertEquals(Integer.valueOf(1), heap.remove());
        assertEquals(Integer.valueOf(3), heap.remove());
        assertEquals(Integer.valueOf(5), heap.remove());
        assertEquals(Integer.valueOf(7), heap.remove());
        assertEquals(Integer.valueOf(9), heap.remove());
        
        // Verificar que la cola esté vacía después de eliminar todos los elementos
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }
    
    @Test
    public void testRemoveEmpty() {
        // Verificar que se lanza una excepción al intentar eliminar de una cola vacía
        assertThrows(IllegalStateException.class, () -> {
            heap.remove();
        });
    }
    
    @Test
    public void testPeekEmpty() {
        // Verificar que se lanza una excepción al intentar consultar una cola vacía
        assertThrows(IllegalStateException.class, () -> {
            heap.peek();
        });
    }
    
    @Test
    public void testAddRemoveAdd() {
        // Agregar elementos
        heap.add(5);
        heap.add(3);
        
        // Eliminar un elemento
        assertEquals(Integer.valueOf(3), heap.remove());
        
        // Agregar más elementos
        heap.add(1);
        heap.add(7);
        
        // Verificar el orden de eliminación
        assertEquals(Integer.valueOf(1), heap.remove());
        assertEquals(Integer.valueOf(5), heap.remove());
        assertEquals(Integer.valueOf(7), heap.remove());
    }
    
    @Test
    public void testAddSameValue() {
        // Agregar elementos con el mismo valor
        heap.add(5);
        heap.add(5);
        heap.add(5);
        
        // Verificar que se eliminan correctamente
        assertEquals(Integer.valueOf(5), heap.remove());
        assertEquals(Integer.valueOf(5), heap.remove());
        assertEquals(Integer.valueOf(5), heap.remove());
        
        assertTrue(heap.isEmpty());
    }
    
    @Test
    public void testPacientePriority() {
        // Crear una cola de prioridad para pacientes
        VectorHeap<Paciente> pacienteHeap = new VectorHeap<>();
        
        // Crear algunos pacientes con diferentes prioridades
        Paciente p1 = new Paciente("Juan", "Fractura", 'C');
        Paciente p2 = new Paciente("Maria", "Apendicitis", 'A');
        Paciente p3 = new Paciente("Pedro", "Dolor de cabeza", 'E');
        Paciente p4 = new Paciente("Ana", "Dolor de parto", 'B');
        
        // Agregar pacientes a la cola
        pacienteHeap.add(p1);
        pacienteHeap.add(p2);
        pacienteHeap.add(p3);
        pacienteHeap.add(p4);
        
        // Verificar que los pacientes se atienden según su prioridad
        assertEquals("Maria", pacienteHeap.remove().getNombre()); // A (mayor prioridad)
        assertEquals("Ana", pacienteHeap.remove().getNombre());   // B
        assertEquals("Juan", pacienteHeap.remove().getNombre());  // C
        assertEquals("Pedro", pacienteHeap.remove().getNombre()); // E (menor prioridad)
    }
}