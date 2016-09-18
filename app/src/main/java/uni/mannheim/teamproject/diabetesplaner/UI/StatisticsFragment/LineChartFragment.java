package uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
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

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 11.01.2016.
 */
public class LineChartFragment extends ChartFragment {

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
     * @param timeFrame String value indicating the timeframe for the chart
     * @return data to be set to the chart
     * @author Ivo Gosemann 08.04.2016
     * retrieves the data for the tiemframe from the DB and puts it in a CombinedData object
     */
    public CombinedData getData(String timeFrame) {
        DataBaseHandler handler = AppGlobal.getHandler();
        Date date = TimeUtils.getCurrentDate();

        ArrayList<MeasureItem> insulinList = AppGlobal.getHandler().getMeasurementValues(AppGlobal.getHandler(), date, "DAY", "insulin");
        // creating the x-Axes Values
        ArrayList<String> labels = new ArrayList<>();
        String dateValue = "";
        for (int i = 0; i < insulinList.size(); i++) {
            dateValue = TimeUtils.getTimeStampAsDateString(insulinList.get(i).getTimestamp());
            dateValue = TimeUtils.dateToTimeString(dateValue);
            labels.add(dateValue);
        }
        //creating the values for the bar chart
        ArrayList<BarEntry> barValues = new ArrayList<>();


        for (int i = 0; i < insulinList.size(); i++) {
            float value = (float) insulinList.get(i).getMeasure_value();
            barValues.add(new BarEntry(value, i));
        }
        BarDataSet barDataSet = new BarDataSet(barValues, "Insulin Dosage");
        BarData barData = new BarData(labels, barDataSet);

        //creating the values for the line chart
        ArrayList<Entry> lineValues2 = new ArrayList<>();
        ArrayList<MeasureItem> sugarList = AppGlobal.getHandler().getMeasurementValues(AppGlobal.getHandler(), date, "DAY", "bloodsugar");
        ArrayList<String> labelsBloodsugar = new ArrayList<>();
        dateValue = "";

        for (int i = 0; i < insulinList.size(); i++) {
            dateValue = TimeUtils.getTimeStampAsDateString(insulinList.get(i).getTimestamp());
            dateValue = TimeUtils.dateToTimeString(dateValue);
            labelsBloodsugar.add(dateValue);
        }

        for (int i = 0; i < sugarList.size(); i++) {
            float value = (float) sugarList.get(i).getMeasure_value();
            lineValues2.add(new Entry(value, i));
        }


        LineDataSet lineDataSet2 = new LineDataSet(lineValues2, "Blood Sugar Level");
        lineDataSet2.setDrawHighlightIndicators(false);
        LineData lineData2 = new LineData(labelsBloodsugar, lineDataSet2);

        //creating CombinedData for the Chart with Bar and Line Data
        CombinedData combinedData = new CombinedData(labels);
//        barData.setHighlightEnabled(false);
        combinedData.setData(lineData2);
//        lineData.setHighlightEnabled(false);
        combinedData.setData(barData);
        return combinedData;
    }

    /**
     * @param timeFrame String value indicating the timeframe for the chart
     * @author Ivo Gosemann 08.04.2016
     * updates the chart with the new time window
     */
    @Override
    public void updateChart(String timeFrame) {
        CombinedChart chart = (CombinedChart) this.getView().findViewById(R.id.combinedInsulinGlucoseChart);
        CombinedData combinedData = this.getData(timeFrame);
        chart.setData(combinedData);
        chart.invalidate();
    }
}