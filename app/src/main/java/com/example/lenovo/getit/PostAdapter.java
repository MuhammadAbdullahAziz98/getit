package com.example.lenovo.getit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.getit.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private ArrayList<Post> postsList;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    Context context;

    PostAdapter(Context context, ArrayList<Post> postsList){
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_post, parent, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("posts");
        return new PostHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, int position) {
        final Post p = postsList.get(position);
        final int PREMIUM_PRODUCT = 1;
        final int TOP_USER_PRODUCT = 2;
        final int SIMPLE_PRODUCT = 3;
        if(p.getPriority() == PREMIUM_PRODUCT){
            ((TextView) holder.itemView.findViewById(R.id.priority_text_view)).setText("PREMIUM");
        }else if(p.getPriority() == TOP_USER_PRODUCT){
            ((TextView) holder.itemView.findViewById(R.id.priority_text_view)).setText("TOP USER");
        }else if(p.getPriority() == SIMPLE_PRODUCT){
            ((TextView) holder.itemView.findViewById(R.id.priority_text_view)).setVisibility(View.GONE);
        }
        if(p.getProductPic().equals("")){               // for post
            holder.itemView.findViewById(R.id.product_category).setVisibility(View.GONE);                                          // category gone
            holder.itemView.findViewById(R.id.product_location).setVisibility(View.GONE);                                          // location gone
            holder.itemView.findViewById(R.id.product_pic).setVisibility(View.GONE);                                               // product pic gone
            holder.itemView.findViewById(R.id.product_used_new).setVisibility(View.GONE);                                          // product type gone
            holder.itemView.findViewById(R.id.product_price).setVisibility(View.GONE);                                             // product price gone
            holder.itemView.findViewById(R.id.view_post).setVisibility(View.GONE);                                        // changing text to view post
        }
        else{
            ((ImageView) holder.itemView.findViewById(R.id.product_pic)).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.product_category)).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.product_location)).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.product_used_new)).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.product_price)).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.view_post)).setVisibility(View.VISIBLE);

            Glide.with(context)//holder.itemView.findViewById(R.id.product_pic).getContext())
                    .load(p.getProductPic())
                    .into((ImageView) holder.itemView.findViewById(R.id.product_pic));
            ((TextView) holder.itemView.findViewById(R.id.product_category)).setText(p.getProductCategory());                          // setting product category
            ((TextView) holder.itemView.findViewById(R.id.product_location)).setText(p.getProductLocation());                          // setting product location
            ((TextView) holder.itemView.findViewById(R.id.product_used_new)).setText(p.getProductType());                              // setting product type
            ((TextView) holder.itemView.findViewById(R.id.product_price)).setText("Rs " +Integer.toString(p.getProductPrice()));       // setting product price
            ((TextView) holder.itemView.findViewById(R.id.view_post)).setText("View Ad");                                              // setting text to view ad
        }

        if(!p.getProfilePic().equals("")) {
            Glide.with(holder.itemView.findViewById(R.id.profile_pic).getContext())
                    .load(p.getProfilePic())
                    .into((ImageView) holder.itemView.findViewById(R.id.profile_pic));
        }

        if(p.getPostedWho().length() > 15)
            ((TextView) holder.itemView.findViewById(R.id.who_posted)).setText(p.getPostedWho().substring(0, 14) + "...");                    // setting user who posted
        else
            ((TextView) holder.itemView.findViewById(R.id.who_posted)).setText(p.getPostedWho());                    // setting user who posted
        ((TextView) holder.itemView.findViewById(R.id.who_posted)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "Profile " + ((TextView) holder.itemView.findViewById(R.id.who_posted)).getText().toString(), Toast.LENGTH_SHORT).show();

/*******    Abdullah's Code         ******/
                Intent intentProfile = new Intent(context, ProfileActivity.class);
                intentProfile.putExtra("uid",p.getUserId());
                context.startActivity(intentProfile);
            }
        });

        ((TextView) holder.itemView.findViewById(R.id.view_post)).setOnClickListener(new View.OnClickListener() {                  // on click listener for view post/ad
            @Override
            public void onClick(View view) {

/********       Zulqar's Code       *****/
                // go to activity to show ad
//                Intent intentProfile = new Intent(context, SubCategories.class);
  //              context.startActivity(intentProfile);

                String name,category,sub,newOrUsed,company,des,profile,pid;
                ArrayList<String> otherimageURLs;
                int price,quantity;
                String mainImageURL;

                name = p.getProductTitle();
                category = p.getProductCategory();
                newOrUsed = p.getProductType();
                company = "";
                des = p.getProductDescription();
                profile = p.getUserId();
                mainImageURL = p.getProductPic();
                price = p.getProductPrice();
                otherimageURLs = p.getProductImages();
                pid = p.getPostId();

                try {
                    Intent intChange = new Intent(context, viewAdFromFeed.class);

                    intChange.putExtra("name", name);
                    intChange.putExtra("category", category);
                    intChange.putExtra("neworused", newOrUsed);
                    //intChange.putExtra("company",p.getProductCompany);
                    intChange.putExtra("desc", des);
                    intChange.putExtra("profileID", profile);
                    intChange.putExtra("imagePath", mainImageURL);
                    intChange.putExtra("price", price);
                    intChange.putExtra("otherPics", otherimageURLs);
                    intChange.putExtra("productID", p.getPostId());

                    context.startActivity(intChange);
                }catch(Exception e){
                    Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
                }

//                Intent i = new Intent(context, viewFullAd.class);
//                i.putExtra(p.getPostId());
//                 startActivity(i);
//                Toast.makeText(holder.itemView.getContext(), p.getProductTitle(), Toast.LENGTH_SHORT).show();
//
            }
        });

        ((TextView) holder.itemView.findViewById(R.id.product_title)).setText(p.getProductTitle());                                    // setting product title or post title
        ((TextView) holder.itemView.findViewById(R.id.product_description)).setText(p.getProductDescription());                        // setting product description or post description
        if(p.getLikedByIds() != null && p.getLikedByIds().size() > 0)
            ((TextView) holder.itemView.findViewById(R.id.no_of_likes)).setText(Integer.toString(p.getLikedByIds().size()) + " likes");            // setting number of likes
        else
            ((TextView) holder.itemView.findViewById(R.id.no_of_likes)).setText("No likes");            // setting number of likes
        ((TextView) holder.itemView.findViewById(R.id.no_of_likes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  button click function
                final Dialog numberDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_Black_NoTitleBar);
                numberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(220, 220, 220, 220)));
                numberDialog.setContentView(R.layout.number_likes_reacts_comments_popup);
                numberDialog.setCancelable(true);
                numberDialog.show();
                ((TextView) numberDialog.findViewById(R.id.cross_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        numberDialog.dismiss();
                    }
                });

                ArrayAdapter<String> arrayAdapter;
                ArrayList<String> temp = new ArrayList<>();
                if(p.getLikedByIds() != null && p.getLikedByIds().size() > 0) {
                    for(Like iterator:p.getLikedByIds()){
                        temp.add(iterator.getName());
                    }
                }
                else {
                    temp.add("No Likes");
                }
                arrayAdapter = new ArrayAdapter<String>(holder.itemView.getContext(), android.R.layout.simple_list_item_1, temp);
                ((ListView) numberDialog.findViewById(R.id.listview)).setAdapter(arrayAdapter);
            }
        });
        if(p.getRatings() != null && p.getRatings().size() > 0)
            ((TextView) holder.itemView.findViewById(R.id.no_of_reacts)).setText(Integer.toString(p.getRates().size()) + " reacts");      // setting number of reacts
        else
            ((TextView) holder.itemView.findViewById(R.id.no_of_reacts)).setText("No reacts");      // setting number of reacts
        ((TextView) holder.itemView.findViewById(R.id.no_of_reacts)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  button click function
                final Dialog numberDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_Black_NoTitleBar);
                numberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(220, 220, 220, 220)));
                numberDialog.setContentView(R.layout.number_likes_reacts_comments_popup);
                numberDialog.setCancelable(true);
                numberDialog.show();
                ((TextView) numberDialog.findViewById(R.id.cross_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        numberDialog.dismiss();
                    }
                });
                ArrayAdapter<String> arrayAdapter;
                ArrayList<String> tempRatings = new ArrayList<>();
                if(p.getRatings() != null && p.getRatings().size() > 0) {
                    for(Rate iterator:p.getRatings()){
                        tempRatings.add(iterator.toString());
                    }
                }
                else {
                    tempRatings.add("No Reacts");
                }
                arrayAdapter = new ArrayAdapter<String>(holder.itemView.getContext(), android.R.layout.simple_list_item_1, tempRatings);
                ((ListView) numberDialog.findViewById(R.id.listview)).setAdapter(arrayAdapter);
            }
        });
        if(p.getComments() != null && p.getComments().size() > 0)
            ((TextView) holder.itemView.findViewById(R.id.no_of_comments)).setText(Integer.toString(p.getComments().size()) + " comments");      // setting number of comments
        else
            ((TextView) holder.itemView.findViewById(R.id.no_of_comments)).setText("No comments");      // setting number of comments
        ((TextView) holder.itemView.findViewById(R.id.no_of_comments)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  button click function
                final Dialog numberDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_Black_NoTitleBar);
                numberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(220, 220, 220, 220)));
                numberDialog.setContentView(R.layout.number_likes_reacts_comments_popup);
                numberDialog.setCancelable(true);
                numberDialog.show();
                ((TextView) numberDialog.findViewById(R.id.cross_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        numberDialog.dismiss();
                    }
                });

                ArrayAdapter<String> arrayAdapter;
                ArrayList<String> tempComments = new ArrayList<>();
                if(p.getComments() != null && p.getComments().size() > 0) {
                    for(Comment iterator:p.getComments()){
                        tempComments.add(iterator.toString());
                    }
                }
                else {
                    tempComments.add("No Comments");
                }
                arrayAdapter = new ArrayAdapter<String>(holder.itemView.getContext(), android.R.layout.simple_list_item_1, tempComments);
                ((ListView) numberDialog.findViewById(R.id.listview)).setAdapter(arrayAdapter);
            }
        });

        if(p.searchLiked(mFirebaseAuth.getCurrentUser().getUid())) {
            ((Button) holder.itemView.findViewById(R.id.like_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
            ((Button) holder.itemView.findViewById(R.id.like_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
        }
        else{
            ((Button) holder.itemView.findViewById(R.id.like_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorLightGrey));
            ((Button) holder.itemView.findViewById(R.id.like_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorBlack));
        }
        ((Button) holder.itemView.findViewById(R.id.like_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                                        // on click listener for like
                // like button click function
                if(p.searchLiked(mFirebaseAuth.getCurrentUser().getUid())) {
                    databaseReference2.child(p.getPostId()).child("likedByIds").child(mFirebaseAuth.getCurrentUser().getUid()).removeValue();
                    p.removeLiked(mFirebaseAuth.getCurrentUser().getUid());
                    //    ((Button) holder.itemView.findViewById(R.id.like_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
                    //    ((Button) holder.itemView.findViewById(R.id.like_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
                }
                else {
                    databaseReference2.child(p.getPostId()).child("likedByIds").child(mFirebaseAuth.getCurrentUser().getUid()).setValue(mFirebaseAuth.getCurrentUser().getDisplayName());
                    p.addLiked(mFirebaseAuth.getCurrentUser().getUid(), mFirebaseAuth.getCurrentUser().getDisplayName());
                    //    ((Button) holder.itemView.findViewById(R.id.like_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorLightGrey));
                    //    ((Button) holder.itemView.findViewById(R.id.like_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorBlack));
                }
                ((TextView) holder.itemView.findViewById(R.id.no_of_likes)).setText(Integer.toString(p.getLikedByIds().size()) + " likes");
            }
        });
        if(p.searchRated(mFirebaseAuth.getCurrentUser().getUid()) != 0){
            ((Button) holder.itemView.findViewById(R.id.react_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
            ((Button) holder.itemView.findViewById(R.id.react_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
            ((Button) holder.itemView.findViewById(R.id.react_button)).setText("Rated " + Integer.toString(p.searchRated(mFirebaseAuth.getCurrentUser().getUid())));
        }
        ((Button) holder.itemView.findViewById(R.id.react_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog reactDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_Black_NoTitleBar);
                reactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 100, 100, 100)));
                reactDialog.setContentView(R.layout.react_popup);
                reactDialog.setCancelable(true);
                reactDialog.show();
                ((TextView) reactDialog.findViewById(R.id.five_star_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rating = 5;
                        for(Rate iterator:p.getRatings()){
                            if(iterator.getId().equals(mFirebaseAuth.getCurrentUser().getUid()))
                                iterator.setRated(rating);
                        }
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid().trim()).child("name").setValue(mFirebaseAuth.getCurrentUser().getDisplayName().trim());
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid().trim()).child("rating").setValue(5);
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setText("Rated 5");
                        reactDialog.dismiss();
                    }
                });
                ((TextView) reactDialog.findViewById(R.id.four_start_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rating = 4;
                        for(Rate iterator:p.getRatings()){
                            if(iterator.getId().equals(mFirebaseAuth.getCurrentUser().getUid()))
                                iterator.setRated(rating);
                        }
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("name").setValue(mFirebaseAuth.getCurrentUser().getDisplayName());
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("rating").setValue(rating);
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setText("Rated 4");
                        reactDialog.dismiss();
                    }
                });
                ((TextView) reactDialog.findViewById(R.id.three_star_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rating = 3;
                        for(Rate iterator:p.getRatings()){
                            if(iterator.getId().equals(mFirebaseAuth.getCurrentUser().getUid()))
                                iterator.setRated(rating);
                        }
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("name").setValue(mFirebaseAuth.getCurrentUser().getDisplayName());
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("rating").setValue(rating);
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setText("Rated 3");
                        reactDialog.dismiss();
                    }
                });
                ((TextView) reactDialog.findViewById(R.id.two_star_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rating = 2;
                        for(Rate iterator:p.getRatings()){
                            if(iterator.getId().equals(mFirebaseAuth.getCurrentUser().getUid()))
                                iterator.setRated(rating);
                        }
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("name").setValue(mFirebaseAuth.getCurrentUser().getDisplayName());
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("rating").setValue(rating);
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setText("Rated 2");
                        reactDialog.dismiss();
                    }
                });
                ((TextView) reactDialog.findViewById(R.id.one_star_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rating = 1;
                        for(Rate iterator:p.getRatings()){
                            if(iterator.getId().equals(mFirebaseAuth.getCurrentUser().getUid()))
                                iterator.setRated(rating);
                        }
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("name").setValue(mFirebaseAuth.getCurrentUser().getDisplayName());
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).child("rating").setValue(rating);
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorForegroundSMD));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorWhite));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setText("Rated 1");
                        reactDialog.dismiss();
                    }
                });
                ((TextView) reactDialog.findViewById(R.id.cancel_textview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rating = 0;
                        for(Rate iterator:p.getRatings()){
                            if(iterator.getId().equals(mFirebaseAuth.getCurrentUser().getUid()))
                                iterator.setRated(rating);
                        }
                        databaseReference2.child(p.getPostId()).child("ratings").child(mFirebaseAuth.getCurrentUser().getUid()).removeValue();
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorLightGrey));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorBlack));
                        ((Button) holder.itemView.findViewById(R.id.react_button)).setText("React");
                        reactDialog.dismiss();
                    }
                });
            }
        });


        ((Button) holder.itemView.findViewById(R.id.comment_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // comment button click function
                final Dialog commentDialog = new Dialog(holder.itemView.getContext(), android.R.style.Theme_Black_NoTitleBar);
                commentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(200, 100, 100, 100)));
                commentDialog.setContentView(R.layout.comment_popup);
                commentDialog.setCancelable(true);
                commentDialog.show();
                ((Button) commentDialog.findViewById(R.id.add_comment_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = ((EditText) commentDialog.findViewById(R.id.comment_textview)).getText().toString();
                        if(!comment.equals(""))
                        {
                            String id = databaseReference2.child(p.getPostId()).child("comments").push().getKey();
                            databaseReference2.child(p.getPostId()).child("comments").child(id).child("userId").setValue(mFirebaseAuth.getCurrentUser().getUid());
                            databaseReference2.child(p.getPostId()).child("comments").child(id).child("name").setValue(mFirebaseAuth.getCurrentUser().getDisplayName());
                            databaseReference2.child(p.getPostId()).child("comments").child(id).child("commented").setValue(comment);
                            p.getComments().add(new Comment(id, mFirebaseAuth.getCurrentUser().getUid(),
                                    mFirebaseAuth.getCurrentUser().getDisplayName(),
                                    comment));
                            commentDialog.dismiss();
                            Toast.makeText(holder.itemView.getContext(), "Comment Posted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                ((Button) commentDialog.findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commentDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public String getName(String id){
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child("Name").getValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return null;
    }

    public class PostHolder extends RecyclerView.ViewHolder{

        public PostHolder(View itemView) {
            super(itemView);
        }
    }

}
