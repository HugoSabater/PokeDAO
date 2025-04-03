package org.hugo.dao;

import org.hugo.pojo.*;
import org.hugo.dbconnection.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PokemonDAO {
    public void createPokemon(Pokemon pokemon) {
        String sqlPokemon = "INSERT INTO pokemon (id, name, height, weight, type, ability_name, ability_effect) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCrie = "INSERT INTO crie (pokemon_id, latest, legacy) VALUES (?, ?, ?)";
        String sqlForm = "INSERT INTO form (pokemon_id, back_default, back_shiny, front_default, front_shiny) VALUES (?, ?, ?, ?, ?)";
        // el id de Move es autoincremental
        String sqlMove = "INSERT INTO move (name, type, power) VALUES (?, ?, ?)";
        String sqlPokemonMove = "INSERT INTO pokemon_move (pokemon_id, move_id) VALUES (?, ?)";
        // el id de Stat es autoincremental
        String sqlStat = "INSERT INTO stat (name, base_stat) VALUES (?, ?)";

        String sqlPokemonStat = "INSERT INTO pokemon_stat (pokemon_id, stat_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false); // Se desactiva el auto-commit para usar transacciones

            // Insertar Pokémon
            try (PreparedStatement psPok = conn.prepareStatement(sqlPokemon)) {
                psPok.setInt(1, pokemon.getId());
                psPok.setString(2, pokemon.getName());
                psPok.setInt(3, pokemon.getHeight());
                psPok.setInt(4, pokemon.getWeight());
                psPok.setString(5, pokemon.getType());
                psPok.setString(6, pokemon.getAbility().getName());
                psPok.setString(7, pokemon.getAbility().getEffect());
                psPok.executeUpdate();
            }

            // Insertar cries
            for (Crie crie : pokemon.getCrieList()) {
                try (PreparedStatement psCrie = conn.prepareStatement(sqlCrie)) {
                    psCrie.setInt(1, pokemon.getId());
                    psCrie.setString(2, crie.getLatest());
                    psCrie.setString(3, crie.getLegacy());
                    psCrie.executeUpdate();
                }
            }

            // Insertar formas
            for (Form form : pokemon.getFormList()) {
                try (PreparedStatement psForm = conn.prepareStatement(sqlForm)) {
                    psForm.setInt(1, pokemon.getId());
                    psForm.setString(2, form.getBack_default());
                    psForm.setString(3, form.getBack_shiny());
                    psForm.setString(4, form.getFront_default());
                    psForm.setString(5, form.getFront_shiny());
                    psForm.executeUpdate();
                }
            }

            // Insertar movimientos
            for (Move move : pokemon.getMoves()) {
                int moveId;
                try (PreparedStatement psMove = conn.prepareStatement(sqlMove, Statement.RETURN_GENERATED_KEYS)) {
                    psMove.setString(1, move.getName());
                    psMove.setString(2, move.getType());
                    psMove.setInt(3, move.getPower());
                    psMove.executeUpdate();

                    // Obtener el ID generado para el movimiento
                    try (ResultSet rs = psMove.getGeneratedKeys()) {
                        if (rs.next()) {
                            moveId = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID del movimiento.");
                        }
                    }
                }

                // Insertar relación Pokémon-Movimiento
                try (PreparedStatement pstmtPokemonMove = conn.prepareStatement(sqlPokemonMove)) {
                    pstmtPokemonMove.setInt(1, pokemon.getId());
                    pstmtPokemonMove.setInt(2, moveId);
                    pstmtPokemonMove.executeUpdate();
                }
            }

            // Insertar estadísticas
            for (Stat stat : pokemon.getStats()) {
                int statId;
                try (PreparedStatement pstmtStat = conn.prepareStatement(sqlStat, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtStat.setString(1, stat.getStat().getName());
                    pstmtStat.setInt(2, stat.getBaseStat());
                    pstmtStat.executeUpdate();

                    // Obtener el ID generado para la estadística
                    try (ResultSet rs = pstmtStat.getGeneratedKeys()) {
                        if (rs.next()) {
                            statId = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID de la estadística.");
                        }
                    }
                }

                // Insertar relación Pokémon-Estadística
                try (PreparedStatement pstmtPokemonStat = conn.prepareStatement(sqlPokemonStat)) {
                    pstmtPokemonStat.setInt(1, pokemon.getId());
                    pstmtPokemonStat.setInt(2, statId);
                    pstmtPokemonStat.executeUpdate();
                }
            }

            conn.commit(); // Confirmar la transacción
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir la transacción en caso de error
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            System.err.println(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                    conn.close(); // Cerrar la conexión
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    // Se encarga de Leer un Pokémon
    public Pokemon readPokemon(int id) {
        String sqlPokemon = "SELECT * FROM pokemon WHERE id = ?;";
        String sqlCrie = "SELECT * FROM crie WHERE pokemon_id = ?;";
        String sqlForm = "SELECT * FROM form WHERE pokemon_id = ?;";
        String sqlMoves = "SELECT m.* FROM move m JOIN pokemon_move pm ON m.id = pm.move_id WHERE m.id = ?;";
        String sqlStats = "SELECT s.* FROM stat s JOIN pokemon_stat ps ON s.id = ps.stat_id WHERE s.id = ?;";

        Pokemon pokemon = null;
        Connection conn = null;
        try {
            // Crea la conexión de base de datos (conn)
            conn = ConexionBD.getConnection();

            // Obtener Pokémon
            try (PreparedStatement pstmtPokemon = conn.prepareStatement(sqlPokemon)) {
                // pasa el parámetro a la consulta
                pstmtPokemon.setInt(1, id);

                try (ResultSet rs = pstmtPokemon.executeQuery()) {
                    if (rs.next()) {
                        pokemon = new Pokemon(
                                // Devuelve el name de la consulta, rs para insertarlo en el objeto pokemon.
                                rs.getString("name"),
                                // Devuelve el id de la consulta, rs para insertarlo en el objeto pokemon.
                                rs.getInt("id"),
                                // Devuelve el height de la consulta, rs para insertarlo en el objeto pokemon.
                                rs.getInt("height"),
                                // Devuelve el weight de la consulta, rs para insertarlo en el objeto pokemon.
                                rs.getInt("weight"),
                                // Devuelve el type de la consulta, rs para insertarlo en el objeto pokemon.
                                rs.getString("type"),
                                new ArrayList<>(), // Cries (se llenarán después)
                                new ArrayList<>(), // Forms (se llenarán después)
                                new ArrayList<>(), // Stats (se llenarán después)
                                new ArrayList<>(), // Moves (se llenarán después)
                                new Ability(rs.getString("ability_name"), rs.getString("ability_effect"))
                        );
                    }
                }
            }

            // Obtener cries
            if (pokemon != null) {
                try (PreparedStatement pstmtCrie = conn.prepareStatement(sqlCrie)) {
                    // Prepara la consulta proporcionando el parámetro id
                    pstmtCrie.setInt(1, id);
                    try (ResultSet rs = pstmtCrie.executeQuery()) {
                        while (rs.next()) {
                            pokemon.getCrieList().add( new Crie(
                                    // Devuelve el valor del campo latest de la tabla crie.
                                    rs.getString("latest"),
                                    // Devuelve el valor del campo legacy de la tabla crie.
                                    rs.getString("legacy")
                            ) );
                        }
                    }
                }
            }

            // Obtener formas
            if (pokemon != null) {
                try (PreparedStatement pstmtForm = conn.prepareStatement(sqlForm)) {
                    // Prepara la consulta proporcionando el parámetro id
                    pstmtForm.setInt(1, id);
                    try (ResultSet rs = pstmtForm.executeQuery()) {
                        while (rs.next()) {
                            pokemon.getFormList().add(new Form(
                                    // Devuelve el valor del campo back_default de la tabla Form.
                                    rs.getString("back_default"),
                                    // Devuelve el valor del campo front_shiny de la tabla Form,
                                    rs.getString("front_shiny"),
                                    // Devuelve el valor del campo front_default de la tabla Form,
                                    rs.getString("front_default"),
                                    // Devuelve el valor del campo back_shiny de la tabla Form
                                    rs.getString("back_shiny")
                                    ));
                        }
                    }
                }
            }

            // Obtener movimientos
            if (pokemon != null) {
                try (PreparedStatement pstmtMoves = conn.prepareStatement(sqlMoves)) {
                    // Prepara la consulta proporcionando el parámetro id
                    pstmtMoves.setInt(1, id);
                    try (ResultSet rs = pstmtMoves.executeQuery()) {
                        while (rs.next()) {
                            pokemon.getMoves().add(new Move(
                                    // Devuelve el valor del campo name de la tabla Move,
                                    rs.getString("name"),
                                    // Devuelve el valor del campo type de la tabla Move,
                                    rs.getString("type"),
                                    // Devuelve el valor del campo power de la tabla Move
                                    rs.getInt("power")
                                    ));
                        }
                    }
                }
            }

            // Obtener estadísticas
            if (pokemon != null) {
                try (PreparedStatement pstmtStats = conn.prepareStatement(sqlStats)) {
                    // Prepara la consulta proporcionando el parámetro id
                    pstmtStats.setInt(1, id);
                    try (ResultSet rs = pstmtStats.executeQuery()) {
                        while (rs.next()) {
                            pokemon.getStats().add(new Stat(
                                    // Devuelve el valor del campo base_stat de la tabla stat,
                                    rs.getInt("base_stat"),
                                    new StatDetail(rs.getString("name"))
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Cerrar la conexión
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return pokemon;
    }

    public void updatePokemon(Pokemon pokemon) {
        String sqlUpdatePokemon = "UPDATE pokemon SET name = ?, height = ?, weight = ?, type = ?, ability_name = ?, ability_effect = ? WHERE id = ?;";
        String sqlDeleteCrie = "DELETE FROM crie WHERE pokemon_id = ?;";
        String sqlDeleteForm = "DELETE FROM form WHERE pokemon_id = ?;";
        String sqlDeleteMoves = "DELETE FROM pokemon_move WHERE pokemon_id = ?;";
        String sqlDeleteStats = "DELETE FROM pokemon_stat WHERE pokemon_id = ?;";
        String sqlInsertCrie = "INSERT INTO crie (pokemon_id, latest, legacy) VALUES (?, ?, ?);";
        String sqlInsertForm = "INSERT INTO form (pokemon_id, back_default, back_shiny, front_default, front_shiny) VALUES (?, ?, ?, ?, ?);";
        String sqlInsertMove = "INSERT INTO move (name, type, power) VALUES (?, ?, ?);";
        String sqlInsertPokemonMove = "INSERT INTO pokemon_move (pokemon_id, move_id) VALUES (?, ?);";
        String sqlInsertStat = "INSERT INTO stat (name, base_stat) VALUES (?, ?);";
        String sqlInsertPokemonStat = "INSERT INTO pokemon_stat (pokemon_id, stat_id) VALUES (?, ?);";

        Connection conn = null;
        try {
            // Crea la conexión de base de datos (conn)
            conn = ConexionBD.getConnection();
            // Desactiva el AutoCommit, ponlo a false
            conn.setAutoCommit(false);

            // Actualizar información básica del Pokémon
            try (PreparedStatement psUpdatePokemon = conn.prepareStatement(sqlUpdatePokemon)) {
                // Establece en el PreparedStatement, psUpdatePokemon, el Name del pokemon. Debes hacer uso del getter que obtiene el Name
                psUpdatePokemon.setString(1, pokemon.getName());
                // Establece en el PreparedStatement, psUpdatePokemon, el Height del pokemon. Debes hacer uso del getter que obtiene el Height
                psUpdatePokemon.setInt(2, pokemon.getHeight());
                // Establece en el PreparedStatement, psUpdatePokemon, el Weight del pokemon. Debes hacer uso del getter que obtiene el Weight
                psUpdatePokemon.setInt(3, pokemon.getWeight());
                // Establece en el PreparedStatement, psUpdatePokemon, el Type del pokemon. Debes hacer uso del getter que obtiene el Type
                psUpdatePokemon.setString(4, pokemon.getType());
                // Establece en el PreparedStatement, psUpdatePokemon, el Name del pokemon. Debes hacer uso del getter que obtiene el Name de Ability
                psUpdatePokemon.setString(5, pokemon.getAbility().getName());
                // Establece en el PreparedStatement, psUpdatePokemon, el Effect del pokemon. Debes hacer uso del getter que obtiene el Effect de Ability
                psUpdatePokemon.setString(6, pokemon.getAbility().getEffect());
                // Establece en el PreparedStatement, psUpdatePokemon, el Id del pokemon. Debes hacer uso del getter que obtiene el Id
                psUpdatePokemon.setInt(7, pokemon.getId());

                psUpdatePokemon.executeUpdate();
            }

            // Eliminar cries existentes
            try (PreparedStatement psDeleteCrie = conn.prepareStatement(sqlDeleteCrie)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteCrie.setInt(1, pokemon.getId());
                psDeleteCrie.executeUpdate();
            }

            // Insertar nuevos cries
            for (Crie crie : pokemon.getCrieList()) {
                try (PreparedStatement psInsertCrie = conn.prepareStatement(sqlInsertCrie)) {
                    // Establece en el `PreparedStatement, psInsertCrie, el Id. Debes hacer uso del getter que obtiene el Id
                    psInsertCrie.setInt(1, pokemon.getId());
                    // Establece en el `PreparedStatement, psInsertCrie, el Latest. Debes hacer uso del getter que obtiene el Latest
                    psInsertCrie.setString(2, crie.getLatest());
                    // Establece en el `PreparedStatement, psInsertCrie, el Legacy. Debes hacer uso del getter que obtiene el Legacy
                    psInsertCrie.setString(3, crie.getLegacy());
                    psInsertCrie.executeUpdate();
                }
            }

            // Eliminar formas existentes
            try (PreparedStatement psDeleteForm = conn.prepareStatement(sqlDeleteForm)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteForm.executeUpdate();
            }

            // Insertar nuevas formas
            for (Form form : pokemon.getFormList()) {
                try (PreparedStatement psInsertForm = conn.prepareStatement(sqlInsertForm)) {

                    // Establece en el `PreparedStatement, psInsertForm, el Id. Debes hacer uso del getter que obtiene el Id
                    psInsertForm.setInt(1, pokemon.getId());
                    // Establece en el `PreparedStatement, psInsertForm, el Back_default. Debes hacer uso del getter que obtiene el Back_default
                    psInsertForm.setString(2, form.getBack_default());
                    // Establece en el `PreparedStatement, psInsertForm, el Back_shiny. Debes hacer uso del getter que obtiene el Back_shiny
                    psInsertForm.setString(3, form.getBack_shiny());
                    // Establece en el `PreparedStatement, psInsertForm, el Front_default. Debes hacer uso del getter que obtiene el Front_default
                    psInsertForm.setString(4, form.getFront_default());
                    // Establece en el `PreparedStatement, psInsertForm, el Front_shiny. Debes hacer uso del getter que obtiene el Front_shiny
                    psInsertForm.setString(5, form.getFront_shiny());

                    psInsertForm.executeUpdate();
                }
            }

            // Eliminar movimientos existentes
            try (PreparedStatement psDeleteMoves = conn.prepareStatement(sqlDeleteMoves)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteMoves.setInt(1, pokemon.getId());

                psDeleteMoves.executeUpdate();
            }

            // Insertar nuevos movimientos
            for (Move move : pokemon.getMoves()) {
                int moveId;
                try (PreparedStatement psInsertMove = conn.prepareStatement(sqlInsertMove, Statement.RETURN_GENERATED_KEYS)) {
                    // Establece en el `PreparedStatement, psInsertForm, el Name. Debes hacer uso del getter que obtiene el Name
                    psInsertMove.setString(1, move.getName());
                    // Establece en el `PreparedStatement, psInsertForm, el Type. Debes hacer uso del getter que obtiene el Type
                    psInsertMove.setString(2, move.getType());
                    // Establece en el `PreparedStatement, psInsertForm, el Power. Debes hacer uso del getter que obtiene el Power
                    psInsertMove.setInt(3, move.getPower());

                    psInsertMove.executeUpdate();

                    // Obtener el ID generado para el movimiento
                    try (ResultSet rs = psInsertMove.getGeneratedKeys()) {
                        if (rs.next()) {
                            moveId = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID del movimiento.");
                        }
                    }
                }

                // Insertar relación Pokémon-Movimiento
                try (PreparedStatement psInsertPokemonMove = conn.prepareStatement(sqlInsertPokemonMove)) {
                    // Establece en el `PreparedStatement, psInsertPokemonMove, el Id. Debes hacer uso del getter que obtiene el Id
                    psInsertPokemonMove.setInt(1, pokemon.getId());
                    // Establece en el `PreparedStatement, psInsertPokemonMove, el moveId.
                    psInsertPokemonMove.setInt(2, moveId);

                    psInsertPokemonMove.executeUpdate();
                }
            }

            // Eliminar estadísticas existentes
            try (PreparedStatement psDeleteStats = conn.prepareStatement(sqlDeleteStats)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteStats.setInt(1, pokemon.getId());

                psDeleteStats.executeUpdate();
            }

            // Insertar nuevas estadísticas
            for (Stat stat : pokemon.getStats()) {
                int statId;
                try (PreparedStatement psInsertStat = conn.prepareStatement(sqlInsertStat, Statement.RETURN_GENERATED_KEYS)) {
                    // Establece en el `PreparedStatement, psInsertStat, el Name. Debes hacer uso del getter que obtiene el Name de Stat
                    psInsertStat.setString(1, stat.getStat().getName());
                    // Establece en el `PreparedStatement, psInsertStat, el BaseStat. Debes hacer uso del getter que obtiene el BaseStat
                    psInsertStat.setInt(2, stat.getBaseStat());

                    psInsertStat.executeUpdate();

                    // Obtener el ID generado para la estadística
                    try (ResultSet rs = psInsertStat.getGeneratedKeys()) {
                        if (rs.next()) {
                            statId = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID de la estadística.");
                        }
                    }
                }

                // Insertar relación Pokémon-Estadística
                try (PreparedStatement psInsertPokemonStat = conn.prepareStatement(sqlInsertPokemonStat)) {
                    // Establece en el `PreparedStatement, psInsertPokemonStat, el Id. Debes hacer uso del getter que obtiene el Name de Id
                    psInsertPokemonStat.setInt(1, pokemon.getId());
                    // Establece en el `PreparedStatement, psInsertPokemonStat, el statId.
                    psInsertPokemonStat.setInt(2, statId);

                    psInsertPokemonStat.executeUpdate();
                }
            }

            // Realiza la confirmación de la actualización.
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    // Deshaz la actualización
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            System.err.println(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    // Restaura el autoCommit a true
                    conn.setAutoCommit(true);
                    // Cierra la conexión
                    conn.close();

                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public void deletePokemon(int id) {
        String sqlDeleteCrie = "DELETE FROM crie WHERE pokemon_id = ?;";
        String sqlDeleteForm = "DELETE FROM form WHERE pokemon_id = ?;";
        String sqlDeleteMoves = "DELETE FROM pokemon_move WHERE pokemon_id = ?;";
        String sqlDeleteStats = "DELETE FROM pokemon_stat WHERE pokemon_id = ?;";
        String sqlDeletePokemon = "DELETE FROM pokemon WHERE id = ?;";

        Connection conn = null;
        try {
            // Crea la conexión de base de datos (conn)
            conn = ConexionBD.getConnection();
            // Establece el AutoCommit a false
            conn.setAutoCommit(false);

            // Eliminar cries
            try (PreparedStatement psDeleteCrie = conn.prepareStatement(sqlDeleteCrie)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteCrie.setInt(1, id);

                psDeleteCrie.executeUpdate();
            }

            // Eliminar formas
            try (PreparedStatement psDeleteForm = conn.prepareStatement(sqlDeleteForm)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteForm.setInt(1, id);

                psDeleteForm.executeUpdate();
            }

            // Eliminar movimientos
            try (PreparedStatement psDeleteMoves = conn.prepareStatement(sqlDeleteMoves)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteMoves.setInt(1, id);

                psDeleteMoves.executeUpdate();
            }

            // Eliminar estadísticas
            try (PreparedStatement psDeleteStats = conn.prepareStatement(sqlDeleteStats)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeleteStats.setInt(1, id);

                psDeleteStats.executeUpdate();
            }

            // Eliminar Pokémon
            try (PreparedStatement psDeletePokemon = conn.prepareStatement(sqlDeletePokemon)) {
                // Prepara el borrado proporcionando el parámetro Id
                psDeletePokemon.setInt(1, id);
                
                psDeletePokemon.executeUpdate();
            }

            // Confirma los borrados
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    // Deshaz los borrados
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            System.err.println(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    // Restaura el autoCommit a true
                    conn.setAutoCommit(true);
                    // Cierra la conexión
                    conn.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    // Obtener todos los pokémon

    public List<Pokemon> getAllPokemon() {
        String sqlPokemon = "SELECT * FROM pokemon;";
        String sqlCrie = "SELECT * FROM crie WHERE pokemon_id = ?;";
        String sqlForm = "SELECT * FROM form WHERE pokemon_id = ?;";
        String sqlMoves = "SELECT m.* FROM move m JOIN pokemon_move pm ON m.id = pm.move_id WHERE pm.pokemon_id = ?;";
        String sqlStats = "SELECT s.* FROM stat s JOIN pokemon_stat ps ON s.id = ps.stat_id WHERE ps.pokemon_id = ?;";

        List<Pokemon> pokemonList = new ArrayList<>();
        Connection conn = null;
        try {
            // Crea la conexión de base de datos (conn)
            conn = ConexionBD.getConnection();

            // Obtener todos los Pokémon. Con esta consulta vamos a obtener todos los Pokémon
            try (PreparedStatement psPokemon = conn.prepareStatement(sqlPokemon)) {
                try (ResultSet rs = psPokemon.executeQuery()) {
                    while (rs.next()) {
                        Pokemon pokemon = new Pokemon(

                                // Se obtiene del resulSet rs el name.
                                rs.getString("name"),
                                // Se obtiene del resulSet rs el id.
                                rs.getInt("id"),
                                // Se obtiene del resulSet rs el height.
                                rs.getInt("height"),
                                // Se obtiene del resulSet rs el weight.
                                rs.getInt("weight"),
                                // Se obtiene del resulSet rs el type.
                                rs.getString("type"),
                                new ArrayList<>(), // Cries (se llenarán después)
                                new ArrayList<>(), // Forms (se llenarán después)
                                new ArrayList<>(), // Stats (se llenarán después)
                                new ArrayList<>(), // Moves (se llenarán después)
                                new Ability(rs.getString("ability_name"), rs.getString("ability_effect"))
                        );

                        // Obtener cries del Pokémon
                        try (PreparedStatement psCrie = conn.prepareStatement(sqlCrie)) {
                            // Prepara la consulta proporcionando el parámetro Id
                            psCrie.setInt(1, pokemon.getId());
                            try (ResultSet rsCrie = psCrie.executeQuery()) {
                                while (rsCrie.next()) {
                                    pokemon.getCrieList().add(new Crie(
                                            // Se obtiene del resulSet rsCrie el latest.
                                            rsCrie.getString("latest"),
                                            // Se obtiene del resulSet rsCrie el legacy
                                            rsCrie.getString("legacy")
                                    ) );
                                }
                            }
                        }

                        // Obtener formas del Pokémon
                        try (PreparedStatement psForm = conn.prepareStatement(sqlForm)) {
                            // Prepara la consulta proporcionando el parámetro Id
                            psForm.setInt(1, pokemon.getId());
                            try (ResultSet rsForm = psForm.executeQuery()) {
                                while (rsForm.next()) {
                                    pokemon.getFormList().add(new Form(
                                            // Se obtiene del resulSet rsForm el back_default.
                                            rsForm.getString("back_default"),
                                            // Se obtiene del resulSet rsForm el front_shiny.
                                            rsForm.getString("front_shiny"),
                                            // Se obtiene del resulSet rsForm el front_default.
                                            rsForm.getString("front_default"),
                                            // Se obtiene del resulSet rsForm el back_shiny
                                            rsForm.getString("back_shiny")
                                    ));
                                }
                            }
                        }

                        // Obtener movimientos del Pokémon
                        try (PreparedStatement psMoves = conn.prepareStatement(sqlMoves)) {
                            // Prepara la consulta proporcionando el parámetro Id
                            psMoves.setInt(1, pokemon.getId());
                            try (ResultSet rsMoves = psMoves.executeQuery()) {
                                while (rsMoves.next()) {
                                    pokemon.getMoves().add(new Move(
                                            // Se obtiene del resulSet rsMoves el name.
                                            rsMoves.getString("name"),
                                            // Se obtiene del resulSet rsMoves el type.
                                            rsMoves.getString("type"),
                                            // Se obtiene del resulSet rsMoves el power
                                            rsMoves.getInt("power")
                                    ));
                                }
                            }
                        }

                        // Obtener estadísticas del Pokémon
                        try (PreparedStatement psStats = conn.prepareStatement(sqlStats)) {
                            // Prepara la consulta proporcionando el parámetro Id
                            psStats.setInt(1, pokemon.getId());
                            try (ResultSet rsStats = psStats.executeQuery()) {
                                while (rsStats.next()) {
                                    pokemon.getStats().add(new Stat(
                                            // Se obtiene del resulSet rsStats el base_stat,
                                            rsStats.getInt("base_stat"),
                                            new StatDetail(rsStats.getString("name"))
                                    ));
                                }
                            }
                        }

                        // Agregar el Pokémon a la lista
                        pokemonList.add(pokemon);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Cerrar la conexión
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return pokemonList;
    }

}
