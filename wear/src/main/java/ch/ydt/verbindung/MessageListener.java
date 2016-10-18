package ch.ydt.verbindung;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by didi on 08.09.14.
 */
public class MessageListener extends WearableListenerService {

    private static final String MESSAGE_RECEIVED_PATH = "/verbindung_pfad";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(MESSAGE_RECEIVED_PATH)) {
            final String message = new String(messageEvent.getData());
            startActivity(new Intent(getApplicationContext(), Result.class).putExtra("json_message", message).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            super.onMessageReceived(messageEvent);
        }
    }


}