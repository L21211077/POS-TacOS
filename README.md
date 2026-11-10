# 🌮 POS TacOS — Sistema de Punto de Venta Android (Gourmet POS)

[![Android SDK](https://img.shields.io/badge/Android%20SDK-37-brightgreen.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)

**POS TacOS** es un sistema de punto de venta (POS) nativo para Android diseñado especialmente para taquerías y restaurantes. Su interfaz visual está optimizada para tabletas profesionales mediante un diseño de pantalla dividida (*split-screen*), combinando la estética gourmet oscura (*Dark Gourmet Palette*) con una experiencia de usuario rápida, ágil y táctil.

---

## 🌟 Características Principales

- **📱 Diseño de Pantalla Dividida (Split-Screen Tablet Layout)**:
  - **Columna Izquierda (65% del ancho)**: Catálogo visual de productos en cuadrícula adaptable (*Grid*) y selector de categorías estilo cápsula (*Pills*).
  - **Columna Derecha (35% del ancho)**: Panel de orden actual / carrito de compras con control de cantidades, resumen de importes y botón de envío a cocina.

- **🪑 Asignación de Mesas y Ubicación (`TableSelectionDialog`)**:
  - Selección interactiva de ubicación: `Mesa 1`, `Mesa 2`, `Mesa 3`, `Mesa 4`, `Mesa 5`, `Mesa 6`, `Mesa 7` y `Barra`.
  - Acceso rápido desde el TopBar o desde el panel de la orden.

- **🛍️ Opción "Para Llevar" (Takeout/To-Go)**:
  - Toggle/chip interactivo para cambiar instantáneamente entre `🍽️ Comer Aquí` y `🛍️ Para Llevar`.
  - Reflejado en la interfaz, en el encabezado del ticket ESC/POS (`*** ORDEN PARA LLEVAR ***`) y en las notificaciones a cocina.

- **🏷️ Selector de Categorías Rápido**:
  - Pestañas/Pills para categorizar instantáneamente entre `🔥 Todos`, `🌮 Comida` y `🥤 Bebidas`.

- **🛠️ Modificadores y Notas de Preparación (`ProductModifierDialog`)**:
  - Al hacer clic en un producto se abre un diálogo interactivo para personalizar la orden.
  - Selección de opciones comunes con chips táctiles (*"Con todo"*, *"Sin cebolla"*, *"Sin cilantro"*, *"Salsa aparte"*, *"Doble tortilla"*, *"Bien dorado"*, etc.).
  - Campo de texto libre para notas especiales dirigidas al cocinero (ej. *"Sin sal"*, *"Salsa roja aparte"*).
  - Los modificadores se muestran claramente debajo del nombre de cada producto en la lista de la orden.

- **🖨️ Generador de Comanda / Ticket Térmico (Formato ESC/POS)**:
  - Generación de ticket en texto plano formateado a 32 columnas para impresoras térmicas ESC/POS.
  - Registro automático del ticket impreso en el Logcat del sistema (`Log.d("POS_TICKET", ...)`).
  - Diálogo de vista previa interactivo (`ThermalReceiptPreviewDialog`) con diseño estilo papel de recibo previo al envío a cocina.

- **🎨 Tema Visual Gourmet Oscuro (Inspired by Burger App Concept UI)**:
  - Paleta de colores sofisticada con fondo negro carbón (*Obsidian Dark* `#141318`), acentos rojo carmesí fuego (`#E53935`) y detalles dorados/ámbar (`#FFB300`).
  - Tarjetas de producto elevadas con indicador de popularidad/calificación (ej. `⭐ 4.9`), contenedor de emoji ambient y badge de cantidad en carrito.

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje**: [Kotlin](https://kotlinlang.org/) (versión 2.2+)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material 3
- **Arquitectura**: Declarativa orientada a estados (*Unidirectional Data Flow*)
- **Compile / Target SDK**: Android 15 (API 37)
- **Min SDK**: Android 8.0 (API 26)
- **Gestor de Dependencias**: Gradle KTS con Version Catalogs (`libs.versions.toml`)

---

## 📁 Estructura del Proyecto

```text
POSTacOS/
├── app/
│   ├── build.gradle.kts                # Configuración de dependencias y compilación
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml      # Manifiesto de la aplicación Android
│           └── java/com/example/pos_service/
│               ├── MainActivity.kt      # Pantalla principal POS, catálogo, carrito y diálogos
│               └── ui/theme/            # Configuración del tema visual
│                   ├── Color.kt         # Paleta de colores Gourmet Dark
│                   ├── Theme.kt         # Configuración del tema Material 3
│                   └── Type.kt          # Tipografía
├── gradle/
│   └── libs.versions.toml              # Catálogo de versiones centralizado
├── build.gradle.kts                    # Configuración raíz de Gradle
├── settings.gradle.kts                 # Configuración de módulos
└── README.md                           # Documentación del proyecto
```

---

## 🚀 Requisitos e Instalación

### Requisitos Previos

- **Android Studio**: Android Studio Ladybug / Meerkat / 2026.2+
- **JDK**: Java Development Kit 17 o superior
- **Dispositivo / Emulador**: Dispositivo físico o emulador Android (Recomendado: Pantalla estilo Tableta 10" o resolución 1280x800).

### Pasos para Ejecutar el Proyecto

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tu-usuario/POSTacOS.git
   cd POSTacOS
   ```

2. **Abrir en Android Studio**:
   - Abre Android Studio y selecciona **Open an existing project**.
   - Selecciona la carpeta raíz del proyecto `POSTacOS`.

3. **Sincronizar Gradle**:
   - Deja que Gradle descargue las dependencias y sincronice el proyecto.

4. **Compilar y Ejecutar**:
   - Selecciona un emulador o dispositivo conectado.
   - Presiona `Run` (`Shift + F10`) o ejecuta por línea de comandos:
     ```bash
     ./gradlew assembleDebug
     ```

---

## 📸 Capturas / Previsualización de Pantalla

El proyecto incluye previsualizaciones `@Preview` listas para ser inspeccionadas desde el editor de Compose en Android Studio:

- `POSMainScreenTabletPreview`: Simula una pantalla de tableta completa (1280dp x 800dp) con catálogo, modificadores y panel de orden.

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
