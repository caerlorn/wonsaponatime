package project.prototype;

//An Interactive Story Book Creator and Reader for Kids
//Author(s): Cem Süzen 
//			 Yýldýrým Can Þehirlioðlu
//			 Alp Buðra Ekti
//			 Volkan Saydam

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CreateStory extends Activity{

	//Data Field
	//Layouts
	RelativeLayout touchView;//Holds story objects
	private RelativeLayout rootView;
	//RelativeLayout drawLayout;
	EditText input;
	//Image Dialog choices
	String[] choiceArr = {"Insert Image","Set Background","Take a Photo"};
	private static boolean backgroundFlag;
	boolean deleteFlag;
    private boolean scaleFlag;//ScaleFlag
	//Voice Record
	private String fileName = null;
	private static final String LOG_TAG = "AudioRecord";
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	boolean mStartRecording = false;
	boolean mStartPlaying = false;
	//Story Object
	Story currentStory;//Current story that is being created
	private int currentPageNo;//No of the current page we are editing, sync with the Pages ArrayList's index(-1 of ArrayList's size)
	Page dynPage;
	//Gesture
	GestureDetectorCompat  gestDetector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    AlertDialog.Builder builder;
    //Take Photo Related
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private static final int MEDIA_TYPE_IMAGE = 1;
	//Volkan
	String[] choiceColor = {"BLACK","BLUE","CYAN","GRAY","GREEN","MAGENTA","RED","YELLOW","QUIT DRAWING"};//VOLKAN
    private String color;//VOLKAN
    SimpleDrawView drawView;//VOLKAN

//-----------------------------------------	
    
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_create_story);
			
			touchView = (RelativeLayout) findViewById(R.id.touchView);//initialize layouts
			rootView = (RelativeLayout) findViewById(R.id.rootView);
			
			//drawLayout = (RelativeLayout) findViewById(R.id.drawLayout);
			
			
			
			
			gestDetector = new GestureDetectorCompat (this,new MyGestureListener());

			currentPageNo = 0;
			//Create new story and its first page
			currentStory = new Story();
			dynPage = new Page();
			//TODO get story name, set story name to current story(DialogBox)
			currentStory.newPage(dynPage);
			
			//----------------------debug section
			Toast.makeText(getApplicationContext(), "StorySize: " + currentStory.getStorySize() + "\n" + "CurrPageNo: " + currentPageNo, Toast.LENGTH_LONG).show();
			//---------------------/end debug section
			
			fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
			fileName += "/audiorecord.3gp";
			
			
		}//end onCreate

		
//---------------------------------------------
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.create_story_menu, menu);
		return true;
	}
//----------------------------------------------
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.drawCrayon:
			
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	    	
			builder1.setTitle("Choose a color!")
			.setItems(choiceColor, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog1,int which1){
					drawView = new SimpleDrawView(CreateStory.this);
					
					switch(which1){
					case 0: color = "BLACK"; break;
					case 1: color = "BLUE"; break;
					case 2: color = "CYAN"; break;
					case 3: color = "GRAY"; break;
					case 4: color = "GREEN"; break;
					case 5: color = "MAGENTA"; break;
					case 6: color = "RED"; break;
					case 7: color = "YELLOW"; break;
					case 8: drawView.setDrawFlag(false); break;
					}
					drawView.setLineColor(color);
					currentStory.getPages().get(currentPageNo).addDraw(drawView);
					touchView.addView(drawView);
				}//end onClick
			});//end setitems
			AlertDialog dialog1 = builder1.create();
			dialog1.show();
			break;
		case R.id.record://-----------------------------------------------------------------Record Voice
			if(!mStartRecording && mStartPlaying == false){//if not recording, proceed to recording
			mStartRecording = true;
			onRecord(mStartRecording);
				item.setIcon(R.drawable.exit_icon);
			} else if(mStartRecording && mStartPlaying == false) {//Currently recording,quit recording
				item.setIcon(R.drawable.record);
				mStartRecording = false;
				onRecord(mStartRecording);
				Toast.makeText(getApplicationContext(), "Saved to: " + fileName, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.playAudio://----------------------------------------------------------------play audio
			if(!mStartPlaying && mStartRecording == false){//if not playing, proceed to playing
				mStartPlaying = true;
				onPlay(mStartPlaying);
					item.setIcon(R.drawable.stopaudio);
				} else if(mStartPlaying && mStartRecording == false) {//Currently playing,quit playing
					item.setIcon(R.drawable.playaudio);
					mStartPlaying = false;
					onPlay(mStartPlaying);
				}
			break;
		case R.id.deleteElement://------------------------------------------------------Delete Element
			Toast.makeText(getApplicationContext(), "Tap on Object to delete", Toast.LENGTH_SHORT).show();
			deleteFlag = true;
			break;
		case R.id.exit://---------------------------------------------------------------Exit
			Intent intent = new Intent(CreateStory.this, MainMenu.class);
			startActivity(intent);
			break;

		case R.id.addImage://-----------------------------------------------------------Add Image
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick One")
			.setItems(choiceArr, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int which){
					if(which == 0){
						sendBroadcast(new Intent(
								Intent.ACTION_MEDIA_MOUNTED,
								Uri.parse("file://"
								+ Environment
								.getExternalStorageDirectory())));

						Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, 1);
					}
					else if(which == 1){
						backgroundFlag = true;
						sendBroadcast(new Intent(
								Intent.ACTION_MEDIA_MOUNTED,
								Uri.parse("file://"
								+ Environment
								.getExternalStorageDirectory())));

						Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, 1);
					}
					else if(which == 2){
						Intent takePhotoIntent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

						takePhotoIntent.putExtra(
								MediaStore.EXTRA_OUTPUT, fileUri); // set
																	// the
																	// image
																	// file
																	// name

						// start the image capture Intent
						startActivityForResult(takePhotoIntent,
								CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					}
				}//end onClick
			});//end setitems
			AlertDialog dialog = builder.create();
			dialog.show();
			break;

		case R.id.addText://-----------------------------------------------------------Add Text
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Tell your story:");

			input = new EditText(CreateStory.this);

			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String value = input.getText().toString();

							EditText t = new EditText(CreateStory.this);
							t.setOnTouchListener(new OnTouchListener() {
								@Override
								public boolean onTouch(View arg0,
										MotionEvent arg1) {
									handleTouchText(arg0, arg1);
									return true;
								}
							});
							t.setText(value);
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							params.addRule(RelativeLayout.CENTER_HORIZONTAL);
							params.addRule(RelativeLayout.ALIGN_TOP,
									R.id.arrowLeft);
							Typeface tf = Typeface.createFromAsset(getAssets(),
									"fonts/CoconRegular.ttf");
							t.setTypeface(tf);
							t.setTextSize(25);
							
							//Text added to the current page
							currentStory.getPages().get(currentPageNo).addText(t);
							
							touchView.addView(t, params);
							touchView.postInvalidate();

						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert.show();

			break;
		case R.id.addPage://----------------add new page
			touchView.removeAllViews();
			dynPage = new Page();
			currentStory.newPage(dynPage);
			currentPageNo = currentStory.getStorySize() - 1;
			touchView.setBackground(null);
			touchView.postInvalidate();
			Toast.makeText(getApplicationContext(), "PAGE:" + currentStory.getStorySize() + "\nCurrentPageNo: "+ currentPageNo , Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.scaleImage://--------------scale image
			Toast.makeText(getApplicationContext(), "Tap an image to scale", Toast.LENGTH_LONG).show();
			scaleFlag = true;
			break;

		}//end switch
		return false;

	}

//------------------------------------------Handle Touch

	
	public void handleTouchImage(ImageView v, MotionEvent e) {

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(deleteFlag == true){
				((ViewManager)v.getParent()).removeView(v);//delete view from layout
				currentStory.getPages().get(currentPageNo).removeImage(v);//delete view from data structure
				deleteFlag = false;
				Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
			}
			else if(scaleFlag == true){
				nonScaledView = (ImageView) v;
				scaleDialogInitiate();
				scaleFlag = false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			v.setX(v.getX() - (v.getWidth()/2) + e.getX());
			v.setY(v.getY() - (v.getHeight()/2) + e.getY());
			break;
		case MotionEvent.ACTION_UP:
			break;

		}

	}// end handleTouchImage
	
	public void handleTouchText(View v, MotionEvent e) {
		
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(deleteFlag == true){
				((ViewManager)v.getParent()).removeView(v);//delete view from layout
				currentStory.getPages().get(currentPageNo).removeText(v);//delete view from data structure
				deleteFlag = false;
				Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			v.setX(v.getX() - (v.getWidth()/2) + e.getX());
			v.setY(v.getY() - (v.getHeight()/2) + e.getY());
			break;
		case MotionEvent.ACTION_UP:

			break;

		}

	}// end handleTouchText
	
//---------------------------------------------Image process
	@SuppressWarnings("deprecation")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){
		if (resultCode == RESULT_OK) {
			Uri chosenImageUri = data.getData();
			try {
				Bitmap mBitmap = Media.getBitmap(this.getContentResolver(),
						chosenImageUri);
				Drawable d = new BitmapDrawable(getResources(), mBitmap);
				
				//Not a background image
				if(backgroundFlag == false){
				ImageView view = new ImageView(getApplicationContext());
				view.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						handleTouchImage((ImageView) arg0, arg1);
						return true;
					}
				});
				view.setImageDrawable(d);
				displayImages(view);
				
				currentStory.getPages().get(currentPageNo).addImage(view);
				}
				
				else if(backgroundFlag == true){
				currentStory.getPages().get(currentPageNo).setBackground(d);
				touchView.setBackground(d);
				touchView.postInvalidate();
				backgroundFlag = false;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}//end if (resultCode == RESULT_OK)
		}
	}
	
private void displayImages(View view){ //add images and refresh layout
		touchView.addView(view);
		touchView.postInvalidate();
	
}

//----------------------------------------Voice record section
	public void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}
	
	public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

	public void stopPlaying() {
	      mPlayer.release();
	        mPlayer = null;
	        
	}
	public void startPlaying() {
	       mPlayer = new MediaPlayer();
	        try {
	            mPlayer.setDataSource(fileName);
	            mPlayer.prepare();
	            mPlayer.start();
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "prepare() failed");
	        }
	}
	public void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(fileName);
		MediaRecorder.getAudioSourceMax();
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	public void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}
//--------------------------------------------End Voice record section
	
//-------------------------------------------Page Load Methods
	
	public void loadPage(){
		emptyPage();
		fillPage();
		
	}
	
	//Clear all views from the current page
	public void emptyPage(){
		touchView.removeAllViews();
	}
	
	//Data field
	private ImageView newImage;
	private TextView newText;
	private View newDraw;//VOLKAN
	private static final String LOG_TAG_LOAD = "Load Successful";
	
	//Load all views from the desired page to current page
	public void fillPage(){
		//Initialize images of the page
		int accessIterator = 0;
		Iterator<StoryObject> IterImage = currentStory.getPages().get(currentPageNo).getImageList().listIterator();
		Iterator<StoryObject> IterText = currentStory.getPages().get(currentPageNo).getTextList().listIterator();
		Iterator<StoryObject> IterDraw = currentStory.getPages().get(currentPageNo).getDrawList().listIterator();//VOLKAN
		
			//Load Images
			while(IterImage.hasNext()){//image arraylist has next element
			newImage = new ImageView(getApplicationContext());
			newImage = (ImageView) currentStory.getPages().get(currentPageNo).getImageList().get(accessIterator).getView();
			Log.e(LOG_TAG_LOAD, "Image inserted");
			touchView.addView(newImage);
			IterImage.next();
			accessIterator++;
			}
			//Load Texts
			accessIterator = 0;
			while(IterText.hasNext()){//image arraylist has next element
			newText = new TextView(getApplicationContext());
			newText = (TextView) currentStory.getPages().get(currentPageNo).getTextList().get(accessIterator).getView();
			Log.e(LOG_TAG_LOAD, "Text inserted");
			touchView.addView(newText);
			IterText.next();
			accessIterator++;
			}
			//STARTVOLKAN
			//Load Draw
			accessIterator = 0;
			while(IterDraw.hasNext()){//image arraylist has next element
			newDraw = new View(getApplicationContext());
			newDraw = (View) currentStory.getPages().get(currentPageNo).getDrawList().get(accessIterator).getView();
			Log.e(LOG_TAG_LOAD, "Draw inserted");
			touchView.addView(newDraw);
			IterDraw.next();
			accessIterator++;
			}//ENDVOLKAN
			//Load Background
			touchView.setBackground(currentStory.getPages().get(currentPageNo).getBackground());
			touchView.postInvalidate();
	}//end fillPage
	
//--------------------------------------------Gesture methods
	@Override
	public boolean onTouchEvent(MotionEvent event){
		this.gestDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
		
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		
		
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            
//----------NEXT PAGE
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && currentStory.getStorySize() > (currentPageNo + 1)) {
            	pageScreenshot();
            	currentPageNo++;
            	loadPage();
                Toast.makeText(CreateStory.this, Integer.toString(currentPageNo +1 ), Toast.LENGTH_SHORT).show();
               
                
//----------PREV PAGE
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && (currentPageNo + 1) > 1) {
            	pageScreenshot();
            	currentPageNo--;
            	loadPage();
                Toast.makeText(CreateStory.this, Integer.toString(currentPageNo + 1 ), Toast.LENGTH_SHORT).show();
                       
                
            }
        } catch (Exception e) {
            // nothing
        }
		return false;
	}
	
	}//end MyGestureListener
	
//--------------------------------Scale section
	private void scaleImage(ImageView view, int boundBoxInDp)
	{
	    // Get the ImageView and its bitmap
	    Drawable drawing = view.getDrawable();
	    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	    // Get current dimensions
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.
	    float xScale = ((float) boundBoxInDp) / width;
	    float yScale = ((float) boundBoxInDp) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}

	private int dpToPx(int dp)
	{
	    float density = getApplicationContext().getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	    
	    
	}//end Scale Image
	
	//ScaleDialogInitiate Related Data Field
	private  int scaleMultiplier = 0;
	private ImageView nonScaledView;
	EditText scaleMultInput;
	AlertDialog alert;

	public void scaleDialogInitiate(){

		builder = new AlertDialog.Builder(CreateStory.this);
		LayoutInflater inflater = this.getLayoutInflater();
		builder.setTitle("Scale Image")
		.setView(inflater.inflate(R.layout.scaleimage_dialog_layout, null))
		.setPositiveButton("Apply", new DialogInterface.OnClickListener() { 
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO: get scale multiplier and radiobutton choice then apply scale operation
				scaleMultInput = (EditText) alert.findViewById(R.id.scalemult);
				scaleMultiplier = Integer.parseInt(scaleMultInput.getText().toString());
				scaleImage(nonScaledView, scaleMultiplier );
				
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancelled
				
			}
		});//end builder.set
		
		alert = builder.create();
		
		alert.show();
	}//end scaleDialogInitiate
	
//-------------------------------------------------------Take Photo
	String filePath;	


		/** Create a file Uri for saving an image or video */
		private static Uri getOutputMediaFileUri(int type) {
			return Uri.fromFile(getOutputMediaFile(type));
		}

		/** Create a File for saving the image */
		private static File getOutputMediaFile(int type) {
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.

			File mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"MyCameraApp");
			// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d("MyCameraApp", "failed to create directory");
					return null;
				}
			}

			// Create a media file name
			Calendar c = Calendar.getInstance();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(c
					.getTime());
			File mediaFile;
			if (type == MEDIA_TYPE_IMAGE) {
				mediaFile = new File(mediaStorageDir.getPath() + File.separator
						+ "IMG_" + timeStamp + ".jpg");
			} else {
				return null;
			}

			return mediaFile;
		}//End take photo
//----------------------------------------------------------Page screenshot
		public void pageScreenshot() throws IOException{
			  Bitmap bitmap;
			  View v1 = findViewById(R.id.rootView);
			  v1.setDrawingCacheEnabled(true); 
			  bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			  v1.setDrawingCacheEnabled(false);
			  
			  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			  
			  
			  File f = new File(Environment.getExternalStorageDirectory()
			                    + File.separator + "PG_" + currentPageNo + ".jpg");
			  f.createNewFile();
			  FileOutputStream fo = new FileOutputStream(f);
			  fo.write(bytes.toByteArray());
			  fo.close();
		}//end pageScreenshot

//----------------------------------------------------------Save Story Method Field
/*String storyFileName = currentStory.getStoryName();
FileOutputStream outputStream;

		public void saveStory(){
			
				File newXmlFile = new File(Environment.getExternalStorageDirectory() + "/" + storyFileName + ".xml");
				
				try{
					newXmlFile.createNewFile();
				}catch(IOException e){
						Log.e("IOException", "exception in createNewFile() method");
					}
				
					FileOutputStream fileOs = null;
				try{
					fileOs = new FileOutputStream(newXmlFile);
				}catch(FileNotFoundException e){
					Log.e("FileNotFoundException", "can't create FileOutputStream");
				}
				
				XmlSerializer serializer = Xml.newSerializer();
				try {
					serializer.setOutput(fileOs, "UTF-8");
					serializer.startDocument(null, Boolean.valueOf(true));
					serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
					
					
					serializer.startTag(null, "root");
					
					ImageView newImage;
					TextView newText;
					int accessIterator = 0;
					Iterator<StoryObject> IterImage = currentStory.getPages().get(currentPageNo).getImageList().listIterator();
					Iterator<StoryObject> IterText = currentStory.getPages().get(currentPageNo).getTextList().listIterator();
					
						//Serialize Images
						while(IterImage.hasNext()){//image arraylist has next element
						newImage = new ImageView(getApplicationContext());
						newImage = (ImageView) currentStory.getPages().get(currentPageNo).getImageList().get(accessIterator).getView();
						
						serializer.startTag(null, "image" + accessIterator);	
						serializer.attribute(null, null, String.valueOf(newImage.getId()));
						serializer.endTag(null, "image" + accessIterator);
						
						Log.e("Image serialized", "Image serialized");
						IterImage.next();
						accessIterator++;
						}
						//Serialize Texts
						accessIterator = 0;
						while(IterText.hasNext()){//image arraylist has next element
						newText = new TextView(getApplicationContext());
						newText = (TextView) currentStory.getPages().get(currentPageNo).getTextList().get(accessIterator).getView();
						
						
						
						Log.e("Text serialized", "Text serialized");
						IterText.next();
						accessIterator++;
						}
				} 
		}//end saveStory
	*/
}// end activity