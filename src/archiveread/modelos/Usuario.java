package archiveread.modelos;

import java.io.Serializable;

// =========================================================================
// Usuario
// Clase base "molde" que define los datos básicos de cualquier persona que use el sistema
// =========================================================================

public abstract class Usuario implements Serializable {
	// Identificador de versión para que Java sepa cómo leer los objetos guardados en el .dat
    // Los objetos de tipo usuario tienen el ID 1L para diferenciarse de los libros
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
