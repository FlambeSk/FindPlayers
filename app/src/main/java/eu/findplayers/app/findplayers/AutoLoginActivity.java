package eu.findplayers.app.findplayers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
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

import eu.findplayers.app.findplayers.ForLogin.MySingleton;

public class AutoLoginActivity extends AppCompatActivity {

    String login_url = "https://findplayers.eu/android/login.php";
    AlertDialog.Builder builder;
    public String name_after_login, password_after_login;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        name_after_login = prefs.getString("name_login", null);//"No name defined" is the default value.
        password_after_login = prefs.getString("password_login", "No password");

        if(name_after_login!= null)
        {
            sendOnServer(name_after_login, password_after_login);

        } else {
            Intent intent = new Intent(AutoLoginActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        }

    }

    public void sendOnServer(final String username, final String password)
    {
        StringRequest stringRequest =  new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");

                    if(code.equals("login_failed")){

                        builder.setTitle("Something went wrong");

                    } else {
                        //uklada hodnoty na autologin
                        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("name_login", username);
                        editor.putString("password_login", password);
                        editor.apply();

                        //  Intent intent = new Intent(MainActivity.this, LoginSuccess.class);
                        Intent intent = new Intent(AutoLoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", jsonObject.getInt("id"));
                        bundle.putString("name", jsonObject.getString("name"));
                        bundle.putString("email", jsonObject.getString("email"));
                        bundle.putString("profile_image", jsonObject.getString("profile_image"));
                        bundle.putString("country", jsonObject.getString("country"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AutoLoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                error.printStackTrace();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<String, String>();
                //Tu posielam do stringTequest udaje z editTextov
                params.put("email", username);
                params.put("password", password);
                return params;
            }
        };
        MySingleton.getInstance(AutoLoginActivity.this).addToRequestque(stringRequest);
    }
}
