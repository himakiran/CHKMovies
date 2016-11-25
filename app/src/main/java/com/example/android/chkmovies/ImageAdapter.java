package com.example.android.chkmovies;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by userhk on 25/11/16.
 * Code copied from https://www.tutorialspoint.com/android/android_grid_view.htm
 * This class will serve the purpose of supplying images to the MainActivityFragment for display
 */

public class ImageAdapter extends BaseAdapter {
    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.img1, R.drawable.img2,
            R.drawable.img3, R.drawable.img4,
            R.drawable.img5, R.drawable.img6

    };
    private Context mContext;

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
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

            imageView.setLayoutParams(new RelativeLayout.LayoutParams((int) mContext.getResources().getDimension(R.dimen.width), (int) mContext.getResources().getDimension(R.dimen.height)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
}

