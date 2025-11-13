package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Alquiler {
    private int IDAlquiler;
    private LocalDate FechaDeInicio;
    private LocalTime HoraDeInicio;
    private int Duracion; // en horas
    private List<DetalleAlquiler> detalles;
    
    public Alquiler() {
        this.detalles = new ArrayList<>();
        this.FechaDeInicio = LocalDate.now();
        this.HoraDeInicio = LocalTime.now();
    }
    
    public Alquiler(int IDAlquiler, LocalDate FechaDeInicio, LocalTime HoraDeInicio, int Duracion) {
        this.IDAlquiler = IDAlquiler;
        this.FechaDeInicio = FechaDeInicio;
        this.HoraDeInicio = HoraDeInicio;
        this.Duracion = Duracion;
        this.detalles = new ArrayList<>();
    }
    
    // Getters y Setters
    public int getIDAlquiler() { return IDAlquiler; }
    public void setIDAlquiler(int IDAlquiler) { this.IDAlquiler = IDAlquiler; }
    
    public LocalDate getFechaDeInicio() { return FechaDeInicio; }
    public void setFechaDeInicio(LocalDate FechaDeInicio) { this.FechaDeInicio = FechaDeInicio; }
    
    public LocalTime getHoraDeInicio() { return HoraDeInicio; }
    public void setHoraDeInicio(LocalTime HoraDeInicio) { this.HoraDeInicio = HoraDeInicio; }
    
    public int getDuracion() { return Duracion; }
    public void setDuracion(int Duracion) { this.Duracion = Duracion; }
    
    public List<DetalleAlquiler> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleAlquiler> detalles) { this.detalles = detalles; }
    
    // Métodos de negocio
    public void agregarDetalle(DetalleAlquiler detalle) {
        this.detalles.add(detalle);
    }
    
    public void removerDetalle(DetalleAlquiler detalle) {
        this.detalles.remove(detalle);  
    }
    
    public double calcularTotal() {
        double total = 0;
        for (DetalleAlquiler detalle : detalles) {
            if (detalle.getRecursoObj() != null) {
                double subtotal = detalle.getRecursoObj().getTarifaPorHora() * Duracion;
                
                // Aplicar descuento si existe
                if (detalle.getPromocionObj() != null) {
                    subtotal = detalle.getPromocionObj().aplicarDescuento(subtotal);
                }
                
                total += subtotal;
            }
        }
        return total;
    }
    
    public int getCantidadItems() {
        return detalles.size();
    }
    
    public LocalTime getHoraFin() {
        return HoraDeInicio.plusHours(Duracion);
    }
    
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return FechaDeInicio.format(formatter);
    }
    
    public String getHoraInicioFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return HoraDeInicio.format(formatter);
    }
    
    @Override
    public String toString() {
        return "Alquiler{" +
                "IDAlquiler=" + IDAlquiler +
                ", FechaDeInicio=" + getFechaFormateada() +
                ", HoraDeInicio=" + getHoraInicioFormateada() +
                ", Duracion=" + Duracion + " horas" +
                ", Items=" + getCantidadItems() +
                ", Total=" + String.format("$%.2f", calcularTotal()) +
                '}';
    }
}

