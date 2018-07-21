package com.jtsenkbeil.enki.enkirss.feature.frags;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.activity.ShowEpisodesActivity;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ShowsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ShowsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final ArrayList<String> showList;
    private final Context context;
    private String mParam1;
    private String mParam2;
    private ListView lv;
    private Intent intent;
    private Ki ki;
    private Cursor curs;
    private final String showTbl = "tbl_shows";

    private OnFragmentInteractionListener mListener;
    private View v;

    public ShowsFragment() {

        context = getContext();
        intent = null;
        showList = new ArrayList<>();
        curs = null;
        ki = new Ki();

        //get shows to add
        //showList.add("How Did This Get Made?");
        //showList.add("Stuff You Should Know");
        curs = ki.getTable(showTbl);
        while (curs.moveToNext()) {
            showList.add(curs.getString(curs.getColumnIndex("name")));
            Utils.logD("ShowsFragment::Cursor", curs.getString(curs.getColumnIndex("id")) + "    " + curs.getString(curs.getColumnIndex("name")) + "    " + curs.getString(curs.getColumnIndex("xml_link")) );
        }

        ki.closeDown();

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowsFragment newInstance(String param1, String param2) {
        ShowsFragment fragment = new ShowsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_shows, container, false);
        lv = v.findViewById(R.id.shows_listv);
        ShowsListAdapter adapter = new ShowsListAdapter(this.getContext(), showList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < showList.size()) {
                    //got to ShowEpisodesActivity
                    intent = new Intent(getActivity(), ShowEpisodesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("showName", showList.get(position));
                    intent.putExtra("showB",bundle);
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * EDIT: Meh. :JTS
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
