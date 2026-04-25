package com.dchambers.vistas;

import com.dchambers.Usuario;
import com.dchambers.datos.BaseDatos;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("FideBurguesas - Iniciar Sesión");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        FondoPanel backgroundPanel = new FondoPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        TarjetaPanel card = new TarjetaPanel(30, new Color(8, 16, 60));
        card.setPreferredSize(new Dimension(380, 410));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 32, 30, 32));

        JLabel titleLabel = new JLabel("Iniciar Sesión");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        JLabel subtitleLabel = new JLabel("FideBurguesas POS");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(180, 185, 210));

        JTextField txtUsuario = new JTextField();
        JPasswordField txtContrasena = new JPasswordField();
        estilizarCampo(txtUsuario, "Usuario");
        estilizarCampo(txtContrasena, "Contraseña");

        BotonRedondo btnIngresar = new BotonRedondo("Ingresar", 18);
        btnIngresar.setBackground(new Color(62, 111, 255));
        btnIngresar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel demo = new JLabel("cajero/admin/cocina | contraseña: 1234");
        demo.setForeground(new Color(190, 195, 215));
        demo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        //demo.setMinimumSize(new Dimension(0, 20));
        demo.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnIngresar.addActionListener(e -> ingresar(txtUsuario.getText().trim(), new String(txtContrasena.getPassword()).trim()));
        txtContrasena.addActionListener(e -> ingresar(txtUsuario.getText().trim(), new String(txtContrasena.getPassword()).trim()));

        card.add(titleLabel); card.add(Box.createRigidArea(new Dimension(0, 6))); card.add(subtitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 38))); card.add(txtUsuario); card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(txtContrasena); card.add(Box.createRigidArea(new Dimension(0, 20))); card.add(btnIngresar);
        card.add(Box.createRigidArea(new Dimension(0, 18))); card.add(demo);
        backgroundPanel.add(card);
    }

    private void ingresar(String usuario, String contrasena) {
        try {
            Usuario autenticado = BaseDatos.autenticar(usuario, contrasena);
            if (autenticado == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            autenticado.login();
            new PrincipalFrame(autenticado).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void estilizarCampo(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(Color.DARK_GRAY);
        field.setBackground(new Color(240, 240, 245));
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
    }
}
