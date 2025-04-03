package org.hugo.pojo;

public class Move {
    private final String name;
    private final String type;
    private final int power;

    public Move(String name, String type, int power) {
        this.name = name;
        this.type = type;
        this.power = power;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getPower() { return power; }

    @Override
    public String toString() {
        return "Move [name=" + name + ", type=" + type + ", power=" + power + "]";
    }
}
