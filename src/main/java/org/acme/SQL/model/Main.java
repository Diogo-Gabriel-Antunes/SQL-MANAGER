package org.acme.SQL.model;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Teste teste = new Teste();

        teste.teste = "New teste";
        teste.teste2 = 5;
        teste.teste3 = new ArrayList();
        teste.teste3.add("Teste");
        teste.teste3.add("Teste2");
        teste.teste3.add("Teste3");
    }
}
