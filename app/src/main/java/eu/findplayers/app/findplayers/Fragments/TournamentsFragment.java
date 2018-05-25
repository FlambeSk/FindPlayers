package eu.findplayers.app.findplayers.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.Adapters.TournamentsAdapter;
import eu.findplayers.app.findplayers.Data.TournamentData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.MainActivity;
import eu.findplayers.app.findplayers.ProfileActivity;
import eu.findplayers.app.findplayers.R;
import eu.findplayers.app.findplayers.TournamentAddActivity;
import eu.findplayers.app.findplayers.TournamentCardActivity;
import eu.findplayers.app.findplayers.UserActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TournamentsFragment extends Fragment {

    ImageView newTournament;
    Integer logged_id;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    //My tournaments
    private List<TournamentData> myTournamentsData;
    private TournamentsAdapter myTournamentsAdapter;
    private RecyclerView myTournamentsRecyclerView;

    //all tournaments
    private List<TournamentData> tournamentData;
    private TournamentsAdapter tournamentsAdapter;
    private RecyclerView recyclerView;

    //Biggest tournament
    private TournamentsAdapter tournamentBiggestAdapter;
    private RecyclerView recyclerViewBiggest;
    private List<TournamentData> tournamentBiggestData;

    //Ended tournaments
    private TournamentsAdapter tournamentEndedAdapter;
    private RecyclerView recyclerViewEnded;
    private List<TournamentData> tournamentEndedData;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.tournaments));

        //Get ID of logged user
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.

        newTournament = (ImageView)getActivity().findViewById(R.id.newTournament);

        //Add new tournament
        newTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TournamentAddActivity.class);
                getActivity().startActivity(intent);
            }
        });

        showMyTournaments();

       AllTournaments();

       BiggestTournament();

        showEndedTournaments();

        //SWIPE DOWN - refresh Activity
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_down_layout_fragment);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh,R.color.refresh1,R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Fragment fragment = null;
                        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("TournamentFragment");
                        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.detach(fragment);
                        fragmentTransaction.attach(fragment);
                        fragmentTransaction.commit();
                    }
                },1000);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tournament, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public TournamentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tournaments, container, false);
    }

    private void MyTournaments(final int user_id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String tournamentName = jsonObject.getString("tournamentName");
                        String tournamentImage = jsonObject.getString("tournament_image");
                        Integer tournamentID = jsonObject.getInt("tournamentID");
                        String countt = jsonObject.getString("counts");
                        String startAt = jsonObject.getString("start");

                        TournamentData data = new TournamentData(tournamentID, tournamentName, tournamentImage, countt, startAt);
                        myTournamentsData.add(data);
                        myTournamentsAdapter.notifyDataSetChanged();
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
                String user = String.valueOf(user_id);

                Map<String, String> params = new HashMap<String, String>();
                params.put("showMyTournaments", "true");
                params.put("userID", user);



                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void Tournaments(final int limit, final String order)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String tournamentName = jsonObject.getString("tournamentName");
                        String tournamentImage = jsonObject.getString("tournament_image");
                        Integer tournamentID = jsonObject.getInt("tournamentID");
                        String countt = jsonObject.getString("counts");
                        String startAt = jsonObject.getString("start");

                        TournamentData data = new TournamentData(tournamentID, tournamentName, tournamentImage, countt, startAt);
                        tournamentData.add(data);
                        tournamentsAdapter.notifyDataSetChanged();
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
                String counts = String.valueOf(limit);

                Map<String, String> params = new HashMap<String, String>();
                params.put("tournamentList", "true");
                params.put("tournamentsCount", counts);
                params.put("orderBy", order);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void BiggestTournaments(final int limit, final String order)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String tournamentName = jsonObject.getString("tournamentName");
                        String tournamentImage = jsonObject.getString("tournament_image");
                        Integer tournamentID = jsonObject.getInt("tournamentID");
                        String countt = jsonObject.getString("counts");
                        String startAt = jsonObject.getString("start");

                        TournamentData data = new TournamentData(tournamentID, tournamentName, tournamentImage, countt, startAt);
                        tournamentBiggestData.add(data);
                        tournamentBiggestAdapter.notifyDataSetChanged();

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
                String counts = String.valueOf(limit);

                Map<String, String> params = new HashMap<String, String>();
                params.put("tournamentList", "true");
                params.put("tournamentsCount", counts);
                params.put("orderBy", order);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void EndedTournaments(final int limit, final String order)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String tournamentName = jsonObject.getString("tournamentName");
                        String tournamentImage = jsonObject.getString("tournament_image");
                        Integer tournamentID = jsonObject.getInt("tournamentID");
                        String countt = jsonObject.getString("counts");
                        String startAt = jsonObject.getString("start");

                        TournamentData data = new TournamentData(tournamentID, tournamentName, tournamentImage, countt, startAt);
                        tournamentEndedData.add(data);
                        tournamentEndedAdapter.notifyDataSetChanged();
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
                String counts = String.valueOf(limit);

                Map<String, String> params = new HashMap<String, String>();
                params.put("endedTournamentList", "true");
                params.put("tournamentsCount", counts);
                params.put("orderBy", order);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }



    private void showMyTournaments()
    {
        MyTournaments(logged_id);
        myTournamentsData = new ArrayList<>();
        myTournamentsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_my_tournaments);
        LinearLayoutManager layoutManagerMyT = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        myTournamentsRecyclerView.setLayoutManager(layoutManagerMyT);
        myTournamentsAdapter = new TournamentsAdapter(getActivity(), myTournamentsData);
        myTournamentsRecyclerView.setAdapter(myTournamentsAdapter);
    }
    private void AllTournaments()
    {
        //create first tournament recyclerview
        Tournaments(100, "id DESC");
        tournamentData = new ArrayList<>();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_all_tournaments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        tournamentsAdapter = new TournamentsAdapter(getActivity(), tournamentData);
        recyclerView.setAdapter(tournamentsAdapter);
    }

    private void BiggestTournament()
    {
        BiggestTournaments(100, "players_number DESC");
        tournamentBiggestData = new ArrayList<>();
        recyclerViewBiggest = (RecyclerView) getActivity().findViewById(R.id.recycler_biggest_tournaments);
        LinearLayoutManager layoutManagerr = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBiggest.setLayoutManager(layoutManagerr);
        tournamentBiggestAdapter = new TournamentsAdapter(getActivity(), tournamentBiggestData);
        recyclerViewBiggest.setAdapter(tournamentBiggestAdapter);
    }

    private void showEndedTournaments()
    {
        EndedTournaments(20, "id DESC");
        tournamentEndedData = new ArrayList<>();
        recyclerViewEnded = (RecyclerView) getActivity().findViewById(R.id.recycler_ended_tournaments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewEnded.setLayoutManager(layoutManager);
        tournamentEndedAdapter = new TournamentsAdapter(getActivity(), tournamentEndedData);
        recyclerViewEnded.setAdapter(tournamentEndedAdapter);
    }



}
