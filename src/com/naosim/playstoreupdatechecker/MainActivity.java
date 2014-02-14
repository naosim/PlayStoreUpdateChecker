package com.naosim.playstoreupdatechecker;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Start polling service
		PollingUtils.startPollingService(this, 5, PollingService.class, PollingService.ACTION);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Stop polling service
		PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
	}

}
