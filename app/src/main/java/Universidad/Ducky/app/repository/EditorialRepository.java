package Universidad.Ducky.app.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class EditorialRepository {

    private final JdbcTemplate jdbcTemplate;

    public EditorialRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.queryForList(
                "SELECT id_editorial, nombre FROM editorial ORDER BY nombre");
    }
}
