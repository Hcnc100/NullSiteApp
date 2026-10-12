# Publicación única a Google Play

El usuario autorizó push y publicación de 5.2.0 después del preflight local.
Una única vía: push a `main` → workflow → Play Production. No subir además el
AAB local ni disparar otro workflow. La concurrencia serializa las ejecuciones.

Antes de subir, el job deploy crea atómicamente el ref
`refs/tags/play-production-reserved/v<versionName>`. Un ref existente omite la
subida, incluso en reruns o commits diferentes con el mismo versionName.
Solo el job deploy tiene permiso `contents: write`; build conserva lectura.
Se prueban primer intento, rerun, mismo nombre en otro commit, carrera, versión
inválida y fallos de permisos/red: 6 pruebas Node sin dependencias.

**Reserva no significa publicación correcta.** Si Play responde con error o hay
una interrupción después de reservar, NO se borra ni reintenta automáticamente:
verificar primero Play y el log. Una respuesta ambigua puede haber publicado.
La recuperación manual requiere reconciliar el estado y autorización; una nueva
release normal necesita incrementar versionName y un versionCode mayor.
No borrar el ref para forzar reintentos sin comprobar Play. Esta protección cubre
este workflow, no subidas manuales externas ni versiones publicadas antes de
introducir el registro. Google Play sigue imponiendo unicidad del versionCode.

El environment `production` no tenía configuración accesible durante la revisión
(API devolvió 404); no se afirma que exista aprobación manual configurada. La
autorización de publicación de esta entrega proviene del usuario en esta sesión.

## Key Learnings:

1. Concurrencia evita solapamientos, pero no duplicados secuenciales: se necesita
   una reserva persistente por versión antes del efecto externo.
2. Ante resultados ambiguos de Play, fallar cerrado evita repetir una publicación.
