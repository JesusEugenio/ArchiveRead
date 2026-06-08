package archiveread.ui;

import archiveread.gestores.GestorUsuarios;
import archiveread.utils.CargarFuente;
import archiveread.utils.PaletaColores;
import archiveread.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// =========================================================================
// DialogoRegistro
// Ventana emergente para registrar un nuevo usuario (lector) en el sistema
// =========================================================================

public class DialogoRegistro extends JDialog {

    public DialogoRegistro(JDialog parent, GestorUsuarios gestorUsuarios) {
        // Modalidad activada para bloquear el Login de fondo
        super(parent, "Registro de Nuevo Usuario", true);
        setSize(400, 380); 
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setBackground(Color.WHITE);
        pnlCentro.setBorder(new EmptyBorder(30, 20, 20, 20));

        JLabel lblTitulo = UIUtils.crearLabel("Crear Cuenta", CargarFuente.BOLD, 24f, PaletaColores.TEXTO_NEGRO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlCentro.add(lblTitulo);
        pnlCentro.add(Box.createVerticalStrut(30));

        // =====================================================================
        // FORMULARIO: GridBagLayout (Para alinear a 2 columnas y 3 filas)
        // =====================================================================
        JPanel pnlFormulario = new JPanel(new GridBagLayout());
        pnlFormulario.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); 

        // --- FILA 1: Nombre ---
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        pnlFormulario.add(UIUtils.crearLabel("Nombre:", CargarFuente.REGULAR, 16f, PaletaColores.TEXTO_NEGRO), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField txtNombre = new JTextField();
        txtNombre.setFont(CargarFuente.getRegular(14f));
        txtNombre.setPreferredSize(new Dimension(200, 30));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 8, 5, 8)));
        pnlFormulario.add(txtNombre, gbc);

        // --- FILA 2: Matrícula ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        pnlFormulario.add(UIUtils.crearLabel("Matrícula:", CargarFuente.REGULAR, 16f, PaletaColores.TEXTO_NEGRO), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField txtMatricula = new JTextField();
        txtMatricula.setFont(CargarFuente.getRegular(14f));
        txtMatricula.setPreferredSize(new Dimension(200, 30));
        txtMatricula.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 8, 5, 8)));
        pnlFormulario.add(txtMatricula, gbc);

        // --- FILA 3: Contraseña ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        pnlFormulario.add(UIUtils.crearLabel("Contraseña:", CargarFuente.REGULAR, 16f, PaletaColores.TEXTO_NEGRO), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JPasswordField txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(200, 30));
        txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 8, 5, 8)));
        pnlFormulario.add(txtPass, gbc);

        pnlCentro.add(pnlFormulario);
        pnlCentro.add(Box.createVerticalStrut(25));

        // --- BOTON REGISTRARSE ---
        JButton btnRegistrar = UIUtils.crearBotonEstandar("Registrarse", PaletaColores.PRIMARIO, Color.WHITE);
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrar.setPreferredSize(new Dimension(150, 45));
        btnRegistrar.setMaximumSize(new Dimension(160, 45));
        
        btnRegistrar.addActionListener(e -> {
            String nom = txtNombre.getText().trim();
            String mat = txtMatricula.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();

            // Validación que evita guardar usuarios con datos en blanco
            if (nom.isEmpty() || mat.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return; // Corta la ejecución aqui si faltan datos
            }

            // Enviamos los datos al GestorUsuarios
            boolean exito = gestorUsuarios.registrarLector(mat, pass, nom);

            // Analizamos la respuesta del Gestor
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Cuenta creada exitosamente! Ya puedes iniciar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra el registro y libera la pantalla para volver al Login
            } else {
                // Si 'exito' es falso, significa que la matrícula ya existía en el HashMap
                JOptionPane.showMessageDialog(this, "La matrícula " + mat + " ya está registrada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlCentro.add(btnRegistrar);
        add(pnlCentro, BorderLayout.CENTER);
    }
}