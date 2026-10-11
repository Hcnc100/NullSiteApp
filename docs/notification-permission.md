# Permiso de notificaciones

`MainActivity` solicita `POST_NOTIFICATIONS` una vez en el primer inicio en
Android 13/API 33 o posterior. El manifiesto ya declara el permiso.

La decisión se persiste antes de abrir el diálogo del sistema, por lo que una
rotación, rechazo o cierre del diálogo no repite la solicitud. Rechazar no bloquea
el uso de la app. Si el permiso ya estaba concedido, tampoco se solicita.
Para cambiar la decisión después, usar Ajustes de Android → Aplicaciones →
NullSiteAdmin → Notificaciones. En Android 12 o anterior no se solicita este
permiso en tiempo de ejecución.

También se ofrece una vez a instalaciones existentes que nunca hayan recibido
esta solicitud desde la app y no tengan el permiso concedido. Borrar los datos
o reinstalar restablece la marca local; no depende de la cuenta de Firebase.

Referencia: https://developer.android.com/develop/ui/views/notifications/notification-permission

Pruebas: `NotificationPermissionPolicyTest` cubre primera apertura, versiones
anteriores, permiso concedido y aperturas posteriores tras rechazo/cierre.
