package ru.shchelkin.geometricmethod;

public class DataRow {
    private final String fileName;
    private final int humanId;
    private final int wId;
    private final int winS;
    private final int winE;
    private final Integer sensor1;
    private final Integer sensor2;
    private final Integer sensor3;
    private final Integer sensor4;
    private final Integer sensor5;
    private final Integer sensor6;
    private final Integer sensor7;
    private final Integer sensor8;
    private final Integer sensor9;
    private final Integer sensor10;
    private final Integer sensor11;
    private final Integer sensor12;
    private final Integer sensor13;
    private final Integer sensor14;
    private final Integer sensor15;
    private final Integer sensor16;
    private final Integer sensor17;
    private final Integer sensor18;
    private final Integer sensor19;

    public DataRow(String fileName, int humanId, int wId, int winS, int winE, Integer sensor1, Integer sensor2, Integer sensor3, Integer sensor4, Integer sensor5, Integer sensor6, Integer sensor7, Integer sensor8, Integer sensor9, Integer sensor10, Integer sensor11, Integer sensor12, Integer sensor13, Integer sensor14, Integer sensor15, Integer sensor16, Integer sensor17, Integer sensor18, Integer sensor19) {
        this.fileName = fileName;
        this.humanId = humanId;
        this.wId = wId;
        this.winS = winS;
        this.winE = winE;
        this.sensor1 = sensor1;
        this.sensor2 = sensor2;
        this.sensor3 = sensor3;
        this.sensor4 = sensor4;
        this.sensor5 = sensor5;
        this.sensor6 = sensor6;
        this.sensor7 = sensor7;
        this.sensor8 = sensor8;
        this.sensor9 = sensor9;
        this.sensor10 = sensor10;
        this.sensor11 = sensor11;
        this.sensor12 = sensor12;
        this.sensor13 = sensor13;
        this.sensor14 = sensor14;
        this.sensor15 = sensor15;
        this.sensor16 = sensor16;
        this.sensor17 = sensor17;
        this.sensor18 = sensor18;
        this.sensor19 = sensor19;
    }

    public String getFileName() {
        return fileName;
    }

    public int getHumanId() {
        return humanId;
    }

    public int getwId() {
        return wId;
    }

    public int getWinS() {
        return winS;
    }

    public int getWinE() {
        return winE;
    }

    public Integer getSensor(int sensor) {
        int sensorValue;
        switch (sensor) {
            case 1:
                sensorValue = sensor1;
                break;
            case 2:
                sensorValue = sensor2;
                break;
            case 3:
                sensorValue = sensor3;
                break;
            case 4:
                sensorValue = sensor4;
                break;
            case 5:
                sensorValue = sensor5;
                break;
            case 6:
                sensorValue = sensor6;
                break;
            case 7:
                sensorValue = sensor7;
                break;
            case 8:
                sensorValue = sensor8;
                break;
            case 9:
                sensorValue = sensor9;
                break;
            case 10:
                sensorValue = sensor10;
                break;
            case 11:
                sensorValue = sensor11;
                break;
            case 12:
                sensorValue = sensor12;
                break;
            case 13:
                sensorValue = sensor13;
                break;
            case 14:
                sensorValue = sensor14;
                break;
            case 15:
                sensorValue = sensor15;
                break;
            case 16:
                sensorValue = sensor16;
                break;
            case 17:
                sensorValue = sensor17;
                break;
            case 18:
                sensorValue = sensor18;
                break;
            case 19:
                sensorValue = sensor19;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return sensorValue;
    }
}
