package modelo;

public class Turista extends Usuario{
    private int IDTurista;
    private String Nombre;
    private String Apellido;
    private String DNI;
    private String Nacionalidad;
    private String Telefono;
    private String Email;
    
    @Override
    public String toString() {
        return "Turista [IDTurista=" + IDTurista + ", Nombre=" + Nombre + ", Apellido=" + Apellido + ", DNI=" + DNI
                + ", Nacionalidad=" + Nacionalidad + ", Telefono=" + Telefono + ", Email=" + Email + "]";
    }
    public int getIDTurista() {
        return IDTurista;
    }
    public void setIDTurista(int iDTurista) {
        IDTurista = iDTurista;
    }
    public String getNombre() {
        return Nombre;
    }
    public void setNombre(String nombre) {
        Nombre = nombre;
    }
    public String getApellido() {
        return Apellido;
    }
    public void setApellido(String apellido) {
        Apellido = apellido;
    }
    public String getDNI() {
        return DNI;
    }
    public void setDNI(String dNI) {
        DNI = dNI;
    }
    public String getNacionalidad() {
        return Nacionalidad;
    }
    public void setNacionalidad(String nacionalidad) {
        Nacionalidad = nacionalidad;
    }
    public String getTelefono() {
        return Telefono;
    }
    public void setTelefono(String telefono) {
        Telefono = telefono;
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }

    public Turista() {}
    
     public String getNombreCompleto() {
        return Nombre + " " + Apellido;
    }


    public Turista(int IDUsuario, String TipoDeUsuario, String Nombre, String Contraseña, int iDTurista, String nombre2,
            String apellido, String dNI, String nacionalidad, String telefono, String email) {
        super(IDUsuario, TipoDeUsuario, Nombre, Contraseña);
        IDTurista = iDTurista;
        Nombre = nombre2;
        Apellido = apellido;
        DNI = dNI;
        Nacionalidad = nacionalidad;
        Telefono = telefono;
        Email = email;
    }




}
   





    



