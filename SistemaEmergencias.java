import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Sistema de atención de pacientes en emergencias usando VectorHeap
 */
public class SistemaEmergencias {
    
    private PriorityQueue<Paciente> colaPacientes;
    
    /**
     * Constructor que inicializa el sistema con un tipo de cola con prioridad
     * @param usarVectorHeap true para usar VectorHeap, false para usar Java PriorityQueue
     */
    public SistemaEmergencias(boolean usarVectorHeap) {
        if (usarVectorHeap) {
            colaPacientes = new VectorHeap<>();
        } else {
            colaPacientes = new ColaPrioridadJava<>();
        }
    }
    
    /**
     * Carga pacientes desde un archivo de texto
     * @param rutaArchivo ruta del archivo de pacientes
     * @throws IOException si hay problemas al leer el archivo
     */
    public void cargarPacientesDesdeArchivo(String rutaArchivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Dividir la línea en campos
                String[] campos = linea.split(",");
                if (campos.length >= 3) {
                    String nombre = campos[0].trim();
                    String sintoma = campos[1].trim();
                    char codigoEmergencia = campos[2].trim().charAt(0);
                    
                    // Crear y añadir el paciente a la cola
                    Paciente paciente = new Paciente(nombre, sintoma, codigoEmergencia);
                    colaPacientes.add(paciente);
                }
            }
        }
    }
    
    /**
     * Atiende al siguiente paciente con mayor prioridad
     * @return el paciente atendido o null si no hay pacientes
     */
    public Paciente atenderSiguientePaciente() {
        if (colaPacientes.isEmpty()) {
            return null;
        }
        return colaPacientes.remove();
    }
    
    /**
     * Verifica si hay pacientes esperando
     * @return true si hay pacientes, false en caso contrario
     */
    public boolean hayPacientesEsperando() {
        return !colaPacientes.isEmpty();
    }
    
    /**
     * Obtiene el número de pacientes en espera
     * @return número de pacientes esperando
     */
    public int numeroPacientesEsperando() {
        return colaPacientes.size();
    }
    
    /**
     * Consulta el próximo paciente a atender sin retirarlo de la cola
     * @return el próximo paciente o null si no hay pacientes
     */
    public Paciente consultarProximoPaciente() {
        if (colaPacientes.isEmpty()) {
            return null;
        }
        return colaPacientes.peek();
    }
    
    /**
     * Agrega un paciente al sistema
     * @param paciente el paciente a agregar
     */
    public void agregarPaciente(Paciente paciente) {
        colaPacientes.add(paciente);
    }
}