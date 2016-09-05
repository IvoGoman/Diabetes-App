package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.database.Cursor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import weka.associations.GeneralizedSequentialPatterns;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 * Created by Stefan on 19.07.2016.
 */
public class GSP_Prediction {

    private FastVector m_AllSequentialPatterns;
    private Date m_CycleStart;
    private Date m_CycleEnd;
    private int m_Cycles = 0;

    public static void main(String[] args){
        GSP_Prediction p = new GSP_Prediction();
        Instances i = p.getInstancesFromCSV("C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataCompleteWithCasesJoined2.csv");
        //remove endtime
        i.deleteAttributeAt(2);
        //remove subactivity
        i.deleteAttributeAt(4);
        //remove id
        i.deleteAttributeAt(5);

        i.toSummaryString();

        p.findFrequentSequences(i, 0.8f);
    }

    public void findFrequentSequences(Instances instances, float min_sup){
        GeneralizedSequentialPatterns gsp = new GeneralizedSequentialPatterns();
        try {
            gsp.setDataSeqID(0);
            gsp.setMinSupport(min_sup);

//            -D
//            If set, algorithm is run in debug mode and
//            may output additional info to the console
//            -S <minimum support threshold>
//            The miminum support threshold.
//            (default: 0.9)
//            -I <attribute number representing the data sequence ID
//            The attribute number representing the data sequence ID.
//            (default: 0)
//            -F <attribute numbers used for result filtering
//            The attribute numbers used for result filtering.
//            (default: -1)
            gsp.setOptions(new String[]{"-D"});
            gsp.buildAssociations(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * generates Weka.Instances out of a CSV file
     * @param csvFile
     * @author Stefan 19.07.2016
     */
    public Instances getInstancesFromCSV(String csvFile){
        Instances data = null;
        try {
            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(csvFile));
            data = loader.getDataSet();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * TODO no working yet
     * returns Database as a Weka Instances
     * @return
     * @author Stefan 19.07.2016
     */
    public Instances getInstances(){

        FastVector activities = new FastVector();

        Cursor cursor1 = AppGlobal.getHandler().getAllActions(AppGlobal.getHandler());
        if (cursor1.moveToFirst()) {
            do {
                activities.addElement(cursor1.getString(1).replace(" ",""));
            }
            while (cursor1.moveToNext());
        }
        // close cursor
        if (!cursor1.isClosed()) {
            cursor1.close();
        }

        Attribute caseID = new Attribute("CaseID",activities);
        Attribute activity = new Attribute("Activity",activities);
        Attribute starttime = new Attribute("Starttime");

        FastVector fv = new FastVector(3);
        fv.addElement(caseID);
        fv.addElement(activity);
        fv.addElement(starttime);

        ArrayList<Attribute> attrList = new ArrayList<>();
        attrList.add(caseID);
        attrList.add(activity);
        attrList.add(starttime);

        Cursor cursor2 = AppGlobal.getHandler().getAllRoutine();
        int p = cursor2.getCount();
        Instances inst = new Instances("output",fv,1);
        Instance instance  = new Instance(3);
        instance.setDataset(inst);

        return inst;
    }

//    /**
//     * Extract the frequent subsequences
//     *
//     * @throws CloneNotSupportedException
//     * @author Stefan 19.07.2016
//     */
//    public void findFrequentSequences(int m_MinSupport) throws CloneNotSupportedException {
//        m_CycleStart = TimeUtils.getCurrentDate();
//
//        //get the data
//        Instances originalDataSet = m_OriginalDataSet;
//        FastVector dataSequences = extractDataSequences(m_OriginalDataSet, m_DataSeqID);
//
//        //set the min support
//        long minSupportCount = Math.round(m_MinSupport * dataSequences.size());
//        FastVector kMinusOneSequences;
//        FastVector kSequences;
//
//        //ic_delete first attribute
//        originalDataSet.deleteAttributeAt(0);
//
//
//        FastVector oneElements = Element.getOneElements(originalDataSet);
//        m_Cycles = 1;
//
//        //Step 1:
//        //Returns all events of the given data set as Elements containing a single event.
//        //The order of events is determined by the header information of the corresponding ARFF file.
//        //Returns set of 1-Elements
//        kSequences = Sequence.oneElementsToSequences(oneElements);
//
//        //Updates the support count of a set of Sequence candidates according to a given set of data sequences.
//        Sequence.updateSupportCount(kSequences, dataSequences);
//
//        //Deletes Sequences of a given set which don't meet the minimum support count threshold.
//        kSequences = Sequence.deleteInfrequentSequences(kSequences, minSupportCount);
//
//        m_CycleEnd = TimeUtils.getCurrentDate();
//
//        if (kSequences.size() == 0) {
//            return;
//        }
//
//        //Step 2:
//        while (kSequences.size() > 0) {
//            m_CycleStart = TimeUtils.getCurrentDate();
//
//            m_AllSequentialPatterns.addElement(kSequences.copy());
//            kMinusOneSequences = kSequences;
//            //1./2. Generates all possible candidate k-Sequences and prunes the ones that contain an infrequent (k-1)-Sequence.
//            kSequences = Sequence.aprioriGen(kMinusOneSequences);
//            //3. Updates the support count of a set of Sequence candidates according to a given set of data sequences.
//            Sequence.updateSupportCount(kSequences, dataSequences);
//            //4. Deletes Sequences of a given set which don't meet the minimum support count threshold.
//            kSequences = Sequence.deleteInfrequentSequences(kSequences, minSupportCount);
//
//            m_CycleEnd = TimeUtils.getCurrentDate();
//
//            System.out.println("Cycle " + m_Cycles + " from " + m_CycleStart + " to " + m_CycleEnd);
//
//            m_Cycles++;
//        }
//    }
}
