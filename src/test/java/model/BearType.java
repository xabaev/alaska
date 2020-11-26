package model;

import java.util.Random;

public enum BearType {
    POLAR, BROWN, BLACK, GUMMY;

    public static BearType getRandomType() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }

}
