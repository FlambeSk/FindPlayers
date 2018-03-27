package eu.findplayers.app.findplayers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.Adapters.AllGamesAdapter;
import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AllGamesAdapter adapter;
    private List<MyData> data_list;
    private EditText SearchText;
    private ImageView back_arrow;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        SearchText = (EditText)findViewById(R.id.SearchGame);
        back_arrow = (ImageView)findViewById(R.id.back_arrow);
        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setTitle("Loading games");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        games();
        //load_data_from_server(0);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_games);
        data_list = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AllGamesAdapter(this,data_list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        //Search
        SearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

                filter(editable.toString());
                adapter.notifyDataSetChanged();

            }
        });


        //Back Button
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                finish();
            }
        });

        adapter.notifyDataSetChanged();
    }

    private void filter(String text)
    {
        ArrayList<MyData> filtredList = new ArrayList<>();
        for (MyData item : data_list)
        {
            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filtredList.add(item);
            }
        }
        adapter.filterList(filtredList);
    }

    private void load_data_from_server(final int id)
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://findplayers.eu/android/games.php").build();

                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i=0; i<array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);

                        MyData data = new MyData(object.getInt("id"), object.getString("name"), object.getString("small_image"),0);

                        data_list.add(data);
                        adapter.notifyDataSetChanged();
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


    private void games()
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/games.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
                try{
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        MyData data = new MyData(jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("small_image"), 0);

                        data_list.add(data);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("allGames", "true");
                return params;
            }
        };
        MySingleton.getInstance(SearchActivity.this).addToRequestque(stringRequest);
    }
}
