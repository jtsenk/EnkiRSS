package com.jtsenkbeil.enki.enkirss.feature.frags;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.activity.AudioActivity;
import com.jtsenkbeil.enki.enkirss.feature.activity.ShowEpisodesActivity;
import com.jtsenkbeil.enki.enkirss.feature.adapt.DownloadsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.adapt.ShowsListAdapter;
import com.jtsenkbeil.enki.enkirss.feature.audio.AudioOb;
import com.jtsenkbeil.enki.enkirss.feature.audio.BaseAudioOb;
import com.jtsenkbeil.enki.enkirss.feature.audio.MusicController;
import com.jtsenkbeil.enki.enkirss.feature.db.Ki;
import com.jtsenkbeil.enki.enkirss.feature.dialog.DeleteDownloadDialog;
import com.jtsenkbeil.enki.enkirss.feature.util.Episode;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DownloadsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView lv;
    private Intent intent;
    private View view;
    private ArrayList<String> dList;
    private ArrayList<String> shList;
    private final Context context;
    private Ki ki;
    private Cursor curs;
    private Bundle bundle;
    private final MusicController controller;
    public static ArrayList<BaseAudioOb> contentList;

    private ShowsFragment.OnFragmentInteractionListener mListener;

    public DownloadsFragment() {

        context = getContext();
        intent = null;
        dList = new ArrayList<>();
        shList = new ArrayList<>();
        bundle = null;
        contentList = new ArrayList<>();
        initInfo();

        ki = new Ki();
        curs = ki.getTable("tbl_dl");
        while (curs.moveToNext()) {
            shList.add(curs.getString(curs.getColumnIndex("show")));
            dList.add(curs.getString(curs.getColumnIndex("title")));
        }
        ki.closeDown();

        controller = MusicController.getInstance(MainActivity.mainContext);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadsFragment newInstance(String param1, String param2) {
        DownloadsFragment fragment = new DownloadsFragment();
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
        view = inflater.inflate(R.layout.fragment_downloads, container, false);
        lv = view.findViewById(R.id.downloads_listv);

        //set up the list adapter, which contains the GestureDetector for onTouch events
        final DownloadsListAdapter adapter = new DownloadsListAdapter(this.getContext(), dList, shList, controller);
        lv.setAdapter(adapter);

        //onItenClick is now handled by the adapter through the GestureDetector

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < dList.size()) {
                    //old strategy -- WAY too complicated on both ends :JTS
                    ////(for new strategy see initInfo() below)
                    //Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                    //launch this episode in the audio player
                    //bundle = new Bundle();
                    //ki = new Ki();
                    //pass episode info to player in the bundle (in intent)
                    //bundle.putString("filepath", ki.getEpisodePath(dList.get(position), shList.get(position)));
                    //close ki to prevent leak
                    //ki.closeDown();

                    //bundle.putString("title",dList.get(position));
                    //bundle.putString("show",shList.get(position));
                    //intent = new Intent(getActivity(), AudioActivity.class);
                    //intent.putExtra("bundle",bundle);

                    //below from the instructor's PlayListView.java
                    controller.setPlayList(DownloadsFragment.contentList);
                    if(position == controller.position){
                        if(controller.isPlaying){
                            controller.pause();
                            intent = new Intent(getActivity(), AudioActivity.class);
                            startActivity(intent);
                        }else{
                            controller.play();
                            intent = new Intent(getActivity(), AudioActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        controller.position = position;
                        intent = new Intent(getActivity(), AudioActivity.class);
                        startActivity(intent);
                        controller.play();
                    }
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //put episode info into a Bundle for the dialog :JTS
                bundle = new Bundle();
                ki = new Ki();
                curs = ki.getEpisodeRow(dList.get(position),shList.get(position));
                curs.moveToFirst();
                bundle.putString("showName",shList.get(position));
                bundle.putString("epTitle", dList.get(position));
                bundle.putString("epDesc", curs.getString(curs.getColumnIndex("description")));
                bundle.putLong("epSize", curs.getLong(curs.getColumnIndex("size")));
                bundle.putString("epPath",curs.getString(curs.getColumnIndex("path")));
                bundle.putInt("epID", curs.getInt(curs.getColumnIndex("id")));
                ki.closeDown();

                DeleteDownloadDialog dld = new DeleteDownloadDialog(getActivity(), bundle, new DeleteDownloadDialog.DeleteDownloadDialogEventListener() {
                    @Override
                    public void onDeleteClicked() {
                        Utils.logD("DLsFragment::DDLDialogEventListener","Hears onDeleteClicked");
                        //rebuild the list for the view
                        shList.clear();
                        dList.clear();
                        ki = new Ki();
                        curs = ki.getTable("tbl_dl");
                        while (curs.moveToNext()) {
                            shList.add(curs.getString(curs.getColumnIndex("show")));
                            dList.add(curs.getString(curs.getColumnIndex("title")));
                        }
                        ki.closeDown();
                        Utils.logD("DLsFragment::DDLDialogEventListener","Calling adapter.reset");
                        adapter.resetList(dList, shList);
                    }
                    @Override
                    public void onCancelClicked() {
                        Utils.logD("DLsFragment::DDLDialogEventListener","Hears onCancelClicked");
                    }
                });
                dld.show();
                return true;
            }
        });

        return view;
    }//end OnCreateView

    private void initInfo() {

        //new strategy: read everything from the DL table in the DB
        ki = new Ki();
        curs = ki.getTable("tbl_dl");
        File file;
        AudioOb a;
        while (curs.moveToNext()) {

            file = new File(curs.getString(curs.getColumnIndex("path")));
            a = new AudioOb();
            a.setURL(Uri.fromFile(file).toString());
            a.setName(curs.getString(curs.getColumnIndex("title")));
            a.setShow(curs.getString(curs.getColumnIndex("show")));
            Utils.logD("DownloadFragment::initInfo", "url= " + Uri.fromFile(file).toString());
            Utils.logD("DownloadFragment::initInfo", "does file exist? " + file.exists());
            contentList.add(a);
        }
        ki.closeDown();
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
