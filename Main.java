package org.hugo;

import org.hugo.connect.ApiPokemon;
import org.hugo.dao.PokemonDAO;
import org.hugo.pojo.LuchaPokemon;
import org.hugo.pojo.Move;
import org.hugo.pojo.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    public static void main(String[] args) {
        // Generamos un ObjetoDao
        PokemonDAO pokemonDAO = new PokemonDAO();

        // Escribe aquí el código par poder saber si tenemos pokémons en la base de datos.
        List<Pokemon> pk = new ArrayList<>(pokemonDAO.getAllPokemon());

        // Si no hay pokemon debes descargarlos
        if (pk.isEmpty()) {
            List<Pokemon> pokemonList = ApiPokemon.recogePokemons(BASE_URL, 20);
            for (Pokemon pokemon : pokemonList) {
                pokemonDAO.createPokemon(pokemon);
            }
            if (pokemonList.isEmpty()) return;
        }

        // En este punto ya podemos listar todos los Pokémon
        List<Pokemon> allPokemon = pokemonDAO.getAllPokemon();
        System.out.println("Cantidad de pokémons guardados: " + allPokemon.size());
        for (Pokemon pokemon : allPokemon) {
            System.out.println("Pokémon: " + pokemon.getName());
            System.out.println("\tMovimientos: " + pokemon.getMoves().stream()
                    .map(Move::getName)
                    .collect(Collectors.joining(", ")));
            System.out.println("\tEstadísticas: " + pokemon.getStats().stream()
                    .map(stat -> stat.getStat().getName() + " (" + stat.getBaseStat() + ")")
                    .collect(Collectors.joining(", ")));
        }

        // Generamos una Batalla de Pokemon
        Random rand = new Random();
        Pokemon p1 = allPokemon.get(rand.nextInt(allPokemon.size()));
        Pokemon p2 = allPokemon.get(rand.nextInt(allPokemon.size()));

        LuchaPokemon.lucha(p1, p2);
    }
}