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
 * This program takes in a file whose size is some multiple of 4096 bytes.
 * The file is sorted using a heapsort algorithm with a buffer pool to access the data in the file.
 * This program treats every two bytes as a short, and every two shorts as a key-value pair.
 * The file is sorted according to key values.
 * After running, this program writes some statistics about itself to another file.
 * 
 * 
 * @author Evan Dent, James Latane
 * @version Oct 11, 2011
 * 
 * OS - Windows 7 32 bit
 * Compiler - java 1.6.0_16
 * Completion date - Nov 3, 2011
 */

public class heapsort {

	/**
	 * @param args[0] - the name of the unsorted file
	 * 			args[1] - the number of buffers to give to the buffer pool
	 * 			args[2] - the statistic file to write to
	 */
	public static void main(String[] args) {
		String fileName = args[0];
		int numBuffs = Integer.parseInt(args[1]);
		String statFile = args[2];
		BufferPool buffPool = new BufferPool(numBuffs, fileName, statFile);
		Heap heap = new Heap(buffPool);
		buffPool.close();
	}
}