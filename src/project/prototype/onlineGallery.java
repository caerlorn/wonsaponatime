package project.prototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class onlineGallery extends Activity {// Handles the online gallery

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.online_gallery_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		switch (itemId) {

		case R.id.exit:
			Intent exitIntent = new Intent(onlineGallery.this, MainMenu.class);
			startActivity(exitIntent);

			break;

		case R.id.goLocal:
			Intent localIntent = new Intent(onlineGallery.this, Gallery.class);
			startActivity(localIntent);

			break;

		}
		return false;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_gallery);

	}
}