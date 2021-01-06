package com.example.groupalarmsync.menufragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupalarmsync.GroupMembersActivity;
import com.example.groupalarmsync.R;
import com.example.groupalarmsync.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class GroupsFragment extends Fragment {

    TextView userNameTextView;
    EditText createNewGroupName;
    RecyclerView groupListView;
    LinearLayout defaultGroupsLayout;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3_groups, container, false);

        userNameTextView = view.findViewById(R.id.userNameTextView);
        createNewGroupName = view.findViewById(R.id.createNewGroupName);
        groupListView = view.findViewById(R.id.groupListView);
        defaultGroupsLayout = view.findViewById(R.id.defaultGroupsLayout);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Users").child(userId).child("name")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.getValue(String.class);
                if(userName != null){
                    userNameTextView.setText(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        view.findViewById(R.id.createNewGroupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGroup();
            }
        });

        return view;
    }

    private void createNewGroup() {
        String grpName = createNewGroupName.getText().toString().trim();
        databaseReference.child("Groups").child(grpName).child("name").setValue(grpName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), grpName+" created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), GroupMembersActivity.class));
                        }
                        else {
                            Toast.makeText(getActivity(), "Group creation failed, try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}