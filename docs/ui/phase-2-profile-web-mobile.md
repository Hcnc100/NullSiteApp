# Fase 2 — perfil web y Android

## Implementación local

- Edición de profesión y biografía en español e inglés, sin confundir el idioma
  del contenido publicado con el idioma de la interfaz Android.
- Redes opcionales GitHub, Facebook, Instagram, Twitter/X y WhatsApp: solo HTTPS,
  máximo 2048 caracteres, una entrada por plataforma y eliminación explícita.
- Vista previa bilingüe y fallback al texto base cuando una traducción está vacía.
- Borrador restaurable y confirmación antes de abandonar cambios pendientes.
- Lectura compatible con cachés anteriores; actualizaciones de traducción mediante
  rutas de campos para no sustituir idiomas/campos desconocidos.
- El control de concurrencia incluye las redes y las traducciones editables.

## Contratos importantes

`socialLinks` ausente conserva los enlaces históricos de la web. Una lista vacía
significa eliminarlos: añadir el primer enlace personalizado sustituye el conjunto
histórico, no lo combina silenciosamente. Las traducciones usan
`translations.es/en.profession/description`; el nombre mantiene su valor base.
Los límites son profesión 180 y biografía 5000 caracteres, igual que en la web.

## Traducciones de interfaz

Revisión de recursos `values` (inglés) y `values-es` (español), incluyendo los
mensajes nuevos. Se corrigieron gramática inglesa, acentos y terminología de
correos en español. Una prueba JVM comprueba paridad de claves traducibles,
placeholders de formato y traducciones no vacías. Los cuatro textos de ejemplo
de previsualizaciones están marcados `translatable="false"` intencionalmente.
Los nombres de marcas y los selectores Español/English son intencionales.

## Verificación

- JVM: **39/39 correctas**, incluidas diez pruebas del nuevo contrato/editor y la
  regresión de recursos bilingües. 156 claves traducibles por idioma, sin diferencias
  de placeholders ni traducciones vacías.
- Emulador `Codex_UI_API_33`, Android 13: **48/48 correctas** en vertical, sin
  omitidas. 32 prueban el editor real (4 escenarios × claro/oscuro × ES/EN × fuente
  normal/200 %); 2 prueban DataStore/restauración y 14 son regresiones de formularios
  y contrato de tema. Repositorios falsos: ninguna escritura Firebase.
- Escenarios: traducciones independientes, URLs inválidas, borrado explícito,
  parche de guardado y confirmación al salir sin perder el borrador.
- La suite global de 109 pruebas se interrumpió para focalizar la validación;
  no se presenta como aprobada. Un filtro inicial por método sin el sufijo de
  parámetros ejecutó cero pruebas y tampoco cuenta como validación.
- Se corrigió el montaje de pruebas para que los diálogos también usaran idioma
  y escala de fuente simulados, incluso después de callbacks del teclado. Las
  capturas distinguen la raíz del formulario y la raíz propia del diálogo.
- La restauración con SavedStateHandle no sustituye una muerte real de proceso;
  DataStore sí se prueba cerrando y reabriendo una instancia local.

### Evidencia visual seleccionada

Capturas revisadas en [phase2-profile-qa](phase2-profile-qa/): diálogo ES claro al
200 %, error HTTPS ES claro al 200 %, diálogo EN oscuro al 200 % y vista previa ES
oscura al 200 %. Texto legible, acciones accesibles y contenido desplazable; la
barra de título usa elipsis con fuente grande de forma intencional.

La comprobación horizontal detectó que el mensaje del diálogo podía quedar
recortado con fuente al 200 %. Ahora es desplazable, con botones siempre visibles;
la regresión comprueba el desplazamiento y ambas acciones. Se corrigió también
la etiqueta Profesión/Profession, que antes decía Perfil/Profile.

- Horizontal: **4/4 correctas** tras la corrección (vista previa EN/claro/200 %,
  URLs EN/oscuro/100 %, guardado ES/claro/100 % y diálogo ES/oscuro/200 %).
  No es una matriz horizontal completa de las ocho combinaciones.
- `:app:assembleDebug` y `:app:testDebugUnitTest`: correctos tras el cambio del
  diálogo; APK local `app/build/outputs/apk/debug/app-debug.apk`.
- Las 48 pruebas verticales corresponden a la ejecución previa al ajuste final
  del diálogo; la repetición focalizada final EN/claro/100 % pasó **1/1**.
  También pasó **1/1** la repetición del desplazamiento hasta el final del mensaje
  en horizontal ES/oscuro/200 %. No se afirma una segunda ejecución de toda la matriz.
- `git diff --check`: correcto. Rotación automática del emulador restaurada a su
  valor original. Sin despliegues ni commits.

Evidencia XML conservada localmente en `build/phase2-test-evidence/portrait` y
`build/phase2-test-evidence/landscape`; informe HTML de la última ejecución en
`app/build/reports/androidTests/connected/debug/index.html`. Las capturas completas
se conservaron en `build/phase2-profile-qa`, con una selección en este directorio.

## Límites / siguiente fase

Esto no despliega la web ni escribe en Firebase. La aceptación con reglas,
transacciones y Storage reales/emulados sigue pendiente. La siguiente fase del
plan de cinco fases es proyectos enriquecidos y galerías con migración Room.

## Key Learnings:

1. Ausencia de redes y lista vacía tienen efectos públicos diferentes.
2. Los parches con rutas de campos conservan traducciones desconocidas.
3. Cambiar solo LocalContext en pruebas no cambia los recursos de los diálogos:
   hay que configurar también la Activity y restaurarla al terminar.
4. Un diálogo que cabe en vertical puede recortar su mensaje en horizontal al
   200 %: el contenido debe permitir desplazamiento sin ocultar sus acciones.

## Archivos principales

- `models/personalInfo/data/ProfileContent.kt`: contrato, lectura y validación.
- `models/personalInfo/dto/PersonalInfoDTO.kt`: parches parciales compatibles.
- `ui/screens/profile/editInfoProfile`: estado, formulario y confirmación.
- `ui/screens/profile/infoProfile`: vista previa bilingüe.
- `app/src/test/.../ProfileContentTest.kt` y `StringResourcesTest.kt`: regresiones JVM.
- `app/src/androidTest/.../ProfilePhaseTwoUiTest.kt` y
  `ProfilePhaseTwoPersistenceTest.kt`: escenarios locales sin Firebase.
