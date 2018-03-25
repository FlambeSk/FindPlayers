package eu.findplayers.app.findplayers;

import android.*;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;

public class TournamentAddActivity extends AppCompatActivity {

    public static final String JSON_ARRAY = "result";
    public static final String gameId  = "id";
    public static final String gameName = "name";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Calendar myCalendar = Calendar.getInstance();
    private JSONArray result;
    Spinner spinner;
    String  tournamentNamePost, aboutTournamentPost, date, tournamentPasswordPost;
    private ArrayList<String> arrayList;
    TextView gameText, showDate;
    Button createTournament, chooseImage, chooseDate;
    ImageView tournamentImage, back_arrow;
    private static final int STORAGE_PERMISSION_CODE = 2342;
    private static final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private Bitmap bitmap;
    ProgressDialog progressDialog;
    EditText tournamentName,playersNumber, aboutTournament, tournamentPassword;
    Integer playersNumberPost, logged_id, game_id, newTournamentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_add);

        //Request storage permissions
       // requestStoragePermission();

        //Getting ID of logged user
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        logged_id = prefs.getInt("login_id", 0);//"No name defined" is the default value.

        spinner = (Spinner) findViewById(R.id.gameList);
        gameText = (TextView)findViewById(R.id.game);
        createTournament = (Button) findViewById(R.id.createTournament);
        tournamentName = (EditText) findViewById(R.id.tournament_name);
        playersNumber = (EditText) findViewById(R.id.players_number);
        aboutTournament = (EditText) findViewById(R.id.aboutTournament);
        chooseDate = (Button) findViewById(R.id.chooseDate);
        showDate = (TextView) findViewById(R.id.showDate);
        tournamentPassword = (EditText) findViewById(R.id.tournament_password);

        arrayList = new ArrayList<String>();
        load_games_to_spinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Setting the values to textviews for a selected item
                //gameText.setText(getGameId(position));
                game_id = Integer.parseInt(getGameId(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gameText.setText("");

            }
        });

        //On back arrow click
        back_arrow = (ImageView) findViewById(R.id.back_arrow) ;
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //On choose button click
      /*  chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
*/

        //On submit button click
        createTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tournamentNamePost = tournamentName.getText().toString();
                String playersNumbers = playersNumber.getText().toString();

                aboutTournamentPost = aboutTournament.getText().toString();
                tournamentPasswordPost = tournamentPassword.getText().toString();

                if (tournamentNamePost.equals("") || playersNumbers.equals("") || aboutTournamentPost.equals("") || tournamentNamePost.equals(""))
                {

                    new SweetAlertDialog(TournamentAddActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Fill all inputs!")
                            .show();
                } else
                {
                    playersNumberPost = Integer.parseInt(playersNumber.getText().toString());
                    uploadNewTournament(tournamentNamePost, game_id, playersNumberPost, aboutTournamentPost, bitmap, logged_id, tournamentPasswordPost, date);
                    progressDialog = new ProgressDialog(TournamentAddActivity.this);
                    progressDialog.setTitle("Uploading");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.show();
                }




            }
        });

        //Choose Date start event
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TournamentAddActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, int month, final int dayOfMonth) {

                final Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                final int mon = month+1;
                TimePickerDialog timePickerDialog = new TimePickerDialog(TournamentAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                date = dayOfMonth + "-" + mon + "-" + year +" " + hourOfDay+":"+ minute + ":00";

                                showDate.setText(String.valueOf(date));
                            }
                        }, hour, minute, true);
                        timePickerDialog.show();



            }
        };


    }

/*
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

    */

    public void uploadNewTournament( final String name, final Integer gameID, final Integer playersCount, final String about, final Bitmap pic, final Integer createdBy, final String password, final String startDate)
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

                    if(code.equals("OK")){

                        progressDialog.dismiss();
                        newTournamentID = jsonObject.getInt("lastID");
                        final String newTournamentImage = jsonObject.getString("lastImage");
                        new SweetAlertDialog(TournamentAddActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText("Tournament was created!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent intent = new Intent(TournamentAddActivity.this, TournamentCardActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        Bundle sendBundle = new Bundle();
                                        sendBundle.putInt("tournament_id", newTournamentID);
                                        sendBundle.putString("tournamentName", name);
                                        sendBundle.putString("tournamentImage", newTournamentImage);
                                        intent.putExtras(sendBundle);
                                        startActivity(intent);
                                    }
                                })
                                .show();

                    } else if (code.equals("WARNING")){
                        progressDialog.dismiss();
                        new SweetAlertDialog(TournamentAddActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oopps!")
                                .setContentText(jsonObject.getString("message"))
                                .show();
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
                //Image to String
               // String image = getStringImage(pic);
                String gameId = gameID.toString();
                String playersCounts = playersCount.toString();
                String created_by = createdBy.toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("createTournament", "true");
                params.put("tournamentName", name);
                params.put("tournamentGame", gameId);
                params.put("playersCount", playersCounts);
                params.put("tournamentAbout", about);
                params.put("image", "Game Image");
                params.put("createdBy", created_by);
                params.put("tournamentPassword", password);
                params.put("startDate", startDate);


                return params;
            }
        };
        MySingleton.getInstance(TournamentAddActivity.this).addToRequestque(stringRequest);
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
       // requestQueue.add(stringRequest);
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
