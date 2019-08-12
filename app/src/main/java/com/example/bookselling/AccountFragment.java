package com.example.bookselling;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_account,null);

        TextView emailTV=view.findViewById(R.id.emailTV);
        TextView passwordTv=view.findViewById(R.id.passworTv);

        emailTV.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        Button logoutButton=view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        Button myBooksButton=view.findViewById(R.id.myBooksButton);
        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),MyBooks.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
