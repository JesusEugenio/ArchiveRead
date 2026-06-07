package archiveread.gestores;

import archiveread.modelos.Libro;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

// =========================================================================
// --- Gestor de la Biblioteca ---
// Administra el inventario de libros en memoria y los guarda/carga en el archivo "inventario.dat"
// =========================================================================

public class GestorBiblioteca {
    private ArrayList<Libro> inventario; // Lista principal donde se almacenan todos los libros
    private final String ARCHIVO_BINARIO = "inventario.dat";
    private final String RUTA_COVERS = "covers/";
    
    // --- Constructor ---
    public GestorBiblioteca() {
        inventario = new ArrayList<>();
        cargarInventarioBinario(); // Cargamos los libros guardados anteriormente 
        
        String sinopsis1 = "Una guía completa desde los conceptos básicos de variables hasta el desarrollo de interfaces gráficas. Perfecto para adentrarse al mundo de la Programación Orientada a Objetos sin conocimientos previos.";
        String sinopsis2 = "Descubre cómo organizar y manipular datos en memoria de manera eficiente. Aprende sobre pilas, colas, árboles y grafos para optimizar el rendimiento de tu software.";
        String sinopsis3 = "En la Tierra Media, el Señor Oscuro Sauron forjó un Anillo Único para dominar a todos. Un joven hobbit deberá emprender un viaje épico para destruirlo.";
        String sinopsis4 = "Comprende el funcionamiento de Internet y las redes locales. Desde la capa física hasta la capa de aplicación usando el modelo OSI y TCP/IP.";
        
        // Si el archivo estaba vacio (primera vez que se abre), le añadimos libros de prueba para que la libreria no este vacia
        if (inventario.isEmpty()) {
            File carpetaCovers = new File(RUTA_COVERS);
            if (!carpetaCovers.exists()) carpetaCovers.mkdirs(); // Crea la carpeta "covers" si no existe
            
            inventario.add(new Libro("L001", "Java para Novatos", new ArrayList<>(Arrays.asList("Programación")), RUTA_COVERS + "L001_cover.jpg", "Juan Pérez", 350, sinopsis1));
            inventario.add(new Libro("L002", "Estructuras de Datos", new ArrayList<>(Arrays.asList("Sistemas")), RUTA_COVERS + "L002_cover.jpg", "María Gómez", 420, sinopsis2));
            inventario.add(new Libro("L003", "El Señor de los Anillos", new ArrayList<>(Arrays.asList("Fantasía")), RUTA_COVERS + "L003_cover.jpg", "J.R.R. Tolkien", 1200, sinopsis3));
            inventario.add(new Libro("L004", "Redes de Computadoras", new ArrayList<>(Arrays.asList("Sistemas", "Redes")), RUTA_COVERS + "L004_cover.jpg", "Andrew Tanenbaum", 800, sinopsis4));
            
        }
    }
    
    // Añade un nuevo libro a la lista y guarda los cambios en el binario
    public void registrarLibro(Libro libro) {
        inventario.add(libro);
        guardarInventarioBinario();
    }
    
    // Fuerza el guardado del inventario (util cuando se editó o rentó un libro)
    public void actualizarLibro() { 
    	guardarInventarioBinario(); 
    }
    
    // Busca el libro en la lista, lo borra y actualiza el archivo
    public void eliminarLibro(Libro libro) {
    	if(inventario.contains(libro)) {
    		inventario.remove(libro);
    		actualizarLibro(); // Guardamos los cambios en inventario.dat
    	}
    }
    
    // Obtiene los libros en inventario
    public ArrayList<Libro> obtenerLibros() { 
    	return inventario; 
    }
    
    // Busca en todos los libros y extrae las categorías sin repetirlas
    public ArrayList<String> obtenerCategoriasUnicas() {
        ArrayList<String> categorias = new ArrayList<>();
        for (Libro libro : inventario) {
            for (String cat : libro.getCategorias()) {
                if (!categorias.contains(cat) && !cat.isEmpty()) categorias.add(cat);
            }
        }
        return categorias;
    }
    
    // Devuelve los libros que pertenecen a una categoría específica
    public ArrayList<Libro> filtrarPorCategoria(String categoria) {
    	ArrayList<Libro> resultados = new ArrayList<>();
        
        // Extraemos los libros (Ya sean todos, o filtrados por categoria)
        if (categoria.equals("Todas")) {
            // Hacemos una copia de todo el inventario
            resultados.addAll(inventario); 
        } else {
            // Buscamos solo los que coincidan con la categoría
            for (Libro l : inventario) {
                if (l.getCategorias().contains(categoria)) resultados.add(l);
            }
        }
        
        // Volteamos la lista completa
        // Esto hara que los libros más nuevos aparezcan primero en pantalla
        java.util.Collections.reverse(resultados);
        
        return resultados;
    }
    
    // Guarda los datos del inventario en el archivo binario 
    private void guardarInventarioBinario() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_BINARIO))) {
            oos.writeObject(inventario);
            
        } catch (IOException e) { 
        	e.printStackTrace(); 
        }
    }
    
    // Lee el inventario.dat y lo convierte de vuelta en un ArrayList
    @SuppressWarnings("unchecked")
    private void cargarInventarioBinario() {
        File file = new File(ARCHIVO_BINARIO);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            inventario = (ArrayList<Libro>) ois.readObject();
            
        } catch (IOException | ClassNotFoundException e) { 
        	e.printStackTrace(); 
        }
    }
    
    
    // Generador de ID seguro - Evitamos colisiones al eliminar libros
    // Lee el ID más alto que exista (ej. L004) y genera el siguiente (L005)
    public String generarSiguienteId() {
    	int maxId = 0;
    	
    	// Recorremos todos los libros guardados 
    	for (Libro libro : obtenerLibros()) {
    		try {
    			String numeroTexto = libro.getIdLibro().substring(1);
    			int numeroActual = Integer.parseInt(numeroTexto);
    			
    			// Si este numero es mas grande que nuestro maxID, lo actualizamos
    			if(numeroActual > maxId) {
    				maxId = numeroActual;
    			}
    			
    		} catch (Exception e) {
    			// Si algun libro tiene un ID corrupto, lo ignoramos 	
    		}	
    	}
    	int siguienteNumero = maxId + 1;
    	
    	// String.format("%03d") obliga a que el numero siempre tenga al menos 3 numeros, rellenando lo faltante con 0
    	return "L" + String.format("%03d", siguienteNumero);		
    }
    
    // =========================================================================
    // --- Filtros para la vista 'MiBiblioteca' ---
    // =========================================================================

    // Obtiene los libros rentados por un usuario específico
    public ArrayList<Libro> obtenerLibrosRentadosPor(String matricula) {
        ArrayList<Libro> resultados = new ArrayList<>();
        
        for (Libro libro : inventario) {
            // Si el libro NO está disponible y la matrícula del préstamo coincide con la del usuario
            if (!libro.isDisponible() && matricula.equals(libro.getMatriculaPrestamo())) {
                resultados.add(libro);
            }
        }
        return resultados;
    }

    // Obtiene los libros guardados (Lista de deseos) por un usuario específico
    public ArrayList<Libro> obtenerLibrosGuardadosPor(String matricula) {
        ArrayList<Libro> resultados = new ArrayList<>();
        
        for (Libro libro : inventario) {
            if (libro.estaGuardado(matricula)) {
                resultados.add(libro);
            }
        }
        return resultados;
    }
}