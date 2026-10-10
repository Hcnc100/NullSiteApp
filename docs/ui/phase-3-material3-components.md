# Fase 3 — Componentes Material 3

Fecha: 2026-10-10. Proyecto: NullSiteApp. Referencia visual: NurseCompose2.

## Alcance

- Componentes de producción migrados a `androidx.compose.material3`: botones,
  FAB, tarjetas, textos, campos, switches, indicadores y toolbars.
- `ScaffoldState` sustituido por `SnackbarHostState`; cada Scaffold presenta un
  `SnackbarHost` M3. El drawer utiliza `DrawerState`, `ModalNavigationDrawer`,
  `ModalDrawerSheet` y `NavigationDrawerItem`, sin recortar artificialmente su forma.
- El selector de imagen utiliza `ModalBottomSheet` M3. Su presencia se controla
  por un estado guardable separado del estado de animación; abrir, cerrar y
  regresar de una actividad externa no dependen de componer una hoja oculta M2.
- Refresco de perfil, proyectos y correo mediante `PullToRefreshBox` M3. La
  pequeña abstracción `AppRefreshState` conserva callbacks actualizados mediante
  `rememberUpdatedState` sin reconstruir los estados de scroll. No conecta gestos
  durante la carga inicial; la versión 1.3.2 no ofrece parámetro `enabled`.
- Eliminados el puente de colores/tipografía/formas M2 y su dependencia directa.
  Material 3 se mantiene en **1.3.2**, sin subir minSdk 21 ni cambiar Kotlin/AGP.
- Se mantienen las paletas, fuente del sistema y formas de la fase 2. Tarjetas
  con elevación suave de 1 dp; toolbars con superficies y foreground semánticos.
- Login sobre fondo claro/oscuro del tema, con acentos turquesa y controles M3.
- Splash y fondo de ventana XML alineados con los fondos claro/oscuro Compose.
  `enableEdgeToEdge()` configura barras del sistema según el modo del dispositivo.
  El contenedor raíz ya no añade un segundo Scaffold/insets; Home consume los
  insets que pasa a sus destinos para evitar padding duplicado.
- Selector de imagen desplazable y con objetivos mínimos de 48 dp. Switch
  biométrico con una única interacción de fila, sin comprimir el control.
- Corregidas las etiquetas invertidas de mostrar/ocultar contraseña.
- En editores de perfil/proyecto, la toolbar se oculta únicamente con teclado
  abierto en horizontal. La altura adicional de la barra M3 dejaba el campo
  final parcialmente bajo el IME en un teléfono de 1080×2400, también al 200 %.
  Al cerrar el teclado reaparece la navegación; la prueba comprueba ambos estados.

No se modifican repositorios, autenticación, Firebase ni reglas de negocio.
No se realizaron commit, push ni despliegue. TalkBack se excluye por indicación
expresa del usuario; contraste, escalado, teclado y objetivos táctiles se conservan.

## Verificación

- Compilación APK debug y APK de pruebas: correcta.
- Pruebas unitarias: **6 aprobadas**, cero fallos/omitidas.
- Referencias `androidx.compose.material.*` en Kotlin: **0**. Sin dependencia
  directa `androidx.compose.material:material`.
- `releaseRuntimeClasspath` no contiene controles Material 2. En debug,
  `ui-tooling` aún incorpora Material 2 transitivamente para previews; no se
  fuerza su exclusión. `material-icons-core` y `material-ripple` son dependencias
  compartidas legítimas de M3, no controles M2 de la aplicación.
- Componentes nuevos: **10/10 aprobados** en vertical, Android 13, AVD
  `Medium_Phone_API_33`, instancia aislada `emulator-5558` en modo `-read-only`.
- Horizontal fijado, después de corregir IME: **26/26 aprobados** (57.783 s).
- Repetición de formularios horizontales con guardas de orientación y de
  toolbar oculta/restaurada: **12/12 aprobados** (36.661 s).
- Repetición final vertical de la matriz completa: **26/26 aprobados** (55.285 s).
- Resultado: **52 casos UI únicos aprobados** entre ambas orientaciones, además
  de la repetición horizontal reforzada. `git diff --check`: correcto.
- Evidencia exportada: `build/phase3-qa/`, **54 capturas** (48 formularios +
  4 muestras de tema M3 + 2 MainActivity reales). Se revisaron visualmente, entre otras, perfil oscuro
  horizontal al 200 % con IME y login claro con botón sobre el teclado.
- MainActivity real abierta sin autenticarse: comprobados cambio claro/oscuro,
  fondo, controles y legibilidad de iconos de las barras del sistema. No se
  introdujeron credenciales ni se pulsó autenticar.

Las pruebas de componentes nuevas cubren en ambos temas: drawer y selección,
abrir/cerrar/reabrir selector de imagen sin lanzar cámara/galería, snackbar,
gesto de refresco/callback actualizado/deshabilitado durante carga, contraseña,
errores de vacío/longitud y botón de guardar deshabilitado.

Las pruebas de formularios utilizan los composables de producción y teclado
real, sin autenticarse ni escribir en servicios remotos. Cubren login, perfil y
proyecto, fuentes al 100/200 %, claro/oscuro y ambas orientaciones; verifican el
campo final y botón de envío por encima del teclado con bounds sin recortar.

Incidencias de entorno: la primera ejecución en `emulator-5556` fue interferida
por instrumentación concurrente de RunningCompose (pérdida de jerarquía/foco),
por lo que no se contabiliza como validación. Se inició otra instancia de AVD
en modo read-only, sin sobrescribir el AVD ni sus datos persistidos. Esa instancia
tenía un APK antiguo con versionCode superior: la instalación inicialmente fue
rechazada y las pruebas contra APK incompatible fallaron por clases Compose.
Se reinstaló el APK correcto **solo dentro de la instancia descartable** y la
suite de componentes pasó completa. No se debilitaron aserciones.

La primera corrida nominalmente horizontal presentó deriva de orientación entre
casos y no se contabiliza como cobertura horizontal completa. Se fijó la rotación
con `wm fixed-to-user-rotation enabled` y `wm user-rotation lock 1`. Esto reprodujo
8 fallos reales de bounds en los editores (4 variantes de perfil y 4 de proyecto),
que motivaron la toolbar adaptativa descrita arriba. El test acepta `qaOrientation`
y comprueba la configuración efectiva para detectar futuras derivas.

## Limitaciones y siguientes fases

Reproducir (APK debug y APK androidTest instalados, Android 13):

```powershell
# Sustituir SERIAL por un emulador dedicado; no compartirlo con otras suites.
adb -s SERIAL shell wm fixed-to-user-rotation enabled
adb -s SERIAL shell wm user-rotation lock 1
adb -s SERIAL shell am instrument -w -r -e qaOutputDirectory phase3-qa -e qaOrientation landscape -e class com.nullpointer.nullsiteadmin.ui.FormKeyboardTest,com.nullpointer.nullsiteadmin.ui.PhaseOneAccessibilityTest,com.nullpointer.nullsiteadmin.ui.MaterialThemeContractTest,com.nullpointer.nullsiteadmin.ui.MaterialThreeComponentsTest com.nullpointer.nullsiteadmin.test/androidx.test.runner.AndroidJUnitRunner
# Repetir con lock 0 y qaOrientation portrait.
# Restaurar fixed-to-user-rotation y user-rotation al estado previo al terminar.
```

Logs locales ignorados en `build/`: `phase3-final-build.log`,
`phase3-landscape-fixed.log`, `phase3-landscape-guards.log`,
`phase3-portrait-final.log` y `phase3-dependency-release.log`.

- Sesión autenticada real y roundtrip de cámara/galería/cropper pendientes; la
  validación local no debe describirse como prueba integral del backend.
- API 21 y Android 15/16 no verificados en dispositivo en esta fase.
- Navegación permanente/rail en pantallas grandes sigue siendo una mejora
  posterior; esta fase conserva drawer modal adaptado a la orientación.
- Medición release, recomposiciones, lifecycle/flows y perfiles de inicio
  pertenecen a la fase de rendimiento; no se atribuyen mejoras medidas a esta migración.
- Serena Kotlin falló al inicializar. El usuario autorizó continuar sin Serena.

## Fuentes de API

- [Material 3 pull-to-refresh](https://developer.android.com/develop/ui/compose/components/pull-to-refresh).
- [Navigation drawer](https://developer.android.com/develop/ui/compose/components/drawer).

## Key Learnings:

1. M3 separa drawer y snackbar del Scaffold; migrar los estados evita conservar una dependencia M2 invisible bajo controles nuevos.
2. La presencia de ModalBottomSheet y su SheetState animado son estados distintos: la visibilidad solicitada debe sobrevivir recreaciones.
3. Los destinos anidados deben consumir los insets recibidos del Scaffold padre para evitar espacios duplicados con edge-to-edge.
4. Un callback de refresco capturado en un estado recordado debe actualizarse con rememberUpdatedState.
5. No basta con solicitar rotación al emulador: hay que comprobar la orientación efectiva de cada caso; de otro modo puede ocultar regresiones IME horizontales.
