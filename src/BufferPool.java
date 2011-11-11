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
 * This class manages the buffers
 * Creates new buffers if data needed is not currently in pool
 * Deletes the least recently used buffer
 * Deletes all buffers at the end of the program
 * 
 * @author Evan Dent, James Latane
 * @version Oct 21, 2011
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BufferPool {
	
	//Global:
	public static final int BUFF_SIZE = 4096;	//Number of bytes in buffer
	
	//Fields
	private MyBuffer[] pool;
	private SafeFile file;
	private BufferedWriter statFile;
	private int cacheHits, cacheMisses, diskReads, diskWrites;
	
	/**
	 * Constructor
	 * @param n = number of buffers
	 */
	public BufferPool(int n, String fileName, String sFileName)
	{
		pool = new MyBuffer[n];
		file = new SafeFile(fileName, "rw");
		try{
			statFile = new BufferedWriter(new FileWriter(sFileName, true));
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Returns a byte array from the file at the given index
	 * @param i = index in the file of the first byte in the array to return
	 * @param size = number of bytes to return
	 * @return the byte array at the index, throws exception if index is out of range
	 */
	public byte[] getBytes(int i, int size)
	{
		if(i < 0 || i > file.length())
		{
			throw new IndexOutOfBoundsException("File index is out of range when trying to get bytes");
		}
		int buffIndex = (i/BUFF_SIZE)*BUFF_SIZE;	//Index of first byte in the buffer we want
		for(int c = 0; c < pool.length; c++)	//Return the value from the buffer pool if it is there
		{
			if(pool[c] != null && buffIndex == pool[c].getFilePos())
			{
				cacheHits++;
				return pool[c].getBytes(i, size);
			}		
		}
		//Otherwise, make a new buffer at correct index
		cacheMisses++;
		makeBuffer(buffIndex);	//Buffer we want is now pool[0]
		return pool[0].getBytes(i, size);
	}
	
	/**
	 * Sets a byte array in the file at the given index
	 * @param i = index in the file of the first byte in the array to set
	 * @param bytes = the bytes to set
	 * Throws exception if file index is out of bounds.
	 */
	public void setBytes(int i, byte[] bytes)
	{
		if(i < 0 || i > file.length())
		{
			throw new IndexOutOfBoundsException("File index is out of range when trying to set bytes");
		}
		int buffIndex = (i/BUFF_SIZE)*BUFF_SIZE;	//Index of first byte in the buffer we want
		for(int c = 0; c < pool.length; c++)	//Return the value from the buffer pool if it is there
		{
			if(pool[c] != null && buffIndex == pool[c].getFilePos())
			{
				cacheHits++;
				pool[c].setBytes(i, bytes);
				return;
			}		
		}
		//Otherwise, make a new buffer at correct index
		cacheMisses++;
		makeBuffer(buffIndex);	//Buffer we want is now pool[0]
		pool[0].setBytes(i, bytes);
	}
	
	/**
	 * Gets the length of the file in number of bytes
	 * @return long = number of bytes
	 */
	public long length()
	{
		return file.length();
	}
	
	/**
	 * Flushes the buffer pool and writes runtime statistics to the stat file.
	 */
	public void close()
	{
		for(MyBuffer buff: pool)
		{
			if(buff != null)
			{
				buff.release();
				diskWrites++;
			}
		}
		file.close();
		try
		{
			statFile.write("\n\tCache hits: "+cacheHits+
					"\n\tCache misses: "+cacheMisses+
					"\n\tDisk reads: "+diskReads+
					"\n\tDisk writes: "+diskWrites);
			statFile.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Makes a buffer filled with data from the file starting at the given index
	 * @param i = the file index of the first byte contained within this buffer
	 * @return the MyBuffer filled with the correct data
	 */
	private void makeBuffer(int i)
	{
		int lru = -1;	//lru = least recently used buffer
		for(int c = 0; c < pool.length; c++)
		{
			if(pool[c] != null)
				lru = c;
		}
		if(lru < pool.length-1) //If empty space in the pool, fill it with a buffer
		{
			pool[++lru] = new MyBuffer(BUFF_SIZE, i*BUFF_SIZE, file);
		}
		pool[lru].setFilePos(i);
		pool[lru].readBlock();
		moveToFront(lru);
		diskReads++;
	}
	
	/**
	 * Moves a buffer in the buffer pool to the front to signify that it is most recently used
	 * @param index - the index in the pool of the buffer to move
	 */
	private void moveToFront(int index)
	{
		MyBuffer first = pool[index];
		for(int i = index; i > 0; i--)
		{
			pool[i] = pool[i - 1];
		}
		pool[0] = first;
	}

	/**
	 * @return the statFile
	 */
	public BufferedWriter getStatFile() {
		return statFile;
	}
	
	/**
	 * Returns the name of the file that this buffer pool refers to
	 * @return file name
	 */
	public String getFileName()
	{
		return file.name();
	}
}