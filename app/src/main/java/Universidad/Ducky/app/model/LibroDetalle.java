package Universidad.Ducky.app.model;

import java.math.BigDecimal;

public class LibroDetalle {
    private int id;
    private String nombre;
    private String tipo;
    private int anio;
    private int idEditorial;
    private String isbn;
    private String sinopsis;
    private Integer numPaginas;
    private BigDecimal precio;
    private int numCopias;

    public LibroDetalle(int id, String nombre, String tipo, int anio, int idEditorial,
                        String isbn, String sinopsis, Integer numPaginas,
                        BigDecimal precio, int numCopias) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.anio = anio;
        this.idEditorial = idEditorial;
        this.isbn = isbn;
        this.sinopsis = sinopsis;
        this.numPaginas = numPaginas;
        this.precio = precio;
        this.numCopias = numCopias;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public int getAnio() { return anio; }
    public int getIdEditorial() { return idEditorial; }
    public String getIsbn() { return isbn; }
    public String getSinopsis() { return sinopsis; }
    public Integer getNumPaginas() { return numPaginas; }
    public BigDecimal getPrecio() { return precio; }
    public int getNumCopias() { return numCopias; }
}
