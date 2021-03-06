/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton Institute of Higher Education
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@bolton.ac.uk
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.diva.util;

import java.io.*;
import java.util.Arrays;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Some useful Zip Utilities
 *
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: ZipUtils.java,v 1.1 1998/03/25 20:21:17 ynsingh Exp $
 */
public final class ZipUtils {
	
	/**
	 * Add all files and sub-files to the Zip
	 * @param rootFolder The relative root folder
	 * @param srcFolder The folder to add to the zip file
	 * @param zOut
	 * @param zipFile
	 * @throws IOException
	 */
	public static void addFolderToZip(File rootFolder, File srcFolder, ZipOutputStream zOut, File zipFile) throws IOException {
		File[] files = srcFolder.listFiles();
		
		for(int i = 0; i < files.length; i++) {
			// Sub-folder
			if(files[i].isDirectory()) {
			    addFolderToZip(rootFolder, files[i], zOut, zipFile);
			}
			
			// Careful not to add the zip file to the zip file!
			else if(!files[i].equals(zipFile)) {
				// Get a relative path
			    String entryName = FileUtils.getRelativePath(rootFolder, files[i]);
				addFileToZip(files[i], entryName, zOut);
			}
		}
	}
	
	/**
	 * Add all files and sub-files to the Zip, but only include ones from filesToInclude array.
	 * @param rootFolder The relative offset root folder
	 * @param filesToInclude The files to include
	 * @param zOut
	 * @param zipFile
	 * @throws IOException
	 */
	public static void addFilesToZip(File rootFolder, File[] filesToInclude, ZipOutputStream zOut, File zipFile) throws IOException {
		for(int i = 0; i < filesToInclude.length; i++) {
			// Careful not to add the zip file to the zip file!
			if(!filesToInclude[i].equals(zipFile)) {
				if (filesToInclude[i].exists()){
					String entryName = FileUtils.getRelativePath(rootFolder, filesToInclude[i]);
					addFileToZip(filesToInclude[i], entryName, zOut);
				}
			}
		}
	}
	
	/**
	 * Adds a file to the Zip file
	 * @param file
	 * @param entryName
	 * @param zOut
	 * @throws IOException
	 */
	public static void addFileToZip(File file, String entryName, ZipOutputStream zOut) throws IOException {
		// Don't add directories
	    if(file.isDirectory()) {
	    	return;
	    } 
	    
	    int bytesRead;
		final int bufSize = 1024;
		byte buf[] = new byte[bufSize];
		
		// Not really needed, but you never know...
		// if(File.separatorChar != '/') entryName = entryName.replace(File.separatorChar, '/');

		ZipEntry zipEntry = new ZipEntry(entryName);
		// Set time stamp to file's
		zipEntry.setTime(file.lastModified());
		zOut.putNextEntry(zipEntry);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), bufSize);
		while((bytesRead = in.read(buf)) != -1) {
			zOut.write(buf, 0, bytesRead);
		}
		zOut.closeEntry();
		in.close();
	}

	/**
	 * Adds a String as a field entry to an already opened ZipOutputStream
	 * @param text
	 * @param entryName
	 * @param zOut
	 * @throws IOException
	 */
	public static void addStringToZip(String text, String entryName, ZipOutputStream zOut) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(text));
		
		ZipEntry zipEntry = new ZipEntry(entryName);
		
		zOut.putNextEntry(zipEntry);
		
		int i;
		while((i = reader.read()) != -1) {
			zOut.write(i);
		}
		zOut.closeEntry();
	}
	
	/**
	 * @param zipFile
	 * @param entryName
	 * @return True if the zip file has an entry
	 * @throws IOException
	 */
	public static boolean hasZipEntry(File zipFile, String entryName) throws IOException {
		ZipEntry zipEntry;
		ZipInputStream zIn;
		boolean foundEntry = false;
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile));
		zIn = new ZipInputStream(in);
		
		// Get zip entry
		while((zipEntry = zIn.getNextEntry()) != null) {
			String zipEntryName = zipEntry.getName();
			if(zipEntryName.equalsIgnoreCase(entryName)) {
				foundEntry = true;
				break;
			}
			zIn.closeEntry();
		}
		
		zIn.close();
		
		return foundEntry;
	}

	/**
	 * Extracts a named entry out of the zip file and returns the entry as a String
	 * Returns null if weirdness happens
	 * @param zipFile
	 * @param entryName
	 * @return
	 * @throws IOException
	 */
	public static String extractZipEntry(File zipFile, String entryName) throws IOException {
		ZipEntry zipEntry;
		ZipInputStream zIn;
		int bit;
		boolean foundEntry = false;
		StringBuffer sb;
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile));
		zIn = new ZipInputStream(in);
		
		// Get zip entry
		while((zipEntry = zIn.getNextEntry()) != null) {
			String zipEntryName = zipEntry.getName();
			if(zipEntryName.equalsIgnoreCase(entryName)) {
				foundEntry = true;
				break;
			}
			zIn.closeEntry();
		}
		
		// If we didn't get it return
		if(foundEntry == false) {
			try{
				zIn.close();
			}
			catch(IOException ex) {}
			return null;
		}
		
		sb = new StringBuffer();
		
		// Extract it
		while((bit = zIn.read()) != -1) {
			sb.append((char)bit);
		}
		
		zIn.close();
		
		return sb.toString();
	}
	
	
	/**
	 * Extracts a named entry out of the zip file to the specified file
	 * Returns the File ref if OK or null if weirdness happens
	 * @param zipFile
	 * @param entryName
	 * @param outFile
	 * @return
	 * @throws IOException
	 */
	public static File extractZipEntry(File zipFile, String entryName, File outFile) throws IOException {
		ZipInputStream zIn;
		ZipEntry zipEntry;
		int bytesRead;
		final int bufSize = 1024;
		byte buf[] = new byte[bufSize];
		boolean foundEntry = false;
		
		// Ensure that the parent Folder exists
		if(!outFile.getParentFile().exists()) {
			outFile.getParentFile().mkdirs();
		}
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile), bufSize);
		zIn = new ZipInputStream(in);
		
		// Find the entry
		while((zipEntry = zIn.getNextEntry()) != null) {
			String zipEntryName = zipEntry.getName();
			if(zipEntryName.equalsIgnoreCase(entryName)) {
				foundEntry = true;
				break;
			}
			zIn.closeEntry();
		}
		
		// If we didn't get it return
		if(foundEntry == false) {
			return null;
		} 
		
		// Extract it and save to outFile
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile), bufSize);
		
		while((bytesRead = zIn.read(buf)) != -1) {
			out.write(buf, 0, bytesRead);
			try {
				// Allow other things to happen
				Thread.sleep(2);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		
		out.flush();
		out.close();
		zIn.close();
		
		// Restore time stamp
		outFile.setLastModified(zipEntry.getTime());
		
		return outFile;
	}
	
	/**
	 * Gets all file names out of a zip file in sorted name order
	 * Returns an array of String names or null if none
	 * @param zipFile
	 * @return
	 * @throws IOException
	 */
	public static String[] getZipFileEntryNames(File zipFile) throws IOException {
		ZipInputStream zIn = null;
		ZipEntry zipEntry;
		final int bufSize = 1024;
		Vector fileList = new Vector();
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile), bufSize);
		
		zIn = new ZipInputStream(in);
		
		try {
			while((zipEntry = zIn.getNextEntry()) != null) {
				// Don't add directories
				if(!zipEntry.isDirectory()) {
					String zipEntryName = zipEntry.getName();
					fileList.addElement(zipEntryName);
				}
				zIn.closeEntry();
			}
		}
		// We'll catch this exception to close the file otherwise it will remain locked
		catch(IOException ex) {
			zIn.close();
			throw ex;
		}
		
		String[] names = new String[fileList.size()];
		fileList.copyInto(names);
		Arrays.sort(names);
		return names;
	}
	
	/**
	 * Extracts all entries out of the zip file to the specified folder
	 * Target folder is created if it doesn't exist
	 * @param zipFile
	 * @param targetFolder
	 * @throws IOException
	 */
	public static void unpackZip(File zipFile, File targetFolder) throws IOException {
		unpackZip(zipFile, targetFolder, null);
	}
	
	/**
	 * Extracts all entries out of the zip file to the specified folder
	 * Target folder is created if it doesn't exist
	 * Returns true or false to indicate if the progress cancel was pressed
	 * @param zipFile
	 * @param targetFolder
	 * @param progressMonitor an optional ProgressMonitor.  This can be null.
	 * @return false if the progressMonitor is cancelled
	 * @throws IOException
	 */
	public static boolean unpackZip(File zipFile, File targetFolder, IProgressMonitor progressMonitor) throws IOException {
	    targetFolder.mkdirs();
		
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		ZipInputStream zIn = null;
		ZipEntry zipEntry;
		int bytesRead;
		final int bufSize = 512;
		byte buf[] = new byte[bufSize];
		
		in = new BufferedInputStream(new FileInputStream(zipFile), bufSize);
		zIn = new ZipInputStream(in);
		
		try {
			while((zipEntry = zIn.getNextEntry()) != null) {
				// Don't add directories
				if(!zipEntry.isDirectory()) {
					File outFile = new File(targetFolder, zipEntry.getName());
					
					// Ensure that the parent Folder exists
					if(!outFile.getParentFile().exists()) {
					    outFile.getParentFile().mkdirs();
					}
					
					out = new BufferedOutputStream(new FileOutputStream(outFile), bufSize);
					
					// If we have a Progress Monitor, display name
					if(progressMonitor != null) {
					    progressMonitor.setNote(zipEntry.getName());
					}
					
					int sleep_count = 0;
					
					while((bytesRead = zIn.read(buf)) != -1) {
						out.write(buf, 0, bytesRead);
						
						// Allow other things to happen every 40 chunks
						if(sleep_count >= 40) {
							try {
								Thread.sleep(2);
							} catch(InterruptedException ex) {
								ex.printStackTrace();
							}
							sleep_count = 0;
						}
						
						sleep_count++;
						
						// If we have a Progress Monitor
						if(progressMonitor != null && progressMonitor.isCanceled()) {
							out.flush();
							out.close();
							zIn.close();
							return false;
						}
					}
					
					// Restore time stamp
					outFile.setLastModified(zipEntry.getTime());
					
					// Close File
					out.flush();
					out.close();
				}
				
				zIn.closeEntry();
			}
			
			zIn.close();
			
		}
		// We'll catch this exception to close the file otherwise it remains locked
		catch(IOException ex) {
			zIn.close();
			if(out != null) {
				out.flush();
				out.close();
			}
			// And throw it again
			throw ex;
		}
		
		return true;
	}
}
