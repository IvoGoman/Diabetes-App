package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import uni.mannheim.teamproject.diabetesplaner.Backend.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 31.01.2016.
 */
public class AddDialog extends InputDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_dialog_title);

        View view = getLayout();

        builder.setView(view);


        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityItem activityItem = new ActivityItem(getSelectedItem(), 0, getStarttime(), getEndtime());
                        if(!isTimeValid()){
                            InvalidTimeDialog itd = new InvalidTimeDialog();
                            itd.show(getFragmentManager(),"invalidTime");
                        }else {
                            getDrHandler().add(activityItem);
                        }
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