package archiveread.ui;

import archiveread.modelos.Libro;
import archiveread.utils.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class TarjetaLibro extends JPanel {
    
    public TarjetaLibro(Libro l, Runnable alHacerClic) {
    	// Margen de la Tarjeta 
    	setLayout(new BorderLayout());
        setOpaque(false); 
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); 
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
    	
        // Contenedor principal, tarjeta blanca que agrupa todo el contenido del libro
        JPanel panelTarjeta = new JPanel(new BorderLayout(20, 0));
        panelTarjeta.setBackground(PaletaColores.BLANCO);
        panelTarjeta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PaletaColores.BORDE_TARJETA, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Evento para abrir los detalles al hacer click en la tarjeta
        // Si el usuario da clic, ejecutamos la instrucción que nos pasó el padre.
        MouseAdapter eventoClic = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alHacerClic.run(); 
            }
        };
        
        // Lado izquierdo ... Portada del libro y su estado
        ImageIcon iconoPortada = new ImageIcon(l.getRutaImagen());
        Image imgEscalada = iconoPortada.getImage().getScaledInstance(120, 170, Image.SCALE_SMOOTH);
        JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
        lblImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblImagen.addMouseListener(eventoClic); // La portada funcionara como boton
        
        // Lado derecho ... Informacion del libro
        JPanel panelInfo = new JPanel(new BorderLayout(0, 5));
        panelInfo.setBackground(PaletaColores.BLANCO);
        panelInfo.setBorder(new EmptyBorder(0, 10, 0, 150));
        
        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        pnlTitulos.setBackground(PaletaColores.BLANCO);
        
        JLabel lblTitulo = UIUtils.crearLabel(l.getTitulo(), CargarFuente.BOLD, 20f, PaletaColores.TEXTO_NEGRO);
        lblTitulo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.addMouseListener(eventoClic); // El título también funciona como botón
        
        JLabel lblAutor = UIUtils.crearLabel("Autor: " + l.getAutor(), CargarFuente.BOLD, 13f, PaletaColores.TEXTO_GRIS_CLARO);
        lblAutor.setBorder(new EmptyBorder(5, 0, 5, 0));
        lblAutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(lblAutor);

        // Cortamos la sinopsis a 200 caracteres para que no desborde la tarjeta visualmente
        String sinopsisCorta = l.getSinopsis().length() > 200 ? 
                               l.getSinopsis().substring(0, 200) + "..." : 
                               l.getSinopsis();
        
        JTextArea txtSinopsis = new JTextArea(sinopsisCorta);
        txtSinopsis.setFont(CargarFuente.get(CargarFuente.REGULAR, 13f));
        txtSinopsis.setForeground(PaletaColores.TEXTO_NEGRO);
        txtSinopsis.setLineWrap(true);
        txtSinopsis.setWrapStyleWord(true);
        txtSinopsis.setEditable(false);
        txtSinopsis.setOpaque(false);
        txtSinopsis.setFocusable(false);
        txtSinopsis.setBorder(new EmptyBorder(10, 0, 10, 0));
        txtSinopsis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtSinopsis.addMouseListener(eventoClic); // La sinopsis también funciona como botón

        JLabel lblEstado = UIUtils.crearLabel(l.getPaginas() + " pág.", CargarFuente.REGULAR, 13f, PaletaColores.TEXTO_GRIS_OSCURO);

        panelInfo.add(pnlTitulos, BorderLayout.NORTH);
        panelInfo.add(txtSinopsis, BorderLayout.CENTER);
        panelInfo.add(lblEstado, BorderLayout.SOUTH);

        // Ensamblaje final de la tarjeta
        panelTarjeta.add(lblImagen, BorderLayout.WEST);
        panelTarjeta.add(panelInfo, BorderLayout.CENTER);
        add(panelTarjeta, BorderLayout.CENTER);
    }
}