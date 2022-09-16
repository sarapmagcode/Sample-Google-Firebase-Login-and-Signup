package com.fourbytes.learningfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private TextView txtWelcome;

    private Button btnLogout;
    private Button btnSend;

    private EditText edtMessage;

    private ArrayList<Message> messageList;

    private RecyclerView rvMessages;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        txtWelcome = findViewById(R.id.txt_welcome);
        rvMessages = findViewById(R.id.rv_messages);
        messageList = new ArrayList<>();
        edtMessage = findViewById(R.id.edt_message);
        btnLogout = findViewById(R.id.btn_logout);
        btnSend = findViewById(R.id.btn_send);

        final DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(HomeActivity.this, "Listen failed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null && value.exists()) {
                    name = value.getData().get("firstName").toString();
                    txtWelcome.setText(txtWelcome.getText().toString() + ", " + name);
                } else {
                    Toast.makeText(HomeActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        db.collection("groupChat").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(HomeActivity.this, "Listen failed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                messageList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        messageList.add(new Message(doc.getString("name"), doc.getString("message"), doc.getString("timestamp")));
                    }
                }

                Collections.sort(messageList, new Comparator<Message>() {
                    @Override
                    public int compare(Message message, Message other) {
                        Timestamp ts1 = Timestamp.valueOf(message.getTimestamp());
                        Timestamp ts2 = Timestamp.valueOf(other.getTimestamp());

                        return ts1.compareTo(ts2);
                    }
                });
                updateMessages();
            }
        });

        updateMessages();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Please enter a message.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Timestamp ts = new Timestamp(System.currentTimeMillis());
                db.collection("groupChat").document().set(new Message(name, message, ts.toString()));

                rvMessages.scrollToPosition(messageList.size() - 1);
                edtMessage.setText("");
            }
        });
    }

    private void updateMessages() {
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(messageList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setItemAnimator(new DefaultItemAnimator());
        rvMessages.setAdapter(recyclerAdapter);
    }
}