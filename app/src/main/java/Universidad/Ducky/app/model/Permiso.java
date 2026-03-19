package Universidad.Ducky.app.model;

import java.util.HashSet;
import java.util.Set;

public class Permiso {
    private Integer idPermiso;
    private String nombre;
    private String descripcion;
    private Set<UsuarioPermiso> usuariosConPermiso = new HashSet<>();

    public Permiso() {
    }

    public Permiso(Integer idPermiso, String nombre, String descripcion) {
        this.idPermiso = idPermiso;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Integer idPermiso) {
        this.idPermiso = idPermiso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<UsuarioPermiso> getUsuariosConPermiso() {
        return usuariosConPermiso;
    }

    public void setUsuariosConPermiso(Set<UsuarioPermiso> usuariosConPermiso) {
        this.usuariosConPermiso = usuariosConPermiso;
    }
}
