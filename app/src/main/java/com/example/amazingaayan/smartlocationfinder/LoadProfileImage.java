package com.example.amazingaayan.smartlocationfinder;
/**
 * Created by Amazing Aayan on 22-May-16.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Context context;

    public LoadProfileImage(ImageView bmImage, Context context) {
        this.bmImage = bmImage;
        this.context = context;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

   // protected void onPostExecute(Bitmap result) {
     //   bmImage.setImageBitmap(result);
    //}

    protected void onPostExecute(Bitmap profileImage) {
        saveFile(context, profileImage, "userProfilePicture"); // for saving downloaded profile image;
        Bitmap bitmapProfileImage = loadBitmap(context,"userProfilePicture");
        bmImage.setImageBitmap(bitmapProfileImage); // for loading image from external storage and put it in imageview;
    }

    public static void saveFile(Context context, Bitmap b, String picName){  // for saving bitmap file into external storage;
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            Log.d("File Not Found", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("IO exception", "io exception");
            e.printStackTrace();
        }

    }

    public static Bitmap loadBitmap(Context context, String picName){ // for retriving image from external storage;
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        }
        catch (FileNotFoundException e) {
            Log.d("File Not Found", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("IO exception", "io exception");
            e.printStackTrace();
        }
        return b;
    }
}