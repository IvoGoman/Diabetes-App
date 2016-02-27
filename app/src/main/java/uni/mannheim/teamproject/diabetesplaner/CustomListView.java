package uni.mannheim.teamproject.diabetesplaner;

/**
 * Created by Stefan on 10.01.2016.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomListView extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> files;
    private View rowView;

    /**
     * Constructor.
     * @param context context activity
     * @param files ArrayList<String> with the items that are added to the list
     */
    public CustomListView(Activity context, ArrayList<String> files) {
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
        txtTitle.setText(files.get(position));

        //set the subtitle
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        txtSubTitle.setText("  Added: " + dateFormat.format(date));

        //create delete icon and adds onClickListener to it
        ImageView deleteIcon = (ImageView) rowView.findViewById(R.id.delete_icon_activity_input);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(files.get(position));
               /* if(files.size() == 0){
                    context.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }*/
            }
        });

        return rowView;
    }
}
