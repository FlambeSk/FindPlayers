package eu.findplayers.app.findplayers.Fragments;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.findplayers.app.findplayers.Adapters.FriendsAdapter;
import eu.findplayers.app.findplayers.Data.FriendsData;
import eu.findplayers.app.findplayers.R;
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
        load_data_from_server(id);

        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new FriendsAdapter(getActivity(), friends_list);
        recyclerView.setAdapter(adapter);
    }

    private void load_data_from_server(final int id)
    {
        @SuppressLint("StaticFieldLeak")AsyncTask<Integer, Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("http://findplayers.eu/android/friend_list.php?id="+id).build();

                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i=0; i<array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);

                        FriendsData data = new FriendsData(object.getString("profile_image"), object.getString("username"),  "info", object.getInt("friend_id"), id);

                        friends_list.add(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    System.out.println("End of content");
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
            }
        };
        task.execute(id);
    }

}
