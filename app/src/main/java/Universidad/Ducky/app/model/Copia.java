package Universidad.Ducky.app.model;

import java.util.ArrayList;
import java.util.List;

public class Copia {
    private String identificador;
    private String estado;
    private Libro original;
    private List<Prestamo> prestamos = new ArrayList<>();

    public Copia() {
    }

    public Copia(String identificador, String estado, Libro original) {
        this.identificador = identificador;
        this.estado = estado;
        this.original = original;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Libro getOriginal() {
        return original;
    }

    public void setOriginal(Libro original) {
        this.original = original;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    public void devolver() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public void prestar() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
