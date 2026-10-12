# Fase 3 — proyectos enriquecidos

## Objetivo y alcance

Integración local Android del contrato actual de NullSiteWeb
(`src/app/models/Project.ts`, `project-media.ts`, `managed-content.ts`). No se
modificaron la web, reglas, claims ni datos Firebase. No hay despliegues ni commits.

## Implementación

- Demo/tienda `urlPlay`, enlaces etiquetados, IDs de tecnologías, orden,
  descripción accesible y miniatura de portada.
- Traducciones ES/EN de nombre, descripción y alt de portada, conservando otros
  idiomas/campos mediante parches de rutas individuales.
- Galería compatible con `gallery` y `galleryItems`: edición por URL, miniaturas,
  alt base/ES/EN, eliminación y botones de reordenación. Los metadatos y extensiones
  JSON de una imagen viajan con ella; se sincronizan URL y orden al editar.
- Selección múltiple de documentos, hasta 20 por lote; subida original y miniatura
  WebP a `projects/{id}/{uuid}...`, sin sobrescribir imágenes existentes. Progreso,
  cancelación y reintento de pendientes. Los éxitos parciales quedan en el borrador;
  un fallo no publica el documento ni vuelve a subir los éxitos al reintentar.
- Se validan JPEG/PNG/WebP/GIF, máximo 10 MB, MIME de origen y tipo/dimensiones
  decodificables. Lectura limitada y decodificación muestreada para miniaturas.
- Borrador de campos y URI pendientes en SavedStateHandle; se solicitan permisos
  persistentes del selector. Si el proveedor no los permite, un reintento tras
  reiniciar puede requerir seleccionar de nuevo los archivos.
- Confirmación al abandonar cambios pendientes y bloqueo de guardado durante
  subidas o mientras existan pendientes; se puede cancelar sin publicar.

## Caché y guardado cruzado

Room pasa de **5 a 6** añadiendo `projects.contentJson TEXT NOT NULL DEFAULT '{}'`.
La migración no elimina proyectos ni correos. Se registró explícitamente y se
retiró el fallback destructivo: una base anterior a v5 sin ruta de migración falla
de forma segura, no borra datos silenciosamente. No se inventó una migración de
versiones históricas cuyos esquemas no están disponibles.

El baseline original incluye todos los campos enriquecidos editables. Las formas
antiguas de `galleryItems` conservan la presencia de claves opcionales, evitando
conflictos falsos por miniaturas/traducciones ausentes. Los DTO no serializan
`content`, `originalContent`, baseline ni metadatos internos. Solo actualizan los
campos cambiados y conservan campos web desconocidos fuera del parche.

## Publicación y privacidad

El switch cambia `published` únicamente cuando el usuario lo modifica. No se
convierte una edición de otro campo en un cambio implícito de publicación.
**No publicado no equivale a privado**: las reglas web permiten leer
`last-projects` y objetos `projects/` públicamente. La interfaz lo advierte.
Implementar borradores privados requeriría otra colección/ruta y reglas; no se
simula privacidad cambiando solo un booleano.

## Límites

- Nombre 120, descripción 10000, URLs HTTPS 2048 sin credenciales.
- Alt base y ES/EN: 300; enlaces: 10, etiqueta 120; tecnologías: 30, ID móvil 120.
- Máximo 60 URLs **combinadas**, incluyendo portada, galería, enlaces, repositorio
  y demo, como el validador web; por ello pueden caber menos de 60 capturas.
- No se permiten URLs duplicadas en la galería móvil para evitar ambigüedad de
  asociación de metadatos. Los botones de posición mantienen el orden explícito.

## Verificación

- **54/54 pruebas JVM**: contrato, parches, conflictos, borradores y paridad ES/EN.
- **7/7 pruebas instrumentadas en vertical**: migración SQLite/Room real,
  fallos/cancelación/reintento con uploader falso y cuatro variantes del editor.
- **18/18 en horizontal**: cuatro variantes del editor y 14 regresiones de
  validación/tema. Emulador `Codex_UI_API_33`, Android 13/API33.
- Variantes: claro ES 100 %, claro EN 200 %, oscuro ES 200 %, oscuro EN 100 %.
  Se verifican reordenación con metadatos, rechazo HTTP, traducción, aviso al salir
  y guardado explícito de publicación. Etiquetas largas limitadas a una línea y
  errores HTTPS junto al campo; inspección visual de capturas vertical/horizontal.
- APK compilado: `app/build/outputs/apk/debug/app-debug.apk`; `git diff --check`
  sin errores. XML de evidencia en `build/phase3-test-evidence/` (local, no versionado).

Se corrigió el test para no buscar un contenedor desplazable en el botón estático
de guardado horizontal. El emulador degradado se reinició en frío sin borrar sus
datos; las ejecuciones interrumpidas o fallidas no cuentan como aceptación.

Capturas seleccionadas en [phase3-project-qa](phase3-project-qa/): imágenes locales
deterministas mediante Coil, no fotos ni datos de producción.

## Pendientes de aceptación externa

El uploader Firebase está implementado pero su conexión real, permisos de Storage,
reglas y transacciones no se prueban con datos de producción. Los tests usan
repositorios y uploader falsos para no subir imágenes ni publicar proyectos. Los objetos huérfanos
de una subida interrumpida o un conflicto no se borran automáticamente; tampoco
se eliminan recursos históricos al quitarlos del documento. Una limpieza necesita
verificar referencias y autorización por separado.

La restauración por SavedStateHandle no sustituye una prueba real de muerte de
proceso. Falta probar el peor tamaño de borrador (60 entradas y traducciones largas)
frente al límite del Bundle, más dispositivos y la entrega real desde CDN. Las
capturas usan un fetcher local con bitmaps de prueba para validar imagen y controles.

## Continuidad

Fase 3 implementada y validada localmente, sin commit, despliegue ni escritura en
Firebase de producción. La fase 4 (habilidades/CV) queda pendiente de autorización.
Archivos principales: `ProjectContent.kt` (contrato web), `ProjectContentMigration.kt`
(Room), `ProjectContentEditor.kt`/`EditProjectViewModel.kt` (edición),
`FirebaseProjectImageUploader.kt` (subidas) y `ProjectPhaseThree*Test.kt` (aceptación).

## Key Learnings:

1. El límite web de 60 incluye todas las URLs del proyecto, no solo las capturas.
2. Reordenar URLs sin mover alt, traducciones y miniaturas rompe la accesibilidad.
3. Las claves opcionales ausentes deben conservar su forma en el baseline remoto.
4. La migración debe conservar también correos, aunque el cambio sea de proyectos.
5. Un proyecto no publicado sigue siendo público a nivel de reglas actuales.
