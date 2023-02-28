package org.example;

public class Nodo<T>{

        public Nodo siguiente;
        public Nodo izquierda;
        public Nodo derecha;
        public T dato;
        public Nodo(T x, Nodo sig, Nodo izq, Nodo der) {
            dato = x;
            siguiente = sig;
            izquierda = izq;
            derecha = der;
    }


}
