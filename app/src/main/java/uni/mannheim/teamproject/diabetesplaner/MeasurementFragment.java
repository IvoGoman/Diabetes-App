package uni.mannheim.teamproject.diabetesplaner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * created by Naira
 */

public class MeasurementFragment extends Fragment {


    public MeasurementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterView = inflater.inflate(R.layout.fragment_measurement, container, false);

        ImageButton b= (ImageButton) inflaterView.findViewById(R.id.imageButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeasurementPop.class);
                startActivity(intent);

            }
        });

        return inflaterView;
    }
}
