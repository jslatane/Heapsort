/**
On my honor:
- I have not used source code obtained from another student,
or any other unauthorized source, either modified or
unmodified.
- All source code and documentation used in my program is
either my original work, or was derived by me from the
source code published in the textbook for this course.
- I have not discussed coding details about this project with
anyone other than my partner (in the case of a joint
submission), instructor, ACM/UPE tutors or the TAs assigned
to this course. I understand that I may discuss the concepts
of this program with other students, and that another student
may help me debug my program so long as neither of us writes
anything during the discussion or modifies any computer file
during the discussion. I have violated neither the spirit nor
letter of this restriction.
 **/

/**
 * This class access the file data for reading and writing bytes.
 * Keeps track of whether this buffer's data has been changed from original disk data.
 * 
 * @author Evan Dent, James Latane
 * @version Oct 21, 2011
 */

public class MyBuffer
{
	//Fields
	private byte[] arr;
	private boolean dirty;	//Whether buffer has been modified since allocation.
	private int filePos;	//What sector of the file this buffer is
	private SafeFile file;
	private int size;
	
	/**
	 * Constructor
	 * @param n = number of bytes
	 * @param i = the byte index in the file of the start of the buffer
	 * @param f = the data file
	 */
	public MyBuffer(int n, int i, SafeFile f)
	{
		arr = new byte[n];
		size = n;
		dirty = false;
		filePos = i;
		file = f;
	}
	
	/**
	 * Sets internal byte array based on file contents
	 */
	public void readBlock()
	{
		arr = file.read(filePos, size);
	}
	
	/**
	 * Changes the bytes of the buffer to the given bytes starting at the given index 
	 * @param b = byte array to change
	 * @param start = the starting index of the file to write the bytes to
	 * @return true if value was put into buffer, false otherwise
	 */
	public boolean setBytes(int start, byte[] b)
	{
		if(start < 0 || start > file.length())
		{
			System.out.println("Index out of bounds");
			return false;
		}
		for(int i = 0; i < b.length; i++)
		{
			arr[start - filePos + i] = b[i];
		}
		setDirty(true);
		return true;
	}
	
	/**
	 * Gets a byte array of given size from buffer at given position
	 * @param size = how many bytes to get
	 * @param start = file index to get bytes from
	 * @return the byte array
	 */
	public byte[] getBytes(int start, int sz)
	{
		if(start < filePos || start > filePos + BufferPool.BUFF_SIZE)
		{
			throw new ArrayIndexOutOfBoundsException("File index is out of range when trying to get bytes from buffer");
		}
		byte[] theBytes = new byte[sz];
		for(int i = 0; i < sz; i++)
		{
			if((start - filePos + i) < filePos + BufferPool.BUFF_SIZE)
				theBytes[i] = arr[start - filePos + i];
		}
		return theBytes;
	}
	
	/**
	 * @return whether buffer has been modified since allocation
	 */
	public boolean isDirty()
	{
		return dirty;
	}
	
	/**
	 * Sets the dirty variable.
	 * @param b = the boolean to set dirty to
	 */
	public void setDirty(boolean b)
	{
		dirty = b;
	}
	
	/**
	 * @return a string representation of the buffer
	 */
	public String toString()
	{
		return "Current buffer starts at file position: "+filePos;
	}
	
	/**
	 * @return the position of the first short of this buffer in the file
	 */
	public int getFilePos()
	{
		return filePos;
	}
	
	/**
	 * Changes this buffer to refer to a new section of the file.
	 * @param fP = the new position in the file
	 */
	public void setFilePos(int fP)
	{
		release();
		filePos = fP;
	}
	
	/**
	 * Releases this buffer from use
	 * If the buffer has been changed from the original disk data, writes the changes to the file
	 * Sets it to a new clean buffer for reuse.
	 */
	public void release()
	{
		if(isDirty())
			file.write(filePos, arr);
		arr = null;
		setDirty(false);
	}
}