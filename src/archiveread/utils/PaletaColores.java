package archiveread.utils;

import java.awt.Color;

// =========================================================================
// PaletaColores
// Definicion de los colores, es la identidad visual de la app
// =========================================================================

public class PaletaColores {
	
	// Colores Principales
	public static final Color PRIMARIO = new Color(130, 49, 90);			// Magenta
	public static final Color FONDO_PRINCIPAL = new Color(250, 250, 250); 	// Blanco Opaco 
	
	// Texto
	public static final Color TEXTO_NEGRO = Color.BLACK;	
	public static final Color TEXTO_BLANCO = Color.WHITE;
	public static final Color TEXTO_GRIS_OSCURO = new Color(50, 50, 50); 	// Para las sinopsis y menus
	public static final Color TEXTO_GRIS_CLARO = Color.GRAY;				// Para autores y algunos subtitulos
	
	// Bordes y fondos de contenedores
	public static final Color BLANCO = Color.WHITE; 						// Para la tarjeta del libro - Separado para no confundir al usuario
	public static final Color COLOR_FONDO_BOX = new Color(240, 240, 240); 	// Para fondo de tabla dedicada a autor y el boton de ingresar
	public static final Color FONDO_BANNER_OSCURO = new Color(10, 10, 10); 	// El banner oscuro de la vista de detalles 
	public static final Color BORDE_CLARO = new Color(220, 220, 220);
	public static final Color BORDE_TARJETA = new Color(230, 230, 230);
	public static final Color FONDO_AREA = new Color(245, 245, 245);
	
	// Botones y estados de libro
	public static final Color ESTADO_DISPONIBLE = new Color(40, 168, 94);
	public static final Color ESTADO_OCUPADO = Color.RED;
	
	public static final Color BOTON_OSCURO = new Color(30, 35, 40); 		// Para "Añadir a Biblioteca"
	public static final Color BOTON_DESHABILITADO = new Color(60, 60, 60); 	
	
	public static final Color BOTON_EDITAR = new Color(72, 107, 168);
	public static final Color BOTON_ELIMINAR = new Color(140, 55, 78);
	
	public static final Color BOTON_QUITAR = new Color(200, 200, 200);
	public static final Color BOTON_DEVOLVER = new Color(192, 57, 43);
	
}
