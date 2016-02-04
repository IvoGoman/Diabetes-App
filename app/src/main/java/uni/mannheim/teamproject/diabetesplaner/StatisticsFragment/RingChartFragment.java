package uni.mannheim.teamproject.diabetesplaner.StatisticsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.jfree.chart.entity.PieSectionEntity;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 11.01.2016.
 */
public class RingChartFragment extends Fragment{

    public RingChartFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflaterView = inflater.inflate(R.layout.fragment_ring_chart, container, false);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Running");
        labels.add("Sleeping");
        labels.add("Working");
        labels.add("Eating");
        labels.add("Relaxing");

        ArrayList<Entry> pieValues = new ArrayList<>();
        pieValues.add(new Entry(1f, 0));
        pieValues.add(new Entry(8f, 1));
        pieValues.add(new Entry(8f, 2));
        pieValues.add(new Entry(3f, 3));
        pieValues.add(new Entry(4f, 4));

        PieDataSet pieDataSet = new PieDataSet(pieValues, "Activities");
        PieData pieData = new PieData(labels, pieDataSet);

        PieChart chart = (PieChart) inflaterView.findViewById(R.id.activitypiechart);
        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.setDescription("Overview of activities");

        return inflaterView;
    }

}