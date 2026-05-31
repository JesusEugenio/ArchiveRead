package archiveread.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.Utilities;

import archiveread.modelos.*;
import archiveread.utils.*;

import java.util.function.BiConsumer;

public class VistaDetalleLibro extends JPanel {
	
	public VistaDetalleLibro(Libro libro, Usuario usuarioActual, Runnable onVolverCatalogo, Runnable onRentarLibro, 
							Runnable onDevolverLibro, Runnable onToggleGuardar, BiConsumer<JPanel, Libro> cargarReviewsAction) {
		
		setLayout(new BorderLayout());
		setBackground(PaletaColores.FONDO_PRINCIPAL);
		
		// ================================================
    	// Barra de navegacion superior 
    	// ================================================
    	JPanel pnlRuta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
    	pnlRuta.setBackground(PaletaColores.FONDO_PRINCIPAL);
    	pnlRuta.setBorder(new EmptyBorder(5, 40, 0, 0));
    	
    	String catsUnidas = String.join(", ", libro.getCategorias());
    	pnlRuta.add(UIUtils.crearLabel("ArchiveRead  > " + catsUnidas + "  > ", CargarFuente.REGULAR, 14f, Color.GRAY));
    	
    	// Boton para volver al menu principal
    	JLabel lblAtras = UIUtils.crearLabel("Volver al Catalogo", CargarFuente.REGULAR, 14f, PaletaColores.PRIMARIO);
    	lblAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	lblAtras.addMouseListener(new java.awt.event.MouseAdapter() {
        	@Override public void mouseClicked(java.awt.event.MouseEvent e) { onVolverCatalogo.run(); }
        });
    	pnlRuta.add(lblAtras);
    	add(pnlRuta, BorderLayout.NORTH);
		
    	// ================================================================
    	// ===  Panel que agrupa todo el banner e informacion del libro ===
    	// ================================================================
    	JPanel pnlContenidoCentral = new JPanel(new BorderLayout());
        pnlContenidoCentral.setBackground(PaletaColores.FONDO_PRINCIPAL);
    	
        // Panel del banner del libro
    	JPanel pnlBannerOscuro = new JPanel(new BorderLayout(30,0));
    	pnlBannerOscuro.setBackground(PaletaColores.FONDO_BANNER_OSCURO);
    	pnlBannerOscuro.setBorder(new EmptyBorder(40, 40, 40, 40));
    	
    	// Cargar Portada
    	ImageIcon iconoPortada = new ImageIcon(libro.getRutaImagen());
    	Image imgEscalada = iconoPortada.getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
    	JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
    	lblImagen.setVerticalAlignment(SwingConstants.TOP);
    	pnlBannerOscuro.add(lblImagen, BorderLayout.WEST);
    	
    	// Panel para agregar Titulo y categorias
    	JPanel pnlInfoBanner = new JPanel();
    	pnlInfoBanner.setLayout(new BoxLayout(pnlInfoBanner, BoxLayout.Y_AXIS));
    	pnlInfoBanner.setOpaque(false);
    	
    	// Titulo
    	JLabel lblTitulo = UIUtils.crearLabel(libro.getTitulo(), CargarFuente.BOLD, 32f, PaletaColores.TEXTO_BLANCO);
    	lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    	lblTitulo.setBorder(new EmptyBorder(15, 0, 10, 0));
    	
    	// Evaluar disponibilidad
    	String textoDisponibilidad = libro.isDisponible() ? "Disponible" : "Ocupado";
    	JLabel lblStats = UIUtils.crearLabel(" " + libro.getPaginas() + " Paginas   |   " + textoDisponibilidad, CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_GRIS_CLARO);
    	lblStats.setAlignmentX(Component.LEFT_ALIGNMENT);
    	lblTitulo.setBorder(new EmptyBorder(0, 0, 35, 0));
    	
    	// Panel para botones rentar y añadir a biblioteca
    	JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    	pnlBotones.setOpaque(false);
    	pnlBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	// Boton de Rentar / Prestado 
    	JButton btnRentar = UIUtils.crearBotonEstandar("Rentar");
    	btnRentar.setPreferredSize(new Dimension(150, 45));
    	
    	if (libro.isDisponible()) {
            btnRentar.addActionListener(e -> onRentarLibro.run());
            
        } else if (usuarioActual != null && usuarioActual.getMatricula().equals(libro.getMatriculaPrestamo())) {
            btnRentar.setText("Devolver");
            btnRentar.setBackground(Color.DARK_GRAY);
            btnRentar.setForeground(Color.WHITE);
            btnRentar.addActionListener(e -> onDevolverLibro.run());
            
        } else {
            btnRentar.setText("Prestado");
            btnRentar.setBackground(PaletaColores.BOTON_DESHABILITADO);
            btnRentar.setForeground(PaletaColores.TEXTO_GRIS_CLARO);
            btnRentar.setEnabled(false);
        }
    	
    	// Boton de Añadir a Biblioteca / Guardado
    	boolean estaGuardado = (usuarioActual != null) && libro.estaGuardado(usuarioActual.getMatricula());
        JButton btnGuardar = UIUtils.crearBotonEstandar(estaGuardado ? "En Biblioteca" : "Añadir a Biblioteca");
        btnGuardar.setPreferredSize(new Dimension(150, 45));
        btnGuardar.setBackground(estaGuardado ? PaletaColores.BLANCO : PaletaColores.BOTON_OSCURO);
        btnGuardar.setForeground(estaGuardado ? PaletaColores.PRIMARIO : PaletaColores.BLANCO);
        btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        btnGuardar.addActionListener(e -> onToggleGuardar.run());  	
       
        // Agregar los botones al panel
        pnlBotones.add(btnRentar);
        pnlBotones.add(Box.createHorizontalStrut(15));
        pnlBotones.add(btnGuardar);
        
        pnlInfoBanner.add(lblTitulo);
        pnlInfoBanner.add(lblStats);
        pnlInfoBanner.add(Box.createVerticalGlue()); 
        pnlInfoBanner.add(pnlBotones);
        pnlBannerOscuro.add(pnlInfoBanner, BorderLayout.CENTER);
        pnlContenidoCentral.add(pnlBannerOscuro, BorderLayout.NORTH);
        
        // ================================================
        // === Seccion para agregar Sinopsis y Reviews ====
        // ================================================
        JPanel pnlInferior = new JPanel(new BorderLayout());
        pnlInferior.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlInferior.setBorder(new EmptyBorder(30, 40, 40, 40));
        
        // Dividimos sinopsis y reviews en dos pestañas con el card layout
        JPanel pnlIzquierda = new JPanel(new BorderLayout());
        pnlIzquierda.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        pnlTabs.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlTabs.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblTabSinopsis = UIUtils.crearLabel("Sinopsis" , CargarFuente.BOLD, 18f, Color.BLACK);
        lblTabSinopsis.setBorder(BorderFactory.createMatteBorder(0, 0, 4 ,0 , PaletaColores.PRIMARIO));
        lblTabSinopsis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblTabReviews = UIUtils.crearLabel("Reviews" , CargarFuente.BOLD, 18f, Color.GRAY);
        lblTabReviews.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        lblTabReviews.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        pnlTabs.add(lblTabSinopsis);
        pnlTabs.add(lblTabReviews);
        
        JPanel pnlCards = new JPanel(new CardLayout());
        pnlCards.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        // Pestaña 1: Sinopsis
        JPanel cardSinopsis = new JPanel(new BorderLayout(0, 10));
        cardSinopsis.setBackground(PaletaColores.FONDO_PRINCIPAL);
        JTextArea txtSinopsis = new JTextArea(libro.getSinopsis());
        txtSinopsis.setWrapStyleWord(true); 
        txtSinopsis.setLineWrap(true);	
        txtSinopsis.setEditable(false);		
        txtSinopsis.setOpaque(false); 		
        txtSinopsis.setFont(CargarFuente.get(CargarFuente.REGULAR, 15f));
        txtSinopsis.setForeground(PaletaColores.TEXTO_GRIS_OSCURO); 
        cardSinopsis.add(txtSinopsis, BorderLayout.NORTH);
        
        // Pestaña 2: Reviews 
        JPanel cardReviews = new JPanel(new BorderLayout(0, 10));
        cardReviews.setBackground(PaletaColores.FONDO_PRINCIPAL);
        JPanel listaReviews = new JPanel();
        listaReviews.setLayout(new BoxLayout(listaReviews, BoxLayout.Y_AXIS));
        listaReviews.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        // Como la UI no puede leer el .txt, enviamos nuestro panel vacio (lista reviews)
        // y nuestro libro al Main, para que el Gestor los lea y nos devuelva el panel relleno con las reseñas
        cargarReviewsAction.accept(listaReviews, libro);
        
        // Envolvemos las Reviews en un Scroll en caso de ser muchas
        JScrollPane scrollReviews = new JScrollPane(listaReviews);
        scrollReviews.setBorder(null);
        scrollReviews.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
        cardReviews.add(scrollReviews, BorderLayout.CENTER);
        
        pnlCards.add(cardSinopsis, "SINOPSIS");
        pnlCards.add(cardReviews, "REVIEWS");
        pnlIzquierda.add(pnlTabs, BorderLayout.NORTH);
        pnlIzquierda.add(pnlCards, BorderLayout.CENTER);
        
        // Para poder cambiar entre pestañas
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        
        // Al presionar en el texto de Sinopsis
        lblTabSinopsis.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                cl.show(pnlCards, "SINOPSIS");
                lblTabSinopsis.setForeground(Color.BLACK);
                lblTabSinopsis.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, PaletaColores.PRIMARIO));
                lblTabReviews.setForeground(Color.GRAY);
                lblTabReviews.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            }
        });
        
        lblTabReviews.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                cl.show(pnlCards, "REVIEWS");
                lblTabReviews.setForeground(Color.BLACK);
                lblTabReviews.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, PaletaColores.PRIMARIO));
                lblTabSinopsis.setForeground(Color.GRAY);
                lblTabSinopsis.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            }
        });
        pnlInferior.add(pnlIzquierda, BorderLayout.CENTER);
        
        // ================================================
        // Ficha Tecnica del libro y autor
        // ================================================
        JPanel pnlDerecha = new JPanel();
        pnlDerecha.setLayout(new BoxLayout(pnlDerecha, BoxLayout.Y_AXIS));
        pnlDerecha.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlDerecha.setPreferredSize(new Dimension(320, 400));
        
        // Encabezado del autor
        JLabel lblAutorTitulo = UIUtils.crearLabel("Autor", CargarFuente.BOLD, 18f, PaletaColores.TEXTO_NEGRO);
        lblAutorTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAutorTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        JPanel pnlCajaAutor = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pnlCajaAutor.setBackground(new Color(240, 240, 240));
        pnlCajaAutor.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        pnlCajaAutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCajaAutor.setMaximumSize(new Dimension(320, 69));
        pnlCajaAutor.add(UIUtils.crearLabel(libro.getAutor(), CargarFuente.BOLD, 14f, PaletaColores.TEXTO_NEGRO));
        
        // Seccion "Acerca de este libro"
        JLabel lblAcercaTitulo = UIUtils.crearLabel("Acerca de este libro", CargarFuente.BOLD, 18f, PaletaColores.TEXTO_NEGRO);
        lblAcercaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAcercaTitulo.setBorder(new EmptyBorder(30, 0, 10, 0));
        
        // Creamos la tabla donde esta la ficha tecnica
        JPanel pnlFichaInfo = new JPanel(new GridLayout(3, 2, 10, 15));
        pnlFichaInfo.setBackground(PaletaColores.BLANCO);
        
        // Le añadimos un border grisaceo a la tabla
        pnlFichaInfo.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(15, 15, 15, 15)));
        pnlFichaInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFichaInfo.setMaximumSize(new Dimension(320, 140));
        
        // Asignamos el contenido a la tabla
        pnlFichaInfo.add(UIUtils.crearLabel("Estado:", CargarFuente.REGULAR, 13f, Color.GRAY));
        String txtEstadoFicha = libro.isDisponible() ? "Disponible" : "En prestamo";
        Color colEstadoFicha = libro.isDisponible() ? new Color(39, 174, 96) : Color.RED;
        pnlFichaInfo.add(UIUtils.crearLabel(txtEstadoFicha, CargarFuente.BOLD, 13f, colEstadoFicha));
        
        pnlFichaInfo.add(UIUtils.crearLabel("Paginas:", CargarFuente.REGULAR, 13f, Color.GRAY));
        pnlFichaInfo.add(UIUtils.crearLabel(String.valueOf(libro.getPaginas()), CargarFuente.REGULAR, 13f, Color.BLACK));
        
        pnlFichaInfo.add(UIUtils.crearLabel("Categorias:", CargarFuente.REGULAR, 13f, Color.GRAY));
        pnlFichaInfo.add(UIUtils.crearLabel(libro.getCategoria(), CargarFuente.REGULAR, 13f, Color.BLACK));
        
        pnlDerecha.add(lblAutorTitulo);
        pnlDerecha.add(pnlCajaAutor);
        pnlDerecha.add(pnlFichaInfo);
        pnlInferior.add(pnlDerecha, BorderLayout.EAST);
        
        // Agrupamos los paneles verticales
        JPanel pnlAgrupadorVertical = new JPanel(new BorderLayout());
        pnlAgrupadorVertical.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlAgrupadorVertical.add(pnlInferior, BorderLayout.CENTER);
        pnlContenidoCentral.add(pnlAgrupadorVertical, BorderLayout.CENTER);
        
        // Envolvemos el contenido central en un scroll
        JScrollPane scrollGeneralDetalles = new JScrollPane(pnlContenidoCentral);
        scrollGeneralDetalles.setBorder(null);
        scrollGeneralDetalles.getVerticalScrollBar().setUnitIncrement(16); 
        scrollGeneralDetalles.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollGeneralDetalles.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        // Forzamos el scroll hacia arriba (que siempre empiece ahi)
        SwingUtilities.invokeLater(() -> scrollGeneralDetalles.getVerticalScrollBar().setValue(0));
        add(scrollGeneralDetalles, BorderLayout.CENTER);
        
	}       

}
