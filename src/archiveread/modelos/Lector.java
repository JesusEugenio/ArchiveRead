package archiveread.modelos;

// =========================================================================
// Lector
// Representa a un usuario normal que solo puede rentar, devolver y guardar libros
// =========================================================================

public class Lector extends Usuario {
    private static final long serialVersionUID = 1L;
    
    public Lector(String matricula, String password, String nombre) {
        super(matricula, password, nombre);
    }
}
