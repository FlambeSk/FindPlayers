package eu.findplayers.app.findplayers;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

public class RegistrationActivity extends AppCompatActivity {

    Button reg_bn;
    EditText Name,Email,Password,ConPassword;
    String name,email,password,conpass, country_id;
    Spinner spinner_countries;
    AlertDialog.Builder builder;
    String reg_url = "http://findplayers.eu/android/register.php";

    //Countries
    String[] countries = new String[]{"Afghanistan","ÅlandIslands","Albania","Algeria","AmericanSamoa","Andorra","Angola","Anguilla","Antarctica","AntiguaandBarbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia,PlurinationalStateof","Bonaire,SintEustatiusandSaba","BosniaandHerzegovina","Botswana","BouvetIsland","Brazil","BritishIndianOceanTerritory","BruneiDarussalam","Bulgaria","BurkinaFaso","Burundi","Cambodia","Cameroon","Canada","CapeVerde","CaymanIslands","CentralAfricanRepublic","Chad","Chile","China","ChristmasIsland","Cocos(Keeling)Islands","Colombia","Comoros","Congo","Congo,theDemocraticRepublicofthe","CookIslands","CostaRica","Côted'Ivoire","Croatia","Cuba","Curaçao","Cyprus","CzechRepublic","Denmark","Djibouti","Dominica","DominicanRepublic","Ecuador","Egypt","ElSalvador","EquatorialGuinea","Eritrea","Estonia","Ethiopia","FalklandIslands(Malvinas)","FaroeIslands","Fiji","Finland","France","FrenchGuiana","FrenchPolynesia","FrenchSouthernTerritories","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guadeloupe","Guam","Guatemala","Guernsey","Guinea","Guinea-Bissau","Guyana","Haiti","HeardIslandandMcDonaldIslands","HolySee(VaticanCityState)","Honduras","HongKong","Hungary","Iceland","India","Indonesia","Iran,IslamicRepublicof","Iraq","Ireland","IsleofMan","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Korea,DemocraticPeople'sRepublicof","Korea,Republicof","Kuwait","Kyrgyzstan","LaoPeople'sDemocraticRepublic","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macao","Macedonia,theformerYugoslavRepublicof","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","MarshallIslands","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Micronesia,FederatedStatesof","Moldova,Republicof","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar","Namibia","Nauru","Nepal","Netherlands","NewCaledonia","NewZealand","Nicaragua","Niger","Nigeria","Niue","NorfolkIsland","NorthernMarianaIslands","Norway","Oman","Pakistan","Palau","PalestinianTerritory,Occupied","Panama","PapuaNewGuinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","PuertoRico","Qatar","Réunion","Romania","RussianFederation","Rwanda","SaintBarthélemy","SaintHelena,AscensionandTristandaCunha","SaintKittsandNevis","SaintLucia","SaintMartin(Frenchpart)","SaintPierreandMiquelon","SaintVincentandtheGrenadines","Samoa","SanMarino","SaoTomeandPrincipe","SaudiArabia","Senegal","Serbia","Seychelles","SierraLeone","Singapore","SintMaarten(Dutchpart)","Slovakia","Slovenia","SolomonIslands","Somalia","SouthAfrica","SouthGeorgiaandtheSouthSandwichIslands","SouthSudan","Spain","SriLanka","Sudan","Suriname","SvalbardandJanMayen","Swaziland","Sweden","Switzerland","SyrianArabRepublic","Taiwan,ProvinceofChina","Tajikistan","Tanzania,UnitedRepublicof","Thailand","Timor-Leste","Togo","Tokelau","Tonga","TrinidadandTobago","Tunisia","Turkey","Turkmenistan","TurksandCaicosIslands","Tuvalu","Uganda","Ukraine","UnitedArabEmirates","UnitedKingdom","UnitedStates","UnitedStatesMinorOutlyingIslands","Uruguay","Uzbekistan","Vanuatu","Venezuela,BolivarianRepublicof","VietNam","VirginIslands,British","VirginIslands,U.S.","WallisandFutuna","WesternSahara","Yemen","Zambia","Zimbabwe"};
    String[] country_code = new String[]{"AF","AX","AL","DZ","AS","AD","AO","AI","AQ","AG","AR","AM","AW","AU","AT","AZ","BS","BH","BD","BB","BY","BE","BZ","BJ","BM","BT","BO","BQ","BA","BW","BV","BR","IO","BN","BG","BF","BI","KH","CM","CA","CV","KY","CF","TD","CL","CN","CX","CC","CO","KM","CG","CD","CK","CR","CI","HR","CU","CW","CY","CZ","DK","DJ","DM","DO","EC","EG","SV","GQ","ER","EE","ET","FK","FO","FJ","FI","FR","GF","PF","TF","GA","GM","GE","DE","GH","GI","GR","GL","GD","GP","GU","GT","GG","GN","GW","GY","HT","HM","VA","HN","HK","HU","IS","IN","ID","IR","IQ","IE","IM","IL","IT","JM","JP","JE","JO","KZ","KE","KI","KP","KR","KW","KG","LA","LV","LB","LS","LR","LY","LI","LT","LU","MO","MK","MG","MW","MY","MV","ML","MT","MH","MQ","MR","MU","YT","MX","FM","MD","MC","MN","ME","MS","MA","MZ","MM","NA","NR","NP","NL","NC","NZ","NI","NE","NG","NU","NF","MP","NO","OM","PK","PW","PS","PA","PG","PY","PE","PH","PN","PL","PT","PR","QA","RE","RO","RU","RW","BL","SH","KN","LC","MF","PM","VC","WS","SM","ST","SA","SN","RS","SC","SL","SG","SX","SK","SI","SB","SO","ZA","GS","SS","ES","LK","SD","SR","SJ","SZ","SE","CH","SY","TW","TJ","TZ","TH","TL","TG","TK","TO","TT","TN","TR","TM","TC","TV","UG","UA","AE","GB","US","UM","UY","UZ","VU","VE","VN","VG","VI","WF","EH","YE","ZM","ZW"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        reg_bn = (Button)findViewById(R.id.bn_reg);
        Name = (EditText)findViewById(R.id.reg_name);
        Email = (EditText)findViewById(R.id.reg_email);
        Password = (EditText)findViewById(R.id.reg_password);
        ConPassword = (EditText)findViewById(R.id.reg_con_password);
        spinner_countries = (Spinner)findViewById(R.id.countries);

        //Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        spinner_countries.setAdapter(adapter);
        //Setting spinner on country ID
        spinner_countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country_id = String.valueOf(country_code[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Click on registration button
        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = Name.getText().toString();
                email = Email.getText().toString();
                password = Password.getText().toString();
                conpass = ConPassword.getText().toString();

                //Kontrolujem ci su polia prazdne
                if (name.equals("") || email.equals("") || password.equals("") || conpass.equals(""))
                {
                    //Get string resourse
                    String something_went_wrong = getString(R.string.something_went_wrong);
                    String please_fill_all_fields = getString(R.string.please_fill_all_fields);
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                    builder.setTitle(something_went_wrong);
                    builder.setMessage(please_fill_all_fields)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    //Check, if passwords are equals
                    if (!(password.equals(conpass)))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                        builder.setTitle("Something went wrong...");
                        builder.setMessage("Passwords doesn't match!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                        Password.setText("");
                        ConPassword.setText("");
                    }
                    else
                    {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    String message = jsonObject.getString("message");

                                    //Sprava po zaregistrovani zo servera
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                                    builder.setTitle("Server Response");
                                    builder.setMessage(message)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //do things
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();


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
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("username", name);
                                params.put("email", email);
                                params.put("password", password);
                                params.put("country", country_id);
                                return params;
                            }
                        };

                        MySingleton.getInstance(RegistrationActivity.this).addToRequestque(stringRequest);
                    }
                }
            }
        });

    }
}
