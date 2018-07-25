package example.com.seereal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class MyPageActivity extends AppCompatActivity {

    TextView userName, userEmail;
    EditText testEdit;
    String testFirebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userName.setText(InitApp.sUser.getDisplayName());
        userEmail.setText(InitApp.sUser.getEmail());

        testEdit = (EditText) findViewById(R.id.testEdit);
        Button testBtn = (Button) findViewById(R.id.testBtn);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFirebase = testEdit.getText().toString();

                //DatabaseReference mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("info").child("category");
                DatabaseReference mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("test");
                mRef.setValue(testFirebase);
            }
        });

    }
}
