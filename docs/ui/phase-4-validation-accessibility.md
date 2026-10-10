# Fase 4 — Validación y accesibilidad visual

Fecha: 2026-10-10. Proyecto: NullSiteApp. **TalkBack excluido por indicación del usuario.**

## Cambios aplicados

### Validación de formularios

- Perfil revalida nombre, profesión y descripción antes de construir una
  actualización, incluso si el usuario no llegó a tocar alguno de esos campos.
- Proyecto revalida sus cuatro campos antes de comprobar si puede enviar datos.
  Ya no envía campos obligatorios vacíos por conservar el estado inicial sin error.
- Perfil y proyecto rechazan valores compuestos exclusivamente por espacios.
  Es una opción explícita del delegado de campo: no se cambian ni recortan las
  contraseñas ni los demás valores introducidos por el usuario.
- Login valida formato de correo localmente antes de construir credenciales,
  con un mensaje específico en español e inglés. Se conservan los límites
  existentes de longitud y la validación de autenticación del servidor.
- Login, actualización de perfil y actualización de proyecto impiden iniciar
  otra operación mientras la anterior sigue en curso.

### Interacción y presentación

- Guardar perfil se deshabilita durante la carga, tanto en el layout vertical
  como en el horizontal; antes solo dependía de la validez de los datos.
- Mostrar/ocultar contraseña respeta el estado deshabilitado del campo.
- Errores y contadores utilizan el slot `supportingText` M3, colores semánticos
  y una fila que permite envolver errores largos sin comprimir el contador.
- Email y contraseña del login son de una sola línea. En proyecto, los primeros
  tres campos usan **Siguiente** para avanzar el foco; los enlaces usan teclado URI.
- Estados vacíos/error dejan de usar pesos dentro de un contenedor de scroll
  con altura no acotada. Ilustración y mensaje tienen tamaño natural y pueden
  desplazarse con fuentes grandes o en horizontal.
- Corregido `values-v27/themes.xml`: su override de splash todavía usaba el
  antiguo fondo primario y anulaba el fondo claro/oscuro configurado en fase 3.
  El contrato de temas ahora comprueba también el atributo nativo resuelto.

No se cambian Firebase, interfaces de repositorio, reglas remotas, Kotlin/AGP,
dependencias ni minSdk. No hay commit, push ni despliegue.

## Evidencia de regresión

Antes de corregir, las primeras 6 pruebas de política reprodujeron **4 fallos**:

1. Un proyecto incompleto alcanzaba el repositorio local (1 escritura en vez de 0).
2. Perfil generaba una actualización con profesión/descripcion sin validar.
3. Un nombre compuesto por espacios era aceptado.
4. Login construía credenciales con `not-an-email`.

Después de corregir, esas 6 pruebas pasaron completas. Se añadieron además
controles de doble envío y de conservación de actualizaciones válidas.
Los repositorios de prueba son límites locales: no se autentica ni se escribe
en Firebase para verificar estas políticas.

## Matriz de verificación

Android 14: matriz inicial de 58 pruebas en vertical y 50 en horizontal,
ambas completas. Tras reforzar las aserciones de carga y corregir el recurso
nativo del splash, repetición dirigida con el APK final: **36/36 vertical**
(10 políticas + 24 UI + 2 temas) y **26/26 horizontal** (24 UI + 2 temas).
Además, **12/12 pruebas de formularios en horizontal con fuente del sistema
al 200 %**, conservando la densidad original de Android 14 y su escalado no lineal.

Logs: `build/phase4-api34-portrait-final.log`,
`phase4-api34-landscape-final.log`, `phase4-api34-portrait-targeted.log`,
`phase4-api34-landscape-targeted.log` y `phase4-api34-systemfont.log`.
Build debug/androidTest correcto; **6 pruebas unitarias sin fallos**;
`git diff --check` sin errores de whitespace.

Evidencia exportada: 104 capturas en `build/phase4-api34-qa/` y 24 en
`build/phase4-api34-systemfont-qa/` (artefactos ignorados por Git).
Inspección visual realizada de error español oscuro a 320 dp/200 %, estado
de error y formulario de proyecto horizontal con teclado y fuente nativa 200 %.
El botón permanece accesible sobre el IME; el contenido superior puede desplazarse.

Android 13: regresión horizontal **26/26** y drawer vertical **2/2**,
en `build/phase4-api33-landscape.log` y `build/phase4-api33-drawer.log`.
La primera ejecución vertical no cuenta como matriz aprobada: incluyó un nombre
de clase inexistente (`DrawerAccessibilityTest`) y terminó interrumpida.
La repetición vertical con selectores correctos pasó **36/36** (10 políticas,
24 UI y 2 temas), en `build/phase4-api33-portrait-targeted.log`.
No fue un fallo de producto. No se reclama una matriz vertical completa de 60
en Android 13. La exportación adicional de capturas de esa repetición falló
por ruta de salida incorrecta; los resultados de instrumentación sí se conservaron.
La exportación anterior sí conserva 76 capturas en `build/phase4-api33-qa/`.

- `FormValidationPolicyTest`: ViewModels y delegado reales; vacío, espacios,
  formato de correo, longitud 50/51, restauración de valor/error, doble envío y
  rutas válidas de actualización. Una contraseña conserva sus espacios.
- `PhaseFourUiTest`: **24 casos** por orientación; temas claro/oscuro ×
  español/inglés × 6 pruebas, contenido realmente limitado a 320 dp y fuente
  al 200 %. Comprueba texto sin overflow, carga, contraseña deshabilitada,
  navegación de foco y estados vacíos/error.
- Se repite la matriz previa de 26 casos: 12 de formularios con IME real,
  10 de componentes M3, 2 de drawer y 2 de contrato de temas.
- Los formularios comprueban orientación efectiva, bounds sin recortar sobre
  el teclado y toolbar compacta oculta/restaurada; no se relajan aserciones.
- Build debug y androidTest, 6 pruebas unitarias y `git diff --check`.

Emuladores dedicados y descartables (`-read-only`), sin compartir instrumentación
con el emulador de otras tareas:

- Android 14 / API 34: `Codex_Alarm_API_34`.
- Android 13 / API 33: `Medium_Phone_API_33`.

## Límites y fase siguiente

- API 21, Android 15/16, RTL y otras variantes de teclado no se consideran
  verificados. No hay imagen arrancable de API 35/36 instalada al iniciar esta
  fase (API 36 solo contiene metadatos de instalación incompleta).
- Los tests de layout inyectan escala de texto 2.0; se complementan con 12
  ejecuciones de formularios usando la escala nativa Android 14 al 200 %.
  No se verifican todas las escalas intermedias ni todos los dispositivos.
- Restauración comprobada en el delegado con SavedStateHandle, no como prueba
  integral de muerte del proceso y navegación autenticada.
- Sesión real autenticada y roundtrip de cámara/galería/cropper pendientes;
  no se inventan credenciales ni se hacen escrituras remotas para la QA.
- Se conservan los límites de negocio existentes; no se añaden nuevas reglas
  de formato URL ni se amplían longitudes de enlaces en esta fase.
- Fase 5: medir rendimiento release, recomposiciones/lifecycle/flows y perfiles.
  Esta fase no presenta cifras de rendimiento ni afirma mejoras medidas.

## Referencias de plataforma

- [TextField M3: errores y supportingText](https://developer.android.com/reference/kotlin/androidx/compose/material3/TextField.composable).
- [Contenido escalable y escalado de fuentes](https://developer.android.com/develop/ui/compose/accessibility/scalable-content).

## Key Learnings:

1. La ausencia de error inicial no demuestra que un campo requerido sea válido: hay que revalidar todos los campos antes de enviar.
2. Deshabilitar entradas no basta si Guardar sigue activo; los guards de operación en ViewModel complementan la UI contra doble envío.
3. Rechazar campos de texto en blanco no debe modificar silenciosamente las contraseñas: las políticas deben ser explícitas por campo.
4. Un recurso values-v27 puede anular un arreglo de tema en values; comprobar el atributo resuelto detecta estos overrides.
5. Un contenedor desplazable no debe depender de pesos verticales con altura no acotada para mostrar ilustraciones y mensajes.

## Handoff

- Fase 4 aplicada; conservar las modificaciones locales de las fases 1–4.
- Siguiente trabajo: fase 5 de rendimiento, con mediciones antes/después.
- Archivos clave: `PropertySavableString.kt` (política de blancos), ViewModels
  de login/perfil/proyecto (validación y guards), `EditableTextSavable.kt`
  (errores M3), `AnimationScreen.kt` (scroll), `values-v27/themes.xml`
  (splash) y las pruebas `FormValidationPolicyTest`, `PhaseFourUiTest`,
  `FormKeyboardTest` y `MaterialThemeContractTest`.
- Emuladores descartables API 33/34 cerrados; no se modificó el emulador
  compartido ni se realizaron escrituras remotas.
