package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import androidx.annotation.RequiresApi;

public class ImageFileHelper {

    private static final String TAG = ImageFileHelper.class.getName();

    private static final String SEPARATOR = "/";
    private static final String IMAGE = "image";
    private static final String TMP = "tmp";

    private Context context;

    private File imageDir;
    private File imageTmpDir;

    private ImageFileHelper(Context context){
        this.context = context;
        this.imageDir = FileUtilities.getDirectory(context, IMAGE);
        this.imageTmpDir = FileUtilities.getDirectory(imageDir, TMP);
    }

    public static ImageFileHelper create(Context context){
        return new ImageFileHelper(context);
    }

    public static String createNameRandom() {
        return getRandomString(20) +".jpg";
    }


    // IMAGE

    public boolean storageImage(Bitmap bitmap, String name){
        File newImage = getImage(name);
        try(FileOutputStream out = new FileOutputStream(newImage)) {
            return bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param name image into dir 'image'
     * @return complete name string image
     */
    public String getPathImage(String name) {
        return getDirectoryImage() + SEPARATOR + name;
    }

    public File getImage(String name) {
        return new File(getPathImage(name));
    }

    public File getDirectoryImage() {
        return imageDir; //FileUtilities.getDirectory(context, IMAGE);
    }

    public boolean moveToImageDir(String newFile){
        File oldImage = getImageTmp(newFile);
        File newImage = getImage(newFile);
        return FileUtilities.move(oldImage, newImage.getPath());
    }

    // TMP

    /**
     * @param name image into dir 'image/tmp'
     * @return complete name string image
     */
    public String getPathTmpImage(String name) {
        return getDirectoryTmp() + SEPARATOR +name;
    }

    public File getImageTmp(String name) {
        return new File(getPathTmpImage(name));
    }

    public File getDirectoryTmp() {
        //File dirImage = getDirectoryImage();return FileUtilities.getDirectory(dirImage, TMP);
        return imageTmpDir;
    }

    public boolean moveToTmpDir(Uri imageUri, String newName){
        File oldImage = new File(imageUri.getPath());
        return FileUtilities.move(oldImage, getPathTmpImage(newName));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean deleteTmpDir() {
        return FileUtilities.deleteDirectoryAndContent(getDirectoryTmp());
    }




    public void load(ImageView container, File image){
        Glide.with(context).load(image.getPath())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .into(container);
    }


    private static String getRandomString(int length){
        StringBuilder token = new StringBuilder();
        String codeAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        codeAlphabet += "abcdefghijklmnopqrstuvwxyz";
        codeAlphabet+= "0123456789";
        int max = codeAlphabet.length(); // edited
        Random random = new Random();

        for (int i=0; i< length; i++){
            int r = random.nextInt(max);
            token.append(codeAlphabet.charAt(r));
        }
        return token.toString();
    }

}
