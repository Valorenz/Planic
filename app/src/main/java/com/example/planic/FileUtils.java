package com.example.planic;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static String getPath(Context context, Uri uri) {
        String filePath = null;

        // Jika scheme adalah "content://"
        if ("content".equals(uri.getScheme())) {
            try {
                Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
                if (returnCursor != null && returnCursor.moveToFirst()) {
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String name = returnCursor.getString(nameIndex);
                    File file = new File(context.getCacheDir(), name);
                    try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                         OutputStream outputStream = new FileOutputStream(file)) {

                        byte[] buffers = new byte[1024];
                        int read;
                        while ((read = inputStream.read(buffers)) != -1) {
                            outputStream.write(buffers, 0, read);
                        }
                    }
                    returnCursor.close();
                    filePath = file.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equals(uri.getScheme())) {
            filePath = uri.getPath();
        }

        return filePath;
    }
}
