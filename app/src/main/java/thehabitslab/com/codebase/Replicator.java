package thehabitslab.com.codebase;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.data;

/**
 * Sends the energy data to the back end when requested.
 * The Replicator keeps track of when it is replicating and does not replicate multiple times.
 * The replicated data is deleted from the local database.
 * To replicate, this class should be instantiated and execute() should be called.
 * <p/>
 * Students are required to write the meat of the transmission of data to the back end.
 * <p/>
 * Created by William on 12/30/2016.
 */
public class Replicator extends AsyncTask<Void, Void, Object> {
    private static final String TAG = "Replicator";
    private static boolean isReplicating = false;
    private boolean isCanceled = false;

    private Context context;

    public Replicator(Context context) {
        this.context = context;
    }

    @Override
    /**
     * When execute() is called, this happens first
     */
    protected void onPreExecute() {
        isCanceled = isReplicating;
        isReplicating = true;
    }

    @Override
    /**
     * When execute() is called, this happens second
     */
    protected Void doInBackground(Void... params) {
        // Don't do anything if the execution is canceled
        if (!isCanceled) {
            // Query the database and package the data
            Cursor c = EnergyDBHelper.getFirst60Entries(context);
            int timeCol = c.getColumnIndex(EnergyDBHelper.EnergyEntry.COLUMN_NAME_TIME);
            int energyCol = c.getColumnIndex(EnergyDBHelper.EnergyEntry.COLUMN_NAME_ENERGY);

            String id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            c.moveToFirst();



            OutputStreamWriter writer = null;
            BufferedReader reader;




            try {
                // TODO: make an HttpURLConnection and send data as parameters in a POST (one at a time)
                Log.d(TAG, "BEFORE THE STORE TO BACKEND LOOP");

                int count=0;
                while(c.isAfterLast() == false) {


                    Log.d(TAG, "IN THE STORE TO BACKEND LOOP");


                    // Create data variable for sent values to server
                    String query_string = URLEncoder.encode("mac", "UTF-8")
                            + "=" + URLEncoder.encode(id, "UTF-8");
                    query_string += "&" + URLEncoder.encode("time", "UTF-8") + "="
                            + URLEncoder.encode(c.getString(timeCol), "UTF-8");
                    query_string += "&" + URLEncoder.encode("energy", "UTF-8") + "="
                            + URLEncoder.encode(String.valueOf(c.getDouble(energyCol)), "UTF-8");
                    String text = "";

                    // Defined URL  where to send data
                    URL url = null;
                    url = new URL("http://murphy.wot.eecs.northwestern.edu/~ywv3509/SQLGateway.py");
                    // Send POST data request
                    URLConnection conn = null;
                    conn = (URLConnection) url.openConnection();

                    conn.setDoOutput(true);
                    writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(query_string);
                    writer.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line + "\n");
                    }
                    text = sb.toString();
                    Log.d(TAG, text);

                    count++;
                    c.moveToNext();
                }
                //delete the successfully backup data
                EnergyDBHelper.deleteNEntries(context,count);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    /**
     * When execute is called, this happens third
     */
    protected void onPostExecute(Object result) {
        isReplicating = false;
    }

}