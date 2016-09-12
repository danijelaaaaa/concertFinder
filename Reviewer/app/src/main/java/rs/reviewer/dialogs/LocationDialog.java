package rs.reviewer.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import rs.reviewer.R;


public class LocationDialog extends AlertDialog.Builder{

	public LocationDialog(Context context) {
		super(context);
		
		setUpDialog();
	}
	
	private void setUpDialog(){
		setTitle(R.string.oops);
	    setMessage(R.string.location_disabled_message);
	    setCancelable(false);


		setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
	    
	}
	
	public AlertDialog prepareDialog(){
		AlertDialog dialog = create();
		dialog.setCanceledOnTouchOutside(false);
		
		return dialog;
	}
	
}
