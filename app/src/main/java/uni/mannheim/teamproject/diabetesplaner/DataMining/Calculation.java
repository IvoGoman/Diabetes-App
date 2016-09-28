package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.content.Context;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Created by leonidgunko on 05/04/16.
 */
public class Calculation{

    private static final String FILENAME = "output.arff";

    public static double [][] preprocessRecords(double[][] raw){

        double [][] recordsProcessed;
        double [][] recordsforStatistics;
        PearsonsCorrelation calculateCorrelation = new PearsonsCorrelation();

        //create output array
        //new time window every 2.5 seconds, final time window width: 5min
        // --> 5*60/2.5 = arraysize 120
        // features: mean, standard deviation, interquartile range,correlation, timestamp --> 2.arraysize 4*3+1 = 13
        recordsProcessed = new double [AppGlobal.arraySize][13];


        // double array to calculate statistical figures using class DescriptiveStatistics
        // store values for x, y, z --> 3
        // One initial time windows consists of 5 x 50 = 250 records (width=5 seconds, sample rate=50Hz)
        recordsforStatistics = new double [3] [AppGlobal.initTimeWindow * AppGlobal.sampleRate];


        //for statement for all 120 time windows to be calculated
        for(int i=0; i<AppGlobal.arraySize; i++){ //30

            //for statement for all 3 variables to be calculated with (x, y, z)
            for (int ii = 0; ii <3; ii++){

                // for statement for all 250 records that are needed to calculate the mean, stddev, intqr for one time window
                for (int iii=0; iii<(AppGlobal.initTimeWindow * AppGlobal.sampleRate); iii++){ //60

                    //new time window every 2.5 seconds --> every 125th record of the raw data (1*10)
                    recordsforStatistics[ii][iii] = (double) raw[((i*AppGlobal.overlapWindow*AppGlobal.sampleRate)+iii)][ii];	//10
                }

                //calculate the means, standard deviation and interquartile range for x, y, z sequentially
                DescriptiveStatistics statistics = new DescriptiveStatistics(recordsforStatistics[ii]);
                recordsProcessed [i][3*ii]=statistics.getMean();
                recordsProcessed [i][(3*ii)+1]=statistics.getStandardDeviation();
                recordsProcessed [i][(3*ii)+2]=statistics.getPercentile(75)-statistics.getPercentile(25);
            }

            //calculate Correlation between x&y, x&z, y&z
            recordsProcessed[i][9]=calculateCorrelation.correlation(recordsforStatistics[0], recordsforStatistics[1]);
            recordsProcessed[i][10]=calculateCorrelation.correlation(recordsforStatistics[0], recordsforStatistics[2]);
            recordsProcessed[i][11]=calculateCorrelation.correlation(recordsforStatistics[1], recordsforStatistics[2]);

            //add TimeStamp
            recordsProcessed[i][12]=raw[i*AppGlobal.overlapWindow*AppGlobal.sampleRate][3];
        }

        return recordsProcessed;
    }

    public static String[][] labelRecords(double[][] processed, InputStream model, Context c) throws Exception {

        String[][] labeledRecords = new String [AppGlobal.arraySize][2];

        // Create the new ARFF file with the preprocessed data
        createArff(processed, c);

        // Load classifier; deserialize model
        Classifier classifier = (Classifier) weka.core.SerializationHelper.read(model);

        // Retrieve the output from createArff()
        FileInputStream output = c.openFileInput("output.arff");
        BufferedReader readerOutput = new BufferedReader(new InputStreamReader(output));

        Instances unlabeled = new Instances(readerOutput);
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        Instances labeled = new Instances(unlabeled);

        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = classifier.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);

            labeledRecords[i][0] = Double.toString(processed[i][12]);
            labeledRecords[i][1] = label(labeled.instance(i).classValue());
        }

        output.close();
        return labeledRecords;
    }

    static String label(double processed) {

        String label = null;
        int num = (int) processed;

        switch(num) {
            case 0: label = "Sitting";
                break;
            case 1: label = "Not Specified";
                break;
            case 2: label = "Walking";
                break;
            case 3: label = "Standing";
                break;
            case 4: label = "Climbing up";
                break;
            case 5: label = "Climbing down";
                break;
            case 6: label = "Running";
                break;
            case 7: label = "Recumbency";
                break;
            case 8: label = "Unknown";
                break;
            case 9: label = "Jumping";
                break;
            default: label = "Unknown";
                break;
        }
        return label;
    }

    public static String [] aggregateRecords(String[][] labeled){
        String[] finalRecord = new String[2];
        String[] cache = new String[AppGlobal.arraySize];

        //The timestamp of the final record is the time stamp of the first record of the whole time window
        finalRecord[0]=labeled[0][0];

        //load activity labels into the cache array, the cache is needed because the parameter array is two-dimensional
        for (int i = 0; i<AppGlobal.arraySize; i++){
            cache[i]=labeled[i][1];
        }

        //create a hashmap (i.e. a set of key value pairs), key: the different activities, value: the occurrences of the activities
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();


        //load the activities and their occurrences into the hashmap
        for(String word: cache) {
            Integer count = wordCount.get(word);
            wordCount.put(word, (count==null) ? 1 : count+1);
        }

        // This will return the max value in the Hashmap
        int maxValueInMap=(Collections.max(wordCount.values()));

        //Iterate over Hashmap
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue()==maxValueInMap) {

                //the activity that occurred most often is inserted into the finalRecord.
                //BUT: if activities were classified with the same frequency, the most frequent activity is chosen by coincidence
                finalRecord[1]=entry.getKey();
            }
        }


        return finalRecord;
    }

    static void createArff(double[][] processed, Context c) throws Exception {

        double[][] processedArff = processed;

        // Creates ARFF file for the instances to be saved to
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(baos);

        FileOutputStream fos = c.openFileOutput(FILENAME,1);


        // Create the layout for the file
        output.println("@RELATION features");
        output.println();
        output.println("@ATTRIBUTE xMean NUMERIC");
        output.println("@ATTRIBUTE xStdDev NUMERIC");
        output.println("@ATTRIBUTE xIntqr NUMERIC");
        output.println("@ATTRIBUTE yMean NUMERIC");
        output.println("@ATTRIBUTE yStdDev NUMERIC");
        output.println("@ATTRIBUTE yIntqr NUMERIC");
        output.println("@ATTRIBUTE zMean NUMERIC");
        output.println("@ATTRIBUTE zStdDev NUMERIC");
        output.println("@ATTRIBUTE zIntqr NUMERIC");
        output.println("@ATTRIBUTE CorrXY NUMERIC");
        output.println("@ATTRIBUTE CorrXZ NUMERIC");
        output.println("@ATTRIBUTE CorrYZ NUMERIC");
        output.println("@ATTRIBUTE timeStamp NUMERIC");
        output.println("@ATTRIBUTE class {\"Sitting\",\"Not Specified\",\"Walking\",\"Standing\",\"Climbing (up)\",\"Climbing (down)\",\"Running\",\"Recumbency\",\"unknown\"}");
        output.println();
        output.println("@DATA");

        // Write data points from array into file
        for(int i = 0;i<=processedArff.length-1;i++){
            for(int ii = 0; ii<processedArff[1].length; ii++) {

                output.print(processedArff[i][ii]);
                if(ii<processedArff[1].length-1) {
                    output.print(",");

                }

                if(ii==processedArff[1].length-1) {
                    output.print(",?");
                }
            }
            output.println();
        }
        fos.write(baos.toString().getBytes());
        output.close();
        fos.close();
    }

}