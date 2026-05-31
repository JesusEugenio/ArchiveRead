package archiveread.modelos;

import java.io.Serializable;

public abstract class Usuario implements Serializable {
	// Los obj de tipo usuario tienen el ID 1 para no confundir a compilador con el ID del libro
	private static final long serialVersionUID = 1L; 
	
    private String matricula, password, nombre;
    
    public Usuario(String m, String p, String n) { 
        this.matricula = m; 
        this.password = p; 
        this.nombre = n; 
    }
    
    public String getMatricula() { return matricula; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
}
