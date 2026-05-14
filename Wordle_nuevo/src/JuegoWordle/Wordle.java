package JuegoWordle;
import java.util.Scanner;
public class Wordle {

	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		int opcion;

		do {
			System.out.println("1- Cargar partida");
			System.out.println("2- Nueva partida");
			System.out.println("3- Consultar puntuaciones");
			System.out.println("0- Salir");
			opcion = sc.nextInt();

			switch(opcion) {
			case 1:

				break;
			case 2:
			    Partida partida = new Partida();
			    String palabraSecreta = partida.obtenerPalabraSecreta();
			    //Luego lo quitamos, es para ver que funciona.
			    System.out.println("La palabra secreta es: " + palabraSecreta);
			    break;
			case 3: 
				
				break;
			case 0: 
				System.out.println("Saliendo del juego. ¡Gracias por jugar!");
				break;
			default:
				System.out.println("Esa opción no es válida, introduce una que sea correcta.");
				break;
			}
		}while(opcion != 0);	
	}

}