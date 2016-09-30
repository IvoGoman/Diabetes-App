package uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
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
 * Created by Ivo
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
        chart.setDescription(getString(R.string.current_day));
        chart.setDescriptionTextSize(15f);
        chart.highlightValues(null);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(10f);
        chart.setRotationEnabled(false);
        chart.setNoDataText(String.valueOf(R.string.no_data));
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
        CustomPieDataSet pieDataSet = new CustomPieDataSet(pieValues, getResources().getString(R.string.activity), this.getContext());

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
        chart.setData(pieData);
        chart.invalidate();

    }

}