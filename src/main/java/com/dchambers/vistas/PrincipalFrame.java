package com.dchambers.vistas;

import com.dchambers.*;
import com.dchambers.datos.BaseDatos;
import com.dchambers.red.RedCliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrincipalFrame extends JFrame {
    private final Usuario usuario;
    private final JPanel contenido;
    private final JLabel tituloContenido;
    private final Color azulMenu = new Color(10, 22, 78);
    private final Color azulPanel = new Color(19, 30, 95);
    private Runnable pantallaActual;

    public PrincipalFrame(Usuario usuario) {
        this.usuario = usuario;
        setTitle("FideBurguesas POS - " + usuario.getRol());
        setSize(1120, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        contenido = new JPanel(new BorderLayout());
        contenido.setBackground(new Color(1, 6, 38));
        tituloContenido = new JLabel("Bienvenido", SwingConstants.CENTER);
        tituloContenido.setForeground(Color.WHITE);
        tituloContenido.setFont(new Font("SansSerif", Font.BOLD, 34));
        tituloContenido.setBorder(new EmptyBorder(25, 10, 20, 10));
        contenido.add(tituloContenido, BorderLayout.NORTH);

        add(crearMenu(), BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
        RedCliente.getInstancia().agregarObservador(this::refrescarPantallaActual);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { RedCliente.getInstancia().removerObservador(PrincipalFrame.this::refrescarPantallaActual); }
        });
        mostrarInicio();
    }

    private JPanel crearMenu() {
        JPanel menu = new JPanel();
        menu.setBackground(azulMenu);
        menu.setPreferredSize(new Dimension(220, 720));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(25, 18, 25, 18));

        JLabel avatar = new JLabel("●  " + usuario.getNombre());
        avatar.setForeground(Color.WHITE); avatar.setFont(new Font("SansSerif", Font.BOLD, 15));
        JLabel rol = new JLabel("Rol: " + usuario.getRol());
        rol.setForeground(new Color(195, 200, 225)); rol.setFont(new Font("SansSerif", Font.PLAIN, 12));
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT); rol.setAlignmentX(Component.LEFT_ALIGNMENT);

        menu.add(avatar); menu.add(Box.createRigidArea(new Dimension(0, 5))); menu.add(rol);
        menu.add(Box.createRigidArea(new Dimension(0, 45)));

        if (usuario.getRol().equals("CAJERO") || usuario.getRol().equals("ADMIN")) {
            menu.add(botonMenu("Crear orden", this::mostrarCrearOrden)); menu.add(Box.createRigidArea(new Dimension(0, 14)));
            menu.add(botonMenu("Ver órdenes", this::mostrarOrdenes)); menu.add(Box.createRigidArea(new Dimension(0, 14)));
            menu.add(botonMenu("Generar factura", this::mostrarFacturas)); menu.add(Box.createRigidArea(new Dimension(0, 14)));
        }
        if (usuario.getRol().equals("COCINA") || usuario.getRol().equals("ADMIN")) {
            menu.add(botonMenu("Monitor cocina", this::mostrarMonitorCocina)); menu.add(Box.createRigidArea(new Dimension(0, 14)));
        }
        if (usuario.getRol().equals("ADMIN")) {
            JLabel admin = new JLabel("Solo disponible para admin");
            admin.setForeground(new Color(190, 190, 210)); admin.setFont(new Font("SansSerif", Font.PLAIN, 11)); admin.setAlignmentX(Component.LEFT_ALIGNMENT);
            menu.add(Box.createRigidArea(new Dimension(0, 10))); menu.add(admin); menu.add(Box.createRigidArea(new Dimension(0, 8)));
            menu.add(botonMenu("Registrar usuario", this::mostrarRegistrarUsuario)); menu.add(Box.createRigidArea(new Dimension(0, 14)));
            menu.add(botonMenu("Registrar producto", () -> mostrarRegistrarProducto(false))); menu.add(Box.createRigidArea(new Dimension(0, 14)));
            menu.add(botonMenu("Registrar combo", () -> mostrarRegistrarProducto(true)));
        }
        menu.add(Box.createVerticalGlue());
        BotonRedondo salir = botonMenu("Cerrar sesión", () -> { usuario.logout(); new LoginFrame().setVisible(true); dispose(); });
        salir.setBackground(new Color(150, 0, 10));
        menu.add(salir);
        return menu;
    }

    private BotonRedondo botonMenu(String texto, Runnable accion) {
        BotonRedondo b = new BotonRedondo(texto, 16);
        b.setBackground(new Color(30, 48, 125)); b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46)); b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addActionListener(e -> accion.run()); return b;
    }

    private void setPantallaActual(Runnable pantalla) { this.pantallaActual = pantalla; }
    private void refrescarPantallaActual() { if (pantallaActual != null) pantallaActual.run(); }

    private void limpiar(String titulo) {
        contenido.removeAll(); tituloContenido.setText(titulo); contenido.add(tituloContenido, BorderLayout.NORTH);
    }
    private void finalizarPantalla(JComponent panel) { contenido.add(panel, BorderLayout.CENTER); contenido.revalidate(); contenido.repaint(); }
    private void mostrarError(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }

    private void mostrarInicio() {
        setPantallaActual(null);
        limpiar("FideBurguesas POS");
        JLabel label = new JLabel("SQLite persistente + Red por sockets", SwingConstants.CENTER);
        label.setForeground(Color.WHITE); label.setFont(new Font("SansSerif", Font.BOLD, 28));
        finalizarPantalla(label);
    }

    private void mostrarCrearOrden() {
        setPantallaActual(null);
        limpiar("Añadir productos");
        JPanel panel = new JPanel(new BorderLayout(15, 15)); panel.setOpaque(false); panel.setBorder(new EmptyBorder(10, 20, 20, 20));
        DefaultListModel<Producto> modeloCarrito = new DefaultListModel<>();
        JList<Producto> carrito = new JList<>(modeloCarrito); carrito.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JLabel total = new JLabel("Total: ₡0.00"); total.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16)); grid.setOpaque(false);
        try {
            for (Producto p : BaseDatos.getProductos()) {
                BotonRedondo item = new BotonRedondo("<html><center>" + p.getNombre() + "<br>₡" + String.format("%,.2f", p.calcularPrecio()) + "</center></html>", 18);
                item.setBackground(p instanceof Combo ? new Color(55, 72, 150) : azulPanel);
                item.setPreferredSize(new Dimension(160, 105));
                item.addActionListener(e -> { modeloCarrito.addElement(p); actualizarTotal(modeloCarrito, total); });
                grid.add(item);
            }
        } catch (Exception ex) { mostrarError(ex); }

        JPanel derecha = new JPanel(new BorderLayout(8, 8)); derecha.setPreferredSize(new Dimension(300, 0)); derecha.setBorder(new EmptyBorder(15, 15, 15, 15)); derecha.setBackground(new Color(235, 235, 235));
        JLabel carTitulo = new JLabel("Carrito", SwingConstants.CENTER); carTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        derecha.add(carTitulo, BorderLayout.NORTH); derecha.add(new JScrollPane(carrito), BorderLayout.CENTER);
        JPanel acciones = new JPanel(new GridLayout(0, 1, 8, 8)); acciones.setOpaque(false);
        JTextField cliente = new JTextField("Cliente final");
        BotonRedondo completar = new BotonRedondo("Completar orden", 14); completar.setBackground(new Color(73, 135, 36));
        BotonRedondo cancelar = new BotonRedondo("Cancelar orden", 14); cancelar.setBackground(new Color(150, 0, 10));
        acciones.add(cliente); acciones.add(total); acciones.add(completar); acciones.add(cancelar); derecha.add(acciones, BorderLayout.SOUTH);

        completar.addActionListener(e -> {
            try {
                if (modeloCarrito.isEmpty()) { JOptionPane.showMessageDialog(this, "Agregue al menos un producto."); return; }
                Cajero cajero = BaseDatos.comoCajero(usuario);
                List<Producto> productos = new ArrayList<>(); for (int i = 0; i < modeloCarrito.size(); i++) productos.add(modeloCarrito.get(i));
                Orden orden = BaseDatos.crearOrden(cajero, cliente.getText().trim().isEmpty() ? "Cliente final" : cliente.getText().trim(), productos);
                Factura factura = BaseDatos.generarFactura(orden);
                JOptionPane.showMessageDialog(this, factura.mostrarFactura(), "Factura guardada", JOptionPane.INFORMATION_MESSAGE);
                modeloCarrito.clear(); actualizarTotal(modeloCarrito, total);
            } catch (Exception ex) { mostrarError(ex); }
        });
        cancelar.addActionListener(e -> { modeloCarrito.clear(); actualizarTotal(modeloCarrito, total); });
        panel.add(new JScrollPane(grid), BorderLayout.CENTER); panel.add(derecha, BorderLayout.EAST); finalizarPantalla(panel);
    }

    private void actualizarTotal(DefaultListModel<Producto> modelo, JLabel label) {
        double suma = 0; for (int i = 0; i < modelo.size(); i++) suma += modelo.get(i).calcularPrecio();
        label.setText("Total: ₡" + String.format("%,.2f", suma));
    }

    private void mostrarOrdenes() {
        setPantallaActual(this::mostrarOrdenes);
        limpiar("Órdenes registradas");
        String[] cols = {"ID", "Cliente", "Cajero", "Estado", "Total"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        try {
            for (Orden o : BaseDatos.getOrdenes()) {
                double total = 0; try { total = o.calcularTotal(); } catch (Exception ignored) {}
                model.addRow(new Object[]{o.getIdOrden(), o.getCliente(), o.getCajero().getNombre(), o.getEstado(), "₡" + String.format("%,.2f", total)});
            }
        } catch (Exception ex) { mostrarError(ex); }
        JTable tabla = new JTable(model); tabla.setRowHeight(28); finalizarPantalla(new JScrollPane(tabla));
    }

    private void mostrarMonitorCocina() {
        setPantallaActual(this::mostrarMonitorCocina);
        limpiar("Órdenes pendientes/en preparación");
        JPanel panel = new JPanel(new GridLayout(0, 3, 15, 15)); panel.setOpaque(false); panel.setBorder(new EmptyBorder(10, 20, 20, 20));
        try {
            for (Orden o : BaseDatos.getMonitorCocina().mostrarOrdenes()) {
                TarjetaPanel card = new TarjetaPanel(18, o.getEstado().equals("PENDIENTE") ? new Color(67, 10, 55) : new Color(60, 30, 70));
                card.setLayout(new BorderLayout(8, 8)); card.setBorder(new EmptyBorder(15, 15, 15, 15));
                JLabel header = new JLabel("Orden No.: " + o.getIdOrden() + "  " + o.getEstado());
                header.setForeground(Color.WHITE); header.setFont(new Font("SansSerif", Font.BOLD, 16));
                JTextArea productos = new JTextArea(); productos.setEditable(false); productos.setOpaque(false); productos.setForeground(Color.WHITE); productos.setFont(new Font("SansSerif", Font.PLAIN, 14));
                for (Producto p : o.getListaProductos()) productos.append("• " + p.getNombre() + "\n");
                BotonRedondo cambiar = new BotonRedondo(o.getEstado().equals("PENDIENTE") ? "Cambiar estado a Preparando" : "Cambiar estado a Entregado", 14);
                cambiar.setBackground(new Color(165, 205, 42)); cambiar.setForeground(Color.BLACK);
                cambiar.addActionListener(e -> {
                    try { BaseDatos.getMonitorCocina().actualizarEstadoOrden(o, o.getEstado().equals("PENDIENTE") ? "EN_PREPARACION" : "ENTREGADO"); }
                    catch (Exception ex) { mostrarError(ex); }
                    mostrarMonitorCocina();
                });
                card.add(header, BorderLayout.NORTH); card.add(productos, BorderLayout.CENTER); card.add(cambiar, BorderLayout.SOUTH); panel.add(card);
            }
        } catch (Exception ex) { mostrarError(ex); }
        finalizarPantalla(new JScrollPane(panel));
    }

    private void mostrarFacturas() {
        setPantallaActual(this::mostrarFacturas);
        limpiar("Generar factura");
        JPanel panel = new JPanel(new BorderLayout(12, 12)); panel.setOpaque(false); panel.setBorder(new EmptyBorder(10, 25, 25, 25));
        DefaultListModel<Orden> modelo = new DefaultListModel<>();
        try { for (Orden o : BaseDatos.getOrdenes()) modelo.addElement(o); } catch (Exception ex) { mostrarError(ex); }
        JList<Orden> lista = new JList<>(modelo);
        JTextArea factura = new JTextArea(); factura.setFont(new Font("Monospaced", Font.PLAIN, 14)); factura.setEditable(false);
        BotonRedondo generar = new BotonRedondo("Generar factura", 14); generar.setBackground(new Color(62, 111, 255));
        generar.addActionListener(e -> {
            try {
                Orden seleccionada = lista.getSelectedValue();
                if (seleccionada == null) { JOptionPane.showMessageDialog(this, "Seleccione una orden."); return; }
                factura.setText(BaseDatos.generarFactura(seleccionada).mostrarFactura());
            } catch (Exception ex) { mostrarError(ex); }
        });
        panel.add(new JScrollPane(lista), BorderLayout.WEST); panel.add(new JScrollPane(factura), BorderLayout.CENTER); panel.add(generar, BorderLayout.SOUTH); finalizarPantalla(panel);
    }

    private void mostrarRegistrarUsuario() {
        setPantallaActual(null);
        limpiar("Registrar usuario");
        JPanel form = formularioBase(); JTextField nombre = new JTextField(); JTextField user = new JTextField(); JPasswordField pass = new JPasswordField();
        JComboBox<String> rol = new JComboBox<>(new String[]{"CAJERO", "COCINA", "ADMIN"});
        agregarCampo(form, "Nombre", nombre); agregarCampo(form, "Usuario", user); agregarCampo(form, "Contraseña", pass); agregarCampo(form, "Rol", rol);
        BotonRedondo guardar = new BotonRedondo("Guardar usuario", 14); guardar.setBackground(new Color(62, 111, 255));
        guardar.addActionListener(e -> { try { BaseDatos.registrarUsuario(nombre.getText(), user.getText(), new String(pass.getPassword()), (String) rol.getSelectedItem()); JOptionPane.showMessageDialog(this, "Usuario registrado en SQLite."); } catch (Exception ex) { mostrarError(ex); } });
        form.add(guardar); finalizarPantalla(form);
    }

    private void mostrarRegistrarProducto(boolean combo) {
        setPantallaActual(null);
        limpiar(combo ? "Registrar combo" : "Registrar producto");
        JPanel form = formularioBase(); JTextField nombre = new JTextField(); JTextField precio = new JTextField();
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Hamburguesa", "Bebida", "Extra", "Combo"}); tipo.setSelectedItem(combo ? "Combo" : "Hamburguesa");
        agregarCampo(form, "Nombre", nombre); agregarCampo(form, "Precio", precio); agregarCampo(form, "Tipo", tipo);
        BotonRedondo guardar = new BotonRedondo(combo ? "Guardar combo" : "Guardar producto", 14); guardar.setBackground(new Color(62, 111, 255));
        guardar.addActionListener(e -> {
            try { double p = Double.parseDouble(precio.getText()); if (combo) BaseDatos.registrarCombo(nombre.getText(), p); else BaseDatos.registrarProducto(nombre.getText(), p, (String) tipo.getSelectedItem(), true); JOptionPane.showMessageDialog(this, combo ? "Combo registrado en SQLite." : "Producto registrado en SQLite."); }
            catch (Exception ex) { mostrarError(ex); }
        });
        form.add(guardar); finalizarPantalla(form);
    }

    private JPanel formularioBase() {
        JPanel form = new JPanel(); form.setOpaque(false); form.setBorder(new EmptyBorder(30, 260, 120, 260)); form.setLayout(new GridLayout(0, 1, 8, 8)); return form;
    }
    private void agregarCampo(JPanel form, String label, JComponent campo) {
        JLabel l = new JLabel(label); l.setForeground(Color.WHITE); l.setFont(new Font("SansSerif", Font.BOLD, 14)); form.add(l); form.add(campo);
    }
}
