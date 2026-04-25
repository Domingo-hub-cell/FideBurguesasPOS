package com.dchambers;

import com.dchambers.datos.BaseDatos;
import java.sql.SQLException;
import java.util.List;

public class MonitorCocina {
    public List<Orden> mostrarOrdenes() throws SQLException { return BaseDatos.getOrdenesPendientes(); }
    public void actualizarEstadoOrden(Orden orden, String nuevoEstado) throws SQLException { BaseDatos.actualizarEstadoOrden(orden.getIdOrden(), nuevoEstado); }
}
