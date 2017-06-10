package model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dissertation.findtheclue.R;

import java.util.List;

/**
 * Created by Vlad on 10-Jun-17.
 */
public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.MyViewHolder> {
    private List<GamesContent.GameItem> gameItems;

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        public ImageView gamePicture;
        public TextView gameName, gameLocation, gameDifficulty, gameDuration;

        public MyViewHolder(View view) {
            super(view);
            //gamePicture = (ImageView) view.findViewById(R.id.game_picture);
            gameName = (TextView) view.findViewById(R.id.game_name);
            gameLocation = (TextView) view.findViewById(R.id.game_location);
            gameDifficulty = (TextView) view.findViewById(R.id.game_difficulty);
            gameDuration = (TextView) view.findViewById(R.id.game_duration);
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
        //to-do  get pic from byte array
        //holder.gamePicture.setImageBitmap(game.getPicture());
        holder.gameName.setText(game.getName());
        holder.gameLocation.setText(game.getCity());
        holder.gameDifficulty.setText(Integer.toString(game.getDifficulty()));
        holder.gameDuration.setText(Integer.toString(game.getDuration()));
    }

    @Override
    public int getItemCount() {
        return gameItems.size();
    }
}
