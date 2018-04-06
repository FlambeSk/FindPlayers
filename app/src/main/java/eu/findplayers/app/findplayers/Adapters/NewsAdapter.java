package eu.findplayers.app.findplayers.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.List;

import eu.findplayers.app.findplayers.Data.NewsData;
import eu.findplayers.app.findplayers.R;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private static Context context;
    private List<NewsData> newsData;

    public NewsAdapter(Context context, List<NewsData> newsData)
    {
        this.context = context;
        this.newsData = newsData;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (newsData.get(position).getType().equals("text"))
        {
            return 1;
        }
        else if (newsData.get(position).getType().equals("image")){
            return 2;
        } else
        {
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_text, parent,  false);
            return new ViewHolder(itemView);
        } else if (viewType == 2)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_image, parent,  false);
            return new ViewHolder(itemView);
        } else
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_text, parent,  false);
            return new ViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.name.setText(newsData.get(position).getFromName());
        holder.message.setText(newsData.get(position).getMessage());
        //Glide.with(context).load(newsData.get(position).getImage()).into(holder.image);

        Picasso.with(context).load(newsData.get(position).getFromImage()).transform(new CropCircleTransformation()).into(holder.news_user_image);
        final Uri uri = Uri.parse(newsData.get(position).getImage());
        holder.draweeView.setImageURI(uri);

        final String[] a={newsData.get(position).getImage()};

        //On image lick
        holder.draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageViewer.Builder(context, a).setStartPosition(0).show();

            }
        });

        //set time
        String TimeHelp = newsData.get(position).getTimestamp();
        Long TimeHelpInt = Long.valueOf(TimeHelp);
        TimeHelpInt = TimeHelpInt*1000;

        // CharSequence a = DateUtils.getRelativeDateTimeString(context,TimeHelpInt,DateUtils.SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS, 0);
        CharSequence t = DateUtils.getRelativeTimeSpanString(TimeHelpInt,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS, 0);
        holder.timestamp.setText(t);

    }

    @Override
    public int getItemCount() {
        return newsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name, message, timestamp;
        public SimpleDraweeView draweeView;
        public String type;
        public ImageView news_user_image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.image2);
            draweeView.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
            news_user_image = (ImageView) itemView.findViewById(R.id.news_user_image);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }
}