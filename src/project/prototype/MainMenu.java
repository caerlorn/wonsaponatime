package project.prototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainMenu extends Activity {

	private static final String MY_DB = "my_db";
	SharedPreferences sp;
	Editor e;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		sp = getSharedPreferences(MY_DB, Context.MODE_PRIVATE);
		
		
//-----------------//Login
		ImageButton login = (ImageButton) findViewById(R.id.login);
		
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainMenu.this, Login.class);
				startActivity(intent);

			};// end setOnClickListener
		});// end setOnClickListener
		
//-----------------//Create New Story		
		ImageButton createNewStory = (ImageButton) findViewById(R.id.createNewStory);

		createNewStory.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Logged in as:" + sp.getString("activeUser", "NO_USER"), Toast.LENGTH_SHORT).show();//debug item
				Intent intent = new Intent(MainMenu.this, CreateStory.class);
				startActivity(intent);

			}// end onClick
		}// end onClickListener
				);// end setOnClickListener
		
//-----------------//Quit button
		ImageButton quitBtn = (ImageButton) findViewById(R.id.quit);
		quitBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				System.exit(0);
			}
		});
		
//-----------------//Read Story button
		ImageButton readStory = (ImageButton) findViewById(R.id.readStory);

		readStory.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainMenu.this, ReadStory.class);
				startActivity(intent);

			}
		});// end onClickListener
		
//-----------------//View gallery button
		ImageButton viewGallery = (ImageButton) findViewById(R.id.viewGallery);

		viewGallery.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainMenu.this, onlineGallery.class);
				startActivity(intent);

			}
		});// end onClickListener

	}// end onCreate
	
//--------------------------------------------------------------------//
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}// end onCreateOptionsMenu

}
