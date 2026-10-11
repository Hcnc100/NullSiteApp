# Detalle del correo

- Remitente, asunto, fecha y mensaje con jerarquía tipográfica Material 3.
- Nombre, dirección y contenido seleccionables para copiar.
- Fecha/hora con el formato del dispositivo, sin mezclar hora de 24 h con AM/PM.
- Ilustración compacta, contenido desplazable y espacio final para leer debajo
  del botón flotante «Responder».
- Eliminar abre una confirmación cancelable, con aviso de acción irreversible.
- Durante el borrado se muestra progreso y se bloquean nuevas eliminaciones,
  navegación hacia atrás y respuesta. El ViewModel también impide solicitudes
  duplicadas, independientemente de la UI.
- La pantalla se cierra solo tras éxito del repositorio; ante error permanece
  abierta, muestra el snackbar existente y recupera los controles.
- Textos en español e inglés, sin colores personalizados fuera del tema.

El fallo anterior era una llamada inmediata a `actionBack()` después de iniciar
el borrado. Podía cancelar la coroutine del ViewModel al salir de la pantalla,
o causar una segunda navegación cuando se ejecutaba el callback de éxito.

Pruebas: `EmailDetailsDeleteTest`, con repositorios/callbacks locales. No se
eliminan correos reales de Firebase. Incluye confirmación/cancelación, controles
durante carga, navegación, orientación, solicitudes duplicadas y español oscuro.
