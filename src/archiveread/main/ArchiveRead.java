// Archive Read - Version 1.6.0
// ================================
// Laura Alvarez y Jesus Eugenio
// ================================
package archiveread.main;

import archiveread.modelos.*;
import archiveread.gestores.*;
import archiveread.utils.*;
import archiveread.ui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;


// =========================================================================
// CLASE PRINCIPAL
// Controla la Interfaz Grafica principal de la aplicacion
// =========================================================================
public class ArchiveRead extends JFrame {
    // Paneles principales
    private JPanel panelPrincipal;
    private JPanel panelContenidoCentro; // Controla el intercambio entre vistas
    private JPanel panelHeader;
    
    // Gestores de datos
    private GestorBiblioteca gestorBiblioteca;
    private GestorUsuarios gestorUsuarios;
    private GestorReviews gestorReviews;      // Instancia del gestor de reviews
    private GestorReportes gestorReportes;    // Instancia del gestor de reportes
    
    // Estado de la sesión actual
    private Usuario usuarioActual = null; 

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
        gestorReviews = new GestorReviews();      
        gestorReportes = new GestorReportes();    

        // Configuración de la ventana
        setTitle("ArchiveRead");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia maximizada

        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(PaletaColores.FONDO_PRINCIPAL);
        setContentPane(panelPrincipal);
        
        panelContenidoCentro = new JPanel(new BorderLayout());
        panelContenidoCentro.setBackground(PaletaColores.FONDO_PRINCIPAL);
        panelPrincipal.add(panelContenidoCentro, BorderLayout.CENTER);
        
        actualizarHeader();
        mostrarCatalogo("Todas");
        
        // Asegurar que el scroll y la ventana inicien enfocados arriba
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                panelPrincipal.setFocusable(true);
                panelPrincipal.requestFocus();
            }
        });
    }
    
    // =========================================================================
    // NAVEGACION Y CAMBIO DE VISTAS
    // ========================================================================
    
    private void cambiarVista(JPanel nuevoPanel) {
    	// Si ya hay un panel en el centro, lo quitamos de la pantalla principal
    	if(panelContenidoCentro != null) {
    		panelPrincipal.remove(panelContenidoCentro);
    	}
    	
    	// Asignamos el nuevo panel y lo agregamos en el centro del layout
    	panelContenidoCentro = nuevoPanel;
    	panelPrincipal.add(panelContenidoCentro, BorderLayout.CENTER);
    	
    	// Forzamos a que se dibuje de nuevo la interfaz
    	panelPrincipal.revalidate();
    	panelPrincipal.repaint();
    }

    // =========================================================================
    // MÉTODOS DE SESIÓN
    // =========================================================================
    
    private void abrirDialogoLogin() {
    	/* No podemos hacer una funcion que retorne el usuario por que detendria todo el programa esperando el usuario,
    	 * por eso usamos un Consumer, al que se le asignan las instrucciones de u y que va a inyectar el resultado obtenido
    	 * cuando el login salga bien y mientras tanto el Main puede ejecutar otras acciones */
    	
    	// Las instrucciones de u son las que ejecuta 'onLoginExitoso' en DialogoLogin.java y las inyecta al Main
    	// 'u ->' significa: el dato que inyecte de este Callback se llamara 'u' 
    	DialogoLogin dialogLogin = new DialogoLogin(this, gestorUsuarios, u -> {
    		usuarioActual = u; 
    		actualizarHeader();
    		mostrarCatalogo("Todas");
    	});
    	
    	dialogLogin.setVisible(true);
    }
    
    // Cerramos sesion y regresamos la interfaz al modo invitado
    private void cerrarSesion() {
        usuarioActual = null;
        actualizarHeader();
        mostrarCatalogo("Todas");
    }

    // =========================================================================
    // RENDERIZADO DE LAS VISTAS DE LA PANTALLA 
    // =========================================================================
    
    // Pedimos la lista de libros al gestor y se la mandamos a VistaListaLibros pa que la dibuje
    private void mostrarCatalogo(String categoriaFiltro) {
    	String tituloCatalogo = categoriaFiltro.equals("Todas") ?
    			"Libros Recientes" : "Libros de " + categoriaFiltro + ": ";
    	
    	ArrayList<Libro> libros = gestorBiblioteca.filtrarPorCategoria(categoriaFiltro);
    	
    	// Inyectamos las acciones logicas al panel grafico
    	cambiarVista(new VistaListaLibros(
    			tituloCatalogo, libros, categoriaFiltro, gestorBiblioteca.obtenerCategoriasUnicas(),
    			cat -> mostrarCatalogo(cat),
    			() -> gestorReportes.generarReporteCategorias(this, gestorBiblioteca),
    			() -> gestorReportes.generarReporteAutores(this, gestorBiblioteca), 
    			libro -> mostrarDetalleLibro(libro)
    	));
    }
    
    // Reconstruimos la barra superior dependiendo de si alguien esta logeado o no
    private void actualizarHeader() {
    	// Para refrescar la vista cuando sucedan cambios hechos en el menu principal
    	if (panelHeader != null) panelPrincipal.remove(panelHeader);
    	
    	// Creamos el Header y le pasamos las recetas con las acciones que debe ejecutar
    	panelHeader = new PanelHeader(
    			usuarioActual, 
    			() -> mostrarCatalogo("Todas"), 
    			() -> mostrarMiBiblioteca(), 
    			() -> abrirDialogoLogin(), 
    			() -> cerrarSesion()		
    	);
    	
    	panelPrincipal.add(panelHeader, BorderLayout.NORTH);
    	panelPrincipal.revalidate();
    	panelPrincipal.repaint();
    }
    
    // Crea la pantalla de detalles de un libro especifico y la muestra
    public void mostrarDetalleLibro(Libro libro) {
    	// Pasamos todos los eventos hacia la vista de detalles
    	cambiarVista(new VistaDetalleLibro(
    			libro,
    			usuarioActual, 
    			() -> mostrarCatalogo("Todas"), 
    			() -> {rentarLibro(libro); mostrarDetalleLibro(libro); }, 
    			() -> {devolverLibro(libro); mostrarDetalleLibro(libro); }, 
    			() -> {
    				if (usuarioActual == null) {
    					abrirDialogoLogin();
    				} else {
    					libro.toggleGuardado(usuarioActual.getMatricula());
    					gestorBiblioteca.actualizarLibro();
    					mostrarDetalleLibro(libro);
    				}
    			}, 
    			(panelLista, lib) -> gestorReviews.cargarReviews(lib, panelLista)	
    	));
    }
    
    
    // Metodo para navegar a la biblioteca personal del usuario [EN DESARROLLO]
    public void mostrarMiBiblioteca() {
        mostrarCatalogo("Todas");
    }
    
   
    // =========================================================================
    // METODOS DE GESTION DE LIBROS
    // =========================================================================
    
    private void rentarLibro(Libro libro) {
        if (usuarioActual == null) {
            abrirDialogoLogin();
        } else {
            libro.setDisponible(false);
            libro.setMatriculaPrestamo(usuarioActual.getMatricula());
            gestorBiblioteca.actualizarLibro(); 
            mostrarCatalogo("Todas"); 
        }
    }

    private void devolverLibro(Libro libro) {
        libro.setDisponible(true);
        libro.setMatriculaPrestamo(null);
        gestorBiblioteca.actualizarLibro(); 
        mostrarCatalogo("Todas"); 
    }

}