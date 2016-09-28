package uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 11.01.2016.
 */
public class RingChartFragment extends ChartFragment {

    public static RingChartFragment newInstance(){
        RingChartFragment fragment = new RingChartFragment();
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

        PieData pieData = this.getData("DAY");
        pieData.setValueFormatter(new PercentFormatter());
        PieChart chart = (PieChart) inflaterView.findViewById(R.id.activitypiechart);
        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.setDescription("");
        chart.highlightValues(null);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(10f);
        chart.setRotationEnabled(false);
        chart.setNoDataText(String.valueOf(R.string.no_data));
        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(10f);
        chart.invalidate();

        return inflaterView;
    }

    /**
     * @param timeFrame the time window of interest
     * @return PieData to be displayed in the Chart
     * @author Ivo Gosemann 08.04.2016
     * Method to retrieve the Chart Data from the DB and process it into a PieData object
     */
    public PieData getData(String timeFrame) {
        DataBaseHandler handler = AppGlobal.getHandler();
        Date date = TimeUtils.getCurrentDate();
//        ArrayList<ActivityItem> activityItems = handler.GetDay(handler,date);
        ArrayList<ActivityItem> activityItems = handler.getActivities(date, timeFrame);
        List<PieEntry> pieValues = new ArrayList<>();
        HashMap<String, Integer> valueMap = new HashMap<>();
        String label;
        int value;
        ActivityItem item;
        for (int i = 0; i < activityItems.size(); i++) {
            item = activityItems.get(i);
            label = handler.getActionById(item.getActivityId());
            if (!valueMap.containsKey(label)) {
                value = TimeUtils.getDurationMinutes(item.getStarttime(), item.getEndtime());
                valueMap.put(label, value);
            } else {
                value = valueMap.get(label);
                value += TimeUtils.getDurationMinutes(item.getStarttime(), item.getEndtime());
                valueMap.put(label, value);
            }
        }
        int j = 0;
        for (String entryKey : valueMap.keySet()) {
            float duration = (float) valueMap.get(entryKey);
            pieValues.add(new PieEntry(duration, entryKey));
            j++;
        }
        PieDataSet pieDataSet = new PieDataSet(pieValues, getResources().getString(R.string.activity));

        ArrayList<Integer> colors = new ArrayList<>();

//      Color bad
        pieDataSet.addColor(Color.rgb(239, 83, 80));
//      Color potential bad
        pieDataSet.addColor(Color.rgb(255, 202, 40));
//      Color good
        pieDataSet.addColor(Color.rgb(178, 255, 89));
//      Color no influence
        pieDataSet.addColor(Color.rgb(77, 208, 255));


        return new PieData(pieDataSet);
    }

    /**
     * @param timeFrame
     * @author Ivo Gosemann 08.04.2016
     * Method which updates the Chart based on the timeframe provided
     */
    @Override
    public void updateChart(String timeFrame) {
        PieChart chart = (PieChart) this.getView().findViewById(R.id.activitypiechart);
        PieData pieData = this.getData(timeFrame);

        chart.setData(pieData);
        chart.invalidate();

    }

}