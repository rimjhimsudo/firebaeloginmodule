package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    Button btn1;
    EditText et1,et2;
    ArrayList<String> notes;
    ListView lv1;
    ArrayAdapter<String> arrayAdapter;
    FirebaseUser firebaseUser;
    private static final int RC_SIGN_IN=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1=findViewById(R.id.btn1);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        lv1=findViewById(R.id.lv1);
        notes=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        //if()

        arrayAdapter=new ArrayAdapter<String>(this,R.layout.itemrow,R.id.tv1,notes);
        lv1.setAdapter(arrayAdapter);
        if(firebaseUser!=null){
            //already logged in
            addlisteners();

        }
        else{
            //logged out
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                                    new AuthUI.IdpConfig.AnonymousBuilder().build()))
                            .build(),
                    RC_SIGN_IN);

        }

        }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                addlisteners();
                //Log.e("TAG","onActivityresult:"+ firebaseUser.getDisplayName());
            }
            else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button

                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                    return;
                }



            }
        }
    }
    public void addlisteners(){
        final DatabaseReference dbref=FirebaseDatabase.getInstance().getReference();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pojoNote n=new pojoNote("hello","Rimjhim");
                String note=et1.getText().toString();
                //String name=et2.getText().toString();
                dbref.child("note").push().setValue(note);
                //dbref.child("todo").push().setValue(name);


            }
        });

        dbref.child("note").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String data=dataSnapshot.getValue(String.class);
                //pojoNote data1=dataSnapshot.getValue(pojoNote.class);
                notes.add(data);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //pos of subnote is changed i.e.of one of notes.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //operation failed..due to some reason
            }
        });

    }

}
