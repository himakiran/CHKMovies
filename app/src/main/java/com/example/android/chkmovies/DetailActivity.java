package com.example.android.chkmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);


    }

    /*
        This fragment shall hold the detail_main layout
     */
    public static class PlaceholderFragment extends Fragment {
        public String moviesJsonStr;
        public String MovieName;
        public String MoviePoster;
        public String MovieReleaseDate;
        public Integer MovieRunTime;
        public String MovieRating;
        public String MovieReview;
        public String MovieTrailer;

        public DetailElementsArray darray;



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.detail_activity, container, false);
            /*
            This is how we receive the parameters from the intent passed by another activity.
             */
            Intent intent = getActivity().getIntent();
            int movieID = intent.getIntExtra("mov_ID", 0);
            Log.v("CHK-DETAIL-ACT", String.valueOf(movieID));
            /*
            This code runs the asynctask which gets the movie parameters
             */
            GetMovie getmv = new GetMovie(getContext());
            try {
                darray = getmv.execute(movieID).get();

            } catch (java.util.concurrent.ExecutionException | java.lang.InterruptedException k) {
                Log.e("CHK-IMG-ADP-Fetch", "getmv", k);
            }
            Log.v("CHK-DETAILACTVITY-ASYNC", darray.toString());
            /*
            Now we populate the view
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
            //txtView7.setText(darray.MovieRunTime);

            TextView txtView8 = (TextView) rootView.findViewById(R.id.textView8);
            txtView8.setText(darray.MovieRating);

            TextView txtView9 = (TextView) rootView.findViewById(R.id.textView9);
            txtView9.setText(darray.MovieReview);

            VideoView vidView = (VideoView) rootView.findViewById(R.id.videoView);
            vidView.setVideoURI(Uri.parse(darray.MovieTrailer));




            return rootView;
        }

        public class GetMovie extends AsyncTask<Integer, String, DetailElementsArray> {

            // Will contain the raw JSON response as a string.


            public Context fcontext;


            // Will store the context

            public GetMovie(Context c) {

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
                            .appendQueryParameter("api_key", "c690562b8ea669d80e602902ea80a888");
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
                    Log.e("PlaceholderFragment", "Error ", e);

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
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }
                try {
                    Log.v("CHK-DETAIL-ACTVIVTY", moviesJsonStr);
                    getMovieParameters(moviesJsonStr);
                } catch (Exception e) {
                    Log.e("CHK-DO-IN-BACKGROUND", "CHK-GET-IMG-URL", e);
                }
                GetMovie.Wrapper w = new GetMovie.Wrapper();
                w.w_Array = new DetailElementsArray(MovieName, MoviePoster, MovieReleaseDate, MovieRunTime, MovieRating, MovieReview, MovieTrailer);

                return w.w_Array;


            }

            @Override
            protected void onPostExecute(DetailElementsArray d) {
                //You will get your string array result here .
                // do whatever operations you want to do

                //Log.v("CHK-FETCH", "CHK-postexec");

                Log.v("CHK-DETacvty-ASYNC-post", darray.toString());
                super.onPostExecute(d);

                return;
            }
            /*
       This code takes the results of the execution ie array of URLs imageURLArray and movieIDArray  and converts it into
       string arrays and int arrays.

     */

            public void getMovieParameters(String movieStr) {

                JSONObject movieObject;
                String imgPath;
                URL imgUrl;
                Uri.Builder imgURL;
                String mUrl;

                int movID;


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
                            .appendPath(movieJson.getString("poster_path"));

                    MoviePoster = imgURL.build().toString();

                    MovieReleaseDate = movieJson.getString("release_date");
                    MovieRunTime = movieJson.getInt("runtime");
                    MovieRating = movieJson.getString("vote_average");
                    MovieReview = movieJson.getString("overview");
                    MovieTrailer = movieJson.getString("video");


                } catch (JSONException j) {
                    Log.e("CHK-JSON-ISSUE", "json-object", j);


                }

            }

            /*
            We are using the below warpper class so that doInBackground can send multiple arrays to onPostExecute
            http://stackoverflow.com/questions/11833978/asynctask-pass-two-or-more-values-from-doinbackground-to-onpostexecute
             */
            public class Wrapper {
                public DetailElementsArray w_Array;
                /*public String w_MovieName;
                public String w_MoviePoster;
                public String w_MovieReleaseDate;
                public int w_MovieRunTime;
                public String w_MovieRating;
                public String w_MovieReview;
                public String w_MovieTrailer;*/
            }


        }


    }
}


