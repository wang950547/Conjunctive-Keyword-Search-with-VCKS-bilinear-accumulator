package tw.com.encrypt;

import java.util.ArrayList;

public class KeywordIndex {
	
	private String keyword;
	private ArrayList<Integer> fileIndex;
	
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}
	
	public void addIndex(int fileIndex){
		this.fileIndex.add(fileIndex);
	}

}
