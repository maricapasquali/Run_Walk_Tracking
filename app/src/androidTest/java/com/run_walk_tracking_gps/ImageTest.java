package com.run_walk_tracking_gps;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.utilities.FileUtilities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import com.run_walk_tracking_gps.utilities.ImageFileHelper;

import androidx.test.InstrumentationRegistry;
// TODO: 2/17/2020 RIMUOVERE
public class ImageTest {

    private final static String TAG = ImageTest.class.getName();

    private Context context= InstrumentationRegistry.getTargetContext();

    private static final String IMAGE_DIRECTORY = "image";
    private static final String TMP = "tmp";
    private static final String IMAGE_NAME = "image.jpg";
    private static final String IMAGE_NAME_COPY = "copy.jpg";
    private static final String IMAGE_NAME_MOVE = "move";
    private static final String IMAGE_NAME_RENAME = "renames.jpg";

    private File directory;
    private File directory_tmp;
    private File file;

    private ImageFileHelper imageFileHelper;

    @Before
    public void setUp(){
        imageFileHelper = ImageFileHelper.create(context);

        directory = imageFileHelper.getDirectoryImage();
        directory.delete();
    }


    @Test
    public void test(){
        //Assert.assertTrue(FileUtilities.deleteDirectoryAndContent(directory));

        try {
            directory = imageFileHelper.getDirectoryImage();
            directory_tmp = imageFileHelper.getDirectoryTmp();

            Assert.assertTrue(directory.exists());
            Assert.assertTrue(directory_tmp.exists());
        } catch (NullPointerException e){
            Log.e(TAG, "Cartella non creata.");
        }

       /*Assert.assertTrue(imageFileHelper.moveToTmpDir(Uri.parse(file.getPath())));
        Assert.assertTrue(imageFileHelper.moveToImageDir());*/

    }

}
