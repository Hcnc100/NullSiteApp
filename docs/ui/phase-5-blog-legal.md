# Fase 5 — Blog y documentos legales

## Alcance y decisión

Integración híbrida con el administrador web existente. Android permite crear
borradores privados de texto inicial y editar título, slug, extracto, idioma ES/EN
y etiquetas. Conserva el HTML enriquecido, autoría, estado, campos desconocidos y
referencias de publicación. La revisión aumenta mediante transacción; el baseline
SHA-256 incluye el cuerpo y la metadata remota. SavedState guarda la huella, no HTML.
Colisiones de slug conservan la edición; conflictos bloquean reintentos hasta
descartar/recargar. La reserva anterior solo se elimina si pertenece al borrador.

El editor enriquecido, imágenes, publicar/retirar/eliminar y edición/publicación
legal permanecen en `/admin/blog` y `/admin/legal` del sitio configurado. Se abre
el navegador externo con inicio de sesión web independiente, sin tokens Firebase,
WebView ni puente JavaScript. El origen debe ser HTTPS raíz, sin credenciales,
query, fragmento ni puerto distinto de 443. El diálogo advierte que los cambios
locales no viajan al navegador; al regresar se conserva el borrador y se recarga
explícitamente. Una modificación remota exige resolver el conflicto.

Android no escribe `blog-posts`, `terms`, borradores legales/historial ni imágenes
públicas. La web mantiene sanitización DOM, instantáneas públicas, historial
inmutable y versiones de imágenes; no se improvisó un sanitizador nativo.

## Límites de la primera integración

- Vista previa de los primeros 20 documentos por ID en cada colección; detecta
  truncamiento con 21 y avisa. No es un catálogo paginado completo. Ausencia en
  esta vista no significa ausencia remota. Historial: 20 revisiones recientes.
- Título 180, slug 100, extracto 500; 20 etiquetas únicas de hasta 40 caracteres.
  Idioma ES/EN. Texto inicial 10.000, convertido a HTML escapado.
- Borradores con formato/revisión incompatibles o etiquetas que contienen coma
  se consultan, pero se editan en la web para evitar pérdida de información.
- El documento legal no tiene idioma en el contrato web: no se inventan variantes.
- Referencia de publicación no equivale a confirmar que el artículo esté visible;
  se informa también `blogEnabled`.

## Verificación local

- 75/75 pruebas JVM, incluidas 9 nuevas de contratos editoriales y paridad ES/EN.
- 67/67 pruebas Vitest existentes de blog/legal, 7 archivos, en el repositorio web.
- 94/94 pruebas de reglas Firestore/Storage en emuladores aislados de Firebase,
  proyecto demo, Java 21, puertos 18085/19195. No se tocaron datos de producción.
- Emulador Android API 33: pruebas de datos y UI con ES/EN, oscuro/claro, fuente
  200%, validación, conservación del borrador al abrir web e historial legal.
  Resultados finales y evidencia en el informe de release.
- Release lint sin errores; advertencias existentes se detallan en release.

## Correcciones de release

`shareViewModel` usa `LocalActivity` en lugar de convertir `LocalContext`, que puede
ser un ContextWrapper. Se eliminó la cadena de servicio antiguo de foto sin
consumidores, no declarado en manifest y con guardado comentado; no se habilitó
un servicio obsoleto para silenciar lint. La subida actual protegida sigue intacta.

## Pendientes de aceptación

Login real del editor web/admin, publicación/retirada con imágenes y rebase legal
en entorno autorizado; pruebas end-to-end del repositorio Firebase Android y
muerte de proceso real; dispositivos API 34–36 y accesibilidad manual completa.
Las pruebas de reglas reales no convierten los dobles de repositorio Android en
pruebas del SDK. No se modificaron ni desplegaron reglas del repositorio web.

## Key Learnings:

1. Publicar blog implica versionar imágenes y comprometer su instantánea; editar
   metadatos privados no debe sobrescribir la referencia pública.
2. El baseline del blog debe cubrir también HTML aunque el móvil no lo edite.
3. Un origen HTTPS fijo y login independiente evitan transferir sesiones al navegador.
