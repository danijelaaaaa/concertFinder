package rs.reviewer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import rs.reviewer.R;

/**
 * Created by Danijela on 9/9/2016.
 */
public class WifiDialog extends AlertDialog.Builder{

    public WifiDialog(Context context) {
        super(context);

        setUpDialog();
    }

    private void setUpDialog(){
        setTitle(R.string.oops);
        setMessage(R.string.wifi_disabled_message);
        setCancelable(false);

        setPositiveButton("Yes", new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);


                dialog.cancel();
            }

        });

        setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

