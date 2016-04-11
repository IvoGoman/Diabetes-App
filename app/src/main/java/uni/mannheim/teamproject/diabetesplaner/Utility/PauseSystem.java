package uni.mannheim.teamproject.diabetesplaner.Utility;

/**
 * Created by leonidgunko on 05/04/16.
 */

public class PauseSystem {

    // lastTime the system time was called
    public static long lastTime = 0;

    // defines how often the Accelerometer data is accessed in milliseconds.
    // therefore the collection amount is regarded in relation to one minute
    // and converted into milliseconds.
    private static int collectionGap = 1000 / AppGlobal.sampleRate;


    /**
     * gapSuitable checks if the time of the gap is elapsed
     *
     * @author Tobias Baehr
     * @return if time of the gap is over
     */
    public static boolean gapSuitable() {
        // check if the time gap is elapsed.
        // the gap is elapsed if the sum of the collectionGap timestamp and lastTime timestamp
        // is bigger than the current timestamp
        if(System.currentTimeMillis() > collectionGap + lastTime)
            lastTime = System.currentTimeMillis();
        else
            return false;
        return true;
    }

    /**
     * getCollectionGap returns the gap/seconds
     *
     * @author Tobias Baehr
     * @return collectionGap variable
     */
    public static double getCollectionGap() {
        return collectionGap;
    }


}
