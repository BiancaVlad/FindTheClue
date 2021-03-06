package model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.dissertation.findtheclue.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Vlad on 10-Jun-17.
 */
public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.MyViewHolder> {
    private List<GamesContent.GameItem> gameItems;

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        public ImageView gamePicture;
        public TextView gameName, gameLocation, gameDifficulty;

        public MyViewHolder(View view) {
            super(view);
            gamePicture = (ImageView) view.findViewById(R.id.game_picture);
            gameName = (TextView) view.findViewById(R.id.game_name);
            gameLocation = (TextView) view.findViewById(R.id.game_location);
            gameDifficulty = (TextView) view.findViewById(R.id.game_difficulty);
        }
    }

    public GamesAdapter(List<GamesContent.GameItem> gameItems){
        this.gameItems = gameItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.games_recycler_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GamesContent.GameItem game = gameItems.get(position);
        Context context = holder.gamePicture.getContext();

        if(game.getPicture() != null && !game.getPicture().isEmpty()) {
            Uri uri = Uri.parse(game.getPicture());
            if(uri != null && URLUtil.isValidUrl(uri.toString())) {
                try {
                    Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(holder.gamePicture);
                } catch (Exception e) {
                    Bitmap icon = BitmapFactory.decodeResource(holder.gamePicture.getContext().getResources(),
                            R.drawable.game_default);
                    holder.gamePicture.setImageBitmap(icon);
                    // this set the default img source if the path provided in .load is null or some error happened on download.
                }
            }
            else
            {
                Bitmap icon = BitmapFactory.decodeResource(holder.gamePicture.getContext().getResources(),
                        R.mipmap.investi4);
                holder.gamePicture.setImageBitmap(icon);
            }
        }
        else
        {
            Bitmap icon = BitmapFactory.decodeResource(holder.gamePicture.getContext().getResources(),
                    R.mipmap.investi4);
            holder.gamePicture.setImageBitmap(icon);
        }

        holder.gameName.setText(game.getName());
        holder.gameLocation.setText(game.getCity());

        String level = this.GetDifficulty(game.getDifficulty());
        String time = this.GetDuration(game.getDuration());
        holder.gameDifficulty.setText(level + " - " + time);
    }

    public static String GetDifficulty(int difficulty)
    {
        switch (difficulty)
        {
            case 1: return "Beginner";
            case 2: return "Amateur";
            case 3: return "Intermediate";
            case 4: return "Advanced";
            default: return "Expert";
        }
    }

    public static String GetDuration(int duration)
    {
        if(duration < 60)
        {
            return Integer.toString(duration) + "m";
        }

        if(duration % 60 == 0)
        {
            return Integer.toString(duration/60) + "h";
        }

        int hours = duration/60;
        int mins = duration - (hours * 60);
        return Integer.toString(hours) + "h " + Integer.toString(mins) + "m";
    }

    @Override
    public int getItemCount() {
        return gameItems.size();
    }
}
