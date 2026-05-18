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
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    private String palabraSecreta;
    private int puntuacion = 0;
    private int vida = 6;
    private HashSet<String> palabras = new HashSet<>();

    public Partida() {
        cargarPalabras();
        this.palabraSecreta = obtenerPalabraSecreta();
    }

    public int getPuntuacion() {
        return this.puntuacion;
    }

    public int getVida() {
        return this.vida;
    }

    public String getPalabraSecreta() {
        return this.palabraSecreta;
    }

    public void sumarPuntos() {
        puntuacion = puntuacion + (vida * 100);
    }

    public void nuevaPalabra() {
        this.palabraSecreta = obtenerPalabraSecreta();
        this.vida = 6;
    }

    public boolean haAcertado(String intento) {
        return intento.equalsIgnoreCase(palabraSecreta);
    }

    public boolean sinVidas() {
        return vida <= 0;
    }

    public void cargarPalabras() {
        BufferedReader br = null;
        String[] arrayPalabras;

        try {
            // REQUISITO: Nombre exacto del archivo exigido
            br = new BufferedReader(new FileReader("Palabras5L.txt"));
            String linea = br.readLine();
            if (linea != null) {
                arrayPalabras = linea.split(",");
                for (int i = 0; i < arrayPalabras.length; i++) {
                    palabras.add(arrayPalabras[i].trim().toLowerCase());
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("No se ha podido encontrar el archivo Palabras5L.txt.");
        } catch (IOException ex) {
            System.out.println("Error al leer las palabras.");
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                System.out.println("Error en el cierre del lector.");
            }
        }
    }

    public String obtenerPalabraSecreta() {
        if (palabras.isEmpty()) return "casas"; // Salvaguarda por si el archivo está vacío
        ArrayList<String> lista = new ArrayList<>(palabras);
        Collections.shuffle(lista);
        return lista.get(0);
    }

    public String comprobarIntento(String intento) {
        intento = intento.toLowerCase();
        String resultado = "";

        // REQUISITO: Colores solicitados (Verde y Amarillo)
        final String VERDE = "\u001B[32m";
        final String AMARILLO = "\u001B[33m";
        final String RESET = "\u001B[0m";

        // Cada intento consume una vida independientemente de si acierta o falla después
        vida--;

        ArrayList<Character> letrasSecreta = new ArrayList<>();
        for (int i = 0; i < palabraSecreta.length(); i++) {
            letrasSecreta.add(palabraSecreta.charAt(i));
        }

        char[] colorLetra = new char[5];

        // Primera pasada: Detectar los verdes exactos
        for (int i = 0; i < 5; i++) {
            if (intento.charAt(i) == palabraSecreta.charAt(i)) {
                colorLetra[i] = 'v';
                letrasSecreta.set(i, null); // Consumimos la letra para evitar duplicados amarillos
            }
        }

        // Segunda pasada: Detectar amarillos o grises (no existe)
        for (int i = 0; i < 5; i++) {
            if (colorLetra[i] == 'v') continue;

            char letra = intento.charAt(i);
            if (letrasSecreta.contains(letra)) {
                colorLetra[i] = 'a'; // Amarillo
                letrasSecreta.remove((Character) letra); // Consumimos una instancia de la letra
            } else {
                colorLetra[i] = 'n'; // No existe
            }
        }

        // Construcción del String coloreado
        for (int i = 0; i < 5; i++) {
            char letra = intento.charAt(i);
            if (colorLetra[i] == 'v') {
                resultado += VERDE + letra + RESET;
            } else if (colorLetra[i] == 'a') {
                resultado += AMARILLO + letra + RESET;
            } else {
                resultado += letra;
            }
        }

        return resultado;
    }
}