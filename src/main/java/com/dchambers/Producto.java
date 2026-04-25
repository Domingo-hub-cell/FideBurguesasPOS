package com.dchambers;

public class Producto {
    protected int idProducto;
    protected String nombre;
    protected double precio;
    protected String tipo;
    protected boolean disponible;

    public Producto(int idProducto, String nombre, double precio, String tipo, boolean disponible) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
        this.disponible = disponible;
    }

    public void actualizarPrecio(double nuevoPrecio) { if (nuevoPrecio >= 0) precio = nuevoPrecio; }
    public void cambiarDisponibilidad(boolean disponible) { this.disponible = disponible; }
    public double calcularPrecio() { return precio; }
    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public String getTipo() { return tipo; }
    public boolean isDisponible() { return disponible; }
    @Override public String toString() { return nombre + " - ₡" + String.format("%,.2f", calcularPrecio()); }
}
