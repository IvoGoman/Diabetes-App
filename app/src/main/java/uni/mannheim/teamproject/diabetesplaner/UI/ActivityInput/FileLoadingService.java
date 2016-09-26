package uni.mannheim.teamproject.diabetesplaner.UI.ActivityInput;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityInputHandler;

/**
 * Created by Stefan on 26.09.2016.
 */
public class FileLoadingService extends IntentService {

    ActivityInputHandler ActivityInputHndlr = new ActivityInputHandler();

    public FileLoadingService() {
        super("FileLoadingService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String fileString = workIntent.getStringExtra(ActivityFragment.FILEPATH);

        //do the file loading
        ActivityInputHndlr.loadIntoDatabase(fileString);

        String status = "completed";
        Intent localIntent =
                new Intent(ActivityFragment.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(ActivityFragment.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
