package archiveread.gestores;

import archiveread.modelos.Libro;
import archiveread.utils.*;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GestorReviews {
	
	// Metodo para Guardar Reviews
    public void guardarReview(Libro libro, String usuario, String texto) {
        File dir = new File("reviews/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String nombreSeguro = FileUtils.limpiarNombreArchivo(libro.getTitulo());
        String nombreArchivo = "reviews/" + nombreSeguro + "_review.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))){
            writer.write(usuario + "|||" + texto); // Formato para guardar las reviews en el .txt
            writer.newLine();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    // Cargar Reviews y mostrarlas como si fueran un comentario web
    public void cargarReviews(Libro libro, JPanel panelLista) {
        String nombreSeguro = FileUtils.limpiarNombreArchivo(libro.getTitulo());
        String nombreArchivo = "reviews/" + nombreSeguro + "_review.txt";
        File archivo = new File(nombreArchivo);
        
        // Si no hay reviews
        if (!archivo.exists()) {
            panelLista.add(UIUtils.crearEtiquetaVacia("Aun no hay reviews. Sé el primero en opinar!"));
            return;
        }
        
        try(BufferedReader reader = new BufferedReader(new FileReader(archivo))){
            String linea;
            boolean hayReviews = false;
            
            // Leemos el .txt línea por línea
            while((linea = reader.readLine()) != null) {
            	// split("\\|\\|\\|") corta la oracion al encontrar "|||"
            	// partes[0] = Guarda lo de la izquierda (El Nombre)
            	// partes[1] = Guarda lo de la derecha (El Comentario)
                String [] partes = linea.split("\\|\\|\\|");
                
                if (partes.length == 2){
                    JPanel item = new JPanel(new BorderLayout(0,5));
                    item.setBackground(Color.WHITE);
                    
                    item.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 15, 0),
                        BorderFactory.createCompoundBorder(
                                new LineBorder(PaletaColores.BORDE_CLARO, 1),
                                new EmptyBorder(15, 15, 15, 15)
                        )
                    ));
                    item.setAlignmentX(Component.LEFT_ALIGNMENT);
                    item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
                    
                    // Aquí se inserta el nombre del usuario que dejó la review
                    JLabel lblUser = new JLabel(partes[0]);
                    lblUser.setFont(CargarFuente.getBold(14f));
                    lblUser.setForeground(Color.BLACK);
                    
                    JTextArea txtTexto = new JTextArea(partes[1]);
                    txtTexto.setForeground(PaletaColores.TEXTO_GRIS_OSCURO);
                    txtTexto.setLineWrap(true);
                    txtTexto.setWrapStyleWord(true);
                    txtTexto.setEditable(false);
                    txtTexto.setOpaque(false);
                    
                    item.add(lblUser, BorderLayout.NORTH);
                    item.add(txtTexto, BorderLayout.CENTER);
                    
                    panelLista.add(item);
                    hayReviews = true;
                }
            }
            
            if (!hayReviews) {
                panelLista.add(UIUtils.crearEtiquetaVacia("Aun no hay reviews. !Sé el primero en opinar!"));
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}