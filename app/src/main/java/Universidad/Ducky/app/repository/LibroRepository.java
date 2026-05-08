package Universidad.Ducky.app.repository;

import Universidad.Ducky.app.model.LibroAdmin;
import Universidad.Ducky.app.model.LibroCard;
import Universidad.Ducky.app.model.LibroDetalle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LibroRepository {

    private final JdbcTemplate jdbcTemplate;

    public LibroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LibroAdmin> findAllForAdmin() {
        String sql = """
                SELECT l.id_libro,
                       l.nombre,
                       l.anio,
                       l.tipo,
                       COALESCE(string_agg(DISTINCT a.nombre, ', '), 'Sin autor') AS autores,
                       COUNT(DISTINCT c.id_copia) AS num_copias
                FROM libro l
                LEFT JOIN libroautor la ON l.id_libro = la.id_libro
                LEFT JOIN autor a ON la.id_autor = a.id_autor
                LEFT JOIN copia c ON c.id_libro = l.id_libro
                GROUP BY l.id_libro, l.nombre, l.anio, l.tipo
                ORDER BY l.id_libro
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LibroAdmin(
                rs.getInt("id_libro"),
                rs.getString("nombre"),
                rs.getString("autores"),
                rs.getInt("anio"),
                rs.getString("tipo"),
                rs.getInt("num_copias")
        ));
    }

    public LibroDetalle findDetalleById(int id) {
        String sql = """
                SELECT id_libro, nombre, tipo, anio, id_editorial,
                       isbn, sinopsis, num_paginas, precio, num_copias
                FROM libro WHERE id_libro = ?
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new LibroDetalle(
                rs.getInt("id_libro"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getInt("anio"),
                rs.getInt("id_editorial"),
                rs.getString("isbn"),
                rs.getString("sinopsis"),
                rs.getObject("num_paginas", Integer.class),
                rs.getBigDecimal("precio"),
                rs.getInt("num_copias")
        ), id);
    }

    public List<Integer> findAutorIdsByLibroId(int libroId) {
        return jdbcTemplate.queryForList(
                "SELECT id_autor FROM libroautor WHERE id_libro = ?", Integer.class, libroId);
    }

    public void insert(String nombre, String tipo, Integer anio, int idEditorial,
                       String isbn, String sinopsis, Integer numPaginas,
                       java.math.BigDecimal precio, int numCopias,
                       String ubicacionBiblioteca, List<Integer> autorIds) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO libro (nombre, tipo, anio, id_editorial, isbn, sinopsis, num_paginas, precio, num_copias) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"id_libro"});
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            if (anio != null) ps.setInt(3, anio); else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setInt(4, idEditorial);
            ps.setString(5, isbn);
            ps.setString(6, sinopsis);
            if (numPaginas != null) ps.setInt(7, numPaginas); else ps.setNull(7, java.sql.Types.INTEGER);
            if (precio != null) ps.setBigDecimal(8, precio); else ps.setNull(8, java.sql.Types.NUMERIC);
            ps.setInt(9, numCopias);
            return ps;
        }, keyHolder);

        int newId = keyHolder.getKey().intValue();

        for (Integer autorId : autorIds) {
            jdbcTemplate.update("INSERT INTO libroautor (id_libro, id_autor) VALUES (?, ?)", newId, autorId);
        }
        for (int i = 0; i < numCopias; i++) {
            jdbcTemplate.update(
                    "INSERT INTO copia (estado, ubicacion_biblioteca, id_libro) VALUES ('disponible', ?, ?)",
                    ubicacionBiblioteca, newId);
        }
    }

    public void update(int id, String nombre, String tipo, Integer anio, int idEditorial,
                       String isbn, String sinopsis, Integer numPaginas,
                       java.math.BigDecimal precio, List<Integer> autorIds) {
        jdbcTemplate.update(
                "UPDATE libro SET nombre=?, tipo=?, anio=?, id_editorial=?, isbn=?, sinopsis=?, num_paginas=?, precio=? WHERE id_libro=?",
                nombre, tipo, anio, idEditorial, isbn, sinopsis, numPaginas, precio, id);

        jdbcTemplate.update("DELETE FROM libroautor WHERE id_libro=?", id);
        for (Integer autorId : autorIds) {
            jdbcTemplate.update("INSERT INTO libroautor (id_libro, id_autor) VALUES (?, ?)", id, autorId);
        }
    }

    public List<LibroAdmin> searchByTituloForAdmin(String titulo) {
        String sql = """
                SELECT l.id_libro,
                       l.nombre,
                       l.anio,
                       l.tipo,
                       COALESCE(string_agg(DISTINCT a.nombre, ', '), 'Sin autor') AS autores,
                       COUNT(DISTINCT c.id_copia) AS num_copias
                FROM libro l
                LEFT JOIN libroautor la ON l.id_libro = la.id_libro
                LEFT JOIN autor a ON la.id_autor = a.id_autor
                LEFT JOIN copia c ON c.id_libro = l.id_libro
                WHERE LOWER(l.nombre) LIKE LOWER(CONCAT('%', ?, '%'))
                GROUP BY l.id_libro, l.nombre, l.anio, l.tipo
                ORDER BY l.id_libro
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LibroAdmin(
                rs.getInt("id_libro"),
                rs.getString("nombre"),
                rs.getString("autores"),
                rs.getInt("anio"),
                rs.getString("tipo"),
                rs.getInt("num_copias")
        ), titulo);
    }

    public List<LibroCard> searchByTituloForCards(String titulo) {
        String sql = """
                SELECT l.id_libro,
                       l.nombre,
                       l.tipo,
                       (SELECT a.nombre
                        FROM libroautor la
                        JOIN autor a ON la.id_autor = a.id_autor
                        WHERE la.id_libro = l.id_libro
                        LIMIT 1) AS autor_nombre,
                       (SELECT c.estado
                        FROM copia c
                        WHERE c.id_libro = l.id_libro
                        LIMIT 1) AS estado
                FROM libro l
                WHERE LOWER(l.nombre) LIKE LOWER(CONCAT('%', ?, '%'))
                ORDER BY l.nombre
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LibroCard(
                rs.getInt("id_libro"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getString("autor_nombre"),
                rs.getString("estado")
        ), titulo);
    }

    public java.util.Map<String, Object> findDetalleParaUsuario(int id) {
        String sql = """
                SELECT l.id_libro, l.nombre, l.tipo, l.anio, l.isbn,
                       l.sinopsis, l.num_paginas, l.precio,
                       e.nombre AS editorial,
                       COALESCE(STRING_AGG(DISTINCT a.nombre, ', ' ORDER BY a.nombre), 'Sin autor') AS autores
                FROM libro l
                JOIN editorial e ON e.id_editorial = l.id_editorial
                LEFT JOIN libroautor la ON la.id_libro = l.id_libro
                LEFT JOIN autor a ON a.id_autor = la.id_autor
                WHERE l.id_libro = ?
                GROUP BY l.id_libro, l.nombre, l.tipo, l.anio, l.isbn,
                         l.sinopsis, l.num_paginas, l.precio, e.nombre
                """;
        java.util.List<java.util.Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public java.util.List<java.util.Map<String, Object>> findCopiasByLibroId(int id) {
        return jdbcTemplate.queryForList(
                "SELECT id_copia, estado, COALESCE(ubicacion_biblioteca, 'No especificada') AS ubicacion_biblioteca " +
                "FROM copia WHERE id_libro = ? ORDER BY id_copia", id);
    }

    public List<LibroCard> findRandom(int cantidad) {
        String sql = """
                SELECT l.id_libro,
                       l.nombre,
                       l.tipo,
                       (SELECT a.nombre
                        FROM libroautor la
                        JOIN autor a ON la.id_autor = a.id_autor
                        WHERE la.id_libro = l.id_libro
                        LIMIT 1) AS autor_nombre,
                       (SELECT c.estado
                        FROM copia c
                        WHERE c.id_libro = l.id_libro
                        LIMIT 1) AS estado
                FROM libro l
                ORDER BY RANDOM()
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LibroCard(
                rs.getInt("id_libro"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getString("autor_nombre"),
                rs.getString("estado")
        ), cantidad);
    }
}
