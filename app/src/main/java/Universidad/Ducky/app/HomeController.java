package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.LibroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final LibroRepository libroRepository;

    public HomeController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model,
                       @RequestParam(required = false) String q) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null || !"usuario".equals(usuario.getTipo())) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("q", q);
        if (q != null && !q.isBlank()) {
            model.addAttribute("libros", libroRepository.searchByTituloForCards(q));
        } else {
            model.addAttribute("libros", libroRepository.findRandom(6));
        }
        return "home";
    }

    @GetMapping("/home/libro/{id}")
    public String detalleLibro(@PathVariable int id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null || !"usuario".equals(usuario.getTipo())) {
            return "redirect:/";
        }
        java.util.Map<String, Object> libro = libroRepository.findDetalleParaUsuario(id);
        if (libro == null) return "redirect:/home";

        var copias = libroRepository.findCopiasByLibroId(id);
        boolean hayDisponible = copias.stream()
                .anyMatch(c -> "disponible".equals(c.get("estado")));

        model.addAttribute("usuario", usuario);
        model.addAttribute("libro", libro);
        model.addAttribute("copias", copias);
        model.addAttribute("hayDisponible", hayDisponible);
        return "detalle_libro";
    }
}
