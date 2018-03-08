package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.findplayers.app.findplayers.Data.MessageData;
import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 28.2.2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{


    private static Context context;
    private List<MessageData> messageData;

    public MessageAdapter(Context context, List<MessageData> messageData){
        this.context = context;
        this.messageData = messageData;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageData.get(position).isMyMessage())
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_chat_bubble,parent, false);
            return new ViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_chat_bubble,parent, false);
            return new ViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.message.setText(messageData.get(position).getMessage());
        holder.myMessage = messageData.get(position).isMyMessage();

    }

    @Override
    public int getItemCount() {
        return messageData.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        public TextView message;
        public boolean myMessage;

        public ViewHolder(View itemView) {
            super(itemView);
             message = (TextView)itemView.findViewById(R.id.txt_msg);
        }
    }
}
