package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eu.findplayers.app.findplayers.Data.GroupChatData;
import eu.findplayers.app.findplayers.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by DOMA on 27.3.2018.
 */

public class TournamentMessageAdapter extends RecyclerView.Adapter<TournamentMessageAdapter.ViewHolder>{

    private  static Context context;
    private List<GroupChatData> groupChatData;

    public TournamentMessageAdapter(Context context, List<GroupChatData> groupChatData){
        this.context = context;
        this.groupChatData = groupChatData;
    }

    @Override
    public int getItemViewType(int position) {
        if (groupChatData.get(position).isMyMessage())
        {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_tournament_chat_bubble,parent, false);
            return new ViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_tournament_chat_bubble,parent, false);
            return new ViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.message.setText(groupChatData.get(position).getMessage());
        holder.myMessage = groupChatData.get(position).isMyMessage();
        Picasso.with(context).load(groupChatData.get(position).getFromImage()).transform(new CropCircleTransformation()).into(holder.message_picture);
    }

    @Override
    public int getItemCount() {
        return groupChatData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView message;
        public boolean myMessage;
        public ImageView message_picture;

        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.txt_msg);
            message_picture = (ImageView)itemView.findViewById(R.id.message_picture);
        }
    }
}
