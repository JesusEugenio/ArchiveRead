package archiveread.ui;

import archiveread.modelos.Libro;
import archiveread.utils.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.util.ArrayList;

public class DialogoEditarLibro extends JDialog {
	
	private Libro libroEdicion;
	private String rutaImagenActual;
	
	public DialogoEditarLibro(Frame parent, Libro libro, Runnable onGuardar) {
		super(parent, "Editar Libro", true); // true bloquea la ventana detras de este dialogo
		this.libroEdicion = libro;
		this.rutaImagenActual = libro.getRutaImagen();
		
		setSize(750, 650);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout());
		getContentPane().setBackground(PaletaColores.FONDO_PRINCIPAL);
		
		// Panel Izquierdo ---> Portada del Libro
		JPanel pnlIzquierda = new JPanel();
		pnlIzquierda.setLayout(new BoxLayout(pnlIzquierda, BoxLayout.Y_AXIS));
		pnlIzquierda.setBackground(PaletaColores.FONDO_PRINCIPAL);
		pnlIzquierda.setBorder(new EmptyBorder(30, 30, 30, 30));
		pnlIzquierda.setPreferredSize(new Dimension(250, 0));
		
		JLabel lblTituloPortada = UIUtils.crearLabel("Portada del Libro", CargarFuente.BOLD, 16f, PaletaColores.TEXTO_NEGRO);
		lblTituloPortada.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTituloPortada.setBorder(new EmptyBorder(0, 0, 20, 0));
		
		// Caja / Area de portada
		JPanel pnlCajaPortada = new JPanel(new BorderLayout());
		pnlCajaPortada.setBackground(PaletaColores.BLANCO);
		pnlCajaPortada.setBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1));
		pnlCajaPortada.setMaximumSize(new Dimension(180, 260));
		pnlCajaPortada.setPreferredSize(new Dimension(180, 260));
		pnlCajaPortada.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel lblPreview = new JLabel();
		lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
		actualizarPreviewImagen(lblPreview, rutaImagenActual); 	// Cargamos la imagen inicial
		pnlCajaPortada.add(lblPreview, BorderLayout.CENTER);
		
		// Boton para seleccionar una nueca imagen desde el explador de archivos
		JButton btnCambiarPortada = new JButton("Cambiar Portada");
		btnCambiarPortada.setFont(CargarFuente.getRegular(12f));
		btnCambiarPortada.setBackground(PaletaColores.BLANCO);
		btnCambiarPortada.setFocusPainted(false);
		btnCambiarPortada.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		btnCambiarPortada.addActionListener(e -> {
			// Abrimos el selector de archivos nativo del sistema operativo
			FileDialog fd = new FileDialog(parent, "Seleccionar Portada", FileDialog.LOAD);
			fd.setFile("*.jpg; *.jpeg; *.png;"); // Filtro de extensiones permitidas
			fd.setVisible(true);
			
			String directorio = fd.getDirectory();
			String nombreArchivo = fd.getFile();
			
			// Si el usuario selecciono un archivo valido, actualizamos la ruta y la vista previa
			if(directorio != null && nombreArchivo != null) {
				rutaImagenActual = directorio + nombreArchivo;
				actualizarPreviewImagen(lblPreview, rutaImagenActual); 
			}
		});
		
		// Ensamblamos el panel izquierdo
		pnlIzquierda.add(lblTituloPortada);
		pnlIzquierda.add(pnlCajaPortada);
		pnlIzquierda.add(Box.createVerticalStrut(20)); // Espacio separador
		pnlIzquierda.add(btnCambiarPortada);
		
		// Panel Derecho --> Formulario para cambiar parametros del libro
		JPanel pnlDerecha = new JPanel(new BorderLayout());
		pnlDerecha.setBackground(PaletaColores.BLANCO);
		pnlDerecha.setBorder(new EmptyBorder(30, 30, 30, 30));
		
		JLabel lblTituloFormulario = UIUtils.crearLabel("Editar Datos del Libro", CargarFuente.BOLD, 22f, PaletaColores.TEXTO_NEGRO);
		lblTituloFormulario.setBorder(new EmptyBorder(0, 0, 30, 0));
		pnlDerecha.add(lblTituloFormulario, BorderLayout.NORTH);
		
		
		// Configuramos un GridBagLayout para alinear los textos y las cajas de texto
		JPanel pnlForm = new JPanel(new GridBagLayout());
		pnlForm.setBackground(PaletaColores.BLANCO);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL; // Las cajas de texto se estiraran horizontalmente
		gbc.insets = new Insets(0,  0,  15, 0); // Margen para cada celda
		gbc.anchor = GridBagConstraints.WEST; // Alineado a la izquierda
		
		int fila = 0; 	// Controlamos la fila actual en la "cuadricula" que creamos
		
		// Creamos las filas de texto 
		JTextField txtTitulo = agregarFilaFormulario(pnlForm, "Titulo:", libro.getTitulo(), gbc, fila++);
		JTextField txtAutor = agregarFilaFormulario(pnlForm, "Autor:", libro.getAutor(), gbc, fila++);
		JTextField txtCategorias = agregarFilaFormulario(pnlForm, "Categorias: ", String.join(", ", libro.getCategorias()), gbc, fila++);
		
		// Fila para el numero de paginas (creada manualmente por que sera mas pequeña
		gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.0;
		pnlForm.add(UIUtils.crearLabel("Paginas:", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO), gbc);
		gbc.gridx = 1; gbc.weightx = 1.0;
		JTextField txtPaginas = UIUtils.crearTextFieldFormulario();
		txtPaginas.setText(String.valueOf(libro.getPaginas()));
		txtPaginas.setPreferredSize(new Dimension(100, 35));
		
		// Envolvemos el campo de paginas en un FlowLayout para evitar que el gbc lo termine estirando
		JPanel wrapPaginas = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		wrapPaginas.setBackground(PaletaColores.BLANCO);
		wrapPaginas.add(txtPaginas);
		pnlForm.add(wrapPaginas, gbc);
		fila++;
		
		// Campo de sinopsis (Ocupara 2 columnas de ancho)
		gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2; // gridwidth = 2 hace que ocupe toda la fila
		gbc.insets = new Insets(10, 0, 5, 0);
		pnlForm.add(UIUtils.crearLabel("Sinopsis:", CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO), gbc);
		fila++;
		
		JTextArea txtSinopsis = new JTextArea(libro.getSinopsis());
		txtSinopsis.setLineWrap(true);	// Salto de linea automatico
		txtSinopsis.setWrapStyleWord(true); // Respeta palabras completas al saltar
		txtSinopsis.setFont(CargarFuente.getRegular(14f));
		txtSinopsis.setBorder(new EmptyBorder(10, 10, 10, 10));
		txtSinopsis.setMinimumSize(new Dimension(0, 0));
		
		// Scroll para deslizar la sinopsis en caso de ser un texto largo
		JScrollPane scrollSinopsis = new JScrollPane(txtSinopsis);
		scrollSinopsis.setBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1));
		scrollSinopsis.getVerticalScrollBar().setUI(new ScrollModernoUI());
		scrollSinopsis.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
		scrollSinopsis.setPreferredSize(new Dimension(350, 150));
		
		gbc.gridy = fila;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 5, 10); // Limpiamos el margen de la ultima celda
		
		pnlForm.add(scrollSinopsis, gbc);
		
		// Envolvemos todo el editor/formulario en un BorderLayout para mantenerse compacto
		JPanel wrapperFormulario = new JPanel(new BorderLayout());
		wrapperFormulario.setBackground(PaletaColores.BLANCO);
		wrapperFormulario.add(pnlForm, BorderLayout.NORTH);
		
		pnlDerecha.add(wrapperFormulario, BorderLayout.CENTER);
		
		// Panel Inferior --> Botones de Guardar y Cancelar edicion
		JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
		pnlBotones.setBackground(PaletaColores.BORDE_CLARO);
		pnlBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PaletaColores.BORDE_CLARO)); // Una linea separadora
		
		JButton btnCancelar = UIUtils.crearBotonEstandar("Cancelar", PaletaColores.BLANCO, PaletaColores.TEXTO_NEGRO);
		btnCancelar.setPreferredSize(new Dimension(120, 40));
		btnCancelar.addActionListener(e -> { dispose(); }); // Cierra la ventana sin hacer nada
		
		JButton btnGuardar = UIUtils.crearBotonEstandar("Guardar Cambios");
		btnGuardar.setPreferredSize(new Dimension(160, 40));
		
		// Logica de guardado y actualizacion
		btnGuardar.addActionListener(e -> {
			try {
				// Evitamos campos vacios
				if (txtTitulo.getText().trim().isEmpty() || txtAutor.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(this, "El Titulo y Autor no pueden estar vacios!", "Atencion", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				// Intentamos convertir las paginas a int
				int pags = Integer.parseInt(txtPaginas.getText().trim());
				
				// Actualizamos las variables del Libro
				libroEdicion.setTitulo(txtTitulo.getText().trim());
				libroEdicion.setAutor(txtAutor.getText().trim());
				libroEdicion.setPaginas(pags);
				libroEdicion.setSinopsis(txtSinopsis.getText().trim());
				
				// Procesamos las categorias editadas, separamos el texto por comas y limpiamos espacios
				String[] catsArray = txtCategorias.getText().split(",");
				ArrayList<String> nuevasCategorias = new ArrayList<>();
				for (String c : catsArray) {
					if (!c.trim().isEmpty()) nuevasCategorias.add(c.trim()); // Evitamos que agregue categorias vacias
				}
				
				// Si borraron todas las categorias, asignamos una por defecto
				if (nuevasCategorias.isEmpty()) nuevasCategorias.add("Sin especificar");
				libroEdicion.setCategorias(nuevasCategorias);
				
				// Copiamos la nueva portada a la carpeta covers 
				String rutaFinal = FileUtils.guardarPortada(rutaImagenActual, libroEdicion.getIdLibro());
				libroEdicion.setRutaImagen(rutaFinal);
				
				// Ejecutamos la accion que el Main tiene que manejar al presionar el btnGuardar
				onGuardar.run();
				dispose(); // Cerramos el dialogo / miniventana
				
			} catch (NumberFormatException ex) {
				// Si el usuario escribio letras en el campo de paginas, atrapar la excepcion y mostrar el error
				JOptionPane.showMessageDialog(this, "El numero de paginas debe ser numerico", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		pnlBotones.add(btnCancelar);
		pnlBotones.add(btnGuardar);
		
		// Ensamblaje de todos los paneles
		add(pnlIzquierda, BorderLayout.WEST);
		add(pnlDerecha, BorderLayout.CENTER);
		add(pnlBotones, BorderLayout.SOUTH);
				
	}
		// -- Metodos utilitarios de esta clase
		
		// Crear una fila en el GridBagLayout con un Label y un TextFiedl
		private JTextField agregarFilaFormulario(JPanel panel, String label, String valorInicial, GridBagConstraints gbc, int fila) {
			// Configuramos la Columna 0 --> Texto 
			gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.0;
			panel.add(UIUtils.crearLabel(label, CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_NEGRO), gbc);
			
			// Configuramos la Columna 1 --> Campo de Texto
			gbc.gridx = 1; gbc.weightx = 1.0; // weightx le permite estirar el campo de texto todo lo posible
			JTextField txtCampo = UIUtils.crearTextFieldFormulario();
			txtCampo.setText(valorInicial);
			txtCampo.setPreferredSize(new Dimension(300, 35));
			
			panel.add(txtCampo, gbc);
			
			return txtCampo;
		}	
		
		// Para actualizar la imagen que estemos cargando en este editor
		private void actualizarPreviewImagen(JLabel label, String ruta) {
			ImageIcon icon = new ImageIcon(ruta);
			Image img = icon.getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
			label.setIcon(new ImageIcon(img));
					
		}
		
}