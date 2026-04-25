package com.dchambers;

import com.dchambers.datos.BaseDatos;
import com.dchambers.red.RedCliente;
import com.dchambers.red.RedServidor;
import com.dchambers.vistas.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        RedServidor.iniciarSiEsPosible();
        try { BaseDatos.inicializar(); }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo iniciar la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        RedCliente.getInstancia().conectar();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
