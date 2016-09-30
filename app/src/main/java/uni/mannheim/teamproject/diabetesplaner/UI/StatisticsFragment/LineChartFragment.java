package uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment;

import android.graphics.Color;
import android.os.Bundle;
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

import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Ivo
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

        // getting the reference to the chart and setting the data
        LineChart chart = (LineChart) inflaterView.findViewById(R.id.combinedInsulinGlucoseChart);
        LineData lineData = this.getData("DAY");
        chart.setData(lineData);
        chart.setDescription(getString(R.string.current_day));
        chart.setDescriptionTextSize(15f);
        chart.setDrawGridBackground(false);
        YAxis yAxisL = chart.getAxisLeft();
        yAxisL.setAxisMinValue(0f);
        yAxisL.setAxisMaxValue(0.85f);
        yAxisL.setDrawGridLines(false);
        YAxis yAxisR = chart.getAxisRight();
        yAxisR.setAxisMinValue(0f);
        yAxisR.setDrawGridLines(false);
        yAxisR.setAxisMinValue(50f);
        yAxisR.setAxisMaxValue(275f);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                hour values of the day
                if ((int) value < 25) {
                    String result;
                    if ((int) value < 10) {
                        result = "0" + (int) value + ":00";
                    } else {
                        result = String.valueOf((int) value) + ":00";
                    }
                    return result;
//                    long timestamps
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
        Date date = TimeUtils.getCurrentDate();

//        Process Insulin Entries
        List<Entry> lineValues = getEntries(date, timeFrame, "insulin");
        Collections.sort(lineValues, new Comparator<Entry>() {
            @Override
            public int compare(Entry lhs, Entry rhs) {
                if (lhs.getX() > rhs.getX()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        LineDataSet lineDataSet = new LineDataSet(lineValues, getString(R.string.insulin_mlcc));
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

//        process Bloodsugar Entries
        List<Entry> lineValues2 = getEntries(date, timeFrame, "bloodsugar");
        Collections.sort(lineValues2, new Comparator<Entry>() {
            @Override
            public int compare(Entry lhs, Entry rhs) {
                if (lhs.getX() > rhs.getX()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        LineDataSet lineDataSet2 = new LineDataSet(lineValues2, getString(R.string.bloodsugar_mgdl));
        lineDataSet2.setDrawHighlightIndicators(false);
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
        String desc = "";
        switch (timeFrame) {
            case ("DAY"):
                desc = getString(R.string.current_day);
                break;
            case ("WEEK"):
                desc = getString(R.string.last_week);
                break;
            case ("MONTH"):
                desc = getString(R.string.last_month);
                break;
            case ("YEAR"):
                desc = getString(R.string.last_year);
                break;
        }
        chart.setDescription(desc);
        chart.setData(lineData);
        chart.invalidate();
    }

    /**
     * @param date        current date
     * @param timeFrame   "DAY", "WEEK", "MONTH" or "YEAR"
     * @param measurekind "bloodsugar" or "insulin"
     * @return List of all Measurement entries for the timeframe and measurekind
     */
    private List<Entry> getEntries(Date date, String timeFrame, String measurekind) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        ArrayList<MeasureItem> measurements = AppGlobal.getHandler().getMeasurementValues(date, timeFrame, measurekind);
        String[] window = TimeUtils.getWindowStartEnd(date, timeFrame);
        HashMap<Float, Float> values = new HashMap<>();

        List<Entry> result = new ArrayList<>();
        Entry entry;
        float entryTimestamp;
        float value;
        for (MeasureItem item : measurements) {
            if (timeFrame.equals("DAY")) {
                c.setTimeInMillis(item.getTimestamp());
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), 0, 0);
                entryTimestamp = (float) c.getTimeInMillis();
            } else if (timeFrame.equals("WEEK") || timeFrame.equals("MONTH")) {
                c.setTime(date);
                c.setTimeInMillis(item.getTimestamp());
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                entryTimestamp = (float) c.getTimeInMillis();
            } else {
                c.setTime(date);
                c.setTimeInMillis(item.getTimestamp());
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 0, 0, 0, 0);
                entryTimestamp = (float) c.getTimeInMillis();
            }
            if (values.containsKey(entryTimestamp)) {
                value = values.get(entryTimestamp);
                value = (value + (float) item.getMeasure_value()) / 2;
                values.put(entryTimestamp, value);
            } else {
                values.put(entryTimestamp, (float) item.getMeasure_value());
            }
        }
        long timestamp;
        switch (timeFrame) {
            case ("DAY"):
                for (int j = 23; j >= 0; j--) {
                    c.setTime(date);
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), j, 0, 0);
                    timestamp = c.getTimeInMillis();

                    if (values.containsKey((float) timestamp)) {
                        entry = new Entry(j, values.get((float) timestamp));
                        result.add(entry);
                    } else {
                        entry = new Entry(j, 0f);
                        result.add(entry);
                    }
                }
                break;
            case ("WEEK"):
                for (int j = 7; j >= 0; j--) {
                    c.setTime(date);
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH) - j, 0, 0, 0);
                    timestamp = c.getTimeInMillis();
                    if (values.containsKey((float) timestamp)) {
                        entry = new Entry((float) timestamp, values.get((float) timestamp));
                        result.add(entry);
                    } else {
                        entry = new Entry((float) timestamp, 0f);
                        result.add(entry);
                    }
                }
                break;
            case ("MONTH"):
                for (int j = 30; j >= 0; j--) {
                    c.setTime(date);
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH) - j, 0, 0, 0);
                    timestamp = c.getTimeInMillis();
                    if (values.containsKey((float) timestamp)) {
                        entry = new Entry((float) timestamp, values.get((float) timestamp));
                        result.add(entry);
                    } else {
                        entry = new Entry((float) timestamp, 0f);
                        result.add(entry);
                    }
                }
                break;
            case ("YEAR"):
                for (int j = 12; j >= 0; j--) {
                    c.setTime(date);
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) - j, 0, 0, 0, 0);
                    timestamp = c.getTimeInMillis();
                    if (values.containsKey((float) timestamp)) {
                        entry = new Entry((float) timestamp, values.get((float) timestamp));
                        result.add(entry);
                    } else {
                        entry = new Entry((float) timestamp, 0f);
                        result.add(entry);
                    }
                }
                break;
        }
        return result;

    }
}