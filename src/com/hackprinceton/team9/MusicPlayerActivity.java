package com.hackprinceton.team9;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.hackprinceton.team9.MusicService.MusicBinder;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.MediaController.MediaPlayerControl;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

public class MusicPlayerActivity extends Activity implements MediaPlayerControl {
	//song list variables
	private ArrayList<Song> songList;
	private ListView songView;
	private static TextView titleView;
	private static TextView artistView;
	//service
	private MusicService musicSrv;
	private Intent playIntent;
	//binding
	private boolean musicBound=false;
	
	//controller
	private MusicController controller;
	
	//activity and playback pause flags
	private boolean paused=false, playbackPaused=false;
	
	//tell Nicolas about songListUpdate
	boolean songListUpdate;
		
		
	//start and bind the service when the activity starts
	@Override
	protected void onStart() {
		super.onStart();
		if(playIntent==null){
			playIntent = new Intent(this, MusicService.class);
			boolean check = bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			startService(playIntent);
			Log.i("MusicPlayer", "onStart Reached");
			if(check){
				Log.i("MusicPlayer", "onStart Reached true bind");
			}else{
				Log.i("MusicPlayer", "onStart Reached false bind");
			}
		}
		
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);
		
		updateSongList();
		
		//setup controller
		setController();
		
    }
    
    public void updateCurrent(int pos){
    	titleView = (TextView)findViewById(R.id.current_title);
  	    artistView = (TextView)findViewById(R.id.current_artist);
  	    titleView.setText(songList.get(pos).getTitle());
  	    artistView.setText(songList.get(pos).getArtist());
    }
    
    public void updateSongList(){
    	//retrieve list view
		songView = (ListView)findViewById(R.id.song_list);
		//instantiate list
		songList = new ArrayList<Song>();
		//get songs from device
		getSongList();
		//sort alphabetically by title
		Collections.sort(songList, new Comparator<Song>(){
			public int compare(Song a, Song b){
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		//create and set adapter
		SongAdapter songAdt = new SongAdapter(this, songList);
		songView.setAdapter(songAdt);
		songListUpdate = false;
		
    }
    
  //user song select
  	public void songPicked(View view){
  		Log.i("MusicPlayer", "songPicked Reached");
  		musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
  		musicSrv.playSong();
  		if(playbackPaused){
  			setController();
  			playbackPaused=false;
  		}
  		updateCurrent(musicSrv.getSongPosition());
  		if(songListUpdate){
  			updateSongList();
  		}
  		controller.show(0);
  		
  	}

  	@Override
  	public boolean onCreateOptionsMenu(Menu menu) {
  		// Inflate the menu; this adds items to the action bar if it is present.
  		getMenuInflater().inflate(R.menu.main, menu);
  		return true;
  	}

  	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//menu item selected
		switch (item.getItemId()) {
		case R.id.action_shuffle:
			musicSrv.setShuffle();
			break;
		case R.id.action_end:
			stopService(playIntent);
			musicSrv=null;
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
  	
    //connect to the service
  	private ServiceConnection musicConnection = new ServiceConnection(){

  		@Override
  		public void onServiceConnected(ComponentName name, IBinder service) {
  			Log.i("MusicPlayer", "serviceConnection Reached");
  			MusicBinder binder = (MusicBinder)service;
  			//get service
  			musicSrv = binder.getService();
  			//pass list
  			musicSrv.setList(songList);
  			musicBound = true;
  		}

  		@Override
  		public void onServiceDisconnected(ComponentName name) {
  			musicBound = false;
  		}
  	};
    
    //gets the list of songs from specified folder
    public void getSongList() {
    	ContentResolver musicResolver = getContentResolver();
    	Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	
    	final String[] columns = {android.provider.MediaStore.Audio.Media._ID, 
    			android.provider.MediaStore.Audio.Media.TITLE, 
    			android.provider.MediaStore.Audio.Media.ARTIST};
    	
    	//***************FOLDER SELECTION HERE****************//
    	String folder = "/storage/emulated/0/erosTest/";
    	Cursor musicCursor = musicResolver.query(musicUri, columns, android.provider.MediaStore.Audio.Media.DATA + " LIKE \"" + folder + "%\"", null, null);
    	
    	if(musicCursor!=null && musicCursor.moveToFirst()){
    		  //get columns
    		  int titleColumn = musicCursor.getColumnIndex
    		    (android.provider.MediaStore.Audio.Media.TITLE);
    		  int idColumn = musicCursor.getColumnIndex
    		    (android.provider.MediaStore.Audio.Media._ID);
    		  int artistColumn = musicCursor.getColumnIndex
    		    (android.provider.MediaStore.Audio.Media.ARTIST);
    		  //add songs to list
    		  do {
    		    long thisId = musicCursor.getLong(idColumn);
    		    String thisTitle = musicCursor.getString(titleColumn);
    		    String thisArtist = musicCursor.getString(artistColumn);
    		    songList.add(new Song(thisId, thisTitle, thisArtist));
    		  }
    		  while (musicCursor.moveToNext());
    		}
    	
	}
    
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if(musicSrv!=null && musicBound && musicSrv.isPng())
			return musicSrv.getPosn();
		else return 0;
	}

	@Override
	public int getDuration() {
		if(musicSrv!=null && musicBound && musicSrv.isPng())
			return musicSrv.getDur();
		else return 0;
	}

	@Override
	public boolean isPlaying() {
		if(musicSrv!=null && musicBound)
			return musicSrv.isPng();
		return false;
	}

	@Override
	public void pause() {
		playbackPaused=true;
		musicSrv.pausePlayer();
	}

	@Override
	public void seekTo(int pos) {
		musicSrv.seek(pos);
	}

	@Override
	public void start() {
		musicSrv.go();
	}

	//set the controller up
	private void setController(){
		controller = new MusicController(this);
		//set previous and next button listeners
		controller.setPrevNextListeners(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playNext();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playPrev();
			}
		});
		//set and show
		controller.setMediaPlayer(this);
		controller.setAnchorView(findViewById(R.id.song_list));
		controller.setEnabled(true);
	}

	private void playNext(){
		musicSrv.playNext();
		if(playbackPaused){ 
			setController();
			playbackPaused=false;
		}
		updateCurrent(musicSrv.getSongPosition());
		if(songListUpdate){
  			updateSongList();
  		}
		controller.show(0);
	}

	private void playPrev(){
		musicSrv.playPrev();
		if(playbackPaused){
			setController();
			playbackPaused=false;
		}
		updateCurrent(musicSrv.getSongPosition());
		if(songListUpdate){
  			updateSongList();
  		}
		controller.show(0);
	}

	@Override
	protected void onPause(){
		super.onPause();
		paused=true;
	}

	@Override
	protected void onResume(){
		super.onResume();
		if(paused){
			setController();
			paused=false;
		}
	}

	@Override
	protected void onStop() {
		controller.hide();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		stopService(playIntent);
		musicSrv=null;
		super.onDestroy();
	}
}
