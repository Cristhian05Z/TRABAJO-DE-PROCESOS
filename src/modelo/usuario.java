package modelo;
public class Usuario {
    private int idusuario;
    private String Nombre;
    private String contraseña;
    private String tipodeusuario;

    @Override
    public String toString() {
        return "usuario [idusuario=" + idusuario + ", Nombre=" + Nombre + ", contraseña=" + contraseña
                + ", tipodeusuario=" + tipodeusuario + "]";
    }

    public Usuario() {}
    
    public Usuario(int idusuario, String nombre, String contraseña, String tipodeusuario) {
        this.idusuario = idusuario;
        Nombre = nombre;
        this.contraseña = contraseña;
        this.tipodeusuario = tipodeusuario;
    }


    public int getIdusuario() {
        return idusuario;
    }


    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }


    public String getNombre() {
        return Nombre;
    }


    public void setNombre(String nombre) {
        Nombre = nombre;
    }


    public String getContraseña() {
        return contraseña;
    }


    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }


    public String getTipodeusuario() {
        return tipodeusuario;
    }


    public void setTipodeusuario(String tipodeusuario) {
        this.tipodeusuario = tipodeusuario;
    }
}
