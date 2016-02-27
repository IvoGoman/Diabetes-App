package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Jan on 22.02.16.
 */
public class bloodsugar_dialog extends DialogFragment implements View.OnClickListener{
    Button submit,cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.dialog_layout, null);

        setCancelable(false);
        submit = (Button) view.findViewById(R.id.bs_submit);
        cancel = (Button) view.findViewById(R.id.bs_cancel);
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        return view;
    }

    /***
     * Click Handler for the dialog.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.bs_submit)
        {
            dismiss();
        }
        else if(view.getId() == R.id.bs_cancel)
        {
            dismiss();
        }
    }

    /***
     * Converts mg/dl in mmol/l
     * @param mg
     * @return mmol/l
     */
    private double miligram_to_mol(double mg)
    {
        return mg * 0.0555;
    }

    /***
     * Converts mmol/l in mg/dl
     * @param mmol
     * @return mg/dl
     */
    private double mmol_to_milligram(double mmol)
    {
        return mmol * 18.0182;
    }
}