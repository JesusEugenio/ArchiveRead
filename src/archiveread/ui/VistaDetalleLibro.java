package archiveread.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import archiveread.modelos.*;
import archiveread.utils.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer; 
import javax.swing.JOptionPane;

// =============================================
// VistaDetalleLibro
// Pantalla a detalle de un libro con opciones de préstamo, guardado y reseñas
// =============================================

public class VistaDetalleLibro extends JPanel {
	
	public VistaDetalleLibro(Libro libro, Usuario usuarioActual, Runnable onVolverCatalogo, Runnable onRentarLibro, 
							Runnable onDevolverLibro, Runnable onToggleGuardar, BiConsumer<JPanel, Libro> cargarReviewsAction,
							Consumer<String> onGuardarReview, boolean abrirOnReviews, Runnable onEditarLibro, Runnable onEliminar) {
		
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
    	// Panel que agrupa todo el banner e informacion del libro 
    	// ================================================================
    	JPanel pnlContenidoCentral = new JPanel(new BorderLayout());
        pnlContenidoCentral.setBackground(PaletaColores.FONDO_PRINCIPAL);
    	
        // Panel del banner del libro
    	JPanel pnlBannerOscuro = new JPanel(new BorderLayout(30,0));
    	pnlBannerOscuro.setBackground(PaletaColores.FONDO_BANNER_OSCURO);
    	pnlBannerOscuro.setBorder(new EmptyBorder(40, 40, 40, 40));
    	
    	// Cargar Portada
    	JLabel lblImagen = new JLabel(UIUtils.escalarImagenAltaCalidad(libro.getRutaImagen(), 180, 260));;
    	lblImagen.setVerticalAlignment(SwingConstants.TOP);
    	pnlBannerOscuro.add(lblImagen, BorderLayout.WEST);
    	
    	// Panel para agregar Titulo y categorias
    	JPanel pnlInfoBanner = new JPanel();
    	pnlInfoBanner.setLayout(new BoxLayout(pnlInfoBanner, BoxLayout.Y_AXIS));
    	pnlInfoBanner.setOpaque(false);
    	
    	// Titulo
    	JLabel lblTitulo = UIUtils.crearLabel(libro.getTitulo(), CargarFuente.BOLD, 32f, PaletaColores.TEXTO_BLANCO);
    	lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	// Evaluar disponibilidad
    	String textoDisponibilidad = libro.isDisponible() ? "Disponible" : "Ocupado";
    	JLabel lblStats = UIUtils.crearLabel(" " + libro.getPaginas() + " Paginas   |   " + textoDisponibilidad, CargarFuente.REGULAR, 14f, PaletaColores.TEXTO_GRIS_CLARO);
    	lblStats.setAlignmentX(Component.LEFT_ALIGNMENT);
    	lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
    	
    	// Panel para botones rentar y añadir a biblioteca
    	JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    	pnlBotones.setOpaque(false);
    	pnlBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	// Boton de Rentar / Prestado 
    	JButton btnRentar = UIUtils.crearBotonEstandar("Rentar");
    	btnRentar.setPreferredSize(new Dimension(180, 45));
    	
    	if (libro.isDisponible()) {
    		// El libro está libre para rentar
            btnRentar.addActionListener(e -> onRentarLibro.run());
            
        } else if (usuarioActual != null && usuarioActual.getMatricula().equals(libro.getMatriculaPrestamo())) {
        	// El libro NO está libre y el dueño actual del préstamo es el usuario que mira la pantalla
            btnRentar.setText("Devolver");
            btnRentar.setBackground(Color.DARK_GRAY);
            btnRentar.setForeground(Color.WHITE);
            btnRentar.addActionListener(e -> onDevolverLibro.run());
            
        } else {
        	// El libro NO está libre y el usuario no lo tiene
            btnRentar.setText("Prestado");
            btnRentar.setBackground(PaletaColores.BOTON_DESHABILITADO);
            btnRentar.setForeground(PaletaColores.TEXTO_GRIS_CLARO);
            btnRentar.setEnabled(false);
        }
    	
    	// Boton de Añadir a Biblioteca / Guardado
    	boolean estaGuardado = (usuarioActual != null) && libro.estaGuardado(usuarioActual.getMatricula());
    	
    	// Verificamos si este usuario en específico es quien tiene rentado el libro
        boolean loTieneRentado = (usuarioActual != null) && usuarioActual.getMatricula().equals(libro.getMatriculaPrestamo());
        
        JButton btnGuardar = UIUtils.crearBotonEstandar("Añadir a Biblioteca");
        btnGuardar.setPreferredSize(new Dimension(180, 45));

        if (loTieneRentado) {
            // Si ya lo tiene rentado, bloqueamos la opcion de Añadir a Biblioteca
            btnGuardar.setText("En Lectura"); 
            btnGuardar.setBackground(PaletaColores.BOTON_DESHABILITADO);
            btnGuardar.setForeground(PaletaColores.TEXTO_GRIS_CLARO);
            btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            btnGuardar.setEnabled(false); // Apagamos el botón por completo
            
        } else {
            // Comportamiento normal (si no lo tiene rentado)
            btnGuardar.setText(estaGuardado ? "En Biblioteca" : "Añadir a Biblioteca");
            btnGuardar.setBackground(estaGuardado ? PaletaColores.BLANCO : PaletaColores.BOTON_OSCURO);
            btnGuardar.setForeground(estaGuardado ? PaletaColores.PRIMARIO : PaletaColores.BLANCO);
            btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            btnGuardar.addActionListener(e -> onToggleGuardar.run());
        }
        
        // Agregar los botones al panel
        pnlBotones.add(btnRentar);
        pnlBotones.add(Box.createHorizontalStrut(15));
        pnlBotones.add(btnGuardar);  	
        
        // Boton editar y eliminar (Solo para Administradores)
        if (usuarioActual instanceof Administrador) {
        	// Boton Editar 
        	JButton btnEditar = UIUtils.crearBotonEstandar("Editar Libro", PaletaColores.BOTON_EDITAR, PaletaColores.TEXTO_BLANCO);
        	btnEditar.setPreferredSize(new Dimension(110, 45));
        	btnEditar.addActionListener(e -> onEditarLibro.run());
        	pnlBotones.add(Box.createHorizontalStrut(15));
        	pnlBotones.add(btnEditar);
        	
        	// Boton Eliminar
        	JButton btnEliminar = UIUtils.crearBotonEstandar("Eliminar", PaletaColores.BOTON_ELIMINAR, PaletaColores.TEXTO_BLANCO);
        	btnEliminar.setPreferredSize(new Dimension(110, 45));
        	btnEliminar.addActionListener(e -> onEliminar.run()); 
        	pnlBotones.add(Box.createHorizontalStrut(15));
        	pnlBotones.add(btnEliminar);
        }
        
        pnlInfoBanner.add(Box.createVerticalStrut(40));
        pnlInfoBanner.add(lblTitulo);
        pnlInfoBanner.add(lblStats);
        pnlInfoBanner.add(Box.createVerticalGlue()); 
        pnlInfoBanner.add(pnlBotones);
        pnlBannerOscuro.add(pnlInfoBanner, BorderLayout.CENTER);
        pnlContenidoCentral.add(pnlBannerOscuro, BorderLayout.NORTH);
        
        // ================================================
        // Seccion para agregar Sinopsis y Reviews 
        // ================================================
        JPanel pnlInferior = new JPanel(new BorderLayout());
        pnlInferior.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlInferior.setBorder(new EmptyBorder(30, 40, 40, 40));
        
        // Dividimos sinopsis y reviews en dos pestañas con el card layout
        JPanel pnlIzquierda = new JPanel(new BorderLayout());
        pnlIzquierda.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlIzquierda.setMinimumSize(new Dimension(400, 300)); 
        pnlIzquierda.setBorder(new EmptyBorder(0, 0, 0, 40));
        
        pnlIzquierda.setBorder(new EmptyBorder(0, 0, 0, 40));
        JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTabs.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlTabs.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblTabSinopsis = UIUtils.crearLabel("Sinopsis" , CargarFuente.BOLD, 18f, Color.BLACK);
        lblTabSinopsis.setBorder(BorderFactory.createMatteBorder(0, 0, 4 ,0 , PaletaColores.PRIMARIO));
        lblTabSinopsis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblTabReviews = UIUtils.crearLabel("Reviews" , CargarFuente.BOLD, 18f, Color.GRAY);
        lblTabReviews.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        lblTabReviews.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        pnlTabs.add(lblTabSinopsis);
        pnlTabs.add(Box.createHorizontalStrut(15));
        pnlTabs.add(lblTabReviews);
        
        // Usamos un CardLayout para intercambiar entre vista de Sinopsis y Reseñas
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
        txtSinopsis.setFont(CargarFuente.getRegular(15f));
        txtSinopsis.setForeground(PaletaColores.TEXTO_GRIS_OSCURO); 
        cardSinopsis.add(txtSinopsis, BorderLayout.NORTH);
        
        // Pestaña 2: Reviews 
        JPanel cardReviews = new JPanel(new BorderLayout(0, 10));
        cardReviews.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        // Contenedor de las reseñas existentes
        JPanel listaReviews = new JPanel();
        listaReviews.setLayout(new BoxLayout(listaReviews, BoxLayout.Y_AXIS));
        listaReviews.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        // Como la UI no puede leer el .txt, enviamos nuestro panel vacio (lista reviews)
        // y nuestro libro al Main, para que el Gestor los lea y nos devuelva el panel relleno con las reseñas
        cargarReviewsAction.accept(listaReviews, libro);
        
        JPanel contenedorAlineadoArriba = new JPanel(new BorderLayout());
        contenedorAlineadoArriba.setBackground(PaletaColores.FONDO_PRINCIPAL);
        contenedorAlineadoArriba.add(listaReviews, BorderLayout.NORTH);
        
        // Envolvemos las Reviews en un Scroll en caso de ser muchas
        JScrollPane scrollReviews = new JScrollPane(contenedorAlineadoArriba);
        scrollReviews.setBorder(null);
        scrollReviews.getVerticalScrollBar().setUnitIncrement(16);
        scrollReviews.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
        scrollReviews.getVerticalScrollBar().setUI(new ScrollModernoUI());
        scrollReviews.getVerticalScrollBar().setPreferredSize(new Dimension(14, 0));
        scrollReviews.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cardReviews.add(scrollReviews, BorderLayout.CENTER);
        
        // Espacio/Caja para escribir reviews
        JPanel pnlEscribirReview = new JPanel(new BorderLayout(0, 10)); // 10 es  la separacion vertical
        pnlEscribirReview.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlEscribirReview.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        if(usuarioActual != null) {
        	JTextArea txtNuevaReview = new JTextArea(2, 20);
        	txtNuevaReview.setLineWrap(true);
        	txtNuevaReview.setWrapStyleWord(true);
        	txtNuevaReview.setFont(CargarFuente.getRegular(14f));
        	
        	// Borde del txtArea
        	txtNuevaReview.setBorder(BorderFactory.createCompoundBorder(
        			new LineBorder(PaletaColores.BORDE_CLARO, 1, true),
        			new EmptyBorder(12, 12, 12, 12)
        	));
        	
        	JScrollPane scrollNuevaReview = new JScrollPane(txtNuevaReview);
        	scrollNuevaReview.getVerticalScrollBar().setUI(new ScrollModernoUI());
        	scrollNuevaReview.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        	scrollNuevaReview.setBorder(null);
        	
        	JButton btnEnviar = UIUtils.crearBotonEstandar("Enviar");
        	btnEnviar.setPreferredSize(new Dimension(100, 35));
        	
        	btnEnviar.addActionListener(e -> {
        		String texto = txtNuevaReview.getText().trim();
        		
        		if(texto.isEmpty()) {
        			JOptionPane.showMessageDialog(this, "La reseña no puede estar vacia", "Aviso", JOptionPane.WARNING_MESSAGE);
        			return;
        		}
        		onGuardarReview.accept(texto); // La vista envia la review al Main para que el la maneje
        	});
        	
        	// Agreguemos la caja de texto al centro
        	pnlEscribirReview.add(scrollNuevaReview, BorderLayout.CENTER);
        	
        	// Colocamos el boton enviar en el panel
        	JPanel pnlBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        	pnlBoton.setBackground(PaletaColores.FONDO_PRINCIPAL);
        	pnlBoton.add(btnEnviar);
        	
        	pnlEscribirReview.add(pnlBoton, BorderLayout.SOUTH);
       	
        } else {
        	// El Usuario esta en modo invitado
        	JLabel lblInvitado = UIUtils.crearLabel("Inicia Sesion para dejar una reseña", CargarFuente.ITALIC, 14f, PaletaColores.TEXTO_GRIS_OSCURO);
        	JButton btnLoginReview = UIUtils.crearBotonEstandar("Iniciar Sesion");
        	
        	// El Main detecta que no hay usuario registrado, pide login y si es exitoso el "" nos ayuda a que no guarde un texto vacio en las reviews
        	btnLoginReview.addActionListener(e -> onGuardarReview.accept(""));
        	
        	JPanel pnlInvitado = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        	pnlInvitado.setBackground(PaletaColores.FONDO_PRINCIPAL);
        	pnlInvitado.add(lblInvitado);
        	pnlInvitado.add(btnLoginReview);
        	
        	pnlEscribirReview.add(pnlInvitado, BorderLayout.CENTER);
        	
        }
        
        cardReviews.add(pnlEscribirReview, BorderLayout.SOUTH);
        
        pnlCards.add(cardSinopsis, "SINOPSIS");
        pnlCards.add(cardReviews, "REVIEWS");
        pnlIzquierda.add(pnlTabs, BorderLayout.NORTH);
        pnlIzquierda.add(pnlCards, BorderLayout.CENTER);
        
        // Para poder cambiar entre pestañas
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        
        // Al presionar en el panel de Sinopsis
        lblTabSinopsis.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                cl.show(pnlCards, "SINOPSIS");
                lblTabSinopsis.setForeground(Color.BLACK);
                lblTabSinopsis.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, PaletaColores.PRIMARIO));
                lblTabReviews.setForeground(Color.GRAY);
                lblTabReviews.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            }
        });
        
        // Al presionar en el panel de Reviews
        lblTabReviews.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
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
        pnlDerecha.setMinimumSize(new Dimension(320, 0)); // El 0 de altura permite que estire
        pnlDerecha.setMaximumSize(new Dimension(320, Integer.MAX_VALUE));
        
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
        JPanel pnlFichaInfo = new JPanel(new GridLayout(3, 2, 10, 5)) {
            @Override
            public Dimension getMaximumSize() {
                // Le decimos a Java: Tu altura maxima es tu altura ideal (PreferredSize)
                // Si hay pocas categorias, medira aprox 100. Si hay muchas, medira lo que necesite
                return new Dimension(320, getPreferredSize().height);
            }
        };
        pnlFichaInfo.setBackground(PaletaColores.BLANCO);
        
        // Le añadimos un border grisaceo a la tabla
        pnlFichaInfo.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1, true), new EmptyBorder(15, 15, 15, 15)));
        pnlFichaInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Asignamos el contenido a la tabla
        pnlFichaInfo.add(UIUtils.crearLabel("Estado:", CargarFuente.REGULAR, 13f, Color.GRAY));
        String txtEstadoFicha = libro.isDisponible() ? "Disponible" : "En prestamo";
        Color colEstadoFicha = libro.isDisponible() ? new Color(39, 174, 96) : Color.RED;
        pnlFichaInfo.add(UIUtils.crearLabel(txtEstadoFicha, CargarFuente.BOLD, 13f, colEstadoFicha));
        
        pnlFichaInfo.add(UIUtils.crearLabel("Paginas:", CargarFuente.REGULAR, 13f, Color.GRAY));
        pnlFichaInfo.add(UIUtils.crearLabel(String.valueOf(libro.getPaginas()), CargarFuente.REGULAR, 13f, Color.BLACK));
        
        pnlFichaInfo.add(UIUtils.crearLabel("Categorias:", CargarFuente.REGULAR, 13f, Color.GRAY));
        
        // Usamos un JTextArea para las categorías para que haga el salto de linea automatico si hay muchas
        JTextArea txtCats = new JTextArea(catsUnidas);
        txtCats.setFont(CargarFuente.getRegular(13f));
        txtCats.setForeground(Color.BLACK);
        txtCats.setLineWrap(true);
        txtCats.setWrapStyleWord(true);
        txtCats.setEditable(false);
        txtCats.setOpaque(false);
        txtCats.setFocusable(false);
        txtCats.setMinimumSize(new Dimension(0, 0));
        pnlFichaInfo.add(txtCats);
        
        // Agrupamo los paneles de la caja
        pnlDerecha.add(lblAutorTitulo);
        pnlDerecha.add(pnlCajaAutor);
        pnlDerecha.add(lblAcercaTitulo);
        pnlDerecha.add(pnlFichaInfo);
        pnlDerecha.add(Box.createVerticalGlue());
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
        scrollGeneralDetalles.getHorizontalScrollBar().setUnitIncrement(16);      
        scrollGeneralDetalles.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        scrollGeneralDetalles.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollGeneralDetalles.getVerticalScrollBar().setUI(new ScrollModernoUI());
        scrollGeneralDetalles.getVerticalScrollBar().setPreferredSize(new Dimension(13, 0));
        
        scrollGeneralDetalles.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollGeneralDetalles.getHorizontalScrollBar().setUI(new ScrollModernoUI());
        scrollGeneralDetalles.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 13));
        
        // Forzamos el scroll hacia arriba (que siempre empiece ahi)
        SwingUtilities.invokeLater(() -> scrollGeneralDetalles.getVerticalScrollBar().setValue(0));
        add(scrollGeneralDetalles, BorderLayout.CENTER);
        
        // Evitamos que al guardar una reseña el panel regrese obligatoriamente a Sinopsis
        if (abrirOnReviews) {
        	// Cambiamos el CardLayout visible
        	cl.show(pnlCards, "REVIEWS");
            lblTabReviews.setForeground(Color.BLACK);
            lblTabReviews.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, PaletaColores.PRIMARIO));
            
            // Apagamos visualmente el texto de "Sinopsis" (Añadirle un tono oscuro)
            lblTabSinopsis.setForeground(Color.GRAY);
            lblTabSinopsis.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        }
	}       
}
