package archiveread.ui;

import archiveread.modelos.*;
import archiveread.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// =============================================
// VistaListaLibros
// dibuja la parte superior de la app (Titulo de la app, inicio de sesion, etc.)
// =============================================

public class PanelHeader extends JPanel {
	
	public PanelHeader(Usuario usuarioActual, Consumer<String> onBuscar, Runnable onLogoClick, Runnable onMiBibliotecaClick, Runnable onIngresarClick, Runnable onSalirClick,
						Runnable onAnadirLibroClick, Runnable onReportePrestamosClick) {
		// Runnables : Header no sabe que pasa cuando hacemos click en esas partes, solo sabe que debe ejecutar la accion que le mandaron
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 60));
		setBackground(PaletaColores.BLANCO);
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, PaletaColores.BORDE_CLARO), 
				new EmptyBorder(5, 20, 0, 20)                                          
		));
		
		// --- Panel Izquierdo (Logo y Navegación) ---
		JPanel pnlIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        pnlIzquierda.setBackground(PaletaColores.BLANCO);
        
        JLabel lblLogo = new JLabel("ArchiveRead");
        lblLogo.setFont(CargarFuente.getBold(22f));
        lblLogo.setForeground(PaletaColores.PRIMARIO);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onLogoClick.run(); }
        });
        pnlIzquierda.add(lblLogo);
        pnlIzquierda.add(UIUtils.crearMenuLabel("Mi biblioteca", onMiBibliotecaClick));
        
        JPanel pnlDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlDerecha.setBackground(PaletaColores.BLANCO);
        
        // Evaluamos si el usuario que existe es Admin ("Soy Admin.png")
        if (usuarioActual instanceof Administrador) {
        	// Si el usuario es una instancia de Administrador añadimos los siguientes botones solo para el 
        	pnlIzquierda.add(UIUtils.crearMenuLabel("Añadir Libro ", onAnadirLibroClick));
        	pnlIzquierda.add(UIUtils.crearMenuLabel("ReportePrestamos ", onReportePrestamosClick));
        }
        
        // --- Panel Central (Barra de Búsqueda Moderna) ---
        JPanel pnlCentro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 7));
        pnlCentro.setBackground(PaletaColores.BLANCO);
        
        // Sobreescribimos la caja de texto para dibujar el Placeholder "Buscar..."
        JTextField txtBuscar = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Si la caja está vacía, dibujamos el texto fantasma
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // Suavizado para que las letras se vean nítidas
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 150, 150)); // Gris claro
                    g2.setFont(CargarFuente.getItalic(14f));
                    
                    // Cálculo matemático para centrar el texto verticalmente en cualquier pantalla
                    int y = (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
                    g2.drawString("Buscar...", 12, y); // 12 es el margen izquierdo
                    g2.dispose();
                }
            }
        };
        
        txtBuscar.setFont(CargarFuente.getRegular(14f));
        txtBuscar.setPreferredSize(new Dimension(280, 35));
        txtBuscar.setBackground(PaletaColores.FONDO_AREA);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
    			new LineBorder(PaletaColores.BORDE_CLARO, 1, true),
    			new EmptyBorder(0, 10, 0, 10)
    	));
        txtBuscar.setToolTipText("Buscar...");
        
        // Escuchador en tiempo real para capturar cada tecla presionada o borrada
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { accionar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { accionar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { accionar(); }
            
            private void accionar() {
                // invokeLater asegura que Swing actualice los hilos gráficos de forma segura
                SwingUtilities.invokeLater(() -> onBuscar.accept(txtBuscar.getText()));
            }
        });
        pnlCentro.add(txtBuscar);
        
        // --- Panel Derecho (Sesión) ---
        // Evaluamos si existe una sesion iniciada o no hay nadie aun
        if (usuarioActual == null) {
            JButton btnIngresar = UIUtils.crearBotonEstandar("Ingresar", PaletaColores.COLOR_FONDO_BOX, PaletaColores.TEXTO_GRIS_OSCURO);
            
            btnIngresar.addActionListener(e -> onIngresarClick.run());
            pnlDerecha.add(btnIngresar);
            
        } else {
            pnlDerecha.add(UIUtils.crearLabel("Hola, " + usuarioActual.getNombre(), CargarFuente.BOLD, 14f, Color.BLACK));
            JButton btnSalir = UIUtils.crearBotonEstandar("Cerrar Sesión", PaletaColores.PRIMARIO, PaletaColores.TEXTO_BLANCO);
            btnSalir.addActionListener(e -> onSalirClick.run());
            pnlDerecha.add(btnSalir);
        }

        add(pnlIzquierda, BorderLayout.WEST);
        add(pnlCentro, BorderLayout.CENTER);
        add(pnlDerecha, BorderLayout.EAST);
        
	}

}
