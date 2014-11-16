package com.hackprinceton.team9;

import android.view.View;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SongAdapter extends BaseAdapter {
	private ArrayList<Song> songs;
	private LayoutInflater songInf;
	
	private boolean color;

	public SongAdapter(Context c, ArrayList<Song> theSongs, boolean change){
		  songs=theSongs;
		  songInf=LayoutInflater.from(c);
		  color = change;
		}
	
	@Override
	public int getCount() {
	  return songs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  //map to song layout
	  LinearLayout songLay = (LinearLayout)songInf.inflate
	      (R.layout.song, parent, false);
	  //get title and artist views
	  TextView songView = (TextView)songLay.findViewById(R.id.song_title);
	  TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
	  //get song using position
	  Song currSong = songs.get(position);
	  //get title and artist strings
	  songView.setText(currSong.getTitle());
	  artistView.setText(currSong.getArtist());
	  if(color){
	  	    songView.setTextColor(Color.parseColor("#000000"));
	  	    artistView.setTextColor(Color.parseColor("#000000"));
	  }else{
	    	songView.setTextColor(Color.parseColor("#6fb7ff"));
	  	    artistView.setTextColor(Color.parseColor("#6fb7ff"));
	  }
	  //set position as tag
	  songLay.setTag(position);
	  return songLay;
	}

}
