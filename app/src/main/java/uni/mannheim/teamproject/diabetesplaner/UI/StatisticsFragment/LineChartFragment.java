package uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 11.01.2016.
 */
public class LineChartFragment extends ChartFragment {
private final String tag = "CHARTS";
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
        LineChart chart = (LineChart) inflaterView.findViewById(R.id.combinedInsulinGlucoseChart);
        LineData lineData = this.getData("DAY");
        chart.setData(lineData);
        chart.setDescription("Combination of Blood Sugar and Insulin Levels");
        chart.setDrawGridBackground(false);
//        chart.setDrawHighlightArrow(false);
//        YAxis rightAxis = chart.getAxisRight();
//        rightAxis.setAxisMinValue(0f);
//        rightAxis.setAxisMaxValue(160f);
        YAxis yAxisL = chart.getAxisLeft();
        yAxisL.setAxisMinValue(0f);
//        yAxisL.setAxisMaxValue(500f);
        yAxisL.setDrawGridLines(false);
        YAxis yAxisR = chart.getAxisRight();
        yAxisR.setAxisMinValue(0f);
//        yAxisR.setAxisMaxValue(500f);
        yAxisR.setDrawGridLines(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
//        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if ((int) value < 25) {
                    String result;
                    if ((int) value < 10) {
                        result = "0" + (int) value + ":00";
                    } else {
                        result = String.valueOf((int) value) + ":00";
                    }
                    return result;
                } else {
                    long millis = (long) value;
                    return sdf.format(new Date(millis));
                }
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
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
    public LineData getData(String timeFrame) {
        DataBaseHandler handler = AppGlobal.getHandler();
        Date date = TimeUtils.getCurrentDate();
        Entry entry = null;

//        Process Insulin Entries
        List<Entry> lineValues = getEntries(date, timeFrame, "insulin");
        Collections.sort(lineValues, new Comparator<Entry>() {
                    @Override
                    public int compare(Entry lhs, Entry rhs) {
                        if (lhs.getX()>rhs.getX()){
                            return 1;
                        }else{
                            return -1;
                        }
                    }
                });
        LineDataSet lineDataSet = new LineDataSet(lineValues, "Insulin");
        lineDataSet.setDrawHighlightIndicators(false);
        LineData insulinEntries = new LineData(lineDataSet);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

//        process Bloodsugar Entries
        List<Entry> lineValues2 = getEntries(date, timeFrame, "bloodsugar");
        Collections.sort(lineValues2, new Comparator<Entry>() {
            @Override
            public int compare(Entry lhs, Entry rhs) {
                if (lhs.getX()>rhs.getX()){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        LineDataSet lineDataSet2 = new LineDataSet(lineValues2, "Blood Sugar Level");
        lineDataSet2.setDrawHighlightIndicators(false);
        LineData bloodsugarEntries = new LineData(lineDataSet2);
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setCircleColor(Color.RED);
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet2);
        LineData lineData = new LineData(dataSets);

        return lineData;
    }

    /**
     * @param timeFrame String value indicating the timeframe for the chart
     * @author Ivo Gosemann 08.04.2016
     * updates the chart with the new time window
     */
    @Override
    public void updateChart(String timeFrame) {
        LineChart chart = (LineChart) this.getView().findViewById(R.id.combinedInsulinGlucoseChart);
        LineData lineData = this.getData(timeFrame);
        XAxis xAxis = chart.getXAxis();
        chart.setData(lineData);
        chart.invalidate();
    }

    private List<Entry> getEntries(Date date, String timeFrame, String measurekind) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        ArrayList<MeasureItem> measurements = AppGlobal.getHandler().getMeasurementValues(date, timeFrame, measurekind);
        String[] window = TimeUtils.getWindowStartEnd(date, timeFrame);
        int i = 0;
        HashMap<Float,Float> values = new HashMap<>();

        List<Entry> result = new ArrayList<>();
        Date startdate = TimeUtils.getDateFromString(window[0]);
        Entry entry = null;
        Date entryDate = null;
        float entryTimestamp = 0;
        float value = 0;
        for (MeasureItem item : measurements) {
            if(timeFrame.equals("DAY")) {
                c.setTimeInMillis(item.getTimestamp());
                c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR_OF_DAY),0,0);
                entryTimestamp = (float) c.getTimeInMillis();
            } else {
                c.setTime(date);
                c.setTimeInMillis(item.getTimestamp());
                c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),0,0,0);
                entryTimestamp = (float) c.getTimeInMillis();
                Log.d(tag, TimeUtils.getTimeStampAsDateString(((long) entryTimestamp)) +" Calendar Value");
            }
            if(values.containsKey(entryTimestamp)){
                value = values.get(entryTimestamp);
                value = (value + (float) item.getMeasure_value()) / 2;
                Log.d(tag, TimeUtils.getTimeStampAsDateString(((long) entryTimestamp)) +" Contained");
                values.put(entryTimestamp, value);
            } else{
                values.put(entryTimestamp, (float) item.getMeasure_value());
                Log.d(tag, TimeUtils.getTimeStampAsDateString(((long) entryTimestamp)) +" Not Contained");
            }
        }
        long timestamp;
        switch(timeFrame){
            case("DAY"):
                i = 24;
                for(int j = 23; j>=0; j--){
                    c.setTime(date);
                    c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), j, 0,0);
                    timestamp = c.getTimeInMillis();

                    if (values.containsKey((float)timestamp)){
                        entry = new Entry( j, values.get((float)timestamp));
                        Log.d(tag,TimeUtils.getTimeStampAsDateString(((long) entry.getX())) + " DAY Contained");
                        result.add(entry);
                    } else {
                        entry = new Entry(j, 0f);
                        Log.d(tag,TimeUtils.getTimeStampAsDateString(((long) entry.getX())) + " DAY Not Contained");
                        result.add(entry);
                    }
                }
                break;
            case("WEEK"):
                i = 7;
                for(int j = 7; j >= 0; j--){
                    c.setTime(date);
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH) - j,0,0,0);
                    timestamp = c.getTimeInMillis();
                    if (values.containsKey((float)timestamp)){
                        entry = new Entry( (float) timestamp, values.get((float)timestamp));
                        result.add(entry);
                    } else {
                        entry = new Entry((float)timestamp, 0f);
                        result.add(entry);
                    }
                }
                break;
            case("MONTH"):
                i = 30;
                for(int j = 30; j>0; j--){
                    c.setTime(date);
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH) - j,0,0,0);
                    timestamp = c.getTimeInMillis();
                    Log.d(tag,String.valueOf(timestamp));
                    if (values.containsKey((float)timestamp)){
                        entry = new Entry( (float) timestamp, values.get((float)timestamp));
                        result.add(entry);
                    } else {
                        entry = new Entry((float)timestamp, 0f);
                        result.add(entry);
                    }
        }
                break;

    }
        return result;

}
}