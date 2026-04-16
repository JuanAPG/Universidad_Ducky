package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.LibroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BibliotecarioController {

    private final LibroRepository libroRepository;

    public BibliotecarioController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping("/libros")
    public String dashboard(HttpSession session, Model model,
                            @RequestParam(required = false) String q) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null || !"bibliotecario".equals(usuario.getTipo())) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("q", q);
        if (q != null && !q.isBlank()) {
            model.addAttribute("libros", libroRepository.searchByTituloForAdmin(q));
        } else {
            model.addAttribute("libros", libroRepository.findAllForAdmin());
        }
        return "libros";
    }
}
