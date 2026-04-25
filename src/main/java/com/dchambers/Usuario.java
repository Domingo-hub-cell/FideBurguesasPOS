package com.dchambers;

public class Usuario {
    protected int idUsuario;
    protected String nombre;
    protected String username;
    protected String password;
    protected String rol;

    public Usuario(int idUsuario, String nombre, String username, String password, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public boolean validarCredenciales(String user, String pass) { return username.equals(user) && password.equals(pass); }
    public void login() { System.out.println(nombre + " inició sesión."); }
    public void logout() { System.out.println(nombre + " cerró sesión."); }
    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    @Override public String toString() { return nombre + " (" + rol + ")"; }
}
