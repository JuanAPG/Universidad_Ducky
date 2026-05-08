package Universidad.Ducky.app;

import Universidad.Ducky.app.repository.ChatbotRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ChatbotService {

    private final ChatbotRepository repo;

    public ChatbotService(ChatbotRepository repo) {
        this.repo = repo;
    }

    public String responder(String mensaje) {
        String texto = normalizar(mensaje);

        for (Regla regla : reglas()) {
            if (regla.keywords().stream().anyMatch(texto::contains)) {
                return regla.respuesta().get();
            }
        }
        return "No estoy seguro de cómo responder eso. 🤔 Puedo ayudarte con: "
                + "<b>préstamos</b>, <b>devoluciones</b>, <b>multas</b>, <b>estadísticas</b> o <b>cómo usar el sistema</b>.";
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String sin = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sin.replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
    }

    private List<Regla> reglas() {
        return List.of(
                new Regla(List.of("hola", "buenos dias", "buenas tardes", "buenas noches", "buen dia", "saludos"),
                        () -> "¡Hola! 👋 Soy el asistente de <b>Biblioteca Ducky</b>. ¿En qué puedo ayudarte hoy?"),

                new Regla(List.of("cuantos prestamos hay", "total de prestamos", "prestamos totales", "cuantos prestamos en total"),
                        () -> {
                            int total   = repo.countPrestamosTotal();
                            int activos = repo.countPrestamosActivos();
                            return "📊 Estadísticas de préstamos:<br>"
                                    + "• <b>Total histórico:</b> " + total + " préstamos<br>"
                                    + "• <b>Activos ahora:</b> " + activos + " préstamos";
                        }),

                new Regla(List.of("prestamos activos", "cuantos activos", "libros prestados", "en curso", "vigentes"),
                        () -> {
                            int activos = repo.countPrestamosActivos();
                            return "📚 Actualmente hay <b>" + activos + " préstamo(s) activo(s)</b> "
                                    + "(libros que aún no han sido devueltos).";
                        }),

                new Regla(List.of("cuanto dura", "duracion", "vigencia", "cuantos dias", "plazo", "tiempo del prestamo"),
                        () -> "⏱ Cada préstamo tiene una vigencia de <b>5 días hábiles</b> "
                                + "(lunes a viernes, sin contar sábados ni domingos). "
                                + "El sistema calcula la fecha límite automáticamente al registrar el préstamo."),

                new Regla(List.of("que es un prestamo", "que es prestamo", "como funciona el prestamo", "para que sirve el prestamo"),
                        () -> "📖 Un <b>préstamo</b> es la entrega temporal de un libro a un usuario registrado. "
                                + "El sistema registra quién lo tiene, cuándo lo tomó y cuándo debe devolverlo. "
                                + "Cada usuario puede tener hasta <b>3 préstamos activos</b> al mismo tiempo."),

                new Regla(List.of("prestamo", "prestar", "como presto", "registrar prestamo"),
                        () -> "📋 Para registrar un préstamo:<br>"
                                + "1. Ve a <b>Nuevo Préstamo</b> desde el panel de libros.<br>"
                                + "2. Busca al estudiante por matrícula.<br>"
                                + "3. Selecciona el libro disponible y presiona <b>Prestar</b>.<br>"
                                + "El sistema genera automáticamente el comprobante."),

                new Regla(List.of("multa por retraso", "cuanto cuesta el retraso", "precio retraso"),
                        () -> "⚠️ La multa por retraso es de <b>$5 MXN por día</b> de retraso. "
                                + "Se calcula automáticamente al procesar la devolución."),

                new Regla(List.of("multa danado", "multa dano", "libro danado", "libro en mal estado"),
                        () -> "🔧 Si el libro presenta <b>daños físicos</b>, se genera un cargo adicional de <b>$100 MXN</b> "
                                + "al momento de la devolución."),

                new Regla(List.of("multa perdido", "libro perdido", "se perdio"),
                        () -> "❌ Si el libro fue <b>reportado como perdido</b>, se aplica un cargo de <b>$500 MXN</b>. "
                                + "El usuario no podrá realizar nuevos préstamos hasta liquidar la multa."),

                new Regla(List.of("multa", "multas", "cargo", "cargos", "penalidad", "cuanto pago"),
                        () -> {
                            int pendientes = repo.countMultasPendientes();
                            return "💰 Estructura de multas:<br>"
                                    + "• Retraso: <b>$5 MXN / día</b><br>"
                                    + "• Libro dañado: <b>$100 MXN</b><br>"
                                    + "• Libro perdido: <b>$500 MXN</b><br><br>"
                                    + "Actualmente hay <b>" + pendientes + " multa(s) pendiente(s)</b> en el sistema.";
                        }),

                new Regla(List.of("como devuelvo", "devolver libro", "proceso de devolucion", "como registro devolucion"),
                        () -> "🔄 Para registrar una devolución:<br>"
                                + "1. Ve a <b>Nueva Devolución</b> desde el panel de libros.<br>"
                                + "2. Busca al estudiante por matrícula.<br>"
                                + "3. Selecciona el libro a devolver.<br>"
                                + "4. Indica el estado del libro (Normal / Dañado / Perdido).<br>"
                                + "5. Confirma — el sistema calcula multas automáticamente."),

                new Regla(List.of("devolucion", "devolver", "regresar libro"),
                        () -> "↩️ Las devoluciones se gestionan desde el botón <b>Nueva Devolución</b> "
                                + "en el panel principal. El sistema muestra los préstamos activos del usuario "
                                + "y calcula cualquier cargo pendiente."),

                new Regla(List.of("cuantos libros", "total de libros", "libros en el sistema", "catalogo"),
                        () -> {
                            int libros     = repo.countLibros();
                            int disponibles = repo.countCopiasDisponibles();
                            return "📚 Catálogo de la biblioteca:<br>"
                                    + "• <b>Títulos registrados:</b> " + libros + "<br>"
                                    + "• <b>Copias disponibles ahora:</b> " + disponibles;
                        }),

                new Regla(List.of("cuantos usuarios", "usuarios registrados", "alumnos", "estudiantes registrados"),
                        () -> {
                            int usuarios = repo.countUsuarios();
                            return "👥 Hay <b>" + usuarios + " usuario(s)</b> registrados en el sistema.";
                        }),

                new Regla(List.of("usuario", "registrar usuario", "nuevo usuario", "como agrego usuario"),
                        () -> "👤 Para registrar un usuario, el <b>administrador</b> debe ir al panel de "
                                + "<b>Usuarios → Nuevo Usuario</b> e ingresar la matrícula, nombre, correo y rol."),

                new Regla(List.of("buscar libro", "encontrar libro", "busqueda"),
                        () -> "🔍 Puedes buscar libros usando la <b>barra de búsqueda</b> en el panel de libros. "
                                + "Filtra por título o autor. Los libros disponibles aparecen marcados en verde."),

                new Regla(List.of("isbn"),
                        () -> "🔢 El <b>ISBN</b> (International Standard Book Number) es el identificador único "
                                + "de cada título de libro. Se ingresa al registrar un nuevo libro en el catálogo."),

                new Regla(List.of("horario", "hora", "cuando abre", "cuando cierra", "dias de servicio"),
                        () -> "🕐 El horario de la biblioteca es:<br>"
                                + "• <b>Lunes a viernes:</b> 8:00 – 20:00 hrs<br>"
                                + "• <b>Sábado y domingo:</b> Cerrado"),

                new Regla(List.of("estadistica", "estadisticas", "resumen", "datos del sistema"),
                        () -> {
                            int prestamosTotal  = repo.countPrestamosTotal();
                            int activos         = repo.countPrestamosActivos();
                            int multas          = repo.countMultasPendientes();
                            int libros          = repo.countLibros();
                            int disponibles     = repo.countCopiasDisponibles();
                            int usuarios        = repo.countUsuarios();
                            return "📊 <b>Resumen del sistema:</b><br>"
                                    + "• Préstamos totales: <b>" + prestamosTotal + "</b><br>"
                                    + "• Préstamos activos: <b>" + activos + "</b><br>"
                                    + "• Multas pendientes: <b>" + multas + "</b><br>"
                                    + "• Títulos en catálogo: <b>" + libros + "</b><br>"
                                    + "• Copias disponibles: <b>" + disponibles + "</b><br>"
                                    + "• Usuarios registrados: <b>" + usuarios + "</b>";
                        }),

                new Regla(List.of("ayuda", "help", "que puedes hacer", "que sabes", "opciones", "temas"),
                        () -> "🤖 Puedo responder preguntas sobre:<br>"
                                + "• <b>Préstamos</b> — qué son, cómo registrarlos, duración<br>"
                                + "• <b>Devoluciones</b> — proceso y cómo gestionarlas<br>"
                                + "• <b>Multas</b> — cargos por retraso, daño o pérdida<br>"
                                + "• <b>Estadísticas</b> — datos en tiempo real del sistema<br>"
                                + "• <b>Catálogo</b> — libros, copias disponibles<br>"
                                + "• <b>Usuarios</b> — registro y gestión<br>"
                                + "• <b>Horarios</b> — días y horas de servicio")
        );
    }

    private record Regla(List<String> keywords, Supplier<String> respuesta) {}
}
