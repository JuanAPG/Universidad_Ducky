package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.LibroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final LibroRepository libroRepository;

    public AdminController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null || !"admin".equals(usuario.getTipo())) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("libros", libroRepository.findAllForAdmin());
        return "dashboard";
    }
}
