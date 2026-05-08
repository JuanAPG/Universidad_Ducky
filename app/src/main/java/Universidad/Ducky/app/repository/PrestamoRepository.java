package Universidad.Ducky.app.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class PrestamoRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final int MAX_PRESTAMOS = 3;

    public PrestamoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> findUserByUsername(String username) {
        String sql = """
                SELECT u.id_usuario, u.username, u.nombre, u.apellidos, u.tipo,
                       GREATEST(0, ? - (
                           SELECT COUNT(*) FROM prestamo p
                           JOIN copia c ON c.id_copia = p.id_copia
                           WHERE p.id_usuario = u.id_usuario AND c.estado = 'prestado'
                       )) AS prestamos_disponibles
                FROM usuario u
                WHERE u.username = ?
                """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, MAX_PRESTAMOS, username);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<Map<String, Object>> searchCopias(String q) {
        String pattern = "%" + q.toLowerCase() + "%";
        String sql = """
                SELECT c.id_copia, c.estado,
                       l.nombre AS titulo,
                       COALESCE(STRING_AGG(DISTINCT a.nombre, ', ' ORDER BY a.nombre), 'Sin autor') AS autores
                FROM copia c
                JOIN libro l ON l.id_libro = c.id_libro
                LEFT JOIN libroautor la ON la.id_libro = l.id_libro
                LEFT JOIN autor a ON a.id_autor = la.id_autor
                WHERE LOWER(l.nombre) LIKE ? OR LOWER(COALESCE(a.nombre, '')) LIKE ?
                GROUP BY c.id_copia, c.estado, l.nombre
                ORDER BY c.estado = 'disponible' DESC, l.nombre
                LIMIT 50
                """;
        return jdbcTemplate.queryForList(sql, pattern, pattern);
    }

    public List<Map<String, Object>> findAllCopias() {
        String sql = """
                SELECT c.id_copia, c.estado,
                       l.nombre AS titulo,
                       COALESCE(STRING_AGG(DISTINCT a.nombre, ', ' ORDER BY a.nombre), 'Sin autor') AS autores
                FROM copia c
                JOIN libro l ON l.id_libro = c.id_libro
                LEFT JOIN libroautor la ON la.id_libro = l.id_libro
                LEFT JOIN autor a ON a.id_autor = la.id_autor
                GROUP BY c.id_copia, c.estado, l.nombre
                ORDER BY c.estado = 'disponible' DESC, l.nombre
                LIMIT 50
                """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> findPrestamosActivos(int idUsuario) {
        String sql = """
                SELECT p.id_prestamo, p.fecha_inicio, p.fecha_fin,
                       l.nombre AS titulo,
                       COALESCE(l.isbn, 'N/A') AS isbn,
                       COALESCE(STRING_AGG(DISTINCT a.nombre, ', ' ORDER BY a.nombre), 'Sin autor') AS autores,
                       c.id_copia,
                       GREATEST(0, CURRENT_DATE - p.fecha_fin) AS dias_retraso
                FROM prestamo p
                JOIN copia c ON c.id_copia = p.id_copia
                JOIN libro l ON l.id_libro = c.id_libro
                LEFT JOIN libroautor la ON la.id_libro = l.id_libro
                LEFT JOIN autor a ON a.id_autor = la.id_autor
                WHERE p.id_usuario = ? AND c.estado = 'prestado'
                GROUP BY p.id_prestamo, p.fecha_inicio, p.fecha_fin, l.nombre, l.isbn, c.id_copia
                ORDER BY p.fecha_fin ASC
                """;
        return jdbcTemplate.queryForList(sql, idUsuario);
    }

    @Transactional
    public Map<String, Object> procesarDevolucion(int idPrestamo, String estadoLibro) {
        String sql = """
                SELECT p.id_prestamo, p.fecha_fin, p.id_usuario, p.id_copia,
                       l.nombre AS titulo,
                       COALESCE(STRING_AGG(DISTINCT a.nombre, ', ' ORDER BY a.nombre), 'Sin autor') AS autores,
                       GREATEST(0, CURRENT_DATE - p.fecha_fin) AS dias_retraso
                FROM prestamo p
                JOIN copia c ON c.id_copia = p.id_copia
                JOIN libro l ON l.id_libro = c.id_libro
                LEFT JOIN libroautor la ON la.id_libro = l.id_libro
                LEFT JOIN autor a ON a.id_autor = la.id_autor
                WHERE p.id_prestamo = ? AND c.estado = 'prestado'
                GROUP BY p.id_prestamo, p.fecha_fin, p.id_usuario, p.id_copia, l.nombre
                """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idPrestamo);
        if (rows.isEmpty()) return null;

        Map<String, Object> p = rows.get(0);
        int idCopia    = (int) p.get("id_copia");
        int idUsuario  = (int) p.get("id_usuario");
        long diasRetraso = ((Number) p.get("dias_retraso")).longValue();

        java.math.BigDecimal multaRetraso = java.math.BigDecimal.valueOf(diasRetraso * 5);
        java.math.BigDecimal multaExtra   = switch (estadoLibro) {
            case "danado"  -> java.math.BigDecimal.valueOf(100);
            case "perdido" -> java.math.BigDecimal.valueOf(500);
            default        -> java.math.BigDecimal.ZERO;
        };
        java.math.BigDecimal total = multaRetraso.add(multaExtra);

        String nuevoEstadoCopia = "normal".equals(estadoLibro) ? "disponible" : "mantenimiento";
        jdbcTemplate.update("UPDATE copia SET estado = ? WHERE id_copia = ?", nuevoEstadoCopia, idCopia);

        if (total.compareTo(java.math.BigDecimal.ZERO) > 0) {
            StringBuilder motivo = new StringBuilder();
            if (diasRetraso > 0)          motivo.append("retraso");
            if (!"normal".equals(estadoLibro)) {
                if (!motivo.isEmpty()) motivo.append(", ");
                motivo.append(estadoLibro);
            }
            jdbcTemplate.update(
                    "INSERT INTO multa (fecha, estatus, id_usuario, id_prestamo, monto, motivo) VALUES (CURRENT_DATE, 'pendiente', ?, ?, ?, ?)",
                    idUsuario, idPrestamo, total, motivo.toString());
        }

        p.put("monto_multa", total);
        p.put("estado_libro", estadoLibro);
        p.put("fecha_devolucion", LocalDate.now().toString());
        return p;
    }

    @Transactional
    public Map<String, Object> crear(int idUsuario, int idCopia, LocalDate fechaInicio, LocalDate fechaFin) {
        Integer activos = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM prestamo p " +
                "JOIN copia c ON c.id_copia = p.id_copia " +
                "WHERE p.id_usuario = ? AND c.estado = 'prestado'",
                Integer.class, idUsuario);
        if (activos != null && activos >= MAX_PRESTAMOS) {
            return java.util.Map.of("error", "limite");
        }

        List<Map<String, Object>> copias = jdbcTemplate.queryForList(
                "SELECT estado FROM copia WHERE id_copia = ? FOR UPDATE", idCopia);
        if (copias.isEmpty() || !"disponible".equals(copias.get(0).get("estado"))) {
            return null;
        }

        jdbcTemplate.update(
                "INSERT INTO prestamo (fecha_inicio, fecha_fin, id_usuario, id_copia) VALUES (?, ?, ?, ?)",
                fechaInicio, fechaFin, idUsuario, idCopia);

        jdbcTemplate.update("UPDATE copia SET estado = 'prestado' WHERE id_copia = ?", idCopia);

        String sql = """
                SELECT u.nombre || ' ' || u.apellidos AS nombre_completo,
                       u.username AS matricula,
                       l.nombre AS titulo_libro,
                       c.id_copia
                FROM copia c
                JOIN libro l ON l.id_libro = c.id_libro
                JOIN usuario u ON u.id_usuario = ?
                WHERE c.id_copia = ?
                """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idUsuario, idCopia);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
