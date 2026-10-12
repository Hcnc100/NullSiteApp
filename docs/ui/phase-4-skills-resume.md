# Fase 4 — Habilidades y CV

## Objetivo e implementación local

Integrar la administración web de habilidades y CV en Android, conservando los
contratos existentes. Nueva entrada **Habilidades y CV** en el menú, traducida
ES/EN; Material 3 con paletas existentes y formulario desplazable.

- Habilidades: listar, crear, editar y eliminar con confirmación; nombre,
  descripción, traducciones ES/EN, categoría `language`/`tech`, catálogo de ocho
  iconos, color `#RRGGBB`, orden 0–9999 y publicación explícita. Nuevas habilidades
  empiezan sin publicar. El icono se selecciona por su clave, no con SVG arbitrario.
- Activación/importación **explícita** de los nueve valores iniciales web y flag
  `siteSettings/public.skillsManaged`. Conserva documentos existentes. Si el flag
  ya está activo, no vuelve a importar ni resucita habilidades iniciales eliminadas.
- CV por idioma: selector PDF, publicación explícita, progreso, cancelación de
  subida, reemplazo y retirada confirmada. Se bloquea el cambio de idioma mientras
  hay un archivo seleccionado, evitando publicarlo accidentalmente en otro idioma.
- PDF: MIME `application/pdf`, extensión `.pdf`, firma `%PDF-`, tamaño >5 bytes y
  <=10 MiB. Lectura acotada antes de subir. Son las comprobaciones web, no una
  auditoría de todo el contenido del PDF ni un antivirus.

## Guardado y compatibilidad

`CareerRepository` separa las operaciones de la UI y `FirebaseCareerRepository`
implementa Firestore/Storage. Requiere claim booleano `admin` antes de operar;
las reglas Firebase siguen siendo la autoridad. Listas y settings se leen del
servidor; esta sección no promete edición offline ni añade una migración Room.

Habilidades usan transacciones y fingerprint de los mismos campos que la web,
incluidas todas las traducciones. Detectan modificación/eliminación remota y
colisión al crear. Updates con paths ES/EN conservan otros campos/locales; las
creaciones usan mapas anidados. El borrador y su baseline original sobreviven a
restauración con SavedStateHandle sin adoptar silenciosamente una base remota nueva.

CV usa `resumes/{es|en}/{UUID}.pdf`, metadatos de descarga `CV-{locale}.pdf` y el
puntero `siteSettings/public.resume.{locale}` con `storagePath`, `fileName`,
`sizeBytes`, `enabled`, `updatedAt`. No se persiste un download URL que evada la
retirada basada en reglas. La transacción compara solo la entrada completa del
idioma editado, incluyendo timestamp, y conserva el otro idioma y otros settings.
El archivo anterior permanece activo hasta terminar subida y commit del puntero.

Retirar cambia solo `enabled: false`, como la web; conserva el objeto y su ruta.
La interfaz advierte que la web puede ofrecer el CV activo del otro idioma como
fallback. Retirar tampoco revoca copias ya descargadas. La web actual descarga
con `getBlob`, no publicando un token permanente. El reemplazo limpia el archivo anterior únicamente después del commit,
si su ruta es válida para ese idioma; un fallo de limpieza no revierte la publicación.

## Fallos y cancelación

Cancelar detiene la tarea de subida y conserva la selección para reintentar o
descartar. No hay botón cancelar durante el commit, cuyo resultado puede ser
ambiguo. Una publicación fallida conserva la selección y bloquea reintentos ciegos
hasta descartar/recargar; dicho bloqueo también se restaura.

**No se borra el PDF nuevo tras un error de commit:** un acuse perdido puede ocultar
una publicación correcta. Se conservan objetos huérfanos de fallos/cancelaciones;
su limpieza requiere comprobar referencias fuera del cliente. Las reglas web solo
permiten lectura pública del puntero activo habilitado (el admin puede leer otros).
No se modificaron reglas, claims, configuración global, hooks ni dependencias.

## Verificación

- **66/66 pruebas JVM** (12 nuevas): catálogo, límites, MIME/firma/tamaño PDF,
  mapas create/update, fingerprints, timestamps y conflictos. Paridad automática
  ES/EN de las 227 claves traducibles (43 nuevas).
- **14 casos instrumentados en vertical**: 10/10 de estado y 4/4 de UI en las
  últimas ejecuciones separadas. Cubren creación, edición,
  eliminación, importación, restauración, doble envío, PDF inválido/cancelado,
  reemplazo, commit ambiguo y retirada, incluida publicación/retirada del CV EN
  conservando ES, y cuatro variantes del editor.
- **18/18 en horizontal**: cuatro variantes del editor y 14 regresiones de
  validación y tema. Revalidación final de las cuatro variantes tras ajustes UI.
- Claro/oscuro × ES/EN, con fuente **200 % real del emulador**, incluyendo diálogos.
  API33/Android 13, AVD `Codex_UI_API_33`. Se restauran fuente y rotación originales.
- `assembleDebug` correcto; APK `app/build/outputs/apk/debug/app-debug.apk`.
  XML local en `build/phase4-test-evidence/`; capturas seleccionadas en
  [phase4-career-qa](phase4-career-qa/).
  `git diff --check` sin errores de espacios.

Se corrigieron problemas detectados al revisar capturas: Surface con colores
Material correctos fuera del Scaffold, textos de diálogo resueltos en el contexto
ES/EN del formulario, y cuerpo desplazable para el aviso largo al 200 % horizontal.
Los selectores usan FlowRow para anchos pequeños. Cambiar de sección no permite
eludir el bloqueo tras un commit ambiguo ni abandona silenciosamente el borrador.
Las primeras ejecuciones fallidas no cuentan como aceptación.
Se sustituyó la captura automática de la ventana de diálogo, que se atascó tras
gestos de scroll, por scroll semántico en el test y evidencia visual directa del
emulador. Las capturas de formulario/CV siguen siendo automáticas; el diálogo
horizontal incluye evidencia del inicio y del final desplazado de su aviso.

Los tests instrumentados usan repositorio falso; validan UI/estado y el contrato,
**no transacciones del SDK contra Firebase ni subidas PDF reales**. No se escriben
datos de producción.

## Pendientes de aceptación externa

- Emuladores Firebase/cuentas de prueba: reglas reales, admin revocado, conflictos
  entre web/móvil y lectura/denegación del PDF al publicar, reemplazar y retirar.
- Proveedores de documentos y persistencia del permiso tras muerte real de proceso;
  si el proveedor no concede permiso persistente, puede requerir elegir de nuevo.
- Red interrumpida y acuse de commit perdido con SDK real; limpieza referenciada
  de huérfanos; más dispositivos/versiones Android y prueba TalkBack manual.
- Guardar/restaurar grandes traducciones con muerte de proceso; las pruebas con
  SavedStateHandle no equivalen a esa prueba del sistema operativo.

## Continuidad y archivos

No se pidió commit, push ni despliegue. Se preservan cambios locales de fases 1–3
y archivos adjuntos. Fase 5 (blog/legal) no se inicia automáticamente.

- `models/career/CareerContent.kt`: catálogo, validación y fingerprints.
- `data/career/FirebaseCareerRepository.kt`: transacciones y subidas PDF.
- `ui/screens/career/CareerViewModel.kt`: borrador, progreso y bloqueo de reintento.
- `ui/screens/career/CareerScreen.kt`: CRUD y CV bilingües.
- `CareerContractTest.kt`, `CareerPhaseFourDataTest.kt`, `CareerPhaseFourUiTest.kt`:
  contratos, restauración, fallos y aceptación local.

## Key Learnings:

1. `skillsManaged` activa el contenido administrado: importar debe ser explícito
   y una importación repetida no debe recrear habilidades eliminadas.
2. El CV se publica con un puntero Storage por idioma, no con una URL descargable.
3. Retirar un idioma no impide que la web ofrezca el otro CV como fallback.
4. Borrar una subida tras un error de commit puede romper una publicación válida
   si solo se perdió su confirmación; conviene bloquear reintentos y recargar.
