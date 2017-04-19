package project.prototype;

import java.util.ArrayList;
import android.view.View;

public class Story {
	
	private ArrayList<Page> Pages;
	private String storyName;
	private String creator;//username of the creator
	private View coverPage;//coverPage to be shown on the gallery
	
	public Story(){
		Pages = new ArrayList<Page>();
		//initialize story starting from the first page, sync pageCounter
	}
	
	public void newPage(Page page){
		Pages.add(page);

	}
	
	public void removePage(int pageNo){
		Pages.remove(pageNo);
		
	}
	
	public boolean nextPage(){
		return false;
		//initialize next page return false if no next page
	}
	
	public boolean prevPage(){
		//initialize previous page return false if no previous page
		return false;
	}
	
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public View getCoverPage() {
		return coverPage;
	}
	public void setCoverPage(View coverPage) {
		this.coverPage = coverPage;
	}
	public String getStoryName() {
		return storyName;
	}

	public void setStoryName(String storyName) {
		this.storyName = storyName;
	}

	public ArrayList<Page> getPages() {
		return Pages;
	}

	public void setPages(ArrayList<Page> pages) {
		Pages = pages;
	}
	
	public int getStorySize(){
		return Pages.size();
	}
	
}
