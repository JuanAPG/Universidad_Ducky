package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import Universidad.Ducky.app.repository.LibroRepository;
import Universidad.Ducky.app.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminController(LibroRepository libroRepository, UsuarioRepository usuarioRepository) {
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getAdminOrNull(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        return (usuario != null && "admin".equals(usuario.getTipo())) ? usuario : null;
    }

    private String cargarVistaUsuarios(Model model, Usuario admin) {
        model.addAttribute("usuario", admin);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios_admin";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = getAdminOrNull(session);
        if (usuario == null) {
            return "redirect:/";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("libros", libroRepository.findAllForAdmin());
        return "dashboard";
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(HttpSession session, Model model) {
        Usuario admin = getAdminOrNull(session);
        if (admin == null) {
            return "redirect:/";
        }

        return cargarVistaUsuarios(model, admin);
    }

    @PostMapping("/admin/usuarios/validar")
    public String validarAlumno(HttpSession session, Model model, @RequestParam int idAlumno) {
        Usuario admin = getAdminOrNull(session);
        if (admin == null) {
            return "redirect:/";
        }

        Usuario alumno = usuarioRepository.findAlumnoById(idAlumno);
        if (alumno == null) {
            model.addAttribute("error", "No se encontró un alumno con ese ID.");
        } else {
            model.addAttribute("alumnoValidado", alumno);
        }
        model.addAttribute("idAlumnoIngresado", idAlumno);
        return cargarVistaUsuarios(model, admin);
    }

    @PostMapping("/admin/usuarios/crear-acceso")
    public String crearAccesoAlumno(HttpSession session,
                                    @RequestParam int idAlumno,
                                    @RequestParam String contrasena) {
        Usuario admin = getAdminOrNull(session);
        if (admin == null) {
            return "redirect:/";
        }

        Usuario alumno = usuarioRepository.findAlumnoById(idAlumno);
        if (alumno == null) {
            return "redirect:/admin/usuarios?errorAlumnoNoExiste=1";
        }
        usuarioRepository.createStudentAccess(idAlumno, contrasena);
        return "redirect:/admin/usuarios?okAccesoCreado=1";
    }

    @PostMapping("/admin/usuarios/crear-personal")
    public String crearPersonal(HttpSession session,
                                @RequestParam String username,
                                @RequestParam String nombre,
                                @RequestParam String apellidos,
                                @RequestParam String contrasena,
                                @RequestParam String tipo,
                                @RequestParam(required = false) String direccion) {
        Usuario admin = getAdminOrNull(session);
        if (admin == null) {
            return "redirect:/";
        }

        if (!"admin".equals(tipo) && !"bibliotecario".equals(tipo)) {
            return "redirect:/admin/usuarios?errorTipoPersonal=1";
        }

        boolean created = usuarioRepository.createStaffUser(
                username, contrasena, nombre, apellidos, tipo, direccion
        );
        return created
                ? "redirect:/admin/usuarios?okPersonalCreado=1"
                : "redirect:/admin/usuarios?errorCrearPersonal=1";
    }

    @PostMapping("/admin/usuarios/cambiar-contrasena")
    public String cambiarContrasena(HttpSession session,
                                    @RequestParam int idUsuario,
                                    @RequestParam String nuevaContrasena) {
        Usuario admin = getAdminOrNull(session);
        if (admin == null) {
            return "redirect:/";
        }

        int updated = usuarioRepository.updatePassword(idUsuario, nuevaContrasena);
        return updated > 0
                ? "redirect:/admin/usuarios?okContrasenaActualizada=1"
                : "redirect:/admin/usuarios?errorActualizarContrasena=1";
    }

    @PostMapping("/admin/usuarios/dar-baja")
    public String darDeBaja(HttpSession session, @RequestParam int idUsuario) {
        Usuario admin = getAdminOrNull(session);
        if (admin == null) {
            return "redirect:/";
        }

        if (admin.getId() != null && admin.getId() == idUsuario) {
            return "redirect:/admin/usuarios?errorAutoBaja=1";
        }

        if (usuarioRepository.hasActiveLoan(idUsuario)) {
            return "redirect:/admin/usuarios?errorPrestamoActivo=1";
        }

        boolean deleted = usuarioRepository.deleteById(idUsuario);
        return deleted
                ? "redirect:/admin/usuarios?okBaja=1"
                : "redirect:/admin/usuarios?errorBaja=1";
    }
}
