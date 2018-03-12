package eu.findplayers.app.findplayers;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.findplayers.app.findplayers.ForLogin.MySingleton;

public class TournamentAddActivity extends AppCompatActivity {

    Spinner gameList;
    TextView gametext;
    String java = "ahoj,gefo";
    ArrayList<String> list;
    ArrayList<String> listItem = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String[] games ;
    List<String> bbb = new ArrayList<String>();
    String[] countries = new String[]{"Afghanistan","ÅlandIslands","Albania","Algeria","AmericanSamoa","Andorra","Angola","Anguilla","Antarctica","AntiguaandBarbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia,PlurinationalStateof","Bonaire,SintEustatiusandSaba","BosniaandHerzegovina","Botswana","BouvetIsland","Brazil","BritishIndianOceanTerritory","BruneiDarussalam","Bulgaria","BurkinaFaso","Burundi","Cambodia","Cameroon","Canada","CapeVerde","CaymanIslands","CentralAfricanRepublic","Chad","Chile","China","ChristmasIsland","Cocos(Keeling)Islands","Colombia","Comoros","Congo","Congo,theDemocraticRepublicofthe","CookIslands","CostaRica","Côted'Ivoire","Croatia","Cuba","Curaçao","Cyprus","CzechRepublic","Denmark","Djibouti","Dominica","DominicanRepublic","Ecuador","Egypt","ElSalvador","EquatorialGuinea","Eritrea","Estonia","Ethiopia","FalklandIslands(Malvinas)","FaroeIslands","Fiji","Finland","France","FrenchGuiana","FrenchPolynesia","FrenchSouthernTerritories","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guadeloupe","Guam","Guatemala","Guernsey","Guinea","Guinea-Bissau","Guyana","Haiti","HeardIslandandMcDonaldIslands","HolySee(VaticanCityState)","Honduras","HongKong","Hungary","Iceland","India","Indonesia","Iran,IslamicRepublicof","Iraq","Ireland","IsleofMan","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Korea,DemocraticPeople'sRepublicof","Korea,Republicof","Kuwait","Kyrgyzstan","LaoPeople'sDemocraticRepublic","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macao","Macedonia,theformerYugoslavRepublicof","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","MarshallIslands","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Micronesia,FederatedStatesof","Moldova,Republicof","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar","Namibia","Nauru","Nepal","Netherlands","NewCaledonia","NewZealand","Nicaragua","Niger","Nigeria","Niue","NorfolkIsland","NorthernMarianaIslands","Norway","Oman","Pakistan","Palau","PalestinianTerritory,Occupied","Panama","PapuaNewGuinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","PuertoRico","Qatar","Réunion","Romania","RussianFederation","Rwanda","SaintBarthélemy","SaintHelena,AscensionandTristandaCunha","SaintKittsandNevis","SaintLucia","SaintMartin(Frenchpart)","SaintPierreandMiquelon","SaintVincentandtheGrenadines","Samoa","SanMarino","SaoTomeandPrincipe","SaudiArabia","Senegal","Serbia","Seychelles","SierraLeone","Singapore","SintMaarten(Dutchpart)","Slovakia","Slovenia","SolomonIslands","Somalia","SouthAfrica","SouthGeorgiaandtheSouthSandwichIslands","SouthSudan","Spain","SriLanka","Sudan","Suriname","SvalbardandJanMayen","Swaziland","Sweden","Switzerland","SyrianArabRepublic","Taiwan,ProvinceofChina","Tajikistan","Tanzania,UnitedRepublicof","Thailand","Timor-Leste","Togo","Tokelau","Tonga","TrinidadandTobago","Tunisia","Turkey","Turkmenistan","TurksandCaicosIslands","Tuvalu","Uganda","Ukraine","UnitedArabEmirates","UnitedKingdom","UnitedStates","UnitedStatesMinorOutlyingIslands","Uruguay","Uzbekistan","Vanuatu","Venezuela,BolivarianRepublicof","VietNam","VirginIslands,British","VirginIslands,U.S.","WallisandFutuna","WesternSahara","Yemen","Zambia","Zimbabwe"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_add);

        gametext =(TextView)findViewById(R.id.game);
        gameList = (Spinner) findViewById(R.id.gameList);
        load_games_to_spinner();

        //Spinner
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, bbb);
        gameList.setAdapter(adapter);

        gameList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gametext.setText(String.valueOf(bbb));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void load_games_to_spinner()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://findplayers.eu/android/game.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //response
                Log.d("Response", response);
                try{
                    JSONArray jsonArray = new JSONArray(response);

                    games = new String[jsonArray.length()];
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String mesage = jsonObject.getString("name");
                       // Toast.makeText(TournamentAddActivity.this, mesage, Toast.LENGTH_SHORT).show();
                        listItem.add(mesage);

                        games[i] = mesage;


                    }

                    for(int i=0;i<games.length;i++){
                        bbb.add(games[i]);
                    }

//                    listItem.addAll(list);
                }catch (JSONException e) {
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

}
