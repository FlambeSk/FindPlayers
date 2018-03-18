package eu.findplayers.app.findplayers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;

public class TournamentCardActivity extends AppCompatActivity {
    Integer tournamentID;
    String tournament;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_card);
        Bundle bundle = getIntent().getExtras();
        tournamentID = bundle.getInt("tournamentID");
        tournament = tournamentID.toString();


        Toast.makeText(this, tournament, Toast.LENGTH_LONG).show();
    }

    public void getTournament(final Integer tournamentID)
    {
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://findplayers.eu/android/tournament.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //response
                    //Log.d("Response", response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String code = jsonObject.getString("response");
                        if (code.equals("OK"))
                        {

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // progressDialog.dismiss();
                            //error
                            //Log.d("Error.Response", error);
                            // Toast.makeText(MessagesActivity.this, "Error", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {

                    String tournamentIDString = tournamentID.toString();

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("getTournament", "true");
                    params.put("tournamentID", tournamentIDString);



                    return params;
                }
            };
            MySingleton.getInstance(TournamentCardActivity.this).addToRequestque(stringRequest);
            //RequestQueue requestQueue = Volley.newRequestQueue(this);
            // requestQueue.add(stringRequest);
        }
    }
}
