package uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment;

import android.content.Context;

import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.ColorUtils;

/**
 * Created by Ivo on 28.09.2016.
 */
public class CustomPieDataSet extends PieDataSet {
    private Context context;
    public CustomPieDataSet(List<PieEntry> yVals, String label, Context context) {
        super(yVals, label);
        this.context = context;
    }

    @Override
    public int getColor(int index){
        String label = getEntryForIndex(index).getLabel();
        DataBaseHandler handler = AppGlobal.getHandler();
            return ColorUtils.getColorBySuperActivity(handler.getSuperActivityID(handler.getActivityID(label)),context);
        }
    }
