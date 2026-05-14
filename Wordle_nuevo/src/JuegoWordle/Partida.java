package JuegoWordle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Partida implements Serializable {


	private int puntuacion;
	private HashSet<String> palabras = new HashSet<>();
	
	public Partida () {
		cargarPalabras();
	}

	public void cargarPalabras () {
		BufferedReader br = null;
		String [] arrayPalabras;

		try {
			br = new BufferedReader(new FileReader ("Palabras.txt"));
			String linea = br.readLine();
			arrayPalabras = linea.split(",");
			for (int i = 0; i < arrayPalabras.length; i++) {
				arrayPalabras[i] = arrayPalabras[i].trim();
				palabras.add(arrayPalabras[i]);
			}
		} catch (FileNotFoundException ex) {
			System.out.println("No se han podido encontrar las palabras secretas.");
		} catch (IOException ex) {
			System.out.println("Error.");
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				System.out.println("Error en el cierre del BR.");
			}
		}
	}
	
    public String obtenerPalabraSecreta() {
        ArrayList<String> lista = new ArrayList<>(palabras);
        Collections.shuffle(lista);
        return lista.get(0);
    }

}