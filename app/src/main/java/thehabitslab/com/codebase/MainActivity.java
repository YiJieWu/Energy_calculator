package thehabitslab.com.codebase;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

import static android.util.Log.*;


/**
 * This is the class for the main activity that the user interacts with.
 * <p/>
 * In this class, most of the workings are left in place since the methods called will need to be
 * implemented anyway. Students should figure out how to register the accelerometer to the
 * listener and do so, maintaining the fields in this class as documented.
 * <p/>
 * Created by William on 12/7/2016
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";

    // Sensor related fields
    private Sensor mAccel;
    private SensorManager mManager;
    private TextView statusText;
    /**
     * Maintains accelerometer registration state.
     * Should be updated every time you register/unregister outside of
     * activity lifecycle.
     */
    private boolean accelIsRegistered = false;
    private DataAccumulator dataManager = new DataAccumulator();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /* ***************************** ACTIVITY CONTROL METHODS ********************************** */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccel = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        statusText = (TextView) findViewById(R.id.status);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if the accelerometer was streaming before paused
        if (accelIsRegistered) {
            mManager.registerListener(accelListener, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
            statusText.setText(R.string.text_active);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Check if accelerometer should be paused
        if (accelIsRegistered) {
            mManager.unregisterListener(accelListener);
            statusText.setText(R.string.text_inactive);
        }
    }


    /* ****************************** USER INTERACTION HANDLING ******************************** */

    /**
     * Called when the accelerometer button is clicked
     */

    //This method is called toggleAccelClicked because you specify this in the XML file onclick
    //attribute for this button

    public void toggleAccelClicked(View v) {
        // TODO: Toggle streaming on or off depending on previous state
        if(!accelIsRegistered){
            mManager.registerListener(accelListener, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
            statusText.setText(R.string.text_active);
            accelIsRegistered=true;
        }else{
            mManager.unregisterListener(accelListener);
            statusText.setText(R.string.text_inactive);
            accelIsRegistered=false;
        }

    }

    /**
     * Called when show document button is clicked
     */
    public void showDocumentClicked(View v) {
        // Get the string representing the current table from SQLite
        String content = SQLiteInterface.getCurrentTableString(this);
        ((TextView) findViewById(R.id.docText)).setText(content);
    }

    /**
     * Called when replication button is clicked
     */
    public void onReplicateClicked(View v) {
        SQLiteInterface.sendDataToBackend(this.getBaseContext());
    }


    /* ********************************** WORKING PARTS **************************************** */
    /**
     * Custom implementation of SensorEventListener specific to what we want to do with
     * accelerometer data.
     */
    private SensorEventListener accelListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Log.d("TAG","The accelerometer value is "+"x is:"+ x+"y is:"+y+"z is:"+z);
            EnergyReading energy;
            if ((energy = dataManager.addEvent(event)) != null)
                handleEnergyValue(energy);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle change of accuracy
            w(TAG, "Accuracy of accelerometer changed.");
        }


        /**
         * Performs operations on the energy value once it is obtained
         * @param energy returned from an accumulator
         */
        private void handleEnergyValue(EnergyReading energy) {
            try {
                SQLiteInterface.addEnergyReading(energy, MainActivity.this);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
