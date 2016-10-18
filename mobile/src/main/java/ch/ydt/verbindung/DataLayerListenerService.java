package ch.ydt.verbindung;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class DataLayerListenerService extends WearableListenerService implements
        TextToSpeech.OnInitListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String searchQuery;
    GoogleApiClient googleClient;
    private static final String MESSAGE_RECEIVED_PATH = "/verbindung_pfad";


    @Override
    public void onCreate() {
        super.onCreate();
        // Build a new GoogleApiClient
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();

    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        String[] locations = new String[2];
        searchQuery = messageEvent.getPath();
        try {
          locations = searchQuery.split(" nach ");
        }
        catch (Exception e){
            Log.e("Verbindung", "String not correct");

        }
        try {
            FetchConnectionTask connectionTask = new FetchConnectionTask();
            connectionTask.execute(locations[0], locations[1]);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }



    }

 /*   private OTConnection convertJsonToConnection(String jsonConnectionString) {
        //JSONObject latestDataJson = new JSONObject(jsonConnectionString);
        //JSONArray valueArray = latestDataJson.getJSONArray("value");
        OTConnection connection = new OTConnection(null, null, null, null, null, null, null, null);
        return connection;
    }*/

    @Override
    public void onDestroy() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //sendDataLayerMessage();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onInit(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public class FetchConnectionTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchConnectionTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String latestDataJsonStr = null;

            try {

                final String FORECAST_BASE_URL = "http://transport.opendata.ch/v1/connections?";
                final String FIELDS = "fields%5B%5D]";
                final String FROM = "from";
                final String TO = "to";
                final String STATION_NAME_FROM_PARAM = "connections/from/station/name";
                final String STATION_NAME_TO_PARAM = "connections/to/station/name";
                final String DEPARTURE_PARAM = "connections/from/departure";
                final String ARRIVAL_PARAM = "connections/to/arrival";
                final String DURATION_PARAM = "connections/duration";
                final String PLATFORM_FROM_PARAM = "connections/from/platform";
                final String PLATFORM_TO_PARAM = "connections/to/platform";
                final String PRODUCTS_PARAM = "connections/products";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(FROM, params[0])
                        .appendQueryParameter(TO, params[1])
                        .appendQueryParameter(FIELDS, STATION_NAME_FROM_PARAM)
                        .appendQueryParameter(FIELDS, STATION_NAME_TO_PARAM)
                        .appendQueryParameter(FIELDS, DEPARTURE_PARAM)
                        .appendQueryParameter(FIELDS, ARRIVAL_PARAM)
                        .appendQueryParameter(FIELDS, DURATION_PARAM)
                        .appendQueryParameter(FIELDS, PLATFORM_FROM_PARAM)
                        .appendQueryParameter(FIELDS, PLATFORM_TO_PARAM)
                        .appendQueryParameter(FIELDS, PRODUCTS_PARAM)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    Log.e(LOG_TAG, "no Answer received");
                    return null;
                }
                latestDataJsonStr = buffer.toString();
                Log.v(LOG_TAG, latestDataJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {

                sendDataToWatch(latestDataJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

    }

    private void sendDataToWatch(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the connected nodes and wait for results
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
                for (Node node : nodes.getNodes()) {
                    // Send a message and wait for result
                    MessageApi.SendMessageResult result =
                            Wearable.MessageApi.sendMessage(googleClient, node.getId(),
                                    MESSAGE_RECEIVED_PATH, message.getBytes()).await();
                    if (result.getStatus().isSuccess()) {
                        Log.v("myTag", "Message sent to : " + node.getDisplayName());
                    }
                    else {
                        // Log an error
                        Log.v("myTag", "MESSAGE ERROR: failed to send Message");
                    }
                }
            }
        }).start();
    }
}