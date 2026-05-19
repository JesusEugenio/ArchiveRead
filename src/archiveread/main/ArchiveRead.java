// Archive Read - Version 1.3.0
// ================================
// Laura Alvarez y Jesus Eugenio
// ================================
package archiveread.main;

import java.awt.*;
import java.io.*;
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
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia maximizada

        panelPrincipal = new JPanel(new BorderLayout());
        setContentPane(panelPrincipal);

        // ---------------------------------------------------------
        // CONSTRUCCIÓN DEL ENCABEZADO (HEADER)
        // ---------------------------------------------------------
        panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBorder(new LineBorder(Color.LIGHT_GRAY));
        
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JLabel lblLogo = new JLabel("ArchiveRead");
        lblLogo.setFont(new Font("Tahoma", Font.BOLD, 18));
        pnlFiltros.add(lblLogo);

        JComboBox<String> comboCategorias = new JComboBox<>(new String[]{
            "Todas", "Programación", "Sistemas", "Fantasía", "Redes"
        });
        pnlFiltros.add(comboCategorias);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> {
            // Filtra y actualiza la cuadrícula de inmediato
            String categoria = comboCategorias.getSelectedItem().toString();
            mostrarCatalogo(gestorBiblioteca.filtrarPorCategoria(categoria));
        });
        pnlFiltros.add(btnFiltrar);

        // --- Lado Derecho (Usuario y Login) ---
        JPanel pnlUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        lblStatusUsuario = new JLabel(""); // Vacío por defecto (modo invitado)
        
        btnLoginHeader = new JButton("Iniciar Sesión");
        btnLoginHeader.addActionListener(e -> {
            if (usuarioActual == null) {
                abrirDialogoLogin();
            } else {
                cerrarSesion();
            }
        });

        pnlUsuario.add(lblStatusUsuario);
        pnlUsuario.add(btnLoginHeader);

        panelHeader.add(pnlFiltros, BorderLayout.WEST);
        panelHeader.add(pnlUsuario, BorderLayout.EAST);
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // Carga inicial de todos los libros
        mostrarCatalogo(gestorBiblioteca.obtenerLibros());
        
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
                lblStatusUsuario.setText("Bienvenido, " + u.getNombre());
                btnLoginHeader.setText("Cerrar Sesión");
                dialogLogin.dispose();
                
                // Recargamos pantalla para habilitar botones de devolver/rentar
                mostrarCatalogo(gestorBiblioteca.obtenerLibros());
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
        lblStatusUsuario.setText(""); 
        btnLoginHeader.setText("Iniciar Sesión");
        mostrarCatalogo(gestorBiblioteca.obtenerLibros());
    }

    // =========================================================================
    // RENDERIZADO DE LA PANTALLA 
    // =========================================================================
    
    private void mostrarCatalogo(ArrayList<Libro> librosMostrados) {
    	
    	JPanel panelGridTemporal = new JPanel(new GridLayout(0,5,20,20));
    	panelGridTemporal.setBorder(new EmptyBorder(20, 20, 20, 20));
    	
        for (Libro l : librosMostrados) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(new LineBorder(Color.LIGHT_GRAY));

            // Escalado de imagen
            ImageIcon icon = new ImageIcon(l.getRutaImagen());
            Image img = icon.getImage().getScaledInstance(140, 190, Image.SCALE_SMOOTH);
            JLabel lblImg = new JLabel(new ImageIcon(img));
            
            JPanel pnlInfo = new JPanel(new GridLayout(2, 1));
            JLabel lblT = new JLabel(l.getTitulo(), SwingConstants.CENTER);
            
            // Botón Dinámico según el estado del libro
            JButton btnAccion = new JButton();
            
            if (l.isDisponible()) {
                btnAccion.setText("Rentar");
                btnAccion.setEnabled(true);
                btnAccion.addActionListener(e -> rentarLibro(l));
                
            } else if (usuarioActual != null && usuarioActual.getMatricula().equals(l.getMatriculaPrestamo())) {
                // Si está ocupado pero el usuario actual fue quien lo rentó
                btnAccion.setText("Devolver");
                btnAccion.setEnabled(true);
                btnAccion.addActionListener(e -> devolverLibro(l));
                
            } else {
                // Si está ocupado por otro usuario o somos invitados
                btnAccion.setText("Prestado");
                btnAccion.setEnabled(false);
            }

            pnlInfo.add(lblT);
            pnlInfo.add(btnAccion);

            card.add(lblImg, BorderLayout.CENTER);
            card.add(pnlInfo, BorderLayout.SOUTH);
            panelGridTemporal.add(card);
        }
        
        // Envolvemos en un ScrollPane
        JScrollPane scrollPane = new JScrollPane(panelGridTemporal);
        scrollPane.setBorder(null);
       
        // Empacamos en un panel base para la navegacion entre ventanas
        JPanel panelBase = new JPanel(new BorderLayout());
        panelBase.add(scrollPane, BorderLayout.CENTER);
        cambiarVista(panelBase);
    }

    private void rentarLibro(Libro libro) {
        if (usuarioActual == null) {
            abrirDialogoLogin();
        } else {
            libro.setDisponible(false);
            libro.setMatriculaPrestamo(usuarioActual.getMatricula());
            gestorBiblioteca.actualizarLibro(); // Guarda en binario
            mostrarCatalogo(gestorBiblioteca.obtenerLibros()); // Refresca UI
        }
    }

    private void devolverLibro(Libro libro) {
        libro.setDisponible(true);
        libro.setMatriculaPrestamo(null);
        gestorBiblioteca.actualizarLibro(); // Guarda en binario
        mostrarCatalogo(gestorBiblioteca.obtenerLibros()); // Refresca UI
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

    // Getters y Setters necesarios
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getRutaImagen() { return rutaImagen; }
    public ArrayList<String> getCategorias() { return categorias; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean d) { this.disponible = d; }
    public String getMatriculaPrestamo() { return matriculaPrestamo; }
    public void setMatriculaPrestamo(String m) { this.matriculaPrestamo = m; }
    
    public String getCategoria() {
    	if(categorias != null && !categorias.isEmpty()) {
    		return categorias.get(0);
    	} else {
    		return "General";
    	}
    }

    public boolean estaGuardado(String matricula) { return usuariosQueGuardaron.contains(matricula); }
    
    public void toggleGuardado(String matricula) { 
    	if(estaGuardado(matricula)) {
    		usuariosQueGuardaron.remove(matricula);
    	}else {
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