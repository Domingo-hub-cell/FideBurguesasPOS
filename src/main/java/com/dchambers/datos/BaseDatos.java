package com.dchambers.datos;

import com.dchambers.*;
import com.dchambers.red.RedCliente;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BaseDatos {
    private static final String DB_URL = "jdbc:sqlite:data/fideburguesas.db";
    private static final MonitorCocina monitorCocina = new MonitorCocina();

    private static Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void inicializar() throws Exception {
        Files.createDirectories(Path.of("data"));
        try (Connection cn = conectar(); Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("CREATE TABLE IF NOT EXISTS usuarios (id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, rol TEXT NOT NULL, sucursal TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS productos (id_producto INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, precio REAL NOT NULL, tipo TEXT NOT NULL, disponible INTEGER NOT NULL DEFAULT 1)");
            st.execute("CREATE TABLE IF NOT EXISTS ordenes (id_orden INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT NOT NULL, id_cajero INTEGER NOT NULL, cliente TEXT NOT NULL, estado TEXT NOT NULL, FOREIGN KEY(id_cajero) REFERENCES usuarios(id_usuario))");
            st.execute("CREATE TABLE IF NOT EXISTS orden_detalle (id_detalle INTEGER PRIMARY KEY AUTOINCREMENT, id_orden INTEGER NOT NULL, id_producto INTEGER NOT NULL, nombre_producto TEXT NOT NULL, precio REAL NOT NULL, tipo TEXT NOT NULL, FOREIGN KEY(id_orden) REFERENCES ordenes(id_orden), FOREIGN KEY(id_producto) REFERENCES productos(id_producto))");
            st.execute("CREATE TABLE IF NOT EXISTS facturas (id_factura INTEGER PRIMARY KEY AUTOINCREMENT, id_orden INTEGER NOT NULL, subtotal REAL NOT NULL, impuesto REAL NOT NULL, total REAL NOT NULL, fecha TEXT NOT NULL, FOREIGN KEY(id_orden) REFERENCES ordenes(id_orden))");
        }
        insertarDatosIniciales();
    }

    private static void insertarDatosIniciales() throws SQLException {
        if (contar("usuarios") == 0) {
            registrarUsuarioSinNotificar("Ana Cajero", "cajero", "1234", "CAJERO", "Sucursal Central");
            registrarUsuarioSinNotificar("Mario Cocina", "cocina", "1234", "COCINA", null);
            registrarUsuarioSinNotificar("Admin General", "admin", "1234", "ADMIN", null);
        }
        if (contar("productos") == 0) {
            registrarProductoSinNotificar("Hamburguesa clásica", 2800, "Hamburguesa", true);
            registrarProductoSinNotificar("Hamburguesa doble", 3800, "Hamburguesa", true);
            registrarProductoSinNotificar("Extra queso", 500, "Extra", true);
            registrarProductoSinNotificar("Papas fritas", 1200, "Extra", true);
            registrarProductoSinNotificar("Refresco", 900, "Bebida", true);
            registrarProductoSinNotificar("Malteada", 1600, "Bebida", true);
            registrarProductoSinNotificar("Combo clásico", 5000, "Combo", true);
            registrarProductoSinNotificar("Combo doble", 6500, "Combo", true);
            registrarProductoSinNotificar("Combo triple", 7500, "Combo", false);

        }
    }

    private static int contar(String tabla) throws SQLException {
        try (Connection cn = conectar(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static Usuario autenticar(String username, String password) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username=? AND password=?";
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUsuario(rs) : null;
            }
        }
    }

    private static Usuario mapUsuario(ResultSet rs) throws SQLException {
        String rol = rs.getString("rol");
        if ("CAJERO".equalsIgnoreCase(rol)) {
            return new Cajero(rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("username"), rs.getString("password"), rol, rs.getString("sucursal"));
        }
        return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("username"), rs.getString("password"), rol);
    }

    public static Cajero comoCajero(Usuario usuario) {
        if (usuario instanceof Cajero c) return c;
        return new Cajero(usuario.getIdUsuario(), usuario.getNombre(), usuario.getUsername(), usuario.getPassword(), usuario.getRol(), "Sucursal Central");
    }

    public static void registrarUsuario(String nombre, String username, String password, String rol) throws SQLException {
        registrarUsuarioSinNotificar(nombre, username, password, rol, "CAJERO".equalsIgnoreCase(rol) ? "Sucursal Central" : null);
        notificarCambio("USUARIOS");
    }

    private static void registrarUsuarioSinNotificar(String nombre, String username, String password, String rol, String sucursal) throws SQLException {
        String sql = "INSERT INTO usuarios(nombre, username, password, rol, sucursal) VALUES(?,?,?,?,?)";
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, rol.toUpperCase());
            ps.setString(5, sucursal);
            ps.executeUpdate();
        }
    }

    public static void registrarProducto(String nombre, double precio, String tipo, boolean disponible) throws SQLException {
        registrarProductoSinNotificar(nombre, precio, tipo, disponible);
        notificarCambio("PRODUCTOS");
    }

    public static void registrarCombo(String nombre, double precio) throws SQLException {
        registrarProducto(nombre, precio, "Combo", true);
    }

    private static void registrarProductoSinNotificar(String nombre, double precio, String tipo, boolean disponible) throws SQLException {
        String sql = "INSERT INTO productos(nombre, precio, tipo, disponible) VALUES(?,?,?,?)";
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.setString(3, tipo);
            ps.setInt(4, disponible ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public static List<Producto> getProductos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement("SELECT * FROM productos WHERE disponible=1 ORDER BY tipo, nombre"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) productos.add(mapProducto(rs));
        }
        return productos;
    }

    private static Producto mapProducto(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_producto");
        String nombre = rs.getString("nombre");
        double precio = rs.getDouble("precio");
        String tipo = rs.getString("tipo");
        boolean disp = rs.getInt("disponible") == 1;
        return "Combo".equalsIgnoreCase(tipo) ? new Combo(id, nombre, precio, tipo, disp) : new Producto(id, nombre, precio, tipo, disp);
    }

    public static Orden crearOrden(Cajero cajero, String cliente, List<Producto> productos) throws Exception {
        if (productos == null || productos.isEmpty()) throw new Exception("La orden no contiene productos.");
        try (Connection cn = conectar()) {
            cn.setAutoCommit(false);
            try {
                int idOrden;
                String fecha = LocalDateTime.now().toString();
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO ordenes(fecha, id_cajero, cliente, estado) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, fecha);
                    ps.setInt(2, cajero.getIdUsuario());
                    ps.setString(3, cliente);
                    ps.setString(4, "PENDIENTE");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idOrden = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO orden_detalle(id_orden, id_producto, nombre_producto, precio, tipo) VALUES(?,?,?,?,?)")) {
                    for (Producto p : productos) {
                        ps.setInt(1, idOrden);
                        ps.setInt(2, p.getIdProducto());
                        ps.setString(3, p.getNombre());
                        ps.setDouble(4, p.calcularPrecio());
                        ps.setString(5, p.getTipo());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                cn.commit();
                notificarCambio("ORDENES");
                return getOrdenPorId(idOrden);
            } catch (Exception ex) {
                cn.rollback();
                throw ex;
            }
        }
    }

    public static List<Orden> getOrdenes() throws SQLException {
        return consultarOrdenes("SELECT o.*, u.nombre, u.username, u.password, u.rol, u.sucursal FROM ordenes o JOIN usuarios u ON o.id_cajero=u.id_usuario ORDER BY o.id_orden DESC");
    }

    public static List<Orden> getOrdenesPendientes() throws SQLException {
        return consultarOrdenes("SELECT o.*, u.nombre, u.username, u.password, u.rol, u.sucursal FROM ordenes o JOIN usuarios u ON o.id_cajero=u.id_usuario WHERE o.estado <> 'ENTREGADO' ORDER BY o.id_orden DESC");
    }

    private static List<Orden> consultarOrdenes(String sql) throws SQLException {
        List<Orden> ordenes = new ArrayList<>();
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ordenes.add(mapOrden(rs, cn));
        }
        return ordenes;
    }

    public static Orden getOrdenPorId(int idOrden) throws SQLException {
        String sql = "SELECT o.*, u.nombre, u.username, u.password, u.rol, u.sucursal FROM ordenes o JOIN usuarios u ON o.id_cajero=u.id_usuario WHERE o.id_orden=?";
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapOrden(rs, cn) : null;
            }
        }
    }

    private static Orden mapOrden(ResultSet rs, Connection cn) throws SQLException {
        Cajero cajero = new Cajero(rs.getInt("id_cajero"), rs.getString("nombre"), rs.getString("username"), rs.getString("password"), rs.getString("rol"), rs.getString("sucursal"));
        ArrayList<Producto> productos = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement("SELECT * FROM orden_detalle WHERE id_orden=? ORDER BY id_detalle")) {
            ps.setInt(1, rs.getInt("id_orden"));
            try (ResultSet d = ps.executeQuery()) {
                while (d.next()) {
                    String tipo = d.getString("tipo");
                    Producto p = "Combo".equalsIgnoreCase(tipo) ? new Combo(d.getInt("id_producto"), d.getString("nombre_producto"), d.getDouble("precio") / 0.90, tipo, true) : new Producto(d.getInt("id_producto"), d.getString("nombre_producto"), d.getDouble("precio"), tipo, true);
                    productos.add(p);
                }
            }
        }
        return new Orden(rs.getInt("id_orden"), LocalDateTime.parse(rs.getString("fecha")), cajero, rs.getString("cliente"), productos, rs.getString("estado"));
    }

    public static void actualizarEstadoOrden(int idOrden, String estado) throws SQLException {
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement("UPDATE ordenes SET estado=? WHERE id_orden=?")) {
            ps.setString(1, estado);
            ps.setInt(2, idOrden);
            ps.executeUpdate();
        }
        notificarCambio("ORDENES");
    }

    public static Factura generarFactura(Orden orden) throws SQLException {
        Factura factura = new Factura(0, orden);
        Integer existente = facturaExistente(orden.getIdOrden());
        if (existente != null) return new Factura(existente, orden);
        String sql = "INSERT INTO facturas(id_orden, subtotal, impuesto, total, fecha) VALUES(?,?,?,?,?)";
        int idFactura;
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orden.getIdOrden());
            ps.setDouble(2, factura.getSubtotal());
            ps.setDouble(3, factura.getImpuesto());
            ps.setDouble(4, factura.getTotal());
            ps.setString(5, LocalDateTime.now().toString());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                idFactura = keys.getInt(1);
            }
        }
        notificarCambio("FACTURAS");
        return new Factura(idFactura, orden);
    }

    private static Integer facturaExistente(int idOrden) throws SQLException {
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement("SELECT id_factura FROM facturas WHERE id_orden=?")) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public static MonitorCocina getMonitorCocina() {
        return monitorCocina;
    }

    private static void notificarCambio(String area) {
        RedCliente.getInstancia().enviar("REFRESH:" + area);
    }
}
