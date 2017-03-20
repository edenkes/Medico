package bredesh.medico.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bredesh.medico.MainActivity;
import bredesh.medico.R;


public class MainLoginActivity extends AppCompatActivity  implements View.OnClickListener{
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
    }*/

    private final int size = 8;
    private DbHelper db;
    private Session session;
    private Button login, goregister, register, gologin;
    private EditText etEmailLog, etPassLog, etEmailReg, etPassReg, etPassConfirmReg;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //global
        db = new DbHelper(this);
        session = new Session(this);

        setLogin();

        if(session.loggedin()){
            startActivity(new Intent(MainLoginActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnGoToRegister:
                setContentView(R.layout.activity_register);
                setRegister();
                break;
            case R.id.btnRegister:
                register();
                break;
            case R.id.btnGoToLogin:
                setContentView(R.layout.activity_login);
                setLogin();
                break;
            default:
                break;
        }
    }

    private void setLogin(){
        //Login Content
        login = (Button)findViewById(R.id.btnLogin);
        goregister = (Button)findViewById(R.id.btnGoToRegister);
        etEmailLog = (EditText)findViewById(R.id.etEmailLog);
        etPassLog = (EditText)findViewById(R.id.etPassLog);

        login.setOnClickListener(this);
        goregister.setOnClickListener(this);
    }

    private void setRegister(){
        //Register Content
        register = (Button)findViewById(R.id.btnRegister);
        gologin = (Button)findViewById(R.id.btnGoToLogin);
        etEmailReg = (EditText)findViewById(R.id.etEmailReg);
        etPassReg = (EditText)findViewById(R.id.etPassReg);
        etPassConfirmReg = (EditText) findViewById(R.id.etPassConfirmReg);

        register.setOnClickListener(this);
        gologin.setOnClickListener(this);

    }

    private void login(){
        String email = etEmailLog.getText().toString();
        String pass = etPassLog.getText().toString();

        if(db.getUser(email,pass)){
            session.setLoggedin(true);
            startActivity(new Intent(MainLoginActivity.this, MainActivity.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Wrong email/password",Toast.LENGTH_SHORT).show();
        }
    }

    private void register(){
        String email = etEmailReg.getText().toString();
        String pass = etPassReg.getText().toString();
        String passConfirm = etPassConfirmReg.getText().toString();
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
            setContentView(R.layout.activity_login);
            setLogin();
        }
    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
