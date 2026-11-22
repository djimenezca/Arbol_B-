import java.util.Scanner;

/**
 * Programa de consola para interactuar con un árbol B+.
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("-----------------------------------------");
        System.out.println("        SIMULADOR DE ÁRBOL B+");
        System.out.println("-----------------------------------------");

        // El orden mínimo es 3 para que las divisiones y fusiones tengan sentido
        int orden = leerEntero(sc, "Ingrese el orden del árbol (mínimo 3, recomendado 4): ");
        while (orden < 3) {
            orden = leerEntero(sc, "El orden debe ser al menos 3. Intente de nuevo: ");
        }

        ArbolBMas arbol = new ArbolBMas(orden);
        menu(arbol, sc);

        sc.close();
    }

    // Menú principal: se repite hasta que el usuario elige salir
    private static void menu(ArbolBMas arbol, Scanner sc) {
        int opcion;
        do {
            System.out.println();
            System.out.println("------------ MENÚ (orden " + arbol.getOrden() + ") ------------");
            System.out.println("1. Insertar un elemento");
            System.out.println("2. Eliminar un elemento");
            System.out.println("3. Buscar un elemento");
            System.out.println("4. Recorrer un rango de elementos");
            System.out.println("5. Mostrar la estructura del árbol");
            System.out.println("6. Mostrar las hojas enlazadas");
            System.out.println("7. Cargar datos de ejemplo");
            System.out.println("0. Salir");
            opcion = leerEntero(sc, "Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> opcionInsertar(arbol, sc);
                case 2 -> opcionEliminar(arbol, sc);
                case 3 -> opcionBuscar(arbol, sc);
                case 4 -> opcionRecorrerRango(arbol, sc);
                case 5 -> {
                    if (verificarNoVacio(arbol)) arbol.imprimirArbol();
                }
                case 6 -> {
                    if (verificarNoVacio(arbol)) arbol.imprimirHojas();
                }
                case 7 -> cargarEjemplo(arbol);
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción inválida. Elija un número del 0 al 7.");
            }
        } while (opcion != 0);
    }

    private static void opcionInsertar(ArbolBMas arbol, Scanner sc) {
        int clave = leerEntero(sc, "Clave (número entero): ");
        String dato = leerTexto(sc, "Dato asociado (texto): ");
        if (arbol.insertar(clave, dato)) {
            System.out.println("Elemento " + clave + " insertado correctamente.");
        } else {
            System.out.println("La clave " + clave + " ya existe en el árbol.");
        }
    }

    private static void opcionEliminar(ArbolBMas arbol, Scanner sc) {
        if (!verificarNoVacio(arbol)) return;
        int clave = leerEntero(sc, "Clave a eliminar: ");
        if (arbol.eliminar(clave)) {
            System.out.println("Elemento " + clave + " eliminado. Árbol resultante:");
            if (!arbol.estaVacio()) arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionBuscar(ArbolBMas arbol, Scanner sc) {
        if (!verificarNoVacio(arbol)) return;
        int clave = leerEntero(sc, "Clave a buscar: ");
        if (arbol.buscar(clave)) {
            System.out.println("Encontrado: " + clave + " -> " + arbol.obtenerDato(clave));
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionRecorrerRango(ArbolBMas arbol, Scanner sc) {
        if (!verificarNoVacio(arbol)) return;
        int clave = leerEntero(sc, "Clave inicial: ");
        int n = leerEntero(sc, "Cantidad de elementos a mostrar: ");
        while (n <= 0) {
            n = leerEntero(sc, "La cantidad debe ser mayor que 0: ");
        }
        arbol.recorrerRango(clave, n);
    }

    // Inserta los mismos valores del ejemplo original visto en clase, ahora con datos
    private static void cargarEjemplo(ArbolBMas arbol) {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50};
        String[] datos = {"Ana", "Luis", "María", "Carlos", "Sofía", "Pedro", "Lucía", "Jorge", "Elena", "Diego"};
        int insertados = 0;
        for (int i = 0; i < claves.length; i++) {
            if (arbol.insertar(claves[i], datos[i])) insertados++;
        }
        System.out.println(insertados + " elemento(s) de ejemplo cargados. Árbol resultante:");
        arbol.imprimirArbol();
    }

    private static boolean verificarNoVacio(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío. Inserte elementos primero (opción 1 o 7).");
            return false;
        }
        return true;
    }

    // Lee un entero validando la entrada para que el programa no se caiga con texto
    private static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: debe ingresar un número entero.");
            }
        }
    }

    // Lee un texto que no esté vacío
    private static String leerTexto(Scanner sc, String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = sc.nextLine().trim();
        } while (texto.isEmpty());
        return texto;
    }
}
