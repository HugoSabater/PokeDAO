package org.hugo.pojo;

import java.util.HashMap;
import java.util.Map;

enum PokemonType {
    NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE,
    FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG,
    ROCK, GHOST, DRAGON, DARK, STEEL, FAIRY;
}

public class TipoEfectividad {
    private static final Map<String, Map<String, Double>> efectividad = new HashMap<>();

    static {
        for (PokemonType type : PokemonType.values()) {
            efectividad.put(String.valueOf(type), new HashMap<>());
        }

        //        2.0 → Súper efectivo
        //        0.5 → Poco efectivo
        //        0.0 → No afecta

        // Definición de efectividad de tipos
        efectividad.get("NORMAL").put("ROCK", 0.5);
        efectividad.get("NORMAL").put("GHOST", 0.0);
        efectividad.get("NORMAL").put("STEEL", 0.5);

        efectividad.get("FIRE").put("GRASS", 2.0);
        efectividad.get("FIRE").put("WATER", 0.5);
        efectividad.get("FIRE").put("ICE", 2.0);
        efectividad.get("FIRE").put("BUG", 2.0);
        efectividad.get("FIRE").put("ROCK", 0.5);
        efectividad.get("FIRE").put("DRAGON", 0.5);
        efectividad.get("FIRE").put("STEEL", 2.0);

        efectividad.get("WATER").put("FIRE", 2.0);
        efectividad.get("WATER").put("WATER", 0.5);
        efectividad.get("WATER").put("GRASS", 0.5);
        efectividad.get("WATER").put("GROUND", 2.0);
        efectividad.get("WATER").put("ROCK", 2.0);
        efectividad.get("WATER").put("DRAGON", 0.5);

        efectividad.get("ELECTRIC").put("WATER", 2.0);
        efectividad.get("ELECTRIC").put("ELECTRIC", 0.5);
        efectividad.get("ELECTRIC").put("GRASS", 0.5);
        efectividad.get("ELECTRIC").put("GROUND", 0.0);
        efectividad.get("ELECTRIC").put("FLYING", 2.0);
        efectividad.get("ELECTRIC").put("DRAGON", 0.5);

        efectividad.get("GRASS").put("FIRE", 0.5);
        efectividad.get("GRASS").put("WATER", 2.0);
        efectividad.get("GRASS").put("GRASS", 0.5);
        efectividad.get("GRASS").put("POISON", 0.5);
        efectividad.get("GRASS").put("GROUND", 2.0);
        efectividad.get("GRASS").put("FLYING", 0.5);
        efectividad.get("GRASS").put("BUG", 0.5);
        efectividad.get("GRASS").put("ROCK", 2.0);
        efectividad.get("GRASS").put("DRAGON", 0.5);
        efectividad.get("GRASS").put("STEEL", 0.5);

        efectividad.get("ICE").put("FIRE", 0.5);
        efectividad.get("ICE").put("WATER", 0.5);
        efectividad.get("ICE").put("GRASS", 2.0);
        efectividad.get("ICE").put("ICE", 0.5);
        efectividad.get("ICE").put("GROUND", 2.0);
        efectividad.get("ICE").put("FLYING", 2.0);
        efectividad.get("ICE").put("DRAGON", 2.0);
        efectividad.get("ICE").put("STEEL", 0.5);

        efectividad.get("FIGHTING").put("NORMAL", 2.0);
        efectividad.get("FIGHTING").put("ROCK", 2.0);
        efectividad.get("FIGHTING").put("STEEL", 2.0);
        efectividad.get("FIGHTING").put("ICE", 2.0);
        efectividad.get("FIGHTING").put("DARK", 2.0);
        efectividad.get("FIGHTING").put("FLYING", 0.5);
        efectividad.get("FIGHTING").put("POISON", 0.5);
        efectividad.get("FIGHTING").put("PSYCHIC", 0.5);
        efectividad.get("FIGHTING").put("BUG", 0.5);
        efectividad.get("FIGHTING").put("FAIRY", 0.5);
        efectividad.get("FIGHTING").put("GHOST", 0.0);
    }

    public static double getEfectividad(String tipoAtaque, String tipoDefensa) {
        return efectividad.getOrDefault(tipoAtaque.toUpperCase(), new HashMap<>()).getOrDefault(tipoDefensa.toUpperCase(), 1.0);
    }
}

