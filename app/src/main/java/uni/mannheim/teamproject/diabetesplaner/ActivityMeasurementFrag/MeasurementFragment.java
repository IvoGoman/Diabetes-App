package uni.mannheim.teamproject.diabetesplaner.ActivityMeasurementFrag;

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

import uni.mannheim.teamproject.diabetesplaner.Backend.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Backend.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Backend.MeasurementInputHandler;
import uni.mannheim.teamproject.diabetesplaner.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
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

    MeasurementInputHandler MeasurementInputHandlr= new MeasurementInputHandler();
    final DataBaseHandler DBHandler = new DataBaseHandler(getContext());

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_measurement, container, false);
        final ImageButton floatingButton = (ImageButton) inflaterView.findViewById(R.id.add_button);

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
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.pop_measurement_window);
                final EditText GlucoseInput = (EditText) dialog.findViewById(R.id.editText);
                GlucoseInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                //GlucoseInput.setText("My Edit");
                final EditText InsulinInput = (EditText) dialog.findViewById(R.id.editText2);
                InsulinInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                final TextView Add = (TextView) dialog.findViewById(R.id.textView4);

                // Button Done = (Button) dialog.findViewById(R.id.button);
                dialog.setTitle("Input Measurements");
                measurementList.clear();
                Cursor cursor = AppGlobal.getHandler().getAllMeasurements(AppGlobal.getHandler(),1);
                if (cursor.moveToFirst()) {
                    do {
                        String MeasurementString = cursor.getString(1) + " " + cursor.getString(2) + "," + " " + cursor.getString(2) + " units";
                        measurementList.add(MeasurementString);
                    } while (cursor.moveToNext());
                }

                lv = (AbsListView) inflaterView.findViewById(R.id.MListView);
                adapter = new CustomListView(getActivity(), measurementList);

                Add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //   if (GlucoseInput.getText().toString().equals("") && InsulinInput.getText().toString().equals("")){
                        //      Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_LONG).show();
                        //  }
                        //  else {
                        String MeasurementString = GlucoseInput.getText().toString() + " mg/dl" + "," + " " + InsulinInput.getText().toString() + " units";
                        measurementList.add(MeasurementString);
                        Toast.makeText(getActivity(), "Measurements have been added", Toast.LENGTH_LONG).show();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        java.util.Date CurDatTime = new Date();
                        String SCurDatTime = format.format(CurDatTime);
                        AppGlobal.getHandler().InsertBloodsugar(AppGlobal.getHandler(), 1, Double.parseDouble( GlucoseInput.getText().toString()),"mg/dl");
                        AppGlobal.getHandler().InsertInsulin(AppGlobal.getHandler(),1, Integer.parseInt( InsulinInput.getText().toString()),"ml");
                        ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
                        // }
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return inflaterView;
    }


}
