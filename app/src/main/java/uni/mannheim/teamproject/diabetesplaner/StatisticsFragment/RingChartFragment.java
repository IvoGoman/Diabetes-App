package uni.mannheim.teamproject.diabetesplaner.StatisticsFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 11.01.2016.
 *
 */
public class RingChartFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RingChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RingChartFragment newInstance(String param1, String param2) {
        RingChartFragment fragment = new RingChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


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

        ArrayList<Integer> colors = new ArrayList<>();

        /**   colors.add(R.color.good);
         colors.add(R.color.bad);
         colors.add(R.color.potential_bad);
         colors.add(R.color.no_influence);
         colors.add(R.color.good);
         pieDataSet.setColors(colors);*/
        pieDataSet.addColor(R.color.good);
        pieDataSet.addColor(R.color.bad);
        pieDataSet.addColor(R.color.potential_bad);
        pieDataSet.addColor(R.color.no_influence);
        pieDataSet.addColor(R.color.good);

        PieData pieData = new PieData(labels, pieDataSet);

        PieChart chart = (PieChart) inflaterView.findViewById(R.id.activitypiechart);
        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.setDescription("Overview of activities");
        chart.highlightValues(null);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setRotationEnabled(false);
        chart.invalidate();

        return inflaterView;
    }

}