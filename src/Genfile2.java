// WARNING: This program uses the Assertion class. When it is run,
// assertions must be turned on. For example, under Linux, use:
// java -ea Genfile

/** Generate a data file. The size is a multiple of 4096 bytes.
    The records are short ints, with each record having a value
    less than 30,000.
*/

import java.io.*;
import java.util.*;
import java.math.*;

public class Genfile2 {

static final int BlockSize = 4096;
static final int NumRecs = 2048; // Because they are short ints

/** Initialize the random variable */
static private Random value = new Random(); // Hold the Random class object

static int random(int n) {
    return Math.abs(value.nextInt()) % n;
}

public static void main(String args[]) throws IOException {

    assert (args.length == 3) && (args[0].charAt(0) == '-') :
         "\nUsage: Genfile <option> <filename> <size>" +
	 "\nOptions ust be '-a' for ASCII, or '-b' for binary." +
	 "\nSize is measured in blocks of 4096 bytes";

  int filesize = Integer.parseInt(args[2]); // Size of file in blocks
  DataOutputStream file = new DataOutputStream(
      new BufferedOutputStream(new FileOutputStream(args[1])));

  int recs = NumRecs * filesize;

  if (args[0].charAt(1) == 'b') // Write out random numbers
      for (int i=0; i<filesize; i++)
	  for (int j=0; j<NumRecs; j++) {
	      short val = (short)(random(29999) + 1);
	      file.writeShort(val);
	  }
  else if (args[0].charAt(1) == 'a') // Write out ASCII-readable values
      for (int i=0; i<filesize; i++)
	  for (int j=0; j<NumRecs; j++) {
	      short val = (short)((8224 << 16) + random(26) + 0x2041);
	      file.writeShort(val);
          }
  else assert false : "Bad parameters";

  file.flush();
  file.close();
}

}