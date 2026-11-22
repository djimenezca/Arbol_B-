import java.util.ArrayList;

public class ArbolBMas {

    // Atributos
    private final int orden;
    private NodoArbolBMas raiz;

    // Métodos
    // Constructor
    public ArbolBMas(int orden) {
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    public int getOrden() {
        return orden;
    }

    public boolean estaVacio() {
        return raiz.getClaves().isEmpty();
    }

    // Cantidad mínima de claves de un nodo distinto de la raíz: ceil(m/2) - 1
    private int minimoClaves() {
        return (orden + 1) / 2 - 1;
    }

    // Posición del hijo por el que se debe bajar para encontrar una clave
    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.getClaves().size() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // Baja desde la raíz hasta la hoja donde está (o estaría) una clave
    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.getHijos().get(indiceHijo(actual, clave));
        }
        return actual;
    }

    // INSERCIÓN

    // Inserción de una clave con su dato. Retorna false si la clave ya existe.
    public boolean insertar(int clave, String dato) {
        if (buscar(clave)) {
            return false;
        }

        // Se inserta primero de forma recursiva
        insertarRecursivo(this.raiz, clave, dato);

        // Se verifica si la raíz se desbordó
        if (this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return true;
    }

    // Inserción recursiva de una clave
    // Permite que los nodos se desborden temporalmente y los divide al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        int i = nodo.getClaves().size() - 1;

        if (nodo.esHoja()) {
            while (i >= 0 && clave < nodo.getClaves().get(i)) {
                i--;
            }
            // El dato se guarda en la misma posición que su clave
            nodo.getClaves().add(i + 1, clave);
            nodo.getDatos().add(i + 1, dato);
        } else {
            while (i >= 0 && clave < nodo.getClaves().get(i)) {
                i--;
            }
            i++;
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la clave primero en el hijo correspondiente
            insertarRecursivo(hijo, clave, dato);

            // Evaluación de desbordamiento al retornar de la recursividad
            if (hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            int total = nodoLleno.getClaves().size();

            // La segunda mitad de claves y datos pasa a la nueva hoja
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, total)));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // La nueva hoja queda enlazada entre la hoja dividida y su antigua siguiente
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador la primera clave del nuevo nodo (se copia, no se mueve)
            int claveMedia = nuevoNodo.getClaves().getFirst();
            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        } else {
            // En nodos internos, la clave media sube y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    // BÚSQUEDA

    // En un B+ la búsqueda siempre termina en una hoja, que es donde están los datos
    public boolean buscar(int clave) {
        return obtenerDato(clave) != null;
    }

    // Retorna el dato asociado a la clave, o null si no existe
    public String obtenerDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int pos = hoja.getClaves().indexOf(clave);
        return pos >= 0 ? hoja.getDatos().get(pos) : null;
    }

    // ELIMINACIÓN

    // Eliminación de una clave (y su dato). Retorna false si la clave no existe.
    public boolean eliminar(int clave) {
        if (!eliminarRecursivo(raiz, clave)) {
            return false;
        }

        // Si la raíz interna se quedó sin claves, su único hijo pasa a ser la raíz (el árbol baja un nivel)
        if (!raiz.esHoja() && raiz.getClaves().isEmpty()) {
            raiz = raiz.getHijos().getFirst();
        }

        // La clave borrada pudo quedar como separador en un nodo interno
        actualizarSeparador(raiz, clave);
        return true;
    }

    // Eliminación recursiva: borra en la hoja y corrige los subdesbordamientos al regresar
    private boolean eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            int pos = nodo.getClaves().indexOf(clave);
            if (pos < 0) {
                return false;
            }
            nodo.getClaves().remove(pos);
            nodo.getDatos().remove(pos);
            return true;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);
        boolean eliminado = eliminarRecursivo(hijo, clave);

        // Si el hijo quedó con menos claves de las permitidas, se rebalancea
        if (eliminado && hijo.getClaves().size() < minimoClaves()) {
            rebalancear(nodo, i);
        }
        return eliminado;
    }

    // Corrige un hijo con pocas claves: primero intenta pedir prestado a un hermano, si no, fusiona
    private void rebalancear(NodoArbolBMas padre, int i) {
        NodoArbolBMas hijo = padre.getHijos().get(i);
        NodoArbolBMas izquierdo = (i > 0) ? padre.getHijos().get(i - 1) : null;
        NodoArbolBMas derecho = (i < padre.getHijos().size() - 1) ? padre.getHijos().get(i + 1) : null;

        if (izquierdo != null && izquierdo.getClaves().size() > minimoClaves()) {
            prestarDeIzquierdo(padre, i, izquierdo, hijo);
        } else if (derecho != null && derecho.getClaves().size() > minimoClaves()) {
            prestarDeDerecho(padre, i, hijo, derecho);
        } else if (izquierdo != null) {
            fusionar(padre, i - 1);
        } else {
            fusionar(padre, i);
        }
    }

    // Pasa la última clave del hermano izquierdo al inicio del hijo
    private void prestarDeIzquierdo(NodoArbolBMas padre, int i, NodoArbolBMas izquierdo, NodoArbolBMas hijo) {
        if (hijo.esHoja()) {
            hijo.getClaves().addFirst(izquierdo.getClaves().removeLast());
            hijo.getDatos().addFirst(izquierdo.getDatos().removeLast());
            padre.getClaves().set(i - 1, hijo.getClaves().getFirst());
        } else {
            // En nodos internos la clave rota a través del padre
            hijo.getClaves().addFirst(padre.getClaves().get(i - 1));
            padre.getClaves().set(i - 1, izquierdo.getClaves().removeLast());
            hijo.getHijos().addFirst(izquierdo.getHijos().removeLast());
        }
    }

    // Pasa la primera clave del hermano derecho al final del hijo
    private void prestarDeDerecho(NodoArbolBMas padre, int i, NodoArbolBMas hijo, NodoArbolBMas derecho) {
        if (hijo.esHoja()) {
            hijo.getClaves().addLast(derecho.getClaves().removeFirst());
            hijo.getDatos().addLast(derecho.getDatos().removeFirst());
            padre.getClaves().set(i, derecho.getClaves().getFirst());
        } else {
            hijo.getClaves().addLast(padre.getClaves().get(i));
            padre.getClaves().set(i, derecho.getClaves().removeFirst());
            hijo.getHijos().addLast(derecho.getHijos().removeFirst());
        }
    }

    // Une el hijo j con el hijo j+1 y quita del padre el separador entre ambos
    private void fusionar(NodoArbolBMas padre, int j) {
        NodoArbolBMas izquierdo = padre.getHijos().get(j);
        NodoArbolBMas derecho = padre.getHijos().get(j + 1);

        if (izquierdo.esHoja()) {
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            // Se salta la hoja eliminada en la lista enlazada
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos el separador del padre baja al nodo fusionado
            izquierdo.getClaves().add(padre.getClaves().get(j));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }

        padre.getClaves().remove(j);
        padre.getHijos().remove(j + 1);
    }

    // Reemplaza un separador obsoleto por la clave más pequeña de su subárbol derecho
    private void actualizarSeparador(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            return;
        }
        int pos = nodo.getClaves().indexOf(clave);
        if (pos >= 0) {
            NodoArbolBMas actual = nodo.getHijos().get(pos + 1);
            while (!actual.esHoja()) {
                actual = actual.getHijos().getFirst();
            }
            nodo.getClaves().set(pos, actual.getClaves().getFirst());
            return;
        }
        actualizarSeparador(nodo.getHijos().get(indiceHijo(nodo, clave)), clave);
    }

    // RECORRIDO POR RANGO

    // Busca la clave inicial e imprime los siguientes n elementos usando el enlace entre hojas.
    // Si la clave no existe, el recorrido empieza en la primera clave mayor.
    public void recorrerRango(int claveInicial, int n) {
        NodoArbolBMas hoja = buscarHoja(claveInicial);
        int pos = 0;
        while (pos < hoja.getClaves().size() && hoja.getClaves().get(pos) < claveInicial) {
            pos++;
        }

        if (!hoja.getClaves().contains(claveInicial)) {
            System.out.println("La clave " + claveInicial + " no existe; se muestran las siguientes claves mayores.");
        }

        int impresos = 0;
        while (hoja != null && impresos < n) {
            if (pos < hoja.getClaves().size()) {
                System.out.println("  " + hoja.getClaves().get(pos) + " -> " + hoja.getDatos().get(pos));
                impresos++;
                pos++;
            } else {
                hoja = hoja.getSiguiente();
                pos = 0;
            }
        }

        if (impresos == 0) {
            System.out.println("No hay elementos a partir de esa clave.");
        } else if (impresos < n) {
            System.out.println("Solo había " + impresos + " elemento(s) disponibles.");
        }
    }

    // IMPRESIÓN

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // Recorre todas las hojas de izquierda a derecha usando el enlace "siguiente"
    public void imprimirHojas() {
        NodoArbolBMas hoja = raiz;
        while (!hoja.esHoja()) {
            hoja = hoja.getHijos().getFirst();
        }
        StringBuilder sb = new StringBuilder();
        while (hoja != null) {
            sb.append(hoja.getClaves()).append(hoja.getSiguiente() != null ? " -> " : "");
            hoja = hoja.getSiguiente();
        }
        System.out.println(sb);
    }
}
