package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Login {

    private final UsuarioRepository usuarioRepository;

    public Login(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Usuario usuario = usuarioRepository.findByUsernameAndPassword(username, password);
        if (usuario == null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
        session.setAttribute("usuarioSesion", usuario);

        return switch (usuario.getTipo()) {
            case "usuario" -> "redirect:/home";
            case "bibliotecario" -> "redirect:/bibliotecario";
            case "admin" -> "redirect:/dashboard";
            default -> "redirect:/";
        };
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
