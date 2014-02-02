package nz.co.cyma.integrations.archigitsync.model;


public interface IFolderPath {
	/**
	 * Return the full folder path as a path string i.e. folder names separated by a /
	 * @return
	 */
	public String getPathAsString();
	//public void setFolder(String folderName);
	
	
	/**
	 * Get the folder at the bottom of the stack without removing it i.e. the current folder
	 * @return The current folder name
	 */
	public String getBottomFolder();
	
	
	/**
	 * Add a folder to the path
	 * @param folderName
	 * @return The folder path after the folder has been added
	 */
	public String addFolderToPath(String folderName);
	
	/**
	 * Remove folder that is at the bottom of the path and make the one above it the one at the bottom
	 * @return The folder path after the folder has been removed
	 */
	public String moveUpFolderTree();
	
	/**
	 * Return a string representing the core location types where folders can be created e.g. Business Layer
	 * @return String representing type
	 */
	public String getFolderPathTypeAsString();
	
	public IFolderPath clonePath();
}
