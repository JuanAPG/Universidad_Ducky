package Universidad.Ducky.app;

import Universidad.Ducky.app.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chatbot/mensaje")
    @ResponseBody
    public ResponseEntity<Map<String, String>> mensaje(
            HttpSession session,
            @RequestParam String texto) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return ResponseEntity.status(401).build();

        String respuesta = chatbotService.responder(texto);
        return ResponseEntity.ok(Map.of("respuesta", respuesta));
    }
}
