package com.dchambers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Orden {
    private int idOrden;
    private LocalDateTime fecha;
    private Cajero cajero;
    private String cliente;
    private ArrayList<Producto> listaProductos;
    private String estado;

    public Orden(int idOrden, Cajero cajero, String cliente) {
        this(idOrden, LocalDateTime.now(), cajero, cliente, new ArrayList<>(), "PENDIENTE");
    }

    public Orden(int idOrden, LocalDateTime fecha, Cajero cajero, String cliente, ArrayList<Producto> listaProductos, String estado) {
        this.idOrden = idOrden;
        this.fecha = fecha;
        this.cajero = cajero;
        this.cliente = cliente;
        this.listaProductos = listaProductos;
        this.estado = estado;
    }

    public void agregarProducto(Producto producto) throws Exception {
        if (producto == null || !producto.isDisponible()) throw new Exception("Producto no disponible.");
        listaProductos.add(producto);
    }

    public double calcularTotal() throws Exception {
        if (listaProductos.isEmpty()) throw new Exception("La orden no contiene productos.");
        return listaProductos.stream().mapToDouble(Producto::calcularPrecio).sum();
    }

    public void cambiarEstado(String estado) {
        this.estado = estado;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public String getCliente() {
        return cliente;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Cajero getCajero() {
        return cajero;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "Orden #" + idOrden + " - " + cliente + " - " + estado + " - " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
