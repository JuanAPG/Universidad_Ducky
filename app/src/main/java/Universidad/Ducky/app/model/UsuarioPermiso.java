package Universidad.Ducky.app.model;

public class UsuarioPermiso {
    private Usuario usuario;
    private Permiso permiso;
    private boolean activo = true;

    public UsuarioPermiso() {
    }

    public UsuarioPermiso(Usuario usuario, Permiso permiso, boolean activo) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.activo = activo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
