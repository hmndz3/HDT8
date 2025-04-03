import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaz gráfica para el sistema de atención de emergencias
 */
public class EmergenciasGUI extends JFrame {
    
    private SistemaEmergencias sistema;
    private JTextArea areaPacientes;
    private JButton btnAtender;
    private JButton btnCargarArchivo;
    private JButton btnAgregarPaciente;
    private JTextField txtNombre;
    private JTextField txtSintoma;
    private JComboBox<String> cmbPrioridad;
    private JLabel lblProximoPaciente;
    private JLabel lblPacientesEsperando;
    private List<Paciente> pacientesAtendidos;
    
    /**
     * Constructor de la interfaz gráfica
     */
    public EmergenciasGUI() {
        // Inicializar con VectorHeap (true) o Java PriorityQueue (false)
        sistema = new SistemaEmergencias(true);
        pacientesAtendidos = new ArrayList<>();
        
        // Configurar la ventana principal
        setTitle("Sistema de Atención de Emergencias");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior para información y acciones
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        
        // Panel para información de pacientes
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        lblProximoPaciente = new JLabel("Próximo paciente: ninguno");
        lblPacientesEsperando = new JLabel("Pacientes esperando: 0");
        panelInfo.add(lblProximoPaciente);
        panelInfo.add(lblPacientesEsperando);
        
        // Panel para botones de acción
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCargarArchivo = new JButton("Cargar Archivo");
        btnAtender = new JButton("Atender Paciente");
        btnAtender.setEnabled(false);
        
        panelAcciones.add(btnCargarArchivo);
        panelAcciones.add(btnAtender);
        
        panelSuperior.add(panelInfo, BorderLayout.WEST);
        panelSuperior.add(panelAcciones, BorderLayout.EAST);
        
        // Panel central para mostrar los pacientes
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Pacientes atendidos"));
        
        areaPacientes = new JTextArea();
        areaPacientes.setEditable(false);
        JScrollPane scrollArea = new JScrollPane(areaPacientes);
        
        panelCentral.add(scrollArea, BorderLayout.CENTER);
        
        // Panel inferior para agregar pacientes
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Agregar paciente"));
        
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);
        
        panelFormulario.add(new JLabel("Síntoma:"));
        txtSintoma = new JTextField();
        panelFormulario.add(txtSintoma);
        
        panelFormulario.add(new JLabel("Prioridad:"));
        cmbPrioridad = new JComboBox<>(new String[] {
            "A - Muy urgente (vida o muerte)",
            "B - Urgente",
            "C - Poco urgente",
            "D - Normal",
            "E - No urgente"
        });
        panelFormulario.add(cmbPrioridad);
        
        JPanel panelBotonAgregar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAgregarPaciente = new JButton("Agregar Paciente");
        panelBotonAgregar.add(btnAgregarPaciente);
        
        panelInferior.add(panelFormulario, BorderLayout.CENTER);
        panelInferior.add(panelBotonAgregar, BorderLayout.SOUTH);
        
        // Agregar los paneles al panel principal
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        // Añadir el panel principal a la ventana
        add(panelPrincipal);
        
        // Configurar eventos
        configurarEventos();
    }
    
    /**
     * Configura los eventos de los botones
     */
    private void configurarEventos() {
        // Evento para cargar archivo
        btnCargarArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(EmergenciasGUI.this);
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        sistema.cargarPacientesDesdeArchivo(fileChooser.getSelectedFile().getAbsolutePath());
                        actualizarInformacion();
                        JOptionPane.showMessageDialog(EmergenciasGUI.this, 
                            "Pacientes cargados correctamente", 
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(EmergenciasGUI.this, 
                            "Error al cargar el archivo: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        // Evento para atender paciente
        btnAtender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Paciente paciente = sistema.atenderSiguientePaciente();
                if (paciente != null) {
                    pacientesAtendidos.add(paciente);
                    actualizarListaPacientesAtendidos();
                    actualizarInformacion();
                }
            }
        });
        
        // Evento para agregar paciente
        btnAgregarPaciente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombre.getText().trim();
                String sintoma = txtSintoma.getText().trim();
                String prioridadSeleccionada = (String) cmbPrioridad.getSelectedItem();
                char codigoEmergencia = prioridadSeleccionada.charAt(0);
                
                if (nombre.isEmpty() || sintoma.isEmpty()) {
                    JOptionPane.showMessageDialog(EmergenciasGUI.this,
                        "Por favor, complete todos los campos",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Paciente nuevoPaciente = new Paciente(nombre, sintoma, codigoEmergencia);
                sistema.agregarPaciente(nuevoPaciente);
                
                // Limpiar campos
                txtNombre.setText("");
                txtSintoma.setText("");
                cmbPrioridad.setSelectedIndex(0);
                
                actualizarInformacion();
                JOptionPane.showMessageDialog(EmergenciasGUI.this,
                    "Paciente agregado correctamente",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    /**
     * Actualiza la información mostrada en la interfaz
     */
    private void actualizarInformacion() {
        int pacientesEsperando = sistema.numeroPacientesEsperando();
        Paciente proximoPaciente = sistema.consultarProximoPaciente();
        
        lblPacientesEsperando.setText("Pacientes esperando: " + pacientesEsperando);
        
        if (proximoPaciente != null) {
            lblProximoPaciente.setText("Próximo paciente: " + proximoPaciente.toString());
            btnAtender.setEnabled(true);
        } else {
            lblProximoPaciente.setText("Próximo paciente: ninguno");
            btnAtender.setEnabled(false);
        }
    }
    
    /**
     * Actualiza la lista de pacientes atendidos en el área de texto
     */
    private void actualizarListaPacientesAtendidos() {
        StringBuilder sb = new StringBuilder();
        sb.append("PACIENTES ATENDIDOS (").append(pacientesAtendidos.size()).append("):\n\n");
        
        for (int i = 0; i < pacientesAtendidos.size(); i++) {
            sb.append(i + 1).append(". ").append(pacientesAtendidos.get(i).toString()).append("\n");
        }
        
        areaPacientes.setText(sb.toString());
    }
    
    /**
     * Método principal para iniciar la aplicación
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configurar el aspecto similar al sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar la interfaz gráfica
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EmergenciasGUI gui = new EmergenciasGUI();
                gui.setVisible(true);
            }
        });
    }
}