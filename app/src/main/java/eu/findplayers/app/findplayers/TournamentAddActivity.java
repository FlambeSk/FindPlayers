package eu.findplayers.app.findplayers;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.ForLogin.MySingleton;

public class TournamentAddActivity extends AppCompatActivity {

    public static final String JSON_ARRAY = "result";
    public static final String gameId  = "id";
    public static final String gameName = "name";
    private JSONArray result;
    Spinner spinner;
    String  game_id;
    private ArrayList<String> arrayList;
    TextView gameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_add);

        spinner = (Spinner) findViewById(R.id.gameList);
        gameText = (TextView)findViewById(R.id.game);
        arrayList = new ArrayList<String>();
        load_games_to_spinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Setting the values to textviews for a selected item
                //gameText.setText(getGameId(position));
                game_id = getGameId(position);
                Toast.makeText(TournamentAddActivity.this, game_id, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gameText.setText("");

            }
        });



    }


    private void load_games_to_spinner()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://findplayers.eu/android/game.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray(JSON_ARRAY);
                    empdetails(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        },
                new Response.ErrorListener() {
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


                Map<String, String> params = new HashMap<String, String>();
                params.put("gameSpinner", "a");

                return params;
            }
        };
        MySingleton.getInstance(TournamentAddActivity.this).addToRequestque(stringRequest);
    }

    private void empdetails(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                arrayList.add(json.getString(gameName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // arrayList.add(0,"Select Employee");
        spinner.setAdapter(new ArrayAdapter<String>(TournamentAddActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList));
    }

    private String getGameId(int position) {
        String ID = "";
        try{
            JSONObject json = result.getJSONObject(position);
            ID = json.getString(gameId);

        }  catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return ID;
    }

}
