package Universidad.Ducky.app.model;

public class LibroCard {
    private int id;
    private String nombre;
    private String tipo;
    private String autorNombre;
    private String estado;

    public LibroCard(int id, String nombre, String tipo, String autorNombre, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.autorNombre = autorNombre;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getAutorNombre() { return autorNombre; }
    public String getEstado() { return estado; }
}
