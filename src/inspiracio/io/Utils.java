/*  Copyright 2012 David Aparicio 
 * http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
 * This is the fastest version I have found so far,
 * about 6 times faster than readLines. On a 150MB log file this takes 0.35 seconds,
 * versus 2.40 seconds when using readLines(). Just for fun,
 * linux' wc -l command takes 0.15 seconds.
 * */
package inspiracio.io;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class Utils{
	public static int count(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 1;
	        int readChars = 0;
	        while ((readChars = is.read(c)) != -1) {
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n')
	                    ++count;
	            }
	        }
	        return count;
	    } finally {
	        is.close();
	    }
	}
}