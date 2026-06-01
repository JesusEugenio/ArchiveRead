package archiveread.gestores;

import archiveread.modelos.*;
import java.io.*;
import java.util.ArrayList;

// =========================================================================
//	CLASE: GESTOR DE USUARIOS
//	Encargada de leer y escribir 'usuarios.dat'
// =========================================================================

public class GestorUsuarios {
    private ArrayList<Usuario> usuariosRegistrados;
    private final String ARCHIVO_USUARIOS = "usuarios.dat";

    public GestorUsuarios() {
        usuariosRegistrados = new ArrayList<>();
        cargarUsuariosBinario();
    }

    @SuppressWarnings("unchecked")
    private void cargarUsuariosBinario() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            usuariosRegistrados.add(new Administrador("admin", "admin123", "Administrador"));
            usuariosRegistrados.add(new Lector("548821", "1234", "Jesus Eugenio"));
            guardarUsuariosBinario();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            usuariosRegistrados = (ArrayList<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void guardarUsuariosBinario() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))) {
            oos.writeObject(usuariosRegistrados);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Usuario validarUsuario(String matricula, String password) {
        for (Usuario u : usuariosRegistrados) {
            if (u.getMatricula().equals(matricula) && u.getPassword().equals(password)) return u;
        }
        return null;
    }
    
    public Usuario buscarPorMatricula(String matricula) {
        for (Usuario u : usuariosRegistrados) {
            if (u.getMatricula().equals(matricula)) return u;
        }
        return null;
    }

    public boolean registrarNuevoUsuario(Usuario nuevoUsuario) {
        for (Usuario u : usuariosRegistrados) {
            if (u.getMatricula().equals(nuevoUsuario.getMatricula())) return false; 
        }
        usuariosRegistrados.add(nuevoUsuario);
        guardarUsuariosBinario();
        return true;
    }
}