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
 * This class sorts the data file using the heapsort algorithm
 * Makes calls to the BufferPool class, treating it as a large array of shorts.
 * The heapsort algorithm references only keys in the file, which are the shorts at even numbered indexes.
 * 
 * @author Evan Dent, James Latane
 * @version Oct 11, 2011
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Heap {

	//Fields
	private BufferPool pool;
	private int len;
	private BufferedWriter statFile;
	private long time;
	private static final int REC_LEN = 4;
	
	//Constructor
	public Heap(BufferPool pool){
		this.pool = pool;
		len = (int)(2*pool.length())/REC_LEN;	//pool.length gives number of byte, len is number of key-value pairs
		statFile = pool.getStatFile();
		time = System.currentTimeMillis();
		buildHeap(len);
		sort();
		time = System.currentTimeMillis() - time;
		try
		{
			statFile.write("\nStats for file " +
					pool.getFileName() + ":" +
					"\n\tSort time: "+time);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		this.print();
	}
	
	/**
	 * The method to construct the array as a max heap.
	 * Calls siftDown on every parent element
	 * 	starting at parent with largest index working down to the first element in the array
	 * @param len = number of elements
	 */
	private void buildHeap(int len){
		int lastParentIndex = ((((len-2)/2)/2)*2);
		for(int i = lastParentIndex; i >= 0; i-=2)
		{
			siftDown(i, len - 2);
		}
	}
	
	/**
	 * The helper method for building a heap.
	 * Takes in an index of an element of the array and sifts the element down
	 * 	if one of its children larger than itself.
	 * @param index = the index of the root/parent to sift down
	 * @param endIndex = the last index of the array to sift down to.
	 */
	private void siftDown(int startIndex, int endIndex){
		int rootIndex = startIndex;
		int leftIndex, rightIndex;
		int swap;	//Index that the parent should swap with
		
		while (rootIndex*2 + 2 <= endIndex){	//Keep looping as long as root is a parent
			leftIndex = leftChild(rootIndex);
			rightIndex = rightChild(rootIndex);
			swap = rootIndex;	//By default, swap with itself so array does not change
			//if(leftIndex > -1 && arr[swap] < arr[leftIndex]){	//Left child larger than parent
			short swapShort = getShort(swap);
			short leftShort = getShort(leftIndex);
			if(leftIndex > -1 && swapShort < leftShort)
			{
				swap = leftIndex;
				swapShort = getShort(swap);
			}
				
			//Right child larger than parent and left child and in range
			//Original: if(rightIndex > -1 && rightIndex <= endIndex && arr[swap] < arr[rightIndex])
			
			short rightShort = getShort(rightIndex);
			if(rightIndex > -1 && rightIndex <= endIndex && swapShort < (rightShort)){
				swap = rightIndex;
			}
			
			if (swap != rootIndex){	//Either child is larger than parent
				swap(swap, rootIndex);	//Swap both key and value in the file.
				rootIndex = swap;
			}
			else return;
		}
	}
	
	/**
	 * Assumes that the array is in max heap form.
	 * Swaps largest heap element with last heap element.
	 * Rebuilds heap on section of the array disregarding largest element just swapped.
	 * At the end of this method array is sorted in ascending order.
	 */
	private void sort(){
		for(int i = len-2; i > 0; i-=2)	//Loop through every element in the array
		{
	
			swap(i, 0);		//First value of max heap = last value of heap
			siftDown(0, i-2);
		}
	}
	
	
	/**
	 * Get the left child's index.
	 * @param index = index of parent with left child
	 * @return index of left child
	 */
	private int leftChild(int index){
		if (index < len/2)
			return 2*index + 2;
		return -1;
	}
	
	/**
	 * Get the right child index.
	 * @param index = index of parent with right child
	 * @return index of right child
	 */
	private int rightChild(int index){
		if (index < (len-3)/2)
			return 2*index + 4;
		return -1;
	}
	
	/**
	 * Returns the ith short from the file
	 * @param i - the short index of the key
	 * @return the ith short
	 */
	private short getShort(int i)
	{
		if(i < 0){return -1;}
		ByteBuffer bytes = ByteBuffer.wrap(pool.getBytes(i*(REC_LEN/2), REC_LEN/2));
		return bytes.getShort(0);
	}
	
	/**
	 * Swaps two four-byte key-record pairs at the ath and bth positions in the file
	 * @param a - the first pair
	 * @param b - the second pair
	 */
	private void swap(int a, int b)
	{
		byte[] temp = new byte[REC_LEN];
		temp = pool.getBytes(a*(REC_LEN/2), REC_LEN);
		pool.setBytes(a*(REC_LEN/2), pool.getBytes(b*(REC_LEN/2), REC_LEN));
		pool.setBytes(b*(REC_LEN/2), temp);
	}

	/**
	 * Prints out the first record from each block of the file in order from the sorted data file.
	 */
	public void print()
	{
		int blocks = (int)pool.length()/BufferPool.BUFF_SIZE;
		for(int i = 0; i < blocks; i++)
		{
			if(i%8 == 0 && i != 0)
				System.out.println();
			ByteBuffer bytes = ByteBuffer.wrap(pool.getBytes(i*BufferPool.BUFF_SIZE, REC_LEN));
			System.out.printf("%7s %5s", bytes.getShort(0), bytes.getShort(2));
		}
	}
}