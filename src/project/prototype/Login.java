package project.prototype;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	//Data Field
	EditText usernameField;
	EditText passwordField;
	String MY_DB;
	SharedPreferences sp;
	int userCounter;
	Editor e;
	boolean userVerify;//Set true if login is succesful

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		MY_DB ="my_db";
		sp = getSharedPreferences(MY_DB, Context.MODE_PRIVATE);
		userVerify = false;
		
		usernameField = (EditText) findViewById(R.id.usernameField);
		passwordField = (EditText) findViewById(R.id.passwordField);
		userCounter = sp.getInt("userCounter", 0);
		
		Button submit = (Button) findViewById(R.id.submit);
		Button cancel = (Button) findViewById(R.id.cancel);
		Button newAccount = (Button) findViewById(R.id.newAccount);
		Button debugButton = (Button) findViewById(R.id.debugButton);//Used to autoinsert name and password for testing, delete later on.
		Button clearButton = (Button) findViewById(R.id.clearButton);//Clear database
		
		//Debug Button
		debugButton.setOnClickListener(new OnClickListener(){//debug item
			public void onClick(View v){
				 usernameField.setText("cem");
				 passwordField.setText("1234");
			}//end onClick
		});//end debug button
		
		//Clear Button
		clearButton.setOnClickListener(new OnClickListener(){//debug item
			public void onClick(View v){
				e = sp.edit();
				e.clear();
				e.commit();
			}
		});//end setOnClickListener
		
		//Submit Button
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = usernameField.getText().toString();
				String password = passwordField.getText().toString();
				String userNameBuffer;
				String passwordBuffer;
				e = sp.edit();
				
				//Check if either EditText is null(form is filled or not) 
				if(userName.isEmpty() || password.isEmpty() ){
					Toast.makeText(getApplicationContext(), "Please Fill The Form", Toast.LENGTH_SHORT).show();
				}
				else{//Form filled as required
					//Search for inputted name and password 
					for(int i = 0;i < userCounter;i++){
						userNameBuffer = sp.getString("name" + i, "error");
						passwordBuffer = sp.getString("pass" + i, "error");
						
						//Data error
						if(userNameBuffer == "error" || passwordBuffer == "error"){
							Toast.makeText(getApplicationContext(), "NO USER", Toast.LENGTH_SHORT).show();
							break;
						}
					
						//Successful login
					 	if(userNameBuffer.equals(userName) && passwordBuffer.equals(password) ){
					 		userVerify = true;
					 	}
		
					}//end for
				
				if(userVerify == true){
				Toast.makeText(getApplicationContext(), "Successful Login\nWelcome " + userName, Toast.LENGTH_SHORT).show();
				e = sp.edit();
				e.putString("activeUser", userName);
				e.commit();
				Intent intent = new Intent(Login.this,MainMenu.class);
				startActivity(intent);
				}
				else if(userVerify == false){
				Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
				}

				}//end else
			};// end onClick
		});//end setOnClickListener
		
		//Cancel button
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Login.this,MainMenu.class);
				startActivity(intent);
			}
		});

		//TODO: control structure to block account duplicates , ex: user 1234 added, user 1234 added
		//New Account Button
		newAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
			String userName = usernameField.getText().toString();
			String password = passwordField.getText().toString();
			e = sp.edit();
			
			if(password.length() < 4){//password cannot be less than 4 digits
				Toast.makeText(getApplicationContext(), "Password must be at least 4 digits", Toast.LENGTH_SHORT).show();
			}
			else{
			e.putString("name" + userCounter, userName);
			e.putString("pass" + userCounter, password);
			e.remove("userCounter");//no need if putInt overwrites the previous userCounter
			int incrementHolder = userCounter + 1;
			e.putInt("userCounter",incrementHolder);
			e.putString("activeUser", userName);
			e.commit();
			Toast.makeText(getApplicationContext(), " " + sp.getInt("userCounter",-1), Toast.LENGTH_SHORT).show();//debug item
			
			Toast.makeText(getApplicationContext(), "New User Created\nWelcome " + userName, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Login.this,MainMenu.class);
			startActivity(intent);
			}//end else
			}//end onClick
		});//end setOnClickListener 
		
	}//end onCreate
}//EOA