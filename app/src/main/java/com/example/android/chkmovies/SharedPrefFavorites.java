package com.example.android.chkmovies;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by userhk on 18/01/17.
 */

public final class SharedPrefFavorites {

    public static String[] f_imageUrlArray = new String[20];
    public static int[] f_movieIdArray = new int[20];
    public static int pos = 0;

    public SharedPrefFavorites() {
        super();
    }

    public void saveToFav(Context c) {
        SharedPreferences fav;
        SharedPreferences.Editor editor;
        fav = c.getSharedPreferences(c.getString(R.string.preference_file_key), c.MODE_PRIVATE);
        editor = fav.edit();
        editor.putString("imageUrlArray", f_imageUrlArray.toString());
        editor.putString("movieIdArray", f_movieIdArray.toString());

    }

    public void addToFav(Context c, String s, int i) {
        this.f_imageUrlArray[pos] = s;
        this.f_movieIdArray[pos] = i;
        pos++;
        saveToFav(c);
    }

    public int[] getMovArray() {
        return f_movieIdArray;
    }

    public String[] getImgUrlArray() {
        return f_imageUrlArray;
    }
}
