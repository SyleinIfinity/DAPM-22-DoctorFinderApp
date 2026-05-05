package nhom22.doctorfinder.utils;


import android.content.Context;
import android.net.Uri;

import java.io.*;

public class FileUtils {

    public static File getFile(Context context, Uri uri) {
        File file = new File(context.getCacheDir(), "upload.jpg");

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
}