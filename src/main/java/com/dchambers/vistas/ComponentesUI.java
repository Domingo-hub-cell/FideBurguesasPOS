package com.dchambers.vistas;

import javax.swing.*;
import java.awt.*;

class FondoPanel extends JPanel {
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, new Color(1, 6, 38), getWidth(), getHeight(), new Color(0, 2, 25)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}

class BotonRedondo extends JButton {
    private final int radius;
    public BotonRedondo(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isArmed() ? getBackground().darker() : (getModel().isRollover() ? getBackground().brighter() : getBackground()));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
    @Override protected void paintBorder(Graphics g) { }
}

class TarjetaPanel extends JPanel {
    private final int radius;
    private final Color color;
    public TarjetaPanel(int radius, Color color) {
        this.radius = radius;
        this.color = color;
        setOpaque(false);
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
