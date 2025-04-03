package org.hugo.pojo;

public class Crie {
    private String latest;
    private String legacy;

    public Crie(String latest, String legacy) {
        this.latest = latest;
        this.legacy = legacy;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getLegacy() {
        return legacy;
    }

    public void setLegacy(String legacy) {
        this.legacy = legacy;
    }

    @Override
    public String toString() {
        return "Crie [latest=" + latest + ", legacy=" + legacy + "]";
    }
}
