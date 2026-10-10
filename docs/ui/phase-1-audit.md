# Fase 1 — auditoría y correcciones de accesibilidad

Fecha: 2026-10-10. Objetivo: NullSiteApp. Referencia visual: NurseCompose2.
La migración Material 2 → Material 3 pertenece a las fases siguientes.

## Alcance y evidencia

- Revisión estática de temas, tipografía, navegación, listas y formularios.
- Emulador `Codex_UI_API_33`, Android 13, `emulator-5556`.
- Pruebas de componentes de producción con datos locales y callbacks de prueba:
  login, edición de perfil y edición de proyecto. Sin autenticación ni escrituras remotas.
- Las propiedades de texto usan etiquetas/hints de fixture y contenido `Sample`:
  no se valida aquí el ajuste de cada traducción o etiqueta real del ViewModel.
- Matriz IME: claro/oscuro × fuente 100/200 % × vertical/horizontal.
- Cada caso abre el teclado real, escribe en el último campo, desplaza campo y botón,
  verifica sus límites contra el borde superior del teclado, cierra el teclado y
  comprueba que el botón invoca el callback.
- El tamaño de fuente y el tema se inyectan en Compose; no se cambia la fuente global.
- Capturas generadas en `files/phase1-qa` del sandbox de la app; copias locales en
  `build/phase1-qa` (no se versionan). La inspección visual complementa las aserciones.

## Hallazgos y cambios

| Hallazgo | Corrección de fase 1 |
|---|---|
| Blanco sobre el turquesa `#0097A7`: contraste 3.51:1 | `onPrimary` negro explícito en ambos temas: 5.98:1; toolbars usan el token |
| Drawer personalizado sin estado de selección accesible y alto visual de unos 44 dp | `selectable`, rol Tab, alto mínimo 48 dp y `onSecondary`; icono decorativo sin anuncio duplicado |
| Descripción de proyecto con alto fijo 80 dp | Alto mínimo 80 dp, expansible con fuente ampliada; se mantiene resumen de tres líneas |
| Formularios sin reserva explícita de espacio IME | `imePadding` en Scaffold de login, perfil y proyecto; se conserva `adjustResize` del manifiesto |
| Login limitado al alto de pantalla | Alto mínimo, no fijo; conserva scroll y permite crecer con texto ampliado |
| Captura de perfil al 200 % mostró título de toolbar partido y cortado | Una línea con elipsis; semántica conserva el texto del título completo |

El umbral objetivo de texto normal es 4.5:1 y los controles deben ofrecer al menos
48 × 48 dp. Ver [accesibilidad Android](https://developer.android.com/guide/topics/ui/accessibility/apps).
La corrección de IME sigue el manejo de [insets en Compose](https://developer.android.com/develop/ui/compose/system/insets-ui).
Esto no certifica contraste de todos los estados: foco, deshabilitado, errores y
superposición sobre imágenes necesitan una auditoría adicional.

## Referencia de diseño para fase 2

NurseCompose2 usa Material 3, esquemas completos rosa claro/oscuro, superficies
tonales, radios 16/24 dp y fuente del sistema con cuerpo 16 sp / interlineado 24 sp.
NullSiteApp usa Material 2, turquesa con variantes moradas heredadas y sólo body1
personalizado a 16 sp. No se encontró una fuente personalizada en estos temas.

Propuesta: conservar identidad turquesa, incorporar la jerarquía de superficies
y tarjetas de NurseCompose2, definir todos los roles semánticos claro/oscuro y
la escala tipográfica. No copiar ciegamente versiones Gradle de la referencia;
ambos proyectos declaran minSdk 21. Verificar compatibilidad antes de actualizar.
Migrar primero componentes compartidos y luego pantallas, conservando navegación
e insets. [Guía de migración Material 3](https://developer.android.com/develop/ui/compose/designsystems/material2-material3).

## Rendimiento: línea base estática, no mejoras medidas

- Listas de emails y proyectos ya son lazy y tienen claves estables: conservarlas.
- Se encontraron ocho llamadas `collectAsState()` en pantallas: evaluar
  `collectAsStateWithLifecycle()` según la semántica de cada flujo, sin cambiar
  listeners o navegación a ciegas en esta fase.
- No hay resultados de Macrobenchmark, tiempo de arranque, memoria o jank en esta
  auditoría. Un build debug/emulador no demuestra mejoras de rendimiento.
- Medir release/profileable antes y después sobre arranque, proyectos, emails y
  apertura de formularios; revisar recomposiciones y trazas antes de optimizar.
- Generar Baseline Profiles de recorridos críticos tras estabilizar la migración.
  [Referencia oficial](https://developer.android.com/develop/ui/compose/performance/baseline-profiles).

## Pruebas y límites

- `PhaseOneAccessibilityTest`: selección, objetivo táctil y contraste primario en ambos temas.
- `FormKeyboardTest`: 12 combinaciones por orientación; guarda capturas con teclado.
- `:app:testDebugUnitTest`: pruebas unitarias existentes, sin modificar sus aserciones.
- Resultados finales de ejecución: ver sección de cierre a continuación.
- Pendientes: TalkBack manual, navegación completa con sesión autenticada,
  validaciones/errores reales, selector de imagen/bottom sheet, teclado de otros
  fabricantes, pantalla pequeña y Android 15/16 con edge-to-edge obligatorio.
- Las pruebas IME habilitan edge-to-edge en la Activity de prueba y `adjustResize`;
  no equivalen a validar todos los dispositivos o la Activity autenticada real.
- La primera corrida horizontal detectó cuatro errores del test: intentaba
  desplazar el botón de proyecto, que está en una columna fija y ya visible.
  El test ahora sólo desplaza si existe ancestro scrollable y mantiene las
  aserciones de límites no recortados contra el IME.
- No se cambian dependencias, Firebase, reglas, firma, CI ni despliegue.

## Reproducción

```powershell
.\gradlew.bat :app:installDebug :app:installDebugAndroidTest :app:testDebugUnitTest
adb -s emulator-5556 shell am instrument -w -r -e class com.nullpointer.nullsiteadmin.ui.FormKeyboardTest,com.nullpointer.nullsiteadmin.ui.PhaseOneAccessibilityTest com.nullpointer.nullsiteadmin.test/androidx.test.runner.AndroidJUnitRunner
```

Ejecutar la instrumentación una vez por orientación, con teclado virtual habilitado
(en este emulador se activó `show_ime_with_hard_keyboard=1`). La prueba utiliza
una Activity aislada, no necesita credenciales y deja capturas en el almacenamiento
privado de la app debug. No ejecutarla contra una instalación con datos personales.

## Cierre de ejecución

- Compilación Kotlin de app y pruebas: correcta.
- Pruebas unitarias: 6, sin fallos ni omisiones.
- Horizontal, versión final: 14 pruebas correctas (12 IME + 2 accesibilidad).
- Vertical, versión final: 14 pruebas correctas (12 IME + 2 accesibilidad).
- Total final: 28 ejecuciones instrumentadas correctas y 6 pruebas unitarias correctas.
- Login real de `MainActivity` comprobado adicionalmente en vertical: se abrió
  Password con teclado, se escribió texto ficticio y se desplazó hasta mostrar
  ambos campos y Authenticate sobre el IME. No se pulsó Authenticate; el texto
  ficticio se eliminó y se cerró el teclado. El botón requiere scroll, no está fijado
  permanentemente encima del IME. Captura: `build/phase1-qa/actual-login-ime-scrolled.png`.
- Captura revisada tras corrección: título de perfil al 200 % ya usa elipsis,
  botón Guardar visible sobre el teclado; también se revisó perfil oscuro horizontal.
- Se exportaron las 48 capturas de la matriz a `build/phase1-qa`; orientación
  vertical/rotación automática restauradas. Teclado virtual quedó habilitado para QA.
- `git diff --check`: sin errores. Sin commit, push ni publicación.

## Key Learnings:

1. El turquesa actual admite texto negro 5.98:1; blanco sólo alcanza 3.51:1.
2. La revisión de capturas detectó recorte del título al 200 %, además de los casos IME automatizados.
