import java.util.ArrayList;
import java.util.List;

public class NodoArbolBMas {

    // Atributos
    private boolean esHoja;
    private List<Integer> listaClaves;
    private List<NodoArbolBMas> listaHijos; // Vacía si es una hoja
    private List<String> datos;             // null si es un nodo interno
    private NodoArbolBMas siguiente;        // null si es un nodo interno

    // Métodos
    // Constructor: solo las hojas reservan espacio para los datos
    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.listaClaves = new ArrayList<>();
        this.listaHijos = new ArrayList<>();
        this.datos = esHoja ? new ArrayList<>() : null;
        this.siguiente = null;
    }

    // Getters
    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return listaClaves;
    }

    public List<NodoArbolBMas> getHijos() {
        return listaHijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    // Setters
    public void setClaves(List<Integer> listaClaves) {
        this.listaClaves = listaClaves;
    }

    public void setHijos(List<NodoArbolBMas> listaHijos) {
        this.listaHijos = listaHijos;
    }

    public void setDatos(List<String> datos) {
        this.datos = datos;
    }

    public void setSiguiente(NodoArbolBMas siguiente) {
        this.siguiente = siguiente;
    }
}
