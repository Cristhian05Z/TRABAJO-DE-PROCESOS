package modelo;

public class promocion {
    private int idPromocion;
    private double porcentajeDescuento;
    private String condiciones; // "3x2", "Descuento familia", "Happy hour", etc
    
    @Override
    public String toString() {
        return "promocion [idPromocion=" + idPromocion + ", porcentajeDescuento=" + porcentajeDescuento
                + ", condiciones=" + condiciones + "]";
    }

    public int getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(int idPromocion) {
        this.idPromocion = idPromocion;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public promocion() {}
    
    public promocion(int idPromocion, double porcentajeDescuento, String condiciones) {
        this.idPromocion = idPromocion;
        this.porcentajeDescuento = porcentajeDescuento;
        this.condiciones = condiciones;
    }
}
