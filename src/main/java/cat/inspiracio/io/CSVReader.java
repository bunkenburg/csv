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
import java.io.PushbackReader;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Locale;

import cat.inspiracio.text.NumberFormatWrapper;

/** Reads CSV records from a java.io.Reader, one at a time.
 * Can be used to read the records and process them without having to read
 * all of them into memory (streaming).
 * Example:
 * <pre>
 * CSVReader csv=new CSVReader(reader)
 * Object[] line=csv.readln()
 * while(line!=null){
 *  for(Object field : line)
 *    process(field)
 *  processEndOfLine()
 *  line=csv.readln()
 * }
 * </pre>
 * When interpreting numbers, Locale.US is assumed.
 * */
public class CSVReader{

	// Constants ----------------------------------------------

	/** Delimits a field. Can be " or '. 
	 * Some fields have no delimiters. */
	private char delimiter='"';

	/** Separates fields. Can be ',' or ';' or ':' or TAB or SPACE. */
	private char separator=',';

	/** Detects integers.
	 * 
	 * Internationalised: numbers according to US locale.
	 * Prefers Integer over Long. 
	 * NumberFormat is not threadsafe. */
	private NumberFormat integers=new NumberFormatWrapper(NumberFormat.getIntegerInstance(Locale.US)){

		/** Return Integer if possible rather than Long. */
		@Override public Number parse(String source, ParsePosition position) {
			Number number=super.parse(source, position);
			if(number==null)
				return null;
			long n=number.longValue();
			if(Integer.MIN_VALUE <= n && n <= Integer.MAX_VALUE)
				return Integer.valueOf(number.intValue());
			return number;
		}
	};

	/** Detects floating point numbers.
	 * 
	 * Internationalised: numbers according to US locale.
	 * Prefers Integer over Long.
	 * NumberFormat is not threadsafe. */
	private NumberFormat numbers=new NumberFormatWrapper(NumberFormat.getNumberInstance(Locale.US)){

		/** Return Integer if possible rather than Long. */
		@Override public Number parse(String source, ParsePosition position) {
			Number number=super.parse(source, position);
			if(number==null)
				return null;
			long n=number.longValue();
			if(number instanceof Long && Integer.MIN_VALUE <= n && n <= Integer.MAX_VALUE)
				return Integer.valueOf(number.intValue());
			return number;
		}
	};


	//State ------------------------------------------------

	/** number of records already parsed and delivered in readln() */
	private int count=0;

	/** Parse the records from this reader. */
	private PushbackReader reader;

	//Constructor -----------------------------------------

	/** @param reader An open reader of a CSV file */
	public CSVReader(Reader reader){
		this.reader=new PushbackReader(reader, 2);//two character pushback buffer for CRLF
	}

	//Accessors -------------------------------------------

	/** Sets the field delimiter.
	 * Accepts '"' and '\''. */
	public void setDelimiter(char delimiter){
		if(delimiter!='"' && delimiter!='\'')
			throw new IllegalArgumentException();
		this.delimiter=delimiter;
	}

	/** Sets the field separator.
	 * Accepts ',' and ';' and ':' and TAB and SPACE. */
	public void setSeparator(char separator){
		if(separator!=',' && separator!=';' && separator!=':' && separator!='\t' && separator!=' ')
			throw new IllegalArgumentException();
		this.separator=separator;
	}

	/** How many records have been read? */
	public int getCount(){return count;}

	//Methods ---------------------------------------------

	/** Reads (parses) one more record from the CSV file.
	 * 
	 * If there is nothing more, returns null to signal end of input.
	 * The object returned are Strings, Numbers, or Booleans.
	 * 
	 * Should be called when the position is just before a record or
	 * at the end of input. Leaves the position just before the next record
	 * or at the end of input.
	 * 
	 * @return array of the fields, or null if there are no more records
	 *  The array contains: String, Number, Boolean, null.
	 * 
	 * @throws IOException
	 * */
	public Object[] readln() throws IOException{
		
		if(eof())return null;

		//Read all the fields of one record
		ArrayList<Object>record=new ArrayList<Object>();
		try{
			Object field=readField();
			while(true){  //An EOR exception gets us out of the loop.
				record.add(field);
				String c=read(separator);//at the end of the record, returns null
				if(c==null)
					throw new EOR();
				field=readField();
			}
		}
		catch(EOR e){}//Have reached the end of record.

		readRecordSeparator();//Maybe read trailing line terminator

		count++;
		return record.toArray();
	}

	//Helpers ----------------------------------------------

	/** Are we at the end of input? */
	private boolean eof()throws IOException{
		int i=reader.read();
		if(i<0)
			return true;//end of input
		reader.unread(i);
		return false;
	}
	
	/** Reads one field.
	 * <p>
	 * The position should be just before a field and is left
	 * just after the field. The field returned my be just "".
	 * <p>
	 * If the method could not read a field, it sets the read position
	 * to where it was and returns null.
	 * @return the field, represented in Java
	 * @exception EOR
	 * */
	private Object readField()throws IOException, EOR{
		
		//End of input? Return "" at least".
		if(eof())
			return "";

		//Is the field delimited?
		if(delimiter())
			return readDelimitedField();
		
		//The field is not delimited. Field may be 0 chars.
		int i=reader.read();
		StringBuilder builder=new StringBuilder();
		while(terminatesField(i)){  
			char c=(char)i;
			builder.append(c);
			i=reader.read();
		}
		if(0<=i)reader.unread(i);
		String field=builder.toString();
		return parseField(field);
	}

	private boolean terminatesField(int i){
		return 0<=i && i!=separator && i!='\r' && i!='\n';
	}
	
	/** Does input start with delimiter?
	 * In either case, leaves position unchanged. */
	private boolean delimiter()throws IOException{
		int i=reader.read();
		if(0<=i)
			reader.unread(i);
		return i==delimiter;
	}
	
	/** Reads the record separator.
	 * Accepts CR, LF, and CRLF.
	 * <p>
	 * The position should be just before the CRLF
	 * and is left just after it.
	 * <p>
	 * If the method can not read a line terminator, returns null
	 * and leaves the position where it was. */
	private String readRecordSeparator()throws IOException{
		
		//try LF
		if(read('\n')!=null)
			return "\n";		

		//try CRLF
		if(read("\r\n")!=null)
			return "\r\n";		

		//try CR
		if(read('\r')!=null)
			return "\r";		
		
		return null;//found no record separator
	}

	/** Reads a certain character and returns it.
	 * If the reader does not start with that character, returns null and leaves position unchanged.
	 * @throws IOException 
	 * */
	private String read(char c) throws IOException{
		int i=reader.read();
		if(i==c)
			return c+"";//found it!
		if(0<=i)
			reader.unread(i);//something else, unread. Don't unread EOF.
		return null;
	}

	/** Reads some certain characters and returns them.
	 * If the reader does not start with those characters, returns null and leaves position unchanged.
	 * @throws IOException 
	 * */
	private String read(String s)throws IOException{
		//Implemented only for two characters.
		if(s.length()!=2)
			throw new IllegalArgumentException(s);
		char c=s.charAt(0);
		char d=s.charAt(1);
		int i=reader.read();
		if(i==c){
			int j=reader.read();
			if(j==d)
				return s;
			if(0<=j)
				reader.unread(j);
		}
		if(0<=i)
			reader.unread(i);
		return null;
	}
	
	/** Reads a delimited field from the CSV and returns it as
	 * Boolean, Number, or String.
	 * <p>
	 * The position should be just before the first field delimiter
	 * and is left just after the second field delimiter.
	 * @exception EOR If the method cannot read a field, it throws EOR and leaves the
	 * position where it was.
	 * */
	private Object readDelimitedField()throws IOException,EOR{

		//read starting delimiter
		int i=reader.read();
		if(i!=delimiter){//fail
			reader.unread(i);
			throw new EOR();
		}

		//Inside the field ...
		boolean inside=true;
		StringBuilder builder=new StringBuilder();
		while(inside){
			i=reader.read();
			if(i<0){
				//In fact, the CSV file is bad. Fail.
				throw new EOR();
			}
			char c=(char)i;
			if(c==delimiter){
				i=reader.read();
				if(i==delimiter){
					//"" -> one escaped "
					builder.append(delimiter);
				}else{
					if(0<=i)reader.unread(i);
					//else System.out.println("here");
					reader.unread(delimiter);
					inside=false;
				}
			}else{
				builder.append(c);
			}
		}

		//read final delimiter
		i=reader.read();
		if(i!=delimiter){//fail
			reader.unread(i);
			throw new EOR();
		}

		String field=builder.toString();
		return parseField(field);
	}

	/** Parses a field:
	 * <table border="1">
	 *  <tr><th>CSV field:<th>parsed</tr>
	 *  <tr><td>""<td>String ""</tr>
	 *  <tr><td>integers and floating point numbers, according to default locale<td>Number</tr>
	 *  <tr><td>"true", "TRUE", "false", "FALSE"<td>Boolean</tr>
	 *  <tr><td>"null"<td>null</tr>
	 *  <tr><td>... otherwise<td>String</tr>
	 * </table>
	 * The string must be not-null, and the characters in it already unescaped.
	 *  */
	private Object parseField(String field){
		//try ""
		if(field.length()==0)return field;

		//try boolean
		if("true".equals(field.toLowerCase()))return true;
		if("false".equals(field.toLowerCase()))return false;

		//try integers
		ParsePosition position=new ParsePosition(0);
		Number n=integers.parse(field, position);
		if(position.getIndex()==field.length())return n;

		//try floating point numbers
		position.setIndex(0);
		position.setErrorIndex(-1);
		n=numbers.parse(field, position);
		if(position.getIndex()==field.length())return n;

		//try null
		if("null".equals(field))return null;

		//Otherwise, just the string
		return field;
	}

	/** Signals End-of-record. */
	private static class EOR extends Exception{}
}