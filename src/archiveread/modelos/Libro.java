package archiveread.modelos;

import java.io.Serializable; // Se implementa Serializable para poder guardar objetos en archivos .dat
import java.util.ArrayList;
import java.util.ArrayList;

public class Libro implements Serializable {
	// Identificador de control de version para la serializacion 
	/* Con esto nos aseguramos que los archivos binarios guardados sean compatibles 
	 * y no se corrompan al ser leidos por el programa, evitando errores si suceden cambios*/
    private static final long serialVersionUID = 5L; 
    
    // Atributos de información del libro
    private String idLibro;
    private String titulo;
    private ArrayList<String> categorias; 
    private String rutaImagen;
    private String autor;
    private int paginas;
    private String sinopsis;
    
    // Atributos de estado y lógica de negocio
    private boolean disponible;
    private String matriculaPrestamo; 
    private ArrayList<String> usuariosQueGuardaron; 

    public Libro(String idLibro, String titulo, ArrayList<String> categorias, String rutaImagen, String autor, int paginas, String sinopsis) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.categorias = categorias;
        this.rutaImagen = rutaImagen;
        this.autor = autor;
        this.paginas = paginas;
        this.sinopsis = sinopsis;
        this.disponible = true;
        this.matriculaPrestamo = null;
        this.usuariosQueGuardaron = new ArrayList<>();
    }

    // ==========================================
    // GETTERS Y SETTERS ORDENADOS
    // ==========================================

    // --- ID del Libro ---
    public String getIdLibro() { return idLibro; }
    public void setIdLibro(String idLibro) { this.idLibro = idLibro; }

    // --- Título ---
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    // --- Categorías ---
    public ArrayList<String> getCategorias() { return categorias; }
    public void setCategorias(ArrayList<String> categorias) { this.categorias = categorias; }
    
    // Método auxiliar para obtener la categoría principal
    public String getCategoria() { 
        if (categorias != null && !categorias.isEmpty()) {
            return categorias.get(0);
        } else {
            return "General";
        }
    } 
    
    // --- Portada (Ruta de la Imagen) ---
    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
    
    // --- Autor ---
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    // --- Páginas ---
    public int getPaginas() { return paginas; }
    public void setPaginas(int paginas) { this.paginas = paginas; }
    
    // --- Sinopsis ---
    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }
    
    // --- Disponibilidad (Préstamos) ---
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    // --- Matrícula de quien lo tiene prestado ---
    public String getMatriculaPrestamo() { return matriculaPrestamo; }
    public void setMatriculaPrestamo(String matriculaPrestamo) { this.matriculaPrestamo = matriculaPrestamo; }

    // ==========================================
    // MÉTODOS DE LÓGICA DE LISTA DE DESEOS
    // ==========================================
    
    // Verifica si un usuario específico ya guardó este libro
    public boolean estaGuardado(String matricula) { 
        return usuariosQueGuardaron.contains(matricula); 
    }
    
    // Agrega o quita el libro de la lista del usuario dependiendo de si ya estaba guardado
    public void toggleGuardado(String matricula) {
        if (estaGuardado(matricula)) {
            usuariosQueGuardaron.remove(matricula);
        } else {
            usuariosQueGuardaron.add(matricula);
        }
    }
}



