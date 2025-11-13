package modelo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class alquiler {

    private int idAlquiler;
    private LocalDate fecha;
    private LocalTime horaDeInicio;
    private int duracion; // en horas
    private List<detallealquiler> detalles;
    
    public int getIdAlquiler() {
        return idAlquiler;
    }

    public void setIdAlquiler(int idAlquiler) {
        this.idAlquiler = idAlquiler;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraDeInicio() {
        return horaDeInicio;
    }

    public void setHoraDeInicio(LocalTime horaDeInicio) {
        this.horaDeInicio = horaDeInicio;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public List<detallealquiler> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<detallealquiler> detalles) {
        this.detalles = detalles;
    }

    public alquiler() {
        this.detalles = new ArrayList<>();
        this.fecha = LocalDate.now();
        this.horaDeInicio = LocalTime.now();
    }
    
    public alquiler(int idAlquiler, LocalDate fecha, LocalTime horaDeInicio, int duracion) {
        this.idAlquiler = idAlquiler;
        this.fecha = fecha;
        this.horaDeInicio = horaDeInicio;
        this.duracion = duracion;
        this.detalles = new ArrayList<>();
    }
    public void agregarDetalle(detallealquiler detalle) {
        this.detalles.add(detalle);
    }
    
    public void removerDetalle(detallealquiler detalle) {
        this.detalles.remove(detalle);
    }
    
    public double calcularSubtotal() {
        double subtotal = 0;
        for (detallealquiler detalle : detalles) {
            subtotal += detalle.calcularSubtotal();
        }
        return subtotal;
    }
    
    public double calcularTotalConDescuentos() {
        double total = 0;
        for (detallealquiler detalle : detalles) {
            total += detalle.calcularTotalConDescuento();
        }
        return total;
    }
    
    public int getCantidadItems() {
        return detalles.size();
    }
    
    public LocalTime getHoraFin() {
        return horaDeInicio.plusHours(duracion);
    }
    
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }
    
    public String getHoraInicioFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return horaDeInicio.format(formatter);
    }
    
    @Override
    public String toString() {
        return "Alquiler{" +
                "idAlquiler=" + idAlquiler +
                ", fecha=" + getFechaFormateada() +
                ", horaDeInicio=" + getHoraInicioFormateada() +
                ", duracion=" + duracion + " horas" +
                ", items=" + getCantidadItems() +
                ", total=" + String.format("$%.2f", calcularTotalConDescuentos()) +
                '}';
    }
}
    

