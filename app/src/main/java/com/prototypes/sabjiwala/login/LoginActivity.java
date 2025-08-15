package com.prototypes.sabjiwala.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prototypes.sabjiwala.R;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    private EditText editText;
    private Button btnContinue;
    ImageView tick_image;
    FirebaseFirestore fStore;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        editText = findViewById(R.id.editTextMobile);
        btnContinue = findViewById(R.id.buttonContinue);
        tick_image = findViewById(R.id.tick_image);
        tick_image.setVisibility(View.INVISIBLE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = editText.getText().toString().trim();
                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Please enter Valid number");
                    editText.requestFocus();
                    return;
                }
                String phoneNumber = "+91" + number;
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                startActivity(intent);
                finish();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    btnContinue.setEnabled(true);
                    tick_image.setVisibility(View.VISIBLE);
                } else {
                    btnContinue.setEnabled(false);
                    tick_image.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}