package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DailyRoutineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyRoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyRoutineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_LIST = "list";
    private ArrayList<String[]> list2 = new ArrayList<String[]>();
    private ArrayList<DailyRoutineView> items = new ArrayList<DailyRoutineView>();

    public static final String TAG = DailyRoutineFragment.class.getSimpleName();


    // TODO: Rename and change types of parameters
    private ArrayList<String> arglist;


    private OnFragmentInteractionListener mListener;
    private DailyRoutineView dailyRoutineView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DailyRoutineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyRoutineFragment newInstance(ArrayList<String> list) {
        DailyRoutineFragment fragment = new DailyRoutineFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_LIST, list);
        fragment.setArguments(args);

        return fragment;
    }

    public DailyRoutineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arglist = getArguments().getStringArrayList(ARG_LIST);
        }

        AppCompatActivity aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.menu_item_daily_routine);

        list2.add(new String[]{"1", "0:00", "9:14"});
        list2.add(new String[]{"2","9:14","9:53"});
        list2.add(new String[]{"13","9:53","13:07"});
        list2.add(new String[]{"2","13:07","13:22"});
        list2.add(new String[]{"13","13:22","15:35"});
        list2.add(new String[]{"10","15:35","15:38"});
        list2.add(new String[]{"13","15:38","21:53"});
        list2.add(new String[]{"5","21:53","22:22"});
        list2.add(new String[]{"2","22:22","22:51"});
        list2.add(new String[]{"1","22:51","23:59"});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflaterView = inflater.inflate(R.layout.fragment_daily_routine, container, false);

        LinearLayout linearLayout = (LinearLayout) inflaterView.findViewById(R.id.layout_daily_routine);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.topMargin = getpx(8);


        for(int i=0; i<list2.size(); i++){
            DailyRoutineView drv = new DailyRoutineView(getActivity(),Integer.valueOf(list2.get(i)[0]),0,list2.get(i)[1], list2.get(i)[2]);
            linearLayout.addView(drv);
            drv.setLayoutParams(params);
            drv.getLayoutParams().height = drv.getTotalHeight();
            items.add(drv);
        }
        return inflaterView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }**/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * returns the px value for dp
     * @param dp
     * @return
     */
    public int getpx(int dp){

        return (int)(dp*getResources().getDisplayMetrics().density);
    }

    /**
     * return the dp value for px
     * @param px
     * @return
     */
    public int getdp(int px){

        return (int)(px/getResources().getDisplayMetrics().density);
    }

}
