package uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag;

import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * old subfragment for old measurements tab.. not used
 * created by Naira
 */

public class MeasurementFragment extends Fragment {
    private static ArrayAdapter adapter;
    public static AbsListView lv;
    public static ArrayList<String> measurementList = new ArrayList<String>();
    public static final String TAG = MeasurementFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static View inflaterView;
    final DataBaseHandler DBHandler = AppGlobal.getHandler();

    /**
     * @param param1
     * @param param2
     * @return
     * @author Naira
     */
    public static MeasurementFragment newInstance(String param1, String param2) {
        MeasurementFragment fragment = new MeasurementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MeasurementFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param savedInstanceState
     * @author Naira
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @author Naira
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_measurement, container, false);
        final ImageButton floatingButton = (ImageButton) inflaterView.findViewById(R.id.add_button);

        //added to adjust floating button location
        ViewTreeObserver viewTreeObserver = floatingButton.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    // not sure the above is equivalent, but that's beside the point for this example...
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) floatingButton.getLayoutParams();
                    params.setMargins(0, 0, 16, 16); // (int left, int top, int right, int bottom)
                    floatingButton.setLayoutParams(params);

                }
            });
        }


        floatingButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v
             * @author Naira
             */
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.pop_measurement_window);
                final EditText GlucoseInput = (EditText) dialog.findViewById(R.id.editText);
                GlucoseInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                final EditText InsulinInput = (EditText) dialog.findViewById(R.id.editText2);
                InsulinInput.setInputType(InputType.TYPE_CLASS_NUMBER);

                dialog.setTitle("Input Measurements");
                measurementList.clear();

                Cursor cursor = AppGlobal.getHandler().getAllMeasurements(1);

                if (cursor.moveToFirst()) {
                    do {
                        String MeasurementString = cursor.getString(1) + " " + cursor.getString(2) + "," + " " + cursor.getString(2) + " units";
                        measurementList.add(MeasurementString);
                    } while (cursor.moveToNext());
                }
                if(!cursor.isClosed()){
                    cursor.close();
                }

                lv = (AbsListView) inflaterView.findViewById(R.id.MListView);
                adapter = new CustomListView(getActivity(), measurementList);
                dialog.show();
            }
        });

        return inflaterView;
    }

}
