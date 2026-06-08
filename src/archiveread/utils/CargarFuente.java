package archiveread.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

// =========================================================================
// CargarFuente
// Crea la fuente personalizada 'Red Hat Display' para el proyecto
// =========================================================================

public class CargarFuente {
	public static Font REGULAR;
	public static Font BOLD;
	public static Font ITALIC;
	
	// Ruta para indicar donde se encuentra la fuente
	private static final String RUTA_FONTS = "fonts/";
	static {
		try {
			REGULAR = Font.createFont(Font.TRUETYPE_FONT, new File(RUTA_FONTS + "RedHatDisplay-Regular.ttf"));
			BOLD = Font.createFont(Font.TRUETYPE_FONT, new File(RUTA_FONTS + "RedHatDisplay-Bold.ttf"));
			ITALIC = Font.createFont(Font.TRUETYPE_FONT, new File(RUTA_FONTS + "RedHatDisplay-Italic.ttf"));
			
			// Para permitir el uso de la fuente en la UI
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(REGULAR);
			ge.registerFont(BOLD);
			ge.registerFont(ITALIC);
			
			
		} catch (IOException | FontFormatException e) {
			// Plan de respaldo en caso de que las fuentes no se carguen
			System.err.println("Aviso - No se lograron cargar las fuentes. Usaremos SansSerif");
			REGULAR = new Font("SansSerif", Font.PLAIN, 12);
			BOLD = new Font("SansSerif", Font.PLAIN, 12);
			ITALIC = new Font("SansSerif", Font.PLAIN, 12);
			
			}
		
		}
	
	
	// Getters
	public static Font getRegular(float tamano) {
		return REGULAR.deriveFont(tamano);
	}
	
	public static Font getBold(float tamano) {
		return BOLD.deriveFont(tamano);
	}
	
	public static Font getItalic(float tamano) {
		return ITALIC.deriveFont(tamano);
	}
	
}