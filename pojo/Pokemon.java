package org.hugo.pojo;

import javax.sound.sampled.*;
import java.net.URI;
import java.util.List;

public class Pokemon {
    private String name;
    private int id;
    private int height;
    private int weight;
    private String type;

    private List<Crie> crieList;
    private List<Form> formList;
    private List<Stat> stats;
    private List<Move> moves;
    private Ability ability;


    public Pokemon(String name, int id, int height, int weight, String type, List<Crie> crieList, List<Form> formList, List<Stat> stats, List<Move> moves, Ability ability) {
        this.name = name;
        this.id = id;
        this.height = height;
        this.weight = weight;
        this.type = type;
        this.crieList = crieList;
        this.formList = formList;
        this.stats = stats;
        this.moves = moves;
        this.ability = ability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Crie> getCrieList() {
        return crieList;
    }

    public void setCrieList(List<Crie> crieList) {
        this.crieList = crieList;
    }

    public List<Form> getFormList() {
        return formList;
    }

    public void setFormList(List<Form> formList) {
        this.formList = formList;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public void playChillar(boolean ultimo) {
        if (crieList== null) {
            System.out.println("❌ No hay chillido disponible para " + name);
            return;
        }

        String cryUrl = ultimo ? crieList.getFirst().getLatest() : crieList.getFirst().getLegacy();
        if (cryUrl == null || cryUrl.equals("N/A")) {
            System.out.println("❌ No se pudo obtener el cry para " + name);
            return;
        }

        try {
            System.out.println("🔊 Reproduciendo cry de " + name + ": " + cryUrl);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new URI(cryUrl).toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

/*
            try {
                System.out.println("🔊 Reproduciendo cry de " + name + ": " + cryUrl); // Reemplaza "name"

                URL url = new URI(cryUrl).toURL();
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream())); // Para MP3 con Tritonus

                AudioFormat baseFormat = audioStream.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getFrameSize() * baseFormat.getChannels(),
                        baseFormat.getFrameRate(),
                        false);

                AudioInputStream decodedAudioStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);


                Clip clip = AudioSystem.getClip();
                clip.open(decodedAudioStream);
                clip.start();

            } catch (Exception e) {
                System.err.println("Error al reproducir el sonido: " + e.getMessage());
                e.printStackTrace(); // Imprime la traza del error para depurar
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new URI(cryUrl).toURL().openStream()));
            // Obtener el formato del audio
            AudioFormat formato = ais.getFormat();
            // Crear un DataLine.Info para el clip
            DataLine.Info info = new DataLine.Info(Clip.class, formato);
            // Obtener un clip de sonido
            Clip clip = (Clip) AudioSystem.getLine(info);
            // Abrir el flujo de audio en el clip
            clip.open(ais);
            // Iniciar la reproducción
            clip.start();
            // Esperar a que termine la reproducción (opcional)
            while (clip.isRunning()) {
                Thread.yield();
            }
            // Cerrar el clip y el flujo de audio
            clip.close();
            ais.close();
*/

        } catch (Exception e) {
            System.out.println("⚠️ Error al reproducir el cry: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Pokemon [name=" + name +", id=" + id + ", height=" + height + ", weight=" + weight +
                ", type='" + type + ", crieList=" + crieList + ", formList=" + formList + ", stats=" + stats +
                ", moves=" + moves + ", ability=" + ability + ']';
    }
}

