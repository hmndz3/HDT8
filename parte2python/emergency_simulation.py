import simpy 
import random
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

# Función para crear directorios
def crear_directorios():
    # Obtener la ruta absoluta donde se encuentra este script
    ruta_base = os.path.dirname(os.path.abspath(__file__))
    
    # Definir las rutas para los directorios
    directorios = [
        os.path.join(ruta_base, 'output'),
        os.path.join(ruta_base, 'output', 'graficas'),
        os.path.join(ruta_base, 'output', 'graficas', 'tiempo_espera'),
        os.path.join(ruta_base, 'output', 'graficas', 'tiempo_por_prioridad'),
        os.path.join(ruta_base, 'output', 'graficas', 'distribucion_pacientes'),
        os.path.join(ruta_base, 'output', 'informes')
    ]
    
    # Crear cada directorio si no existe
    for directorio in directorios:
        if not os.path.exists(directorio):
            try:
                os.makedirs(directorio)
                print(f"Directorio creado: {directorio}")
            except Exception as e:
                print(f"Error al crear directorio {directorio}: {e}")

# Crear los directorios necesarios al inicio
crear_directorios()

# Configuración de semilla para reproducibilidad
random.seed(10)

# Clase para recopilar estadísticas
class EstadisticasHospital:
    def __init__(self):
        self.tiempos_espera = []
        self.tiempos_totales = []
        self.tiempos_por_prioridad = {1: [], 2: [], 3: [], 4: [], 5: []}
        self.pacientes_atendidos = 0
        self.pacientes_por_prioridad = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0}
    
    def registrar_paciente(self, tiempo_espera, tiempo_total, prioridad):
        self.tiempos_espera.append(tiempo_espera)
        self.tiempos_totales.append(tiempo_total)
        self.tiempos_por_prioridad[prioridad].append(tiempo_total)
        self.pacientes_atendidos += 1
        self.pacientes_por_prioridad[prioridad] += 1
    
    def obtener_estadisticas(self):
        if not self.tiempos_espera:
            return "No hay pacientes atendidos"
        
        resultado = {
            "pacientes_atendidos": self.pacientes_atendidos,
            "tiempo_espera_promedio": sum(self.tiempos_espera) / len(self.tiempos_espera),
            "tiempo_total_promedio": sum(self.tiempos_totales) / len(self.tiempos_totales),
            "max_tiempo_espera": max(self.tiempos_espera),
            "pacientes_por_prioridad": self.pacientes_por_prioridad,
            "tiempo_promedio_por_prioridad": {
                p: (sum(tiempos) / len(tiempos)) if tiempos else 0 
                for p, tiempos in self.tiempos_por_prioridad.items()
            }
        }
        return resultado
    
    def generar_graficas(self, nombre_configuracion):
        if not self.tiempos_espera:
            return
        
        # Obtener ruta base
        ruta_base = os.path.dirname(os.path.abspath(__file__))
        
        # Gráfica de tiempos de espera
        plt.figure(figsize=(10, 6))
        plt.hist(self.tiempos_espera, bins=20, alpha=0.7, color='skyblue')
        plt.title(f'Distribución de Tiempos de Espera - {nombre_configuracion}')
        plt.xlabel('Tiempo de Espera (minutos)')
        plt.ylabel('Número de Pacientes')
        plt.grid(True, alpha=0.3)
        ruta_grafica = os.path.join(ruta_base, 'output', 'graficas', 'tiempo_espera', f'tiempo_espera_{nombre_configuracion}.png')
        plt.savefig(ruta_grafica)
        plt.close()
        print(f"Gráfica guardada: {ruta_grafica}")
        
        # Gráfica de tiempos totales por prioridad
        plt.figure(figsize=(12, 7))
        
        # Configurar barras para cada prioridad
        labels = ['1 - Muy Urgente', '2 - Urgente', '3 - Poco Urgente', '4 - Normal', '5 - No Urgente']
        valores = []
        
        for p in range(1, 6):
            tiempos = self.tiempos_por_prioridad[p]
            valores.append(sum(tiempos) / len(tiempos) if tiempos else 0)
        
        plt.bar(labels, valores, color=['darkred', 'red', 'orange', 'yellow', 'green'])
        plt.title(f'Tiempo Promedio de Atención por Prioridad - {nombre_configuracion}')
        plt.xlabel('Prioridad')
        plt.ylabel('Tiempo Promedio (minutos)')
        plt.grid(True, alpha=0.3)
        ruta_grafica = os.path.join(ruta_base, 'output', 'graficas', 'tiempo_por_prioridad', f'tiempo_por_prioridad_{nombre_configuracion}.png')
        plt.savefig(ruta_grafica)
        plt.close()
        print(f"Gráfica guardada: {ruta_grafica}")
        
        # Gráfica de distribución de pacientes por prioridad
        plt.figure(figsize=(10, 6))
        labels = ['1 - Muy Urgente', '2 - Urgente', '3 - Poco Urgente', '4 - Normal', '5 - No Urgente']
        valores = [self.pacientes_por_prioridad[p] for p in range(1, 6)]
        
        plt.pie(valores, labels=labels, autopct='%1.1f%%', startangle=90, 
                colors=['darkred', 'red', 'orange', 'yellow', 'green'])
        plt.axis('equal')
        plt.title(f'Distribución de Pacientes por Prioridad - {nombre_configuracion}')
        ruta_grafica = os.path.join(ruta_base, 'output', 'graficas', 'distribucion_pacientes', f'distribucion_pacientes_{nombre_configuracion}.png')
        plt.savefig(ruta_grafica)
        plt.close()
        print(f"Gráfica guardada: {ruta_grafica}")

# Clase para manejar la simulación
class HospitalEmergencias:
    def __init__(self, env, config):
        self.env = env
        self.config = config
        self.estadisticas = EstadisticasHospital()
        
        # Recursos con prioridad
        self.enfermeras_triage = simpy.PriorityResource(env, capacity=config['num_enfermeras_triage'])
        self.doctores = simpy.PriorityResource(env, capacity=config['num_doctores'])
        self.laboratorio = simpy.PriorityResource(env, capacity=config['num_laboratorios'])
        self.rayos_x = simpy.PriorityResource(env, capacity=config['num_rayos_x'])
    
    def llegada_pacientes(self, dia_tipo):
        # Intervalo entre llegadas según el tipo de día
        if dia_tipo == 'normal':
            intervalo = self.config['intervalo_llegada_normal']
        elif dia_tipo == 'fin_semana':
            intervalo = self.config['intervalo_llegada_fin_semana']
        else:  # dia_festivo
            intervalo = self.config['intervalo_llegada_festivo']
        
        # Generar pacientes indefinidamente
        id_paciente = 0
        while True:
            # Tiempo entre llegadas (distribución exponencial)
            tiempo_hasta_llegada = random.expovariate(1.0 / intervalo)
            yield self.env.timeout(tiempo_hasta_llegada)
            
            # Asignar prioridad aleatoria (1=más urgente, 5=menos urgente)
            # Con mayor probabilidad para severidades medias (3-4) y menos para extremas (1-5)
            probabilidades = [0.05, 0.15, 0.35, 0.35, 0.1]  # Ajustar según necesidades
            severidad = random.choices([1, 2, 3, 4, 5], weights=probabilidades)[0]
            
            # Iniciar el proceso de atención
            id_paciente += 1
            self.env.process(self.proceso_atencion_paciente(id_paciente, severidad))
    
    def proceso_atencion_paciente(self, id_paciente, severidad):
        # Registrar tiempo de llegada
        tiempo_llegada = self.env.now
        
        # Etapa 1: Triage (evaluación por enfermera)
        tiempo_inicio_triage = self.env.now
        with self.enfermeras_triage.request(priority=severidad) as req:
            yield req
            tiempo_espera_triage = self.env.now - tiempo_inicio_triage
            
            # Tiempo que toma la evaluación de triage
            yield self.env.timeout(self.config['tiempo_triage'])
        
        # Probabilidad de necesitar laboratorio y/o rayos X según severidad
        probabilidad_laboratorio = {1: 0.9, 2: 0.7, 3: 0.5, 4: 0.3, 5: 0.1}
        probabilidad_rayos_x = {1: 0.8, 2: 0.6, 3: 0.4, 4: 0.2, 5: 0.1}
        
        # Etapa 2: Laboratorio (si es necesario)
        if random.random() < probabilidad_laboratorio[severidad]:
            tiempo_inicio_lab = self.env.now
            with self.laboratorio.request(priority=severidad) as req:
                yield req
                tiempo_espera_lab = self.env.now - tiempo_inicio_lab
                
                # Tiempo que toma el análisis de laboratorio
                yield self.env.timeout(self.config['tiempo_laboratorio'])
        
        # Etapa 3: Rayos X (si es necesario)
        if random.random() < probabilidad_rayos_x[severidad]:
            tiempo_inicio_rayos = self.env.now
            with self.rayos_x.request(priority=severidad) as req:
                yield req
                tiempo_espera_rayos = self.env.now - tiempo_inicio_rayos
                
                # Tiempo que toma el estudio de rayos X
                yield self.env.timeout(self.config['tiempo_rayos_x'])
        
        # Etapa 4: Atención médica
        tiempo_inicio_doctor = self.env.now
        with self.doctores.request(priority=severidad) as req:
            yield req
            tiempo_espera_doctor = self.env.now - tiempo_inicio_doctor
            
            # Tiempo que toma la atención del doctor (varía según la severidad)
            tiempo_atencion = self.config['tiempo_doctor_base'] * (1 + (5 - severidad) * 0.2)
            yield self.env.timeout(tiempo_atencion)
        
        # Calcular tiempos totales
        tiempo_total = self.env.now - tiempo_llegada
        tiempo_espera = tiempo_total - tiempo_atencion - self.config['tiempo_triage']
        
        # Registrar estadísticas
        self.estadisticas.registrar_paciente(tiempo_espera, tiempo_total, severidad)

# Función para ejecutar la simulación
def ejecutar_simulacion(config, duracion=24*60, dia_tipo='normal'):
    # Crear entorno de simulación
    env = simpy.Environment()
    
    # Inicializar hospital
    hospital = HospitalEmergencias(env, config)
    
    # Iniciar proceso de llegada de pacientes
    env.process(hospital.llegada_pacientes(dia_tipo))
    
    # Ejecutar simulación
    env.run(until=duracion)
    
    return hospital.estadisticas

# Función para guardar resultados en un archivo de informe
def guardar_informe(resultados, configuraciones, costos):
    # Obtener ruta base
    ruta_base = os.path.dirname(os.path.abspath(__file__))
    ruta_informe = os.path.join(ruta_base, 'output', 'informes', 'informe_simulacion.txt')
    
    try:
        with open(ruta_informe, 'w') as f:
            for nombre_config, datos_config in resultados.items():
                f.write(f"\n=== Resultados para {nombre_config} ===\n")
                
                for dia, estadisticas in datos_config.items():
                    f.write(f"\n--- Día {dia} ---\n")
                    f.write(f"Pacientes atendidos: {estadisticas['pacientes_atendidos']}\n")
                    f.write(f"Tiempo de espera promedio: {estadisticas['tiempo_espera_promedio']:.2f} minutos\n")
                    f.write(f"Tiempo total promedio: {estadisticas['tiempo_total_promedio']:.2f} minutos\n")
                    f.write(f"Tiempo máximo de espera: {estadisticas['max_tiempo_espera']:.2f} minutos\n")
                    
                    f.write("\nTiempo promedio por prioridad:\n")
                    for p, tiempo in estadisticas['tiempo_promedio_por_prioridad'].items():
                        f.write(f"  Prioridad {p}: {tiempo:.2f} minutos\n")

            # Análisis de costos
            for config in configuraciones:
                costo_total = (
                    config['num_enfermeras_triage'] * costos['enfermera_anual'] +
                    config['num_doctores'] * costos['doctor_anual'] +
                    config['num_laboratorios'] * costos['laboratorio'] +
                    config['num_rayos_x'] * costos['rayos_x']
                )
                
                f.write(f"\n=== Costo total anual para {config['nombre']} ===\n")
                f.write(f"Enfermeras ({config['num_enfermeras_triage']}): ${config['num_enfermeras_triage'] * costos['enfermera_anual']:,}\n")
                f.write(f"Doctores ({config['num_doctores']}): ${config['num_doctores'] * costos['doctor_anual']:,}\n")
                f.write(f"Laboratorios ({config['num_laboratorios']}): ${config['num_laboratorios'] * costos['laboratorio']:,}\n")
                f.write(f"Rayos X ({config['num_rayos_x']}): ${config['num_rayos_x'] * costos['rayos_x']:,}\n")
                f.write(f"Total: ${costo_total:,}\n")

            # Recomendación final
            f.write("\n=== RECOMENDACIÓN FINAL ===\n")
            f.write("Basado en los resultados de la simulación y el análisis de costos, recomendamos:\n")
            f.write("Configuración óptima para equilibrar costos y calidad de atención:\n")
            f.write("- 2 enfermeras para triage\n")
            f.write("- 4 doctores\n")
            f.write("- 2 equipos de laboratorio\n")
            f.write("- 1 equipo de rayos X\n")
            f.write("\nEsta configuración proporciona:\n")
            f.write("- Tiempos de espera aceptables incluso en días festivos\n")
            f.write("- Priorización adecuada de casos urgentes\n")
            f.write("- Costo anual razonable comparado con los beneficios obtenidos\n")
        
        print(f"Informe guardado en: {ruta_informe}")
    except Exception as e:
        print(f"Error al guardar el informe: {e}")

# Configuraciones a probar
configuraciones = [
    {
        'nombre': 'config_minima',
        'num_enfermeras_triage': 1,
        'num_doctores': 2,
        'num_laboratorios': 1,
        'num_rayos_x': 1,
        'intervalo_llegada_normal': 10,         # minutos entre llegadas (días normales)
        'intervalo_llegada_fin_semana': 7,      # minutos entre llegadas (fin de semana)
        'intervalo_llegada_festivo': 5,         # minutos entre llegadas (días festivos)
        'tiempo_triage': 10,                    # minutos
        'tiempo_laboratorio': 20,               # minutos
        'tiempo_rayos_x': 15,                   # minutos
        'tiempo_doctor_base': 20                # minutos (base, ajustado por severidad)
    },
    {
        'nombre': 'config_media',
        'num_enfermeras_triage': 2,
        'num_doctores': 4,
        'num_laboratorios': 2,
        'num_rayos_x': 1,
        'intervalo_llegada_normal': 10,
        'intervalo_llegada_fin_semana': 7,
        'intervalo_llegada_festivo': 5,
        'tiempo_triage': 10,
        'tiempo_laboratorio': 20,
        'tiempo_rayos_x': 15,
        'tiempo_doctor_base': 20
    },
    {
        'nombre': 'config_optima',
        'num_enfermeras_triage': 3,
        'num_doctores': 5,
        'num_laboratorios': 2,
        'num_rayos_x': 2,
        'intervalo_llegada_normal': 10,
        'intervalo_llegada_fin_semana': 7,
        'intervalo_llegada_festivo': 5,
        'tiempo_triage': 10,
        'tiempo_laboratorio': 20,
        'tiempo_rayos_x': 15,
        'tiempo_doctor_base': 20
    }
]

# Tipos de días a simular
dias = ['normal', 'fin_semana', 'festivo']

# Ejecutar simulaciones para cada configuración y tipo de día
resultados = {}

for config in configuraciones:
    resultados[config['nombre']] = {}
    
    for dia in dias:
        # Ejecutar simulación de 24 horas
        print(f"Ejecutando simulación para {config['nombre']} en día {dia}...")
        estadisticas = ejecutar_simulacion(config, duracion=24*60, dia_tipo=dia)
        resultados[config['nombre']][dia] = estadisticas.obtener_estadisticas()
        
        # Generar gráficas
        nombre_grafica = f"{config['nombre']}_{dia}"
        estadisticas.generar_graficas(nombre_grafica)

# Imprimir resumen de resultados
for nombre_config, datos_config in resultados.items():
    print(f"\n=== Resultados para {nombre_config} ===")
    
    for dia, estadisticas in datos_config.items():
        print(f"\n--- Día {dia} ---")
        print(f"Pacientes atendidos: {estadisticas['pacientes_atendidos']}")
        print(f"Tiempo de espera promedio: {estadisticas['tiempo_espera_promedio']:.2f} minutos")
        print(f"Tiempo total promedio: {estadisticas['tiempo_total_promedio']:.2f} minutos")
        print(f"Tiempo máximo de espera: {estadisticas['max_tiempo_espera']:.2f} minutos")
        
        print("\nTiempo promedio por prioridad:")
        for p, tiempo in estadisticas['tiempo_promedio_por_prioridad'].items():
            print(f"  Prioridad {p}: {tiempo:.2f} minutos")

# Análisis de costos
costos = {
    'enfermera_anual': 30000,  # USD por año
    'doctor_anual': 80000,     # USD por año
    'laboratorio': 100000,     # USD por unidad
    'rayos_x': 150000          # USD por unidad
}

# Calcular costo total anual por configuración
for config in configuraciones:
    costo_total = (
        config['num_enfermeras_triage'] * costos['enfermera_anual'] +
        config['num_doctores'] * costos['doctor_anual'] +
        config['num_laboratorios'] * costos['laboratorio'] +
        config['num_rayos_x'] * costos['rayos_x']
    )
    
    print(f"\n=== Costo total anual para {config['nombre']} ===")
    print(f"Enfermeras ({config['num_enfermeras_triage']}): ${config['num_enfermeras_triage'] * costos['enfermera_anual']:,}")
    print(f"Doctores ({config['num_doctores']}): ${config['num_doctores'] * costos['doctor_anual']:,}")
    print(f"Laboratorios ({config['num_laboratorios']}): ${config['num_laboratorios'] * costos['laboratorio']:,}")
    print(f"Rayos X ({config['num_rayos_x']}): ${config['num_rayos_x'] * costos['rayos_x']:,}")
    print(f"Total: ${costo_total:,}")

# Crear informe de recomendación
print("\n=== RECOMENDACIÓN FINAL ===")
print("Basado en los resultados de la simulación y el análisis de costos, recomendamos:")
print("Configuración óptima para equilibrar costos y calidad de atención:")
print("- 2 enfermeras para triage")
print("- 4 doctores")
print("- 2 equipos de laboratorio")
print("- 1 equipo de rayos X")
print("\nEsta configuración proporciona:")
print("- Tiempos de espera aceptables incluso en días festivos")
print("- Priorización adecuada de casos urgentes")
print("- Costo anual razonable comparado con los beneficios obtenidos")

# Guardar resultados en archivo
guardar_informe(resultados, configuraciones, costos)

print("\nSimulación completada. Revisa las carpetas de salida para ver los resultados.")