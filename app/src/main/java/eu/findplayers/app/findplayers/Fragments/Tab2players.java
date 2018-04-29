package eu.findplayers.app.findplayers.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Adapters.FriendsAdapter;
import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.Data.TournamentData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 20.3.2018.
 */

public class Tab2players extends Fragment {

    Integer tournament_id, logged_id;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FriendsAdapter adapter;
    private List<FriendsData> friends_list;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.

        final Bundle bundle = getActivity().getIntent().getExtras();

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_players);

        tournament_id = bundle.getInt("tournament_id");

        friends_list = new ArrayList<>();
        load_players(tournament_id);
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new FriendsAdapter(getActivity(), friends_list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2players, container, false);

        return rootView;
    }

    private void load_players(final int id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Responsea", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String user_image = jsonObject.getString("profile_image");
                        String user_name = jsonObject.getString("username");
                        Integer user_id = jsonObject.getInt("friend_id");

                        FriendsData data = new FriendsData(user_image,user_name, "info", user_id, logged_id);
                        friends_list.add(data);
                        adapter.notifyDataSetChanged();
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
                params.put("tournamentUserList", "true");
                params.put("tournamentID", tournamentID);


                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }
}