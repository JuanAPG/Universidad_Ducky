package Universidad.Ducky.app.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Editorial {
    private String nombre;
    private String pais;
    private LocalDate fechaFundacion;
    private List<Libro> libros = new ArrayList<>();

    public Editorial() {
    }

    public Editorial(String nombre, String pais, LocalDate fechaFundacion) {
        this.nombre = nombre;
        this.pais = pais;
        this.fechaFundacion = fechaFundacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public LocalDate getFechaFundacion() {
        return fechaFundacion;
    }

    public void setFechaFundacion(LocalDate fechaFundacion) {
        this.fechaFundacion = fechaFundacion;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }
}
