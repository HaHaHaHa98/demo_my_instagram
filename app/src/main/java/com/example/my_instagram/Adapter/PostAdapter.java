package com.example.my_instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_instagram.CommentActivity;
import com.example.my_instagram.Fragment.PostDetailFragment;
import com.example.my_instagram.Fragment.ProfileFragment;
import com.example.my_instagram.Model.Post;
import com.example.my_instagram.Model.User;
import com.example.my_instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Post> mPosts;
    private FirebaseUser firebaseUser;


    public PostAdapter(Context mContext, ArrayList<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPosts.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.imgPost);

        if (post.getDescription().equals("")) {
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(post.getDescription());
        }
        publisherInfo(holder.imgProfile, holder.tvUsername, holder.tvPublisher, post.getPublisher());
        isLikes(post.getPostId(), holder.imgLike);
        countLikes(holder.tvLike, post.getPostId());
        getComments(post.getPostId(), holder.tvComment);
        isSaved(post.getPublisher(), holder.imgSave);

        holder.tvPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileId", post.getPublisher());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain, new ProfileFragment()).commit();
            }
        });

        holder.imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postId", post.getPostId());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain, new PostDetailFragment()).commit();
            }
        });

        holder.imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.imgSave.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).removeValue();
                }
            }
        });

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.imgLike.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(), post.getPostId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        holder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("publisherId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    private void addNotification(String userId, String postId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postId", postId);
        hashMap.put("isPost", true);
        myRef.push().setValue(hashMap);
    }

    private void isSaved(String postId, ImageView imgSave) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    imgSave.setImageResource(R.drawable.ic_saved);
                    imgSave.setTag("saved");
                } else {
                    imgSave.setImageResource(R.drawable.ic_save);
                    imgSave.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void publisherInfo(final ImageView imgProfile, final TextView username, final TextView publisher, final String userId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageURL()).into(imgProfile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComments(String postId, final TextView tvComment) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvComment.setText("View all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLikes(String postId, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countLikes(final TextView likes, String postId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProfile, imgPost, imgLike, imgSave, imgComment;
        private TextView tvUsername, tvLike, tvDescription, tvPublisher, tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgSave = itemView.findViewById(R.id.imgSave);
            imgComment = itemView.findViewById(R.id.imgComment);

            tvComment = itemView.findViewById(R.id.tvComment);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPublisher = itemView.findViewById(R.id.tvPublisher);
        }
    }
}
