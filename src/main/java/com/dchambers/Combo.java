package com.dchambers;

import java.util.ArrayList;
import java.util.List;

public class Combo extends Producto {
    private final ArrayList<Producto> listaProductos = new ArrayList<>();
    public Combo(int idProducto, String nombre, double precio, String tipo, boolean disponible) { super(idProducto, nombre, precio, tipo, disponible); }
    public void agregarProducto(Producto producto) { if (producto != null) listaProductos.add(producto); }
    public double calcularPrecioCombo() { return precio > 0 ? precio * 0.90 : listaProductos.stream().mapToDouble(Producto::calcularPrecio).sum() * 0.90; }
    @Override public double calcularPrecio() { return calcularPrecioCombo(); }
    public List<Producto> getListaProductos() { return listaProductos; }
    @Override public String toString() { return nombre + " (Combo) - ₡" + String.format("%,.2f", calcularPrecio()); }
}
