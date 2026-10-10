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
No se cuenta como publicación exitosa. Corrección y ejecución final pendientes.

## Key Learnings:

1. Push y carga manual de AAB son vías distintas: elegir solo CI evita dos publicaciones para el mismo trabajo.
2. Esta app exige envío automático a revisión: changesNotSentForReview=true rechaza el commit del edit aunque el AAB ya se haya cargado.
3. Las capturas de instrumentación con datos de ejemplo no deben confundirse con una sesión real para la tienda.

## Handoff

Código, pruebas, textos e imágenes de tienda versionados; resultados finales
de revisión se documentarán sin disparar otra publicación del binario.
