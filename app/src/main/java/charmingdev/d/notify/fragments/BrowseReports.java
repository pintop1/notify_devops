package charmingdev.d.notify.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import charmingdev.d.notify.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseReports extends Fragment {


    public BrowseReports() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_reports, container, false);
    }

}
