package rs.reviewer.dialogs;

/**
 * Created by Danijela on 9/12/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;

import rs.reviewer.R;
import rs.reviewer.database.DBContentProvider;
import rs.reviewer.database.ReviewerSQLiteHelper;


/**
 * Created by Danijela on 9/9/2016.
 */
public class DeleteFav extends AlertDialog.Builder{

    private Long id;

    public DeleteFav(Context context, Long id) {
        super(context);
        this.id = id;
        setUpDialog();
    }

    private void setUpDialog(){
        setTitle(R.string.delete_fav);
        setMessage(R.string.delete_fav_message);
        setCancelable(false);

        setPositiveButton("Yes", new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteFav(id);

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

    public void deleteFav(Long id){
        ReviewerSQLiteHelper dbHelper = new ReviewerSQLiteHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Uri todoUri = Uri.parse(DBContentProvider.CONTENT_URI_FAV+ "/" + id);

        getContext().getContentResolver().delete(todoUri, null, null);
        db.close();



    }

}

