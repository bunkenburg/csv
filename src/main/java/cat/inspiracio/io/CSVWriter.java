/*  Copyright 2011 Alexander Bunkenburg alex@inspiracio.cat

    This file is part of csv.

    csv is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    csv is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with csv.  If not, see <http://www.gnu.org/licenses/>.
 */
package cat.inspiracio.io;

import java.io.IOException;
import java.io.Writer;

/** Writes data in CSV format to somewhere.
 * Spec http://tools.ietf.org/html/rfc4180. 
 * This class does not control that each record has the same number of fields. */
public class CSVWriter {

	// State ----------------------------------------------

	/** Encloses a field. */
	private char delimiter='"';

	/** Field separator. */
	private char separator=',';

	/** Goes at the end of a CSV record. */
	private String terminator=System.getProperty("line.separator");

	/** The underlying writer where the CSV is written. */
	private Writer writer;

	/** Is the current line fresh? true: current line is fresh, there are no
	 * fields on it yet. false: current line already has some fields on it. */
	private boolean fresh=true;

	// Constructors ----------------------------------------------

	/** Makes a new CSVWriter that writes to the given writer.
	 * @param w Where the data will go. */
	public CSVWriter(Writer w){writer=w;}

	// Configuration methods ------------------------------------------

	/** Sets the field separator. Normally it's ',' or ';'. */
	public void setSeparator(char s){separator=s;}

	/** Sets the delimiter of a field. Accepts single or double quote. */
	public void setDelimiter(char c){
		if(c!='\'' && c!='"')
			throw new IllegalArgumentException(c+"");
		delimiter=c;
	}
	
	// Business methods ------------------------------------------

	/** Writes some objects to CSV, each object as one more field in the current
	 * record. The fields are always enclosed in the delimiters double quotes.
	 * The fields are escaped properly according to RFC4180. At the end, flushes
	 * the underlying writer.
	 * 
	 * @param fields
	 *            Meant for primitives and String. Other objects are converted
	 *            to String by toString(), and null is represented "null".
	 * @exception IOException Writing has failed.
	 */
	public void write(Object... fields) throws IOException {
		for (Object field : fields) {
			if (!fresh)
				separator();// the line already has fields on it
			
			String s = toString(field);
			
			delimiter();// opening quote always
			
			int N = s.length();
			for (int i = 0; i < N; i++) {
				char c = s.charAt(i);
				if (c == delimiter)
					delimiter();// escape
				writer.write(c);
			}
			
			delimiter();// closing quote always
			fresh = false;// Now the line definitely is not fresh anymore.
		}
	}

	/** Writes some objects to CSV, each object as one more field in the current
	 * record, and then writes a line ending to terminate the record, and
	 * flushes the underlying writer. The fields are escaped properly according
	 * to RFC4180.
	 * 
	 * @param fields
	 *            Meant for primitives and String. Other objects are converted
	 *            to String by toString(), and null is represented by "null".
	 * @exception IOException
	 *                Writing has failed.
	 */
	public void writeln(Object... fields) throws IOException {
		write(fields);
		endRecord();
	}

	protected void endRecord() throws IOException {
		writer.write(terminator);
		writer.flush();
		fresh = true;// fresh line
	}

	public void flush()throws IOException{writer.flush();}
	
	/** Flushes and closes underlying writer. */
	public void close() throws IOException{writer.close();}

	// Helpers ---------------------------------------------------------------

	private String toString(Object field){
		if(field==null)
			return "null";
		return field.toString();
	}
	
	private void separator() throws IOException{
		writer.write(separator);
	}
	
	private void delimiter()throws IOException{
		writer.write(delimiter);
	}
}