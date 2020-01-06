package com.elisvobs.yard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);
        setTitle("Please Log in");

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar1);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
    }

    public void login(View v) {
        String email = inputEmail.getEditableText().toString();
        final String password = inputPassword.getEditableText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign-in fails, display a message to the user else the auth state listener will be notified & logic to handle the signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    public void signUp(View v) {
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    public void resetPassword(View v) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_reset_password, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText editEmail = dialogView.findViewById(R.id.email);
        final MaterialButton btnReset = dialogView.findViewById(R.id.btn_reset_password);
        final ProgressBar progressBar1 = dialogView.findViewById(R.id.progressBar2);

        //dialogBuilder.setTitle("Send Photos");
        final AlertDialog dialog = dialogBuilder.create();

        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = editEmail.getEditableText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar1.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
                                            "We have sent you instructions to reset your password!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                progressBar1.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                        });
            }
        });
        dialog.show();
    }

}