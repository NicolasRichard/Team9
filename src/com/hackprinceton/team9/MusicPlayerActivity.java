package com.hackprinceton.team9;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class MusicPlayerActivity extends Activity {
	
	private MusicPlayerActivity activity = this;
	private FileReceiver fileReceiver;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private Channel channel;
    private BroadcastReceiver receiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);
		// Show the Up button in the action bar.
		setupActionBar();
		
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
	}
	
    @Override
    public void onResume() {
        super.onResume();
        manager.createGroup(channel, new ActionListener() {

			@Override
			public void onFailure(int reason) {
				return;				
			}

			@Override
			public void onSuccess() {
				return;				
			}
        	
        });
        receiver = new WifiBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
        fileReceiver = new FileReceiver(activity, "fghfg");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        fileReceiver.cancel(true);
    }

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
