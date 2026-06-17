package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.controllers.ContadoDetalleController;
import lat.mediteam.controllers.PagoController;
import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;
import lat.mediteam.enums.MetodoPago;
import lat.mediteam.enums.PagoEstado;
import lat.mediteam.enums.PagoTipo;
import lat.mediteam.services.ContadoDetalleService;
import lat.mediteam.services.PagoService;
import lat.mediteam.exceptions.InvalidArgumentException;

public class PagoCommands implements Command {
    AppContext ctx;
    Session session;

    public CommandResponse execute(AppContext ctx, Session session, List<String> args) {
        if (args.isEmpty()) {
            throw new InvalidArgumentException("No se proporcionó ningún subcomando para 'Pago'");
        }

        this.ctx = ctx;
        this.session = session;

        String subCommand = args.remove(0).toLowerCase();

        switch (subCommand) {
            case "crear":
                return crear(args);
            case "obtener":
                return obtener(args);
            case "listar":
                return listar(args);
            case "pagar":
                return pagar(args);
            default:
                throw new InvalidArgumentException("Subcomando de Pago inválido: " + subCommand);
        }
    }

    public String getHelp() {
        return "Comandos de Pago:\n" +
               "  Pago crear <idSecretaria:numero> <idServicio:numero> <total:numero decimal> <estado:{pagado | pendiente} <tipo:{contado | cuotas} - Crea un nuevo Pago\n" +
               "  Pago obtener <id> - Obtiene los detalles de un Pago por ID\n" +
               "  Pago listar - Lista todos los Pagos\n"+
               "  Pago pagar <pagoId> <metodoPago:{efectivo | qr}> <montoRecibido> - Registra el detalle de pago (fecha = hoy) - MontoRecibido será sobreescrito por la llegada de QR si usa el metodo";
    }

        private CommandResponse crear(List<String> args) {
        if (args.size() < 5 || args.size() > 6) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'crear'. Se requieren entre 5 y 6 argumentos: " +
                "<secretariaId> <servicioId> <fechaCreacion> <total> <tipo> [estado]");
        }
 
        Long secretariaId = parseId(args.get(0), "secretaria");
        Long servicioId = parseId(args.get(1), "servicio");
        String fechaCreacion = args.get(2);
        Float total = parseTotal(args.get(3));
        PagoTipo tipo = parseTipo(args.get(4));
        PagoEstado estado = args.size() > 5 ? parseEstado(args.get(5)) : PagoEstado.PENDIENTE;
 
        PagoController controller = new PagoController(ctx, session, new PagoService());
        return controller.crearPago(secretariaId, servicioId, fechaCreacion, total, estado, tipo);
    }
 
    // pago obtener <id>
    private CommandResponse obtener(List<String> args) {
        if (args.size() != 1) {
            throw new InvalidArgumentException("Argumentos erróneos para 'obtener'. Se requiere 1 argumento: <id>");
        }
 
        Long id = parseId(args.get(0), "pago");
 
        PagoController controller = new PagoController(ctx, session, new PagoService());
        return controller.obtenerPago(id);
    }
 
    // pago listar
    private CommandResponse listar(List<String> args) {
        if (!args.isEmpty()) {
            throw new InvalidArgumentException("Argumentos erróneos para 'listar'. No se requieren argumentos.");
        }
 
        PagoController controller = new PagoController(ctx, session, new PagoService());
        return controller.listarPagos();
    }

    // pago pagar <pagoId> <metodoPago> <montoRecibido>
    private CommandResponse pagar(List<String> args) {
        if (args.size() != 3) {
            throw new InvalidArgumentException(
                "Argumentos erróneos para 'registrar'. Se requieren 3 argumentos: " +
                "<pagoId> <metodoPago> <montoRecibido>");
        }
 
        Long pagoId = parseId(args.get(0), "pago");
        MetodoPago metodoPago = parseMetodoPago(args.get(1));
        Float montoRecibido = parseMonto(args.get(2));
 
        ContadoDetalleController controller = new ContadoDetalleController(ctx, session, new ContadoDetalleService());
        return controller.registrarDetalle(pagoId, metodoPago, montoRecibido);
    }

 
    // validaciones
    private Long parseId(String idStr, String entidad) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("ID de " + entidad + " inválido: " + idStr);
        }
    }
 
    private Float parseTotal(String totalStr) {
        try {
            return Float.parseFloat(totalStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Total inválido: " + totalStr);
        }
    }
 
    private PagoTipo parseTipo(String tipoStr) {
        try {
            return PagoTipo.valueOf(tipoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Tipo de pago inválido: " + tipoStr);
        }
    }
 
    private PagoEstado parseEstado(String estadoStr) {
        try {
            return PagoEstado.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Estado de pago inválido: " + estadoStr);
        }
    }

    private Float parseMonto(String montoStr) {
        try {
            return Float.parseFloat(montoStr);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Monto recibido inválido: " + montoStr);
        }
    }
 
    private MetodoPago parseMetodoPago(String metodoStr) {
        try {
            return MetodoPago.valueOf(metodoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Método de pago inválido: " + metodoStr);
        }
    }

}