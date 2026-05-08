package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.LibroRepository;
import Universidad.Ducky.app.repository.PrestamoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

@Controller
public class BibliotecarioController {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public BibliotecarioController(LibroRepository libroRepository,
                                   PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    private Usuario getBibliotecario(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioSesion");
        return (u != null && "bibliotecario".equals(u.getTipo())) ? u : null;
    }

    @GetMapping("/libros")
    public String dashboard(HttpSession session, Model model,
                            @RequestParam(required = false) String q) {
        Usuario usuario = getBibliotecario(session);
        if (usuario == null) return "redirect:/";

        model.addAttribute("usuario", usuario);
        model.addAttribute("q", q);
        if (q != null && !q.isBlank()) {
            model.addAttribute("libros", libroRepository.searchByTituloForAdmin(q));
        } else {
            model.addAttribute("libros", libroRepository.findAllForAdmin());
        }
        return "libros";
    }

    // ── Préstamo ─────────────────────────────────────────────────────────────

    @GetMapping("/prestamo")
    public String prestamoPage(HttpSession session, Model model) {
        Usuario usuario = getBibliotecario(session);
        if (usuario == null) return "redirect:/";
        model.addAttribute("usuario", usuario);
        return "prestamo";
    }

    @PostMapping("/prestamo/buscar-usuario")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarUsuario(
            HttpSession session,
            @RequestParam String username) {
        if (getBibliotecario(session) == null) return ResponseEntity.status(401).build();
        Map<String, Object> usuario = prestamoRepository.findUserByUsername(username);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    @GetMapping("/prestamo/buscar-libros")
    @ResponseBody
    public ResponseEntity<Object> buscarLibros(
            HttpSession session,
            @RequestParam(required = false) String q) {
        if (getBibliotecario(session) == null) return ResponseEntity.status(401).build();
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(prestamoRepository.searchCopias(q));
        }
        return ResponseEntity.ok(prestamoRepository.findAllCopias());
    }

    // ── Devolución ────────────────────────────────────────────────────────────

    @GetMapping("/devolucion")
    public String devolucionPage(HttpSession session, Model model) {
        Usuario usuario = getBibliotecario(session);
        if (usuario == null) return "redirect:/";
        model.addAttribute("usuario", usuario);
        return "devolucion";
    }

    @PostMapping("/devolucion/buscar-usuario")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarUsuarioDev(
            HttpSession session,
            @RequestParam String username) {
        if (getBibliotecario(session) == null) return ResponseEntity.status(401).build();
        Map<String, Object> usuario = prestamoRepository.findUserByUsername(username);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    @GetMapping("/devolucion/prestamos-activos")
    @ResponseBody
    public ResponseEntity<Object> prestamosActivos(
            HttpSession session,
            @RequestParam int idUsuario) {
        if (getBibliotecario(session) == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(prestamoRepository.findPrestamosActivos(idUsuario));
    }

    @PostMapping("/devolucion/procesar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> procesarDevolucion(
            HttpSession session,
            @RequestParam int idPrestamo,
            @RequestParam String estadoLibro) {
        if (getBibliotecario(session) == null) return ResponseEntity.status(401).build();
        Map<String, Object> result = prestamoRepository.procesarDevolucion(idPrestamo, estadoLibro);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
    }

    @PostMapping("/prestamo/crear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crearPrestamo(
            HttpSession session,
            @RequestParam int idUsuario,
            @RequestParam int idCopia) {
        if (getBibliotecario(session) == null) return ResponseEntity.status(401).build();
        LocalDate hoy        = LocalDate.now();
        LocalDate devolucion = sumarDiasHabiles(hoy, 5);
        Map<String, Object> comprobante = prestamoRepository.crear(idUsuario, idCopia, hoy, devolucion);
        if (comprobante == null) return ResponseEntity.badRequest().build();
        if (comprobante.containsKey("error")) return ResponseEntity.status(409).body(comprobante);
        comprobante.put("fechaInicio", hoy.toString());
        comprobante.put("fechaFin", devolucion.toString());
        return ResponseEntity.ok(comprobante);
    }

    private static LocalDate sumarDiasHabiles(LocalDate desde, int dias) {
        LocalDate fecha = desde;
        int contados = 0;
        while (contados < dias) {
            fecha = fecha.plusDays(1);
            if (fecha.getDayOfWeek() != DayOfWeek.SATURDAY &&
                fecha.getDayOfWeek() != DayOfWeek.SUNDAY) {
                contados++;
            }
        }
        return fecha;
    }
}
