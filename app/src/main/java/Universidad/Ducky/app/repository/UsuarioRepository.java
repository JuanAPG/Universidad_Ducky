package Universidad.Ducky.app.repository;

import Universidad.Ducky.app.model.Usuario;
import org.springframework.dao.DataAccessException;
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

    public List<Usuario> findAll() {
        String sql = """
                SELECT u.id_usuario, u.username, u.contrasena, u.nombre, u.apellidos, u.tipo, u.direccion,
                       EXISTS (
                           SELECT 1
                           FROM prestamo p
                           WHERE p.id_usuario = u.id_usuario
                             AND p.fecha_fin IS NULL
                       ) AS prestamo_activo
                FROM usuario u
                ORDER BY id_usuario
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Usuario u = new Usuario();
            u.setId(rs.getInt("id_usuario"));
            u.setUsername(rs.getString("username"));
            u.setContrasena(rs.getString("contrasena"));
            u.setNombre(rs.getString("nombre"));
            u.setApellidos(rs.getString("apellidos"));
            u.setTipo(rs.getString("tipo"));
            u.setDireccion(rs.getString("direccion"));
            u.setPrestamoActivo(rs.getBoolean("prestamo_activo"));
            return u;
        });
    }

    public Usuario findAlumnoById(int idAlumno) {
        String sql = """
                SELECT id_usuario, username, contrasena, nombre, apellidos, tipo, direccion
                FROM usuario
                WHERE id_usuario = ? AND tipo = 'usuario'
                """;
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
        }, idAlumno);
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }

    public void createStudentAccess(int idAlumno, String nuevaContrasena) {
        jdbcTemplate.update(
                "UPDATE usuario SET username = ?, contrasena = ? WHERE id_usuario = ? AND tipo = 'usuario'",
                String.valueOf(idAlumno), nuevaContrasena, idAlumno
        );
    }

    public int updatePassword(int idUsuario, String nuevaContrasena) {
        return jdbcTemplate.update("UPDATE usuario SET contrasena = ? WHERE id_usuario = ?", nuevaContrasena, idUsuario);
    }

    public boolean hasActiveLoan(int idUsuario) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM prestamo
                    WHERE id_usuario = ?
                      AND fecha_fin IS NULL
                )
                """;
        Boolean hasActive = jdbcTemplate.queryForObject(sql, Boolean.class, idUsuario);
        return Boolean.TRUE.equals(hasActive);
    }

    public boolean createStaffUser(String username, String contrasena, String nombre,
                                   String apellidos, String tipo, String direccion) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO usuario (username, contrasena, nombre, apellidos, tipo, direccion) VALUES (?, ?, ?, ?, ?, ?)",
                    username, contrasena, nombre, apellidos, tipo, direccion
            ) > 0;
        } catch (DataAccessException ex) {
            return false;
        }
    }

    public boolean deleteById(int idUsuario) {
        try {
            return jdbcTemplate.update("DELETE FROM usuario WHERE id_usuario = ?", idUsuario) > 0;
        } catch (DataAccessException ex) {
            return false;
        }
    }
}
