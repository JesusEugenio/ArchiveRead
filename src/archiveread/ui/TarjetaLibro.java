package archiveread.ui;

import archiveread.modelos.Libro;
import archiveread.utils.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// =============================================
// TarjetaLibro
// Componente visual que representa un resumen de un libro
// =============================================

public class TarjetaLibro extends JPanel {
    
	// Recibe el libro a mostrar y la acción que debe ejecutarse al hacerle clic
    public TarjetaLibro(Libro l, Runnable alHacerClic) {
    	// Margen de la Tarjeta 
    	setLayout(new BorderLayout());
        setOpaque(false); 
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Espaciado inferior entre tarjetas
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 250)); // Tope de altura
    	
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
        JPanel pnlIzq = new JPanel(new BorderLayout());
        pnlIzq.setBackground(PaletaColores.BLANCO);
        
        JLabel lblImagen = new JLabel(UIUtils.escalarImagenAltaCalidad(l.getRutaImagen(), 120, 170));
        lblImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblImagen.addMouseListener(eventoClic); // La portada funcionara como boton
        pnlIzq.add(lblImagen, BorderLayout.CENTER);
        
        String textoEstado = l.isDisponible() ? "Disponible" : "Prestado";
        Color colorEstado = l.isDisponible() ? PaletaColores.ESTADO_DISPONIBLE : PaletaColores.ESTADO_OCUPADO;
        
        JLabel lblEstadoSub = new JLabel(textoEstado, SwingConstants.CENTER);
        lblEstadoSub.setForeground(colorEstado);
        lblEstadoSub.setFont(CargarFuente.getRegular(12f));
        lblEstadoSub.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        pnlIzq.add(lblEstadoSub, BorderLayout.SOUTH);
        
        
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
        txtSinopsis.setFont(CargarFuente.getRegular(13f));
        txtSinopsis.setForeground(PaletaColores.TEXTO_NEGRO);
        txtSinopsis.setLineWrap(true);
        txtSinopsis.setWrapStyleWord(true);
        txtSinopsis.setEditable(false);
        txtSinopsis.setOpaque(false);
        txtSinopsis.setFocusable(false);
        txtSinopsis.setBorder(new EmptyBorder(10, 0, 5, 0));
        txtSinopsis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtSinopsis.addMouseListener(eventoClic); // La sinopsis también funciona como botón

        JLabel lblEstado = UIUtils.crearLabel(l.getPaginas() + " pág.", CargarFuente.REGULAR, 13f, PaletaColores.TEXTO_GRIS_OSCURO);
        
        // Agrupamos Sinopsis y Estado para que suban juntos
        JPanel pnlCentroTextos = new JPanel(new BorderLayout());
        pnlCentroTextos.setOpaque(false);
        pnlCentroTextos.add(txtSinopsis, BorderLayout.NORTH);
        
        JPanel pnlWrapEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlWrapEstado.setOpaque(false);
        pnlWrapEstado.add(lblEstado);
        
        pnlCentroTextos.add(pnlWrapEstado, BorderLayout.CENTER);

        panelInfo.add(pnlTitulos, BorderLayout.NORTH);
        panelInfo.add(pnlCentroTextos, BorderLayout.CENTER);;

        // Ensamblaje final de la tarjeta
        panelTarjeta.add(pnlIzq, BorderLayout.WEST);
        panelTarjeta.add(panelInfo, BorderLayout.CENTER);
        add(panelTarjeta, BorderLayout.CENTER);
    }
}