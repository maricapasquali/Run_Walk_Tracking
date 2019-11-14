package com.run_walk_tracking_gps.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class BitmapUtilities {

    /**
     * Convert Bitmap into byte array and compress into Webp format,
     * then byte array is compressed and converted into Base64 string.
     * @param bitmap to convert
     * @return a encoding Base64 and compressing string
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP,100, bos);
        byte[] compressByte = compressByteArray(bos.toByteArray());
        return Base64.getEncoder().encodeToString(compressByte);
    }

    public static byte[] compressByteArray(byte[] in){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream defl = new DeflaterOutputStream(out);

        try {
            defl.write(in);
            defl.flush();
            defl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public static byte[] decompress(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InflaterOutputStream infl = new InflaterOutputStream(out);
        try {
            infl.write(in);
            infl.flush();
            infl.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap StringToBitMap(String encodedString){
        byte[] b = Base64.getDecoder().decode(encodedString);
        byte[] decompress = decompress(b);
        return BitmapFactory.decodeByteArray(decompress, 0, decompress.length);
    }
}
