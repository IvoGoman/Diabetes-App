package uni.mannheim.teamproject.diabetesplaner.UI.ActivityInput;

/**
 * Created by Stefan on 10.01.2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Domain.Datafile;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

public class CustomListView extends ArrayAdapter<Datafile>{

    private final Activity context;
    private final ArrayList<Datafile> files;
    private View rowView;

    /**
     * Constructor.
     * @param context context activity
     * @param files ArrayList<String> with the items that are added to the list
     */
    public CustomListView(Activity context, ArrayList<Datafile> files) {
        super(context, R.layout.fragment_activity_input_list_item, files);
        this.context = context;
        this.files = files;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //layout of a single list item
        rowView= inflater.inflate(R.layout.fragment_activity_input_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView txtSubTitle = (TextView) rowView.findViewById(R.id.subtxt);

        //set title text to the String in the ArrayList at position "position"
        txtTitle.setText(files.get(position).getTitle());

        //set the subtitle
        txtSubTitle.setText(getContext().getResources().getString(R.string.added) + ": " + TimeUtils.dateToDateTimeString(files.get(position).getDate()));
        txtSubTitle.setTextColor(getContext().getResources().getColor(R.color.textColorSecondary));

        //create ic_delete icon and adds onClickListener to it
        ImageView deleteIcon = (ImageView) rowView.findViewById(R.id.delete_icon_activity_input);
        deleteIcon.setVisibility(View.GONE);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you sure you want to Delete?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                remove(files.get(position));
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        return rowView;
    }
}
