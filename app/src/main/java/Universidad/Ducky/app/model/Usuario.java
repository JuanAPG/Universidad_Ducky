package Universidad.Ducky.app.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Usuario {
    private Integer id;
    private String username;
    private String contrasena;
    private String nombre;
    private String apellidos;
    private String tipo;
    private String direccion;
    private List<Prestamo> prestamos = new ArrayList<>();
    private List<Multa> multas = new ArrayList<>();
    private Set<UsuarioPermiso> permisos = new HashSet<>();

    public Usuario() {
    }

    public Usuario(Integer id, String username, String contrasena, String nombre, String apellidos, String direccion, String tipo) {
        this.id = id;
        this.username = username;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.tipo = tipo;
        this.direccion = direccion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    public List<Multa> getMultas() {
        return multas;
    }

    public void setMultas(List<Multa> multas) {
        this.multas = multas;
    }

    public Set<UsuarioPermiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<UsuarioPermiso> permisos) {
        this.permisos = permisos;
    }

    public boolean comprobarMultasPendientes() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
