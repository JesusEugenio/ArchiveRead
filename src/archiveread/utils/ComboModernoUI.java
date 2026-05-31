package archiveread.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class ComboModernoUI extends BasicComboBoxUI {
    
    // =========================================================================
    // FLECHA QUE DESPLEGA LAS OPCIONES
    // =========================================================================
    @Override
    protected JButton createArrowButton() {
        // Creamos un botón normal pero tomamos control total de cómo se dibuja
        JButton botonFlecha = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                // 1. Forzamos el fondo del botón a que sea 100% blanco
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // 2. Dibujamos la flecha triangular gris manualmente con Antialiasing
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 100, 100)); // Gris oscuro
                
                // Matemáticas simples para centrar un triángulo
                int width = getWidth();
                int height = getHeight();
                int size = 10; // Tamaño de la flecha
                int x = (width - size) / 2;
                int y = (height - (size / 2)) / 2;
                
                // Coordenadas de los 3 puntos del triángulo
                int[] xPoints = {x, x + size, x + (size / 2)};
                int[] yPoints = {y, y, y + (size / 2)};
                
                g2.fillPolygon(xPoints, yPoints, 3);
                g2.dispose();
            }
        };
        
        // Eliminamos cualquier rastro de bordes interactivos
        botonFlecha.setBorder(BorderFactory.createEmptyBorder());
        botonFlecha.setContentAreaFilled(false);
        botonFlecha.setFocusPainted(false);
        botonFlecha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Grosor estético para la barra de la flecha
        botonFlecha.setPreferredSize(new Dimension(24, 0)); 
        
        return botonFlecha;
    }

    // =========================================================================
    //  FONDO BLANCO 
    // =========================================================================
    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        g.setColor(Color.WHITE);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    // =========================================================================
    // INYECCIÓN DEL SCROLL MODERNO
    // =========================================================================
    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {
            @Override
            protected JScrollPane createScroller() {
                JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                
                // Inyectamos el scroll moderno 
                scroller.getVerticalScrollBar().setUI(new ScrollModernoUI());
                scroller.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0)); 
                scroller.setBorder(BorderFactory.createLineBorder(PaletaColores.BORDE_CLARO, 1));
                
                return scroller;
            }
        };
        return popup;
    }
}