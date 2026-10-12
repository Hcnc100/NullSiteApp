# Integración de mejoras web en Android — 5 fases

Base: [comparativa del 11/10/2026](comparativa-web-android-2026-10-11.md).
El usuario autorizó planificar todas las fases e implementar la primera. No incluye
despliegues, cambios de reglas/claims, publicación de contenido ni una reescritura.

## Fase 1 — Compatibilidad y guardado seguro

**Objetivo:** evitar que un editor móvil obsoleto sobrescriba cambios hechos en la
web y preparar contratos seguros para las fases siguientes.

**Implementado localmente:**

- Baseline original obligatorio en la solicitud de edición, separado del DTO
  enviado a Firestore. Se propaga por repositorio y datasource.
- Perfil y proyecto comparan dentro de una transacción los campos que Android
  conoce; si cambiaron o desapareció el documento, no se escribe. Crear perfil
  exige que siga sin existir. No se recrea un documento eliminado mientras se editaba.
- Updates parciales: preservan redes, traducciones, galerías y demás campos web.
  El editor de proyectos envía solo los campos cambiados y no reescribe `published`.
- Baseline y borrador sobreviven a restauración con `SavedStateHandle`; una nueva
  emisión de datos no sustituye silenciosamente la base de un formulario abierto.
- Mensaje de conflicto ES/EN: volver, actualizar y reabrir el editor. No se hace
  reintento ciego ni se descartan los textos automáticamente.
- Límites por recurso alineados con validadores web: perfil 120/180/5000 y
  proyecto 120/10000; URL 2048. Repositorio opcional.
- Login nuevo exige claim booleano `admin`; ante fallo de validación cierra la
  sesión Firebase creada en ese intento. Se explican denegaciones de permisos.
- Fotos con nombre único dentro de `imgProfile`: una subida no reemplaza el
  archivo publicado antes de que la transacción del perfil haya tenido éxito.

**Verificación automática:** consultar el resultado registrado al final de este
documento. Incluye políticas de conflictos, contratos de parches y propagación de
baseline/conflicto al repositorio; no simula dos dispositivos Firebase reales.

**Límites deliberados:**

- La comparación cubre campos editables conocidos por Android, no timestamps ni
  todo el documento. Un cambio solo en galería/redes no bloquea un cambio de texto
  base y se conserva. No detecta una edición que haya vuelto exactamente al valor original.
- No modifica datos locales Room ni requiere migración: los campos enriquecidos
  se añadirán en las fases 2–3.
- El claim se comprueba en nuevos inicios de sesión; sesiones locales ya abiertas
  siguen dependiendo de las reglas Firebase. No se asignan claims desde el cliente.
- No convierte proyectos en borradores privados: las reglas web locales permiten
  lectura pública y el flag histórico no es una barrera de privacidad.
- No se borran fotos anteriores ni subidas huérfanas tras un conflicto: pueden
  existir referencias externas. Un mantenimiento futuro debe comprobar referencias.
- Fotos y documento no forman una transacción común de Storage/Firestore. Se evita
  sobrescribir la foto publicada; puede quedar una subida nueva sin referencia.

**Aceptación de integración pendiente (entorno de prueba, no producción):**

1. Abrir perfil en Android; cambiar descripción desde web; guardar móvil: debe
   mostrar conflicto, no guardar y conservar lo escrito hasta salir.
2. Repetir para proyecto y para eliminación remota del documento.
3. Cambiar solo un campo web desconocido por Android: edición móvil de texto debe
   conservarlo. Probar documentos antiguos sin campos opcionales.
4. Restaurar el proceso con borrador, editar desde web y guardar móvil: debe
   comparar contra la base original, no contra el último valor remoto.
5. Subir foto y provocar conflicto: la URL/imagen anterior debe seguir publicada.
6. Cuenta sin `admin`, con `admin: true` y con permiso revocado; revisar reglas y
   errores reales. Verificar las rutas de Storage antes de distribuir.
7. Comprobar visualmente contadores largos, campos vacíos opcionales y mensajes.

## Fase 2 — Perfil completo y bilingüe

**Entregas:** modelos de redes y `translations.es/en`, persistencia DataStore
compatible, editor de enlaces, selector de contenido ES/EN y previsualización.

**Aceptación:** editar ambos idiomas desde web/móvil sin perder campos; fallback
para perfiles antiguos; HTTPS y plataformas válidas; cambios pendientes y conflictos.

## Fase 3 — Proyectos enriquecidos

**Entregas:** `urlPlay`, enlaces, tecnologías, galerías ordenables, texto alternativo
ES/EN, miniaturas y selección/subida múltiple. Actualizar DTO, Room, conversiones,
migración y formulario. Definir explícitamente publicación frente a privacidad.

**Aceptación:** proyecto web completo visible/editable en móvil; migración sin pérdida
de caché; metadatos siguen a las capturas al reordenar; subida fallida/reintentada;
límites y URLs; edición cruzada sin pérdidas ni falsos borradores privados.

## Fase 4 — Habilidades y CV

**Entregas:** CRUD de habilidades (categoría, icono permitido, color, orden y
publicación); PDF de CV por ES/EN, progreso, reemplazo y retirada usando
`siteSettings/public.resume` y `resumes/{locale}/...`.

**Aceptación:** respetar catálogo/reglas, PDF y tamaño permitidos; conflictos;
retirada coherente con web; fallos/cancelación de subida sin activar archivos incompletos.

## Fase 5 — Blog y documentos legales

**Entregas:** primero lista/estado/metadatos y borradores. Después editor enriquecido
compatible y publicación controlada. Respetar revisiones, historial, HTML seguro y
separación de recursos privados/públicos. Elegir editor nativo o integración web
autenticada antes de implementar el editor completo.

**Aceptación:** borradores privados; publicación/retirada consistente; conflictos;
sanitización; imágenes y revisiones; nunca sobrescribir directamente el documento
público saltándose el flujo de publicación. Pruebas de reglas en emulador.

## Fuera del alcance

SEO/rutas públicas, CAPTCHA en la bandeja móvil, laboratorio experimental de
editores y editor de certificados no existente en el administrador web actual.
Las fases no son estimaciones de calendario: cada una se valida antes de continuar.

## Resultado de verificación de la fase 1

**Implementación local completada; aceptación Firebase/dispositivos pendiente.**

- Pruebas focalizadas iniciales: 15/15 correctas.
- Suite final `:app:testDebugUnitTest`: **28/28 correctas**, sin errores ni omitidas;
  18 son nuevas (8 de conflictos, 2 de autorización, 3 de contratos/límites,
  2 del repositorio y 3 de restauración/validación del editor de proyectos).
- `:app:assembleDebug`: **BUILD SUCCESSFUL**; APK en
  `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check`: correcto.
- La restauración del editor de proyectos se prueba con una nueva instancia de
  `SavedStateHandle`; no sustituye una prueba de muerte de proceso en dispositivo.
- Los tests de parches comprueban los mapas generados y su combinación sin pérdida
  de campos; no son pruebas de transacciones del SDK contra un emulador.
- Se utilizó JDK 17 instalado, solo mediante `JAVA_HOME` del proceso de build.
  El JBR encontrado de Android Studio estaba incompleto (`lib/jvm.cfg` ausente).
  No se modificaron la configuración global ni los archivos de configuración del proyecto.
- No se ejecutó ni instaló la app en dispositivo; no hubo despliegues ni escrituras
  remotas. Continúan pendientes los escenarios de integración descritos arriba.

## Estado de la fase 2

Implementados localmente el perfil bilingüe ES/EN, enlaces sociales HTTPS,
vista previa y protección del borrador al salir. La revisión de traducciones
incluye paridad automática de las 156 claves traducibles.

Ver el [informe de implementación y pruebas](ui/phase-2-profile-web-mobile.md).
Se conserva el plan de **cinco fases**; la siguiente es proyectos enriquecidos
y galerías. No se han desplegado cambios ni modificado datos remotos.

## Estado de la fase 3

Implementación local de proyectos enriquecidos, galería bilingüe ordenable,
subida múltiple/reintento, publicación explícita y migración Room 5→6.
Ver [alcance, contratos, pruebas y límites de aceptación](ui/phase-3-enriched-projects.md).
La fase 4 fue autorizada posteriormente por el usuario.

## Estado de la fase 4

Implementación local de CRUD de habilidades, importación explícita del catálogo
web y CV ES/EN con subida, reemplazo y retirada transaccional por idioma.
Ver [contratos, pruebas y límites de aceptación](ui/phase-4-skills-resume.md).
Se conserva el plan de cinco fases; blog/documentos legales se abordan en la fase 5.

## Estado de la fase 5

Implementada la integración híbrida: borradores y metadatos privados de blog,
consulta legal/historial y apertura segura del administrador web para edición
enriquecida y publicación. No se replica el publicador HTML/imágenes en Android.
Ver [alcance y verificación](ui/phase-5-blog-legal.md) y
[preparación de despliegue 5.2.0](release/5.2.0-preflight.md).
Las cinco fases tienen implementación local; los controles de aceptación remota
y publicación siguen siendo obligatorios. No se ha publicado esta versión.

## Key Learnings:

1. El baseline debe viajar separado del DTO para no publicarlo por reflexión.
2. Una foto debe subirse a un objeto nuevo: una transacción Firestore no revierte Storage.
3. Los límites de perfil y proyecto son diferentes en la web; no conviene una única descripción máxima.
