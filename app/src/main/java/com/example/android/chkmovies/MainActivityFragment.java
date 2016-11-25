package com.example.android.chkmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import static com.example.android.chkmovies.R.layout.fragment_main;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // first we get the rootView

        View rootView = inflater.inflate(fragment_main, container, false);

        // The we use the rootview to get the gridview

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

        // we set the image adapter in the gridview and then return the rootview

        gridview.setAdapter(new ImageAdapter(this.getActivity()));

        return rootView;
    }
}
