package modelo;
public class recursos {
    private int idRecurso;
    private String tipoRecurso; // sombrilla, silla, tabla, kayak, etc
    private String descripcion;
    private double tarifaPorHora;
    private String estado; // disponible, alquilado, mantenimiento
    
    @Override
    public String toString() {
        return "recursos [idRecurso=" + idRecurso + ", tipoRecurso=" + tipoRecurso + ", descripcion=" + descripcion
                + ", tarifaPorHora=" + tarifaPorHora + ", estado=" + estado + "]";
    }

    public int getIdRecurso() {
        return idRecurso;
    }

    public void setIdRecurso(int idRecurso) {
        this.idRecurso = idRecurso;
    }

    public String getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(String tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getTarifaPorHora() {
        return tarifaPorHora;
    }

    public void setTarifaPorHora(double tarifaPorHora) {
        this.tarifaPorHora = tarifaPorHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public recursos() {}
    
    public recursos(int idRecurso, String tipoRecurso, String descripcion, 
                   double tarifaPorHora, String estado) {
        this.idRecurso = idRecurso;
        this.tipoRecurso = tipoRecurso;
        this.descripcion = descripcion;
        this.tarifaPorHora = tarifaPorHora;
        this.estado = estado;
    }
}
