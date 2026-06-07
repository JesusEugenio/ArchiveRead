package archiveread.modelos;

// =========================================================================
//	Administrador 
// 	Representa a un usuario con privilegios especiales (Añadir/Editar/Eliminar libros)
// =========================================================================

public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;
    
    public Administrador(String matricula, String password, String nombre) {
        super(matricula, password, nombre);
    }
}

