package archiveread.ui;

import archiveread.modelos.Usuario;
import archiveread.gestores.GestorUsuarios;

import java.util.function.Consumer;

// Simplificamos llamada a librerias con .*
import javax.swing.*;
import java.awt.*;

public class DialogoLogin extends JDialog {
	  
	public DialogoLogin(JFrame padre, GestorUsuarios gestorUsuarios, Consumer<Usuario> onLoginExitoso) {
		super(padre, "Login", true);
		setSize(300, 180);
		setLocationRelativeTo(padre);
		setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
	
		JPanel pnlInputs = new JPanel(new GridLayout(2, 2, 5, 10));
        JTextField txtMatricula = new JTextField(12);
        JPasswordField txtPassword = new JPasswordField(12);
        
        pnlInputs.add(new JLabel("Matrícula:"));
        pnlInputs.add(txtMatricula);
        pnlInputs.add(new JLabel("Contraseña:"));
        pnlInputs.add(txtPassword);
        
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> {
            Usuario u = gestorUsuarios.validarUsuario(txtMatricula.getText(), new String(txtPassword.getPassword()));
            if (u != null) {
                // Consumer funciona como Runnable, la diferencia es que Consumer si envia parametros para ejecutar una accion
            	onLoginExitoso.accept(u); // Ejecuta la accion aqui 
            	dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
            }
        });

        add(pnlInputs);
        add(btnEntrar);
	}
}
