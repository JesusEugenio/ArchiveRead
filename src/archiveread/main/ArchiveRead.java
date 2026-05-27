// Archive Read - Version 1.5.0
// ================================
// Laura Alvarez y Jesus Eugenio
// ================================
package archiveread.main;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// =========================================================================
// CLASE PRINCIPAL
// Controla la Interfaz Grafica principal de la aplicacion
// =========================================================================
public class ArchiveRead extends JFrame {
    // Paneles principales
    private JPanel panelPrincipal;
    private JPanel panelContenidoCentro; //Controla el intercambio entre vistas
    private JPanel panelHeader;
    
    // Gestores de datos
    private GestorBiblioteca gestorBiblioteca;
    private GestorUsuarios gestorUsuarios;
    
    // Estado de la sesión actual
    private Usuario usuarioActual = null; 
    
    // Colores para la UI
    private final Color COLOR_PRIMARIO = new Color(130, 49, 90);	// Magenta
    private final Color COLOR_FONDO = new Color(250, 250, 250);		// Blanco 
    
    // Enlazar el cargador de fuentes con el texto de la UI
    private JLabel crearLabel(String texto, Font fuente, float size, Color color) {
    	JLabel lbl = new JLabel(texto);
    	lbl.setFont(CargarFuente.get(fuente, size));
    	lbl.setForeground(color);
    	return lbl;
    }
    
    // Elementos del Header 
    private JLabel lblStatusUsuario; 
    private JButton btnLoginHeader;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ArchiveRead frame = new ArchiveRead();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Constructor de la ventana principal
    public ArchiveRead() {
        gestorBiblioteca = new GestorBiblioteca();
        gestorUsuarios = new GestorUsuarios();

        // Configuración de la ventana
        setTitle("ArchiveRead");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia maximizada

        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);
        setContentPane(panelPrincipal);
        
        panelContenidoCentro = new JPanel(new BorderLayout());
        panelContenidoCentro.setBackground(COLOR_FONDO);
        panelPrincipal.add(panelContenidoCentro, BorderLayout.CENTER);
        
        actualizarHeader();
        mostrarCatalogo("Todas");
        
    }
    
    // =========================================================================
    // NAVEGACION Y CAMBIO DE VISTAS
    // ========================================================================
    
    private void cambiarVista(JPanel nuevoPanel) {
    	// Si ya hay un panel en el centro, lo quitamos de la pantalla principal
    	if(panelContenidoCentro != null) {
    		panelPrincipal.remove(panelContenidoCentro);
    	}
    	
    	// Asignamos un nuevo panel y lo agregamos en el centro del Layout
    	panelContenidoCentro = nuevoPanel;
    	panelPrincipal.add(panelContenidoCentro, BorderLayout.CENTER);
    	
    	// Forzamos que se vuelva a redibujar la interfaz
    	panelPrincipal.revalidate();
    	panelPrincipal.repaint();
    	
    }

    // =========================================================================
    // MÉTODOS DE SESIÓN
    // =========================================================================
    
    // Abre una ventana modal para pedir credenciales
    private void abrirDialogoLogin() {
        JDialog dialogLogin = new JDialog(this, "Login", true);
        dialogLogin.setSize(300, 180);
        dialogLogin.setLocationRelativeTo(this);
        dialogLogin.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        JPanel pnlInputs = new JPanel(new GridLayout(2, 2, 5, 10));
        JTextField txtMatricula = new JTextField(12);
        JPasswordField txtPassword = new JPasswordField(12);

        pnlInputs.add(new JLabel("Matrícula:"));
        pnlInputs.add(txtMatricula);
        pnlInputs.add(new JLabel("Contraseña:"));
        pnlInputs.add(txtPassword);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> {
            // Validamos contra el archivo binario
            Usuario u = gestorUsuarios.validarUsuario(txtMatricula.getText(), new String(txtPassword.getPassword()));
            if (u != null) {
                // Login exitoso: actualizamos estado y textos
                usuarioActual = u;
                actualizarHeader();
                dialogLogin.dispose();
                
                // Recargamos pantalla para habilitar botones de devolver/rentar
                mostrarCatalogo("Todas");
            } else {
                JOptionPane.showMessageDialog(dialogLogin, "Credenciales incorrectas");
            }
        });

        dialogLogin.add(pnlInputs);
        dialogLogin.add(btnEntrar);
        dialogLogin.setVisible(true); // Bloquea hasta que se cierre
    }

    // Cierra la sesión y regresa la interfaz al modo invitado
    private void cerrarSesion() {
        usuarioActual = null;
        actualizarHeader();
        mostrarCatalogo("Todas");
    }

    // =========================================================================
    // RENDERIZADO DE LAS VISTAS DE LA PANTALLA 
    // =========================================================================
    
    private void mostrarCatalogo(String categoriaFiltro) {
    	String tituloCatalogo = categoriaFiltro.equals("Todas") ?
    			"Libros Recientes" : "Libros de " + categoriaFiltro + ": ";
    	
    	ArrayList<Libro> libros = gestorBiblioteca.filtrarPorCategoria(categoriaFiltro);
    	cambiarVista(crearVistaListaLibros(tituloCatalogo, libros,categoriaFiltro));
    	
    }
    
    // Ahora la funcion muestra la lista de libros de acuerdo a la categoria (por defecto "Todas)
    private JPanel crearVistaListaLibros(String tituloLabel, ArrayList<Libro> librosMostrados, String filtroActual) {
    	// Contenedor base
    	JPanel panelBase = new JPanel(new BorderLayout(20, 20));
    	panelBase.setBackground(COLOR_FONDO);
    	panelBase.setBorder(new EmptyBorder(20, 40, 20, 40));
    	
    	JLabel lblTitulo = new JLabel(tituloLabel);
    	lblTitulo.setFont(CargarFuente.get(CargarFuente.BOLD, 22f));
    	panelBase.add(lblTitulo, BorderLayout.NORTH);
    	
    	// Seccion Central - Aqui se apilan las entradas de cada libro
    	JPanel panelListaLibros = new JPanel();
    	panelListaLibros.setLayout(new BoxLayout(panelListaLibros, BoxLayout.Y_AXIS));
    	panelListaLibros.setBackground(COLOR_FONDO);
    	
    	if (librosMostrados.isEmpty()) {
    		panelListaLibros.add(crearEtiquetaVacia("No se encontraron libros para esta vista"));
    	} else {
    		for (Libro l : librosMostrados) {
    			panelListaLibros.add(crearTarjetaLibro(l)); 
    			
    		}
    	}
    	
    	// Envolvemos la lista en un scrollPane
    	JScrollPane scrollPane = new JScrollPane(panelListaLibros);
    	scrollPane.setBorder(null);
    	scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    	scrollPane.getViewport().setBackground(COLOR_FONDO);
    	panelBase.add(scrollPane, BorderLayout.CENTER);
    	
    	// Seccion Derecha - Filtros y Generacion de reportes (aun en desarrollo)
    	JPanel sidebar = new JPanel(null);
    	sidebar.setBackground(Color.WHITE);
    	sidebar.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
    	sidebar.setPreferredSize(new Dimension(300, 320));
    	
    	JLabel lblSel = new JLabel("Seleccion de Libros");
    	lblSel.setFont(CargarFuente.get(CargarFuente.BOLD, 16f));
    	lblSel.setBounds(20, 20, 200, 20); // Nos referimos al tamaño y posicion de este texto
    	sidebar.add(lblSel);
    
    	JLabel lblCat = new JLabel("Categoria");
    	lblCat.setFont(CargarFuente.get(CargarFuente.REGULAR, 14f));
    	lblCat.setBounds(20, 60, 200, 15);
    	sidebar.add(lblCat);
    	
    	// Cargamos categorias existentes
    	ArrayList<String> listaCategorias = gestorBiblioteca.obtenerCategoriasUnicas();
    	if (!listaCategorias.contains("Todas")) {
    		listaCategorias.add(0, "Todas");
    	}
    	
    	JComboBox<String> comboCategorias = new JComboBox<>(listaCategorias.toArray(new String[0])); // Convertir en arreglo FIJO para ser usado aqui
    	comboCategorias.setFont(CargarFuente.get(CargarFuente.REGULAR, 14f));
    	comboCategorias.setSelectedItem(filtroActual);
    	comboCategorias.setBounds(20, 80, 250, 30);
    	sidebar.add(comboCategorias);
    	
    	// Boton para aplicar filtro y actualizar la vista
    	JButton btnFiltrar = crearBotonEstandar("Mostrar Libros", COLOR_PRIMARIO, Color.WHITE);
    	btnFiltrar.setBounds(20, 140, 250, 30);
    	btnFiltrar.addActionListener( e -> {
    		mostrarCatalogo(comboCategorias.getSelectedItem().toString());
    	});
    	sidebar.add(btnFiltrar);
    	
    	// Enlaces para generar reportes en archivos .txt "AUN EN PROCESO / REDISEÑO"
        JLabel lblReportesTitulo = crearLabel("Generar Reportes (.txt)", CargarFuente.BOLD, 15f, Color.BLACK);
        lblReportesTitulo.setBounds(20, 210, 250, 20);
        sidebar.add(lblReportesTitulo);

        JLabel lblReporteCat = crearLabel("Reporte de Categorías", CargarFuente.REGULAR, 14f, COLOR_PRIMARIO);
        lblReporteCat.setBounds(20, 240, 250, 20);
        lblReporteCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReporteCat.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) { 
            	generarReporteCategorias(); 
            }
        });
        sidebar.add(lblReporteCat);

        JLabel lblReporteAut = crearLabel("Reporte de Autores", CargarFuente.REGULAR, 14f, COLOR_PRIMARIO);
        lblReporteAut.setBounds(20, 270, 250, 20);
        lblReporteAut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReporteAut.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e) { 
            	generarReporteAutores(); 
            }
        });
        sidebar.add(lblReporteAut);

        // Envoltorio para fijar el Sidebar arriba y evitar que se estire con la ventana
        JPanel wrapperFiltro = new JPanel(new BorderLayout());
        wrapperFiltro.setBackground(COLOR_FONDO);
        wrapperFiltro.add(sidebar, BorderLayout.NORTH);
        panelBase.add(wrapperFiltro, BorderLayout.EAST);

        return panelBase;
    	
    	
    }
    
    private JPanel crearTarjetaLibro(Libro l) {
    	// Contenedor principal, tarjeta blanca que agrupa todo el contenido del libro
    	JPanel panelItem = new JPanel(new BorderLayout(20,0));
    	panelItem.setBackground(Color.WHITE);
    	panelItem.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createEmptyBorder(0, 0, 20, 0),
    			BorderFactory.createCompoundBorder(
    					new LineBorder(new Color(230, 230, 230), 1),
    					new EmptyBorder(15, 15, 15, 15)
    					)
    			));
    	panelItem.setAlignmentX(Component.LEFT_ALIGNMENT);
    	panelItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
    	
    	// Evento para abrir los detalles al hacer click en la tarjeta
    	MouseAdapter eventoClic = new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			mostrarDetalleLibro(l);
    		}
    	};
    	
    	// Lado izquierdo ... Portada del libro y su estado
    	ImageIcon iconoPortada = new ImageIcon(l.getRutaImagen());
    	Image imgEscalada = iconoPortada.getImage().getScaledInstance(120, 170, Image.SCALE_SMOOTH);
    	JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
    	lblImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	lblImagen.addMouseListener(eventoClic); // La portada funcionara como boton
    	
    	
    	// Lado derecho ... Informacion del libro
    	JPanel panelInfo = new JPanel(new BorderLayout(0, 5));
    	panelInfo.setBackground(Color.WHITE);
    	panelInfo.setBorder(new EmptyBorder(0, 10, 0, 0 ));
    	
    	JPanel pnlTitulos = new JPanel();
    	pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
    	pnlTitulos.setBackground(Color.WHITE);
    	
    	JLabel lblTitulo = crearLabel(l.getTitulo(), CargarFuente.BOLD, 20f, Color.BLACK);
    	lblTitulo.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    	lblTitulo.addMouseListener(eventoClic); // El título también funciona como botón
        
        JLabel lblAutor = crearLabel("Autor: " + l.getAutor(), CargarFuente.BOLD, 13f, Color.GRAY);
        lblAutor.setBorder(new EmptyBorder(5, 0, 5, 0));
        lblAutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(lblAutor);

        // Cortamos la sinopsis a 200 caracteres para que no desborde la tarjeta visualmente
        String sinopsisCorta = l.getSinopsis().length() > 200 ? 
                               l.getSinopsis().substring(0, 200) + "..." : 
                               l.getSinopsis();
        
        JTextArea txtSinopsis = new JTextArea(sinopsisCorta);
        txtSinopsis.setFont(CargarFuente.get(CargarFuente.REGULAR, 13f));
        txtSinopsis.setForeground(Color.BLACK);
        txtSinopsis.setLineWrap(true);
        txtSinopsis.setWrapStyleWord(true);
        txtSinopsis.setEditable(false);
        txtSinopsis.setOpaque(false);
        txtSinopsis.setFocusable(false);
        txtSinopsis.setBorder(new EmptyBorder(10, 0, 10, 0));
        txtSinopsis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtSinopsis.addMouseListener(eventoClic); // La sinopsis también funciona como botón

        JLabel lblEstado = crearLabel(l.getPaginas() + " pág.", CargarFuente.REGULAR, 13f, Color.DARK_GRAY);

        panelInfo.add(pnlTitulos, BorderLayout.NORTH);
        panelInfo.add(txtSinopsis, BorderLayout.CENTER);
        panelInfo.add(lblEstado, BorderLayout.SOUTH);

        // Ensamblaje final de la tarjeta
        panelItem.add(lblImagen, BorderLayout.WEST);
        panelItem.add(panelInfo, BorderLayout.CENTER);
        return panelItem;
    
    }
    
    
    private void actualizarHeader() {
    	// Para refrescar la vista cuando sucedan cambios hechos en el menu principal
    	if (panelHeader != null) panelPrincipal.remove(panelHeader);

        panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JPanel pnlIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        pnlIzquierda.setBackground(Color.WHITE);
        
        JLabel lblLogo = new JLabel("ArchiveRead");
        lblLogo.setFont(CargarFuente.get(CargarFuente.BOLD, 22f));
        lblLogo.setForeground(COLOR_PRIMARIO);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mostrarCatalogo("Todas"); }
        });
        pnlIzquierda.add(lblLogo);
        pnlIzquierda.add(crearMenuLabel("Mi biblioteca", () -> mostrarMiBiblioteca()));

        JPanel pnlDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlDerecha.setBackground(Color.WHITE);
        
        if (usuarioActual == null) {
            JButton btnIngresar = crearBotonEstandar("Ingresar", new Color(240, 240, 240), Color.DARK_GRAY);
            btnIngresar.addActionListener(e -> abrirDialogoLogin());
            pnlDerecha.add(btnIngresar);
        } else {
            pnlDerecha.add(crearLabel("Hola, " + usuarioActual.getNombre(), CargarFuente.BOLD, 14f, Color.BLACK));
            JButton btnSalir = crearBotonEstandar("Cerrar Sesión", Color.DARK_GRAY, Color.WHITE);
            btnSalir.addActionListener(e -> cerrarSesion());
            pnlDerecha.add(btnSalir);
        }

        panelHeader.add(pnlIzquierda, BorderLayout.WEST);
        panelHeader.add(pnlDerecha, BorderLayout.EAST);
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }
    
    // =========================================================================
    // METODOS AUXILIARES DE DISEÑO INTERNO
    // =========================================================================
    
    //Creacion de boton con estilo uniforme
    private JButton crearBotonEstandar(String texto, Color bg, Color fg) {
    	JButton btn = new JButton(texto);
    	btn.setFont(CargarFuente.get(CargarFuente.BOLD,  14f));
    	//Colores de fondo y de texto
    	btn.setBackground(bg);
    	btn.setForeground(fg);
    	
    	//Cursor (quita el borde de seleccion si se hace click, cambia a manita)
    	btn.setFocusPainted(false);
    	btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	
    	btn.setContentAreaFilled(false);
    	btn.setOpaque(true);
    	
    	return btn;
    }
    
    //Creacion de boton de menu (como texto)
    private JLabel crearMenuLabel(String texto, Runnable accion) {
    	JLabel lbl = new JLabel(texto);
    	lbl.setFont(CargarFuente.get(CargarFuente.REGULAR, 15f));
    	lbl.setForeground(new Color (50, 50, 50));
    	lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	
    	//detector de mouse
    	lbl.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) { 
    			accion.run();
    		}
    	});
    	return lbl;
    }
    
    //Creacion de etiquetas de texto
    private JLabel crearLabel_N(String texto, Font fuente, float size, Color color) {
    	JLabel lbl = new JLabel(texto);
    	lbl.setFont(CargarFuente.get(fuente, size));
    	lbl.setForeground(color);
    	return lbl;
    }
    
    
    
    public void mostrarDetalleLibro(Libro libro) {
    	cambiarVista(crearPanelDetalle(libro));
 
    }
    
    private JPanel crearPanelDetalle(Libro libro) { 	
    	JPanel panelBase = new JPanel(new BorderLayout());
    	panelBase.setBackground(COLOR_FONDO);
    	
    	// ================================================
    	// Barra de navegacion superior 
    	// ================================================
    	JPanel pnlRuta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
    	pnlRuta.setBackground(COLOR_FONDO);
    	pnlRuta.setBorder(new EmptyBorder(5, 40, 0, 0));
    	
    	String catsUnidas = String.join(", ", libro.getCategorias());
    	pnlRuta.add(crearLabel("ArchiveRead  > " + catsUnidas + "  > ", CargarFuente.REGULAR, 14f, Color.GRAY));
    	
    	// Boton para volver al menu principal
    	JLabel lblAtras = crearLabel("Volver al Catalogo", CargarFuente.REGULAR, 14f, COLOR_PRIMARIO);
    	lblAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	
    	// Registro de evento lblAtras
    	lblAtras.addMouseListener(new java.awt.event.MouseAdapter() {
        	@Override
        	public void mouseClicked(java.awt.event.MouseEvent e) {
        		// Volvemos a cargar el catalogo de libros
        		mostrarCatalogo("Todas");
        	}
        });
    	pnlRuta.add(lblAtras);
    	panelBase.add(pnlRuta, BorderLayout.NORTH);
    	
    	
    	// ================================================================
    	// ===  Panel que agrupa todo el banner e informacion del libro ===
    	// ================================================================
    	JPanel pnlContenidoCentral = new JPanel(new BorderLayout());
        pnlContenidoCentral.setBackground(COLOR_FONDO);
    	
    	// Panel del banner del libro
    	JPanel pnlBannerOscuro = new JPanel(new BorderLayout(30,0));
    	pnlBannerOscuro.setBackground(new Color(10, 10, 10));
    	pnlBannerOscuro.setBorder(new EmptyBorder(40, 40, 40, 40));

    	// Cargar Portada
    	ImageIcon iconoPortada = new ImageIcon(libro.getRutaImagen());
    	Image imgEscalada = iconoPortada.getImage().getScaledInstance(180, 260, Image.SCALE_SMOOTH);
    	JLabel lblImagen = new JLabel(new ImageIcon(imgEscalada));
    	lblImagen.setVerticalAlignment(SwingConstants.TOP);
    	pnlBannerOscuro.add(lblImagen, BorderLayout.WEST);
    	
    	// Instancia para poner titulo y categorias
    	JPanel pnlInfoBanner = new JPanel();
    	pnlInfoBanner.setLayout(new BoxLayout(pnlInfoBanner, BoxLayout.Y_AXIS));
    	pnlInfoBanner.setOpaque(false);
    	
    	// Titulo
    	JLabel lblTitulo = crearLabel(libro.getTitulo(), CargarFuente.BOLD, 32f, Color.WHITE);
    	lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    	lblTitulo.setBorder(new EmptyBorder(15, 0, 10, 0));
    	
    	// Evaluar disponibilidad
    	String textoDisponibilidad = libro.isDisponible() ? "Disponible" : "Ocupado";
    	JLabel lblStats = crearLabel(" " + libro.getPaginas() + " Paginas   |   " + textoDisponibilidad, CargarFuente.REGULAR, 14f, new Color(200, 200, 200));
    	lblStats.setAlignmentX(Component.LEFT_ALIGNMENT);
    	lblTitulo.setBorder(new EmptyBorder(0, 0, 35, 0));
    	
    	// Panel para botones rentar y añadir a biblioteca
    	JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    	pnlBotones.setOpaque(false);
    	pnlBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	// Boton de Rentar / Prestado
    	JButton btnRentar = new JButton();
    	btnRentar.setFont(CargarFuente.get(CargarFuente.BOLD, 13f));
    	btnRentar.setPreferredSize(new Dimension(150, 50));
    	btnRentar.setFont(CargarFuente.get(CargarFuente.BOLD, 14f));
    	btnRentar.setFocusPainted(false);
    	btnRentar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	
    	if (libro.isDisponible()) {
            btnRentar.setText("Rentar");
            btnRentar.setBackground(COLOR_PRIMARIO);
            btnRentar.setForeground(Color.WHITE);
            btnRentar.addActionListener(e -> { rentarLibro(libro); mostrarDetalleLibro(libro); });
            
        } else if (usuarioActual != null && usuarioActual.getMatricula().equals(libro.getMatriculaPrestamo())) {
            btnRentar.setText("Devolver");
            btnRentar.setBackground(Color.DARK_GRAY);
            btnRentar.setForeground(Color.WHITE);
            btnRentar.addActionListener(e -> { devolverLibro(libro); mostrarDetalleLibro(libro); });
            
        } else {
            btnRentar.setText("Prestado");
            btnRentar.setBackground(new Color(60, 60, 60));
            btnRentar.setForeground(Color.LIGHT_GRAY);
            btnRentar.setEnabled(false);
        }

        // Botón de Añadir a Biblioteca / Guardado 
        boolean estaGuardado = (usuarioActual != null) && libro.estaGuardado(usuarioActual.getMatricula());
        JButton btnGuardar = new JButton(estaGuardado ? "En Biblioteca" : "Añadir a Biblioteca");
        btnGuardar.setPreferredSize(new Dimension(200, 40));
        btnGuardar.setFont(CargarFuente.get(CargarFuente.BOLD, 14f));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBackground(estaGuardado ? Color.WHITE : new Color(30, 35, 40));
        btnGuardar.setForeground(estaGuardado ? COLOR_PRIMARIO : Color.WHITE);
        btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        btnGuardar.addActionListener(e -> {
            if (usuarioActual == null) {
                abrirDialogoLogin();
            } else {
                libro.toggleGuardado(usuarioActual.getMatricula());
                gestorBiblioteca.actualizarLibro();
                mostrarDetalleLibro(libro);
            }
        });
        
        
        // Agregar los botones al panel 
        pnlBotones.add(btnRentar);
        pnlBotones.add(Box.createHorizontalStrut(15));
        pnlBotones.add(btnGuardar);
        
        // Agregar detalles del libro al panel 
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
        pnlInferior.setBackground(COLOR_FONDO);
        pnlInferior.setBorder(new EmptyBorder(30, 40, 40, 40));
        
        // Dividimos sinopsis y reviews en dos pestañas con el card layout
        JPanel pnlIzquierda = new JPanel(new BorderLayout());
        pnlIzquierda.setBackground(COLOR_FONDO);
        
        JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        pnlTabs.setBackground(COLOR_FONDO);
        pnlTabs.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblTabSinopsis = crearLabel("Sinopsis" , CargarFuente.BOLD, 18f, Color.BLACK);
        lblTabSinopsis.setBorder(BorderFactory.createMatteBorder(0, 0, 4 ,0 , COLOR_PRIMARIO));
        lblTabSinopsis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblTabReviews = crearLabel("Reviews" , CargarFuente.BOLD, 18f, Color.GRAY);
        lblTabReviews.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        lblTabReviews.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        pnlTabs.add(lblTabSinopsis);
        pnlTabs.add(lblTabReviews);
        
        JPanel pnlCards = new JPanel(new CardLayout());
        pnlCards.setBackground(COLOR_FONDO);
        
        
        // Pestaña 1: Sinopsis 
        JPanel cardSinopsis = new JPanel(new BorderLayout(0, 10));
        cardSinopsis.setBackground(COLOR_FONDO);
        JTextArea txtSinopsis = new JTextArea(libro.getSinopsis());
        txtSinopsis.setWrapStyleWord(true); // Ajuste automatico de palabras
        txtSinopsis.setLineWrap(true);	
        txtSinopsis.setEditable(false);		// Desactiva la edicion del texto
        txtSinopsis.setOpaque(false); 		// Texto sin opacidad
        txtSinopsis.setFont(CargarFuente.get(CargarFuente.REGULAR, 15f));
        txtSinopsis.setForeground(new Color(50, 50, 50)); // Texto gris oscuro
        cardSinopsis.add(txtSinopsis, BorderLayout.NORTH);
        
        // Pestaña 2: Reviews 
        JPanel cardReviews = new JPanel(new BorderLayout(0, 10));
        cardReviews.setBackground(COLOR_FONDO);
        JPanel listaReviews = new JPanel();
        listaReviews.setLayout(new BoxLayout(listaReviews, BoxLayout.Y_AXIS));
        listaReviews.setBackground(COLOR_FONDO);
        
        // Cargar reviews desde archivo de texto
        cargarReviews(libro, listaReviews);
        
        // Envolver las Reviews en un Scroll en caso de ser muchas
        JScrollPane scrollReviews = new JScrollPane(listaReviews);
        scrollReviews.setBorder(null);
        scrollReviews.getViewport().setBackground(COLOR_FONDO);
        cardReviews.add(scrollReviews, BorderLayout.CENTER);

        pnlCards.add(cardSinopsis, "SINOPSIS");
        pnlCards.add(cardReviews, "REVIEWS");
        pnlIzquierda.add(pnlCards, BorderLayout.CENTER);
        
        // Para poder cambiar entre pestañas
        CardLayout cl = (CardLayout) pnlCards.getLayout();
        
        // Al presionar en el texto de sinopsis
        lblTabSinopsis.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                cl.show(pnlCards, "SINOPSIS");
                lblTabSinopsis.setForeground(Color.BLACK);
                lblTabSinopsis.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, COLOR_PRIMARIO));
                lblTabReviews.setForeground(Color.GRAY);
                lblTabReviews.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            }
        });
        
        
        // Al presionar el texto de Reviews
        lblTabReviews.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                cl.show(pnlCards, "REVIEWS");
                lblTabReviews.setForeground(Color.BLACK);
                lblTabReviews.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, COLOR_PRIMARIO));
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
        pnlDerecha.setBackground(COLOR_FONDO);
        pnlDerecha.setPreferredSize(new Dimension(320, 400));
        
        // Encabezado del autor
        JLabel lblAutorTitulo = crearLabel("Autor", CargarFuente.BOLD, 18f, Color.BLACK);
        lblAutorTitulo.setAlignmentX(Component.LEFT_ALIGNMENT); // Que se alinee en la izquierda
        lblAutorTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        JPanel pnlCajaAutor = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pnlCajaAutor.setBackground(new Color(240, 240, 240));
        pnlCajaAutor.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        pnlCajaAutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCajaAutor.setMaximumSize(new Dimension(320, 69));
        pnlCajaAutor.add(crearLabel(libro.getAutor(), CargarFuente.BOLD, 14f, Color.BLACK));
        
        // Seccion "Acerca de este libro"
        JLabel lblAcercaTitulo = crearLabel("Acerca de este libro", CargarFuente.BOLD, 18f, Color.BLACK);
        lblAcercaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAcercaTitulo.setBorder(new EmptyBorder(30, 0, 10, 0));
        
        // Crear la tabla donde esta la ficha tecnica 
        JPanel pnlFichaInfo = new JPanel(new GridLayout(3, 2, 10, 15));
        pnlFichaInfo.setBackground(Color.WHITE);
        
        // Añadir un borde grisaceo a la tabla
        pnlFichaInfo.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(15, 15, 15, 15)));
        pnlFichaInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFichaInfo.setMaximumSize(new Dimension(320, 140));
        
        // Asignar contenido a la tabla
        pnlFichaInfo.add(crearLabel("Estado:", CargarFuente.REGULAR, 13f, Color.GRAY));
        String txtEstadoFicha = libro.isDisponible() ? "Disponible" : "En prestamo";
        Color colEstadoFicha = libro.isDisponible() ? new Color(39, 174, 96) : Color.RED;
        pnlFichaInfo.add(crearLabel(txtEstadoFicha, CargarFuente.BOLD, 13f, colEstadoFicha));
        
        pnlFichaInfo.add(crearLabel("Paginas:", CargarFuente.REGULAR, 13f, Color.GRAY));
        pnlFichaInfo.add(crearLabel(String.valueOf(libro.getPaginas()), CargarFuente.REGULAR, 13f, Color.BLACK));
        
        pnlFichaInfo.add(crearLabel("Categorias:", CargarFuente.REGULAR, 13f, Color.GRAY));
        pnlFichaInfo.add(crearLabel(libro.getCategoria(), CargarFuente.REGULAR, 13f, Color.BLACK));
        
        pnlDerecha.add(lblAutorTitulo);
        pnlDerecha.add(pnlCajaAutor);
        pnlDerecha.add(pnlFichaInfo);
        pnlInferior.add(pnlDerecha, BorderLayout.EAST);
        
        // Agrupar los panels verticales
        JPanel pnlAgrupadorVertical = new JPanel(new BorderLayout());
        pnlAgrupadorVertical.setBackground(COLOR_FONDO);
        pnlAgrupadorVertical.add(pnlInferior, BorderLayout.CENTER);
        pnlContenidoCentral.add(pnlAgrupadorVertical, BorderLayout.CENTER);
        
        // Envolvemos el contenido en un scroll
        JScrollPane scrollGeneralDetalles = new JScrollPane(pnlContenidoCentral);
        scrollGeneralDetalles.setBorder(null);
        scrollGeneralDetalles.getVerticalScrollBar().setUnitIncrement(16); // Velocidad de avance de 16 pixeles con el mouse
        scrollGeneralDetalles.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Apagar scroll horizontal
        scrollGeneralDetalles.getViewport().setBackground(COLOR_FONDO);
        panelBase.add(scrollGeneralDetalles, BorderLayout.CENTER);
        
        // Devolvemos la vista creada
        return panelBase;
    
    }
    
    private void rentarLibro(Libro libro) {
        if (usuarioActual == null) {
            abrirDialogoLogin();
        } else {
            libro.setDisponible(false);
            libro.setMatriculaPrestamo(usuarioActual.getMatricula());
            gestorBiblioteca.actualizarLibro(); // Guarda en binario
            mostrarCatalogo("Todas"); // Refresca UI
        }
    }

    private void devolverLibro(Libro libro) {
        libro.setDisponible(true);
        libro.setMatriculaPrestamo(null);
        gestorBiblioteca.actualizarLibro(); // Guarda en binario
        mostrarCatalogo("Todas"); // Refresca UI
    }
    
    
    private String limpiarNombreArchivo(String nombre) {
    	if(nombre == null) {
    		return "desconocido";
    	}
    	String normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD);
    	String sinAcentos = normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    	String sinEspacios = sinAcentos.replace(" ", "");
    	return sinEspacios.replaceAll("[^a-zA-Z0-9]", "");
    }
    
    //Renombra la ruta de la imagen que suba el administrador y se copia en un directorio/carpeta
    private String guardarPortada(String rutaOrigen, String titulo) {
    	if(rutaOrigen == null || rutaOrigen.contains("default.jpg")) {
    		return "covers/default.jpg";
    	}
    	
    	try {
    		File archivoOrigen = new File(rutaOrigen);
    		if(!archivoOrigen.exists()) {
    			return rutaOrigen;
    		}
    		
    		String extension = "";
    		int i = rutaOrigen.lastIndexOf('.');
    		if(i > 0) {
    			extension = rutaOrigen.substring(i);
    		}
    		
    		String nombreSeguro = limpiarNombreArchivo(titulo);
    		String nuevoNombreArchivo = nombreSeguro + "_cover" + extension;
    		
    		File carpetaCovers = new File("covers/");
    		if(!carpetaCovers.exists()) {
    			carpetaCovers.mkdirs();
    		}
    		
    		File archivoDestino = new File(carpetaCovers, nuevoNombreArchivo);
    		
    		if(!archivoOrigen.getAbsolutePath().equals(archivoDestino.getAbsolutePath())) {
    			Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
    		}
    		return archivoDestino.getPath();
    				
    		
    	}catch(IOException e) {
    		e.printStackTrace();
    		return rutaOrigen; 
    	}
		
    	
    }
    
    // ======================================
    // MÉTODOS DE LÓGICA DE REVIEWS 
    // ====================================== 
    
    
    private void guardarReview(Libro libro, String usuario, String texto) {
    	File dir = new File("reviews/");
    	if(!dir.exists()) {
    		dir.mkdirs();
    	}
    	
    	String nombreSeguro = limpiarNombreArchivo(libro.getTitulo());
    	String nombreArchivo = "reviews/" + nombreSeguro + "_review.txt";
    	
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))){
    		writer.write(usuario + "|||" + texto);
    		writer.newLine();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    private void cargarReviews(Libro libro, JPanel panelLista) {
    	String nombreSeguro = limpiarNombreArchivo(libro.getTitulo());
    	String nombreArchivo = "reviews/" + nombreSeguro + "_review.txt";
    	File archivo = new File(nombreArchivo);
    	
    	if(!archivo.exists()) {
    		panelLista.add(crearEtiquetaVacia("Aun no hay reviews. !Sé el primero en opinar¡"));
    		return;
    	}
    	
    	try(BufferedReader reader = new BufferedReader(new FileReader(archivo))){
    		String linea;
    		boolean hayReviews = false;
    		
    		while((linea = reader.readLine()) != null) {
    			String [] partes = linea.split("\\|\\|\\|");
    			
    			if(partes.length == 2){
    				JPanel item = new JPanel(new BorderLayout(5,5));
    				item.setBackground(Color.WHITE);
    				item.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(0, 0, 10, 0),
						BorderFactory.createCompoundBorder(
								new LineBorder(new Color(230, 230, 230), 1),
								new EmptyBorder(10, 10, 10, 10)
								)
						));
    				item.setAlignmentX(Component.LEFT_ALIGNMENT);
    				
    				JLabel lblUser = new JLabel("");
    				
    				JTextArea txtTexto = new JTextArea(partes[1]);
    				//lblVacio.setFont(CargarFuente.get(CargarFuente.REGULAR, 13f));
    				txtTexto.setForeground(Color.DARK_GRAY);
    				txtTexto.setLineWrap(true);
    				txtTexto.setWrapStyleWord(true);
    				txtTexto.setEditable(false);
    				txtTexto.setOpaque(false);
    				
    				item.add(lblUser, BorderLayout.NORTH);
    				item.add(txtTexto, BorderLayout.CENTER);
    				
    				panelLista.add(item);
    				hayReviews = true;
    			}
    		}
    		
    		if(!hayReviews) {
    			panelLista.add(crearEtiquetaVacia("Aun no hay reviews. !Sé el primero en opinar¡"));
    		}
    		
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    // Método para navegar a la biblioteca personal del usuario
    public void mostrarMiBiblioteca() {
        // Por ahora, redirige al catálogo completo en lo que se implementa la vista de rentados
        mostrarCatalogo("Todas");
    }
    
    // ============================================
    // MÉTODOS DE GENERACION DE REPORTES (TXT)
    // ============================================
    
    
    private void generarReportePrestamos() {
    	try{
    		File file = new File("reporte_prestamos.txt");
    		try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
    			writer.write("========================================\n");
    			writer.write("      REPORTE DE LIBROS PRESTADOS       \n");
    			writer.write("========================================\n");
    			
    			boolean hayPrestamos = false;
    			for(Libro l : gestorBiblioteca.obtenerLibros()) {
    				if(!l.isDisponible()) {
    					writer.write("Título: " + l.getTitulo() + "\n");
    					writer.write("Autor: " + l.getAutor() + "\n");
    					writer.write("Presstado a (Matrícula): " + l.getMatriculaPrestamo() + "\n");
    					
    					Usuario u = gestorUsuarios.buscarPorMatricula(l.getMatriculaPrestamo());
    					if(u != null) {
    						writer.write("Nombre del Lector: " + u.getNombre() + "\n");
    					}
    					
    					writer.write("------------------------------------------\n");
    					hayPrestamos = true;
    				}
    			}
    			
    			if(!hayPrestamos) {
    				writer.write("No hay libros prestados en este momento. \n");
    			}
    			
    		}
    		JOptionPane.showMessageDialog(this, "Reporte generado Exitosamente en 'reporte_prestamos.txt'");
    	}catch(IOException e) {
    		JOptionPane.showMessageDialog(this, "Error al generar el reporte de préstamos.", "Error", JOptionPane.ERROR_MESSAGE);
    		e.printStackTrace();
    	}
    }
    
    
    private void limpiarDirectorio(File dir) {
    	if(dir.exists() && dir.isDirectory()) {
    		File[] archivos = dir.listFiles();
    		if(archivos != null) {
    			for(File f : archivos) {
    				f.delete();
    			}
    		}
    	}
    }
    
    private void generarReporteCategorias() {
    	try {
    		File dir = new File("reportes_categorias");
    		if(!dir.exists()) {
    			dir.mkdirs();
    		} else {
    			limpiarDirectorio(dir);
    		}
    		
    		ArrayList<String> categorias = gestorBiblioteca.obtenerCategoriasUnicas();
    		for(String cat : categorias) {
    			String nombreSeguro = limpiarNombreArchivo(cat);
    			File file = new File(dir, "Categoria_" + nombreSeguro + ".txt");
    			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
    				writer.write("--- CATEGORÍA: " + cat + " ---\n\n");
    				for(Libro l : gestorBiblioteca.filtrarPorCategoria(cat)) {
    					writer.write("- " + l.getTitulo() + " (Autor: " + l.getAutor() + ")\n");
    				}
    			}
    		}
    		JOptionPane.showMessageDialog(this, "Reportes de categorías generados en la carpeta 'reportes_categorias'.");
    		
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    private void generarReporteAutores() {
    	try {
    		File dir = new File("reportes_autores");
    		if(!dir.exists()) {
    			dir.mkdirs();
    		} else {
    			limpiarDirectorio(dir);
    		}
    		
    		ArrayList<String> autores = new ArrayList<>();
    		for(Libro l : gestorBiblioteca.obtenerLibros()) {
    			if(!autores.contains(l.getAutor())) {
    				autores.add(l.getAutor());
    			}
    		}
    		
    		for(String autor : autores) {
    			String nombreSeguro = limpiarNombreArchivo(autor);
    			File file = new File(dir, "Autor_" + nombreSeguro + ".txt");
    			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
    				writer.write("--- AUTOR: " + autor + " ---\n\n");
    				for(Libro l : gestorBiblioteca.obtenerLibros()) {
    					writer.write("- " + l.getTitulo() + " (" + l.getCategoria() + ")\n");
    				}
    			}
    		}
    		JOptionPane.showMessageDialog(this, "Reportes de autores generados en la carpeta 'reportes_autores'.");
    		
    	}catch(IOException e) {
    		e.getStackTrace();
    	}
    }
    
    
    
    private JLabel crearEtiquetaVacia(String texto) {
    	JLabel lblVacio = new JLabel(texto);
    	//lblVacio.setFont(CargarFuente.get(CargarFuente.REGULAR, 13f));
    	lblVacio.setForeground(Color.GRAY);
    	lblVacio.setBorder(new EmptyBorder(0, 10, 0,0 ));
    	lblVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
    	return lblVacio;
    }
     
    
}

// =========================================================================
// CLASE: GESTOR DE USUARIOS
// Encargada de leer y escribir 'usuarios.dat'
// =========================================================================
class GestorUsuarios {
    private ArrayList<Usuario> usuariosRegistrados;
    private final String PATH = "usuarios.dat";

    public GestorUsuarios() {
        usuariosRegistrados = new ArrayList<>();
        cargarUsuarios();
    }

    @SuppressWarnings("unchecked") //Silencia advertencias de archivos
    private void cargarUsuarios() {
        File f = new File(PATH);
        // Si no existe, creamos los usuarios por defecto
        if (!f.exists()) {
            usuariosRegistrados.add(new Administrador("admin", "admin123", "Administrador"));
            usuariosRegistrados.add(new Lector("548821", "1234", "Jesus Eugenio"));
            guardarUsuarios();
            return;
        }
        // Deserialización
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            usuariosRegistrados = (ArrayList<Usuario>) ois.readObject();
        } catch (Exception e) { 
            System.err.println("Error al cargar usuarios: " + e.getMessage()); 
        }
    }

    private void guardarUsuarios() {
        // Serialización
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATH))) {
            oos.writeObject(usuariosRegistrados);
        } catch (Exception e) { 
            System.err.println("Error al guardar usuarios: " + e.getMessage()); 
        }
    }

    public Usuario validarUsuario(String m, String p) {
        return usuariosRegistrados.stream()
                .filter(u -> u.getMatricula().equals(m) && u.getPassword().equals(p))
                .findFirst()
                .orElse(null);
    }
    
    public boolean registrarNuevoUsuario(Usuario nuevoUsuario) {
    	for(Usuario u : usuariosRegistrados) {
    		if(u.getMatricula().equals(nuevoUsuario.getMatricula())) {
    			return false;
    		}
    	}
    	usuariosRegistrados.add(nuevoUsuario);
    	guardarUsuarios();
    	return true;
    }
    
    public Usuario buscarPorMatricula(String matricula) {
    	for(Usuario u : usuariosRegistrados) {
    		if(u.getMatricula().equals(matricula)) {
    			return u;
    		}
    	}
    	return null;
    }
    
    
}

// =========================================================================
// CLASE: GESTOR DE BIBLIOTECA
// Encargada de leer y escribir 'inventario.dat'
// =========================================================================
class GestorBiblioteca {
    private ArrayList<Libro> inventario;
    private final String INVENTARIO = "inventario.dat";
    private final String RUTA_COVERS = "covers/"; // Carpeta de portadas

    public GestorBiblioteca() {
        inventario = new ArrayList<>();
        cargarInventario();

        String sinopsis1 = "Una guía completa desde los conceptos básicos de variables hasta el desarrollo de interfaces gráficas. Perfecto para adentrarse al mundo de la Programación Orientada a Objetos sin conocimientos previos.";
        String sinopsis2 = "Descubre cómo organizar y manipular datos en memoria de manera eficiente. Aprende sobre pilas, colas, árboles y grafos para optimizar el rendimiento de tu software.";
        String sinopsis3 = "En la Tierra Media, el Señor Oscuro Sauron forjó un Anillo Único para dominar a todos. Un joven hobbit deberá emprender un viaje épico para destruirlo.";
        String sinopsis4 = "Comprende el funcionamiento de Internet y las redes locales. Desde la capa física hasta la capa de aplicación usando el modelo OSI y TCP/IP.";


        // Si el archivo no existe o está vacío, precargamos la base de datos de prueba
        if (inventario.isEmpty()) {
            inventario.add(new Libro("L001", "Java para Novatos", new ArrayList<>(Arrays.asList("Programación")), RUTA_COVERS + "Java_para_Novatos_cover.jpg", "Juan Pérez", 350, sinopsis1));
            inventario.add(new Libro("L002", "Estructuras de Datos", new ArrayList<>(Arrays.asList("Sistemas")), RUTA_COVERS + "Estructuras_de_Datos_cover.jpg", "María Gómez", 420, sinopsis2));
            inventario.add(new Libro("L003", "El Señor de los Anillos", new ArrayList<>(Arrays.asList("Fantasía")), RUTA_COVERS + "El_Señor_de_los_Anillos_cover.jpg", "J.R.R. Tolkien", 1200, sinopsis3));
            inventario.add(new Libro("L004", "Redes de Computadoras", new ArrayList<>(Arrays.asList("Sistemas", "Redes")), RUTA_COVERS + "Redes_de_Computadoras_cover.jpg", "Andrew Tanenbaum", 800, sinopsis4));
            actualizarLibro(); // Guarda los datos iniciales
        }
    }

    public ArrayList<Libro> obtenerLibros() { 
        return inventario; 
    }
    
    //Devuelve una lista con los libros que coinciden con el nombre buscado
    public ArrayList<Libro> buscarLibrosPorTitulo(String termino){
    	String terminoMin = termino.toLowerCase();
    	ArrayList<Libro> resultados = new ArrayList<>();
    	
    	for(Libro l : inventario) {
    		if(l.getTitulo().toLowerCase().contains(terminoMin)) {
    			resultados.add(l);
    		}
    	}
    	return resultados;
    }
    
    //Devuelve una lista con todas las categorias que se han guardado
    public ArrayList<String> obtenerCategoriasUnicas(){
    	ArrayList<String> categorias = new ArrayList<>();
    	
    	for(Libro libro : inventario) {
    		for(String cat : libro.getCategorias()) {
    			if(!categorias.contains(cat) && !cat.isEmpty()) {
    				categorias.add(cat);
    			}
    		}
    	}
    	return categorias; 
    }
    
    public void registrarLibro(Libro libro) {
    	inventario.add(libro);
    	actualizarLibro();
    }
    
    

    // Filtra la lista por categoría (o devuelve toda si es "Todas")
    public ArrayList<Libro> filtrarPorCategoria(String cat) {
        if (cat.equals("Todas")) return inventario;
        return (ArrayList<Libro>) inventario.stream()
                .filter(l -> l.getCategorias().contains(cat))
                .collect(Collectors.toList());
    }

    // Guarda el inventario actual en el archivo binario
    public void actualizarLibro() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVENTARIO))) {
            oos.writeObject(inventario);
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarInventario() {
        File f = new File(INVENTARIO);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            inventario = (ArrayList<Libro>) ois.readObject();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}

// =========================================================================
// CLASES DE MODELO (DATOS)
// Se implementa Serializable para poder guardarlas en archivos .dat
// =========================================================================

class Libro implements Serializable {
    // ID de serialización para asegurar la compatibilidad con el archivo avanzado
    private static final long serialVersionUID = 5L; 
    
    private String idLibro, titulo, rutaImagen, autor, sinopsis, matriculaPrestamo;
    private ArrayList<String> categorias;
    private int paginas;
    private boolean disponible = true;
    private ArrayList<String> usuariosQueGuardaron;

    public Libro(String id, String t, ArrayList<String> c, String r, String a, int p, String s) {
        this.idLibro = id; 
        this.titulo = t; 
        this.categorias = c; 
        this.rutaImagen = r; 
        this.autor = a; 
        this.paginas = p; 
        this.sinopsis = s;
        this.usuariosQueGuardaron = new ArrayList<>();
        
    }
    
    // Getters 
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getPaginas() { return paginas; }
    public String getSinopsis() { return sinopsis; }
    public String getRutaImagen() { return rutaImagen; }
    public ArrayList<String> getCategorias() { return categorias; }
    public boolean isDisponible() { return disponible; }
    public String getMatriculaPrestamo() { return matriculaPrestamo; }
    public String getCategoria() {
    	if(categorias != null && !categorias.isEmpty()) {
    		return categorias.get(0);
    	} else {
    		return "General";
    	}
    }
    public boolean estaGuardado(String matricula) { return usuariosQueGuardaron.contains(matricula); }
    
    // Setters
    public void setDisponible(boolean d) { this.disponible = d; }
    public void setMatriculaPrestamo(String m) { this.matriculaPrestamo = m; }
    public void toggleGuardado(String matricula) { 
    	if(estaGuardado(matricula)) {
    		usuariosQueGuardaron.remove(matricula);
    	} else {
    		usuariosQueGuardaron.add(matricula);
    	}
    }
    
}

abstract class Usuario implements Serializable {
    private String matricula, password, nombre;
    
    public Usuario(String m, String p, String n) { 
        this.matricula = m; 
        this.password = p; 
        this.nombre = n; 
    }
    
    public String getMatricula() { return matricula; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
}

// Herencia de usuarios
class Lector extends Usuario { 
    public Lector(String m, String p, String n) { 
        super(m, p, n); 
    } 
}


class Administrador extends Usuario { 
    public Administrador(String m, String p, String n) { 
        super(m, p, n); 
    } 
}



// Cargador de fuente personalizada
class CargarFuente {
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