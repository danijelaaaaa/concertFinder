package rs.reviewer.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import rs.reviewer.MainActivity;
import rs.reviewer.R;
import rs.reviewer.adapters.GooglePlacesAdapter;
import rs.reviewer.services.GooglePlacesAPIService;
import rs.reviewer.sync.SyncTask;
import rs.reviewer.tools.FragmentTransition;

public class BlankFragment extends Fragment implements AdapterView.OnItemClickListener{


    private AutoCompleteTextView actvGoogle;



    public static BlankFragment newInstance() {
        // Required empty public constructor
       BlankFragment bf = new BlankFragment();
        return bf;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_places, container, false);
        actvGoogle = (AutoCompleteTextView)getLayoutInflater(savedInstanceState).inflate(R.layout.google_autocomplete_tw, null);
        actvGoogle.setAdapter(new GooglePlacesAdapter(getActivity(), R.layout.list_item_city));
        actvGoogle.setOnItemClickListener(this);

        ((LinearLayout)view).addView(actvGoogle);





        return view;
    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        String str = (String) adapterView.getItemAtPosition(i);

       // String placeId = GooglePlacesAPIService.locationIds.get(str);

        Toast.makeText(getActivity(), "Location is changed, concert list is updating", Toast.LENGTH_SHORT).show();

         SharedPreferences  sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String radius = sharedPreferences.getString(getString(R.string.pref_radius), "5");


      //  ((MainActivity)getActivity()).startSyncFromFragment(radius,str);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("location",str);
        editor.putString("getLatLong", "true");
        editor.commit();



        new SyncTask(getActivity().getApplicationContext(),null).execute();
        getActivity().setTitle("Concerts");


        FragmentTransition.to(MyFragment.newInstance(), getActivity(), false);


    }



}
