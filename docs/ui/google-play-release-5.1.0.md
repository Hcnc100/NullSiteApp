# Publicación Google Play — 5.1.0

Fecha: 2026-10-10. App: `com.nullpointer.nullsiteadmin`.

## Capturas y ficha

- Revisadas las capturas reales de acceso y las familias de evidencia QA de las
  fases 1–5. Las pruebas de teclado, errores y datos Sample no son material de
  tienda: se conservan únicamente como evidencia local ignorada por Git.
- La ficha antigua incluía splash y pantallas con el diseño previo. Se reemplazan
  sus cuatro capturas de teléfono por dos capturas reales de acceso Material 3,
  claro/oscuro, fuente normal, sin teclado, credenciales ni datos personales.
- Nuevas imágenes originales PNG 1080×2340, sin retoque generativo ni recorte:
  `distribution/listing/es-419/images/`. No se inventa una sesión autenticada
  para mostrar perfil o proyectos; ampliar esa galería requiere acceso autorizado.
- Descripciones breve/completa actualizadas en español latinoamericano, con
  funciones existentes, cuenta autorizada y sin promesas de velocidad no medidas.
  Textos reproducibles en `distribution/listing/es-419/`.
- Ícono y gráfico de funciones existentes conservados. No se cambian privacidad,
  seguridad de datos, precios, países ni condiciones contractuales.
- Ficha guardada en Console: «Cambios listos para enviar a revisión».
- Las verificaciones rápidas señalaron falta de claridad en la primera redacción.
  Se precisó que es el panel privado del sitio NullSite de NullPointerDev, sus
  funciones concretas por pantalla y sus límites: sin registro, sin creación de
  sitios ni administración de sitios de terceros. Nueva comprobación en curso.
- Notas de versión en español e inglés: `distribution/whatsnew/`.

## Una sola vía de despliegue

- Push a `main` dispara el único workflow existente, `main.yml`.
- Se retira `workflow_dispatch`: no se ejecuta además una publicación manual.
- Concurrencia `google-play-production`, sin cancelar una publicación en curso.
  Serializa ejecuciones; no se presenta como deduplicación universal de commits.
- Solo los cambios de documentación/Markdown no disparan otro despliegue.
- CI construye un AAB con `versionCode = 1000 + github.run_number`, lo sube a
  producción con las notas nuevas y envío automático a revisión.
- Esta app no admite `changesNotSentForReview: true`: Google rechazó el commit
  del edit en la ejecución 24. No se confirmó esa publicación. Se retira el
  parámetro y se realiza una nueva ejecución, sin carga manual en paralelo.
- Después de CI se comprueba Console; si la ficha aún está pendiente, se envía
  únicamente ese cambio. El envío de revisión **no sube un segundo AAB**.
- No se ejecuta `gh workflow run`, no se sube el bundle local y no se crea una
  segunda versión desde el navegador. Google decide aprobación y disponibilidad.

## Verificación previa

- `bundleRelease -PversionCode=1024` y unitarias: build correcto, 6 unitarias
  sin fallos. Evidencia: `build/play-release-verification.log`.
- Revisión del índice Git: sin archivos de credenciales/keystore ni patrones de
  clave privada/token añadidos; esto no sustituye una auditoría de seguridad total.
- Se conservan las comprobaciones UI y los límites documentados en fases 1–5.
- Emulador dedicado cerrado; emulador compartido sin cambios.

## Resultado

Push inicial: `3845389`, ejecución 24 (`38083496142`), build correcto; carga de
1024 completada pero commit del edit rechazado por la opción de revisión.
No se cuenta como publicación exitosa.

Corrección: `1ed4209`, [ejecución 25](https://github.com/Hcnc100/NullSiteApp/actions/runs/38084048516).
Build y deploy **success**. Google Play aceptó un AAB 5.1.0, versionCode **1025**;
log: «Successfully uploaded 1 artifacts» y «Successfully committed».

Console confirmó en **Cambios en la etapa de revisión**:

- Producción: **5.1.0 — Iniciar lanzamiento completo**.
- Ficha es-419: descripción breve, descripción completa y capturas de teléfono.
- Verificaciones rápidas de Google aún en curso al cerrar; no se afirma que la
  versión ya esté visible ni que la aprobación esté garantizada. Con publicación
  administrada desactivada, Google la publicará si aprueba los cambios.

No hubo carga manual de AAB ni workflow_dispatch ni segunda versión exitosa
para este trabajo. Hubo dos intentos CI (uno rechazado y uno confirmado), no dos
despliegues efectivos. Los textos finales y este registro se sincronizan con
un commit **[skip ci]** exclusivamente de metadatos/documentación para no crear
otro AAB/versionCode. El código de la versión aceptada permanece sin cambios.

Evidencia local ignorada: `build/play-ci-failure.log`, `play-ci-success.log`,
`play-ci-final-watch.log`. Página de Console abierta como resultado verificable.

## Key Learnings:

1. Push y carga manual de AAB son vías distintas: elegir solo CI evita dos publicaciones para el mismo trabajo.
2. Esta app exige envío automático a revisión: changesNotSentForReview=true rechaza el commit del edit aunque el AAB ya se haya cargado.
3. Las capturas de instrumentación con datos de ejemplo no deben confundirse con una sesión real para la tienda.

## Handoff

Código, pruebas, textos e imágenes de tienda versionados; resultados finales
de revisión se documentarán sin disparar otra publicación del binario.
Pendiente externo: resultado de verificaciones/aprobación de Google. Si vuelve
a aparecer el problema de claridad, revisar el motivo concreto y el acceso de
revisión de la cuenta, sin inventar credenciales ni prometer aprobación.

Referencias: [notas de la acción Play](https://github.com/r0adkll/upload-google-play/blob/v1.1.3/README.md),
[omitir workflows de GitHub](https://docs.github.com/en/actions/how-tos/manage-workflow-runs/skip-workflow-runs).
