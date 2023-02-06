package com.example.musicplayer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;


public class songAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //members
    Context context;
    List<Song> songs;

    //constructors


    public songAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate song row layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_item, parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    //current song and view holder
        Song song = songs.get(position);
        SongViewHolder viewHolder = (SongViewHolder) holder;

        //set values to views
        viewHolder.titleHolder.setText(song.getTitle());
        viewHolder.durationHolder.setText(getDuration(song.getDuration()));
        viewHolder.sizeHolder.setText(getSize(song.getSize()));

        //artwork
        Uri artworkUri = song.getArtworkUri();
        if(artworkUri != null){
            //set the uri to image view
            viewHolder.artworkHolder.setImageURI(artworkUri);

            //make sure the uri has an artwork
            if(viewHolder.artworkHolder.getDrawable() == null){
                viewHolder.artworkHolder.setImageResource(R.drawable.default_artwork);
            }

        }

        //on item click
        viewHolder.itemView.setOnClickListener(view -> Toast.makeText(context, song.getTitle(), Toast.LENGTH_SHORT).show());

    }

    //view holder
    public static class SongViewHolder extends RecyclerView.ViewHolder{
        //members
        ImageView artworkHolder;
        TextView titleHolder, durationHolder,sizeHolder;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            artworkHolder = itemView.findViewById(R.id.artWorkView);
            titleHolder = itemView.findViewById(R.id.titleView);
            durationHolder = itemView.findViewById(R.id.durationView);
            sizeHolder = itemView.findViewById(R.id.sizeView);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

//filter songs/ search results
    @SuppressLint("NotifyDataSetChanged")
    public void filterSongs(List<Song> filteredlist){
        songs = filteredlist;
        notifyDataSetChanged();
    }

    private String getDuration(int totalDuration){
        String totalDurationText;

        int hrs = totalDuration/(000*60*60);
        int min = (totalDuration%(1000*60*60))/(1000*60);
        int secs = (((totalDuration%(1000*60*60))%(1000*60*60))%(1000*60*60))/1000;

        if(hrs < 1){
            totalDurationText = String.format("%02d:%02d",min, secs);

        }
        else{
            totalDurationText = String.format("%1d:%02d:%02d",hrs ,min, secs);
        }
        return totalDurationText;
    }



    //size
    private String getSize(long bytes){
        String hrSize;

        double k = bytes/1024.0;
        double m = ((bytes/1024.0)/1024.0);
        double g = (((bytes/1024.0)/1024.0)/1024.0);
        double t =  ((((bytes/1024.0)/1024.0)/1024.0)/1024.0);

        //the format
        DecimalFormat dec = new DecimalFormat("0.00");

        if(t>1){
            hrSize = dec.format(t).concat(" TB");
        }
        else if(g > 1){
            hrSize = dec.format(g).concat(" GB");
        }
        else if(m > 1){
            hrSize = dec.format(m).concat(" MB");
        }
        else {
            hrSize = dec.format(k).concat(" KB");
        }



        return hrSize;
    }
    



}
