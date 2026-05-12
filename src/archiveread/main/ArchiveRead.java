package archiveread.main;

import java.awt.*;
import java.io.*;
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
    private JPanel panelLibrosGrid;
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
            actualizarPantalla(gestorBiblioteca.filtrarPorCategoria(categoria));
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

        // ---------------------------------------------------------
        // CONSTRUCCIÓN DE LA CUADRÍCULA CENTRAL (GRID)
        // ---------------------------------------------------------
        panelLibrosGrid = new JPanel(new GridLayout(0, 5, 20, 20)); // 5 columnas, filas dinámicas (0)
        panelLibrosGrid.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Agregamos scroll por si hay muchos libros
        JScrollPane scrollPane = new JScrollPane(panelLibrosGrid);
        scrollPane.setBorder(null);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Carga inicial de todos los libros
        actualizarPantalla(gestorBiblioteca.obtenerLibros());
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
                actualizarPantalla(gestorBiblioteca.obtenerLibros());
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
        actualizarPantalla(gestorBiblioteca.obtenerLibros());
    }

    // =========================================================================
    // MÉTODOS DE RENDERIZADO Y LÓGICA DE NEGOCIO
    // =========================================================================
    
    // Dibuja las tarjetas de los libros enviados por parámetro
    private void actualizarPantalla(ArrayList<Libro> librosMostrados) {
        panelLibrosGrid.removeAll(); // Limpia la pantalla
        
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
            panelLibrosGrid.add(card);
        }
        
        // Refresca la ventana para aplicar los cambios visuales
        panelLibrosGrid.revalidate();
        panelLibrosGrid.repaint();
    }

    private void rentarLibro(Libro libro) {
        if (usuarioActual == null) {
            abrirDialogoLogin();
        } else {
            libro.setDisponible(false);
            libro.setMatriculaPrestamo(usuarioActual.getMatricula());
            gestorBiblioteca.actualizarLibro(); // Guarda en binario
            actualizarPantalla(gestorBiblioteca.obtenerLibros()); // Refresca UI
        }
    }

    private void devolverLibro(Libro libro) {
        libro.setDisponible(true);
        libro.setMatriculaPrestamo(null);
        gestorBiblioteca.actualizarLibro(); // Guarda en binario
        actualizarPantalla(gestorBiblioteca.obtenerLibros()); // Refresca UI
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

    public Libro(String id, String t, ArrayList<String> c, String r, String a, int p, String s) {
        this.idLibro = id; 
        this.titulo = t; 
        this.categorias = c; 
        this.rutaImagen = r; 
        this.autor = a; 
        this.paginas = p; 
        this.sinopsis = s;
    }

    // Getters y Setters necesarios
    public String getTitulo() { return titulo; }
    public String getRutaImagen() { return rutaImagen; }
    public ArrayList<String> getCategorias() { return categorias; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean d) { this.disponible = d; }
    public String getMatriculaPrestamo() { return matriculaPrestamo; }
    public void setMatriculaPrestamo(String m) { this.matriculaPrestamo = m; }
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