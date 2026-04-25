package com.dchambers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Factura {
    private int idFactura;
    private Orden orden;
    private double subtotal;
    private double impuesto;
    private double total;
    private LocalDateTime fecha;

    public Factura(int idFactura, Orden orden) {
        this(idFactura, orden, LocalDateTime.now());
    }
    public Factura(int idFactura, Orden orden, LocalDateTime fecha) {
        this.idFactura = idFactura;
        this.orden = orden;
        this.fecha = fecha;
        calcularTotales();
    }
    public void calcularTotales() {
        try { subtotal = orden.calcularTotal(); impuesto = subtotal * 0.13; total = subtotal + impuesto; }
        catch (Exception e) { subtotal = impuesto = total = 0; }
    }
    public String generarArchivoFactura() { return mostrarFactura(); }
    public String imprimirFactura() { return mostrarFactura(); }
    public String mostrarFactura() {
        StringBuilder sb = new StringBuilder();
        sb.append("FideBurguesas\nFactura Cliente\n\n");
        sb.append("Factura No.: ").append(idFactura == 0 ? "Pendiente de guardar" : idFactura).append("\n");
        sb.append("Fecha: ").append(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        sb.append("Cajero: ").append(orden.getCajero().getNombre()).append("\n");
        sb.append("Cliente: ").append(orden.getCliente()).append("\n\nProductos:\n");
        for (Producto p : orden.getListaProductos()) sb.append(" - ").append(p.getNombre()).append("  ₡").append(String.format("%,.2f", p.calcularPrecio())).append("\n");
        sb.append("\nSubtotal: ₡").append(String.format("%,.2f", subtotal));
        sb.append("\nImpuesto (13%): ₡").append(String.format("%,.2f", impuesto));
        sb.append("\nTotal: ₡").append(String.format("%,.2f", total));
        return sb.toString();
    }
    public int getIdFactura() { return idFactura; }
    public Orden getOrden() { return orden; }
    public double getSubtotal() { return subtotal; }
    public double getImpuesto() { return impuesto; }
    public double getTotal() { return total; }
    public LocalDateTime getFecha() { return fecha; }
}
