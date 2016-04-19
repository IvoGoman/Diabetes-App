package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import uni.mannheim.teamproject.diabetesplaner.Backend.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Backend.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;
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

//                Date date = getDrHandler().getDate();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                String dateString = sdf.format(date);


                String dateStringStart = Util.dateToDateTimeString(getStartDate());
                String dateStringEnd = Util.dateToDateTimeString(getEndDate());

                if(!isTimeValid()){
                    InvalidTimeDialog itd = new InvalidTimeDialog();
                    itd.show(getFragmentManager(),"invalidTime");
                }else {

                    //AppGlobal.getHandler().ReplaceActivity(AppGlobal.getHandler(), getSelectedItem(), 1, dateStringStart, dateStringEnd);
                    ActivityItem activityItem = new ActivityItem(getSelectedItem(), 0, dateStringStart, dateStringEnd, getImagePath(), getMeal());
                    AppGlobal.getHandler().ReplaceActivity(AppGlobal.getHandler(), activityItem);


//                    AppGlobal.getHandler().ReplaceActivity(AppGlobal.getHandler(), getSelectedItem(), 1, dateString + " " + getStarttime(), dateString + " " + getEndtime());
//                    ActivityItem activityItem = new ActivityItem(getSelectedItem(), 0, Util.setTime(dateString, getStarttime()), Util.setTime(dateString, getEndtime()), getImageUri(), getImage(), getMeal());
//                    getDrHandler().add(activityItem);
                    getDrHandler().update();

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
