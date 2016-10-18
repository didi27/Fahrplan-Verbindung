package ch.ydt.verbindung;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Result extends Activity implements WearableListView.ClickListener{

    private TextView mTextView;
    private WearableListView listView;
    DateFormat sBB = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    DateFormat sBBduration = new SimpleDateFormat("dd'd'HH:mm:ss");
    DateFormat watchFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                listView = (WearableListView) stub.findViewById(R.id.listView);
                listView.setAdapter(new OTConnectionAdapter(Result.this));
                listView.setClickListener(Result.this);
            }
        });
        //ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.list_item_connection,)
        if (getIntent() != null) {
            String json_message = getIntent().getStringExtra("json_message");
            try {
                convertJsonToConnection(json_message);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // TO ADD LATER :)
        //    for (int i = 0; i<3; i++) {
         //       listItems.add(title);
          //  }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private static ArrayList<OTConnection> listItems;

    static {
        listItems = new ArrayList<OTConnection>();


    }

    private void convertJsonToConnection(String jsonConnectionString) throws JSONException, ParseException {
        listItems.clear();
        JSONObject latestDataJson = new JSONObject(jsonConnectionString);
        JSONArray valueArray = latestDataJson.getJSONArray("connections");
        for (int i = 0; i < valueArray.length(); i++){
            JSONObject connection = valueArray.getJSONObject(i);

            JSONObject locationDeparture = connection.getJSONObject("from");
            String departureTime = locationDeparture.getString("departure");
                    //2014-09-12T06:14:00+0200
            Date departureDate = sBB.parse(departureTime);
            JSONObject stationDeparture = locationDeparture.getJSONObject("station");
            String DepartureLocation = stationDeparture.getString("name");
            JSONObject locationArrival = connection.getJSONObject("to");
            String arrivalTime = locationArrival.getString("arrival");
            Date arrivalDate;
            arrivalDate = sBB.parse(arrivalTime);
            JSONObject stationArrival = locationArrival.getJSONObject("station");
            String ArrivalLocation = stationArrival.getString("name");
            Date duration = sBBduration.parse(connection.getString("duration"));
            int platform = locationDeparture.getInt("platform");
         listItems.add(new OTConnection(DepartureLocation, ArrivalLocation, departureDate, arrivalDate, duration, platform));

        }

       // OTConnection connection = new OTConnection(null, null, null, null, null, null, null, null);
     //   return connection;
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    public class OTConnectionAdapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;


        private OTConnectionAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.list_item_connection, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView locationsFrom = (TextView) holder.itemView.findViewById(R.id.locationFrom);
            TextView locationsTo = (TextView) holder.itemView.findViewById(R.id.locationTo);
            TextView duration = (TextView) holder.itemView.findViewById(R.id.duration);
            TextView platform = (TextView) holder.itemView.findViewById(R.id.platform);
            TextView departureTime = (TextView) holder.itemView.findViewById(R.id.departureTime);
            TextView arrivalTime = (TextView) holder.itemView.findViewById(R.id.arrivalTime);



            locationsFrom.setText(listItems.get(position).getLocationFrom());
            locationsTo.setText(listItems.get(position).getLocationTo());
            duration.setText(watchFormat.format(listItems.get(position).getDuration()));
            platform.setText(Integer.toString(listItems.get(position).getPlatform()));
            departureTime.setText(watchFormat.format(listItems.get(position).getdepartureTime()));
            arrivalTime.setText(watchFormat.format(listItems.get(position).getarrivalTime()));
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
}