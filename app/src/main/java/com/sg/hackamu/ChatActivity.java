package com.sg.hackamu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.adapter.ChatAdapter;
import com.sg.hackamu.model.ChatMessage;
import com.sg.hackamu.model.Faculty;
import com.sg.hackamu.model.User;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.ArrayList;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ChatActivity extends AppCompatActivity {
    User user;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase firebaseDatabase = FirebaseUtils.getDatabase();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ProgressBar progressBar;
    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();
    EditText editText;
    FloatingActionButton floatingActionButton;
    ChatMessage senderchatMessage, recieverchatmessage;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    Faculty faculty;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Intent x;
    boolean isuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        x=new Intent(ChatActivity.this,MapsActivity.class);
        if (i.hasExtra("user")) {
            user = i.getParcelableExtra("user");
            getSupportActionBar().setTitle(user.getName());
            x.putExtra("user",user);
            isuser=true;
        }
        if(user==null)
        {
            faculty=i.getParcelableExtra("faculty");
            getSupportActionBar().setTitle(faculty.getName());
            x.putExtra("faculty",faculty);
            isuser=false;
        }
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBarChat);
        progressBar.setVisibility(View.VISIBLE);
        editText = findViewById(R.id.chattext);
        floatingActionButton = findViewById(R.id.floatingActionButtonSend);
        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        chatAdapter = new ChatAdapter(chatMessages, ChatActivity.this, firebaseUser);
        recyclerView.setAdapter(chatAdapter);
        reference = firebaseDatabase.getReference();
        if(isuser) {
            reference.child("chats").child(firebaseUser.getUid()).child(user.getUuid()).keepSynced(true);
            reference.child("chats").child(firebaseUser.getUid()).child(user.getUuid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot != null) {
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setMessageText(dataSnapshot.getValue(ChatMessage.class).getMessageText());
                        chatMessage.setSenderuuid(dataSnapshot.getValue(ChatMessage.class).getSenderuuid());
                        chatMessage.setRecieveruuid(dataSnapshot.getValue(ChatMessage.class).getRecieveruuid());
                        chatMessage.setMessageTime(dataSnapshot.getValue(ChatMessage.class).getMessageTime());
                        chatMessages.add(chatMessage);
                        progressBar.setVisibility(View.INVISIBLE);
                        chatAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });
        }
        else
        {
            reference.child("chats").child(firebaseUser.getUid()).child(faculty.getUuid()).keepSynced(true);
            reference.child("chats").child(firebaseUser.getUid()).child(faculty.getUuid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot != null) {
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setMessageText(dataSnapshot.getValue(ChatMessage.class).getMessageText());
                        chatMessage.setSenderuuid(dataSnapshot.getValue(ChatMessage.class).getSenderuuid());
                        chatMessage.setRecieveruuid(dataSnapshot.getValue(ChatMessage.class).getRecieveruuid());
                        chatMessage.setMessageTime(dataSnapshot.getValue(ChatMessage.class).getMessageTime());
                        chatMessages.add(chatMessage);
                        progressBar.setVisibility(View.INVISIBLE);
                        chatAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });
        }
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    Log.d("Auth State", "Auth State Changed");

                }
            };
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getText().toString().trim().length() != 0) {
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {

                        }
                        senderchatMessage = new ChatMessage();
                        senderchatMessage.setMessageText(editText.getText().toString().trim());
                        if(isuser) {
                            senderchatMessage.setRecieveruuid(user.getUuid());
                            senderchatMessage.setSenderuuid(firebaseUser.getUid());
                            reference.child("chats").child(firebaseUser.getUid()).child(user.getUuid()).child(Long.toString(senderchatMessage.getMessageTime())).setValue(senderchatMessage);
                            reference.child("chats").child(user.getUuid()).child(firebaseUser.getUid()).child(Long.toString(senderchatMessage.getMessageTime())).setValue(senderchatMessage);
                        }else
                        {
                            senderchatMessage.setRecieveruuid(faculty.getUuid());
                            senderchatMessage.setSenderuuid(firebaseUser.getUid());
                            reference.child("chats").child(firebaseUser.getUid()).child(faculty.getUuid()).child(Long.toString(senderchatMessage.getMessageTime())).setValue(senderchatMessage);
                            reference.child("chats").child(faculty.getUuid()).child(firebaseUser.getUid()).child(Long.toString(senderchatMessage.getMessageTime())).setValue(senderchatMessage);
                        }
                        editText.setText("");
                    }
                }
            });
        if (chatMessages.size() == 0) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.requestlocation) {
           checkUserPermission();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isuser==false) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.chatactivity, menu);
            return true;
        }
        else
        {
            return false;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(x);
                } else {
                    Toast.makeText(ChatActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
        }
        startActivity(x);
    }



}

