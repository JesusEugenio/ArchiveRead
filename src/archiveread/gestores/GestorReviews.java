package archiveread.gestores;

import archiveread.modelos.Libro;
import archiveread.utils.*;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GestorReviews {

    public void guardarReview(Libro libro, String usuario, String texto) {
        File dir = new File("reviews/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String nombreSeguro = FileUtils.limpiarNombreArchivo(libro.getTitulo());
        String nombreArchivo = "reviews/" + nombreSeguro + "_review.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))){
            writer.write(usuario + "|||" + texto);
            writer.newLine();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void cargarReviews(Libro libro, JPanel panelLista) {
        String nombreSeguro = FileUtils.limpiarNombreArchivo(libro.getTitulo());
        String nombreArchivo = "reviews/" + nombreSeguro + "_review.txt";
        File archivo = new File(nombreArchivo);
        
        if (!archivo.exists()) {
            panelLista.add(UIUtils.crearEtiquetaVacia("Aun no hay reviews. Sé el primero en opinar!"));
            return;
        }
        
        try(BufferedReader reader = new BufferedReader(new FileReader(archivo))){
            String linea;
            boolean hayReviews = false;
            
            while((linea = reader.readLine()) != null) {
                String [] partes = linea.split("\\|\\|\\|");
                
                if (partes.length == 2){
                    JPanel item = new JPanel(new BorderLayout(5,5));
                    item.setBackground(Color.WHITE);
                    item.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 10, 0),
                        BorderFactory.createCompoundBorder(
                                new LineBorder(new Color(230, 230, 230), 1),
                                new EmptyBorder(10, 10, 10, 10)
                        )
                    ));
                    item.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    // Aquí se inserta el nombre del usuario que dejó la review
                    JLabel lblUser = new JLabel(partes[0]);
                    lblUser.setFont(CargarFuente.getBold(14f));
                    lblUser.setForeground(Color.BLACK);
                    
                    JTextArea txtTexto = new JTextArea(partes[1]);
                    txtTexto.setForeground(Color.DARK_GRAY);
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