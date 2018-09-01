package com.jostlingjacks.craftlife;

import java.util.Random;

public class Tool {

    public static int randomNumberGenerator(int upperBound) {
        Random rand = new Random();
        return rand.nextInt(upperBound) + 1;
    }
}
