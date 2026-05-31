package archiveread.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ScrollModernoUI extends BasicScrollBarUI {
    
    // Configuramos los grosores
    private final int GROSOR = 8;
    
    @Override
    protected void configureScrollBarColors() {
        // Color del fondo de la barra (El riel)
        this.trackColor = PaletaColores.FONDO_PRINCIPAL;
        // Color de la barra que se mueve (El pulgar)
        this.thumbColor = new Color(200, 200, 200); 
    }

    // ==========================================
    // 1. ELIMINAR LAS FLECHAS
    // ==========================================
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return crearBotonInvisible();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return crearBotonInvisible();
    }

    private JButton crearBotonInvisible() {
        JButton boton = new JButton();
        boton.setPreferredSize(new Dimension(0, 0));
        boton.setMinimumSize(new Dimension(0, 0));
        boton.setMaximumSize(new Dimension(0, 0));
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        return boton;
    }

    // ==========================================
    // 2. DIBUJAR LA PISTA (FONDO)
    // ==========================================
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    // ==========================================
    // 3. DIBUJAR EL PULGAR (BARRA MÓVIL)
    // ==========================================
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        // Antialiasing para que los bordes redondeados se vean suaves y no pixelados
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(thumbColor);
        // Dibujamos un rectángulo con bordes redondeados
        // Restamos un poco al ancho y agregamos un margen (x+2, width-4) para que no toque los bordes
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
        
        g2.dispose();
    }
}