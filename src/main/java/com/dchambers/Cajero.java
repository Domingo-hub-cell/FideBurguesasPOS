package com.dchambers;

public class Cajero extends Usuario {
    private String sucursal;
    public Cajero(int idUsuario, String nombre, String username, String password, String rol, String sucursal) {
        super(idUsuario, nombre, username, password, rol);
        this.sucursal = sucursal;
    }
    public Orden crearOrden(int idOrden, String cliente) { return new Orden(idOrden, this, cliente); }
    public Factura generarFactura(Orden orden) { return new Factura(0, orden); }
    public String getSucursal() { return sucursal; }
}
