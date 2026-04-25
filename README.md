## Clases principales

| Clase | Descripción |
|---|---|
| `Usuario` | Representa a los usuarios del sistema. Contiene datos como nombre, usuario, contraseña y rol. |
| `Cajero` | Hereda de `Usuario`. Representa al empleado encargado de crear órdenes y generar facturas. |
| `Producto` | Representa un producto individual del menú, como hamburguesas, bebidas o extras. |
| `Combo` | Hereda de `Producto`. Representa una promoción o conjunto de productos. |
| `Orden` | Representa el pedido de un cliente. Contiene productos, cajero, cliente, fecha y estado. |
| `Factura` | Se genera a partir de una orden. Calcula subtotal, impuesto y total. |

### Redes

El sistema utiliza sockets TCP para notificar cambios entre ventanas o instancias.

Ejemplos de eventos que pueden enviarse por red:

- `ORDEN_CREADA`
- `ORDEN_ACTUALIZADA`
- `FACTURA_GENERADA`
- `PRODUCTO_ACTUALIZADO`

Cuando se recibe una notificación, la ventana correspondiente vuelve a consultar la base de datos y actualiza su contenido.

### Interfaz gráfica

| Ventana | Función |
|---|---|
| `LoginFrame` | Permite iniciar sesión con usuario y contraseña. |
| Menú principal | Muestra opciones según el rol del usuario. |
| Crear orden | Permite seleccionar productos, agregar al carrito y finalizar una orden. |
| Monitor de cocina | Muestra órdenes pendientes o en preparación y permite cambiar su estado. |
| Factura | Muestra subtotal, impuesto y total de una orden. |
| Administración | Permite gestionar usuarios, productos o combos, según la versión implementada. |

## Roles del sistema

| Rol | Funciones principales |
|---|---|
| Cajero | Crear órdenes, ver órdenes y generar facturas. |
| Cocina | Ver órdenes pendientes y cambiar su estado. |
| Administrador | Registrar o administrar usuarios, productos y combos. |

## Usuarios de prueba

| Rol | Usuario | Contraseña |
|---|---|---|
| Cajero | `cajero` | `1234` |
| Cocina | `cocina` | `1234` |
| Administrador | `admin` | `1234` |


## Flujo básico de uso

1. Abrir el programa.
2. Iniciar sesión con un usuario de prueba.
3. Si se ingresa como cajero, crear una nueva orden.
4. Seleccionar productos o combos y finalizar la orden.
5. La orden se guarda en la base de datos.
6. El sistema envía una notificación por red.
7. La ventana de cocina actualiza la lista de órdenes.
8. Cocina cambia el estado de la orden.
9. El cambio se guarda en la base de datos y se notifica al sistema.
10. El cajero puede generar la factura de la orden.

## Demostración de base de datos

El proyecto demuestra el uso de base de datos porque la información no queda almacenada únicamente en variables temporales. Los datos se guardan en SQLite, por ejemplo:

- Usuarios registrados.
- Productos y combos.
- Órdenes creadas.
- Productos asociados a cada orden.
- Estados de las órdenes.
- Facturas generadas.

Esto permite cerrar y volver a abrir la aplicación sin perder la información registrada.

## Demostración de redes

El proyecto demuestra el uso de redes mediante sockets TCP. Cuando ocurre un cambio importante, una ventana o instancia envía una notificación a las demás.

Ejemplo:

```text
Cajero crea una orden
        ↓
La orden se guarda en SQLite
        ↓
Se envía una notificación por socket
        ↓
El monitor de cocina recibe el aviso
        ↓
Cocina consulta la base de datos y actualiza la pantalla
```

De esta forma, se puede demostrar comunicación entre diferentes partes del sistema.

## Flujo de ejecución interno

```text
Main
 ↓
Inicializa base de datos
 ↓
Inicia servidor y cliente de red
 ↓
Abre LoginFrame
 ↓
Valida usuario en la base de datos
 ↓
Abre menú según rol
 ↓
El usuario realiza acciones
 ↓
Los cambios se guardan en SQLite
 ↓
Se notifican cambios por red
 ↓
Las ventanas actualizan su información
```

## Posibles mejoras futuras

- Agregar reportes de ventas por fecha.
- Exportar facturas en PDF.
- Mejorar el módulo de administración de productos.
- Agregar control de inventario.
- Permitir conexión a una base de datos remota.
- Agregar autenticación con contraseñas cifradas.
- Mejorar el diseño visual de las ventanas.


