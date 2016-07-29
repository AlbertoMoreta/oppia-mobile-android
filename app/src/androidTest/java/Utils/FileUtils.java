package Utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.digitalcampus.oppia.utils.storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class FileUtils {

    ///from https://github.com/riggaroo/android-retrofit-test-examples/blob/master/RetrofitTestExample/app/src/androidTest/java/za/co/riggaroo/retrofittestexample/RestServiceTestHelper.java

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(Context context, String filePath) throws Exception {
        final InputStream stream = context.getResources().getAssets().open(filePath);

        String ret = convertStreamToString(stream);

        stream.close();
        return ret;
    }

    public static void copyZipFromAssets(String filename){

        try {
            InputStream is = InstrumentationRegistry.getContext().getResources().getAssets().open("courses/" + filename);
            File outputFile = new File(Storage.getDownloadPath(InstrumentationRegistry.getTargetContext()), filename);
            OutputStream os = new FileOutputStream(outputFile);

            //Copy File
            byte[] buffer = new byte[1024];
            int read;
            while((read = is.read(buffer)) != -1){
                os.write(buffer, 0, read);
            }

            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
