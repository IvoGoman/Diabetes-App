package uni.mannheim.teamproject.diabetesplaner.StatisticsFragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uni.mannheim.teamproject.diabetesplaner.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppCompatActivity aca;

    private OnFragmentInteractionListener mListener;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        aca = (AppCompatActivity) getActivity();
//        final ActionBar actionBar = aca.getSupportActionBar();
        aca.getSupportActionBar().setTitle(R.string.menu_item_statistics);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterView = inflater.inflate(R.layout.fragment_statistics, container, false);

        //setup tablayout
        viewPager = (ViewPager) inflaterView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) inflaterView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Inflate the layout for this fragment
        return inflaterView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.statistics, menu);

    }

    /**
     * Set the Add Icon in the Action Bar to invisible.
     *
     * @param menu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            MenuItem addItemRoutine = menu.findItem(R.id.add_icon_action_bar_routine);
            addItemRoutine.setVisible(false);
//           menu.removeItem(R.id.add_icon_action_bar);
        }
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Update the TimeWindow of the Chart that is currently displayed to the user
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        get the currently active fragment that is shown to the user
        Fragment active = null;
        FragmentManager fragmentManager = aca.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.getUserVisibleHint())
                active = fragment;
        }
//        update the chart of the fragment based on the TimeWindow selected
        if (!(active == null)) {
            switch (id) {
                case R.id.statistic_day:
//                    TODO:Update the charts by calling them by their ID
                    try{
                        RingChartFragment ringFragment = (RingChartFragment) active;
                        ringFragment.updateChart("DAY");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        LineChartFragment lineFragment = (LineChartFragment) active;
                        lineFragment.updateChart("DAY");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.statistic_week:
//                    TODO: Update the charts by calling them by their ID
                    try{
                        RingChartFragment ringFragment = (RingChartFragment) active;
                        ringFragment.updateChart("WEEK");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        LineChartFragment lineFragment = (LineChartFragment) active;
                        lineFragment.updateChart("WEEK");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.statistic_month:
//                    TODO:Update the charts by calling them by their ID
                    try{
                        RingChartFragment ringFragment = (RingChartFragment) active;
                        ringFragment.updateChart("MONTH");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        LineChartFragment lineFragment = (LineChartFragment) active;
                        lineFragment.updateChart("MONTH");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * setUp ViewPager with a RingChartFragment and a LineChartFragment
     *
     * @param viewPager a viewPager object
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(aca.getSupportFragmentManager());
        adapter.addFragment(new RingChartFragment(), "Activities");
        adapter.addFragment(new LineChartFragment(), "Glucose & Insulin");
        viewPager.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * @Override public void onAttach(Activity activity) {
     * super.onAttach(activity);
     * try {
     * mListener = (OnFragmentInteractionListener) activity;
     * } catch (ClassCastException e) {
     * throw new ClassCastException(activity.toString()
     * + " must implement OnFragmentInteractionListener");
     * }
     * }
     **/

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
        void onFragmentInteraction(Uri uri);
    }

    /**
     * class needed for the TapLayout
     */
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
