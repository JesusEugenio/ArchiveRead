package archiveread.gestores;

import archiveread.modelos.Libro;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

// =========================================================================
// CLASE: GESTOR DE BIBLIOTECA
// Encargada de leer y escribir 'inventario.dat'
// =========================================================================

public class GestorBiblioteca {
    private ArrayList<Libro> inventario;
    private final String ARCHIVO_BINARIO = "inventario.dat";
    private final String RUTA_COVERS = "covers/";

    public GestorBiblioteca() {
        inventario = new ArrayList<>();
        cargarInventarioBinario();
        
        String sinopsis1 = "Una guía completa desde los conceptos básicos de variables hasta el desarrollo de interfaces gráficas. Perfecto para adentrarse al mundo de la Programación Orientada a Objetos sin conocimientos previos.";
        String sinopsis2 = "Descubre cómo organizar y manipular datos en memoria de manera eficiente. Aprende sobre pilas, colas, árboles y grafos para optimizar el rendimiento de tu software.";
        String sinopsis3 = "En la Tierra Media, el Señor Oscuro Sauron forjó un Anillo Único para dominar a todos. Un joven hobbit deberá emprender un viaje épico para destruirlo.";
        String sinopsis4 = "Comprende el funcionamiento de Internet y las redes locales. Desde la capa física hasta la capa de aplicación usando el modelo OSI y TCP/IP.";
        
        if (inventario.isEmpty()) {
            File carpetaCovers = new File(RUTA_COVERS);
            if (!carpetaCovers.exists()) carpetaCovers.mkdirs(); 
            
            inventario.add(new Libro("L001", "Java para Novatos", new ArrayList<>(Arrays.asList("Programación")), RUTA_COVERS + "Java_para_Novatos_cover.jpg", "Juan Pérez", 350, sinopsis1));
            inventario.add(new Libro("L002", "Estructuras de Datos", new ArrayList<>(Arrays.asList("Sistemas")), RUTA_COVERS + "Estructuras_de_Datos_cover.jpg", "María Gómez", 420, sinopsis2));
            inventario.add(new Libro("L003", "El Señor de los Anillos", new ArrayList<>(Arrays.asList("Fantasía")), RUTA_COVERS + "El_Señor_de_los_Anillos_cover.jpg", "J.R.R. Tolkien", 1200, sinopsis3));
            inventario.add(new Libro("L004", "Redes de Computadoras", new ArrayList<>(Arrays.asList("Sistemas", "Redes")), RUTA_COVERS + "Redes_de_Computadoras_cover.jpg", "Andrew Tanenbaum", 800, sinopsis4));
            
        }
    }

    public void registrarLibro(Libro libro) {
        inventario.add(libro);
        guardarInventarioBinario();
    }

    public void actualizarLibro() { guardarInventarioBinario(); }

    public ArrayList<Libro> obtenerLibros() { return inventario; }

    public ArrayList<Libro> buscarLibrosPorTitulo(String termino) {
        String terminoMinusculas = termino.toLowerCase();
        ArrayList<Libro> resultados = new ArrayList<>();
        for (Libro l : inventario) {
            if (l.getTitulo().toLowerCase().contains(terminoMinusculas)) resultados.add(l); 
        }
        return resultados;
    }

    public ArrayList<String> obtenerCategoriasUnicas() {
        ArrayList<String> categorias = new ArrayList<>();
        for (Libro libro : inventario) {
            for (String cat : libro.getCategorias()) {
                if (!categorias.contains(cat) && !cat.isEmpty()) categorias.add(cat);
            }
        }
        return categorias;
    }

    public ArrayList<Libro> filtrarPorCategoria(String categoria) {
        if (categoria.equals("Todas")) return inventario;
        ArrayList<Libro> resultados = new ArrayList<>();
        for (Libro l : inventario) {
            if (l.getCategorias().contains(categoria)) resultados.add(l);
        }
        return resultados;
    }

    private void guardarInventarioBinario() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_BINARIO))) {
            oos.writeObject(inventario);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void cargarInventarioBinario() {
        File file = new File(ARCHIVO_BINARIO);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            inventario = (ArrayList<Libro>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
    }
}