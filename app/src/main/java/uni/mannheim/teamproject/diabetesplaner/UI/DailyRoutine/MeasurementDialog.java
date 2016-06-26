package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag.MeasurementFragment;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasurementInputHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * created by Naira
 */

public class MeasurementDialog extends MeasurementInputDialog {
 //   private static ArrayAdapter adapter;
  //  public static AbsListView lv;
 //   public static ArrayList<String> measurementList = new ArrayList<String>();
  //  public static final String TAG = MeasurementFragment.class.getSimpleName();
  //  private static final String ARG_PARAM1 = "param1";
  //  private static final String ARG_PARAM2 = "param2";
  //  private String mParam1;
  //  private String mParam2;

  //  private static View inflaterView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Input Measurements");

        View view = getLayout();

        builder.setView(view);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //add measurements
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}