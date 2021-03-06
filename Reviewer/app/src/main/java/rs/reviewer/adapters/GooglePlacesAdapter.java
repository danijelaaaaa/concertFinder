package rs.reviewer.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import rs.reviewer.services.GooglePlacesAPIService;

/**
 * Created by Danijela on 9/8/2016.
 */
public class GooglePlacesAdapter extends ArrayAdapter implements Filterable {

    private ArrayList<String> resultList;

    public GooglePlacesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

               // System.out.println("Filter results "+filterResults);

                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = GooglePlacesAPIService.autocomplete(constraint.toString());




                    for(int i= 0; i<resultList.size(); i++){

                        System.out.println(resultList.get(i));



                    }


                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }


}
