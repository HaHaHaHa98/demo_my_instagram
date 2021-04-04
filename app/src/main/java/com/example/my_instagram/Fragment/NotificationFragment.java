package com.example.my_instagram.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_instagram.Adapter.NotificationAdapter;
import com.example.my_instagram.Model.Notification;
import com.example.my_instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class NotificationFragment extends Fragment {
    private RecyclerView rvNotification;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notifications;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        rvNotification = view.findViewById(R.id.rvNotification);
        rvNotification.setHasFixedSize(true);
        rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        notifications=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(getContext(),notifications);
        rvNotification.setAdapter(notificationAdapter);
        readNotification();
        return view;

    }
    private void readNotification(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Notification notification=snapshot.getValue(Notification.class);
                    notifications.add(notification);
                }
                Collections.reverse(notifications);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}