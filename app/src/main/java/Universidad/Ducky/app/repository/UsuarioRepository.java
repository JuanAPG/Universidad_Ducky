package Universidad.Ducky.app.repository;

import Universidad.Ducky.app.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Usuario findByUsernameAndPassword(String username, String contrasena) {
        String sql = "SELECT id_usuario, username, contrasena, nombre, apellidos, tipo, direccion " +
                     "FROM usuario WHERE username = ? AND contrasena = ?";
        List<Usuario> usuarios = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Usuario u = new Usuario();
            u.setId(rs.getInt("id_usuario"));
            u.setUsername(rs.getString("username"));
            u.setContrasena(rs.getString("contrasena"));
            u.setNombre(rs.getString("nombre"));
            u.setApellidos(rs.getString("apellidos"));
            u.setTipo(rs.getString("tipo"));
            u.setDireccion(rs.getString("direccion"));
            return u;
        }, username, contrasena);
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }
}
