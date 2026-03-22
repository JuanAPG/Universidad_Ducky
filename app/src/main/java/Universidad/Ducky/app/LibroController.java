package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.AutorRepository;
import Universidad.Ducky.app.repository.EditorialRepository;
import Universidad.Ducky.app.repository.LibroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/libros")
public class LibroController {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final EditorialRepository editorialRepository;

    private static final List<String> TIPOS = List.of(
            "Administracion", "Arquitectura", "Arte", "Ciencia", "Ciencias sociales",
            "Comunicacion", "Derecho", "Diseno", "Economia", "Educacion",
            "Ensayo", "Historia", "Ingenieria", "Literatura", "Matematicas",
            "Metodologia", "Negocios", "Novela historica", "Psicologia",
            "Salud", "Tecnologia");

    public LibroController(LibroRepository libroRepository,
                           AutorRepository autorRepository,
                           EditorialRepository editorialRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.editorialRepository = editorialRepository;
    }

    private Usuario getAdminOrRedirect(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioSesion");
        return (u != null && "admin".equals(u.getTipo())) ? u : null;
    }

    // ── Nuevo libro ──────────────────────────────────────────────────────────

    @GetMapping("/nuevo")
    public String nuevoForm(HttpSession session, Model model) {
        Usuario usuario = getAdminOrRedirect(session);
        if (usuario == null) return "redirect:/";

        model.addAttribute("usuario", usuario);
        model.addAttribute("autores", autorRepository.findAll());
        model.addAttribute("editoriales", editorialRepository.findAll());
        model.addAttribute("tipos", TIPOS);
        return "nuevo_libro";
    }

    @PostMapping("/nuevo")
    public String nuevoGuardar(HttpSession session,
                               @RequestParam String nombre,
                               @RequestParam String tipo,
                               @RequestParam(required = false) Integer anio,
                               @RequestParam int idEditorial,
                               @RequestParam(required = false) String isbn,
                               @RequestParam(required = false) String sinopsis,
                               @RequestParam(required = false) Integer numPaginas,
                               @RequestParam(required = false) BigDecimal precio,
                               @RequestParam int numCopias,
                               @RequestParam(required = false) String ubicacionBiblioteca,
                               @RequestParam(value = "autorIds") List<Integer> autorIds) {
        if (getAdminOrRedirect(session) == null) return "redirect:/";

        libroRepository.insert(nombre, tipo, anio, idEditorial,
                isbn, sinopsis, numPaginas, precio, numCopias, ubicacionBiblioteca, autorIds);
        return "redirect:/dashboard";
    }

    // ── Editar libro ─────────────────────────────────────────────────────────

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, HttpSession session, Model model) {
        Usuario usuario = getAdminOrRedirect(session);
        if (usuario == null) return "redirect:/";

        model.addAttribute("usuario", usuario);
        model.addAttribute("libro", libroRepository.findDetalleById(id));
        model.addAttribute("autorIdsActuales", libroRepository.findAutorIdsByLibroId(id));
        model.addAttribute("autores", autorRepository.findAll());
        model.addAttribute("editoriales", editorialRepository.findAll());
        model.addAttribute("tipos", TIPOS);
        return "editar_libro";
    }

    @PostMapping("/editar/{id}")
    public String editarGuardar(@PathVariable int id,
                                HttpSession session,
                                @RequestParam String nombre,
                                @RequestParam String tipo,
                                @RequestParam(required = false) Integer anio,
                                @RequestParam int idEditorial,
                                @RequestParam(required = false) String isbn,
                                @RequestParam(required = false) String sinopsis,
                                @RequestParam(required = false) Integer numPaginas,
                                @RequestParam(required = false) BigDecimal precio,
                                @RequestParam(value = "autorIds") List<Integer> autorIds) {
        if (getAdminOrRedirect(session) == null) return "redirect:/";

        libroRepository.update(id, nombre, tipo, anio, idEditorial,
                isbn, sinopsis, numPaginas, precio, autorIds);
        return "redirect:/dashboard";
    }
}
