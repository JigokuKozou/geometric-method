package ru.shchelkin.geometricmethod.model;

import javafx.scene.Group;

public enum Groups {
    ALZ("alz", "alz_c1_new.mat"),
    CONTROLS("controls", "controls_c1_new.mat"),
    DEPRESSION("dep", "dep_c1_new.mat"),
    MCI("mci", "mci_c1_new.mat"),
    SCHIZ("schiz", "schiz_c1_new.mat");

    private final String name;
    private final String fileName;

    Groups(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public static Groups getGroupByFileName(String fileName)  {
        for (Groups group : Groups.values()) {
            if (group.fileName.equals(fileName))
                return group;
        }

        throw new IllegalArgumentException();
    }
}
