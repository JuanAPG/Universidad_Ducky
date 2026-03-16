package Universidad.Ducky.app.model;

import java.time.LocalDate;

public class Prestamo {
    private LocalDate fechaInicio;
    private String estado;
    private Usuario usuario;
    private Copia copia;
    private Multa multa;

    public Prestamo() {
    }

    public Prestamo(LocalDate fechaInicio, String estado, Usuario usuario, Copia copia) {
        this.fechaInicio = fechaInicio;
        this.estado = estado;
        this.usuario = usuario;
        this.copia = copia;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Copia getCopia() {
        return copia;
    }

    public void setCopia(Copia copia) {
        this.copia = copia;
    }

    public Multa getMulta() {
        return multa;
    }

    public void setMulta(Multa multa) {
        this.multa = multa;
    }

    public LocalDate calcularFechaFin() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public Multa generarMulta() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
