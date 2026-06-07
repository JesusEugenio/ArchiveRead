package archiveread.gestores;

import archiveread.modelos.*;
import java.io.*;
import java.util.HashMap;

// =========================================================================
//	--- Gestor de Usuarios ---
//	Controla el registro, el login y la carga/guardado de los usuarios en "usuarios.dat"
// =========================================================================

public class GestorUsuarios {
    private HashMap<String, Usuario> usuariosRegistrados;		// HashMap para buscar usuarios por matricula
    private final String ARCHIVO_USUARIOS = "usuarios.dat";
    
    // --- Constructor --- 
    public GestorUsuarios() {
        usuariosRegistrados = new HashMap<>();
        
        // Al iniciar el gestor, cargamoss los usuarios que ya existan
        cargarUsuarios();
              
        //En la primera ejecucion (sin usuarios), creamos al admin
        if(usuariosRegistrados.isEmpty()) {
        	Administrador adminPorDefecto = new Administrador("admin", "admin123", "Administrador");
        	usuariosRegistrados.put(adminPorDefecto.getMatricula(), adminPorDefecto);
        	guardarUsuarios();	
        }
        
    }
    
    // =========================================================================
    // REGISTRA UN NUEVO LECTOR
    // ==========================================================================
    
    // Crea una cuenta nueva para un lector normal
    public boolean registrarLector(String matricula, String password, String nombre) {
    	//Si la matricula ya existe, rechaza el registro
    	if(usuariosRegistrados.containsKey(matricula)) {
    		return false;
    	}
    	
    	//Instancia el lector usando su constructor 
    	Lector nuevoLector = new Lector(matricula, password, nombre);
    	usuariosRegistrados.put(matricula, nuevoLector);
    	guardarUsuarios();
    	return true;
    	
    }
    
    // =========================================================================
    // INICIAR SESIÓN
    // ==========================================================================
    
    // Verifica si la matrícula y contraseña coinciden con alguna cuenta guardada
    public Usuario validarCredenciales(String matricula, String password) {
    	Usuario u = usuariosRegistrados.get(matricula);
    	
    	// Verifica que el usuario exista y que su contraseña coincida
    	if(u != null && u.getPassword().equals(password)) {
    		return u;	// Si todo está bien, devuelve la cuenta del usuario
    	}
    	
    	// Matricula o contraseña incorrecta
    	return null;
    }
    
    // =========================================================================
    // BÚSQUEDA RÁPIDA DE USUARIO
    // =========================================================================
    
    // Permite encontrar a un usuario al instante sin usar ciclos 'for'
    public Usuario buscarPorMatricula(String matricula) {
        // Como usamos un HashMap, no necesitamos un ciclo 'for'
        return usuariosRegistrados.get(matricula);
    }
    
    // =========================================================================
    // 	CARGA Y GUARDADO EN BINARIOS
    // ==========================================================================
    
    // Guarda el HashMap completo en el archivo 'usuarios.dat'
    private void guardarUsuarios() {
    	try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))){
    		oos.writeObject(usuariosRegistrados);
    	}catch(IOException e) {
    		System.err.println("Error al guardar usuarios: " + e.getMessage());
    	}
    }
    
    // Lee el archivo 'usuarios.dat' y lo convierte de nuevo en un HashMap
    @SuppressWarnings("unchecked")
    private void cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (archivo.exists()) {
        	try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))){
        		usuariosRegistrados = (HashMap<String, Usuario>) ois.readObject();
        	}catch(IOException | ClassNotFoundException e) {
        		System.err.println("Error al cargar usuarios: " + e.getMessage());
        		usuariosRegistrados = new HashMap<>();	// Si falla, inicia desde cero
        	}
        }
    }
}