package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;

public class FileUtilities {

    public static File getDirectory(Context context, String nameDir) {
        final File myDir = new File(context.getCacheDir().getParent(), nameDir);

        if(!myDir.exists())
            if(!myDir.mkdir())
                throw new NullPointerException();

        return myDir;
    }

    public static File getDirectory(File dirDest, String nameDir) {
        final File myDir = new File(dirDest, nameDir);

        if(!myDir.exists())
            if(!myDir.mkdir())
                throw new NullPointerException();

        return myDir;
    }

    public static boolean move(File file, String pathImage) {
        return file.renameTo(new File(pathImage));
    }


    public static boolean deleteDirectoryAndContent(File dir) {
        if(dir.exists()) {
            try {
                final List<File> files = Files.walk(dir.toPath()).map(Path::toFile).collect(Collectors.toList());
                if(files.size()>0); files.forEach(File::delete);
                return dir.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
