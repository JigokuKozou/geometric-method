package ru.shchelkin.geometricmethod;

public enum Groups {
    ALZ("alz", "alz_c1_new.mat", "red"),
    CONTROLS("controls", "controls_c1_new.mat", "green"),
    DEPRESSION("dep", "dep_c1_new.mat", "blue"),
    MCI("mci", "mci_c1_new.mat", "black"),
    SCHIZ("schiz", "schiz_c1_new.mat", "pink");

    private final String name;
    private final String fileName;

    private final String color;

    Groups(String name, String fileName, String color) {
        this.name = name;
        this.fileName = fileName;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getColor() {
        return color;
    }

    public static Groups getGroupByFileName(String fileName)  {
        for (Groups group : Groups.values()) {
            if (group.fileName.equals(fileName))
                return group;
        }

        throw new IllegalArgumentException();
    }
}
