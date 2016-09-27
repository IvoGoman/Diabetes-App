package uni.mannheim.teamproject.diabetesplaner.UI.Settings;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Jens on 29.02.16.
 */

//TODO: connect with the database
//TODO: maybe add restore button

public class edit_activitylist_dialog extends DialogFragment implements View.OnClickListener{
    Button add,cancel,delete;
    BloodsugarDialog_and_Settings communicator;
    ListAdapter adapter;
    ListView lv_edit_activity;
    EditText et_new_activity;

    private String m_Text = "";

    ArrayList<String> Activity_List = new ArrayList<String>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (BloodsugarDialog_and_Settings) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity_List.add("Schlafen");
        Activity_List.add("Essen");
        Activity_List.add("Lernen");
        Activity_List.add("Fernsehen");
        View view= inflater.inflate(R.layout.edit_activity_dialog_layout, null);
        lv_edit_activity = (ListView) view.findViewById(R.id.lv_edit_activity);

        adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_multiple_choice, Activity_List);
        lv_edit_activity.setAdapter(adapter);

        add = (Button) view.findViewById(R.id.add_activity_button);
        cancel = (Button) view.findViewById(R.id.bs_cancel);
        delete = (Button) view.findViewById(R.id.delete_button);
        et_new_activity = (EditText) view.findViewById(R.id.new_activity_edit_text);

        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
        delete.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.add_activity_button)
        {

            String new_activtiy = et_new_activity.getText().toString();
            et_new_activity.setText("");

            Activity_List.add(new_activtiy);
            adapter = null;
            adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_multiple_choice, Activity_List);
            lv_edit_activity.setAdapter(adapter);

        }
        else if(view.getId() == R.id.delete_button)
        {
            int num = lv_edit_activity.getCheckedItemPosition();

            if(num > -1) {
                Activity_List.remove(num);
                adapter = null;
                adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_multiple_choice, Activity_List);
                lv_edit_activity.setAdapter(adapter);
            }
        }
        else if(view.getId() == R.id.bs_cancel)
        {
            dismiss();
        }
    }
}
