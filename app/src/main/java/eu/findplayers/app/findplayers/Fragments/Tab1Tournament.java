package eu.findplayers.app.findplayers.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 20.3.2018.
 */

public class Tab1Tournament extends Fragment {
    TextView tab1, tournamentName,tournamentIsFull;
    Integer tournament_id, logged_id;
    Button addToTournament, removeFromTournament;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get ID of logged user
        //Initialised in HomeFragment
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.

        //Get bundle from TournamentsAdapter(TournamentsFragment)
        final Bundle bundle = getActivity().getIntent().getExtras();

        tab1 = (TextView) getActivity().findViewById(R.id.tab1);
        tournamentName = (TextView) getActivity().findViewById(R.id.tournamentName);
        addToTournament = (Button) getActivity().findViewById(R.id.addToTournament);
        removeFromTournament = (Button) getActivity().findViewById(R.id.removeFromTournament);
        tournamentIsFull = (TextView) getActivity().findViewById(R.id.tournamentIsFull);
        tournamentName.setText(bundle.getString("tournamentName"));
        tournament_id = bundle.getInt("tournament_id");

        getTournament(tournament_id);

        //Add yourself into tournament
        addToTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addYourselfToTournament(logged_id,tournament_id);
            }
        });

        removeFromTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveTournament(logged_id,tournament_id);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1tournament, container, false);
        return rootView;
    }

    private void getTournament(final int id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Integer alreadyInTournament = jsonObject.getInt("alreadyInTournament");
                        Integer allPlayers = jsonObject.getInt("players_number");

                        String playersNumber = jsonObject.getString("players");
                        String[] players = playersNumber.split(",");

                        //Check if tournament is already full
                        if (allPlayers.equals(alreadyInTournament) )
                        {
                            addToTournament.setVisibility(View.INVISIBLE);
                            //Check, if user is already in tournament
                            if (Arrays.asList(players).contains(logged_id.toString()))
                            {
                                removeFromTournament.setVisibility(View.VISIBLE);
                            } else
                            {
                                tournamentIsFull.setVisibility(View.VISIBLE);
                            }

                        } else {
                            addToTournament.setVisibility(View.VISIBLE);
                            //Check, if user is already in tournament
                            if (Arrays.asList(players).contains(logged_id.toString()))
                            {
                                tab1.setText("In tournament");
                                addToTournament.setVisibility(View.INVISIBLE);
                                removeFromTournament.setVisibility(View.VISIBLE);
                            } else {
                                tab1.setText("No in Tournament");
                                addToTournament.setVisibility(View.VISIBLE);
                            }
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

    private void addYourselfToTournament(final int logged_id, final int tournament_id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Responsea", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("OK"))
                    {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText(message)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        getActivity().finish();
                                        getActivity().overridePendingTransition( 0, 0);
                                        startActivity(getActivity().getIntent());
                                        getActivity().overridePendingTransition( 0, 0);
                                    }
                                })
                                .show();

                    } else if (code.equals("ERROR"))
                    {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Ou no!")
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
                String userID = String.valueOf(logged_id);
                String tournID = String.valueOf(tournament_id);


                Map<String, String> params = new HashMap<String, String>();
                params.put("addToTournament", "true");
                params.put("tournamentID", tournID);
                params.put("userID", userID);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void leaveTournament(final int logged_id, final int tournament_id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Responsea", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("OK"))
                    {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText(message)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        getActivity().finish();
                                        getActivity().overridePendingTransition( 0, 0);
                                        startActivity(getActivity().getIntent());
                                        getActivity().overridePendingTransition( 0, 0);
                                    }
                                })
                                .show();

                    } else if (code.equals("ERROR"))
                    {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Ou no!")
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
                String userID = String.valueOf(logged_id);
                String tournID = String.valueOf(tournament_id);


                Map<String, String> params = new HashMap<String, String>();
                params.put("leaveTournament", "true");
                params.put("tournamentID", tournID);
                params.put("userID", userID);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }
}
