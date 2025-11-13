package modelo;

public class detallealquiler {
    private int idDetalleDeAlquiler;
    private int idAlquiler;
    private int idTurista;
    private int idRecurso;
    private Integer idPromocion; // Puede ser null
    private String formatoDePago; // efectivo, tarjeta_credito, tarjeta_debito, transferencia
    
    // Relaciones
    private recursos recurso;
    private turista turista;
    private promocion promocion;
    
    // Atributos de cálculo
    private int cantidad;
    private double precioUnitario;
    
    public detallealquiler() {}
    
    public detallealquiler(int idDetalleDeAlquiler, int idAlquiler, int idTurista, 
                            int idRecurso, int cantidad, double precioUnitario) {
        this.idDetalleDeAlquiler = idDetalleDeAlquiler;
        this.idAlquiler = idAlquiler;
        this.idTurista = idTurista;
        this.idRecurso = idRecurso;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.formatoDePago = "efectivo";
    }
    
    // Getters y Setters
    public int getIdDetalleDeAlquiler() { return idDetalleDeAlquiler; }
    public void setIdDetalleDeAlquiler(int idDetalleDeAlquiler) { 
        this.idDetalleDeAlquiler = idDetalleDeAlquiler; 
    }
    
    public int getIdAlquiler() { return idAlquiler; }
    public void setIdAlquiler(int idAlquiler) { this.idAlquiler = idAlquiler; }
    
    public int getIdTurista() { return idTurista; }
    public void setIdTurista(int idTurista) { this.idTurista = idTurista; }
    
    public int getIdRecurso() { return idRecurso; }
    public void setIdRecurso(int idRecurso) { this.idRecurso = idRecurso; }
    
    public Integer getIdPromocion() { return idPromocion; }
    public void setIdPromocion(Integer idPromocion) { this.idPromocion = idPromocion; }
    
    public String getFormatoDePago() { return formatoDePago; }
    public void setFormatoDePago(String formatoDePago) { this.formatoDePago = formatoDePago; }
    
    public recursos getRecurso() { return recurso; }
    public void setRecurso(recursos recurso) { 
        this.recurso = recurso;
        this.idRecurso = recurso.getIdRecurso();
        this.precioUnitario = recurso.getTarifaPorHora();
    }
    
    public turista getTurista() { return turista; }
    public void setTurista(turista turista) { 
        this.turista = turista;
        this.idTurista = turista.getIdturista();
    }
    
    public promocion getPromocion() { return promocion; }
    public void setPromocion(promocion promocion) { 
        this.promocion = promocion;
        this.idPromocion = promocion != null ? promocion.getIdPromocion() : null;
    }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    
    // Métodos de negocio
    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }
    
    public double calcularDescuento() {
        if (promocion != null) {
            return promocion.calcularDescuento(calcularSubtotal());
        }
        return 0.0;
    }
    
    public double calcularTotalConDescuento() {
        double subtotal = calcularSubtotal();
        double descuento = calcularDescuento();
        return subtotal - descuento;
    }
    
    public boolean tienePromocion() {
        return promocion != null && idPromocion != null;
    }
    
    public String obtenerNombreRecurso() {
        return recurso != null ? recurso.getTipoRecurso() : "N/A";
    }
    
    public String obtenerNombreTurista() {
        return turista != null ? turista.getNombreCompleto() : "N/A";
    }
    
    @Override
    public String toString() {
        return "DetalleDeAlquiler{" +
                "idDetalle=" + idDetalleDeAlquiler +
                ", recurso='" + obtenerNombreRecurso() + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnit=" + String.format("$%.2f", precioUnitario) +
                ", subtotal=" + String.format("$%.2f", calcularSubtotal()) +
                ", descuento=" + String.format("$%.2f", calcularDescuento()) +
                ", total=" + String.format("$%.2f", calcularTotalConDescuento()) +
                ", pago='" + formatoDePago + '\'' +
                '}';
    }
}
