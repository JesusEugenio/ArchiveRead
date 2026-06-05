package archiveread.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;

public class FileUtils {
	
	public static String limpiarNombreArchivo(String nombre) {
    	if(nombre == null) {
    		return "desconocido";
    	}
    	
    	// Normalizer.Form.NFD separa los acentos ortograficos de las letras para que quede 'limpio'
    	String normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD);
    	
    	// Mediantes RegEx eliminamos los acentos o marcas diacriticas ( ´ ¨ )
    	String sinAcentos = normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    	
    	// Borramos los espacios 
    	String sinEspacios = sinAcentos.replace(" ", "");
    	
    	// Mediante RegEx conservamos solo caracteres de la 'A' a la 'Z' y numeros del 0 al 9
    	// Eliminamos cualquier otro caracter que puede corromper la ruta del archivo
    	return sinEspacios.replaceAll("[^a-zA-Z0-9]", "");
    }
	
	// Renombra la ruta de la imagen que suba el Administrador y se copia en un directorio
	public static String guardarPortada(String rutaOrigen, String idLibro) {
    	if(rutaOrigen == null || rutaOrigen.contains("default.jpg")) {
    		return "covers/default.jpg";
    	}
    	try {
    		// archivoOrigen representa donde está la imagen en la PC en este momento
    		File archivoOrigen = new File(rutaOrigen);
    		if(!archivoOrigen.exists()) {
    			return rutaOrigen;
    		}
    		
    		// Extraemos la extension original (.jpg, .png)
    		String extension = "";
    		int i = rutaOrigen.lastIndexOf('.');
    		if(i > 0) {
    			extension = rutaOrigen.substring(i);
    		}
    		
    		String nuevoNombreArchivo = idLibro + "_cover" + extension;
    		
    		// Nos dirigimos a la carpeta covers del proyecto 
    		File carpetaCovers = new File("covers/");
    		// Si no existe la carpeta (por x razon), la creamos
    		if(!carpetaCovers.exists()) {
    			carpetaCovers.mkdirs();
    		}
    		
    		// archivoDestino representa dónde necesita tu programa que esté la imagen guardada para siempre
    		// Construimos el objeto con su ruta y el nombre que queremos que tenga
    		File archivoDestino = new File(carpetaCovers, nuevoNombreArchivo);
    		
    		// Si el archivo origen y el de destino son iguales, evita copiarlo para prevenir errores (el usuario escogio la portada desde la misma ruta /covers)
    		if(!archivoOrigen.getAbsolutePath().equals(archivoDestino.getAbsolutePath())) {
    			// Copiamos el archivo desde donde esta hasta donde lo vamos guardar
    			Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
    		}
    		
    		// Retornamos la nueva ruta para que el GestorBiblioteca la guarde en el archivo.dat
    		return archivoDestino.getPath();
    		
    	} catch(IOException e) {
    		e.printStackTrace();
    		return rutaOrigen; 
    	}
    }

}
