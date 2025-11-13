package modelo;

public class Usuario {
    private int IDUsuario;
    private String TipoDeUsuario; // admin, vendedor, turista
    private String Nombre;
    private String Contraseña;
    
    public Usuario() {}
    
    public Usuario(int IDUsuario, String TipoDeUsuario, String Nombre, String Contraseña) {
        this.IDUsuario = IDUsuario;
        this.TipoDeUsuario = TipoDeUsuario;
        this.Nombre = Nombre;
        this.Contraseña = Contraseña;
    }
    
    // Getters y Setters
    public int getIDUsuario() { return IDUsuario; }
    public void setIDUsuario(int IDUsuario) { this.IDUsuario = IDUsuario; }
    
    public String getTipoDeUsuario() { return TipoDeUsuario; }
    public void setTipoDeUsuario(String TipoDeUsuario) { this.TipoDeUsuario = TipoDeUsuario; }
    
    public String getNombre() { return Nombre; }
    public void setNombre(String Nombre) { this.Nombre = Nombre; }
    
    public String getContraseña() { return Contraseña; }
    public void setContraseña(String Contraseña) { this.Contraseña = Contraseña; }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "IDUsuario=" + IDUsuario +
                ", TipoDeUsuario='" + TipoDeUsuario + '\'' +
                ", Nombre='" + Nombre + '\'' +
                '}';
    }
}

