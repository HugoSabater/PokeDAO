package org.hugo.pojo;

import java.util.Random;

public class LuchaPokemon {
    public static void lucha(Pokemon p1, Pokemon p2) {
        System.out.println("\n⚔️ COMBATE: " + p1.getName() + " VS " + p2.getName() + " ⚔️");

        Random rand = new Random();
        Move move1 = p1.getMoves().get(rand.nextInt(p1.getMoves().size()));
        Move move2 = p2.getMoves().get(rand.nextInt(p2.getMoves().size()));

        double tipoefectividad1 = TipoEfectividad.getEfectividad(move1.getType(), p2.getType());
        double tipoefectividad2 = TipoEfectividad.getEfectividad(move2.getType(), p1.getType());

        int danyo1 = (int) (move1.getPower() * tipoefectividad1);
        int danyo2 = (int) (move2.getPower() * tipoefectividad2);

        System.out.println(p1.getName() + " usa " + move1.getName() + "! (Efectividad: " + tipoefectividad1 + ")");
        System.out.println(p2.getName() + " usa " + move2.getName() + "! (Efectividad: " + tipoefectividad2 + ")");

        if (danyo1 > danyo2) {
            System.out.println("🏆 " + p1.getName() + " gana el combate!");
        } else if (danyo2 > danyo1) {
            System.out.println("🏆 " + p2.getName() + " gana el combate!");
        } else {
            System.out.println("🤝 ¡Empate!");
        }
    }
}
