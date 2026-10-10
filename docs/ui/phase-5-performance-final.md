# Fase 5 — Rendimiento y cierre de migración Material 3

Fecha: 2026-10-10. **TalkBack omitido por indicación del usuario.**
Se conserva el trabajo local de las fases 1–4; sin commit, push ni despliegue.

## Cambios aplicados

- Ocho observaciones de estado en siete pantallas pasan a
  `collectAsStateWithLifecycle`: raíz/autenticación, bloqueo, correos y detalle,
  perfil, proyectos y ajustes. La dependencia de lifecycle ya existía.
  La recolección UI se suspende por debajo de STARTED y recupera el estado
  reciente al volver. No se cambia el timeout de 5 s de los `stateIn` existentes.
- Paginación de proyectos/correos rechaza otra página durante una página en
  curso o un refresco. El efecto de fin de lista reconsidera la carga al acabar
  el refresco, para no perder una solicitud bloqueada temporalmente.
- Se conservan claves estables por ID en listas, `derivedStateOf` para fin de
  scroll y listas lazy. No se añaden anotaciones `@Stable` falsas, cachés nuevas
  ni cambios cosméticos presentados como optimización.
- Variante local `profile`: R8 activo, no depurable y ahora `profileable` para
  herramientas del sistema. Este permiso solo existe en `src/profile`, no
  se habilita en release. No se cambian firma, dependencias ni reglas remotas.
- `scripts/Measure-Startup.ps1` permite repetir arranques de proceso frío
  indicando explícitamente el emulador; valida estado COLD y exporta muestras.

Se mantienen los collectors de eventos/snackbar separados de los estados:
no se altera su política de entrega como optimización incidental.

## Evidencia y mediciones

API 34, AVD descartable `Codex_Alarm_API_34`, misma variante `profile`
optimizada antes/después, siete arranques por APK, `am force-stop` + `am start -W`.
Se descarta la primera muestra tras instalación; no se borra el almacenamiento.

| TotalTime de arranque | Antes | Después |
|---|---:|---:|
| Muestras analizadas | 6 | 6 |
| Mediana | 691 ms | 608,5 ms |
| Mínimo–máximo | 635–775 ms | 570–829 ms |

**No demuestra una aceleración causal.** Son muestras exploratorias de emulador,
con carga variable del host y compilaciones concurrentes; los rangos se solapan.
No son tiempo de interacción completa, Macrobenchmark, percentiles de producción
ni mediciones de un teléfono físico. La mejora comprobable es de política:
en la prueba local la suscripción UI pasa de 1 a 0 al detenerse y vuelve a 1
al reanudarse; el collector anterior permanece en 1 estando detenido.
Esto no equivale a afirmar que todos los productores de la app se detengan.

Logs: `build/phase5-startup-before.log`, `phase5-startup-after.csv` y
`phase5-startup-after-summary.log`; APK de referencia local
`build/phase5-baseline-profile.apk`. Estos artefactos están ignorados por Git.

## Verificación

- Build debug, profile y androidTest; 6 unitarias sin fallos.
- Build release optimizado completado, incluyendo lint vital y R8.
- `PerformanceLifecycleTest`: comparación antes/después de suscripciones,
  actualización mientras está detenido y recuperación del valor más reciente.
- `PaginationPerformanceTest`: ViewModels reales y repositorios locales;
  página durante refresco, doble solicitud y final de resultados.
- Regresión de formularios, temas, componentes M3 y drawer, más IME real
  horizontal a fuente nativa Android 14 del 200 %: resultados finales abajo.
- Una prueba anterior de doble login falló por un repositorio falso que podía
  completar antes de observar el estado ocupado. Se controla ahora su finalización
  con una barrera local también en perfil; se mantienen las aserciones de ocupado,
  segundo envío rechazado y una sola escritura. No se relaja el contrato.

Resultados finales API 34:

| Ejecución | Resultado | Evidencia en `build/` |
|---|---:|---|
| Lifecycle 2 + paginación 2 + formularios 10 + temas 2 + M3 10 + drawer 2 | 28/28 | `phase5-targeted-tests-final.log` |
| Formularios con IME, horizontal y escala nativa 200 % | 12/12 | `phase5-keyboard-retry.log` |
| Unitarias | 6/6 | `app/build/test-results/testDebugUnitTest/` |
| Debug/profile/androidTest | Correcto | `phase5-build-verified.log` |
| Release optimizado | Correcto | `phase5-release-build.log` |

El primer intento IME detectó correctamente que la rotación aún era vertical;
se repitió tras fijar rotación, desactivar autorrotación y esperar su aplicación.
No se relajó la comprobación de orientación. Las 24 capturas de la ejecución
correcta se exportaron a `build/phase5-qa/`.
Además se abrió la MainActivity real sin credenciales a fuente del sistema
200 %, alternando claro/oscuro. Login y controles permanecen legibles;
capturas inspeccionadas: `build/phase5-main-light.png` y
`build/phase5-main-dark.png`. No se envió ningún formulario.

## Cierre y límites

La fase pendiente documentada era la 5; no se inventa una fase adicional.
El diseño y la migración local M3 quedan aplicados con las validaciones de fases
anteriores. Este cierre no es una certificación integral de producción.

- API 21, Android 15/16, RTL y otras variantes de teclado siguen sin verificación
  física. API 33/34 y temas/fuentes se verificaron en fases anteriores.
- Sesión autenticada, cámara/galería/cropper y roundtrip Firebase requieren
  credenciales y un entorno de pruebas; no se realizaron escrituras remotas.
- No se genera un Baseline Profile ni se afirma una reducción de jank sin un
  recorrido representativo y medición controlada. Próximo trabajo opcional:
  Macrobenchmark de inicio/scroll en teléfono físico y perfil de sesión autenticada.
- No se cambia el bloqueo biométrico al salir: el TODO existente sobre selección
  de imágenes queda fuera del alcance para no introducir una regresión de seguridad.

## Referencias

- [Estado y Compose](https://developer.android.com/develop/ui/compose/state).
- [Lifecycle en Compose](https://developer.android.com/topic/libraries/architecture/lifecycle).

## Key Learnings:

1. WhileSubscribed no suspende el productor mientras una UI detenida siga suscrita; la UI Android necesita recolección consciente del lifecycle.
2. Bloquear paginación durante refresco exige reconsiderar la solicitud al finalizarlo, no solo añadir un guard.
3. Una prueba de concurrencia debe controlar cuándo termina su repositorio falso; una respuesta instantánea no garantiza una operación todavía en curso.
4. Una mediana menor en un emulador con carga variable no demuestra una mejora causal de rendimiento.

## Handoff

- Archivos clave: siete pantallas con estados lifecycle-aware, ViewModels de
  proyectos/correos, `src/profile/AndroidManifest.xml`, las dos pruebas nuevas,
  fixture de `FormValidationPolicyTest` y `scripts/Measure-Startup.ps1`.
- Conservar todos los cambios sin confirmar; publicación y medición en dispositivo
  físico siguen siendo decisiones posteriores del usuario.
- Emulador descartable API 34 cerrado al terminar; emulador compartido sin tocar.
