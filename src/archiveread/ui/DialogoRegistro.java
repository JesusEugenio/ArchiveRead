package archiveread.ui;

import archiveread.gestores.GestorUsuarios;
import archiveread.utils.CargarFuente;
import archiveread.utils.PaletaColores;
import archiveread.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class DialogoRegistro extends JDialog {
	
	/* @param parent 
	@param gestorUsuarios*/
	
	public DialogoRegistro(JDialog parent, GestorUsuarios gestorUsuarios) {
		super(parent, "Registro de Nuevo Usuario", true);	//La ventana se configura como modal (true)
		setSize(350, 500);
		setLocationRelativeTo(parent);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.WHITE);
		
		JPanel pnlCentro = new JPanel();
		pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
		pnlCentro.setBackground(Color.WHITE);
		pnlCentro.setBorder(new EmptyBorder(30, 40, 30, 40));
		
		JLabel lblTitulo = UIUtils.crearLabel("Crear Cuenta", CargarFuente.getBold(22f), PaletaColores.TEXTO_NEGRO);
		lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlCentro.add(lblTitulo);
		pnlCentro.add(Box.createVerticalStrut(30));
		
		
		// --- CAMPO: NOMBRE COMPLETO ---
		JLabel lblNombre = UIUtils.crearLabel("Nombre Completo: ", CargarFuente.getRegular(14f), PaletaColores.TEXTO_NEGRO); 
		lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
		JTextField txtNombre = UIUtils.crearTextFieldFormulario();
		txtNombre.setMaximumSize(new Dimension(250, 35));
		
		// --- CAMPO: MATRICULA ---
		JLabel lblMatricula = UIUtils.crearLabel("Matricula: ", CargarFuente.getRegular(14f), PaletaColores.TEXTO_NEGRO);
		lblMatricula.setAlignmentX(Component.CENTER_ALIGNMENT);
		JTextField txtMatricula = UIUtils.crearTextFieldFormulario();
		txtMatricula.setMaximumSize(new Dimension(250, 35));
		
		// --- CAMPO: MATRICULA ---
		JLabel lblPass = UIUtils.crearLabel("Contraseña: ", CargarFuente.getRegular(14f), PaletaColores.TEXTO_NEGRO);
		lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPasswordField txtPass = new JPasswordField();
		txtPass.setMaximumSize(new Dimension(250, 35));
		txtPass.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PaletaColores.BORDE_CLARO, 1),
				new EmptyBorder(5, 10, 5, 10)
		));
		
		pnlCentro.add(lblNombre);
		pnlCentro.add(Box.createVerticalStrut(5));
		pnlCentro.add(txtNombre);
		pnlCentro.add(Box.createVerticalStrut(15));
		
		pnlCentro.add(lblMatricula);
		pnlCentro.add(Box.createVerticalStrut(5));
		pnlCentro.add(txtMatricula);
		pnlCentro.add(Box.createVerticalStrut(15));
		
		pnlCentro.add(lblPass);
		pnlCentro.add(Box.createVerticalStrut(5));
		pnlCentro.add(txtPass);
		pnlCentro.add(Box.createVerticalStrut(30));
		
		
		// --- BOTÓN DE ACCIÓN FINAL ---
		JButton btnRegistrar = UIUtils.crearBotonEstandar("Registrarse", PaletaColores.PRIMARIO, Color.WHITE);
		btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnRegistrar.setMaximumSize(new Dimension(150, 40));
		
		btnRegistrar.addActionListener(e -> {
			String nom = txtNombre.getText().trim();
			String mat = txtMatricula.getText().trim();
			String pass = new String(txtPass.getPassword()).trim();
			
			if(nom.isEmpty() || mat.isEmpty() || pass.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			
			// El gestor intenta guardar al lector
			boolean exito = gestorUsuarios.registrarLector(mat, pass, nom);
			
			if(exito) {
				JOptionPane.showMessageDialog(this, "¡Cuenta creada exitosamente! Ta puedes inicar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(this, "La matrícula: " + mat + " ya está registrada. ", "Error", JOptionPane.ERROR_MESSAGE);
			}	
		});
		
		pnlCentro.add(btnRegistrar);
		add(pnlCentro, BorderLayout.CENTER);	
	}
}
