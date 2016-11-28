package com.example.android.chkmovies;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

import static com.example.android.chkmovies.R.layout.fragment_main;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // IMG is the imageadpater that gets executed in postexecute of fetchMovie.
    public ImageAdapter IMG;
    // stores the value of the toggle setting
    public Boolean popular = true;

    public GridView gridview;

    public MainActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);


    }

    public void updateView(MenuItem item) {
        Log.v("CHK-UPDT-VIEW", item.toString());

        IMG.notifyDataSetChanged();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular:
                popular = true;
                item.setChecked(true);
                updateView(item);
                Log.v("CHK-ONOPTION-POPLR", item.toString());

                return true;
            case R.id.top_rated:
                popular = false;
                item.setChecked(true);
                updateView(item);
                Log.v("CHK-ONOPTION-RATED", item.toString());

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        gridview.setAdapter(IMG);



        return rootView;
    }


    public class FetchMovie extends AsyncTask<String, String, String[]> {

        // Will contain the raw JSON response as a string.
        public String moviesJsonStr;
        public String[] imageUrlArray;


        // Will store the context

        public Context fcontext;

        public FetchMovie(Context c) {

            fcontext = c;
        }


        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

        /*

        here we define that doInBackground takes one string parameter moviesType which will be either "popular"
        or "top_rated"
        */

            String moviesType = params[0];


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
                        .appendQueryParameter("api_key", "c690562b8ea669d80e602902ea80a888");

                // Create the request to OpenWeatherMap, and open the connection
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
                getImageURLs(moviesJsonStr);
            } catch (Exception e) {
                Log.e("CHK-DO-IN-BACKGROUND", "CHK-GET-IMG-URL", e);
            }
            return imageUrlArray;


        }
    /*
       This code takes the results of the execution ie array of URLs imageURLs and converts it into
       string arrays.

     */

        @Override
        protected void onPostExecute(String[] strings) {
            //You will get your string array result here .
            // do whatever operations you want to do
            super.onPostExecute(strings);
            //Log.v("CHK-FETCH", "CHK-postexec");

            imageUrlArray = new String[strings.length];
            for (int i = 0; i < strings.length; i++) {
                imageUrlArray[i] = strings[i];
                //Log.v("CHK-ON-POST-EXEC", imageUrlArray[i]);
            }

            return;
        }

        /*
         This code receives the moviesJsonStr and returns an array of image Urls.
         */

        public void getImageURLs(String movieStr) {

            JSONObject movieObject;
            String imgPath;
            URL imgUrl;
            Uri.Builder imgURL;
            String mUrl;
            URL[] imageURLs;


            try {
                JSONObject movieJson = new JSONObject(movieStr);
                JSONArray moviesJsonArray = movieJson.getJSONArray("results");
                imageURLs = new URL[moviesJsonArray.length()];
                imageUrlArray = new String[moviesJsonArray.length()];
                for (int i = 0; i < moviesJsonArray.length(); i++) {
                    movieObject = moviesJsonArray.getJSONObject(i);
                    /*
                     .substring(2) is required to get rid of "/\" in the poster path
                     "\/9HE9xiNMEFJnCzndlkWD7oPfAOx.jpg"
                     */
                    imgPath = movieObject.getString("poster_path").substring(1);

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


                }


            } catch (JSONException j) {
                Log.e("CHK-JSON-ISSUE", "json-object", j);


            }

        }

    }


    public class ImageAdapter extends BaseAdapter {
        // Keep all Images in array
        private String[] mThumbIds;
        private Context mContext;
        private LayoutInflater inflater;



        // Constructor
        public ImageAdapter(Context c) {
            mContext = c;
            inflater = LayoutInflater.from(c);
       /* mThumbIds = new String[imageArray.length];
        for(int i=0; i < imageArray.length; i++){
            mThumbIds[i] = imageArray[i];
            Log.v("CHK-mThumbsId",mThumbIds[i]);
        }*/
        /*
           Now we run the fetch task to fetch image urls
         */

            FetchMovie fetch = new FetchMovie(c);
            try {
                if (MainActivityFragment.this.popular == true) {
                    mThumbIds = fetch.execute("popular").get();
                    Log.v("CHK-IMG-ADPTR", "this-poplr-true");
                }
                else {
                    mThumbIds = fetch.execute("top_rated").get();
                    Log.v("CHK-IMG-ADPTR", "this-poplr-false");
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


