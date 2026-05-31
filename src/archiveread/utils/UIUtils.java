package archiveread.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class UIUtils {
	
	// Es el encargado de crear el texto para la UI y que tenga la fuente personalizada
	public static JLabel crearLabel(String texto, Font fuente, float size, Color colorTexto) {
		JLabel lbl = new JLabel(texto);
		lbl.setFont(CargarFuente.get(fuente, size));
		lbl.setForeground(colorTexto);
		
		return lbl;
	}

	
    // Para crear un boton con un diseño mas moderno al que se ofrece por defecto (Texto y colores personalizados)
	// Version 1 -> Con colores personalizados
    public static JButton crearBotonEstandar(String texto, Color bg, Color fg) {
    	JButton btn = new JButton(texto);
    	btn.setFont(CargarFuente.get(CargarFuente.BOLD,  14f));
    	
    	// Asignamos color al texto 
    	if (fg != null) {
    		btn.setForeground(fg);
    	}
    	
    	// Evaluamos y asignamos color
    	if (bg !=  null) {
    		btn.setBackground(bg);
    		btn.setOpaque(true);
    		btn.setBorderPainted(false);
    		
    	} else {
    		btn.setOpaque(false);
    		btn.setContentAreaFilled(false);
    		btn.setContentAreaFilled(false);
    	}
    	
    	btn.setFocusPainted(false);
    	btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	
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
    	lbl.setFont(CargarFuente.get(CargarFuente.REGULAR, 15f));
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
    
    // Cuando hay campos que rellenar se utiliza esta funcion para que el usuario sepa que tiene que escribir ahi
    public static JLabel crearEtiquetaVacia(String texto) {
    	JLabel lblVacio = new JLabel(texto);
    	lblVacio.setFont(CargarFuente.get(CargarFuente.REGULAR, 14f));
    	lblVacio.setBorder(new EmptyBorder(0, 10, 0,0 ));
    	lblVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
    	return lblVacio;
    }

}
