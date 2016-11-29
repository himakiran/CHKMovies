package com.example.android.chkmovies;

/**
 * Created by userhk on 29/11/16.
 */

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

public class DetailElementsArrayAdapter extends ArrayAdapter<DetailElementsArray> {

    private static final String LOG_TAG = "CHK-" + DetailElementsArrayAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param detailElements of DetailElementsArray objects to display in a list
     */
    public DetailElementsArrayAdapter(Activity context, List<DetailElementsArray> detailElements) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, detailElements);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        DetailElementsArray deArray = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_main, parent, false);
        }

        ImageView imgView = (ImageView) convertView.findViewById(R.id.imageView);
        imgView.setImageURI(Uri.parse(deArray.MoviePoster));

        TextView txtView = (TextView) convertView.findViewById(R.id.d_textView);
        txtView.setText(deArray.MovieName);

        TextView txtView3 = (TextView) convertView.findViewById(R.id.textView3);
        txtView3.setText(deArray.MovieReleaseDate);

        TextView txtView7 = (TextView) convertView.findViewById(R.id.textView7);
        txtView7.setText(deArray.MovieRunTime);

        TextView txtView8 = (TextView) convertView.findViewById(R.id.textView8);
        txtView8.setText(deArray.MovieRating);

        TextView txtView9 = (TextView) convertView.findViewById(R.id.textView9);
        txtView9.setText(deArray.MovieReview);

        VideoView vidView = (VideoView) convertView.findViewById(R.id.videoView);
        vidView.setVideoURI(Uri.parse(deArray.MovieTrailer));

        return convertView;
    }
}