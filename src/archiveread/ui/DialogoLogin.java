package archiveread.ui;

import archiveread.modelos.Usuario;
import archiveread.utils.CargarFuente;
import archiveread.utils.PaletaColores;
import archiveread.utils.UIUtils;
import archiveread.gestores.GestorUsuarios;

// Simplificamos llamada a librerias con .*
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//=========================================================================
// DialogoLogin
// Mini-ventana (Diálogo) para que los usuarios inicien sesión en el sistema
// =========================================================================

public class DialogoLogin extends JDialog {
	
	private Usuario usuarioAutenticado = null;
	  
	public DialogoLogin(JFrame padre, GestorUsuarios gestorUsuarios) {
		super(padre, "Acceso al Sistema", true);
		
		// Centra la ventana y evita que el usuario la haga grande o chica
		setSize(380, 320);
		setLocationRelativeTo(padre);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.WHITE);
		
		
		// --- Caja Prencipal ---
		JPanel pnlCentro = new JPanel();
		pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
		pnlCentro.setBackground(Color.WHITE);
		pnlCentro.setBorder(new EmptyBorder(30, 20, 20, 20));
		
		JLabel lblTitulo = UIUtils.crearLabel("Iniciar Sesión", CargarFuente.BOLD, 24f, PaletaColores.TEXTO_NEGRO);
		lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlCentro.add(lblTitulo);
		pnlCentro.add(Box.createVerticalStrut(15));
		
		// --- Cajitas de Texto (Formulario) ---
		JPanel pnlFormulario = new JPanel(new GridBagLayout());
		pnlFormulario.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints(); // GridBagConstraints ayuda a los botones y cajas de texto acomodarse en un lugar adecuado
		gbc.insets = new Insets(12, 10, 12, 10);
		
		// --- Fila de la Matricula ---
		gbc.gridx = 0; 
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		pnlFormulario.add(UIUtils.crearLabel("Matricula:", CargarFuente.REGULAR, 16f, PaletaColores.TEXTO_NEGRO), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		JTextField txtMatricula = new JTextField();
		txtMatricula.setFont(CargarFuente.getRegular(14f));
		txtMatricula.setPreferredSize(new Dimension(200, 30));
		txtMatricula.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 8, 5, 8)));
		pnlFormulario.add(txtMatricula, gbc);
		
		// --- Fila de la Contraseña ---
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		pnlFormulario.add(UIUtils.crearLabel("Contraseña:", CargarFuente.REGULAR, 16f, PaletaColores.TEXTO_NEGRO), gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		JPasswordField txtPass = new JPasswordField();
		txtPass.setPreferredSize(new Dimension(200, 30));
		txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 8, 5, 8)));
		pnlFormulario.add(txtPass, gbc);
		
		//Se mete la tabla a la caja principal
		pnlCentro.add(pnlFormulario);
		pnlCentro.add(Box.createVerticalStrut(10));
		
		// --- BOTON ENTRAR ---
		JButton btnEntrar = UIUtils.crearBotonEstandar("Entrar", PaletaColores.PRIMARIO, Color.WHITE);
		btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnEntrar.setPreferredSize(new Dimension(160, 45));
		btnEntrar.setMaximumSize(new Dimension(160, 45));
		
		//Si el usuario le da Clic:
		
		btnEntrar.addActionListener(e -> {
			String mat = txtMatricula.getText().trim();
			String pass = new String(txtPass.getPassword()).trim();
			
			Usuario u = gestorUsuarios.validarCredenciales(mat, pass);
			
			if(u != null) {
				//Encontró al usuario
				this.usuarioAutenticado = u;
				dispose();	//destruye la ventana
			}else {
				//No encontró al usuario
				JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		pnlCentro.add(btnEntrar);
		pnlCentro.add(Box.createVerticalStrut(15));
		
		// --- TEXTO PARA CREAR CUENTA NUEVA ---
		JLabel lblRegistro = UIUtils.crearLabel("¿No tienes cuenta?", CargarFuente.REGULAR, 13f, Color.GRAY);
		lblRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		//Si el usuario le da Clic al texto:
		lblRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				//Se abre otra ventana y se le pasa el archivo GestorUsuarios
				DialogoRegistro dialogReg = new DialogoRegistro(DialogoLogin.this, gestorUsuarios);
				dialogReg.setVisible(true);
			}
		});
		
		// Se mete el texto a la caja
		pnlCentro.add(lblRegistro);
		
		// Se mete la caja al centro de la ventana
		add(pnlCentro, BorderLayout.CENTER);		
	}
	
	//Funcion que regresa el usuario que entro, si cerraron la ventana, regresa null
	public Usuario getUsuarioAutentificado() {
		return usuarioAutenticado;
	}
}
