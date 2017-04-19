package project.prototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ReadStory extends Activity {

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.read_story_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.exit:
			Intent intent = new Intent(ReadStory.this, MainMenu.class);
			startActivity(intent);

			break;

		}
		return false;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_gallery);

	}
}