package com.elisvobs.yard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String[] provinces = {" ", "Bulawayo", "Harare", "Manicaland",
            "Mashonaland Central", "Mashonaland East", "Mashonaland West","Masvingo",
            "Matebeleland North", "Matebeleland South", "Midlands"};
    public static final String[] education = {" ","Ordinary Level", "Advanced Level",
            "National Certificate", "National Diploma","Higher National Diploma",
            "Bachelors Degree", "Masters Degree", "Doctorate"};

    RadioGroup radioGroup;
    Spinner spinner, mSpinner;
    RadioButton radioButton;
    TextInputEditText nameField, phoneField,ageField, idField, occupationField, wardField,
            villageField, districtField, constituencyField, emailField;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String userId;
    private DatabaseReference mFirebaseDatabase;
    FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    // instantiate the DB
                    mFirebaseInstance = FirebaseDatabase.getInstance();
                    // get reference to 'users' node
                    mFirebaseDatabase = mFirebaseInstance.getReference("users");
                }
            }
        };

        nameField = findViewById(R.id.name);
        ageField = findViewById(R.id.age);
        idField = findViewById(R.id.idNumber);
        occupationField = findViewById(R.id.occupation);
        mSpinner = findViewById(R.id.spinner1);
        villageField = findViewById(R.id.village);
        wardField = findViewById(R.id.ward);
        constituencyField = findViewById(R.id.constituency);
        districtField = findViewById(R.id.district);
        spinner = findViewById(R.id.spinner);
        phoneField = findViewById(R.id.phone);
        emailField = findViewById(R.id.email);
        radioGroup = findViewById(R.id.sex);

        ageField.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                radioButton = radioGroup.findViewById(checkedId);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, education);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)
                        +" education", Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, provinces);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getBaseContext(), adapterView.getItemAtPosition(position)
                        +" province", Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onClick(View view){
        // calender class's instance
        final Calendar calendar = Calendar.getInstance();
        // date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        ageField.setText(DateFormat.getDateInstance(DateFormat.SHORT)
                                .format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void saveEntry(View view) {
        TextView textView = (TextView) mSpinner.getSelectedView();
        TextView textView1 = (TextView) spinner.getSelectedView();

        String name = nameField.getEditableText().toString().trim();
        String age = ageField.getEditableText().toString().trim();
        String gender = radioButton.getText().toString();
        String idNumber = idField.getEditableText().toString().trim();
        String occupation = occupationField.getEditableText().toString().trim();
        String education = textView.getText().toString();
        String village = villageField.getEditableText().toString().trim();
        String ward = wardField.getEditableText().toString().trim();
        String district = districtField.getEditableText().toString().trim();
        String constituency = constituencyField.getEditableText().toString().trim();
        String province = textView1.getText().toString();
        String phone = phoneField.getEditableText().toString().trim();
        String email = emailField.getEditableText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || idNumber.isEmpty() || occupation.isEmpty() ||
                education.isEmpty() || village.isEmpty() || ward.isEmpty() || district.isEmpty() ||
                province.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please Fill All the Fields", Toast.LENGTH_SHORT).show();
        } else {
            checkUserId(name, age, gender, idNumber, occupation, education, village, ward, district,
                    constituency, province, phone, email);
        }
    }

    private void checkUserId(String name, String age, String gender, String idNumber, String occupation, String education, String village, String ward, String district, String constituency, String province, String phone, String email) {
        // Check for already existed userId
        if (TextUtils.isEmpty(userId)){
            createUser(name, age, gender, idNumber, occupation, education, village, ward, constituency, district, province, phone, email);
            } else {
            updateUser(name, age, gender, idNumber, occupation, education, village, ward, constituency, district, province, phone, email);
            }
    }

    private void createUser(String name, String age, String gender, String idNumber, String occupation, String education, String village, String ward, String constituency, String district, String province, String phone, String email) {
        if (TextUtils.isEmpty(userId)){
            userId = mFirebaseDatabase.push().getKey();
            Toast.makeText(this, "New Entry Successfully Entered. Add more!",
                    Toast.LENGTH_LONG).show();
        }

        User user = new User(name, age, gender, idNumber, occupation, education, village, ward, constituency, district, province, phone, email);
        mFirebaseDatabase.child(userId).setValue(user);
        addUserChangeListener();
    }

    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }
                clearSetValues();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void clearSetValues() {
        nameField.getEditableText().clear();
        ageField.getEditableText().clear();
        radioGroup.clearCheck();
        idField.getEditableText().clear();
        occupationField.getEditableText().clear();
        mSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_dropdown_item_1line, education));
        villageField.getEditableText().clear();
        wardField.getEditableText().clear();
        constituencyField.getEditableText().clear();
        districtField.getEditableText().clear();
        spinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_dropdown_item_1line, provinces));
        phoneField.getEditableText().clear();
        emailField.getEditableText().clear();
    }

    private void updateUser(String name, String age, String gender, String idNumber, String occupation, String education, String village, String ward, String constituency, String district, String province, String phone, String email) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(userId).child("Name").setValue(name);

        if (!TextUtils.isEmpty(age))
            mFirebaseDatabase.child(userId).child("D.O.B").setValue(age);

        if (!TextUtils.isEmpty(gender))
            mFirebaseDatabase.child(userId).child("Gender").setValue(gender);

        if (!TextUtils.isEmpty(idNumber))
            mFirebaseDatabase.child(userId).child("Id Number").setValue(idNumber);

        if (!TextUtils.isEmpty(occupation))
            mFirebaseDatabase.child(userId).child("Occupation").setValue(occupation);

        if (!TextUtils.isEmpty(education))
            mFirebaseDatabase.child(userId).child("Education").setValue(education);

        if (!TextUtils.isEmpty(village))
            mFirebaseDatabase.child(userId).child("Village").setValue(village);

        if (!TextUtils.isEmpty(ward))
            mFirebaseDatabase.child(userId).child("Ward").setValue(ward);

        if (!TextUtils.isEmpty(constituency))
            mFirebaseDatabase.child(userId).child("Constituency").setValue(constituency);

        if (!TextUtils.isEmpty(district))
            mFirebaseDatabase.child(userId).child("District").setValue(district);

        if (!TextUtils.isEmpty(province))
            mFirebaseDatabase.child(userId).child("Province").setValue(province);

        if (!TextUtils.isEmpty(phone))
            mFirebaseDatabase.child(userId).child("WhatsApp Contact").setValue(phone);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(userId).child("Email Address").setValue(email);
        Toast.makeText(this, "Entry Successfully Updated!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logout:
                signOut();
                break;
//            case R.id.profile:
//                startActivity(new Intent(this, ProfileActivity.class));
//                finish();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}