package archiveread.ui;

import archiveread.modelos.Libro;
import archiveread.utils.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

// Vista exclusiva para el administrador, donde puede agregar nuevos libros

public class VistaNuevoLibro extends JPanel {
	
	public VistaNuevoLibro(ArrayList<String> categoriasExistentes, Consumer<Libro> onGuardarLibro) {
		
		// Consumer<Libro> es un cable de envio, esta vista no sabe como guardar el Libro en el archivo .dat
		// solo se encarga de recopilar los datos, crear el objeto Libro y usar este Consumer para "empujarlo/inyectarlo"
		// hacia el Main, que es quien realmente lo va a guardar
		
		setLayout(new BorderLayout());
		setBackground(PaletaColores.FONDO_PRINCIPAL);
		
		// -- Encabezado --
		JLabel lblTitulo = UIUtils.crearLabel("Nuevo Libro", CargarFuente.BOLD, 26f, PaletaColores.TEXTO_NEGRO);
		
	}

}
