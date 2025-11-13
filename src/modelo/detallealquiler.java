package modelo;

public class DetalleAlquiler {
    private int IDDetalleAlquiler;
    private int IDRecurso;
    private int IDTurista;
    private int IDAlquiler;
    private Integer IDPromocion; // Puede ser null
    
    // Objetos relacionados (para facilitar el uso)
    private Recurso recursoObj;
    private Turista turistaObj;
    private Promocion promocionObj;
    
    public DetalleAlquiler() {}
    
    public DetalleAlquiler(int IDDetalleAlquiler, int IDRecurso, int IDTurista, 
                          int IDAlquiler, Integer IDPromocion) {
        this.IDDetalleAlquiler = IDDetalleAlquiler;
        this.IDRecurso = IDRecurso;
        this.IDTurista = IDTurista;
        this.IDAlquiler = IDAlquiler;
        this.IDPromocion = IDPromocion;
    }
    
    // Getters y Setters
    public int getIDDetalleAlquiler() { return IDDetalleAlquiler; }
    public void setIDDetalleAlquiler(int IDDetalleAlquiler) { 
        this.IDDetalleAlquiler = IDDetalleAlquiler; 
    }
    
    public int getIDRecurso() { return IDRecurso; }
    public void setIDRecurso(int IDRecurso) { this.IDRecurso = IDRecurso; }
    
    public int getIDTurista() { return IDTurista; }
    public void setIDTurista(int IDTurista) { this.IDTurista = IDTurista; }
    
    public int getIDAlquiler() { return IDAlquiler; }
    public void setIDAlquiler(int IDAlquiler) { this.IDAlquiler = IDAlquiler; }
    
    public Integer getIDPromocion() { return IDPromocion; }
    public void setIDPromocion(Integer IDPromocion) { this.IDPromocion = IDPromocion; }
    
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
        this.IDPromocion = promocionObj != null ? promocionObj.getIdPromocion() : null;
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
    
    @Override
    public String toString() {
        return "DetalleAlquiler{" +
                "IDDetalleAlquiler=" + IDDetalleAlquiler +
                ", Recurso='" + obtenerNombreRecurso() + '\'' +
                ", Turista='" + obtenerNombreTurista() + '\'' +
                ", TienePromocion=" + tienePromocion() +
                '}';
    }
}
