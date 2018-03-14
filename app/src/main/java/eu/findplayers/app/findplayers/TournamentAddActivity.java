package eu.findplayers.app.findplayers;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    Button createTournament, chooseImage;
    ImageView tournamentImage;
    private static final int STORAGE_PERMISSION_CODE = 2342;
    private static final int PICK_IMAGE_REQUEST = 22;
    private  static final String UPLOAD_URL = "https://findplayers.eu/android/tournament.php";
    private Uri filePath;
    private Bitmap bitmap;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_add);

        //Request storage permissions
        requestStoragePermission();

        spinner = (Spinner) findViewById(R.id.gameList);
        gameText = (TextView)findViewById(R.id.game);
        createTournament = (Button) findViewById(R.id.createTournament);
        chooseImage = (Button) findViewById(R.id.chooseImage);
        tournamentImage = (ImageView) findViewById(R.id.tournamentImage);

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

        //On choose button click
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        //On submit button click
        createTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadNewTournament(bitmap);
               progressDialog = new ProgressDialog(TournamentAddActivity.this);
               progressDialog.setTitle("Uploading");
               progressDialog.setMessage("Please wait...");
               progressDialog.show();

            }
        });



    }

    //Storage request
    private void requestStoragePermission(){
       if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
           return;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                filePath = data.getData();
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    tournamentImage.setImageBitmap(bitmap);
                } catch (IOException e) {

                }
        } else {
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    private void uploadTournament() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(TournamentAddActivity.this, "Try", Toast.LENGTH_SHORT).show();

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");

                    if (message.equals("Uploaded")){
                        Toast.makeText(TournamentAddActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TournamentAddActivity.this, "Not Uploaded", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String image = getStringImage(bitmap);
                Map<String, String> params = new HashMap<String, String>();
                params.put("createTournament", "true");
                params.put("image", image);

                return params;
            }
        };
       // RequestQueue requestQueue = Volley.newRequestQueue(this);
       // requestQueue.add(stringRequest);
        MySingleton.getInstance(TournamentAddActivity.this).addToRequestque(stringRequest);
    }

    public void uploadNewTournament( final Bitmap pic)
    {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/tournament.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String message = jsonObject.getString("message");

                    if (message.equals("Uploaded")){
                        Toast.makeText(TournamentAddActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TournamentAddActivity.this, "Not Uploaded", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //response
                Log.d("Response", response);
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
                String image = getStringImage(pic);

                Map<String, String> params = new HashMap<String, String>();
                params.put("createTournament", "true");
                params.put("image", image);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
