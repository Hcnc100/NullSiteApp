# Límites de formularios

Política compartida: `core/utils/FormLengthLimits.kt`. Los nueve campos editables
usan estos límites tanto para validación como para el contador, sin truncar texto.
Se cuentan unidades UTF-16 (`String.length`), igual que antes.

| Tipo | Límite |
| --- | ---: |
| Nombre de perfil/proyecto | 120 |
| Profesión de perfil | 180 |
| Descripción de perfil | 5000 |
| Descripción de proyecto | 10000 |
| URL de imagen/repositorio | 2048 |
| Correo de acceso | 254 |
| Contraseña de acceso | 4096 |

Auditoría de solo lectura, 2026-10-11, Firebase `nullpointer-716ae`:

| Colección | Documentos | Máximos observados |
| --- | ---: | --- |
| last-projects | 10 | name 15, description 135, urlRepo 53, urlImg 167, urlPlay 75, coverAlt 27 |
| infoProfile | 1 | name 20, profession 29, description 202, urlImg 153 |

El límite anterior de URL (100) rechazaba imágenes existentes (160–167).
Los nuevos límites dejan margen para nombres compuestos, biografías y URLs con
rutas codificadas/tokens. Son decisiones de UX de la app, no cuotas de Firebase
ni restricciones nuevas en el servidor. La contraseña no se consultó: el límite
de acceso admite credenciales largas y conserva espacios. Los campos `urlPlay`
y `coverAlt` no tienen editor en esta app; la imagen de perfil usa un selector,
no una entrada de URL. No se cambiaron datos ni reglas de Firebase.

## Compatibilidad web/móvil — fase 1

Desde esta fase los límites se separan por recurso (`PROFILE_*`, `PROJECT_*`)
y coinciden con los validadores locales actuales del administrador web. No se
truncan textos existentes. El repositorio de un proyecto es opcional, como en
la web; la URL de portada sigue siendo obligatoria. No se implementaron todavía
las traducciones ni los demás campos enriquecidos.
