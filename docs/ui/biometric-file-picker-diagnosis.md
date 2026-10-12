# Diagnóstico: bloqueo biométrico al regresar del selector

## Goal
Reproducir en emulador y explicar cómo evitar pérdida de selección con huella activa.
El usuario pidió diagnóstico/propuesta, no una nueva publicación.

## Reproducción verificada
- Emulador API 33, emulator-5556; instrumentation **1/1 correcta**.
- Selector DocumentsUI real, contrato OpenDocument, PDF de prueba en Downloads.
- BiometricRepoImpl real y rememberLifecycleEvent real; Settings/resultado de
  autenticación controlados. No sensor/huella físicos, cuenta Firebase ni subida.
- El arnés parte de una sesión desbloqueada y reproduce la llamada de MainScreen
  al retornar (omite solo la primera comprobación, ya realizada en el setup).
- Secuencia registrada: `picker callback: file selected`, después
  `return: ON_RESUME -> initVerifyBiometrics`; estado true → false y editor retirado
  de composición. Archivo de prueba eliminado en finally.
- No es un E2E del NavHost real con Hilt/admin; no se afirma haber medido cancelación
  de upload o pérdida exacta de ViewModel en ese NavHost. Sí verifica el relock
  que inicia el problema y el desmontaje condicional del editor en el arnés.

## Causa en producción
`MainScreen.kt:38` invoca initVerifyBiometrics en **cada ON_RESUME**, también cuando
retorna de una actividad legítima iniciada por la app. En BiometricRepoImpl,
initVerifyBiometrics asigna `!isBiometricEnabled.first()` (false con huella activa),
sin preservar que la sesión ya estaba autenticada.
MainScreen usa ese estado para cambiar el startRoute del NavHost a LockScreen.
El mismo camino es global: PDF, galería múltiple, selector/crop de perfil y otras
actividades externas pueden activarlo. El ON_STOP comentado no evita el problema:
el reset ya ocurre directamente en ON_RESUME.

## Solución recomendada (implementada localmente tras autorización)
1. Separar autenticación de cuenta, bloqueo local y navegación del editor.
   Mantener NavHost/backstack/ViewModels estables mientras la cuenta siga conectada;
   mostrar un bloqueo superpuesto que capture interacción y oculte contenido, no
   sustituir el grafo al cambiar el bloqueo. Limpiar todo sí al cerrar sesión.
2. Conservar el resultado del selector y el borrador mientras está bloqueado;
   procesarlo/permitir publicación solo tras desbloquear. Para OpenDocument tomar
   permiso persistente si disponible; con fuentes temporales copiar al almacenamiento
   privado. No almacenar bytes grandes en SavedState.
3. Distinguir inicialización de la sesión de revalidación del ciclo de vida.
   Modelar explícitamente salida/retorno externo; no usar un booleano global de
   "ignorar próximo resume" que deje la app abierta indefinidamente si el usuario
   pulsa Inicio, apaga pantalla o abandona el selector.
4. Mantener bloqueo por salida real, apagado de pantalla y muerte de proceso;
   no arreglarlo eliminando toda reautenticación. No es necesario que el selector
   deje de mostrar huella si conservar el resultado evita perder la operación.
5. Probar selección/cancelación PDF y fotos, crop, Inicio desde el selector,
   bloqueo de pantalla, rotación, process death y logout; verificar no doble subida.

## Mitigación temporal
Si se necesita subir inmediatamente, desactivar temporalmente el bloqueo de
huella de esta app en un entorno controlado, hacer la operación y reactivarlo.
Reduce protección local durante ese intervalo; no cambiar permisos Firebase ni
retirar la seguridad del dispositivo. Preferir la solución de conservación.

## Accomplished / Next Steps
El usuario autorizó cambiar el funcionamiento y probar opciones. Se implementó
el gate local: NavHost estable por autenticación de cuenta, editor invisible y sin
semántica accesible durante bloqueo, diálogo opaco fullscreen con FLAG_SECURE,
sin dismiss por Atrás/exterior y bloqueo explícito del back del grafo subyacente.
Se limpia foco/teclado al bloquear. ON_PAUSE invalida autorización local; ON_RESUME
la revalida mediante el repositorio existente. El observador directo evita perder
eventos rápidos por coalescencia de estado Compose.

El test diagnóstico se convirtió en regresión: exige conservar selección y NavHost,
no el comportamiento defectuoso. Se usan PDF/PNG seleccionados en DocumentsUI,
cancelación real y salida/retorno a Ajustes del sistema como otra actividad externa.
El diálogo se muestra solo cuando la Activity está RESUMED: no se añade una ventana
modal mientras el selector externo está en primer plano. El editor permanece
oculto durante la pausa. Login/logout cambian explícitamente la identidad de la
composición del grafo; los cambios de bloqueo biométrico no la cambian.
En esa etapa no hubo commit/push/publicación ni incremento de versionName.
La entrega posterior usa 5.2.1: 5.2.0 ya tiene reserva anti-duplicado.

## Opciones evaluadas
- Quitar reautenticación ON_RESUME: descartado por debilitar bloqueo.
- Eximir selectores con un booleano global: descartado; no distingue Inicio,
  pantalla apagada o abandono prolongado y complica crop/resultados en cadena.
- Bloqueo superpuesto con estado estable: elegido; conserva seguridad local y
  resultados de ActivityResult sin reiniciar el grafo. Se sigue pidiendo huella.

Las subidas ya solicitadas, por ejemplo enqueueImages, pueden continuar bajo el
bloqueo si siguen autorizadas en Firebase: el bloqueo es de interacción local,
no una revocación de credenciales ni una cancelación del trabajo en curso. No se
añadió auto-publicación; el CV sigue requiriendo pulsar Publicar tras desbloquear.
No se promete supervivencia de uploads a muerte de proceso; no se añadió WorkManager.

## Verificación final del cambio
- `:app:assembleDebug` y `:app:assembleDebugAndroidTest`: correctos.
- `:app:testDebugUnitTest`: **75/75**, cero fallos.
- `:app:lintRelease`: **0 errores**, 90 warnings y 1 hint existentes; sin baseline
  nuevo ni supresiones. `git diff --check`: correcto.
- API 33, vertical, fuente normal: **4/4** regresiones nuevas correctas con
  PDF/PNG × tema claro/oscuro. Se selecciona el archivo en DocumentsUI y se
  verifica su nombre por ContentResolver, callback único, bloqueo visible,
  editor ausente de semántica, Atrás consumido, navegación sin desmontar,
  selección conservada tras desbloquear y procesamiento simulado único.
- Cada caso también cancela otro selector real: sin selección inventada ni
  procesamiento extra. Cambiar a Ajustes pausa la Activity e invalida la
  autorización a null; al retornar sigue bloqueada y conserva el editor.
- **27/27** regresiones existentes: CareerPhaseFourDataTest,
  ProjectPhaseThreeDataTest, FormValidationPolicyTest y MaterialThemeContractTest.
- Se restauró la configuración del emulador (fuente 1.0, rotación original).

### Límites y problemas de la primera ejecución (histórico)
El intento adicional horizontal con fuente de sistema 200% no llegó a devolver
la selección desde DocumentsUI y se interrumpió. **No se cuenta como aprobado**:
quedó pendiente esa aceptación visual/OS picker. No se rebajaron las aserciones
del resultado para ocultarlo. Se mantuvo el caso funcional final en configuración
vertical normal, y las pruebas específicas de tema/formularios existentes.

Se corrigió el arnés para seleccionar PNG en la vista de documentos: algunos
contenedores de miniaturas anuncian ACTION_CLICK pero no abren el archivo; se
usa tap sobre bounds observados y se espera el callback real, no solo la aceptación
del tap. La navegación Atrás se inyecta como evento síncrono para no dejar un
back pendiente que llegue después de desbloquear. Los intentos de usar Home/
ActivityScenario para fondo fueron inestables en este arnés: la verificación
final usa Ajustes reales y comprueba la pausa, no simula una pérdida de foco.

### Límites de cobertura inicial (histórico)
Huella real/enrolamiento y cancelación del prompt en dispositivo, selector múltiple
de galería, cadena de crop de perfil, pantalla apagada, muerte de proceso y logout
end-to-end en el grafo Hilt real. El test usa el gate de producción, NavHost real
de prueba y repositorio biométrico real, pero sensor/settings controlados. No se
subieron archivos a Firebase para probarlo. Probar además API 34–36 y TalkBack.
En esa etapa no se publicó ni se hizo push; el APK debug local no actualiza Play.

## Cierre de pruebas para 5.2.1
El usuario autorizó completar pruebas y hacer push después de probar; indicó que
es el único administrador. No se elimina la protección anti-duplicado ni el bloqueo.

- API 33: **8/8** casos finales correctos en horizontal y fuente del sistema 200%:
  PDF individual, PNG individual, OpenMultipleDocuments con dos PNG y cadena
  OpenDocument → CropImageContract, cada uno en tema claro/oscuro.
- Cada caso verifica callback único, archivo consultable, editor/NavHost sin
  desmontar, Atrás bloqueado, selección retenida y procesamiento simulado único.
- Cada caso cancela otro selector real y simula cancelación biométrica mediante
  CancellationException (como la fuente real): no desbloquea ni pierde selección.
- Se verifica pausa/retorno por Ajustes en todos los casos. El último también usa
  Inicio real y pantalla apagada/encendida, comprueba invalidación durante la pausa
  y exige desbloquear nuevamente al regresar. Sin excepción global para selectores.
- Vertical/fuente normal: PDF/PNG y recorte correctos en la ejecución ampliada;
  selección múltiple **2/2** correcta tras reparar el arnés. La primera ejecución
  ampliada falló solo en automatización de selección múltiple: se corrigieron la
  pulsación larga accesible y la búsqueda exacta del botón Select; no se rebajaron
  las aserciones. La ejecución final completa de ocho casos quedó verde.
- Se restauraron fuente 1.0, rotación 0 y autorrotación habilitada.
- VersionName 5.2.1 y notas ES/EN preparadas para un solo push a main. El AAB local
  firmado es validación; solamente el pipeline publica su propio AAB en Play.

### Límites restantes, no presentados como pruebas aprobadas
El sensor y los ajustes biométricos del arnés son controlados. No se afirma prueba
de huella física, prompt nativo real ni subida de archivos a Firebase. La cadena
de recorte usa la misma biblioteca, no el flujo Hilt completo de perfil con
GetContent/compresión. Quedan aceptación en teléfono, logout Hilt end-to-end,
TalkBack y otras APIs. No se agregó recuperación de uploads tras muerte de proceso.
El caso con fuente grande comprueba interacción/semántica del gate y selector,
no es una aprobación visual de todas las pantallas de la aplicación.

Evidencia adicional local (ignorada por Git):
- `build/5.2.1-all-flows-landscape-font200.log` — OK (8 tests), 431.605 segundos.
- `build/5.2.1-multiple-final.log` — OK (2 tests), vertical y fuente normal.
- `build/5.2.1-validation-final.log` — compilación, unitarias y lint.
- `build/5.2.1-release-bundle.log` — AAB firmado de validación, versionCode 1032.

## Relevant Files
- `app/src/androidTest/java/com/nullpointer/nullsiteadmin/ui/BiometricPickerReproductionTest.kt`
  — reproducción controlada con selector real, sin operaciones remotas.
- `app/src/main/java/com/nullpointer/nullsiteadmin/ui/screens/main/MainScreen.kt`
  — revalidación ON_RESUME y selección del grafo por bloqueo.
- `app/src/main/java/com/nullpointer/nullsiteadmin/domain/biometric/BiometricRepoImpl.kt`
  — inicialización sobrescribe sesión desbloqueada.
- `build/biometric-picker-reproduction.log` — resultado instrumentation local.
- `app/src/main/java/com/nullpointer/nullsiteadmin/ui/screens/main/BiometricContentGate.kt`
  — protección visual/entrada sin destruir el editor.
- `app/src/main/java/com/nullpointer/nullsiteadmin/ui/screens/main/BiometricLifecycleEffect.kt`
  — observación directa de pausa/resume.
- `build/biometric-gate-device-final.log` — 4 regresiones finales correctas.
- `build/biometric-gate-regressions.log` — 27 regresiones existentes correctas.
- `build/biometric-gate-final-build.log` — compilación, 75 JVM y lint final.

## Key Learnings:
1. ON_RESUME también sigue a selectores/crop legítimos; no equivale a inicio de sesión.
2. Una barrera de seguridad debe ocultar/bloquear sin destruir estado de edición.
3. El callback puede llegar antes del relock; persistir URI no basta si se reinicia
   el propietario de SavedState o se reemplaza el grafo.
4. Consumir Atrás también en el grafo subyacente impide perder la ruta bajo el lock.
5. No presentar ventanas de bloqueo mientras una actividad externa posee el foreground.
