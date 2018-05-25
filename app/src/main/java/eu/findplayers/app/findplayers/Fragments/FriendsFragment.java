package eu.findplayers.app.findplayers.Fragments;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.Adapters.FriendsAdapter;
import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    TextView username;
    Integer id;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FriendsAdapter adapter;
    private List<FriendsData> friends_list;


    public FriendsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Friends");
        username = (TextView)getView().findViewById(R.id.username);

        //Getting logged User ID
        Bundle bundle = getActivity().getIntent().getExtras();
        id = bundle.getInt("id");

        recyclerView =(RecyclerView) getView().findViewById(R.id.recycler_view_friends);
        friends_list = new ArrayList<>();
       // load_data_from_server(id);
        load(id);

        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new FriendsAdapter(getActivity(), friends_list);
        recyclerView.setAdapter(adapter);
    }



    private void load(final int id)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://findplayers.eu/android/friend_list.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
                try{
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        FriendsData data = new FriendsData(jsonObject.getString("profile_image"), jsonObject.getString("username"),  "info", jsonObject.getInt("friend_id"), id);

                        friends_list.add(data);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                String userID = String.valueOf(id);
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", userID);
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

}
