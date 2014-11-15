package com.hackprinceton.shareMusicSystem;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;


public final class ShareMusicHost extends BroadcastReceiver {
    private Activity activity;
	private Channel channel;
	private String filesDirectory;	
	private WifiP2pManager manager;
    private PeerListListener peerListener;
	
	public ShareMusicHost(Activity activity, String filesDirectory) {
		this.activity = activity;
		this.filesDirectory = filesDirectory;
		manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(activity, activity.getMainLooper(), null);	
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
		    // request available peers from the wifi p2p manager. This is an
		    // asynchronous call and the calling activity is notified with a
		    // callback on PeerListListener.onPeersAvailable()
		    if (manager != null) {
		        manager.requestPeers(channel, peerListener);
		    }
		}		
	}	
	
	public void addPeerListChangedListener(PeerListListener listener) {
		peerListener = listener;
	}
}
