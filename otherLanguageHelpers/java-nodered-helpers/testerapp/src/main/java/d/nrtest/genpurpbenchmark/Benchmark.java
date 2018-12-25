package d.nrtest.genpurpbenchmark;

import d.nrtest.nrcommon.PerformanceMeter;

import java.util.Random;

/**
 * @author Domen Mori 20. 10. 2018.
 */
public class Benchmark {

    private static final int ARRAY_LENGHT = 30000000;
    private static int writer = 0;

    private static int incrementWriter(){
        int r = writer++;
//        writer = (writer + 1) % ARRAY_LENGHT;
        if (writer >= ARRAY_LENGHT){
            writer = 0;
        }
        return r;
    }

    public static void main(String[] args) {

        Random rand = new Random();
        PerformanceMeter meter = new PerformanceMeter(2000000);

        long []first = new long[ARRAY_LENGHT];
        long []second = new long[ARRAY_LENGHT];

        for(int i = 0; i<ARRAY_LENGHT; ++i){
            first[i] = rand.nextLong();
            second[i] = rand.nextLong();
        }

        long [] results = new long[ARRAY_LENGHT*2];

        while (true){
            results[incrementWriter()] = first[rand.nextInt(ARRAY_LENGHT)] + second[rand.nextInt(ARRAY_LENGHT)];
            results[incrementWriter()] = (long)((double)first[rand.nextInt(ARRAY_LENGHT)] / (double)second[rand.nextInt(ARRAY_LENGHT)]);
            results[incrementWriter()] = first[rand.nextInt(ARRAY_LENGHT)] % second[rand.nextInt(ARRAY_LENGHT)];
            meter.notifyEvent();
        }
    }
}
