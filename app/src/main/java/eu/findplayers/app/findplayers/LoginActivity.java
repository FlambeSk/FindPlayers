package eu.findplayers.app.findplayers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    TextView textView;
    Button login_button, btn_games;
    EditText UserName, Password;
    String username,password;
    String login_url = "https://findplayers.eu/android/login.php";
    AlertDialog.Builder builder;
    public String name_after_login, password_after_login, image_after_login;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Getting Variables for Autologin
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        name_after_login = prefs.getString("name_login", null);//"No name defined" is the default value.
        password_after_login = prefs.getString("password_login", "No password");

        //Check, if user is login in
        if (name_after_login != null)
        {
            sendOnServer(name_after_login, password_after_login);
        } else
        {
            Toast.makeText(LoginActivity.this, "You need to login", Toast.LENGTH_SHORT).show();
        }

        //Po kliknuti na text Registration otvori novu aktivitu
        textView = (TextView)findViewById(R.id.reg_txt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));

            }
        });

        builder = new AlertDialog.Builder(LoginActivity.this);
        login_button = (Button)findViewById(R.id.bn_login);
        UserName = (EditText)findViewById(R.id.login_name);
        Password = (EditText)findViewById(R.id.login_password);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = UserName.getText().toString();
                password = Password.getText().toString();

                if (username.equals("") || password.equals(""))
                {
                    builder.setTitle("Something went wrong");
                    displayAlert("Enter a valid username and password");

                }
                else
                {

                    sendOnServer(username, password);
                }
            }
        });

        //Save autologin
        String restoredText = prefs.getString("text", null);
        SharedPreferences prefss = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        name_after_login = prefss.getString("name_login", "No name defined");//"No name defined" is the default value.
        password_after_login = prefss.getString("password_login", "No password");


    }

    public void displayAlert(String message)
    {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // UserName.setText("");
                Password.setText("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                        displayAlert("Email or password is wrong !");

                    } else {
                        //uklada hodnoty na autologin
                        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("name_login", username);
                        editor.putString("password_login", password);
                        editor.apply();

                        //  Intent intent = new Intent(MainActivity.this, LoginSuccess.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", jsonObject.getInt("id"));
                        bundle.putString("profile_image", jsonObject.getString("profile_image"));
                        bundle.putString("name", jsonObject.getString("name"));
                        bundle.putString("email", jsonObject.getString("email"));
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
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(LoginActivity.this).addToRequestque(stringRequest);
    }
}
