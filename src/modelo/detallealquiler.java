package modelo;

public class DetalleAlquiler {
    private String IDDetalleAlquiler;
    private String IDRecurso;
    private String IDTurista;
    private String IDAlquiler;
    private String IDPromocion; 
    private String FormatodePago;
    
    private Recurso recursoObj;
    private Turista turistaObj;
    private Promocion promocionObj;
    
    public DetalleAlquiler() {}
    
    public DetalleAlquiler(String IDDetalleAlquiler, String IDRecurso, String IDTurista, 
                          String IDAlquiler, String IDPromocion, String FormatodePago) {
        this.IDDetalleAlquiler = IDDetalleAlquiler;
        this.IDRecurso = IDRecurso;
        this.IDTurista = IDTurista;
        this.IDAlquiler = IDAlquiler;
        this.IDPromocion = IDPromocion;
        this.FormatodePago = FormatodePago;
    }
    
    // Getters y Setters
    public String getIDDetalleAlquiler() { return IDDetalleAlquiler; }
    public void setIDDetalleAlquiler(String IDDetalleAlquiler) { 
        this.IDDetalleAlquiler = IDDetalleAlquiler; 
    }
    
    public String getIDRecurso() { return IDRecurso; }
    public void setIDRecurso(String IDRecurso) { this.IDRecurso = IDRecurso; }
    
    public String getIDTurista() { return IDTurista; }
    public void setIDTurista(String IDTurista) { this.IDTurista = IDTurista; }
    
    public String getIDAlquiler() { return IDAlquiler; }
    public void setIDAlquiler(String IDAlquiler) { this.IDAlquiler = IDAlquiler; }
    
    public String getIDPromocion() { return IDPromocion; }
    public void setIDPromocion(String IDPromocion) { this.IDPromocion = IDPromocion; }
      public String getFormatodePago() {
        return FormatodePago;
    }

    public void setFormatodePago(String formatodePago) {
        FormatodePago = formatodePago;
    }
    
    // Objetos relacionados
    public Recurso getRecursoObj() { return recursoObj; }
    public void setRecursoObj(Recurso recursoObj) { 
        this.recursoObj = recursoObj;
        if (recursoObj != null) {
            this.IDRecurso = recursoObj.getIDRecursos();
        }
    }
    
    public Turista getTuristaObj() { return turistaObj; }
    public void setTuristaObj(Turista turistaObj) { 
        this.turistaObj = turistaObj;
        if (turistaObj != null) {
            this.IDTurista = turistaObj.getIDTurista();
        }
    }
    
    public Promocion getPromocionObj() { return promocionObj; }
    public void setPromocionObj(Promocion promocionObj) { 
        this.promocionObj = promocionObj;
        this.IDPromocion = promocionObj != null ? promocionObj.getIDPromocion() : null;
    }
    
    // Métodos de negocio
    public double calcularSubtotal(int duracionHoras) {
        if (recursoObj != null) {
            return recursoObj.getTarifaPorHora() * duracionHoras;
        }
        return 0.0;
    }
    
    public double calcularDescuento(int duracionHoras) {
        if (promocionObj != null && recursoObj != null) {
            double subtotal = calcularSubtotal(duracionHoras);
            return promocionObj.calcularDescuento(subtotal);
        }
        return 0.0;
    }
    
    public double calcularTotal(int duracionHoras) {
        double subtotal = calcularSubtotal(duracionHoras);
        double descuento = calcularDescuento(duracionHoras);
        return subtotal - descuento;
    }
    
    public boolean tienePromocion() {
        return promocionObj != null && IDPromocion != null;
    }
    
    public String obtenerNombreRecurso() {
        return recursoObj != null ? recursoObj.getRecurso() : "N/A";
    }
    
    public String obtenerNombreTurista() {
        return turistaObj != null ? turistaObj.getNombreCompleto() : "N/A";
    }
    
    
}
