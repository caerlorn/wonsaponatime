package project.prototype;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class Page {

	private static final String LOG_REMOVE = "Remove object";
	private int idHolder;
	private ArrayList<StoryObject> imageList;
	private ArrayList<StoryObject> textList;
	private ArrayList<StoryObject> drawList;
	Drawable background;

	//Create objects, query data, obtain data, fill objects with data
	public Page(){
		idHolder = 0;
		imageList = new ArrayList<StoryObject>();
		textList = new ArrayList<StoryObject>();
		drawList = new ArrayList<StoryObject>();
		
		//Query data
		
		//Fill lists with data
		
		//create storyobjects and sync with data
	}//end initialize
	
	//Edit section
	
	public void addDraw(View v){
		StoryObject so = new StoryObject();
		so.setView(v);
		so.setId("draw" + idHolder++);
		drawList.add(so);
	}
	
	public void removeDraw(View v){
		drawList.remove(v);
	}
	
	public String addImage(View v){
		StoryObject so = new StoryObject(); 
		so.setView(v);
		so.setId("image" + idHolder++);
		imageList.add(so);
		return so.getId();
	}

	public void removeImage(View v){
		boolean returnFlag = imageList.remove(v);
		if(returnFlag == true)
			Log.e(LOG_REMOVE, "Image removed");
	}
	
	public void addText(View v){
		StoryObject so = new StoryObject();
		so.setView(v);
		so.setId("text" + idHolder++);
		textList.add(so);
	}
	
	public void removeText(View v){
		boolean returnFlag = textList.remove(v);
		if(returnFlag == true)
			Log.e(LOG_REMOVE, "Text removed");
	}
	
	
	public Drawable getBackground() {
		return background;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	//return image or text according to parameters from the currentpageno
	public StoryObject getParticularObject(int currentPageNo, String type){
		if(type.equals("image")){
			return imageList.get(currentPageNo);
		}
		else if(type.equals("text")){
			return textList.get(currentPageNo);
		}
		return null;
	}
	public ArrayList<StoryObject> getImageList() {
		return imageList;
	}

	public ArrayList<StoryObject> getTextList() {
		return textList;
	}
	
	
	public ArrayList<StoryObject> getDrawList() {
		return drawList;
	}
	
	//TODO
	//Remove any StoryObject with null view
	public void cleanLists(){
		//traverse lists
		//remove null views
	}
}
