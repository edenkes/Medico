package bredesh.medico.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bredesh.medico.MedicoDB;
import bredesh.medico.R;

public class EditInfo extends AppCompatActivity {
    private MedicoDB dbManager;

    private String first_name;
    private String last_name;
    private String email_address;

    private EditText etFirstName, etLastName, etEmail;
    private Button btConfirm, btClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        dbManager = new MedicoDB(getApplicationContext());

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);

        if ((first_name = dbManager.getFirstName()) != null)
            etFirstName.setText(first_name);
        if ((last_name = dbManager.getLastName()) != null)
            etLastName.setText(last_name);
        if ((email_address = dbManager.getEmail()) != null)
            etEmail.setText(email_address);

        btConfirm = (Button) findViewById(R.id.btConfirm);
        btClear = (Button) findViewById(R.id.btClear);

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first_name = etFirstName.getText().toString();
                dbManager.setFirstName(first_name);
                dbManager.setLastName(etLastName.getText().toString());
                dbManager.setEmail(etEmail.getText().toString());

                finish();
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager.clearInfo();
                finish();
            }
        });

    }




}
