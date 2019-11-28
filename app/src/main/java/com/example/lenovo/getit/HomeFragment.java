package com.example.lenovo.getit;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    ArrayList<Post> posts;
    RecyclerView allPostRecyclerView;
    LinearLayoutManager manager;
    PostAdapter allPostArrayAdapter;
    ArrayList<Post> filteredPosts;
    ProgressBar post_progress_bar;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;

    boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    SwipeRefreshLayout swipe_refresh;
    EditText search_post_edittext;
    Query query;
    static boolean flag = true;
    final AtomicBoolean done = new AtomicBoolean(false);

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        fetchData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Get It");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        search_post_edittext = (EditText) view.findViewById(R.id.search_post_edittext);
        search_post_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                addFilter(s.toString());
                //    Toast.makeText(getActivity(), "Watcher", Toast.LENGTH_SHORT).show();
            }
        });
        if(flag) {
            try {
                String search = getArguments().getString("search");
                getArguments().clear();
                search_post_edittext.setText(search);
                flag = false;
            }catch(Exception e){}
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        //    databaseReference = FirebaseDatabase.getInstance().getReference("posts").orderByKey().limitToFirst(10);
        //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //    FirebaseDatabase.getInstance().getReference("posts").keepSynced(true);
        query = FirebaseDatabase.getInstance().getReference("posts").orderByKey();//.limitToLast(10);

        post_progress_bar = (ProgressBar) view.findViewById(R.id.post_progress_bar);
        posts = new ArrayList<>();
        filteredPosts = new ArrayList<>();
        //addSamplePosts();

        // vertical scroll view for all post with most recent first
        allPostRecyclerView = (RecyclerView) view.findViewById(R.id.all_posts_recyclerview);
        manager = new LinearLayoutManager(getActivity());
        allPostRecyclerView.setLayoutManager(manager);
        allPostArrayAdapter = new PostAdapter(getActivity(), filteredPosts);//posts);
        allPostRecyclerView.setAdapter(allPostArrayAdapter);
        allPostRecyclerView.setHasFixedSize(true);
        allPostRecyclerView.setNestedScrollingEnabled(true);
        /*
        allPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems)){
                    // data fetch
                    isScrolling = false;
                    fetchData();
                    manager.scrollToPosition(scrollOutItems);
                }
            }
        });
        */
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);// SwipeRefreshLayout
        swipe_refresh.setOnRefreshListener(this);
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(true);
                loadRecyclerViewData();
            }
        });
        return view;
    }

    public void addFilter(String s){
        filteredPosts.clear();
        if(!s.equals("")) {
            //        if (posts != null) {
            for (Post post : posts) {
                if (post.getPostedWho().toLowerCase().contains(s.toLowerCase()) ||                // search by user name
                        post.getProductCategory().toLowerCase().contains(s.toLowerCase()) ||     // search by category
                        post.getProductLocation().toLowerCase().contains(s.toLowerCase()) ||     // search by location
                        post.getProductTitle().toLowerCase().contains(s.toLowerCase()) ||          // search by product name
                        s.equals("")
                        ) {
                    filteredPosts.add(post);
                }
            }
            if(filteredPosts.size() <= 0)
                Toast.makeText(getActivity(), "No post(s) found", Toast.LENGTH_SHORT).show();
            //        }
        }
        else {
            filteredPosts.addAll(posts);
        }
        /*
        allPostArrayAdapter = new PostAdapter(getActivity(), filteredPosts);
        allPostRecyclerView.setAdapter(allPostArrayAdapter);
        */
        allPostArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
//        allPostArrayAdapter.notifyDataSetChanged();
        fetchData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("posts", posts);
        savedInstanceState.putSerializable("filteredPosts", filteredPosts);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            posts = (ArrayList<Post>) savedInstanceState.getSerializable("posts");
            filteredPosts = (ArrayList<Post>) savedInstanceState.getSerializable("filteredPosts");
        }
    }

    private void loadRecyclerViewData(){
        // Showing refresh animation before making http call
        swipe_refresh.setRefreshing(true);
        fetchData();
        swipe_refresh.setRefreshing(false);
    }

    public void addSamplePosts(){
        posts.add(new Post(
                "",
                "",
                "profile_pic",
                "Android",
                "Bikes > Heavy Bikes",
                "Lahore",
                "product_pic",
                null,
                "Suzuki Hayabusa",
                "Used",
                900000,
                "Suzuki Hayabusa, 1300cc, blue color...",
                0,
                null,
                null,
                null,
                1
        ));
        posts.add(new Post(
                "",
                "",
                "profile_pic",
                "Anonymous",
                "Cars > Luxurious Cars",
                "Lahore",
                "product_pic",
                null,
                "Audi A8",
                "New",
                1000000,
                "Audi A8, 3600cc, full electric...",
                0,
                null,
                null,
                null,
                1
        ));
        posts.add(new Post(
                "",
                "",
                "profile_pic",
                "Android Again",
                "",
                "",
                "",
                null,
                "Post Title",
                "",
                0,
                "Product Description and...",
                0,
                null,
                null,
                null,
                1
        ));
        posts.add(new Post(
                "",
                "",
                "profile_pic",
                "Anonymous",
                "Cars > Luxurious Cars",
                "Lahore",
                "product_pic",
                null,
                "Audi A8",
                "New",
                1000000,
                "Audi A8, 3600cc, full electric...",
                0,
                null,
                null,
                null,
                1
        ));
    }

    public void fetchData(){
        post_progress_bar.setVisibility(View.VISIBLE);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                post_progress_bar.setVisibility(View.VISIBLE);
                posts.clear();
                filteredPosts.clear();
                for(DataSnapshot iterator:dataSnapshot.getChildren()){
                    final Post post = new Post((String) iterator.getKey(), "", "", "", "", "", "", null, "", "", 0, "", 0, null, null, null, 0);
                    String userId = "";
                    String profilePic = "";
                    String postedWho = "";
                    String productTitle = "";
                    String productDescription = "";
                    String productCategory = "";
                    String productLocation = "";
                    String productPic = "";
                    String productType = "";
                    int productPrice = 0;
                    int priority = 3;
                    int quantity = 0;
                    ArrayList<Like> likedByIds = null;
                    ArrayList<Rate> ratings = null;
                    ArrayList<Comment> comments = null;
                    ArrayList<String> productImages = null;
                    if(iterator.child("userId").exists()) {
                        userId = (String) iterator.child("userId").getValue();                     // always
                        post.setUserId(userId);
                    }
                    if(iterator.child("username").exists()) {
                        postedWho = (String) iterator.child("username").getValue();
                        post.setPostedWho(postedWho);
                    }
                    if(iterator.child("profilePic").exists()){
                        profilePic = (String) iterator.child("profilePic").getValue();
                        post.setProfilePic(profilePic);
                    }
                    if(iterator.child("isPremium").exists()){
                        try{
                            priority = iterator.child("isPremium").getValue(Integer.class);
                            post.setPriority(priority);
                        }catch(NullPointerException e1){}
                    }
                    if(iterator.child("productTitle").exists()) {
                        productTitle = (String) iterator.child("productTitle").getValue();                 // always
                        post.setProductTitle(productTitle);
                    }
                    if(iterator.child("productDescription").exists()) {
                        productDescription = (String) iterator.child("productDescription").getValue();     // always
                        post.setProductDescription(productDescription);
                    }
                    if(iterator.child("productPic").exists()) {
                        if(iterator.child("productCategory").exists()) {
                            productCategory = (String) iterator.child("productCategory").getValue();           // on ad
                            post.setProductCategory(productCategory);
                        }
                        //    productLocation = (String) iterator.child("productLocation").getValue();           // on ad
                        //    post.setProductLocation(productLocation);
                        productPic = (String) iterator.child("productPic").getValue();                     // on ad
                        post.setProductPic(productPic);
                        if(iterator.child("productType").exists()){
                        productType = (String) iterator.child("productType").getValue();                   // on ad
                        post.setProductType(productType);
                        }
                        try {
                            if(iterator.child("productPrice").exists()) {
                                productPrice = iterator.child("productPrice").getValue(Integer.class);             // on ad
                                post.setProductPrice(productPrice);
                            }
                        }catch(NullPointerException e1){}
                        try {
                        //    quantity = iterator.child("quantity").getValue(Integer.class);
                        //    post.setQuantity(quantity);
                        }catch(NullPointerException e1){}
                    }
                    if(iterator.child("likedByIds").exists()) {
                        likedByIds = new ArrayList<>();
                        for(DataSnapshot iterator1:iterator.child("likedByIds").getChildren()){
                            String id = (String) iterator1.getKey();
                            String name = (String) iterator1.getValue();
                            likedByIds.add(new Like(id, name));
                        }
                        post.setLikedByIds(likedByIds);
                    }
                    if(iterator.child("ratings").exists()){
                        ratings = new ArrayList<>();
                        for(DataSnapshot iterator1:iterator.child("ratings").getChildren()){
                            String id = (String) iterator1.getKey();
                            String name = (String) iterator1.child("name").getValue();
                            int rating = 0;
                            try{
                                rating = (int) iterator1.child("rating").getValue(Integer.class);
                            }catch(NullPointerException e1){}

                            ratings.add(new Rate(id, name, rating));// name, rating));
                        }
                        post.setRatings(ratings);
                    }
                    if(iterator.child("comments").exists()){
                        comments = new ArrayList<>();
                        for(DataSnapshot iterator1:iterator.child("comments").getChildren()){
                            String key = (String) iterator1.getKey();
                            String id = (String) iterator1.child("userId").getValue();
                            String name = (String) iterator1.child("name").getValue();
                            String commented = (String) iterator1.child("commented").getValue();
                            comments.add(new Comment(key, id, name, commented));
                        }
                        post.setComments(comments);
                    }
                    if(iterator.child("productImages").exists()){
                        productImages = new ArrayList<>();
                        for(DataSnapshot iterator1:iterator.child("productImages").getChildren()){
                            productImages.add((String) iterator1.getValue());
                        }
                        post.setProductImages(productImages);
                    }
                    posts.add(post);
                }
                Collections.sort(posts);
                filteredPosts.addAll(posts);
                Collections.reverse(posts);
                Collections.reverse(filteredPosts);
                //    refreshUI();

                String search;
                try {
                    if(!search_post_edittext.getText().toString().equals("")){
                        addFilter(search_post_edittext.getText().toString());
                    }
                }catch(Exception e){}

                allPostArrayAdapter.notifyDataSetChanged();
                //onRefresh();
                post_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        post_progress_bar.setVisibility(View.GONE);
    }

    public void refreshUI(){
        /*
        allPostArrayAdapter = new PostAdapter(getActivity(), posts);
        allPostRecyclerView.setAdapter(allPostArrayAdapter);
        */
        allPostArrayAdapter.notifyDataSetChanged();
        manager.scrollToPosition(scrollOutItems);
    }

}
