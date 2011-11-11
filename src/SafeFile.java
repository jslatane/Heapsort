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
 * Wrapper class for RandomAccessFile
 * Catches most exceptions thrown by the RandomAccessFile methods.
 * 
 * @author Evan Dent, James Latane
 * @version Oct 21, 2011
 */


import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SafeFile{
	
	//Fields
	RandomAccessFile file;
	String name;
	
	/**
	 * Constructor
	 */
	public SafeFile(String fN, String mode)
	{
		try{
			file = new RandomAccessFile(fN, mode);
			name = fN;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File "+fN+" not found when trying to construct buffer pool.");
		}
	}
	
	/**
	 * Returns number of bytes in the file. Catches exceptions.
	 * @return number of bytes, -1 if there is an IOException.
	 */
	public long length()
	{
		try{
			return file.length();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
			return -1;
		}
	}
	
	/**
	 * Returns the bytes starting at a given offset, always reads 
	 * @param off = offset 
	 * @return
	 */
	public byte[] read(int start, int numBytes)
	{
		byte[] b = new byte[numBytes];
		seek(start);
		
		if(numBytes == 2)
		{
			try{
				short s = file.readShort();
				b[0] = (byte)(s >> 8);
				b[1] = (byte)(s);
				return b;
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				return null;
			}
		}
		try{
			file.readFully(b);
			return b;
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Writes given bytes to this file at the given byte index
	 * @param start - place in file to write to
	 * @param b - bytes to write
	 */
	public void write(int start, byte[] b)
	{
		seek(start);
		
		try{
			int len = b.length;
			int offset = 0;
			file.write(b, offset, len);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Closes this random access file.
	 */
	public void close()
	{
		try{
			file.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Helper method for setting the file pointer to the given index
	 * @param index = byte index
	 */
	private void seek(int index)
	{
		try{
			file.seek(index);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Returns the name of the file
	 * @return name
	 */
	public String name()
	{
		return name;
	}
}
