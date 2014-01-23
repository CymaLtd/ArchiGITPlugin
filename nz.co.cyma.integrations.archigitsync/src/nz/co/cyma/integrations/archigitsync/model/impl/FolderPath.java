package nz.co.cyma.integrations.archigitsync.model.impl;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;


import uk.ac.bolton.archimate.model.FolderType;

import nz.co.cyma.integrations.archigitsync.model.IFolderPath;

public class FolderPath implements IFolderPath, Cloneable {
	ArrayDeque<String> folderStack = new ArrayDeque<String>();
	private FolderType folderPathType = null;
	
	public FolderPath(FolderType folderPathType) {
		this.folderPathType = folderPathType;
	}
	
	public FolderPath(FolderType folderPathType, String folderPathString) {
		this.folderPathType = folderPathType;
		StringTokenizer dirs = new StringTokenizer(folderPathString, "/");
		while(dirs.hasMoreElements()) {
			this.addFolderToPath((String) dirs.nextElement());
		}
	}
	
	@Override
	public String getPathAsString() {
		String fullPath = null;
		
		//need to reverse the order when getting as a string...
		Iterator<String> i = folderStack.descendingIterator();
		
		while(i.hasNext()) {
			String currentFolder = i.next();
			if(fullPath ==null)
				fullPath = currentFolder;
			else
				fullPath = fullPath + "/" + currentFolder;
		}

		return (fullPath==null?"":fullPath);
	}//getPathAsString
	
	public String [] getPathAsArray() {
		String [] folders = new String[folderStack.size()];
		
		//need to reverse the order when getting as a string...
		Iterator<String> i = folderStack.descendingIterator();
		int ctr = 0;
		
		while(i.hasNext()) {
			folders[ctr] = i.next();
			ctr++;
		}
		
		return folders;
	}


	@Override
	public String getBottomFolder() {
		// TODO Auto-generated method stub
		return (folderStack.peekLast()==null?"":folderStack.peekLast());
	}

	@Override
	public String addFolderToPath(String folderName) {
		folderStack.push(folderName);
		return this.getPathAsString();
	}

	@Override
	public String moveUpFolderTree() {
		folderStack.pop();
		return this.getPathAsString();
	}

	@Override
	public String getFolderPathTypeAsString() {
		return this.folderPathType.getName();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public IFolderPath clonePath() {
		try {
			return (FolderPath) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
