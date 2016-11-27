package com.example.android.chkmovies;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

/**
 * Created by userhk on 25/11/16.
 * Code copied from https://www.tutorialspoint.com/android/android_grid_view.htm
 * This class will serve the purpose of supplying images to the MainActivityFragment for display
 */

public class ImageAdapter extends BaseAdapter {
    // Keep all Images in array
    private String[] mThumbIds;
    private Context mContext;


    // Constructor
    public ImageAdapter(Context c, String[] imageArray) {
        mContext = c;
        mThumbIds = new String[imageArray.length];
        for(int i=0; i < imageArray.length; i++){
            mThumbIds[i] = imageArray[i];
            Log.v("CHK-mThumbsId",mThumbIds[i]);
        }



    }

    /*
    we r not using the below getCount() , getItem() and getItemId()
     */
    public int getCount() {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
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
        Log.v("CHK-IMG-ADAPTER", "GET-VIEW");
        Picasso
                .with(mContext)
                .load(mThumbIds[position])
                .fit()
                .into(imageView);


        return imageView;
    }
}

