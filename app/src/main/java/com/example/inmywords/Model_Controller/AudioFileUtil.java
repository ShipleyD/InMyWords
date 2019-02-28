package com.example.inmywords.Model_Controller;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioFileUtil {

    private static final String LOG_TAG = "FileUtil";
    public File getRouteStorageDir(String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(Environment.getExternalStorageDirectory(), "InMyWords");
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        file = new File(Environment.getExternalStorageDirectory()+"/InMyWords", albumName);
        return file;
    }

}
