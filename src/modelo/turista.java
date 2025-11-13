package modelo;

public class turista extends Usuario{
    private int idturista;
    private String nombreturista;
    private String apellido;
    private String dni;
    private String nacionalidad;
    private String telefono;
    private String email;
    
    @Override
    public String toString() {
        return "turista [idturista=" + idturista + ", nombreturista=" + nombreturista + ", apellido=" + apellido
                + ", dni=" + dni + ", nacionalidad=" + nacionalidad + ", telefono=" + telefono + ", email=" + email
                + "]";
    }
    public int getIdturista() {
        return idturista;
    }
    public void setIdturista(int idturista) {
        this.idturista = idturista;
    }
    public String getNombreturista() {
        return nombreturista;
    }
    public void setNombreturista(String nombreturista) {
        this.nombreturista = nombreturista;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public turista() {
        super();
    }
     public turista(int idusuario, String tipodeusuario, String nombre, String contraseña,int idturista, String nombreturista, String apellido, String dni, String nacionalidad, String telefono, String email) {
        super(idusuario, tipodeusuario, nombre, contraseña);
        this.idturista = idturista;
        this.nombreturista = nombreturista;
        this.apellido = apellido;
        this.dni = dni;
        this.nacionalidad = nacionalidad;
        this.telefono = telefono;
        this.email = email;
    }
    public String getNombreCompleto() {
        return nombreturista + " " + apellido;
    }



}
