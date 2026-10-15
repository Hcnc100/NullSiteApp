# Actualización consistente — 2026-10-15

## Goal
Unificar la actualización de contenido con pull-to-refresh, siguiendo Perfil y
Proyectos, sin descartar ediciones ni archivos seleccionados.

## Instructions
El usuario prefiere el mismo gesto de actualización en las pantallas de consulta,
en lugar de mezclarlo con botones permanentes de Recargar.

## Discoveries
- Perfil, Proyectos y Correos ya compartían `AppPullToRefreshBox`; Habilidades/CV
  y Blog/Legal usaban botones al final de su contenido.
- Cambiar entre PullToRefreshBox y Box al deshabilitar el gesto recreaba el
  propietario del contenido. Material 3 1.3.2 no ofrece enabled en el contenedor,
  pero sí en `Modifier.pullToRefresh` (verificado en sus fuentes locales).
- Un gesto no debe ser la única opción accesible: el componente expone la acción
  «Actualizar contenido» / «Refresh content», sin añadir otro botón permanente.

## Accomplished
- Habilidades, CV, Blog y Legal usan el mismo componente y gesto que las otras
  pantallas. Se quitaron los botones de recarga de las vistas normales.
- Un botón de reintento permanece cuando falla la primera carga, además del
  gesto y de la acción accesible. El contenedor sigue siendo desplazable aunque
  haya pocos elementos o solo un mensaje de error.
- El gesto y la acción accesible se deshabilitan durante una edición, selección
  de PDF o una operación incompatible; no hay recargas simultáneas.
- Editorial diferencia una recarga de guardar/cargar historial: indicador de
  pull-to-refresh para recarga y progreso de operación para las otras acciones.
- El componente mantiene una estructura estable al deshabilitarse y conserva el
  callback actualizado. No agrega un segundo indicador a las cargas iniciales
  que ya usan una pantalla de progreso.
- Etiquetas accesibles añadidas en español e inglés; colores y tipografía siguen
  Material y los temas existentes. Sin dependencias nuevas ni cambios de Firebase.

### Verificación
- Compilación de APK debug y APK de pruebas: correcta.
- **75/75 pruebas JVM**, cero fallos.
- **37/37 pruebas Android** en `Codex_UI_API_33` (emulator-5556), 182.265 s:
  - 12 casos nuevos: 3 comportamientos × claro/oscuro × ES/EN.
  - 8 regresiones de formularios con fuente 200%, temas e idiomas.
  - 17 regresiones de datos/ViewModels de Career y Editorial.
- Las pruebas nuevas ejecutan gestos reales y acciones accesibles; verifican
  recuperación tras error, exclusión de recargas duplicadas, conservación de
  borrador y PDF pendiente, ausencia de desmontaje y callback actualizado.
- `lintRelease`: **0 errores**, 90 warnings y 1 hint; sin supresiones nuevas.
- Inspección visual de capturas actuales de CV ES oscuro y EN claro, y Legal ES
  oscuro, con fuente 200%: controles y textos mantienen su disposición adaptable.
- `git diff --check`: correcto.

Los repositorios son dobles locales; no se hicieron operaciones reales sobre
contenido de Firebase. Las pruebas no equivalen a certificación TalkBack ni a una
auditoría completa de todos los dispositivos. No se modificó el bloqueo biométrico.

## Next Steps
- El usuario autorizó posteriormente publicar y llevar los cambios a main.
  Se prepara 5.2.2 con notas ES/EN: los cambios ya parten de main, por lo que no
  se requiere merge entre ramas. Un solo push activa el pipeline existente.
- 5.2.1 ya fue publicada y tiene reserva anti-duplicados; no se reutiliza su versión
  ni se relanza su workflow. Verificar CI y Play antes de afirmar publicación.

## Relevant Files
- `app/src/main/java/com/nullpointer/nullsiteadmin/ui/share/AppPullToRefreshBox.kt`
  — gesto común, estructura estable y acción accesible.
- `app/src/main/java/com/nullpointer/nullsiteadmin/ui/screens/career/CareerScreen.kt`
  — actualización de Habilidades/CV con protección de edición/PDF.
- `app/src/main/java/com/nullpointer/nullsiteadmin/ui/screens/editor/EditorialScreen.kt`
  — actualización de Blog/Legal, sin botón permanente.
- `app/src/main/java/com/nullpointer/nullsiteadmin/ui/screens/editor/EditorialViewModel.kt`
  — estado explícito de recarga separado de otras operaciones.
- `app/src/main/res/values/strings.xml`, `app/src/main/res/values-es/strings.xml`
  — etiquetas accesibles EN/ES.
- `app/src/androidTest/java/com/nullpointer/nullsiteadmin/ui/RefreshConsistencyUiTest.kt`
  — 12 pruebas nuevas parametrizadas.
- `build/refresh-consistency-final-device.log`, `build/refresh-consistency-validation.log`,
  `build/refresh-consistency-final-lint.log` — evidencia local ignorada por Git.
- `build/refresh-career-dark-es.png`, `build/refresh-career-light-en.png`,
  `build/refresh-editorial-dark-es.png` — capturas QA revisadas, no versionadas.

## Key Learnings:
1. Unificar el gesto implica también unificar carga, errores y accesibilidad.
2. Deshabilitar una interacción no debe recrear el propietario del contenido.
3. La recarga de consulta no debe competir con ediciones ni selección de archivos.
