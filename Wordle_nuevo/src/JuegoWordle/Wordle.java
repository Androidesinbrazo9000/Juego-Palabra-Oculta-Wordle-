package JuegoWordle;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Wordle {

    private static final String FICHERO_PARTIDA = "partida.dat";
    private static final String FICHERO_PUNTUACIONES = "Puntuaciones.csv";
    // REQUISITO: Obligatorio almacenar puntuaciones en un HashMap
    private static HashMap<String, Integer> ranking = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        // Al arrancar, cargamos el historial de puntuaciones existentes
        cargarPuntuacionesCSV();

        do {
            System.out.println("\n--- MENÚ WORDLE ---");
            System.out.println("1- Cargar partida");
            System.out.println("2- Nueva partida");
            System.out.println("3- Consultar puntuaciones");
            System.out.println("0- Salir");
            System.out.print("Selecciona una opción: ");
            
            while (!sc.hasNextInt()) {
                System.out.print("Por favor, introduce un número válido: ");
                sc.next();
            }
            opcion = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    Partida partidaCargada = cargarPartidaBinaria();
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

        System.out.println("\n¡Comienza la partida! Escribe 'salir' en cualquier momento para guardar y marcharte.");
        System.out.println("Puntuación actual acumulada: " + partida.getPuntuacion());

        while (true) {
            System.out.print("\nIntroduce una palabra de 5 letras: ");
            respuesta = sc.nextLine().trim().toLowerCase();

            // REQUISITO: En cualquier momento el jugador podrá guardar y salir
            if (respuesta.equals("salir")) {
                guardarPartidaBinaria(partida);
                System.out.println("Partida guardada con éxito. Volviendo al menú principal.");
                return; // Rompe el bucle de juego y vuelve al menú
            }

            if (respuesta.length() != 5) {
                System.out.println("La palabra debe tener exactamente 5 letras.");
                continue;
            }

            String resultado = partida.comprobarIntento(respuesta);
            System.out.println("Resultado: " + resultado);
            System.out.println("Te quedan " + partida.getVida() + " intentos.");

            // Caso de Acierto
            if (partida.haAcertado(respuesta)) {
                partida.sumarPuntos(); // Suma 100 * vidas restantes
                System.out.println("¡Correcto! Nueva puntuación total: " + partida.getPuntuacion());
                System.out.println("Siguiente palabra...");
                partida.nuevaPalabra();
                continue;
            }

            // REQUISITO: El juego finaliza cuando no se es capaz de adivinar en los intentos max.
            if (partida.sinVidas()) {
                System.out.println("\n💥 ¡Game Over! Te has quedado sin vidas.");
                System.out.println("La palabra oculta era: " + partida.getPalabraSecreta());
                System.out.println("Puntuación final alcanzada: " + partida.getPuntuacion());
                
                // Procesar el registro del jugador
                gestionarFinJuego(partida.getPuntuacion(), sc);
                
                // Borramos el archivo de partida guardada si existía, ya que esta terminó por completo
                File f = new File(FICHERO_PARTIDA);
                if (f.exists()) f.delete();
                
                break; // Volvemos al menú principal
            }
        }
    }

    // --- GESTIÓN DE FICHEROS BINARIOS (PARTIDA) ---
    private static void guardarPartidaBinaria(Partida partida) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHERO_PARTIDA))) {
            oos.writeObject(partida);
        } catch (IOException e) {
            System.out.println("Error al guardar el estado de la partida: " + e.getMessage());
        }
    }

    private static Partida cargarPartidaBinaria() {
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

    // --- GESTIÓN DE PUNTUACIONES (CSV + HASHMAP) ---
    private static void gestionarFinJuego(int puntuacionFinal, Scanner sc) {
        System.out.print("¿Deseas registrar tu puntuación? (s/n): ");
        String responder = sc.nextLine().trim().toLowerCase();
        
        if (responder.equals("s")) {
            System.out.print("Introduce tu nombre de usuario: ");
            String usuario = sc.nextLine().trim();

            // REQUISITO: Validar si ya existía y si la puntuación actual es mayor
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

            // REQUISITO: Tras actualizar el HashMap, se vuelca al archivo CSV
            guardarPuntuacionesCSV();
        }
    }

    private static void cargarPuntuacionesCSV() {
        File archivo = new File(FICHERO_PUNTUACIONES);
        if (!archivo.exists()) return; // Si no existe el CSV aún, no pasa nada.

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
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar el histórico de puntuaciones.");
        }
    }

    private static void guardarPuntuacionesCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHERO_PUNTUACIONES))) {
            for (Map.Entry<String, Integer> entrada : ranking.entrySet()) {
                pw.println(entrada.getKey() + "," + entrada.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error al escribir en Puntuaciones.csv.");
        }
    }

    private static void mostrarPuntuaciones() {
        System.out.println("\n--- RANKING DE JUGADORES ---");
        if (ranking.isEmpty()) {
            System.out.println("No hay puntuaciones registradas todavía.");
            return;
        }
        for (Map.Entry<String, Integer> entrada : ranking.entrySet()) {
            System.out.println("Jugador: " + entrada.getKey() + " | Puntuación Máxima: " + entrada.getValue());
        }
    }
}