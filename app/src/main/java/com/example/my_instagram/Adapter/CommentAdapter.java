package com.example.my_instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_instagram.MainActivity;
import com.example.my_instagram.Model.Comment;
import com.example.my_instagram.Model.User;
import com.example.my_instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Comment> mComments;

    public CommentAdapter(Context mContext, ArrayList<Comment> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment=mComments.get(position);
        holder.tvComment.setText(comment.getComment());
        getUserInfo(holder.imgProfile,holder.tvUsername,comment.getPublisher());
        holder.tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

    }

    private void getUserInfo(final ImageView imageView, final TextView tvUsername, String publisherId){
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageURL()).into(imageView);
                tvUsername.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProfile;
        private TextView tvUsername,tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile=itemView.findViewById(R.id.imgProfile);
            tvUsername=itemView.findViewById(R.id.tvUsername);
            tvComment=itemView.findViewById(R.id.tvComment);
        }
    }
}
