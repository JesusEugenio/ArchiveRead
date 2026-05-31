package archiveread.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

//Cargador de fuente personalizada
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
	
	public static Font get(Font fuenteBase, float tamano) {
		return fuenteBase.deriveFont(tamano);
		
	}
	
}