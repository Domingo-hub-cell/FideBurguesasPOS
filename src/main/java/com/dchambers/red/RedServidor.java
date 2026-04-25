package com.dchambers.red;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RedServidor {
    public static final int PUERTO = 5050;
    private static final Set<PrintWriter> clientes = ConcurrentHashMap.newKeySet();

    public static void iniciarSiEsPosible() {
        Thread t = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PUERTO)) {
                System.out.println("Servidor de red POS iniciado en localhost:" + PUERTO);
                while (true) atender(server.accept());
            } catch (IOException e) {
                System.out.println("Ya existe un servidor de red POS activo o no se pudo iniciar: " + e.getMessage());
            }
        }, "Servidor-FideBurguesas");
        t.setDaemon(true);
        t.start();
        try { Thread.sleep(250); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void atender(Socket socket) {
        Thread t = new Thread(() -> {
            PrintWriter salida = null;
            try (Socket s = socket;
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                salida = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
                clientes.add(salida);
                String linea;
                while ((linea = entrada.readLine()) != null) broadcast(linea);
            } catch (IOException ignored) {
            } finally {
                if (salida != null) clientes.remove(salida);
            }
        }, "Cliente-Red-POS");
        t.setDaemon(true);
        t.start();
    }

    private static void broadcast(String mensaje) {
        for (PrintWriter cliente : clientes) cliente.println(mensaje);
    }
}
