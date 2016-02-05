package uni.mannheim.teamproject.diabetesplaner.StatisticsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 11.01.2016.
 */
public class LineChartFragment extends Fragment{

    public static LineChartFragment newInstance() {
        LineChartFragment fragment = new LineChartFragment();
        return fragment;
    }
    public LineChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_line_chart, container, false);
        // creating the x-Axes Values
        ArrayList<String> labels = new ArrayList<>();
        labels.add("8:00am");
        labels.add("12:00am");
        labels.add("16:00pm");
        labels.add("20:00pm");
        labels.add("22:00pm");

        //creating the values for the bar chart
        ArrayList<BarEntry> barValues = new ArrayList<>();
        barValues.add(new BarEntry(50f, 0));
        barValues.add(new BarEntry(10f, 1));
        barValues.add(new BarEntry(50f, 2));
        barValues.add(new BarEntry(60f, 3));
        barValues.add(new BarEntry(10f, 4));
        BarDataSet barDataSet = new BarDataSet(barValues, "Insulin Dosage");
        BarData barData = new BarData(labels, barDataSet);

        //creating the values for the line chart
        ArrayList<Entry> lineValues = new ArrayList<>();
        lineValues.add(new Entry(140f, 0));
        lineValues.add(new Entry(90f, 1));
        lineValues.add(new Entry(110f, 2));
        lineValues.add(new Entry(130f, 3));
        lineValues.add(new Entry(100f, 4));
        LineDataSet lineDataSet = new LineDataSet(lineValues, "Glucose Level");
        LineData lineData = new LineData(labels, lineDataSet);

        //creating CombinedData for the Chart with Bar and Line Data
        CombinedData combinedData = new CombinedData(labels);
        combinedData.setData(barData);
        combinedData.setData(lineData);

        // getting the reference to the chart and setting the data
        CombinedChart chart = (CombinedChart) inflaterView.findViewById(R.id.combinedInsulinGlucoseChart);
        chart.setData(combinedData);
        chart.setDescription("Combination of Glucose and Insulin Levels");

        return inflaterView;
    }

}