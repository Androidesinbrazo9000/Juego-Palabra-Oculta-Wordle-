package JuegoWordle;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Wordle {

    private static final String FICHERO_PARTIDA = "Partida.dat";
    private static final String FICHERO_PUNTUACIONES = "Puntuaciones.csv";
    private static HashMap<String, Integer> ranking = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        cargarPuntuacionesCSV();

        do {
            System.out.println("--- MENÚ WORDLE ---");
            System.out.println("1- Cargar partida");
            System.out.println("2- Nueva partida");
            System.out.println("3- Consultar puntuaciones");
            System.out.println("0- Salir");
            System.out.print("Selecciona una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    Partida partidaCargada = cargarPartida();
                    if (partidaCargada != null) {
                        jugar(partidaCargada);
                    }
                    break;

                case 2:
                    Partida nuevaPartida = new Partida();
                    jugar(nuevaPartida);
                    break;

                case 3:
                    mostrarPuntuaciones();
                    break;

                case 0:
                    System.out.println("Saliendo del juego. ¡Gracias por jugar!");
                    break;

                default:
                    System.out.println("Esa opción no es válida.");
                    break;
            }
        } while (opcion != 0);
    }

    public static void jugar(Partida partida) {
        Scanner sc = new Scanner(System.in);
        String respuesta = "";

        System.out.println("¡Comienza la partida! Escribe 'salir' en cualquier momento para guardar y marcharte.");
        System.out.println("Puntuación actual acumulada: " + partida.getPuntuacion());

        while (true) {
            System.out.print("Introduce una palabra de 5 letras: ");
            respuesta = sc.nextLine();

            if (respuesta.equals("salir")) {
                guardarPartida(partida);
                System.out.println("Partida guardada con éxito. Volviendo al menú principal.");
                return;
            }

            if (respuesta.length() != 5) {
                System.out.println("La palabra debe tener exactamente 5 letras.");
                continue;
            }

            String resultado = partida.comprobarIntento(respuesta);
            System.out.println("Resultado: " + resultado);
            System.out.println("Te quedan " + partida.getVida() + " intentos.");

            if (partida.haAcertado(respuesta)) {
                partida.sumarPuntos();
                System.out.println("¡Correcto! Nueva puntuación total: " + partida.getPuntuacion());
                System.out.println("Siguiente palabra...");
                partida.nuevaPalabra();
                continue;
            }

            if (partida.sinVidas()) {
                System.out.println("\n💥 ¡Game Over! Te has quedado sin vidas.");
                System.out.println("La palabra oculta era: " + partida.getPalabraSecreta());
                System.out.println("Puntuación final alcanzada: " + partida.getPuntuacion());
                
                gestionarFinJuego(partida.getPuntuacion(), sc);
                
                File f = new File(FICHERO_PARTIDA);
                if (f.exists()) f.delete();
                
                break;
            }
        }
    }

    private static void guardarPartida(Partida partida) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHERO_PARTIDA))) {
            oos.writeObject(partida);
        } catch (IOException e) {
            System.out.println("Error al guardar el estado de la partida: " + e.getMessage());
        }
    }

    private static Partida cargarPartida() {
        File archivo = new File(FICHERO_PARTIDA);
        if (!archivo.exists()) {
            System.out.println("No hay ninguna partida guardada previamente.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            System.out.println("Partida restaurada con éxito.");
            return (Partida) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar la partida guardada: " + e.getMessage());
            return null;
        }
    }

    private static void gestionarFinJuego(int puntuacionFinal, Scanner sc) {
        System.out.print("¿Deseas registrar tu puntuación? (s/n): ");
        String responder = sc.nextLine().trim().toLowerCase();
        
        if (responder.equals("s")) {
            System.out.print("Introduce tu nombre de usuario: ");
            String usuario = sc.nextLine().trim();

            if (ranking.containsKey(usuario)) {
                int puntuacionAnterior = ranking.get(usuario);
                if (puntuacionFinal > puntuacionAnterior) {
                    ranking.put(usuario, puntuacionFinal);
                    System.out.println("¡Nuevo récord personal para " + usuario + "!");
                } else {
                    System.out.println("Tu puntuación actual (" + puntuacionFinal + ") no supera tu récord registrado (" + puntuacionAnterior + ").");
                }
            } else {
                ranking.put(usuario, puntuacionFinal);
                System.out.println("Usuario registrado correctamente.");
            }

            guardarPuntuacionesCSV();
        }
    }

    private static void cargarPuntuacionesCSV() {
        File archivo = new File(FICHERO_PUNTUACIONES);

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    String usuario = partes[0].trim();
                    int puntos = Integer.parseInt(partes[1].trim());
                    ranking.put(usuario, puntos);
                }
            }
        } catch (FileNotFoundException e) {
        	System.out.println("No se encontró el fichero de puntuaciones.");       
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar el histórico de puntuaciones.");
        }
    }

    private static void guardarPuntuacionesCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FICHERO_PUNTUACIONES))) {

            for (Map.Entry<String, Integer> entrada : ranking.entrySet()) {
                bw.write(entrada.getKey() + "," + entrada.getValue());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error al escribir en Puntuaciones.csv.");
        }
    }

    private static void mostrarPuntuaciones() {
        System.out.println("--- RANKING DE JUGADORES ---");
        if (ranking.isEmpty()) {
            System.out.println("No hay puntuaciones registradas todavía.");
            return;
        }
        for (Map.Entry<String, Integer> entrada : ranking.entrySet()) {
            System.out.println("Jugador: " + entrada.getKey() + " | Puntuación Máxima: " + entrada.getValue());
        }
    }
}