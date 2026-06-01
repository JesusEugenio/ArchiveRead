package archiveread.ui;

import archiveread.modelos.Libro;
import archiveread.utils.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

// Vista exclusiva para el administrador, donde puede agregar nuevos libros

public class VistaNuevoLibro extends JPanel {
	
	public VistaNuevoLibro(ArrayList<String> categoriasExistentes, Consumer<Libro> onGuardarLibro) {
		setLayout(new BorderLayout());
		setBackground(PaletaColores.FONDO_PRINCIPAL);
		
		// -- Encabezado --
		JLabel lblTitulo = UIUtils.crearLabel("Nuevo Libro", CargarFuente.BOLD, 26f, PaletaColores.TEXTO_NEGRO);
		lblTitulo.setBorder(new EmptyBorder(20, 40, 20, 40));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlContenido = new JPanel(new BorderLayout(40, 0));
        pnlContenido.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlContenido.setBorder(new EmptyBorder(0, 40, 40, 40));
        
        // -- Selector de Portada --
        JPanel pnlPortada = new JPanel();
        pnlPortada.setLayout(new BoxLayout(pnlPortada, BoxLayout.Y_AXIS));
        pnlPortada.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        JPanel pnlCajaPortada = new JPanel(new BorderLayout()); 
        pnlCajaPortada.setBackground(new Color(235, 235, 235));
        pnlCajaPortada.setPreferredSize(new Dimension(220, 320));
        pnlCajaPortada.setMaximumSize(new Dimension(220, 320));
        pnlCajaPortada.setBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1, true));
        pnlCajaPortada.setAlignmentX(Component.CENTER_ALIGNMENT); 

        JLabel lblPreview = UIUtils.crearLabel("Sin Portada", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_GRIS_CLARO);
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCajaPortada.add(lblPreview, BorderLayout.CENTER);
        
        // Array de 1 posición para guardar la ruta seleccionada y poder modificarla dentro de la expresion Lambda
        final String[] rutaImagenSeleccionada = {"covers/default.jpg"};
        
        JButton btnSubirPortada = new JButton("Subir portada");
        btnSubirPortada.setBackground(PaletaColores.BLANCO);
        btnSubirPortada.setFocusPainted(false);
        btnSubirPortada.setFont(CargarFuente.getBold(12f));
        
        // la flecha (->) le dice a Java: Cuando ocurra el clic (evento 'e') 
        // simplemente ejecuta el código que está dentro de estas llaves
        btnSubirPortada.addActionListener(e -> {    	
        	Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        	FileDialog fd = new FileDialog((Frame) ventanaPadre, "Seleccionar Portada", FileDialog.LOAD);
        	fd.setFile("*.jpg;*.jpeg;*.png;");
        	fd.setVisible(true);
        	
        	String directorio = fd.getDirectory();
        	String nombreArchivo = fd.getFile();
            
            if (directorio != null && nombreArchivo != null) {
                rutaImagenSeleccionada[0] = directorio + nombreArchivo;
                btnSubirPortada.setText("Cambiar portada");
                btnSubirPortada.setBackground(PaletaColores.BLANCO);

                ImageIcon icon = new ImageIcon(rutaImagenSeleccionada[0]);
                Image img = icon.getImage().getScaledInstance(218, 288, Image.SCALE_SMOOTH);
                lblPreview.setText(""); 
                lblPreview.setIcon(new ImageIcon(img));
            }
        });
        
        pnlCajaPortada.add(btnSubirPortada, BorderLayout.SOUTH);
        pnlPortada.add(pnlCajaPortada);
        pnlContenido.add(pnlPortada, BorderLayout.WEST);

        // -- Area para rellenar los datos del libro --
        JPanel pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setBackground(PaletaColores.FONDO_PRINCIPAL);

        // --- CAJA 1: Datos Generales ---
        JPanel caja1 = new JPanel();
        caja1.setLayout(new BoxLayout(caja1, BoxLayout.Y_AXIS));
        caja1.setBackground(PaletaColores.BLANCO);
        caja1.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PaletaColores.BORDE_TARJETA, 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));
        caja1.setAlignmentX(Component.LEFT_ALIGNMENT);

        caja1.add(UIUtils.crearLabel("Detalles de Publicación", CargarFuente.BOLD, 18f, PaletaColores.TEXTO_NEGRO));
        caja1.add(Box.createVerticalStrut(20));
        
        caja1.add(UIUtils.crearLabel("Nombre de libro", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO));
        JTextField txtNombreLibro = UIUtils.crearTextFieldFormulario();
        txtNombreLibro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        caja1.add(txtNombreLibro);
        caja1.add(Box.createVerticalStrut(20));

        JPanel pnlDetalles = new JPanel(new GridLayout(1, 2, 20, 0)); // El 20 es el espacio entre columnas
        pnlDetalles.setBackground(PaletaColores.BLANCO);
        pnlDetalles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        pnlDetalles.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtAutor = UIUtils.crearTextFieldFormulario();
        JTextField txtPaginas = UIUtils.crearTextFieldFormulario();

        JPanel wrapAutor = new JPanel(new BorderLayout()); 
        wrapAutor.setBackground(PaletaColores.BLANCO);
        wrapAutor.add(UIUtils.crearLabel("Autor", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO), BorderLayout.NORTH); 
        wrapAutor.add(txtAutor, BorderLayout.CENTER);
        
        JPanel wrapPaginas = new JPanel(new BorderLayout()); 
        wrapPaginas.setBackground(PaletaColores.BLANCO);
        wrapPaginas.add(UIUtils.crearLabel("Nº de Paginas", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO), BorderLayout.NORTH); 
        wrapPaginas.add(txtPaginas, BorderLayout.CENTER);

        pnlDetalles.add(wrapAutor);
        pnlDetalles.add(wrapPaginas);
        caja1.add(pnlDetalles);
        caja1.add(Box.createVerticalStrut(20));
        
        // Categorías 
        // Insertamos el texto "Sin especificar" para identificar si el usuario no agrego categorias en su libro
        if (!categoriasExistentes.contains("Sin especificar")) {
            categoriasExistentes.add(0, "Sin especificar");
        }
        
        JPanel pnlCombos = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCombos.setBackground(PaletaColores.BLANCO);
        pnlCombos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        pnlCombos.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Convertimos un ArrayList en uno tradicional para que lo pueda usar ComboBox
        JComboBox<String> comboGenero1 = new JComboBox<>(categoriasExistentes.toArray(new String[0]));
        UIUtils.aplicarEstiloCombo(comboGenero1);
        comboGenero1.setEditable(true); // Permite escribir texto nuevo
        
        JComboBox<String> comboGenero2 = new JComboBox<>(categoriasExistentes.toArray(new String[0]));
        UIUtils.aplicarEstiloCombo(comboGenero2);
        comboGenero2.setEditable(true); 
        
        JPanel wrapGen1 = new JPanel(new BorderLayout()); 
        wrapGen1.setBackground(PaletaColores.BLANCO);
        wrapGen1.add(UIUtils.crearLabel("Categoria Principal", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO), BorderLayout.NORTH); 
        wrapGen1.add(comboGenero1, BorderLayout.CENTER);
        
        JPanel wrapGen2 = new JPanel(new BorderLayout()); 
        wrapGen2.setBackground(PaletaColores.BLANCO);
        wrapGen2.add(UIUtils.crearLabel("Categoria Secundaria", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO), BorderLayout.NORTH); 
        wrapGen2.add(comboGenero2, BorderLayout.CENTER);

        pnlCombos.add(wrapGen1);
        pnlCombos.add(wrapGen2);
        caja1.add(pnlCombos);
		
        // -- CAJA 2: Sinopsis --
        JPanel cajaSinopsis = new JPanel();
        cajaSinopsis.setLayout(new BoxLayout(cajaSinopsis, BoxLayout.Y_AXIS));
        cajaSinopsis.setBackground(PaletaColores.BLANCO);
        cajaSinopsis.setBorder(BorderFactory.createCompoundBorder(
        		new LineBorder(PaletaColores.BORDE_TARJETA, 1, true),
        		new EmptyBorder(25, 25, 25, 25)
        ));
        cajaSinopsis.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cajaSinopsis.add(UIUtils.crearLabel("Sinopsis", CargarFuente.BOLD, 18f, PaletaColores.TEXTO_NEGRO));
        cajaSinopsis.add(Box.createVerticalStrut(5));
        cajaSinopsis.add(UIUtils.crearLabel("Las obras con sinopsis se leen mas a menudo...", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_GRIS_CLARO));
        cajaSinopsis.add(Box.createVerticalStrut(20));
        
        JTextArea txtSinopsisArea = new JTextArea(6, 20);
        txtSinopsisArea.setLineWrap(true);	// Evita que el texto este en una sola linea horizontal infinita
        txtSinopsisArea.setWrapStyleWord(true); // Obliga a que los saltos de linea respeten palabras completas
        txtSinopsisArea.setBackground(PaletaColores.FONDO_AREA);
        txtSinopsisArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtSinopsisArea.setFont(CargarFuente.getRegular(15f));
        
        // Envolvemos el area en un scroll si el texto es muuy largo
        JScrollPane scrollSinopsis = new JScrollPane(txtSinopsisArea);
        scrollSinopsis.setBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1, true));
        scrollSinopsis.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollSinopsis.getVerticalScrollBar().setUI(new ScrollModernoUI()); 
        scrollSinopsis.getVerticalScrollBar().setPreferredSize(new Dimension(14, 0));
        cajaSinopsis.add(scrollSinopsis);
        
        // Agregamos ambas paginas al panel del formulario con sus separaciones
        pnlFormulario.add(caja1);
        pnlFormulario.add(Box.createVerticalStrut(20));
        pnlFormulario.add(cajaSinopsis);
        pnlFormulario.add(Box.createVerticalStrut(20));
        
        // -- BOTON DE GUARDADO --
        JButton btnGuardarFinal = UIUtils.crearBotonEstandar("GuardarLibro");
        btnGuardarFinal.setPreferredSize(new Dimension(150, 45));
        btnGuardarFinal.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Añadimos accion al boton cuando se da click
        btnGuardarFinal.addActionListener(e -> {
        	
        	String titulo = txtNombreLibro.getText().trim(); // .trim() le quita los espacios vacios al inicio y al final
        	String autor = txtAutor.getText().trim();
        	String pags = txtPaginas.getText().trim();
        	String sinopsis = txtSinopsisArea.getText().trim();
        	
        	// Filtro para no enviar categorias vacias ni repetidas
        	ArrayList<String> catsSeleccionadas = new ArrayList<String>();
        	String cat1 = comboGenero1.getSelectedItem() != null ? comboGenero1.getSelectedItem().toString().trim() : "";
        	String cat2 = comboGenero2.getSelectedItem() != null ? comboGenero2.getSelectedItem().toString().trim() : "";
        	
        	if (!cat1.isEmpty() && !cat1.equals("Sin especificar")) catsSeleccionadas.add(cat1);
        	if (!cat2.isEmpty() && !cat2.equals("Sin especificar") && !catsSeleccionadas.contains(cat2))  catsSeleccionadas.add(cat2);
        	
        	// Si falta algun dato, frena y avisa
        	if (titulo.isEmpty() || autor.isEmpty() || pags.isEmpty() || sinopsis.isEmpty() || catsSeleccionadas.isEmpty()) {
        		JOptionPane.showMessageDialog(this, "Completa los campos faltantes", "Faltan datos", JOptionPane.WARNING_MESSAGE);
        		return; // Detiene la ejecucion de esta funcion lambda (e ->)
        	}
        	
        	try {
        		// Pasar pags. a entero
        		int pagsInt = Integer.parseInt(pags);
        		
        		// Guardamos la portada
        		String rutaFinalImagen = FileUtils.guardarPortada(rutaImagenSeleccionada[0], titulo);
        		
        		// Instanciamos el nuevo Libro
        		// No nos corresponde asignar un ID, de eso se encarga el Main, mientras le llamamos n
        		Libro nuevoLibro = new Libro("n", titulo, catsSeleccionadas, rutaFinalImagen, autor, pagsInt, sinopsis);
        		
        		// Empujamos el objeto Libro al Main
        		// Consumer<Libro> es un cable de envio, esta vista no sabe como guardar el Libro en el archivo .dat
        		// solo se encarga de recopilar los datos, crear el objeto Libro y usar este Consumer para "empujarlo/inyectarlo"
        		// hacia el Main, que es quien realmente lo va a guardar
        		onGuardarLibro.accept(nuevoLibro);
        		
        	} catch(NumberFormatException ex) {
        		// Si el parseInt falla, atrapamos la excepcion
        		JOptionPane.showMessageDialog(this, "Las paginas deben tener un valor numerico entero", "Error", JOptionPane.ERROR_MESSAGE);        	
        	}
        });
        
        pnlFormulario.add(btnGuardarFinal);
        
        // Envolvemos todo es un ScrollPane
        JScrollPane scrollGeneral = new JScrollPane(pnlFormulario);
        scrollGeneral.setBorder(null);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        scrollGeneral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollGeneral.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
        scrollGeneral.getVerticalScrollBar().setUI(new ScrollModernoUI());
        
        pnlContenido.add(scrollGeneral, BorderLayout.CENTER);
        add(pnlContenido, BorderLayout.CENTER);
	}

}
