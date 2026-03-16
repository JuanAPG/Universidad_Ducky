package Universidad.Ducky.app.model;

import java.time.LocalDate;

public class Multa {
    private LocalDate fecha;
    private String estatus;
    private Usuario usuario;
    private Prestamo prestamo;

    public Multa() {
    }

    public Multa(LocalDate fecha, String estatus, Usuario usuario, Prestamo prestamo) {
        this.fecha = fecha;
        this.estatus = estatus;
        this.usuario = usuario;
        this.prestamo = prestamo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }
}
