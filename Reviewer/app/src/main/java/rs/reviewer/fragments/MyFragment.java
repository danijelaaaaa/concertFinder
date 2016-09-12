package rs.reviewer.fragments;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import rs.reviewer.R;
import rs.reviewer.activities.DetailActivity;
import rs.reviewer.adapters.GooglePlacesAdapter;
import rs.reviewer.database.DBContentProvider;
import rs.reviewer.database.ReviewerSQLiteHelper;
import rs.reviewer.sync.SyncService;

public class MyFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static String USER_KEY = "rs.reviewer.USER_KEY";
    private SimpleCursorAdapter adapter;
    private String mCurFilter;

    EditText inputSearch;


	public static MyFragment newInstance() {
		
		MyFragment mpf = new MyFragment();


		
		return mpf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {


        setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.map_layout, vg, false);



      //  inputSearch = (EditText) getActivity().findViewById(R.id.inputSearch);

        final EditText searchET = (EditText) view.findViewById(R.id.inputSearch);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // restart loader. this result in garbaging old data and re-creating new loader for a new cursor
                mCurFilter = TextUtils.isEmpty(charSequence) ? null : charSequence.toString();
                getLoaderManager().restartLoader(0, null, MyFragment.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



		return view;
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

//        Cinema cinema = (Cinema)l.getAdapter().getItem(position);


        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Uri todoUri = Uri.parse(DBContentProvider.CONTENT_URI_CONCERT + "/" + id);
        intent.putExtra("id", todoUri);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Toast.makeText(getActivity(), "onActivityCreated()", Toast.LENGTH_SHORT).show();




        //Dodaje se adapter
        getLoaderManager().initLoader(0, null, this);
        String[] from = new String[] { ReviewerSQLiteHelper.COLUMN_NAME, ReviewerSQLiteHelper.COLUMN_FORMATTED_DATETIME};
        int[] to = new int[] {R.id.name, R.id.description};


        adapter = new SimpleCursorAdapter(getActivity(), R.layout.concerts_layout, null, from,
                to, 0);


        setListAdapter(adapter);





    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.activity_itemdetail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if(id == R.id.action_refresh){
//            //startovati servis
//
//
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] allColumns = { ReviewerSQLiteHelper.COLUMN_ID,
                ReviewerSQLiteHelper.COLUMN_NAME, ReviewerSQLiteHelper.COLUMN_FORMATTED_DATETIME};

      Uri baseUri;

        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(DBContentProvider.CONTENT_URI_SEARCH, Uri.encode(mCurFilter));
        }else{
            baseUri = DBContentProvider.CONTENT_URI_CONCERT;
        }

        CursorLoader cursor = new CursorLoader(getActivity(), baseUri,
                allColumns, null, null, null);

        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }




    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}