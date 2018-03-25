package eu.findplayers.app.findplayers.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.findplayers.app.findplayers.Data.MyData;
import eu.findplayers.app.findplayers.ForLogin.MySingleton;
import eu.findplayers.app.findplayers.GamePageActivity;
import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 28.2.2018.
 */

public class AllGamesAdapter extends RecyclerView.Adapter<AllGamesAdapter.ViewHolder> {
    private static Context context;
    private List<MyData> my_data;

    public AllGamesAdapter(Context context, List<MyData> my_data) {
        this.context = context;
        this.my_data = my_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.id_game = String.valueOf(my_data.get(position).getId());
        holder.loginned_id = String.valueOf(my_data.get(position).getLoggin_id());
        holder.description.setText(my_data.get(position).getName());
        Glide.with(context).load(my_data.get(position).getSmall_image()).into(holder.imageView);

        //On menu (3dots) click
        holder.more_img_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.add:
                                sendOnServer(my_data.get(position).getId(), my_data.get(position).getLoggin_id());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.getMenuInflater().inflate(R.menu.add_game_menu,popupMenu.getMenu());
                popupMenu.show();
            }
        });

        //On image click
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer id_game_int = my_data.get(position).getId();
                String game_name = my_data.get(position).getName();
                String image = my_data.get(position).getSmall_image();
                Intent intent = new Intent(context, GamePageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("game_id", id_game_int);
                bundle.putString("game_name", game_name);
                bundle.putString("game_image", image);
                intent.putExtras(bundle);


                    context.startActivity(intent);



            }
        });

    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }

    public void filterList(ArrayList<MyData> filteredLIst)
    {
        my_data = filteredLIst;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView description;
        public ImageView imageView,more_img_button;
        public String id_game;
        public String loginned_id;
        public ViewHolder(final View itemView)
        {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            more_img_button = (ImageView) itemView.findViewById(R.id.more_img_button);

        }

    }

    public static void sendOnServer(final Integer id, final Integer loggined_id)
    {
        StringRequest stringRequest =  new StringRequest(com.android.volley.Request.Method.POST, "https://findplayers.eu/android/game.php?id="+id+"&user_id="+loggined_id, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String msg = jsonObject.getString("msg");

                    if(msg.equals("Error"))
                    {
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Already in Profile")
                                .show();
                    } else {
                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText("Game was Added!")
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                error.printStackTrace();

            }
        })
        {
        };
        MySingleton.getInstance(context).addToRequestque(stringRequest);
    }
}
