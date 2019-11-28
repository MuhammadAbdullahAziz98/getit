package com.example.lenovo.getit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;



/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends Fragment {

    private AdView mAdView;     // for setting admob view
    private Button create_ad_button;
    private Button create_post_button;

    public ShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Shop");

        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        create_ad_button = (Button) view.findViewById(R.id.create_ad_button);
        create_ad_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(getActivity(), AddItem.class));
                //Toast.makeText(getActivity(), "Create Activity", Toast.LENGTH_SHORT).show();
            }
        });
        create_post_button = (Button) view.findViewById(R.id.create_post_button);
        create_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreatePostActivity.class));
            }
        });

// google admob code starts
        // google admob
        MobileAds.initialize(getActivity(), "ca-app-pub-3940256099942544/6300978111");      // testing ad
        //MobileAds.initialize(getActivity(), "ca-app-pub-4541069819167372~3565880771");        // real ad
        // testing adunit       ca-app-pub-3940256099942544/6300978111
        // my adunit            ca-app-pub-4541069819167372~3565880771

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
// google admob code starts


        return view;
    }

}
