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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

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


    /*
        This fragment shall hold the detail_main layout
     */
    public static class PlaceholderFragment extends Fragment {
        public String moviesJsonStr;
        public String MovieName;
        public String MoviePoster;
        public String MovieReleaseDate;
        public int MovieRunTime;
        public String MovieRating;
        public String MovieReview;
        public String MovieTrailer;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.detail_main, container, false);
            /*
            This is how we receive the parameters from the intent passed by another activity.
             */
            Intent intent = getActivity().getIntent();
            int movieID = intent.getIntExtra("mov_ID", 0);
            /*
            And this is how we set the text of any textview
             */

            TextView textView = (TextView) rootView.findViewById(R.id.d_textView);
            textView.setText(MovieName);
            ImageView imgView = (ImageView) rootView.findViewById(R.id.imageView);
            /*Picasso
                    .with(getContext())
                    .load(MoviePoster.toString())
                    .fit()
                    .into(imgView);*/
            textView = (TextView) rootView.findViewById(R.id.textView3);
            textView.setText(MovieReleaseDate);
            textView = (TextView) rootView.findViewById(R.id.textView7);
           /* textView.setText(MovieRunTime); */
            textView = (TextView) rootView.findViewById(R.id.textView8);
            textView.setText(MovieRating);
            Button btn = (Button) rootView.findViewById(R.id.button2);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // adds the movie to a favorite list that is locally stored.
                }
            });
            textView = (TextView) rootView.findViewById(R.id.textView9);
            textView.setText(MovieReview);
            VideoView vview = (VideoView) rootView.findViewById(R.id.videoView);
            // vview.setVideoURI(Uri.parse(MovieTrailer));
            return rootView;
        }

        public class GetMovie extends AsyncTask<Integer, String, GetMovie.Wrapper> {

            // Will contain the raw JSON response as a string.


            public Context fcontext;


            // Will store the context

            public GetMovie(Context c) {

                fcontext = c;
            }

            @Override
            protected Wrapper doInBackground(Integer... params) {

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
                    getMovieParameters(moviesJsonStr);
                } catch (Exception e) {
                    Log.e("CHK-DO-IN-BACKGROUND", "CHK-GET-IMG-URL", e);
                }
                GetMovie.Wrapper w = new GetMovie.Wrapper();
                w.w_MovieName = MovieName;
                w.w_MoviePoster = MoviePoster;
                w.w_MovieReleaseDate = MovieReleaseDate;
                w.w_MovieRunTime = MovieRunTime;
                w.w_MovieRating = MovieRating;
                w.w_MovieReview = MovieReview;
                w.w_MovieTrailer = MovieTrailer;

                return w;


            }

            @Override
            protected void onPostExecute(GetMovie.Wrapper w) {
                //You will get your string array result here .
                // do whatever operations you want to do
                super.onPostExecute(w);
                //Log.v("CHK-FETCH", "CHK-postexec");


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
            /* This code receives the moviesJsonStr and returns an array of image Urls.
                    */

            /*
            We are using the below warpper class so that doInBackground can send multiple arrays to onPostExecute
            http://stackoverflow.com/questions/11833978/asynctask-pass-two-or-more-values-from-doinbackground-to-onpostexecute
             */
            public class Wrapper {
                public String w_MovieName;
                public String w_MoviePoster;
                public String w_MovieReleaseDate;
                public int w_MovieRunTime;
                public String w_MovieRating;
                public String w_MovieReview;
                public String w_MovieTrailer;
            }


        }


    }
}


