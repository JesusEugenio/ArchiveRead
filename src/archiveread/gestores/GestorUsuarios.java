package archiveread.gestores;

import archiveread.modelos.*;
import java.io.*;
import java.util.HashMap;

// =========================================================================
//	CLASE: GESTOR DE USUARIOS
//	Encargada de leer y escribir 'usuarios.dat'
// =========================================================================

public class GestorUsuarios {
    private HashMap<String, Usuario> usuariosRegistrados;		//HashMap para buscar usuarios por matricula
    private final String ARCHIVO_USUARIOS = "usuarios.dat";
    

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
    // REGISTRAR LECTOR (crear cuenta)
    // ==========================================================================
    
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
    
    //Verifica si la matrícula y contraseña coinciden con alguna cuenta guardada
    public Usuario validarCredenciales(String matricula, String password) {
    	Usuario u = usuariosRegistrados.get(matricula);
    	
    	//Usamos getPassword() de la clase Usuario
    	if(u != null && u.getPassword().equals(password)) {
    		return u;
    	}
    	
    	//Matricula o contraseña incorrecta
    	return null;
    }
    
    // =========================================================================
    // BÚSQUEDA RÁPIDA DE USUARIO
    // =========================================================================
    
    public Usuario buscarPorMatricula(String matricula) {
        // Como usamos un HashMap, no necesitamos un ciclo 'for'
        return usuariosRegistrados.get(matricula);
    }
    
    // =========================================================================
    // 	PERSISTENCIA (Guardar y Cargar .dat)
    // ==========================================================================
    
    private void guardarUsuarios() {
    	try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))){
    		oos.writeObject(usuariosRegistrados);
    	}catch(IOException e) {
    		System.err.println("Error al guardar usuarios: " + e.getMessage());
    	}
    }
    

    @SuppressWarnings("unchecked")
    private void cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (archivo.exists()) {
        	try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))){
        		usuariosRegistrados = (HashMap<String, Usuario>) ois.readObject();
        	}catch(IOException | ClassNotFoundException e) {
        		System.err.println("Error al cargar usuarios: " + e.getMessage());
        		usuariosRegistrados = new HashMap<>();
        	}
        }
    }
}