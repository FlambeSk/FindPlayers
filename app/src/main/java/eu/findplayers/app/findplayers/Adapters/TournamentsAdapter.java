package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import eu.findplayers.app.findplayers.Data.TournamentData;
import eu.findplayers.app.findplayers.GamePageActivity;
import eu.findplayers.app.findplayers.R;
import eu.findplayers.app.findplayers.TournamentCardActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by CWSK_DEV on 3/19/2018.
 */

public class TournamentsAdapter extends RecyclerView.Adapter<TournamentsAdapter.ViewHolder> {

    private static Context context;
    private List<TournamentData> tournamentData;

    public TournamentsAdapter(Context context, List<TournamentData> tournamentData) {
        this.context = context;
        this.tournamentData = tournamentData;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tournament_list,parent,false);



        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String TimeHelp;

        //Glide.with(context).load(tournamentData.get(position).getTournamentImage()).into(holder.tournamentImage);
        Picasso.with(context).load(tournamentData.get(position).getTournamentImage()).transform(new RoundedCornersTransformation(10,5)).into(holder.tournamentImage);
        holder.tournamentName.setText(tournamentData.get(position).getTournamnetName());
        holder.tournamentCount.setText(tournamentData.get(position).getPlayersCount());
        holder.tournamentID = tournamentData.get(position).getTournamentID();
        //holder.starAt.setText(tournamentData.get(position).getStartAt());
        TimeHelp = tournamentData.get(position).getStartAt();
        Long TimeHelpInt = Long.valueOf(TimeHelp);
        TimeHelpInt = TimeHelpInt*1000;

        CharSequence a = DateUtils.getRelativeTimeSpanString(TimeHelpInt,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS, 0);
        holder.starAt.setText(a);

        holder.tournamentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TournamentCardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("tournament_id", tournamentData.get(position).getTournamentID());
                bundle.putString("tournamentName", tournamentData.get(position).getTournamnetName());
                bundle.putString("tournamentImage", tournamentData.get(position).getTournamentImage());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tournamentData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tournamentName, tournamentCount, starAt;
        public ImageView tournamentImage;
        public Integer tournamentID;
        public ViewHolder(final View itemView)
        {
            super(itemView);
            tournamentName = (TextView) itemView.findViewById(R.id.tournamentName);
            tournamentCount = (TextView)itemView.findViewById(R.id.tournamentCount);
            tournamentImage = (ImageView) itemView.findViewById(R.id.tournamentImage);
            starAt = (TextView) itemView.findViewById(R.id.startAt);

        }

    }
}
