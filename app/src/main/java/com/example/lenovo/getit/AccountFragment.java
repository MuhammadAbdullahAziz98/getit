package com.example.lenovo.getit;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    ListView listView;
    FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        getActivity().setTitle("Account");
        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        listView = (ListView) view.findViewById(R.id.list_view);
        arrayList = new ArrayList<>();
        arrayList.add("► My Profile");
        arrayList.add("► My Chats");
        arrayList.add("Account Settings");
        arrayList.add("► Settings");
        arrayList.add("► My Cart");
        arrayList.add("► Become Premium");
        arrayList.add("► Logout");
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(arrayList.get(i).equals("► Logout")){
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(),"Logging Out",Toast.LENGTH_SHORT).show();
                                }
                            });
                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(getActivity(),MainActivity2.class);
                    startActivity(intent);

                }
                else if(arrayList.get(i).equals("► My Chats")){
                    Intent intent = new Intent(getActivity(),MainMessages.class);
                    startActivity(intent);

                }
                else if(arrayList.get(i).equals("► My Profile")){
                    Intent intent = new Intent(getActivity(),ProfileActivity.class);
                    intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(intent);

                }
                else if(arrayList.get(i).equals("► Become Premium")){
                    Intent intent = new Intent(getActivity(),PaypalInt.class);
                    intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(intent);

                }
                else if(arrayList.get(i).equals("► My Cart")){
                    Intent intent = new Intent(getActivity(),Cart.class);
                    intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    DBCart db = new DBCart(getActivity().getApplicationContext());
                   // db.insert_cart(mFirebaseAuth.getCurrentUser().getUid(),"-LTSPfoTpm9crwGP_2Pq");
                    startActivity(intent);

                }
                else if(arrayList.get(i).equals("Account Settings")){
                    Intent intent = new Intent(getActivity(),ChangeName.class);
                    startActivity(intent);

                }


            }
        });
        return view;

    }

}
