package d.nrtest.nrcommon;
/**
 * @author Domen Mori 19. 10. 2018.
 * Measures events per second.
 */
public class PerformanceMeter {

    private long startTime;
    private int startCnt = 0;
    private int endCnt;
    private double _1_dividedby_endcnt;
    private static final double MS_IN_S = 1000.0;

    /**
     * Creates meter and sets initial timer.
     * @param endCnt Number of how many events must pass until performance measurment is reported.
     */
    public PerformanceMeter(int endCnt) {
        this.endCnt = endCnt;
        _1_dividedby_endcnt = 1.0 / endCnt;
        startTime = System.currentTimeMillis();
    }

    /**
     * Notifies this class that event has happened. Event can be for example, http request. Whatever we are measuring.
     * This method prints the performance once endCnt has been reached.
     */
    public void notifyEvent() {
        if (startCnt++ > endCnt) {
            startCnt = 0;
            double msPerRequest = (System.currentTimeMillis() - startTime) * _1_dividedby_endcnt;
            System.out.println("Events per second: " + ((1.0 / msPerRequest) * MS_IN_S));
            startTime = System.currentTimeMillis();
        }
    }
}
