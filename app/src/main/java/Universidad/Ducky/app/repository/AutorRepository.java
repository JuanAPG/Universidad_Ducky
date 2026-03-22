package Universidad.Ducky.app.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AutorRepository {

    private final JdbcTemplate jdbcTemplate;

    public AutorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.queryForList(
                "SELECT id_autor, nombre FROM autor ORDER BY nombre");
    }
}
