package bredesh.medico.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bredesh.medico.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private Button reg;
    private TextView tvLogin;
    private EditText etEmail, etPass, etPassConfirm;
    private DbHelper db;
    private final int size = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DbHelper(this);
        reg = (Button)findViewById(R.id.btnReg);
        tvLogin = (TextView)findViewById(R.id.tvLogin);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPass);
        etPassConfirm = (EditText)findViewById(R.id.etPassConfirm);
        reg.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

        reg = (Button)findViewById(R.id.btnReg);
        tvLogin = (TextView)findViewById(R.id.tvLogin);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPass);
        etPassConfirm = (EditText)findViewById(R.id.etPassConfirm);
        reg.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnReg:
                register();
                break;
            case R.id.tvLogin:
                startActivity(new Intent(RegisterActivity.this,MainLoginActivity.class));
                finish();
                break;
            default:

        }
    }

    private void register(){
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        String passConfirm = etPassConfirm.getText().toString();
        if(email.isEmpty()){
            displayToast("Username field can't be empty");
        }else if (pass.isEmpty()){
            displayToast("Password field can't be empty");
        }else if(!email.contains("@")){
            displayToast("Please enter a valid email");
        }else if(pass.length() < size){
            displayToast("Please enter a password with a length greater than " + size + " characters");
        }else if(!pass.equals(passConfirm)){
            displayToast("Entered passwords do not match");
        }else if(db.checkEmail(email)){
            displayToast("That email is already exists, please enter another");
        }
        else{
            db.addUser(email,pass);
            displayToast("You have successfully registered to Medico");
            finish();
        }
    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
