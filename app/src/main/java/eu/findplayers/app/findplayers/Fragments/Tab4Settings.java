package eu.findplayers.app.findplayers.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab4Settings extends Fragment {
    Integer logged_id, tournament_id;
    Spinner FirstWinner, SecondWinner, ThirdWinner;
    TextView firstWinnerText, secondWinnerText, thirdWinnerText;
    Button sendWinners;
    String valueFirst, valueSecond, valueThird;
    public static final String MY_PREFS_NAME = "MyPrefsFile";


    public Tab4Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab4settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FirstWinner = (Spinner) getActivity().findViewById(R.id.firstWinner);
        SecondWinner = (Spinner) getActivity().findViewById(R.id.secondWinner);
        ThirdWinner = (Spinner) getActivity().findViewById(R.id.thirdWinner);
        firstWinnerText = (TextView) getActivity().findViewById(R.id.firstWinnerText);
        secondWinnerText = (TextView) getActivity().findViewById(R.id.secondWinnerText);
        thirdWinnerText = (TextView) getActivity().findViewById(R.id.thirdWinnerText);
        sendWinners = (Button) getActivity().findViewById(R.id.sendWinners);

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.

        //Get bundle from TournamentsAdapter(TournamentsFragment)
        final Bundle bundle = getActivity().getIntent().getExtras();
        tournament_id = bundle.getInt("tournament_id");

        getTournament(tournament_id, logged_id);

        //Send Winners
        sendWinners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueFirst = FirstWinner.getSelectedItem().toString();
                valueSecond = SecondWinner.getSelectedItem().toString();
                valueThird = ThirdWinner. getSelectedItem().toString();
                sendWinnersToServer(tournament_id, valueFirst, valueSecond, valueThird);
            }
        });

    }

    private void getTournament(final int id, final int userID)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Integer admin_id = jsonObject.getInt("admin_id");

                    if(userID == admin_id)
                    {
                        FirstWinner.setVisibility(View.VISIBLE);
                        SecondWinner.setVisibility(View.VISIBLE);
                        ThirdWinner.setVisibility(View.VISIBLE);
                        firstWinnerText.setVisibility(View.VISIBLE);
                        secondWinnerText.setVisibility(View.VISIBLE);
                        thirdWinnerText.setVisibility(View.VISIBLE);
                        sendWinners.setVisibility(View.VISIBLE);
                    }

                    getTournamentWinners(id);

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

    private void getTournamentWinners(final int id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<String> ar = new ArrayList<String>();

                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String user_image = jsonObject.getString("profile_image");
                        String user_name = jsonObject.getString("username");
                        Integer user_id = jsonObject.getInt("friend_id");

                        ar.add(user_name);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, ar);
                    FirstWinner.setAdapter(adapter);
                    SecondWinner.setAdapter(adapter);
                    ThirdWinner.setAdapter(adapter);



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
                params.put("tournamentUserList", "true");
                params.put("tournamentID", tournamentID);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void sendWinnersToServer(final int id, final String first, final String second, final String third)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String ok = jsonObject.getString("response");
                    String message = jsonObject.getString("message");

                    if (ok.equals("OK"))
                    {
                        SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                        pDialog.setTitleText("Great!");
                        pDialog.setContentText(message);
                        pDialog.show();
                    } else
                    {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(message)
                                .show();
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
                params.put("addWinners", "true");
                params.put("tournamentID", tournamentID);
                params.put("first", first);
                params.put("second", second);
                params.put("third", third);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }
}
