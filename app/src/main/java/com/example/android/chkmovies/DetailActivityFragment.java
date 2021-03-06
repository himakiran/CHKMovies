package com.example.android.chkmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by userhk on 03/02/17.
 */

/*
        This fragment shall hold the content_detail layout
     */
public class DetailActivityFragment extends Fragment {
    public static final SharedPrefFavorites sf = new SharedPrefFavorites();
    public static String[] trailers;
    /*
    Below vars hold the values which shall be diaplyed in the content_detail.xmlxml layout
     */
    public String moviesJsonStr;
    public String MovieName;
    public String MoviePoster;
    public String MovieReleaseDate;
    public Integer MovieRunTime;
    public String MovieRating;
    public String MovieReview;
    public String MovieTrailer;
    public ArrayList<String> list_Of_Trailers;
    public int movieID;
    // DetailElementArray is a custom array defined in a separate claas file that hols all the above vars in one structure.
    // The asynctask getMovie ouptuts the result of the backgorund task as a instance of DetailElementArray
    public DetailElementsArray darray;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            /*
            This is how we receive the parameters from the intent passed by another activity.
             */
        Intent intent = getActivity().getIntent();
        movieID = intent.getIntExtra("mov_ID", 0);
        Log.v("CHK-DETAIL-ACT", String.valueOf(movieID));
            /*
            This code runs the asynctask which gets the movie parameters and returns to postexec as a instance of DetailElementsArray
             */
        DetailActivityFragment.GetMovie getmv = new DetailActivityFragment.GetMovie(getContext());
        try {
            darray = getmv.execute(movieID).get();

        } catch (java.util.concurrent.ExecutionException | java.lang.InterruptedException k) {
            Log.e("CHK-IMG-ADP-Fetch", "getmv", k);
        }
        Log.v("CHK-DETAILACTVITY-ASYNC", darray.toString());


        Button button = (Button) rootView.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View rootView) {

                sf.addToFav(getContext(), darray.MoviePoster, movieID);

                Log.v("CHK-FAV-BTN", "BTN-CLICKED");
                Toast.makeText(getActivity().getApplicationContext(), "Movie added to favorites", Toast.LENGTH_LONG).show();


            }
        });

            /*
            Now we populate all the views in content_detail layout with the values returned in darray which is an instance of DetailElementsArray
             */
        ImageView imgView = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso
                .with(getContext())
                .load(darray.MoviePoster)
                .fit()
                .into(imgView);

        TextView txtView = (TextView) rootView.findViewById(R.id.d_textView);
        txtView.setText(darray.MovieName);

        TextView txtView3 = (TextView) rootView.findViewById(R.id.textView3);
        txtView3.setText(darray.MovieReleaseDate);

        TextView txtView7 = (TextView) rootView.findViewById(R.id.textView7);
        txtView7.setText(String.format("%s Mins", String.valueOf(darray.MovieRunTime)));

        TextView txtView8 = (TextView) rootView.findViewById(R.id.textView8);
        txtView8.setText(darray.MovieRating);

        TextView txtView9 = (TextView) rootView.findViewById(R.id.textView9);
        txtView9.setText(darray.MovieReview);

            /*
              We r using this guide to help us play youtube videos in the app

            http://www.androidhive.info/2014/12/how-to-play-youtube-video-in-android-app/
            https://www.sitepoint.com/using-the-youtube-api-to-embed-video-in-an-android-app/
            http://android-er.blogspot.in/2013/06/example-to-use-youtubeplayerfragment-of.html
            http://createdineden.com/blog/post/android-tutorial-how-to-integrate-youtube-videos-into-your-app/
            */


//        YouTubeFragment fragment = (YouTubeFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_youtube);
//        Log.v("CHK-TRAILERS-I",darray.List_Of_Trailers.toString());
//        Log.v("chk-utube-frag",fragment.toString());
//        fragment.setVideoId(darray.List_Of_Trailers.get(0));

        trailers = new String[darray.List_Of_Trailers.size()];
        trailers = darray.List_Of_Trailers.toArray(trailers);


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.youtube_recycler_view);
        recyclerView.setHasFixedSize(true);
        //to use RecycleView, you need a layout manager. default is LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        YoutubeAdapter adapter = new YoutubeAdapter(getContext(), trailers);
        recyclerView.setAdapter(adapter);


        return rootView;
    }


    public class GetMovie extends AsyncTask<Integer, String, DetailElementsArray> {

        // Will contain the raw JSON response as a string.


        private Context fcontext;


        // Will store the context

        private GetMovie(Context c) {

            fcontext = c;
        }

        @Override
        protected DetailElementsArray doInBackground(Integer... params) {

            Integer movieID = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                Uri.Builder movieURL = new Uri.Builder();
                String mUrl = "api.themoviedb.org";
                movieURL.scheme("https")
                        .authority(mUrl)
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(movieID))
                        .appendQueryParameter("api_key", Config.movDBAPIKey);
                URL url = new URL(movieURL.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    moviesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("DetailActivityFragment", "Error ", e);

                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                moviesJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DetailActivityFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                Log.v("CHK-DETAIL-ACTIVITY", moviesJsonStr);
                getMovieParameters(moviesJsonStr, movieID);
            } catch (Exception e) {
                Log.e("CHK-DO-IN-BACKGROUND", e.getMessage(), e);
            }
            DetailActivityFragment.GetMovie.Wrapper w = new DetailActivityFragment.GetMovie.Wrapper();
            w.w_Array = new DetailElementsArray(MovieName, MoviePoster, MovieReleaseDate, MovieRunTime, MovieRating, MovieReview, MovieTrailer, list_Of_Trailers);

            return w.w_Array;


        }

        @Override
        protected void onPostExecute(DetailElementsArray d) {
            //You will get your string array result here .
            // do whatever operations you want to do

            //Log.v("CHK-FETCH", "CHK-postexec");

            Log.v("CHK-DETacvty-ASYNC-post", darray.toString());
            super.onPostExecute(d);


        }
            /*
       This code takes the results of the execution ie array of URLs imageURLArray and movieIDArray  and converts it into
       string arrays and int arrays.

     */

        private void getMovieParameters(String movieStr, Integer mvID) {


            Uri.Builder imgURL, vidURL, trailerURL;
            String mUrl, tUrl;
            String trailerJsonStr;


            try {
                JSONObject movieJson = new JSONObject(movieStr);
                MovieName = movieJson.getString("original_title");
                imgURL = new Uri.Builder();
                mUrl = "image.tmdb.org";
                imgURL.scheme("http")
                        .authority(mUrl)
                        .appendPath("t")
                        .appendPath("p")
                        .appendPath("w185")
                        .appendPath(movieJson.getString("poster_path").substring(1));

                MoviePoster = imgURL.build().toString();
                Log.v("CHK-GET-MOV-PRM", MoviePoster);

                MovieReleaseDate = movieJson.getString("release_date");
                MovieRunTime = movieJson.getInt("runtime");
                MovieRating = movieJson.getString("vote_average");
                MovieReview = movieJson.getString("overview");

                vidURL = new Uri.Builder();
                trailerURL = new Uri.Builder();
                mUrl = "api.themoviedb.org";
                tUrl = "youtu.be";
                vidURL.scheme("https")
                        .authority(mUrl)
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(mvID))
                        .appendPath("videos")
                        .appendQueryParameter("api_key", "c690562b8ea669d80e602902ea80a888");
                MovieTrailer = vidURL.build().toString();
                Log.v("CHK-MOV-TRLR", MovieTrailer);
                    /*
                    Now we use the above url to form a url connection and save the resulting jsonstr
                     */

                HttpURLConnection urlConn = null;
                BufferedReader rdr = null;
                URL url;

                try {
                    url = new URL(MovieTrailer);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setRequestMethod("GET");
                    urlConn.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConn.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        trailerJsonStr = null;
                    }
                    rdr = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = rdr.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        trailerJsonStr = null;
                    }
                    trailerJsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e("CHK-MV-TRAILER", "Error ", e);

                    // If the code didn't successfully get the movie data, there's no point in attempting
                    // to parse it.
                    trailerJsonStr = null;
                } finally {
                    if (urlConn != null) {
                        urlConn.disconnect();
                    }
                    if (rdr != null) {
                        try {
                            rdr.close();
                        } catch (final IOException e) {
                            Log.e("CHK-MV-TRAILER", "Error closing stream", e);
                        }
                    }
                }
                Log.v("CHK-TRLR-JSON", trailerJsonStr);
                JSONObject vidJson = new JSONObject(trailerJsonStr);
                JSONArray list_of_movies = vidJson.getJSONArray("results");
                Log.v("CHK-TRLR-JSON", String.valueOf(list_of_movies.length()));
                JSONObject temp;
                String tempTrailer;
                list_Of_Trailers = new ArrayList<String>();
                for (int i = 0, j = 0; i < list_of_movies.length(); i++) {
                    temp = list_of_movies.getJSONObject(i);

                    if (temp.getString("type").equals("Trailer")) {
                        trailerURL.scheme("https")
                                .authority(tUrl)
                                .path(temp.getString("key"));
                        tempTrailer = trailerURL.build().toString();

                        list_Of_Trailers.add(j++, temp.getString("key"));


                    }

                }
                Log.v("CHK-darray", list_Of_Trailers.get(0));
                Log.v("CHK-darray", list_Of_Trailers.get(1));

            } catch (org.json.JSONException j) {
                Log.e("CHK-MV-TRAILER", "URL-ERROR", j);
            }


        }

        /*
        We are using the below warpper class so that doInBackground can send multiple arrays to onPostExecute
        http://stackoverflow.com/questions/11833978/asynctask-pass-two-or-more-values-from-doinbackground-to-onpostexecute
         */
        public class Wrapper {
            private DetailElementsArray w_Array;

        }


    }


}