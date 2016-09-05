package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 31.01.2016.
 */
public class EditDialog extends InputDialog {

    private int indexSelected;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.edit_dialog_title);

        View view = getLayout();

        builder.setView(view);

        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //ActivityItem activityItem = new ActivityItem(getSelectedItem(), 0, getStarttime(), getEndtime(), getImagePath(), getMeal());
                if(!isTimeValid()){
                    InvalidTimeDialog itd = new InvalidTimeDialog();
                    itd.show(getFragmentManager(),"invalidTime");
                }else {
                    // getDrHandler().edit(indexSelected, activityItem);
                    ActivityItem item = getDrHandler().getDailyRoutine().get(indexSelected);
                    String dateStart = TimeUtils.dateToDateTimeString(item.getStarttime());
                    String dateEnd = TimeUtils.dateToDateTimeString(item.getEndtime());
                    String newDateStart = TimeUtils.dateToDateTimeString(getStartDate());
                    String newDateEnd = TimeUtils.dateToDateTimeString(getEndDate());
                    Log.d("EditDialog", newDateStart);
                    Log.d("EditDialog", newDateEnd);
//                    String newDateStart = Util.combineDateAndTime(getDrHandler().getDate(), activityItem.getStarttime());
//                    String newDateEnd = Util.combineDateAndTime(getDrHandler().getDate(), activityItem.getEndtime());
                    AppGlobal.getHandler().DeleteActivity(AppGlobal.getHandler(), dateStart, dateEnd);
                    AppGlobal.getHandler().ReplaceActivity(AppGlobal.getHandler(), getSelectedActivity(), getSelectedSubActivity(), newDateStart, newDateEnd);
                    getDrHandler().update();
                }
                // FIRE ZE MISSILES!
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

    /**
     * set the index of the activity that is selected
     * @param indexSelected
     */
    public void setSelected(int indexSelected){
        this.indexSelected = indexSelected;
    }


}
