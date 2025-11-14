package modelo;

public class Promocion {
     private String IDPromocion;
    private double PorcentajeDescuento;
    private String Condiciones;
    
    public Promocion() {}
    
    public Promocion(String IDPromocion, double PorcentajeDescuento, String Condiciones) {
        this.IDPromocion = IDPromocion;
        this.PorcentajeDescuento = PorcentajeDescuento;
        this.Condiciones = Condiciones;
    }
    
    // Getters y Setters
    public String getIDPromocion() { return IDPromocion; }
    public void setIDPromocion(String IDPromocion) { this.IDPromocion = IDPromocion; }
    
    public double getPorcentajeDescuento() { return PorcentajeDescuento; }
    public void setPorcentajeDescuento(double PorcentajeDescuento) { 
        this.PorcentajeDescuento = PorcentajeDescuento; 
    }
    
    public String getCondiciones() { return Condiciones; }
    public void setCondiciones(String Condiciones) { this.Condiciones = Condiciones; }
    
    public double calcularDescuento(double monto) {
        return monto * (PorcentajeDescuento / 100.0);
    }
    
    public double aplicarDescuento(double monto) {
        return monto - calcularDescuento(monto);
    }
    
    @Override
    public String toString() {
        return "Promocion{" +
                "IDPromocion=" + IDPromocion +
                ", PorcentajeDescuento=" + PorcentajeDescuento + "%" +
                ", Condiciones='" + Condiciones + '\'' +
                '}';
    }
}

