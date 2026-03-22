package Universidad.Ducky.app.model;

public class LibroAdmin {
    private int id;
    private String nombre;
    private String autores;
    private int anio;
    private String tipo;
    private int numCopias;

    public LibroAdmin(int id, String nombre, String autores, int anio, String tipo, int numCopias) {
        this.id = id;
        this.nombre = nombre;
        this.autores = autores;
        this.anio = anio;
        this.tipo = tipo;
        this.numCopias = numCopias;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getAutores() { return autores; }
    public int getAnio() { return anio; }
    public String getTipo() { return tipo; }
    public int getNumCopias() { return numCopias; }
}
