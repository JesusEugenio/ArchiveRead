package archiveread.utils;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// =========================================================================
// UIUtils
// Clase para rediseñar estilos de botones y textos - reduce lineas de codigo
// =========================================================================

public class UIUtils {
	
	// Es el encargado de crear el texto para la UI y que tenga la fuente personalizada
	public static JLabel crearLabel(String texto, Font fuente, float size, Color colorTexto) {
		JLabel lbl = new JLabel(texto);
		lbl.setFont(fuente.deriveFont(size));
		lbl.setForeground(colorTexto);
		
		return lbl;
	}

	
    // Para crear un boton con un diseño mas moderno al que se ofrece por defecto (Texto y colores personalizados)
	// Version 1 -> Con colores personalizados
    public static JButton crearBotonEstandar(String texto, Color bg, Color fg) {
    	JButton btn = new JButton(texto) {
    		@Override
            protected void paintComponent(Graphics g) {
                // Solo dibujamos la píldora si el botón tiene un color de fondo asignado
                if (bg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // Antialiasing para bordes curvos y suaves
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g2.dispose();
                }
                // Le pedimos a Java que ahora sí dibuje el texto por encima de nuestro fondo
                super.paintComponent(g);
    		}
    	}; 
    	
    	
    	btn.setFont(CargarFuente.getBold(14f));
    	
    	// Asignamos color al texto 
    	if (fg != null) {
    		btn.setForeground(fg);
    	}
    	
    	// Evaluamos y asignamos color
    	if (bg !=  null) {
    		btn.setBackground(bg);
    		btn.setContentAreaFilled(false);
    		btn.setBorderPainted(false);
    		
    	} else {
    		btn.setOpaque(false);
    		btn.setContentAreaFilled(false);
    	}
    	
    	btn.setFocusPainted(false);
    	btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 15));
    	
    	return btn;
    }
    
    // Sobrecarga
    // Version 2 -> Solo envia el texto
    public static JButton crearBotonEstandar(String texto) {
    	// Llamamos a la version 1 con los colores por defecto
    	return crearBotonEstandar(texto, PaletaColores.PRIMARIO, PaletaColores.TEXTO_BLANCO);
    }
    
    
    // Funciona como un Texto con funciones al dar clic en el 
    public static JLabel crearMenuLabel(String texto, Runnable accion) {
    	JLabel lbl = new JLabel(texto);
    	lbl.setFont(CargarFuente.getRegular(15f));
    	lbl.setForeground(new Color (50, 50, 50));
    	lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	
    	// Runnable -> ejecuta una accion que le mandaron como parametro desde otra funcion, es esencial para factorizar el codigo sin generar errores
    	lbl.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) { 
    			accion.run();
    		}
    	});
    	return lbl;
    }
    
    // ComboBox moderno
    public static void aplicarEstiloCombo(JComboBox<String> combo) {
        // Aplicamos el botón de flecha plano
        combo.setUI(new ComboModernoUI());
        combo.setMaximumRowCount(6);
        
        // Colores base del componente cerrado
        combo.setBackground(Color.WHITE);
        combo.setForeground(PaletaColores.TEXTO_NEGRO);
        combo.setFont(CargarFuente.getRegular(14f));
        combo.setBorder(BorderFactory.createLineBorder(PaletaColores.BORDE_CLARO, 1));
        
        // Modificamos cómo se dibujan las celdas de la lista al abrirse
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                // Le damos margen (padding) para que no se vea amontonado
                label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10)); 
                
                // Colores al pasar el mouse por encima (Hover)
                if (isSelected) {
                    label.setBackground(PaletaColores.PRIMARIO); // Fondo Magenta
                    label.setForeground(PaletaColores.TEXTO_BLANCO); // Letras Blancas
                } else {
                    label.setBackground(PaletaColores.BLANCO); // Fondo normal
                    label.setForeground(PaletaColores.TEXTO_NEGRO); // Letras normales
                }
                return label;
            }
        });
    }
    
    // Cuando hay campos que rellenar se utiliza esta funcion para que el usuario sepa que tiene que escribir ahi
    public static JLabel crearEtiquetaVacia(String texto) {
    	JLabel lblVacio = new JLabel(texto);
    	lblVacio.setFont(CargarFuente.getItalic(14f));
    	lblVacio.setBorder(new EmptyBorder(0, 10, 0,0 ));
    	lblVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
    	return lblVacio;
    }
    
    
    // Genera un campo de texto para areas donde el usuario tiene que escribir
    public static JTextField crearTextFieldFormulario() {
    	JTextField txt = new JTextField();
    	txt.setFont(CargarFuente.getRegular(14f));
    	txt.setBorder(BorderFactory.createCompoundBorder(
    			new LineBorder(PaletaColores.BORDE_CLARO, 1, true),
    			new EmptyBorder(0, 10, 0, 10)
    	));
    	txt.setBackground(PaletaColores.FONDO_AREA);
    	txt.setAlignmentX(Component.LEFT_ALIGNMENT);
    	return txt;
    }
    
    // Retiene la imagen original en HD y la renderiza en tiempo real usando el 
    // motor gráfico al momento de pintar, solucionando los pixeles del escalado 1.25
    public static Icon escalarImagenAltaCalidad(String ruta, int ancho, int alto) {
        try {
            java.io.File archivo = new java.io.File(ruta);
            if (!archivo.exists()) return new ImageIcon(); // Retorno seguro

            // Cargamos la imagen original en su resolución nativa más alta
            final Image imgOriginal = javax.imageio.ImageIO.read(archivo);

            // Creamos y devolvemos un Icono Inteligente que se auto-dibuja
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    
                    // Activamos los motores de suavizado del hardware gráfico
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Plasmamos la imagen HD directamente en las coordenadas y tamaño solicitados
                    g2d.drawImage(imgOriginal, x, y, ancho, alto, null);
                    g2d.dispose();
                }

                @Override
                public int getIconWidth() {
                    return ancho;
                }

                @Override
                public int getIconHeight() {
                    return alto;
                }
            };

        } catch (Exception e) {
            System.err.println("Error procesando la imagen: " + ruta);
            return new ImageIcon();
        }
    }
    
}

