package archiveread.gestores;

import archiveread.modelos.Libro;
import archiveread.modelos.Usuario;
import archiveread.utils.FileUtils;

import java.awt.Component;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

// =========================================================================
// --- Gestor de Reportes ---
// Exporta la información de la biblioteca en archivos .txt (Prestamos, Autores y Categorias)
// =========================================================================

public class GestorReportes {
	
	// Genera un archivo con todos los libros que estan prestados y quien los pidio
    public void generarReportePrestamos(Component ventanaPadre, GestorBiblioteca gestorBiblioteca, GestorUsuarios gestorUsuarios) {
        try {
            File file = new File("reporte_prestamos.txt");
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                writer.write("========================================\n");
                writer.write("      REPORTE DE LIBROS PRESTADOS       \n");
                writer.write("========================================\n");
                
                boolean hayPrestamos = false;
                // Revisa todos los libros de la base de datos
                for(Libro l : gestorBiblioteca.obtenerLibros()) {
                    if(!l.isDisponible()) {
                        writer.write("Título: " + l.getTitulo() + "\n");
                        writer.write("Autor: " + l.getAutor() + "\n");
                        writer.write("Prestado a (Matrícula): " + l.getMatriculaPrestamo() + "\n");
                        
                        // Busca quien fue el que lo rento usando el GestorUsuarios
                        Usuario u = gestorUsuarios.buscarPorMatricula(l.getMatriculaPrestamo());
                        if(u != null) {
                            writer.write("Nombre del Lector: " + u.getNombre() + "\n");
                        }
                        
                        writer.write("------------------------------------------\n");
                        hayPrestamos = true;
                    }
                }
                
                if(!hayPrestamos) {
                    writer.write("No hay libros prestados en este momento. \n");
                }
            }
            JOptionPane.showMessageDialog(ventanaPadre, "Reporte generado Exitosamente en 'reporte_prestamos.txt'");
        } catch(IOException e) {
            JOptionPane.showMessageDialog(ventanaPadre, "Error al generar el reporte de préstamos.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Crea una carpeta y dentro un archivo de texto por cada categoria existente
    public void generarReporteCategorias(Component ventanaPadre, GestorBiblioteca gestorBiblioteca) {
        try {
            File dir = new File("reportes_categorias");
            if(!dir.exists()) {
                dir.mkdirs();
            } else {
                limpiarDirectorio(dir); // Borra reportes viejos para actualizarlos
            }
            
            ArrayList<String> categorias = gestorBiblioteca.obtenerCategoriasUnicas();
            for(String cat : categorias) {
                String nombreSeguro = FileUtils.limpiarNombreArchivo(cat);
                File file = new File(dir, "Categoria_" + nombreSeguro + ".txt");
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                    writer.write("--- CATEGORÍA: " + cat + " ---\n\n");
                    // Mete en el archivo todos los libros que compartan esta categoría
                    for(Libro l : gestorBiblioteca.filtrarPorCategoria(cat)) {
                        writer.write("- " + l.getTitulo() + " (Autor: " + l.getAutor() + ")\n");
                    }
                }
            }
            JOptionPane.showMessageDialog(ventanaPadre, "Reportes de categorías generados en la carpeta 'reportes_categorias'.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    // Crea una carpeta y dentro un archivo de texto por cada autor
    public void generarReporteAutores(Component ventanaPadre, GestorBiblioteca gestorBiblioteca) {
        try {
            File dir = new File("reportes_autores");
            if(!dir.exists()) {
                dir.mkdirs();
            } else {
                limpiarDirectorio(dir); // Borra reportes viejos para actualizarlos
            }
            
            // Recolecta a todos los autores sin repetir
            ArrayList<String> autores = new ArrayList<>();
            for(Libro l : gestorBiblioteca.obtenerLibros()) {
                if(!autores.contains(l.getAutor())) {
                    autores.add(l.getAutor());
                }
            }
            
            for(String autor : autores) {
                String nombreSeguro = FileUtils.limpiarNombreArchivo(autor);
                File file = new File(dir, "Autor_" + nombreSeguro + ".txt");
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                    writer.write("--- AUTOR: " + autor + " ---\n\n");
                    for(Libro l : gestorBiblioteca.obtenerLibros()) {
                        // Validamos que el libro pertenezca al autor actual
                        if(l.getAutor().equals(autor)) { // Solo escribe los libros de este autor
                            writer.write("- " + l.getTitulo() + " (" + l.getCategoria() + ")\n");
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(ventanaPadre, "Reportes de autores generados en la carpeta 'reportes_autores'.");
        } catch(IOException e) {
            e.getStackTrace();
        }
    }
    
    // Metodo de limpieza auxiliar que borra el contenido de una carpeta (Util para sobreescribir .txt con informacion desactualizada)
    private void limpiarDirectorio(File dir) {
        if(dir.exists() && dir.isDirectory()) {
            File[] archivos = dir.listFiles();
            if(archivos != null) {
                for(File f : archivos) {
                    f.delete(); // Elimina cada archivo uno por uno
                }
            }
        }
    }

}