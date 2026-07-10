package archiveread.utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// =============================================
// DialogoModerno
// Utilidad para cuadros de diálogo personalizados
// =============================================
public class DialogoModerno {

    // Definimos los tipos de mensajes posibles
    public enum TipoMensaje {
        EXITO, ERROR, ADVERTENCIA, INFO, NINGUNO
    }

    // Método auxiliar para poner el icono de ArchiveRead en la barra superior de la ventanita
    private static void aplicarIconoVentana(JDialog dialogo) {
        try {
            Image iconoApp = new ImageIcon("icons/ArchiveRead_icon.png").getImage();
            dialogo.setIconImage(iconoApp);
        } catch (Exception e) {
            System.err.println("No se encontro el icono de la app para la ventana.");
        }
    }

    // 1. Mensajes (Éxito, Error, Info)
    public static void mostrarMensaje(Component padre, String titulo, String mensaje, TipoMensaje tipo) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = new JDialog(ventanaPadre, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        aplicarIconoVentana(dialogo); // <--- Inyectamos tu icono en la barra de título
        
        dialogo.setLayout(new BorderLayout());
        dialogo.getContentPane().setBackground(PaletaColores.BLANCO);
        dialogo.setResizable(false);
        
        JPanel pnlCentro = new JPanel(new BorderLayout(15, 15));
        pnlCentro.setBackground(PaletaColores.BLANCO);
        pnlCentro.setBorder(new EmptyBorder(30, 40, 20, 40));
        
        JLabel lblMensaje = new JLabel("<html><div style='text-align: center;'>" + mensaje + "</div></html>");
        lblMensaje.setFont(CargarFuente.getRegular(15f));
        lblMensaje.setForeground(PaletaColores.TEXTO_NEGRO);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        
        // --- LÓGICA DE ICONOS INTERNOS ---
        String rutaIcono = "";
        switch (tipo) {
            case EXITO: rutaIcono = "icons/icono_exito.png"; break;
            case ERROR: rutaIcono = "icons/icono_error.png"; break;
            case ADVERTENCIA: rutaIcono = "icons/icono_advertencia.png"; break;
            case INFO: rutaIcono = "icons/icono_info.png"; break;
            default: break;
        }
        
        if (!rutaIcono.isEmpty()) {
            // Reutilizamos tu escalador vectorial para que el icono se vea perfecto
            Icon iconoGrafico = UIUtils.escalarImagenAltaCalidad(rutaIcono, 40, 40);
            lblMensaje.setIcon(iconoGrafico);
            lblMensaje.setIconTextGap(15); // Separación entre el dibujo y el texto
        }
        
        pnlCentro.add(lblMensaje, BorderLayout.CENTER);
        
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBotones.setBackground(PaletaColores.BLANCO);
        pnlBotones.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JButton btnAceptar = UIUtils.crearBotonEstandar("Aceptar");
        btnAceptar.setPreferredSize(new Dimension(120, 35));
        btnAceptar.addActionListener(e -> dialogo.dispose());
        
        pnlBotones.add(btnAceptar);
        
        dialogo.add(pnlCentro, BorderLayout.CENTER);
        dialogo.add(pnlBotones, BorderLayout.SOUTH);
        
        dialogo.pack();
        dialogo.setSize(Math.max(400, dialogo.getWidth()), dialogo.getHeight());
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
    }

    // 2. Confirmación (Sí/No)
    public static boolean mostrarConfirmacion(Component padre, String titulo, String mensaje) {
        final boolean[] confirmado = {false};
        
        Window ventanaPadre = SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = new JDialog(ventanaPadre, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        aplicarIconoVentana(dialogo); // <--- Inyectamos tu icono en la barra de título
        
        dialogo.setLayout(new BorderLayout());
        dialogo.getContentPane().setBackground(PaletaColores.BLANCO);
        dialogo.setResizable(false);
        
        JPanel pnlCentro = new JPanel(new BorderLayout(15, 15));
        pnlCentro.setBackground(PaletaColores.BLANCO);
        pnlCentro.setBorder(new EmptyBorder(30, 40, 20, 40));
        
        JLabel lblMensaje = new JLabel("<html><div style='text-align: center;'>" + mensaje + "</div></html>");
        lblMensaje.setFont(CargarFuente.getRegular(15f));
        lblMensaje.setForeground(PaletaColores.TEXTO_NEGRO);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        
        // A la confirmación le ponemos el icono de advertencia por defecto
        Icon iconoGrafico = UIUtils.escalarImagenAltaCalidad("icons/icono_advertencia.png", 40, 40);
        lblMensaje.setIcon(iconoGrafico);
        lblMensaje.setIconTextGap(15);
        
        pnlCentro.add(lblMensaje, BorderLayout.CENTER);
        
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBotones.setBackground(PaletaColores.BLANCO);
        pnlBotones.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JButton btnCancelar = UIUtils.crearBotonEstandar("Cancelar");
        btnCancelar.setBackground(PaletaColores.BOTON_QUITAR);
        btnCancelar.setForeground(PaletaColores.TEXTO_NEGRO);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.addActionListener(e -> {
            confirmado[0] = false;
            dialogo.dispose();
        });
        
        JButton btnAceptar = UIUtils.crearBotonEstandar("Sí, continuar");
        btnAceptar.setPreferredSize(new Dimension(120, 35));
        btnAceptar.addActionListener(e -> {
            confirmado[0] = true;
            dialogo.dispose();
        });
        
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnAceptar);
        
        dialogo.add(pnlCentro, BorderLayout.CENTER);
        dialogo.add(pnlBotones, BorderLayout.SOUTH);
        
        dialogo.pack();
        dialogo.setSize(Math.max(450, dialogo.getWidth()), dialogo.getHeight());
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
        
        return confirmado[0];
    }
}