package com.example.my_instagram.Adapter;

import android.content.Context;
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
import com.example.my_instagram.Fragment.PostDetailFragment;
import com.example.my_instagram.Fragment.ProfileFragment;
import com.example.my_instagram.Model.Notification;
import com.example.my_instagram.Model.Post;
import com.example.my_instagram.Model.User;
import com.example.my_instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<com.example.my_instagram.Model.Notification> notifications;

    public NotificationAdapter(Context mContext, ArrayList<Notification> notifications) {
        this.mContext = mContext;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        com.example.my_instagram.Model.Notification notification = notifications.get(position);
        holder.tvComment.setText(notification.getText());
        getUserInfo(holder.imgProfile, holder.tvUsername, notification.getPostId());

        if (notification.getIsPost()) {
            holder.imgPost.setVisibility(View.VISIBLE);
            getPostImage(holder.imgPost, notification.getPostId());
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.getIsPost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postId", notification.getPostId());
                    editor.apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain, new PostDetailFragment()).commit();

                }else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileId", notification.getUserId());
                    editor.apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contain, new ProfileFragment()).commit();

                }
            }
        });
    }


    private void getUserInfo(ImageView imgView, TextView tvUsername, String publisherId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(publisherId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageURL()).into(imgView);
                tvUsername.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(ImageView imgView, String postId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostImage()).into(imgView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile, imgPost;
        private TextView tvUsername, tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);

        }
    }
}
