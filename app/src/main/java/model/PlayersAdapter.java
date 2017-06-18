package model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dissertation.findtheclue.R;

import java.util.List;

/**
 * Created by Vlad on 17-Jun-17.
 */
public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.MyViewHolder> {
    private List<PlayersContent.PlayerItem> playerItems;

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        public ImageView profileImage;
        public TextView userName, userScore;

        public MyViewHolder(View view) {
            super(view);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            userName = (TextView) view.findViewById(R.id.user_name);
            userScore = (TextView) view.findViewById(R.id.user_score);
        }
    }

    public PlayersAdapter(List<PlayersContent.PlayerItem> playerItems){
        this.playerItems = playerItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_players_recycler_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlayersContent.PlayerItem player = playerItems.get(position);

        if(!player.getProfile_picture().isEmpty()) {
            byte[] decodedByte = Base64.decode(player.getProfile_picture(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            holder.profileImage.setImageBitmap(bmp);
        }

        holder.userName.setText(player.getFirst_name());
        holder.userScore.setText(Double.toString(player.getScore()));
    }

    @Override
    public int getItemCount() {
        return playerItems.size();
    }
}
