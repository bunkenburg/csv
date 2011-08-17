package inspiracio.io;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Locale;

/** Reads CSV records from a java.io.Reader, one at a time.
 * Can be used to read the records and process them without having to read
 * all of them into memory (streaming).
 * Example:
 * <pre>
 * CSVReader csv=new CSVReader(reader)
 * Object[] line=csv.readln()
 * while(line!=null){
 * 	for(Object field : line)
 * 		process(field)
 * 	processEndOfLine()
 * 	line=csv.readln()
 * }
 * </pre>
 * When interpreting numbers, Locale.US is assumed.
 * */
public class CSVReader {

	// Constants ----------------------------------------------

	/** Encloses a field. Can be " or '. */
	private char FIELD_DELIMITER='"';

	/** Field separator. Can be ',' or ';' or ':' or TAB or SPACE. */
	private char FIELD_SEPARATOR=',';

	/** Detects integers.
	 * Internationalised: numbers according to US locale.
	 * NumberFormat is not threadsafe. */
	private NumberFormat integerFormat=NumberFormat.getIntegerInstance(Locale.US);

	/** Detects floating point numbers.
	 * Internationalised: numbers according to US locale.
	 * NumberFormat is not threadsafe. */
	private NumberFormat numberFormat=NumberFormat.getNumberInstance(Locale.US);

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

	/** Accepts '"' and '\''. */
	public void setFieldDelimiter(char delimiter){
		if(delimiter!='"' && delimiter!='\'')throw new IllegalArgumentException();
		this.FIELD_DELIMITER=delimiter;
	}

	/** Accepts ',' and ';' and ':' and TAB and SPACE. */
	public void setFieldSeparator(char separator){
		if(separator!=',' && separator!=';' && separator!=':' && separator!='\t' && separator!=' ')throw new IllegalArgumentException();
		this.FIELD_SEPARATOR=separator;
	}

	/** How many records have been read? */
	public int getCount(){return count;}

	//Methods ---------------------------------------------

	/** Reads (parses) one more record from the CSV file.
	 * If there is nothing more, returns null to signal end of input.
	 * The object returned are Strings, Numbers, or Booleans.
	 * <p>
	 * Should be called when the position is just before a record or
	 * at the end of input. Leaves the position just before the next record
	 * or at the end of input.
	 * @return array of the fields, or null if there are no more records
	 * 	The array contains: String, Number, Boolean, null.
	 * @throws IOException
	 * */
	public Object[] readln() throws IOException{

		//Are we at the end of input?
		int i=this.reader.read();
		if(i<0)return null;
		this.reader.unread(i);

		//Read all the fields of one record
		ArrayList<Object>record=new ArrayList<Object>();
		try{
			Object field=this.readField();
			while(true){	//An EOR exception gets us out of the loop.
				record.add(field);
				Character c=this.readFieldSeparator();//at the end of the record, returns null
				if(c==null)
					throw new EOR();
				field=this.readField();
			}
		}catch(EOR e){
			//Have reached the end of record.
		}

		//Maybe read trailing line terminator
		this.readRecordSeparator();

		this.count++;
		return record.toArray();
	}

	//Helpers ----------------------------------------------

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
		int i=this.reader.read();

		//End of input? Return "" at least"!
		if(i<0)return "";

		//Is the field delimited?
		if(i==FIELD_DELIMITER){
			this.reader.unread(i);
			return this.readDelimitedField();
		}

		//The field is not delimited. Field may be 0 chars.
		StringBuilder builder=new StringBuilder();
		while(0<=i && i!=FIELD_SEPARATOR && i!='\r' && i!='\n'){	//character i does not terminate field
			char c=(char)i;
			builder.append(c);
			i=this.reader.read();
		}
		if(0<=i)this.reader.unread(i);
		Object field=this.parseField(builder.toString());
		return field;
	}

	/** Reads one field separator.
	 * The position should be just before the separator and is left
	 * just after it.
	 * <p>
	 * If the method cannot read a separator, it returns null and
	 * leaves the position where it was. */
	private Character readFieldSeparator()throws IOException{
		int i=this.reader.read();
		if(i==FIELD_SEPARATOR){
			return FIELD_SEPARATOR;
		}else{
			if(0<=i)this.reader.unread(i);
			//else System.out.println("here");
			return null;
		}
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
		int i=this.reader.read();
		StringBuilder builder=new StringBuilder();
		while(i=='\r' || i=='\n'){
			char c=(char)i;
			builder.append(c);
			i=this.reader.read();
		}
		if(0<=i)this.reader.unread(i);
		//else System.out.println("here");//After the last record, we get here
		if(0<builder.length())
			return builder.toString();
		else
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
		int i=this.reader.read();
		if(i!=FIELD_DELIMITER){//fail
			this.reader.unread(i);
			throw new EOR();
		}

		//Inside the field ...
		boolean inside=true;
		StringBuilder builder=new StringBuilder();
		while(inside){
			i=this.reader.read();
			if(i<0){
				//In fact, the CSV file is bad. Fail.
				throw new EOR();
			}
			char c=(char)i;
			if(c==FIELD_DELIMITER){
				i=this.reader.read();
				if(i==FIELD_DELIMITER){
					//"" -> one escaped "
					builder.append(FIELD_DELIMITER);
				}else{
					if(0<=i)this.reader.unread(i);
					//else System.out.println("here");
					this.reader.unread(FIELD_DELIMITER);
					inside=false;
				}
			}else{
				builder.append(c);
			}
		}

		//read final delimiter
		i=this.reader.read();
		if(i!=FIELD_DELIMITER){//fail
			this.reader.unread(i);
			throw new EOR();
		}

		Object field=this.parseField(builder.toString());
		return field;
	}

	/** Parses a field:
	 * <ul>
	 * 	<li>CSV field:															parsed</li>
	 * 	<li>"":																	String ""</li>
	 * 	<li>integers and floating point numbers, according to default locale:	Number</li>
	 * 	<li>"true", "TRUE", "false", "FALSE":									Boolean</li>
	 * 	<li>"null":																null</li>
	 * 	<li>... otherwise:														String</li>
	 * </ul>
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
		Number n=this.integerFormat.parse(field, position);
		if(position.getIndex()==field.length())return n;

		//try floating point numbers
		position.setIndex(0);
		position.setErrorIndex(-1);
		n=this.numberFormat.parse(field, position);
		if(position.getIndex()==field.length())return n;

		//try null
		if("null".equals(field))return null;

		//Otherwise, just the string
		return field;
	}

	//Helpers -------------------------------------------

	/** Signals End-of-record. */
	private static class EOR extends Exception{}
}