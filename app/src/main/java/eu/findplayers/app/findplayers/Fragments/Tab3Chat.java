package eu.findplayers.app.findplayers.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Adapters.TournamentMessageAdapter;
import eu.findplayers.app.findplayers.Data.GroupChatData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 20.3.2018.
 */

public class Tab3Chat extends Fragment {
    EditText sendMessage;
    Integer logged_id, tournamentId;
    String logged_name, logged_image, tournament_name;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    TextView tournament_messages_warning;

    RecyclerView messages;
    LinearLayoutManager linearLayoutManager;
    TournamentMessageAdapter tournamentMessageAdapter;
    List<GroupChatData> groupChatData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3chat, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get display size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height_px = Resources.getSystem().getDisplayMetrics().heightPixels;
        int pixeldpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        int height_dp = (height_px/pixeldpi)*160;
        ConstraintLayout constraintLayout;
        constraintLayout = (ConstraintLayout) getActivity().findViewById(R.id.constraintLayoutTab3) ;
        constraintLayout.setMinHeight(height_dp);

        //Get ID of logged user
        //Initialised in HomeFragment
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.
        logged_name = prefs.getString("login_name", "");
        logged_image = prefs.getString("login_image", "");

        //Bundle from TournamentsAdapter
        final Bundle bundle = getActivity().getIntent().getExtras();
        tournamentId = bundle.getInt("tournament_id");
        tournament_name = bundle.getString("tournamentName");

        sendMessage = (EditText) getActivity().findViewById(R.id.send_message_to_all);
        messages = (RecyclerView) getActivity().findViewById(R.id.recycler_tournament_messages);
        tournament_messages_warning = (TextView) getActivity().findViewById(R.id.tournament_messages_warning);

        //Set sendMessage visible
        getTournament(tournamentId);

        //Set Messages
        groupChatData = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        messages.setLayoutManager(linearLayoutManager);
        tournamentMessageAdapter = new TournamentMessageAdapter(getActivity(), groupChatData);
        messages.setAdapter(tournamentMessageAdapter);

        //set firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference messageRef = database.getReference();
        final DatabaseReference myRef = database.getReference("tournament_messages");
        final DatabaseReference readMessages = messageRef.child("tournament_messages").child(tournamentId.toString());


        //Send message on keyboard ENTER
        sendMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (i == KeyEvent.KEYCODE_ENTER))
                {
                    String message = sendMessage.getText().toString();
                    if (message.equals(""))
                    {
                        Toast.makeText(getActivity(), "Fill input", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        String key = myRef.push().getKey();

                        Long tsLong = System.currentTimeMillis()/1000;
                        String timestamp = tsLong.toString();

                        GroupChatData groupChatData = new GroupChatData(message, key, "Tournament Message", logged_name, logged_image, timestamp, tournament_name, logged_id, tournamentId, true);

                        String tournament_id = tournamentId.toString();
                        myRef.child(tournament_id).child(key).setValue(groupChatData);
                        sendMessage.setText("");
                        return true;
                    }


                }
                return false;
            }
        });

        //Get messages to tournament
        readMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String eq_from_id = logged_id.toString();
                String whoSendMessage = String.valueOf(dataSnapshot.child("fromName").getValue());
                String fromImage = String.valueOf(dataSnapshot.child("fromImage").getValue());
                String timestamp = String.valueOf(dataSnapshot.child("timestamp").getValue());
                Integer from_id = dataSnapshot.child("from_id").getValue(Integer.class);
                String fromIDString = String.valueOf(dataSnapshot.child("from_id").getValue());
                String message = String.valueOf(dataSnapshot.child("message").getValue());
                String notifiData = String.valueOf(dataSnapshot.child("notification_data").getValue());
                String tournam_name = String.valueOf(dataSnapshot.child("tournamentName").getValue());

                if (fromIDString.equals(eq_from_id))
                {
                    GroupChatData Data = new GroupChatData(message, notifiData, "Tournament Message", whoSendMessage, fromImage,timestamp, tournam_name, from_id, tournamentId, true);
                    groupChatData.add(Data);
                    tournamentMessageAdapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(messages.getAdapter().getItemCount()-1);
                } else
                {
                    GroupChatData Data = new GroupChatData(message, notifiData, "Tournament Message", whoSendMessage, fromImage,timestamp, tournam_name, from_id, tournamentId, false);
                    groupChatData.add(Data);
                    tournamentMessageAdapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(messages.getAdapter().getItemCount()-1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTournament(final int id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String playersNumber = jsonObject.getString("players");
                    String[] players = playersNumber.split(",");

                        //Check, if user is already in tournament
                        if (Arrays.asList(players).contains(logged_id.toString()))
                        {

                        } else
                        {
                            sendMessage.setVisibility(View.GONE);
                            messages.setVisibility(View.GONE);
                            tournament_messages_warning.setVisibility(View.VISIBLE);
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error
                        //Log.d("Error.Response", error);
                        // Toast.makeText(MessagesActivity.this, "Error", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                String tournamentID = String.valueOf(id);

                Map<String, String> params = new HashMap<String, String>();
                params.put("getTournament", "true");
                params.put("tournamentID", tournamentID);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }
}