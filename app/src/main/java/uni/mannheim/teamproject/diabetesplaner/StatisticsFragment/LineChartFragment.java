package uni.mannheim.teamproject.diabetesplaner.StatisticsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Backend.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Backend.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 11.01.2016.
 */
public class LineChartFragment extends Fragment {

    public LineChartFragment() {
        // Required empty public constructor
    }

    public static LineChartFragment newInstance() {
        LineChartFragment fragment = new LineChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_line_chart, container, false);

//        chart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE});
        // getting the reference to the chart and setting the data
        CombinedChart chart = (CombinedChart) inflaterView.findViewById(R.id.combinedInsulinGlucoseChart);
        CombinedData combinedData = this.getData("DAY");
        chart.setData(combinedData);
        chart.setDescription("Combination of Blood Sugar and Insulin Levels");
        chart.setDrawGridBackground(false);
        chart.setDrawHighlightArrow(false);
//        YAxis rightAxis = chart.getAxisRight();
//        rightAxis.setAxisMinValue(0f);
//        rightAxis.setAxisMaxValue(160f);
        YAxis yAxisL = chart.getAxisLeft();
        yAxisL.setDrawGridLines(false);
        YAxis yAxisR = chart.getAxisRight();
        yAxisR.setDrawGridLines(false);
        XAxis xAxis = chart.getXAxis();
        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setEnabled(true);
        xAxis.setDrawGridLines(false);

        chart.invalidate();
        return inflaterView;
    }

    /**
     * @author Ivo Gosemann 08.04.2016
     * retrieves the data for the tiemframe from the DB and puts it in a CombinedData object
     * @param timeFrame String value indicating the timeframe for the chart
     * @return data to be set to the chart
     */
    public CombinedData getData (String timeFrame){
        DataBaseHandler handler = AppGlobal.getHandler();
        Date date = Util.getCurrentDate();

        ArrayList<String> dateList = handler.getAllTimestamps(handler, date,"DAY");
        // creating the x-Axes Values
        ArrayList<String> labels = new ArrayList<>();
        String dateValue="";
        if(!dateList.isEmpty()) {
            for (int i = 0; i < dateList.size(); i++) {
                dateValue = dateList.get(i);
                dateValue = Util.dateToTimeString(dateValue);
                labels.add(dateValue);
            }
        }else {
            labels.add("8:00am");
            labels.add("12:00am");
            labels.add("16:00pm");
            labels.add("20:00pm");
            labels.add("22:00pm");
        }
        //creating the values for the bar chart
        ArrayList<BarEntry> barValues = new ArrayList<>();
        ArrayList<Integer> insulinList = AppGlobal.getHandler().getAllInsulin(AppGlobal.getHandler(),date,"DAY");
        if (!insulinList.isEmpty()) {
            for (int i = 0; i < insulinList.size(); i++) {
                float value = (float) insulinList.get(i);
                barValues.add(new BarEntry(value, i));
            }
        } else {
            barValues.add(new BarEntry(50f, 0));
            barValues.add(new BarEntry(60f, 1));
            barValues.add(new BarEntry(75f, 2));
            barValues.add(new BarEntry(80f, 3));
            barValues.add(new BarEntry(20f, 4));

        }
        BarDataSet barDataSet= new BarDataSet(barValues,"Insulin Dosage");
        BarData barData = new BarData(labels,barDataSet);
//        ArrayList<Entry> lineValues1 = new ArrayList<>();
//        ArrayList<Integer> insulinList = AppGlobal.getHandler().getAllInsulin(AppGlobal.getHandler(), date);
//        if (!insulinList.isEmpty()) {
//            for (int i = 0; i < insulinList.size(); i++) {
//                float value = (float) insulinList.get(i);
//                lineValues1.add(new Entry(value, i));
//            }
//        } else {
//            lineValues1.add(new Entry(50f, 0));
//            lineValues1.add(new Entry(60f, 1));
//            lineValues1.add(new Entry(75f, 2));
//            lineValues1.add(new Entry(80f, 3));
//            lineValues1.add(new Entry(20f, 4));
//
//        }


//        LineDataSet lineDataSet1 = new LineDataSet(lineValues1, "Insulin Dosage");
//        lineDataSet1.setDrawHighlightIndicators(false);
//       LineData lineData1 = new LineData(labels,lineDataSet1);

        //creating the values for the line chart
        ArrayList<Entry> lineValues2 = new ArrayList<>();
        ArrayList<Integer> sugarList = AppGlobal.getHandler().getAllBloodSugar(AppGlobal.getHandler(), date,"DAY");
        if (!sugarList.isEmpty()) {
            for (int i = 0; i < sugarList.size(); i++) {
                float value = (float) sugarList.get(i);
                lineValues2.add(new Entry(value, i));
            }
        } else {
            lineValues2.add(new Entry(140f, 0));
            lineValues2.add(new Entry(90f, 1));
            lineValues2.add(new Entry(110f, 2));
            lineValues2.add(new Entry(130f, 3));
            lineValues2.add(new Entry(100f, 4));}

        LineDataSet lineDataSet2 = new LineDataSet(lineValues2, "Blood Sugar Level");
        lineDataSet2.setDrawHighlightIndicators(false);
        LineData lineData2 = new LineData(labels, lineDataSet2);

        //creating CombinedData for the Chart with Bar and Line Data
        CombinedData combinedData = new CombinedData(labels);
//        barData.setHighlightEnabled(false);
        combinedData.setData(lineData2);
//        lineData.setHighlightEnabled(false);
        combinedData.setData(barData);
        return combinedData;
    }

    /**
     * @author Ivo Gosemann 08.04.2016
     * updates the chart with the new time window
     * @param timeFrame String value indicating the timeframe for the chart
     */
    public void updateChart(String timeFrame){
        CombinedChart chart = (CombinedChart) this.getView().findViewById(R.id.combinedInsulinGlucoseChart);
        CombinedData combinedData = this.getData(timeFrame);
        chart.setData(combinedData);
        chart.invalidate();
    }
}