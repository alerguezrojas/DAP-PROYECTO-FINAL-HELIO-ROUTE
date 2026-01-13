# MEMORIA TÉCNICA: PROYECTO HELIOROUTE
## Sistema de Análisis de Viabilidad y Meteorología para Alta Montaña

**Autor:** [Tu Nombre]  
**Asignatura:** Desarrollo de Aplicaciones (DAP)  
**Fecha:** Enero 2026  
**Repositorio GitHub:** https://github.com/alerguezrojas/DAP-PROYECTO-FINAL-HELIO-ROUTE

---

## INDICE DE CONTENIDOS

1.  **Introducción y Objetivos**
2.  **Arquitectura del Sistema y Diagramas**
3.  **Análisis Técnico y Patrones de Diseño**
4.  **Funcionalidades del Sistema**
5.  **Integración de Inteligencia Artificial (IA)**
6.  **Gestión del Proyecto: Tiempos y Desviaciones**
7.  **Conclusiones y Líneas Futuras**
8.  **Bibliografía y Referencias**

---

<div style="page-break-after: always;"></div>

## 1. INTRODUCCIÓN Y OBJETIVOS

### 1.1 Contexto
El montañismo y las actividades al aire libre requieren una planificación meticulosa. Factores como la radiación solar, la temperatura, el viento y la visibilidad son determinantes para la seguridad. **HelioRoute** nace como una solución tecnológica para centralizar estos datos, ofreciendo no solo métricas crudas, sino interpretación inteligente (recomendaciones de ropa, alertas de seguridad) basada en la geolocalización.

### 1.2 Objetivos del Proyecto
1.  **Centralización de Datos:** Unificar datos topográficos y meteorológicos en una sola interfaz.
2.  **Arquitectura Robusta:** Implementar un sistema modular basado en microservicios (Backend de cálculo + Frontend web).
3.  **Aplicación de Patrones:** Demostrar el dominio de patrones de diseño software (Strategy, Adapter, Proxy) para resolver problemas complejos de variabilidad de algoritmos y consumo de APIs.
4.  **Experiencia de Usuario (UX):** Ofrecer una interfaz visual, basada en mapas interactivos y capaz de generar informes portables (PDF).

---

## 2. ARQUITECTURA DEL SISTEMA Y DIAGRAMAS

El sistema sigue una arquitectura de **Cliente-Servidor desacoplada**, donde el Frontend (`helio-web-interface`) actúa como consumidor de la API REST expuesta por el Backend (`helio-calculation-service`).

### 2.1 Diagrama de Clases (Backend)
Este diagrama representa la estructura del núcleo de cálculo, destacando el uso de interfaces para la implementación del patrón Strategy en los algoritmos de radiación solar.

> **[ESPACIO RESERVADO PARA CAPTURA: Diagrama de Clases del Paquete 'calculation']**
> *Instrucción: Insertar aquí la captura mostrando `SolarService`, la interfaz `ShadowCalculationStrategy` y sus implementaciones (`Fast` y `Precise`).*

### 2.2 Diagrama de Componentes
Muestra la relación entre el navegador del usuario, el servidor web (Thymeleaf), el servicio de cálculo y la API externa (Open-Meteo).

> **[ESPACIO RESERVADO PARA CAPTURA: Diagrama de Componentes o Arquitectura]**
> *Instrucción: Insertar captura que muestre el flujo: Usuario -> Web Interface -> Calculation Service -> Open-Meteo API.*

---

<div style="page-break-after: always;"></div>

## 3. ANÁLISIS TÉCNICO Y PATRONES DE DISEÑO

El proyecto se ha desarrollado utilizando **Java 21** y **Spring Boot 3.2**, priorizando la mantenibilidad y la escalabilidad. A continuación, se detallan las decisiones técnicas más relevantes.

### 3.1 Patrones de Diseño Implementados (GoF)
Para cumplir con los requisitos de la asignatura de Diseño de Aplicaciones, se han integrado los siguientes patrones:

#### A. Patrón Strategy (Estrategia)
*   **Problema:** El sistema necesitaba soportar diferentes métodos de cálculo solar (uno rápido para estimaciones y uno preciso con trazado de rayos simulado), intercambiables en tiempo de ejecución.
*   **Solución:** Se definió la interfaz `ShadowCalculationStrategy`. Las clases `FastApproximationStrategy` y `PreciseRayTracingStrategy` implementan esta interfaz. El usuario selecciona la estrategia desde el Frontend mediante un radio-button.

#### B. Patrón Adapter (Adaptador)
*   **Problema:** La API externa de Open-Meteo devuelve datos en un formato JSON complejo que no coincide con las entidades de dominio interno (`EnvironmentalData`).
*   **Solución:** La clase `OpenTopoDataAdapter` envuelve la librería externa y traduce los datos al formato esperado por nuestro dominio, desacoplando nuestra lógica de negocio de la API de terceros.

#### C. Patrón Proxy (Proxy)
*   **Problema:** Las llamadas a la API meteorológica son costosas en tiempo y recursos. Realizar la misma petición repetidamente es ineficiente.
*   **Solución:** Se implementó `CachingSolarProxy`. Este componente intercepta las llamadas; si los datos para una coordenada y hora ya existen en memoria, los devuelve inmediatamente sin contactar a la API externa.

### 3.2 Tecnologías Clave
*   **Backend:** Spring Boot (Web, DevTools, Lombok).
*   **Frontend:** Thymeleaf (Motor de plantillas), Bootstrap 5 (Estilos), Leaflet.js (Mapas), Chart.js (Gráficos).
*   **Generación Documental:** jsPDF y html2canvas para la creación de reportes vectoriales en el cliente.
*   **Control de Versiones:** Git y GitHub.

---

<div style="page-break-after: always;"></div>

## 4. FUNCIONALIDADES DEL SISTEMA

### 4.1 Selección de Destino Interactivo
Mediante la integración de **Leaflet.js**, el usuario puede seleccionar cualquier punto del globo terráqueo. Se han preconfigurado "Destinos Destacados" (ej. Teide, Everest) que, mediante JavaScript, posicionan el mapa y cargan datos de altitud topográfica real verificada.

### 4.2 Análisis Meteorológico Multidimensional
El sistema procesa datos brutos para devolver:
*   **Isoterma 0°C:** Fundamental para saber dónde hay hielo.
*   **Índice UV:** Con escala de colores de advertencia.
*   **Viento y Sensación Térmica:** Cálculo basado en la fórmula chill-factor.

### 4.3 Generación de Informes PDF
Una de las funcionalidades más complejas fue el exportador a PDF. Permite al montañista descargar una ficha técnica con:
*   Gráfico de evolución térmica (imagen generada dinámicamente).
*   Datos de Wikipedia sobre la ubicación (con imagen si está disponible).
*   Tablas de datos formateadas para impresión A4.

Mecanismo técnico: Se utiliza un renderizado oculto en el DOM (`#pdf-clean-version`) que es capturado y transformado a PDF, garantizando que el diseño en papel sea limpio y distinto al de la web.

---

<div style="page-break-after: always;"></div>

## 5. INTEGRACIÓN DE INTELIGENCIA ARTIFICIAL (IA)

Para el desarrollo de HelioRoute, se ha utilizado **GitHub Copilot** (modelo Gemini 3 Pro Preview) como asistente de programación por pares (Pair Programmer). La IA no solo ha generado código "boilerplate", sino que ha actuado como consultor de arquitectura y experto en depuración.

### 5.1 Rol de la IA en el Ciclo de Vida
1.  **Refactorización:** Ayuda para limpiar el controlador y mover lógica compleja a servicios.
2.  **Depuración Frontend:** Crucial para solucionar problemas de desbordamiento de texto y márgenes en la generación del PDF con `jsPDF`.
3.  **Scripts de Automatización:** Generación de scripts `.bat` para facilitar el despliegue local.

### 5.2 Prompts Destacados (Ejemplos Reales)
A continuación, se presentan algunos de los prompts más efectivos utilizados durante el desarrollo, que ilustran la interacción con el modelo:

| Fase del Proyecto | Prompt utilizado (Usuario) | Resultado/Acción de la IA |
| :--- | :--- | :--- |
| **Arquitectura** | *"Implementa un patrón strategy para el cálculo solar en Java"* | Generó la interfaz `ShadowCalculationStrategy` y las dos clases concretas, explicando cómo inyectarlas en Spring. |
| **Frontend / PDF** | *"El texto de la wikipedia se corta en el PDF al no tener width fijo. Arréglalo usando splitTextToSize"* | Implementó la lógica de cálculo de márgenes y paginación manual en el script de generación PDF. |
| **Lógica Negocio** | *"¿Por qué la altitud del Teide en la app (3697m) no coincide con la real (3715m)?"* | Explicó el concepto de "suavizado de rejilla" en modelos meteorológicos y sugirió diferenciar entre "Altitud Modelo" y "Altitud Real". |
| **Despliegue** | *"Hazme un script .bat para lanzar los dos microservicios a la vez"* | Creó el archivo `start-helio-route.bat` que levanta backend y frontend en paralelo minimizando ventanas. |

---

<div style="page-break-after: always;"></div>

## 6. GESTIÓN DEL PROYECTO: TIEMPOS Y DESVIACIONES

A continuación se detalla la planificación temporal del proyecto, comparando la estimación inicial con el tiempo real invertido. Se observan desviaciones significativas en la capa de presentación (PDF) debido a la complejidad de las librerías gráficas.

### Tabla de Esfuerzo (Horas)

| Tarea / Fase | Tiempo Estimado (h) | Tiempo Real (h) | Desviación | Causa Principal |
| :--- | :---: | :---: | :---: | :--- |
| **1. Configuración del Entorno** | 4.0 | 3.0 | -1.0 | Setup rápido con Spring Initializr y Maven. |
| **2. Backend: Lógica Core** | 10.0 | 12.0 | +2.0 | Ajuste de algoritmos matemáticos solares. |
| **3. Implementación Patrones** | 6.0 | 5.0 | -1.0 | Facilidado por el uso de GitHub Copilot. |
| **4. Frontend: Mapa e Interfaz** | 8.0 | 8.0 | 0.0 | Leaflet.js es intuitivo y bien documentado. |
| **5. Integración API Externa** | 4.0 | 5.0 | +1.0 | Manejo de CORS y mapeo de JSON complejo. |
| **6. Generación PDF (Export)** | **3.0** | **12.0** | **+9.0** | **Problemas críticos de layout, paginación e imágenes.** |
| **7. Refactorización y Docs** | 5.0 | 4.0 | -1.0 | Generación ágil de documentación. |
| **TOTAL** | **40.0 h** | **49.0 h** | **+9.0 h** | (Principalmente por módulo PDF) |

### Análisis de Desviaciones
La mayor desviación del proyecto (22% sobre el total) se debió al módulo de exportación PDF. La librería `jsPDF` requiere cálculos manuales de coordenadas (x, y) para dibujar textos e imágenes, lo que implicó un proceso de "prueba y error" considerable para lograr un diseño profesional que no cortara párrafos largos (como los de Wikipedia).

---

<div style="page-break-after: always;"></div>

## 7. CONCLUSIONES

El proyecto **HelioRoute** cumple con éxito los objetivos planteados. Se ha logrado una aplicación funcional que no solo consume datos, sino que aporta valor mediante patrones de diseño y una interfaz cuidada.

**Puntos Fuertes:**
*   Implementación correcta de patrones de diseño.
*   Interfaz de usuario reactiva y moderna.
*   Automatización del despliegue local.

**Lecciones Aprendidas:**
*   La importancia de validar las librerías de terceros (como jsPDF) antes de comprometerse con una funcionalidad crítica.
*   La diferencia entre datos de modelo (rejas matemáticas) y datos topográficos reales.

## 8. BIBLIOGRAFÍA

**Documentación Oficial y Librerías:**

1.  **Spring Boot Documentation:**  
    [https://docs.spring.io/spring-boot/docs/current/reference/html/](https://docs.spring.io/spring-boot/docs/current/reference/html/)
    *Consulta: Inyección de dependencias y Controladores REST.*

2.  **Open-Meteo Weather API:**  
    [https://open-meteo.com/en/docs](https://open-meteo.com/en/docs)
    *Consulta: Endpoints de radiación solar y modelos de elevación.*

3.  **Leaflet.js (Mapas Interactivos):**  
    [https://leafletjs.com/reference.html](https://leafletjs.com/reference.html)
    *Consulta: Gestión de marcadores y eventos de click.*

4.  **Thymeleaf + Spring Integration:**  
    [https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)
    *Consulta: Renderizado de atributos del servidor en HTML.*

5.  **jsPDF Library:**  
    [https://raw.githack.com/MrRio/jsPDF/master/docs/](https://raw.githack.com/MrRio/jsPDF/master/docs/)
    *Consulta: Métodos `splitTextToSize`, `addImage` y `text`.*

---
*Fin de la Memoria Técnica - Enero de 2026*
