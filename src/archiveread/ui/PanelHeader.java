package archiveread.ui;

import archiveread.modelos.Usuario;
import archiveread.utils.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// ===================
// Esta clase dibuja la parte superior de la app (Titulo de la app, donde inicias sesion, etc.)
// ==================

public class PanelHeader extends JPanel {
	
	public PanelHeader(Usuario usuarioActual, Runnable onLogoClick, Runnable onMiBibliotecaClick, Runnable onIngresarClick, Runnable onSalirClick) {
		// Runnables : Header no sabe que pasa cuando hacemos click en esas partes, solo sabe que debe ejecutar la accion que le mandaron
		
		setLayout(new BorderLayout());
		setBackground(PaletaColores.BLANCO);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, PaletaColores.BORDE_CLARO));
		
		JPanel pnlIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        pnlIzquierda.setBackground(PaletaColores.BLANCO);
        
        JLabel lblLogo = new JLabel("ArchiveRead");
        lblLogo.setFont(CargarFuente.get(CargarFuente.BOLD, 22f));
        lblLogo.setForeground(PaletaColores.PRIMARIO);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onLogoClick.run(); }
        });
        pnlIzquierda.add(lblLogo);
        pnlIzquierda.add(UIUtils.crearMenuLabel("Mi biblioteca", onMiBibliotecaClick));
        
        JPanel pnlDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlDerecha.setBackground(PaletaColores.BLANCO);
        
        if (usuarioActual == null) {
            JButton btnIngresar = UIUtils.crearBotonEstandar("Ingresar", PaletaColores.COLOR_FONDO_BOX, PaletaColores.TEXTO_GRIS_OSCURO);
            
            btnIngresar.addActionListener(e -> onIngresarClick.run());
            pnlDerecha.add(btnIngresar);
            
        } else {
            pnlDerecha.add(UIUtils.crearLabel("Hola, " + usuarioActual.getNombre(), CargarFuente.BOLD, 14f, Color.BLACK));
            JButton btnSalir = UIUtils.crearBotonEstandar("Cerrar Sesión", null, PaletaColores.TEXTO_GRIS_OSCURO);
            btnSalir.addActionListener(e -> onSalirClick.run());
            pnlDerecha.add(btnSalir);
        }

        add(pnlIzquierda, BorderLayout.WEST);
        add(pnlDerecha, BorderLayout.EAST);
        
	}

}
