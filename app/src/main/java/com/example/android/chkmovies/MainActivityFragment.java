package com.example.android.chkmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static com.example.android.chkmovies.DetailActivityFragment.sf;
import static com.example.android.chkmovies.R.layout.fragment_main;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // IMG is the imageadpater that gets executed in postexecute of fetchMovie.
    public ImageAdapter IMG;

    public String[] imageUrlArray;
    public int[] movieIDArray;
    /*
        Stores the value of the selected menu option ie popular,top_rated or favorite
     */
    public String selected_option = "popular";

    // stores the value of the toggle setting
    public Boolean popular = true;
    public Boolean top_rated = false;
    public Boolean favorite = false;

    public GridView gridview;

    public MainActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            popular = savedInstanceState.getBoolean("popular");
            top_rated = savedInstanceState.getBoolean("top_rated");
            favorite = savedInstanceState.getBoolean("favorite");
            selected_option = savedInstanceState.getString("selected-option");
        }
        setHasOptionsMenu(true);


    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean("popular", popular);
        state.putBoolean("top_rated", top_rated);
        state.putBoolean("favorite", favorite);
        state.putString("selected-option", selected_option);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);


    }

    public void updateView(MenuItem item) {
        Log.v("CHK-UPDT-VIEW", item.toString());
        item.setChecked(true);
        IMG.notifyDataSetChanged();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular:
                popular = true;
                top_rated = false;
                favorite = false;
                selected_option = "popular";
                updateView(item);

                Log.v("CHK-ONOPTION-POPLR", item.toString());

                return true;
            case R.id.top_rated:
                popular = false;
                top_rated = true;
                favorite = false;
                selected_option = "top_rated";
                updateView(item);
                Log.v("CHK-ONOPTION-RATED", item.toString());

                return true;

            case R.id.favorite:
                popular = false;
                top_rated = false;
                favorite = true;
                selected_option = "favorite";
                updateView(item);
                Log.v("CHK-ONOPTION-RATED", item.toString());

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (selected_option.equals("popular"))
            menu.findItem(R.id.popular).setChecked(true);
        else if (selected_option.equals("top_rated"))
            menu.findItem(R.id.top_rated).setChecked(true);
        else
            menu.findItem(R.id.favorite).setChecked(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // first we get the rootView

        View rootView = inflater.inflate(fragment_main, container, false);


        // The we use the rootview to get the gridview

        gridview = (GridView) rootView.findViewById(R.id.gridview);

        // we set the image adapter in the gridview and then return the rootview
        // the image adapter is initialized with the movieStrs in the postexecute of fetch task.
        //Log.v("CHK-MAIN-ACTVITY-FRAGT",fetch.imgAdapter.toString());


        IMG = new ImageAdapter(getContext());
        if (savedInstanceState != null) {
            popular = savedInstanceState.getBoolean("popular");
            top_rated = savedInstanceState.getBoolean("top_rated");
            favorite = savedInstanceState.getBoolean("favorite");

            IMG.notifyDataSetChanged();

        }

        gridview.setAdapter(IMG);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                /*The code below illustrates making a new intent, declaring the second activity to open
                ie DetailActivity and then pass a string parameter ie forecast. which will be used
                by the onCreateView() in detailActivity to set the weataher string.
                It also passes geo which shall be used by the if (id == R.id.detail_see_map)
                function in detailActivity to set the Uri.
                 */
                Log.v("CHK-MAINACTFRAG", String.valueOf(movieIDArray[position]));
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("mov_ID", movieIDArray[position]);

                startActivity(intent);
            }
        });

        return rootView;

    }


    public class FetchMovie extends AsyncTask<String, String, FetchMovie.Wrapper> {

        // Will contain the raw JSON response as a string.
        private String moviesJsonStr;

        private Context fcontext;


        // Will store the context

        public FetchMovie(Context c) {

            fcontext = c;
        }

        @Override
        protected Wrapper doInBackground(String... params) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

        /*

        here we define that doInBackground takes one string parameter moviesType which will be either "popular"
        or "top_rated"
        */

            String moviesType = params[0];

            /*
                We will run the background task only if interent connectivity exists
             */

            try {
                        /*
                            Popular Movie URL : https://api.themoviedb.org/3/movie/popular?api_key=c690562b8ea669d80e602902ea80a888

                            Top Rated Movie URL: https://api.themoviedb.org/3/movie/top_rated?api_key=c690562b8ea669d80e602902ea80a888
                         */
                Uri.Builder movieURL = new Uri.Builder();
                String mUrl = "api.themoviedb.org";
                movieURL.scheme("https")
                        .authority(mUrl)
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(moviesType)
                        .appendQueryParameter("api_key", Config.movDBAPIKey);

                // Create the request to OpenWeatherMap, and open the connection
                URL url = new URL(movieURL.build().toString());
                Log.v("CHK-Movie-URL",url.toString());

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
                    Log.v("chk-buffer","buffer length is zero");
                }
                moviesJsonStr = buffer.toString();
                Log.v("CHK-FETCH", moviesJsonStr);
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);
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
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
                    }


            try {
                Log.v("CHK-MOV-JSON-STR", moviesJsonStr);
                getImageURLsMovieIDs(moviesJsonStr);
            } catch (Exception e) {
                Log.e("CHK-DO-IN-BACKGROUND", "CHK-GET-IMG-URL", e);
            }
            Wrapper w = new Wrapper();
            w.w_imageUrlArray = imageUrlArray;
            w.w_movieIDArray = movieIDArray;
            return w;


        }

        @Override
        protected void onPostExecute(Wrapper w) {
            //You will get your string array result here .
            // do whatever operations you want to do
            super.onPostExecute(w);
            //Log.v("CHK-FETCH", "CHK-postexec");


        }
    /*
       This code takes the results of the execution ie array of URLs imageURLArray and movieIDArray  and converts it into
       string arrays and int arrays.

     */

        private void getImageURLsMovieIDs(String movieStr) {

            JSONObject movieObject;
            String imgPath;
            URL imgUrl;
            Uri.Builder imgURL;
            String mUrl;
            URL[] imageURLs;

            int movID;


            try {
                JSONObject movieJson = new JSONObject(movieStr);
                JSONArray moviesJsonArray = movieJson.getJSONArray("results");
                imageURLs = new URL[moviesJsonArray.length()];

                imageUrlArray = new String[moviesJsonArray.length()];
                movieIDArray = new int[moviesJsonArray.length()];
                for (int i = 0; i < moviesJsonArray.length(); i++) {
                    movieObject = moviesJsonArray.getJSONObject(i);
                    /*
                     .substring(1) is required to get rid of "\" in the poster path
                     "\/9HE9xiNMEFJnCzndlkWD7oPfAOx.jpg"
                     */
                    imgPath = movieObject.getString("poster_path").substring(1);
                    movID = movieObject.getInt("id");

                    imgURL = new Uri.Builder();
                    mUrl = "image.tmdb.org";
                    imgURL.scheme("http")
                            .authority(mUrl)
                            .appendPath("t")
                            .appendPath("p")
                            .appendPath("w185")
                            .appendPath(imgPath);
                    try {
                        imgUrl = new URL(imgURL.build().toString());
                    } catch (java.net.MalformedURLException e) {
                        Log.e("CHK-MALFORM-URL", "IMAGE-URL", e);
                        imgUrl = null;
                    }
                    imageURLs[i] = imgUrl;
                    imageUrlArray[i] = imgURL.toString();
                    movieIDArray[i] = movID;


                }


            } catch (JSONException j) {
                Log.e("CHK-JSON-ISSUE", "json-object", j);


            }

        }

        /*
         This code receives the moviesJsonStr and returns an array of image Urls.
         */

        /*
        We are using the below warpper class so that doInBackground can send multiple arrays to onPostExecute
        http://stackoverflow.com/questions/11833978/asynctask-pass-two-or-more-values-from-doinbackground-to-onpostexecute
         */
        public class Wrapper {
            private int[] w_movieIDArray;
            private String[] w_imageUrlArray;
        }


    }


    public class ImageAdapter extends BaseAdapter {
        // Keep all Images in array
        private String[] mThumbIds;
        private Context mContext;
        private LayoutInflater inflater;
        private int[] movIDArray;


        // Constructor
        public ImageAdapter(Context c) {
            mContext = c;
            inflater = LayoutInflater.from(c);


            FetchMovie fetch = new FetchMovie(c);

            try {
                if (MainActivityFragment.this.popular) {
                    mThumbIds = fetch.execute("popular").get().w_imageUrlArray;

                    //movIDArray = fetch.execute("popular").get().w_movieIDArray;
                    Log.v("CHK-IMG-ADPTR", "this-poplr-true");
                } else if (MainActivityFragment.this.top_rated) {
                    mThumbIds = fetch.execute("top_rated").get().w_imageUrlArray;

                    //movIDArray = fetch.execute("top_rated").get().w_movieIDArray;
                    Log.v("CHK-IMG-ADPTR", "this-toprated-true");
                } else {
                    mThumbIds = sf.getImgUrlArray();
                    Log.v("CHK-MTHUMSIDS", Arrays.toString(mThumbIds));
                    if (mThumbIds[0] == null) {
                        Toast.makeText(getActivity().getApplicationContext(), "No Favorites added yet !!", Toast.LENGTH_LONG).show();
                    }

                    Log.v("CHK-IMG-ADPTR", "this-favorite-true");
                }

            } catch (java.util.concurrent.ExecutionException | java.lang.InterruptedException k) {
                Log.e("CHK-IMG-ADP-Fetch", "fetchtask", k);
            }




        }


        /*
        we r not using the below getCount() , getItem() and getItemId()
         */
        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;

        }


        @Override
        public void notifyDataSetChanged() // Create this function in your adapter class
        {

            IMG = new ImageAdapter(getContext());

            gridview.setAdapter(IMG);
            super.notifyDataSetChanged();
        }

        // create a new ImageView for each item referenced by the Adapter
    /*
     Reference : http://www.androidtrainee.com/picasso-image-loader-with-gridview-for-android/
     */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(mContext);

                //imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            /*
            The below code allows you to set the dimens in dimens.xml and then use it here
            https://myskillset.wordpress.com/2013/07/05/android-setting-size-of-grid-view-items-according-to-screensize/
             */
                //imageView.setLayoutParams(new GridView.LayoutParams((int)mContext.getResources().getDimension(R.dimen.width), (int)mContext.getResources().getDimension(R.dimen.height)));
            /*
            The below code solves the layout problem to show the images in equalsized tiles..
            http://stackoverflow.com/questions/23204755/how-to-set-image-size-in-grid-view
             */
                imageView.setLayoutParams(new RelativeLayout.LayoutParams((int) mContext.getResources().getDimension(R.dimen.width), (int) mContext.getResources().getDimension(R.dimen.height)));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0);
            } else {
                imageView = (ImageView) convertView;
            }
            //Log.v("CHK-IMG-ADAPTER", "GET-VIEW");
            Picasso
                    .with(mContext)
                    .load(mThumbIds[position])
                    .fit()
                    .into(imageView);


            return imageView;
        }
    }


}


