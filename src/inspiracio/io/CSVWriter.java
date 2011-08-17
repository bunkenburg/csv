/*  Copyright 2011 Alexander Bunkenburg alex@inspiracio.com

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
package inspiracio.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/** Writes data in CSV format to somewhere.
 * Spec http://tools.ietf.org/html/rfc4180.
 * This class does not control that each record has the same number of fields.
 * */
public class CSVWriter{

	// Constants ----------------------------------------------

	/** Goes at the end of a CSV record. Maybe could be configurable. */
	private static String NL=System.getProperty("line.separator");

	/** Encloses a field. Maybe could be configurable. */
	protected static char QUOTE='"';

	// State ----------------------------------------------

	/** Field separator. Configurable. */
	private char separator=',';

	/** The underlying writer where the CSV is written. */
	private Writer writer;

	/** Is the current line fresh?
	 * true: current line is fresh, there are no fields on it yet.
	 * false: current line already has some fields on it. */
	private boolean fresh=true;

	// Constructors ----------------------------------------------

	/** Makes a new CSVWriter that writes to the given writer.
	 * @param writer Where the data will go. */
	public CSVWriter(Writer writer){
		this.writer=writer;
	}

	// Configuration methods ------------------------------------------

	/** Sets the field separator. Normally it's ',' or ';'. */
	public void setSeparator(char separator){
		this.separator=separator;
	}

	// Business methods ------------------------------------------

	/** Writes some objects to CSV, each object as one more field in the current record.
	 * The fields are always enclosed in the delimiters double quotes.
	 * The fields are escaped properly according to RFC4180.
	 * At the end, flushes the underlying writer.
	 * @param fields
	 * 	Meant for primitives and String.
	 * 	Other objects are converted to String by toString(),
	 * 	and null is represented "null".
	 * @exception IOException Writing has failed. */
	public void write(Object... fields) throws IOException {
		for(Object field : fields){
			if(!fresh)this.writer.write(separator);//the line already has fields on it
			String s = field==null ? "null" : field.toString();//null->"null", and toString() for others. Here you could put a configurable substitution for null.
			this.writer.write(QUOTE);//opening quote always
			int N=s.length();
			for(int i=0; i<N; i++){
				char c=s.charAt(i);
				if(c==QUOTE)this.writer.write(QUOTE);//escape
				this.writer.write(c);
			}
			this.writer.write(QUOTE);//closing quote always
			this.fresh=false;//Now the line definitely is not fresh anymore.
		}
		//this.writer.flush();
	}

	/** Writes some objects to CSV, each object as one more field in the
	 * current record, and then writes a line ending to terminate the record,
	 * and flushes the underlying writer.
	 * The fields are escaped properly according to RFC4180.
	 * @param fields Meant for primitives and String. Other objects are converted to
	 * 	String by toString(), and null is represented by "null".
	 * @exception IOException Writing has failed. */
	public void writeln(Object... fields) throws IOException {
		this.write(fields);
		this.endRecord();
	}

	protected void endRecord()throws IOException{
		this.writer.write(NL);
		this.writer.flush();
		this.fresh=true;//fresh line
	}

	/** Flushes and closes underlying writer. */
	public void close()throws IOException{writer.close();}

	// Helpers ---------------------------------------------------------------

	/** only testing */
	public static void main(String[] args) throws Exception {
		say("hi");
		Writer writer=new FileWriter("out.csv");
		CSVWriter cw=new CSVWriter(writer);
		cw.write();
		cw.write(true, 12, -3.19, "Hola guapo", "She said: \"Hola guapo\".");
		cw.writeln();
		cw.writeln(false, 13, -3.21, "Hola guapa", "He said: \"Hola guapa\".");
		writer.close();
		say("bye");
	}

	/** only testing */
	private static void say(Object o){System.out.println(o);}

}