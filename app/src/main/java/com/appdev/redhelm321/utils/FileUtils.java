package com.appdev.redhelm321.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static File getFileFromUri(Context context, Uri uri) throws Exception {
        // Get file name from URI
        String fileName = getFileName(context, uri);
        File tempFile = new File(context.getCacheDir(), fileName);

        // Copy the content from URI to the temp file
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer);
            }
        }

        return tempFile;
    }

//    public Bitmap getBitmapFromUri(Uri uri) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            try {
//                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
//                return ImageDecoder.decodeBitmap(source);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                return BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    private static String getFileName(Context context, Uri uri) {
        String fileName = "temp_file";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }
}
