package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/usuarios";
    }

    private Usuario getAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        return (usuario != null && "admin".equals(usuario.getTipo())) ? usuario : null;
    }

    private String cargarFormulario(HttpSession session, Model model, Usuario usuarioForm,
                                    Set<String> permisosSeleccionados, boolean modoEdicion) {
        Usuario usuario = getAdmin(session);
        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioForm", usuarioForm);
        model.addAttribute("permisosSeleccionados", permisosSeleccionados);
        model.addAttribute("modoEdicion", modoEdicion);
        return "nuevo_usuario";
    }

    @GetMapping("/usuarios")
    public String usuarios(HttpSession session, Model model,
                           @RequestParam(required = false) String q) {
        Usuario usuario = getAdmin(session);
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuarioForm(HttpSession session, Model model) {
        return cargarFormulario(session, model, new Usuario(), Collections.emptySet(), false);
    }

    @PostMapping("/usuarios/nuevo")
    public String nuevoUsuarioGuardar(HttpSession session,
                                      @RequestParam String username,
                                      @RequestParam String nombre,
                                      @RequestParam String apellidos,
                                      @RequestParam(required = false) String correo,
                                      @RequestParam String contrasena,
                                      @RequestParam String tipo,
                                      @RequestParam(value = "permisos", required = false) List<String> permisos) {
        if (getAdmin(session) == null) {
            return "redirect:/";
        }

        usuarioRepository.insert(
                username,
                contrasena,
                nombre,
                apellidos,
                tipo,
                correo,
                permisos != null ? permisos : Collections.emptyList()
        );
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuarioForm(@PathVariable int id, HttpSession session, Model model) {
        if (getAdmin(session) == null) {
            return "redirect:/";
        }

        Usuario usuarioForm = usuarioRepository.findById(id);
        if (usuarioForm == null) {
            return "redirect:/usuarios";
        }

        return cargarFormulario(
                session,
                model,
                usuarioForm,
                usuarioRepository.findPermissionNamesByUserId(id),
                true
        );
    }

    @PostMapping("/usuarios/editar/{id}")
    public String editarUsuarioGuardar(@PathVariable int id,
                                       HttpSession session,
                                       @RequestParam String username,
                                       @RequestParam String nombre,
                                       @RequestParam String apellidos,
                                       @RequestParam(required = false) String correo,
                                       @RequestParam String contrasena,
                                       @RequestParam String tipo,
                                       @RequestParam(value = "permisos", required = false) List<String> permisos) {
        Usuario admin = getAdmin(session);
        if (admin == null) {
            return "redirect:/";
        }

        if (usuarioRepository.findById(id) == null) {
            return "redirect:/usuarios";
        }

        usuarioRepository.update(
                id,
                username,
                contrasena,
                nombre,
                apellidos,
                tipo,
                correo,
                permisos != null ? permisos : Collections.emptyList()
        );

        if (admin.getId() != null && admin.getId() == id) {
            session.setAttribute("usuarioSesion", usuarioRepository.findById(id));
        }

        return "redirect:/usuarios";
    }
}
