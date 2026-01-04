package modelo;


public class Recurso {
    private String IDRecursos;
    private String Recurso;
    private String Descripcion;
    private double TarifaPorHora;
    private String Estado; 
    
    @Override
    public String toString() {
        return "Recurso [IDRecursos=" + IDRecursos + ", Recurso=" + Recurso + ", Descripcion=" + Descripcion
                + ", TarifaPorHora=" + TarifaPorHora + ", Estado=" + Estado + "]";
    }

    public String getIDRecursos() {
        return IDRecursos;
    }

    public void setIDRecursos(String iDRecursos) {
        IDRecursos = iDRecursos;
    }

    public String getRecurso() {
        return Recurso;
    }

    public void setRecurso(String recurso) {
        Recurso = recurso;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public double getTarifaPorHora() {
        return TarifaPorHora;
    }

    public void setTarifaPorHora(double tarifaPorHora) {
        TarifaPorHora = tarifaPorHora;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
     public boolean estaDisponible() {
        return "disponible".equalsIgnoreCase(Estado);
    }

    public Recurso() {}
    
    public Recurso(String IDRecursos, String Recurso, String Descripcion, 
                   double TarifaPorHora, String Estado) {
        this.IDRecursos = IDRecursos;
        this.Recurso = Recurso;
        this.Descripcion = Descripcion;
        this.TarifaPorHora = TarifaPorHora;
        this.Estado = Estado;
    }    
}
