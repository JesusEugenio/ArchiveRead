package archiveread.ui;

import archiveread.modelos.Libro;
import archiveread.modelos.Usuario;
import archiveread.utils.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.function.Consumer;

// =============================================
// VistaMiBiblioteca
// Pantalla personal del usuario con sus libros rentados y guardados
// =============================================

public class VistaMiBiblioteca extends JPanel{

	public VistaMiBiblioteca(Usuario usuarioActual, ArrayList<Libro> librosRentados, ArrayList<Libro> librosGuardados,
							Runnable onVolverCatalogo,
							Consumer<Libro> onLibroSeleccionado,
							Consumer<Libro> onDevolverLibro,
							Consumer<Libro> onRentarLibro,
							Consumer<Libro> onQuitarLibro)
	{
		setLayout(new BorderLayout());
		setBackground(PaletaColores.FONDO_PRINCIPAL);
		
		// Barra de Navegacion (superior)
		JPanel pnlRuta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
		pnlRuta.setBackground(PaletaColores.FONDO_PRINCIPAL);
		pnlRuta.setBorder(new EmptyBorder(10, 40, 10, 0));
		
		pnlRuta.add(UIUtils.crearLabel("ArchiveRead  >  Mi Biblioteca  >  ", CargarFuente.REGULAR, 14f, Color.GRAY));
		
		JLabel lblAtras = UIUtils.crearLabel("Volver al Cátalogo", CargarFuente.REGULAR, 14f, PaletaColores.PRIMARIO);
		lblAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		lblAtras.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) { onVolverCatalogo.run(); }
		});
		pnlRuta.add(lblAtras);
		add(pnlRuta, BorderLayout.NORTH);
		
		// Contenedor Principal
		JPanel pnlContenido = new JPanel();
		pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));
		pnlContenido.setBackground(PaletaColores.FONDO_PRINCIPAL);
		pnlContenido.setBorder(new EmptyBorder(10, 40, 40, 40));
		
		// Titulo Principal
		JLabel lblTituloPrincipal = UIUtils.crearLabel("Mi biblioteca personal", CargarFuente.BOLD, 28f, PaletaColores.TEXTO_NEGRO);
		lblTituloPrincipal.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlContenido.add(lblTituloPrincipal);
		pnlContenido.add(Box.createVerticalStrut(30));
		
		// --- SECCION A: LIBROS RENTADOS ---
		JLabel lblTitulosRentados = UIUtils.crearLabel("Libros que estás leyendo (Rentados)", CargarFuente.BOLD, 18f, PaletaColores.TEXTO_NEGRO);
		lblTitulosRentados.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlContenido.add(lblTitulosRentados);
		pnlContenido.add(Box.createVerticalStrut(15));
		
		if(librosRentados.isEmpty()) {
			pnlContenido.add(UIUtils.crearEtiquetaVacia("No tienes libros rentados en este momento. "));
		}else {
			for(Libro l : librosRentados) {
				pnlContenido.add(crearFilaLibro(l, "RENTADO", onLibroSeleccionado, onDevolverLibro, null, null));
				pnlContenido.add(Box.createVerticalStrut(10));	
			}
		}
		pnlContenido.add(Box.createVerticalStrut(40));
		
		// --- SECCION B: MI LISTA DE DESEOS (GUARDADOS) ---
		JLabel lblTituloGuardados = UIUtils.crearLabel("Libros Guardados (Lista de deseos)", CargarFuente.BOLD, 18f, PaletaColores.TEXTO_NEGRO);
		lblTituloGuardados.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlContenido.add(lblTituloGuardados);
		pnlContenido.add(Box.createVerticalStrut(15));
		
		if(librosGuardados.isEmpty()) {
			pnlContenido.add(UIUtils.crearEtiquetaVacia("Aún no has guardado libros a tu biblioteca. "));
		}else {
			for(Libro l : librosGuardados) {
				pnlContenido.add(crearFilaLibro(l, "GUARDADO", onLibroSeleccionado, null, onRentarLibro, onQuitarLibro));
				pnlContenido.add(Box.createVerticalStrut(10));	
			}
		}
		
		// Se envuelve todo en un scroll
		JScrollPane scrollGeneral = new JScrollPane(pnlContenido);
		scrollGeneral.setBorder(null);
		scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
		scrollGeneral.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
		scrollGeneral.getVerticalScrollBar().setUI(new ScrollModernoUI());
		scrollGeneral.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
		
		add(scrollGeneral, BorderLayout.CENTER);
	}
	
	// Constructor de Filas
	private JPanel crearFilaLibro(Libro libro, String tipoFila, Consumer<Libro> onSeleccionado,
									Consumer<Libro> onDevolver, Consumer<Libro> onRentar, Consumer<Libro> onQuitar) {
		JPanel fila = new JPanel(new BorderLayout(20, 0));
		fila.setBackground(Color.WHITE);
		fila.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(PaletaColores.BORDE_CLARO, 1),
				new EmptyBorder(15, 15, 15, 20)
				));
		
		// Se limita la altura para que no se estire de más
		fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
		fila.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// Portada
		ImageIcon iconoOriginal = new ImageIcon(libro.getRutaImagen());
		Image imgEscalada = iconoOriginal.getImage().getScaledInstance(70, 105, Image.SCALE_SMOOTH);
		JLabel lblPortada = new JLabel(new ImageIcon(imgEscalada));
		lblPortada.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		lblPortada.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) { onSeleccionado.accept(libro); }
		});
		fila.add(lblPortada, BorderLayout.WEST);
		
		// Informacion del Libro
		JPanel pnlInfo = new JPanel();
		pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
		pnlInfo.setBackground(Color.WHITE);
		pnlInfo.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		JLabel lblTitulo = UIUtils.crearLabel(libro.getTitulo(), CargarFuente.BOLD, 18f, PaletaColores.TEXTO_NEGRO);
		JLabel lblAutor = UIUtils.crearLabel("Autor: " + libro.getAutor(), CargarFuente.REGULAR, 13f, Color.GRAY);
		
		pnlInfo.add(lblTitulo);
		pnlInfo.add(Box.createVerticalStrut(15));
		pnlInfo.add(lblAutor);
		fila.add(pnlInfo, BorderLayout.CENTER);
		
		// Panel de Botones
		JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 35));
		pnlBotones.setBackground(Color.WHITE);
		
		// Se evalua que tipo de fila es para saber que dibujar
		if(tipoFila.equals("RENTADO") && onDevolver != null) {
			
			//Boton Devolver
			JButton btnDevolver = UIUtils.crearBotonEstandar("Devolver Libro", PaletaColores.BOTON_DEVOLVER, Color.WHITE);
			btnDevolver.addActionListener(e -> onDevolver.accept(libro));
			pnlBotones.add(btnDevolver);
		
		}else if(tipoFila.equals("GUARDADO") && onRentar != null && onQuitar != null) {
			
			//Boton Rentar
			JButton btnRentar = UIUtils.crearBotonEstandar("Rentar Ahora", PaletaColores.PRIMARIO, Color.WHITE);
			
			// Solo se puede habilitar si el libro no esta rentado a otra persona
			if(!libro.isDisponible()) {
				btnRentar.setEnabled(false);
				btnRentar.setText("No Disponible");
				btnRentar.setBackground(PaletaColores.BOTON_DESHABILITADO);
			}else {
				btnRentar.addActionListener(e -> onRentar.accept(libro));
			}
			pnlBotones.add(btnRentar);
			
			// Boton Quitar
			JButton btnQuitar = UIUtils.crearBotonEstandar("Quitar", PaletaColores.BOTON_QUITAR, PaletaColores.TEXTO_NEGRO);
			btnQuitar.addActionListener(e -> onQuitar.accept(libro));
			pnlBotones.add(btnQuitar);
		}
		
		fila.add(pnlBotones, BorderLayout.EAST);
		return fila;
	}
}
