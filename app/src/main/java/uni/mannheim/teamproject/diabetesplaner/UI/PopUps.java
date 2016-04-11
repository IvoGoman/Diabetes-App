package uni.mannheim.teamproject.diabetesplaner.UI;

/**
 * Created by leonidgunko on 08.01.16.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.widget.EditText;
import android.widget.Toast;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.TechnicalServices.LocationLogic;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * PopUps shows Alert Dialogs where the user can
 * select or enter data
 */
public class PopUps implements Runnable {

    //private static Context mContext;
    //public static Context getAppContext(){
    //    return mContext;
    //}
//    public static void setAppContext(Context con){
//        mContext = con;
//    }



    public static void addLocation (final Context mContext, final DataBaseHandler Helper, final Location location) {
        //Context here = GuiMain.getAppContext();

        final EditText input = new EditText(mContext);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AppGlobal.setTime(0);
        builder.setTitle("Where are you now?");
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                // add self-defined location record to the location table in the database
                if (LocationLogic.checkAndAddLocation(Helper, value, location.getLatitude(), location.getLongitude()))
                    Toast.makeText(mContext, "Location was added", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "Location with this name exists already", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        builder.show();
    }



    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
