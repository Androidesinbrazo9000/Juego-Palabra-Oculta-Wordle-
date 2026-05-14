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

    private String palabraSecreta;
    private int puntuacion = 0;
    private int vida = 6;
    private HashSet<String> palabras = new HashSet<>();

    public Partida () {
        cargarPalabras();
        this.palabraSecreta = obtenerPalabraSecreta();
    }

    public int getPuntuacion () {
        return this.puntuacion;
    }

    public int getVida () {
        return this.vida;
    }

    public String getPalabraSecreta () {
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

    public void cargarPalabras () {
        BufferedReader br = null;
        String [] arrayPalabras;

        try {
            br = new BufferedReader(new FileReader ("Palabras.txt"));
            String linea = br.readLine();
            arrayPalabras = linea.split(",");
            for (int i = 0; i < arrayPalabras.length; i++) {
                arrayPalabras[i] = arrayPalabras[i].trim().toLowerCase();
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

    public String comprobarIntento(String intento) {

        intento = intento.toLowerCase();
        String resultado = "";

        final String VERDE = "\u001B[32m";
        final String ROJO = "\u001B[31m";
        final String RESET = "\u001B[0m";

        //Dividimos la palabra en una lista de caracteres.
        ArrayList<Character> letrasSecreta = new ArrayList<>();
        for (int i = 0; i < palabraSecreta.length(); i++) {
            letrasSecreta.add(palabraSecreta.charAt(i));
        }

        //Hacemos un array de caracteres para clasificar las letras con colores
        char[] colorLetra = new char[5];

        //Buscamos las verdes
        for (int i = 0; i < 5; i++) {
            if (intento.charAt(i) == palabraSecreta.charAt(i)) {
                colorLetra[i] = 'v';
                letrasSecreta.set(i, null);
            }
        }

        //Buscamos rojas o negras
        for (int i = 0; i < 5; i++) {
            if (colorLetra[i] == 'v')
                continue;

            char letra = intento.charAt(i);

            if (letrasSecreta.contains(letra)) {
                colorLetra[i] = 'r';
            } else {
                colorLetra[i] = 'n';
            }
        }

        //Construimos el resultado coloreado
        for (int i = 0; i < 5; i++) {
            char letra = intento.charAt(i);

            if (colorLetra[i] == 'v') {
                resultado = resultado + VERDE + letra + RESET;
            } else if (colorLetra[i] == 'r') {
                resultado = resultado + ROJO + letra + RESET;
            } else {
                resultado = resultado + letra;
            }
        }

        if (!intento.equals(palabraSecreta)) {
            vida--;
        }

        return resultado;
    }

}