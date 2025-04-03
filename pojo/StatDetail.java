package org.hugo.pojo;

public class StatDetail {
    private String name;

    public StatDetail(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public String toString() {
        return "StatDetail{" +
                "name='" + name + '\'' +
                '}';
    }
}
