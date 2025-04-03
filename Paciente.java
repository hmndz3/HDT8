/**
 * Clase que representa a un paciente en la sala de emergencias
 */
public class Paciente implements Comparable<Paciente> {
    private String nombre;
    private String sintoma;
    private char codigoEmergencia;
    
    /**
     * Constructor para crear un paciente
     * @param nombre Nombre del paciente
     * @param sintoma Descripción del síntoma
     * @param codigoEmergencia Código de A a E (A=más urgente, E=menos urgente)
     */
    public Paciente(String nombre, String sintoma, char codigoEmergencia) {
        this.nombre = nombre;
        this.sintoma = sintoma;
        this.codigoEmergencia = Character.toUpperCase(codigoEmergencia);
    }
    
    /**
     * Obtiene el nombre del paciente
     * @return nombre del paciente
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Obtiene el síntoma del paciente
     * @return síntoma del paciente
     */
    public String getSintoma() {
        return sintoma;
    }
    
    /**
     * Obtiene el código de emergencia del paciente
     * @return código de emergencia (A-E)
     */
    public char getCodigoEmergencia() {
        return codigoEmergencia;
    }
    
    /**
     * Método de comparación para determinar la prioridad entre pacientes
     * Pacientes con código A tienen mayor prioridad que B, B mayor que C, etc.
     * @param otro El otro paciente a comparar
     * @return -1 si este paciente tiene mayor prioridad, 1 si tiene menor prioridad, 
     *         0 si tienen la misma prioridad
     */
    @Override
    public int compareTo(Paciente otro) {
        return Character.compare(this.codigoEmergencia, otro.codigoEmergencia);
    }
    
    /**
     * Representación en texto del paciente
     * @return cadena con el formato "nombre, síntoma, código"
     */
    @Override
    public String toString() {
        return nombre + ", " + sintoma + ", " + codigoEmergencia;
    }
}