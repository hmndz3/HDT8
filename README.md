# Sistema de Atención de Emergencias Hospitalarias

Este proyecto implementa un sistema de colas con prioridad para la atención de pacientes en la sala de emergencias de un hospital. El proyecto se divide en dos partes:

1. **Sistema de atención basado en Java**: Implementación del ADT Priority Queue usando VectorHeap.
2. **Simulación con Python SimPy**: Simulación del funcionamiento de una sala de emergencias con diferentes configuraciones de recursos.

## Estructura del Proyecto

```
proyecto-emergencias/
│
├── parte1-java/
│   ├── src/
│   │   ├── Paciente.java
│   │   ├── PriorityQueue.java
│   │   ├── VectorHeap.java
│   │   ├── ColaPrioridadJava.java
│   │   ├── SistemaEmergencias.java
│   │   ├── EmergenciasGUI.java
│   │   └── VectorHeapTest.java
│   └── resources/
│       └── pacientes.txt
│
└── parte2-python/
    ├── emergency_simulation.py
    ├── pacientes.txt
    └── output/
        ├── graficas/
        ├── informes/
```

## Requisitos

### Parte 1 (Java)
- Java JDK 8 o superior
- JUnit 5 (para las pruebas unitarias)

### Parte 2 (Python)
- Python 3.7 o superior
- SimPy
- Pandas
- Matplotlib
- NumPy

## Instalación de Dependencias (Python)

Instala las dependencias necesarias usando pip:

```bash
pip install simpy pandas matplotlib numpy
```

## Ejecución

### Parte 1: Sistema de Java

1. Compilar todas las clases:
```bash
cd parte1-java
javac src/*.java
```

2. Ejecutar la interfaz gráfica:
```bash
cd parte1-java
java -cp src EmergenciasGUI
```

3. Ejecutar las pruebas unitarias (requiere JUnit):
```bash
cd parte1-java
javac -cp .:lib/junit-platform-console-standalone-1.8.2.jar src/VectorHeapTest.java
java -jar lib/junit-platform-console-standalone-1.8.2.jar -cp src --scan-class-path
```

### Parte 2: Simulación en Python

1. Crear las carpetas de salida:
```bash
cd parte2-python
mkdir -p output/graficas output/informes
```

2. Ejecutar la simulación:
```bash
cd parte2-python
python emergency_simulation.py
```

## Problema con generación de gráficas e informes

Si al ejecutar la simulación no se generan las gráficas o el informe en la carpeta `output/`, puede deberse a permisos o problemas con la estructura de directorios. Sigue estos pasos para solucionar el problema:

1. Verifica que la estructura de carpetas existe:
```bash
mkdir -p parte2-python/output/graficas/tiempo_espera
mkdir -p parte2-python/output/graficas/tiempo_por_prioridad  
mkdir -p parte2-python/output/graficas/distribucion_pacientes
mkdir -p parte2-python/output/informes
```

2. Modifica el script `emergency_simulation.py` para usar rutas relativas correctas:

Cambia todas las rutas de guardado por rutas absolutas o verifica las rutas relativas. Por ejemplo:

```python
# En lugar de:
plt.savefig(f'output/graficas/tiempo_espera/tiempo_espera_{nombre_configuracion}.png')

# Usa una ruta como:
import os
ruta_base = os.path.dirname(os.path.abspath(__file__))
ruta_completa = os.path.join(ruta_base, 'output', 'graficas', 'tiempo_espera', f'tiempo_espera_{nombre_configuracion}.png')
plt.savefig(ruta_completa)
```

## Descripción de los archivos

### Parte 1 (Java)
- `Paciente.java`: Clase que representa un paciente con prioridad.
- `PriorityQueue.java`: Interfaz que define las operaciones de una cola con prioridad.
- `VectorHeap.java`: Implementación de la cola con prioridad usando un heap.
- `ColaPrioridadJava.java`: Adaptador para usar la implementación de Java.
- `SistemaEmergencias.java`: Sistema de atención de pacientes.
- `EmergenciasGUI.java`: Interfaz gráfica para el sistema.
- `VectorHeapTest.java`: Pruebas unitarias para la clase VectorHeap.

### Parte 2 (Python)
- `emergency_simulation.py`: Simulación de la sala de emergencias usando SimPy.
- `pacientes.txt`: Datos de ejemplo de pacientes.

## Funcionamiento

### Sistema Java
El sistema permite cargar pacientes desde un archivo de texto, donde cada línea contiene el nombre del paciente, su síntoma y un código de emergencia (A-E, donde A es la mayor prioridad). Los pacientes son atendidos según su prioridad.

### Simulación Python
La simulación modela el funcionamiento de una sala de emergencias con diferentes recursos (enfermeras, doctores, laboratorios, rayos X). Se simulan diferentes configuraciones y tipos de días (normales, fines de semana, festivos) para encontrar la configuración óptima de recursos.

## Fuentes de información

- Tiempos de atención médica: [Hospital Efficiency Study](https://www.audit.vic.gov.au/report/efficiency-and-effectiveness-hospital-services-emergency-care)
- Proceso de atención en emergencias: [Advocate Health](https://www.advocatehealth.com/health-services/emergency-services/what-happens-in-the-emergency-department)
- Costos estimados de personal y equipamiento médico basados en datos de [Medical Economics](https://www.medicaleconomics.com)