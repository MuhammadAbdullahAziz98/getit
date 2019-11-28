package com.example.lenovo.getit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {
    public CategoriesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        getActivity().setTitle("Categories");
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment.flag = true;
                Intent i = new Intent(getActivity(), SubCategories.class);
                i.putExtra("category", view.getTag().toString());
                startActivity(i);
            }
        };
        CardView mobile_cv = (CardView) view.findViewById(R.id.mobiles_cv);
        mobile_cv.setClickable(true);
        mobile_cv.setOnClickListener(listener);
        CardView laptop_cv = (CardView) view.findViewById(R.id.laptop_cv);
        laptop_cv.setClickable(true);
        laptop_cv.setOnClickListener(listener);
        CardView computers_cv = (CardView) view.findViewById(R.id.computers_cv);
        computers_cv.setClickable(true);
        computers_cv.setOnClickListener(listener);
        CardView processors_cv = (CardView) view.findViewById(R.id.processors_cv);
        processors_cv.setClickable(true);
        processors_cv.setOnClickListener(listener);
        CardView electronics_cv = (CardView) view.findViewById(R.id.electronics_cv);
        electronics_cv.setClickable(true);
        electronics_cv.setOnClickListener(listener);
        CardView miscellaneous_cv = (CardView) view.findViewById(R.id.miscellaneous_cv);
        miscellaneous_cv.setClickable(true);
        miscellaneous_cv.setOnClickListener(listener);
        CardView cameras_cv = (CardView) view.findViewById(R.id.cameras_cv);
        cameras_cv.setClickable(true);
        cameras_cv.setOnClickListener(listener);
        CardView games_cv = (CardView) view.findViewById(R.id.games_cv);
        games_cv.setClickable(true);
        games_cv.setOnClickListener(listener);

        return view;
    }
}
