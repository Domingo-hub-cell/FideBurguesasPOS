package com.dchambers.red;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedCliente {
    private static final RedCliente INSTANCIA = new RedCliente();
    private final List<Runnable> observadores = new CopyOnWriteArrayList<>();
    private PrintWriter salida;
    private boolean conectado;

    public static RedCliente getInstancia() { return INSTANCIA; }

    public synchronized void conectar() {
        if (conectado) return;
        try {
            Socket socket = new Socket("localhost", RedServidor.PUERTO);
            salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            conectado = true;
            Thread lector = new Thread(() -> escuchar(socket), "Cliente-FideBurguesas");
            lector.setDaemon(true);
            lector.start();
            System.out.println("Cliente conectado al servidor POS.");
        } catch (IOException e) {
            System.out.println("No se pudo conectar a la red POS: " + e.getMessage());
        }
    }

    private void escuchar(Socket socket) {
        try (Socket s = socket; BufferedReader entrada = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                if (linea.startsWith("REFRESH:")) {
                    SwingUtilities.invokeLater(() -> observadores.forEach(Runnable::run));
                }
            }
        } catch (IOException ignored) {
        } finally {
            conectado = false;
        }
    }

    public void enviar(String mensaje) {
        if (!conectado) conectar();
        if (salida != null) salida.println(mensaje);
    }

    public void agregarObservador(Runnable observador) { observadores.add(observador); }
    public void removerObservador(Runnable observador) { observadores.remove(observador); }
}
