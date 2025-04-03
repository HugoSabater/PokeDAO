package org.hugo.pojo;

public class Stat {
    private int baseStat;
    private StatDetail stat;

    public Stat(int baseStat, StatDetail stat) {
        this.baseStat = baseStat;
        this.stat = stat;
    }

    public int getBaseStat() { return baseStat; }
    public StatDetail getStat() { return stat; }

    @Override
    public String toString() {
        return "Stat{" +
                "baseStat=" + baseStat +
                ", stat=" + stat +
                '}';
    }
}
