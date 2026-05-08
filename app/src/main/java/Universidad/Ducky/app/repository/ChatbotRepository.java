package Universidad.Ducky.app.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatbotRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChatbotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countPrestamosTotal() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM prestamo", Integer.class);
    }

    public int countPrestamosActivos() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM prestamo p JOIN copia c ON c.id_copia = p.id_copia WHERE c.estado = 'prestado'",
                Integer.class);
    }

    public int countMultasPendientes() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM multa WHERE estatus = 'pendiente'", Integer.class);
    }

    public int countLibros() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM libro", Integer.class);
    }

    public int countCopiasDisponibles() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM copia WHERE estado = 'disponible'", Integer.class);
    }

    public int countUsuarios() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE tipo = 'usuario'", Integer.class);
    }
}
