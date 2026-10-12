# Qué traer de NullSiteWeb a NullSiteApp

Fecha: 11 de octubre de 2026. Comparación estática del código local; no prueba de producción ni ejecución de ambas interfaces. No se modificó código funcional.

## Repositorios encontrados

- **Web actual:** `D:/Documents/Projects/angular/NullSiteWeb`, remoto `Hcnc100/NullSiteWeb`, último commit observado `1d2a0ed` del 11/10/2026. Angular 20; incluye administración y mejoras recientes de contacto.
- **Android actual:** `C:/Users/ricar/AndroidStudioProjects/NullSiteApp`.
- `D:/Documents/Projects/NullSiteWeb` solo contiene `storage.rules`: no es la copia web de trabajo.
- `C:/Users/ricar/Documents/Projects/NullSiteApp` es otra copia Android; no fue la base de esta comparación.

## Conclusión

La oportunidad principal no es copiar la apariencia web: es convertir Android en un administrador móvil de las capacidades que ahora tiene la web. Empezaría por **compatibilidad de datos, redes sociales, traducciones y proyectos con galerías**. Después añadiría habilidades y CV. Blog y legales merecen una fase propia por sus reglas de publicación y seguridad.

La app no necesita reescribirse: ya separa datos, repositorios, modelos y presentación, y dispone de persistencia local. Los modelos nuevos deberán atravesar esas capas, no quedarse solo en formularios.

## Estado de Android que conviene conservar

El código contiene login Firebase, bloqueo biométrico, edición de perfil y proyectos, bandeja y detalle de mensajes, notificaciones FCM, previsualización web y ajustes. Existen Room/DAO para proyectos y mensajes y DataStore para otros estados. Tiene validaciones y límites centralizados; las mejoras de UI están documentadas en `docs/ui/`.

No encontré modelos ni pantallas Android para `socialLinks`, `translations`, `galleryItems`, `urlPlay`, `technologyIds`, `sortOrder`, habilidades, CV, borradores de blog o borradores legales.

## Qué traer y en qué orden

| Prioridad | Capacidad comprobada en la web | Qué ganaría Android | Adaptación necesaria | Esfuerzo relativo |
|---|---|---|---|---|
| 0 | Detección de conflictos al guardar perfil y contenido | Evitar sobrescribir cambios realizados desde otro dispositivo | Transacciones con comparación de la versión/base editada, mensaje de conflicto y recarga explícita | Medio |
| 1 | Redes sociales del perfil | Agregar, editar y quitar enlaces desde el teléfono | Modelo `socialLinks`, validación HTTPS, editor de lista y persistencia local | Bajo–medio |
| 1 | Contenido español/inglés | Editar lo que realmente ve el visitante en cada idioma | `translations` en perfil/proyectos; selector ES/EN; vista previa por idioma; no confundir traducción de contenido con idioma de la interfaz | Medio |
| 1 | Proyectos enriquecidos | Administrar Play Store, enlaces adicionales y tecnologías | Extender DTO, modelo, entidad Room, conversiones y editor con `urlPlay`, `links`, `technologyIds` | Medio |
| 1 | Galerías y metadatos de imágenes | Añadir capturas, reordenarlas y describirlas desde Android | `gallery`, `galleryItems`, `coverAlt`, miniaturas y traducciones del texto alternativo; selección/subida múltiple | Medio–alto |
| 2 | Administración de habilidades | Mantener tecnologías, categorías, iconos, colores, orden y publicación | Nuevo módulo sobre `skills`; usar el catálogo de iconos permitido y el contrato de publicación | Medio |
| 2 | CV por idioma | Subir, reemplazar o retirar los PDF ES/EN sin abrir una computadora | Selector de documentos, validación PDF, progreso y cancelación; `siteSettings/public.resume` y `resumes/{locale}/...` | Medio |
| 3 | Blog con borradores y publicación separada | Crear borradores desde el móvil; publicar después con control | Repositorios y pantallas nuevos; respetar revisiones, HTML permitido, recursos privados/públicos y publicación transaccional | Alto |
| 3 | Administración de textos legales | Corregir políticas y términos desde Android | Respetar borrador, revisión, base publicada e historial; no sobrescribir directamente `terms` | Alto |

El esfuerzo es comparativo, no una estimación en días ni un compromiso de implementación.

### Recomendación para blog y legales

Primera entrega móvil: listar, consultar estado y editar metadatos o borradores simples. Para el editor enriquecido completo, evaluar después una implementación nativa o una integración web autenticada. Tiptap es una biblioteca web: no se puede trasladar directamente a Compose ni basta copiar HTML. Las tablas, imágenes, colores y listas requieren preservar el formato seguro que consume la web.

## Mejoras de experiencia que sí vale la pena adaptar

- **Vista previa contextual:** perfil/proyecto según ES/EN, con enlaces, tecnologías y capturas, antes de guardar. Complementa la previsualización general que ya existe.
- **Aviso al abandonar cambios:** revisar las salidas de formularios y asegurar confirmación cuando haya cambios pendientes, tomando como referencia los guards web.
- **Estados de operación claros:** guardando, subiendo, éxito y error accionable; evitar dobles envíos y permitir reintentar cuando sea seguro. Android ya tiene indicadores de progreso: ampliar su cobertura, no duplicarlos.
- **Imágenes accesibles:** descripción por captura y miniaturas, con previsualización y conservación del metadato al reordenar.

No trasladaría menús de escritorio, hover ni animaciones web literalmente. Mantendría controles táctiles y patrones Material de Android.

## Riesgos de compatibilidad que deben resolverse primero

### 1. Campos nuevos conservados, pero todavía invisibles

`UserApiServices` y `ProjectApiServices` usan `update` sobre documentos existentes. Los DTO actuales solo envían campos base, por lo que esas operaciones no sustituyen por sí mismas todo el documento ni eliminan automáticamente los campos web nuevos. Sin embargo, Android no puede mostrarlos o editarlos y puede sobrescribir campos base cambiados concurrentemente. La web sí compara el estado inicial dentro de transacciones.

### 2. Traducciones distintas del texto base

La web guarda texto base y `translations.es/en` por separado. Android solo conoce el texto base. Debe verificarse la resolución de traducciones y advertir al usuario qué idioma está editando: cambiar la descripción base no equivale a actualizar todas sus versiones traducidas.

### 3. Visibilidad de proyectos no equivale a privacidad

Android envía `published` desde su control `isVisible`. El formulario web indica explícitamente que los proyectos guardados son públicos y que no habilita borradores privados de proyectos. Su parche de proyectos no aplica el mismo control de publicación que el de habilidades. Hay que revisar lector público y reglas antes de prometer que ocultar un proyecto protege su contenido.

### 4. Límites diferentes

Android: nombre 100, descripción 2.000 y URL 2.048 caracteres (`FormLengthLimits.kt`). El validador web de contenido admite nombre 120 y descripción 10.000. Un proyecto válido creado desde web puede resultar difícil de reeditar en móvil. Alinear por tipo de recurso con reglas y validadores reales; no ampliar todos los campos indiscriminadamente.

### 5. Autenticación frente a autorización

La web verifica el claim booleano `admin === true`. El login Android observado valida correo/contraseña, pero no contiene una comprobación equivalente del claim en `AuthApiServices`. Añadir un estado de acceso administrativo y errores claros; las reglas Firebase deben seguir siendo la autoridad. No se verificaron los permisos desplegados.

### 6. Evolución de persistencia local

Proyectos se almacenan en Room y el perfil tiene modelos locales propios. Extender solo Firestore o las pantallas dejaría datos incompletos al leer de caché. Planificar entidades, convertidores, migración Room y pruebas de lectura de documentos antiguos sin campos opcionales.

## Qué no confundir con mejoras transferibles

- **CAPTCHA y antispam del contacto:** protegen el formulario público que crea mensajes; Android administra la bandeja. No tiene sentido añadir CAPTCHA a la lectura de mensajes. Conviene verificar que el nuevo flujo de contacto conserve el contrato que usa Android y la entrega FCM.
- **SEO, rutas canónicas y metadatos públicos:** pertenecen al sitio. Android puede editar contenido asociado si se necesita, pero no debe implementar el SEO de la web.
- **Certificados:** la web tiene lector público y colección histórica, pero el `ContentKind` actual del administrador solo contempla `projects | skills`. No considero un editor de certificados como una función web ya lista para portar.
- **Laboratorio de editores:** no lo trataría como funcionalidad final móvil ni expondría herramientas experimentales por defecto.

## Plan propuesto

1. **Base compatible:** confirmar contratos de lectura/publicación, permisos y límites; añadir detección de conflictos y pruebas de conservación de campos desconocidos.
2. **Perfil completo:** redes sociales y ES/EN, manteniendo compatibilidad con documentos antiguos.
3. **Proyectos completos:** enlaces, tecnologías y galerías accesibles, incluyendo migración local y pruebas de edición cruzada web/móvil.
4. **Habilidades y CV:** módulos nuevos, más acotados que un editor enriquecido.
5. **Blog y legales:** primero consulta/borradores; publicación y editor completo después de verificar seguridad y revisiones.

## Fuentes de código principales

Web, relativas a `D:/Documents/Projects/angular/NullSiteWeb`:

- `src/app/models/Project.ts`, `project-media.ts`, `InfoProfile.ts`, `managed-content.ts`: nuevos contratos y validaciones.
- `src/app/admin/profile/admin-profile.service.ts`: guardado con comparación del perfil base.
- `src/app/admin/content/admin-content.service.ts`: proyectos/habilidades, conflictos y subida de recursos.
- `src/app/admin/content/admin-content.component.html`: galerías, traducciones y aviso de proyectos públicos.
- `src/app/admin/resume/admin-resume.service.ts`: PDF por idioma y retirada.
- `src/app/admin/blog/admin-blog.service.ts`: publicación separada del borrador.
- `src/app/admin/legal/admin-legal.service.ts`: revisiones y conflictos legales.
- `src/app/admin/auth/admin-auth.service.ts`: claim administrativo.

Android, bajo `app/src/main/java/com/nullpointer/nullsiteadmin`:

- `models/project/{data,dto,entity}` y `models/personalInfo/{data,dto}`: alcance actual de campos.
- `data/project/remote/ProjectApiServices.kt`, `data/user/remote/UserApiServices.kt`: escrituras parciales existentes.
- `data/auth/remote/AuthApiServices.kt`: login y registro del dispositivo.
- `core/utils/FormLengthLimits.kt`: límites actuales.

## Límites de esta revisión

Se revisó código local y metadatos Git; no se hizo `fetch`, despliegue, migración, build ni prueba con cuentas reales. Documentos históricos web describen fases que posteriormente cambiaron, por lo que las capacidades recomendadas se contrastaron con implementación actual. Serena no inició el servidor Kotlin; se usó extracción focalizada de archivos. La compatibilidad efectiva, privacidad y notificaciones requieren pruebas de integración antes de implementar.

## Key Learnings:

1. La brecha principal es de modelos y administración: Android no representa los campos enriquecidos que ya edita la web.
2. Las escrituras parciales conservan campos desconocidos, pero no resuelven conflictos ni sincronizan traducciones.
3. Un flag de visibilidad no demuestra privacidad: hay que comprobar lector y reglas Firebase.
