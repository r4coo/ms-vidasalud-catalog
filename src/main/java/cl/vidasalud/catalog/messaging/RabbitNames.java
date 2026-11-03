package cl.vidasalud.catalog.messaging;

/** Nombres centralizados de exchanges, colas, routing keys y argumentos de retención. */
public final class RabbitNames {
    private RabbitNames() {}

    // Exchanges
    public static final String EX_ATENCIONES = "vidasalud.atenciones.direct"; // direct: comandos
    public static final String EX_EVENTOS = "vidasalud.eventos.topic";        // topic: eventos de dominio
    public static final String EX_DLX = "vidasalud.dlx.direct";               // direct: dead-letter

    // Colas funcionales
    public static final String Q_CONFIRMAR = "q.atenciones.confirmar";
    public static final String Q_NOTIFICACIONES = "q.notificaciones";
    public static final String Q_AUDITORIA = "q.auditoria";

    // Colas DLQ
    public static final String DLQ_CONFIRMAR = Q_CONFIRMAR + ".dlq";
    public static final String DLQ_NOTIFICACIONES = Q_NOTIFICACIONES + ".dlq";
    public static final String DLQ_AUDITORIA = Q_AUDITORIA + ".dlq";

    // Routing keys
    public static final String RK_ATENCION_CONFIRMAR = "atencion.confirmar";  // comando (direct)
    public static final String RK_ATENCION_CONFIRMADA = "atencion.confirmada"; // evento (topic)
    public static final String RK_ATENCION_RECHAZADA = "atencion.rechazada";   // evento (topic)
    public static final String RK_ATENCION_ESTADO = "atencion.estado";         // evento (topic)
    public static final String BIND_NOTIFICACIONES = "atencion.*";             // topic
    public static final String BIND_AUDITORIA = "#";                           // topic: todo

    // Retención y limpieza
    public static final int QUEUE_TTL_MS = 5 * 60 * 1000;            // 5 min en cola principal -> DLQ
    public static final long QUEUE_MAX_LENGTH = 10_000;
    public static final int DLQ_TTL_MS = 7 * 24 * 60 * 60 * 1000;    // 7 días en la DLQ
    public static final long DLQ_MAX_LENGTH = 5_000;
}
