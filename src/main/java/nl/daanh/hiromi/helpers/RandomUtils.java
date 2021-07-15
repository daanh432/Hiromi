package nl.daanh.hiromi.helpers;

import java.util.Random;

public class RandomUtils {
    public static Random random = new Random();

    public static Object getRandom(Object[] array) {
        return array[random.nextInt(array.length)];
    }
}
