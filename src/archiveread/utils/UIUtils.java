package archiveread.utils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class UIUtils {
	
	// Es el encargado de crear el texto para la UI y que tenga la fuente personalizada
	public static JLabel crearLabel(String texto, Font fuente, Color colorTexto) {
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
    public static void aplicarEstiloCombo(javax.swing.JComboBox<String> combo) {
        // Aplicamos el botón de flecha plano
        combo.setUI(new ComboModernoUI());
        combo.setMaximumRowCount(6);
        
        // Colores base del componente cerrado
        combo.setBackground(Color.WHITE);
        combo.setForeground(PaletaColores.TEXTO_NEGRO);
        combo.setFont(CargarFuente.getRegular(14f));
        combo.setBorder(BorderFactory.createLineBorder(PaletaColores.BORDE_CLARO, 1));
        
        // Modificamos cómo se dibujan las celdas de la lista al abrirse
        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                javax.swing.JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                // Le damos margen (padding) para que no se vea amontonado
                label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10)); 
                
                // Colores al pasar el mouse por encima (Hover)
                if (isSelected) {
                    label.setBackground(PaletaColores.PRIMARIO); // Fondo Magenta
                    label.setForeground(PaletaColores.TEXTO_BLANCO); // Letras Blancas
                } else {
                    label.setBackground(java.awt.Color.WHITE); // Fondo normal
                    label.setForeground(PaletaColores.TEXTO_NEGRO); // Letras normales
                }
                return label;
            }
        });
    }
    
    // Cuando hay campos que rellenar se utiliza esta funcion para que el usuario sepa que tiene que escribir ahi
    public static JLabel crearEtiquetaVacia(String texto) {
    	JLabel lblVacio = new JLabel(texto);
    	lblVacio.setFont(CargarFuente.getRegular(14f));
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
}

