package modelo;

public class Usuario {
    private String IDUsuario;
    private String TipoDeUsuario; // admin, vendedor, turista
    private String Nombre;
    private String Contraseña;
    
    public Usuario() {}
    
    public Usuario(String IDUsuario, String TipoDeUsuario, String Nombre, String Contraseña) {
        this.IDUsuario = IDUsuario;
        this.TipoDeUsuario = TipoDeUsuario;
        this.Nombre = Nombre;
        this.Contraseña = Contraseña;
    }
    
    // Getters y Setters
    public String getIDUsuario() { return IDUsuario; }
    public void setIDUsuario(String IDUsuario) { this.IDUsuario = IDUsuario; }
    
    public String getTipoDeUsuario() { return TipoDeUsuario; }
    public void setTipoDeUsuario(String TipoDeUsuario) { this.TipoDeUsuario = TipoDeUsuario; }
    
    public String getNombre() { return Nombre; }
    public void setNombre(String Nombre) { this.Nombre = Nombre; }
    
    public String getContraseña() { return Contraseña; }
    public void setContraseña(String Contraseña) { this.Contraseña = Contraseña; }
    public boolean esAdmin() {
        return TipoDeUsuario != null && TipoDeUsuario.toUpperCase().contains("ADMINISTRAD");
    }
    
    public boolean esEmpleado() {
        return TipoDeUsuario != null && TipoDeUsuario.equalsIgnoreCase("EMPLEADO");
    }
    
    public boolean esTurista() {
        return TipoDeUsuario != null && TipoDeUsuario.equalsIgnoreCase("TURISTA");
    }
    @Override
    public String toString() {
        return "Usuario{" +
                "IDUsuario=" + IDUsuario +
                ", TipoDeUsuario='" + TipoDeUsuario + '\'' +
                ", Nombre='" + Nombre + '\'' +
                '}';
    }
}

