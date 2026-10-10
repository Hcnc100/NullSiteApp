# Fase 2 — sistema visual Material 3

Fecha: 2026-10-10. Referencia: NurseCompose2; identidad: NullSiteApp turquesa.

## Implementación

- `NullSiteAdminTheme` ahora proporciona Material 3 y un adaptador Material 2
  temporal, ambos derivados de un único esquema. Las pantallas existentes siguen
  funcionando sin migrar sus APIs de navegación, Scaffold o bottom sheet.
- Paletas completas claro/oscuro en `Color.kt`: primarios, secundarios, terciarios,
  contenedores, superficies tonales, inversos, errores, bordes y scrim explícitos.
  Se eliminan los colores morados del tema Compose. Sin colores dinámicos de wallpaper.
- `AppTypography`: 15 roles, fuente del sistema, tamaño/interlineado/tracking en
  sp; cuerpo 16/24 sp y tracking 0.5 sp. Adaptador de 13 roles M2 sin fuentes remotas.
- `AppShapes`: radios 16 dp; contenedores extra grandes 24 dp. Tarjetas de proyectos,
  correos, detalles de correo, login y selector de imagen consumen tokens de formas
  en lugar de radios aislados. No se modifican los recortes de fotografías.
- Iconos de correo leído/no leído y placeholder de perfil usan colores semánticos;
  ya no dependen de negro/blanco fijo ni del tema del sistema consultado por separado.
- Paletas, estilos, formas y adaptadores son objetos de archivo reutilizados;
  no se generan esquemas ni se descargan fuentes por recomposición.
- Se mantienen las correcciones IME, alturas flexibles, selección accesible y
  elipsis de fase 1. No se promete una ganancia de rendimiento sin medirla.

## Paleta e identidad

| Rol | Claro | Oscuro |
|---|---|---|
| primary | `#006875` | `#4FD8EB` |
| onPrimary | `#FFFFFF` | `#00363E` |
| primaryContainer | `#9BEFFF` | `#004F59` |
| background / surface | `#F5FAFB` | `#0E171A` |
| onBackground / onSurface | `#152023` | `#DDE3E5` |
| surfaceContainer | `#E9EFF1` | `#1B2528` |
| secondary | `#4B6268` | `#B2CBD1` |
| error | `#BA1A1A` | `#FFB4AB` |

El turquesa claro se oscurece respecto de fase 1 para permitir contenido blanco
accesible. El tema oscuro usa turquesa luminoso con contenido oscuro. Se adopta
la jerarquía de superficies y formas de NurseCompose2, no su paleta rosa.

Contraste calculado con luminancia relativa sRGB (foreground/background opacos):

| Par semántico | Claro | Oscuro |
|---|---|---|
| primary / onPrimary | 6.49:1 | 7.73:1 |
| primaryContainer / onPrimaryContainer | 13.24:1 | 7.15:1 |
| secondary / onSecondary | 6.47:1 | 7.70:1 |
| surface / onSurface | 15.80:1 | 14.01:1 |
| surfaceVariant / onSurfaceVariant | 7.26:1 | 5.51:1 |
| error / onError | 6.46:1 | 7.72:1 |

No equivalen a certificar todos los estados animados, deshabilitados o con imágenes.

## Compatibilidad y alcance

- Añadido `androidx.compose.material3:material3:1.3.2`. El manifiesto del AAR
  `material3-android:1.3.2` declara minSdk 21; se conserva minSdk 21 y targetSdk 36.
- Compose UI/Material 2 siguen en 1.8.3; sin actualización de Kotlin, AGP,
  Firebase, reglas, firma ni pipeline. La versión M3 se fija por compatibilidad,
  no como afirmación de que sea la más reciente.
- `Theme.kt` mapea todos los roles M2 al esquema M3; M2 y M3 son bibliotecas
  distintas. El adaptador se retirará después de migrar todos los consumidores.
  [Guía oficial de migración](https://developer.android.com/develop/ui/compose/designsystems/material2-material3).
- No es una migración completa de componentes: Material 3 nativo está instalado
  y tematizado, pero las pantallas heredadas aún utilizan componentes Material 2.

## Verificación

- `MaterialThemeContractTest`: ambos temas, igualdad de los 12 roles del puente
  y modo claro/oscuro, 19 pares de contraste de texto >=4.5:1 por tema, outline
  sobre surface >=3:1, cuerpo 16/24/0.5 sp y formas compartidas.
- Renderizado nativo M3 de Surface, Card, Button y Text con capturas.
- `PhaseOneAccessibilityTest`: selección/48 dp/contraste en menú, ambos temas.
- `FormKeyboardTest`: login, perfil y proyecto; claro/oscuro, fuentes 100/200 %,
  ambas orientaciones y teclado real. Ahora usa las etiquetas/hints reales de
  los ViewModels, con valores locales `Sample` y sin autenticación ni escrituras.
- Los formularios verifican límites no recortados del último campo y botón
  contra el IME y su disponibilidad mediante scroll, no que todo sea visible
  simultáneamente. Se mantiene la Activity aislada edge-to-edge.
- Capturas separadas de fase 1: argumento `qaOutputDirectory=phase2-qa`;
  almacenamiento debug `files/phase2-qa`, copia local `build/phase2-qa`.
- Render M3 claro y formulario de perfil oscuro al 200 % revisados visualmente:
  nueva paleta, curvas, tipografía, icono de perfil visible y botón sobre el IME.
- Las previews nativas usan `onRoot().captureToImage()` (PixelCopy): la captura
  inmediata con UiAutomation había producido un frame vacío aunque Compose estaba listo.
- La corrida vertical inicial completó 16 pruebas correctas. La horizontal se
  interrumpió por timeout y caída del sistema del emulador (`DeadSystemException`,
  servicios activity/package/settings no disponibles). Se reinició sin borrar datos;
  no se considera esa corrida interrumpida como validación completa.
- Resultados finales: los 32 casos únicos de la matriz UI pasaron entre corridas
  y reintentos; 6 pruebas unitarias pasaron, sin omisiones.
- Horizontal: la corrida completó 16 tests, con 15 correctos y 1 fallo de teardown
  en `lightThemeContract`: ActivityScenario no alcanzó DESTROYED desde PAUSED.
  Ese método se repitió aislado en horizontal y pasó (`OK (1 test)`, 11.486 s).
  No se eliminaron ni relajaron las aserciones. No fue una corrida completa libre
  de fallos: los reintentos y el cierre de Activity del emulador se documentan explícitamente.
- El transporte MCP de la corrida larga agotó 300 s, pero la instrumentación
  siguió en el dispositivo. Su cierre se confirmó en logcat de TestRunner:
  `run finished: 16 tests, 1 failed, 0 ignored` (PID 4978). El reintento aislado
  sí devolvió el resultado completo al cliente.
- Tras reiniciar, las previews verticales pasaron de nuevo (2 pruebas) con
  capturas PixelCopy correctas. Otro intento horizontal excedió el timeout del
  cliente; logcat mostró demoras de transición/resume de ActivityScenario.
  El cierre horizontal se repite con mayor margen y animaciones del sistema
  temporalmente desactivadas; se restauran sus valores al terminar.
- Se exportaron 52 capturas originales a `build/phase2-qa`: 48 de formularios
  y 4 muestras M3 (dos temas, dos orientaciones), separadas de fase 1.
- `dependencyInsight` confirmó M3 Android 1.3.2; manifiesto combinado conserva
  minSdk 21 / targetSdk 36. Compilación e instalación debug correctas.
- `git diff --check` correcto. Configuración de rotación/animaciones restaurada
  y aplicación abierta en el emulador. Sin commit, push ni despliegue.

## Reproducción

```powershell
.\gradlew.bat :app:installDebug :app:installDebugAndroidTest :app:testDebugUnitTest
adb -s emulator-5556 shell am instrument -w -r -e qaOutputDirectory phase2-qa -e class com.nullpointer.nullsiteadmin.ui.FormKeyboardTest,com.nullpointer.nullsiteadmin.ui.PhaseOneAccessibilityTest,com.nullpointer.nullsiteadmin.ui.MaterialThemeContractTest com.nullpointer.nullsiteadmin.test/androidx.test.runner.AndroidJUnitRunner
```

Ejecutar por orientación con teclado virtual disponible. El script de validación
restaura orientación, autorrotación y configuración del teclado a sus valores previos.

## Fase 3 y pendientes

1. Migrar toolbars, controles, tarjetas, drawer, Scaffold, diálogos y bottom sheets
   a APIs M3, por pantalla. Eliminar adaptadores y dependencia M2 al terminar.
2. Revisar elevación, composición del login y navegación adaptativa; no atribuir
   a los tokens cambios de estructura que aún no se han implementado.
   Alinear también el splash XML (`primary_color` aún `#0097A7`), fondo de ventana
   y barras del sistema con el tema final; los recursos nativos no cambian en esta fase.
3. TalkBack manual, errores/validaciones reales, sesión autenticada, variantes de
   teclado, locales y Android 15/16 siguen pendientes. No se ha probado API 21
   en dispositivo: se verificó compatibilidad declarada y compilación.
4. Medir rendimiento release antes de optimizar lifecycle/flows y generar perfiles.

## Key Learnings:

1. Compartir una sola paleta entre M2 y M3 evita defaults morados y divergencias durante la migración gradual.
2. Material 3 1.3.2 declara minSdk 21; actualizar indiscriminadamente podría romper la compatibilidad existente.
