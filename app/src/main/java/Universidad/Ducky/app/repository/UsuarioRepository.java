package Universidad.Ducky.app.repository;

import Universidad.Ducky.app.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.queryForList(
                "SELECT id_usuario, username, nombre, apellidos, tipo, direccion AS correo FROM usuario ORDER BY apellidos, nombre");
    }

    public Usuario findByUsernameAndPassword(String username, String contrasena) {
        String sql = "SELECT id_usuario, username, contrasena, nombre, apellidos, tipo, direccion " +
                     "FROM usuario WHERE username = ? AND contrasena = ?";
        List<Usuario> usuarios = jdbcTemplate.query(sql, (rs, rowNum) -> mapUsuario(rs), username, contrasena);
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }

    public Usuario findById(int id) {
        String sql = "SELECT id_usuario, username, contrasena, nombre, apellidos, tipo, direccion " +
                     "FROM usuario WHERE id_usuario = ?";
        List<Usuario> usuarios = jdbcTemplate.query(sql, (rs, rowNum) -> mapUsuario(rs), id);
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }

    public Set<String> findPermissionNamesByUserId(int userId) {
        return new LinkedHashSet<>(jdbcTemplate.queryForList(
                """
                SELECT p.nombre
                FROM usuario_permiso up
                JOIN permiso p ON p.id_permiso = up.id_permiso
                WHERE up.id_usuario = ? AND up.activo = TRUE
                ORDER BY p.id_permiso
                """,
                String.class,
                userId
        ));
    }

    @Transactional
    public void insert(String username, String contrasena, String nombre, String apellidos,
                       String tipo, String correo, List<String> permisos) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO usuario (username, contrasena, nombre, apellidos, tipo, direccion) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"id_usuario"});
            ps.setString(1, normalize(username));
            ps.setString(2, contrasena);
            ps.setString(3, normalize(nombre));
            ps.setString(4, normalize(apellidos));
            ps.setString(5, normalize(tipo));

            String correoNormalizado = normalize(correo);
            if (correoNormalizado != null) {
                ps.setString(6, correoNormalizado);
            } else {
                ps.setNull(6, Types.VARCHAR);
            }
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            replacePermissions(generatedId.intValue(), permisos);
        }
    }

    @Transactional
    public void update(int id, String username, String contrasena, String nombre, String apellidos,
                       String tipo, String correo, List<String> permisos) {
        String correoNormalizado = normalize(correo);
        jdbcTemplate.update(
                "UPDATE usuario SET username = ?, contrasena = ?, nombre = ?, apellidos = ?, tipo = ?, direccion = ? WHERE id_usuario = ?",
                normalize(username),
                contrasena,
                normalize(nombre),
                normalize(apellidos),
                normalize(tipo),
                correoNormalizado,
                id
        );

        replacePermissions(id, permisos);
    }

    private void replacePermissions(int userId, List<String> permisos) {
        jdbcTemplate.update("DELETE FROM usuario_permiso WHERE id_usuario = ?", userId);

        if (permisos == null || permisos.isEmpty()) {
            return;
        }

        for (String permiso : permisos) {
            jdbcTemplate.update(
                    """
                    INSERT INTO usuario_permiso (id_usuario, id_permiso, activo)
                    SELECT ?, id_permiso, TRUE
                    FROM permiso
                    WHERE nombre = ?
                    """,
                    userId,
                    permiso
            );
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setContrasena(rs.getString("contrasena"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidos(rs.getString("apellidos"));
        u.setTipo(rs.getString("tipo"));
        u.setCorreo(rs.getString("direccion"));
        return u;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
