package eu.findplayers.app.findplayers.Fragments;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.MainActivity;
import eu.findplayers.app.findplayers.ProfileActivity;
import eu.findplayers.app.findplayers.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    ImageView profile_image;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Home");

        profile_image = (ImageView) getActivity().findViewById(R.id.profile_image);
        //Getting logged user and set header
        final Bundle bundle = getActivity().getIntent().getExtras();

        //uklada hodnoty na autologin
        editor = this.getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt("login_id", bundle.getInt("id"));
        editor.putString("login_name", bundle.getString("name"));
        editor.putString("login_image", bundle.getString("profile_image"));
        editor.apply();

        Picasso.with(getContext()).load(bundle.getString("profile_image")).transform(new CropCircleTransformation()).into(profile_image);

        //Click on Profile user image
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                bundle.putInt("profile_id", bundle.getInt("id"));
                bundle.putString("profile_name", bundle.getString("name"));
                bundle.putString("profile_image", bundle.getString("profile_image"));
                intent.putExtras(bundle);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), (View)profile_image, "profileImageTransition");
                    getActivity().startActivity(intent, options.toBundle());
                } else
                {
                    getActivity().startActivity(intent);
                }

            }
        });

        if (getActivity().getIntent() != null)
        {
            try{
                if (bundle.getString("friend_name") != null)
                {
                    JSONObject object = new JSONObject(bundle.getString("friend_name"));
                    String user_name = object.optString("friend_name");
                    Toast.makeText(getContext(), user_name, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e){
                e.printStackTrace();
            }

        }




    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }




}
