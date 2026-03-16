package Universidad.Ducky.app.model;

import java.util.ArrayList;
import java.util.List;

public class Libro {
    private String nombre;
    private String tipo;
    private int anio;
    private List<Copia> ejemplares = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();
    private Editorial editorial;

    public Libro() {
    }

    public Libro(String nombre, String tipo, int anio) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.anio = anio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public List<Copia> getEjemplares() {
        return ejemplares;
    }

    public void setEjemplares(List<Copia> ejemplares) {
        this.ejemplares = ejemplares;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
    }
}
