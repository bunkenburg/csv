/*  Copyright 2011 Alexander Bunkenburg alex@inspiracio.cat

    This file is part of csv.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package cat.inspiracio.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

public class CSVReaderTest extends CSVTest {

	/** getCount() How many records have been read?	*/
	@Test public void tcount0()throws IOException{count(0);}
	@Test public void tcount1()throws IOException{count(1);}
	@Test public void tcount2()throws IOException{count(2);}
	@Test public void tcount1000()throws IOException{count(1000);}
	
	/** Makes a CSV with some records, parses all, and counts the parsed records. */
	void count(int count) throws IOException{
		CSVReader reader=new CSVReader(records(count));		
		int records=0;
		assertEquals(records, reader.getCount());
		Object[]record=reader.readln();
		records++;
		while(record!=null){
			assertEquals(records, reader.getCount());
			record=reader.readln();
			records++;
		}
		assertEquals(count, reader.getCount());
	}
	
	/** Makes a reader with some records of CSV.
	 * Every records ends with \n, even the last one. */
	Reader records(int records){
		String record="bla";
		StringBuilder builder=new StringBuilder();
		for(int i=0; i<records; i++){
			builder.append(record);
			builder.append("\n");
		}
		return new StringReader(builder.toString());
	}

	/** readln() one empty string field, no delimiter */
	@Test public void treadln1empty()throws IOException{
		String in="\n\n";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]record=reader.readln();
			//record is [""]
			assertEquals(1, record.length);
			assertEquals("", record[0]);
		}
	}
	
	/** readln() two fields, no delimiter, EOL=LF */
	@Test public void treadln2stringLF()throws IOException{
		String in="one,two\none,two\n";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]record=reader.readln();
			//record is ["one", "two"]
			assertEquals(2, record.length);
			assertEquals("one", record[0]);
			assertEquals("two", record[1]);
		}
	}
	
	/** readln() two fields, no delimiter, EOL=CR */
	@Test public void treadln2stringCR()throws IOException{
		String in="one,two\rone,two\r";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]record=reader.readln();
			//record is ["one", "two"]
			assertEquals(2, record.length);
			assertEquals("one", record[0]);
			assertEquals("two", record[1]);
		}
	}
	
	/** readln() two fields, no delimiter, EOL=CRLF */
	@Test public void treadln2stringCRLF()throws IOException{
		String in="one,two\r\none,two\r\n";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]record=reader.readln();
			//record is ["one", "two"]
			assertEquals(2, record.length);
			assertEquals("one", record[0]);
			assertEquals("two", record[1]);
		}
	}
	
	/** null */
	@Test public void tnull()throws IOException{
		String in="null,null\nnull,null";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]r=reader.readln();
			//record is [null, null]
			assertEquals(2, r.length);
			assertEquals(null, r[0]);
			assertEquals(null, r[1]);
		}
	}
	
	/** boolean */
	@Test public void tboolean()throws IOException{
		String in="true,false\ntrue,false";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]r=reader.readln();
			//record is [null, null]
			assertEquals(2, r.length);
			assertEquals(true, r[0]);
			assertEquals(false, r[1]);
		}
	}

	/** integer */
	@Test public void tinteger()throws IOException{
		String in="-1,0,1\n\"-1\",\"0\",\"1\"";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]r=reader.readln();
			//record is [-1, 0, 1]
			assertEquals(3, r.length);
			assertEquals(-1, r[0]);
			assertEquals(0, r[1]);
			assertEquals(1, r[2]);
		}
	}

	/** double */
	@Test public void tdouble()throws IOException{
		String in="-1.0,0.1,1.2\n\"-1.0\",\"0.10\",\"1.2\"";
		CSVReader reader=new CSVReader(new StringReader(in));
		for(int i=0; i<2; i++){
			Object[]r=reader.readln();
			//record is [-1, 0.0, 1.0]
			assertEquals(3, r.length);
			assertEquals(-1, r[0]);
			assertEquals(0.1, r[1]);
			assertEquals(1.2, r[2]);
		}
	}

	/** string with delimiter */
	@Test public void tstringDelimiter()throws IOException{
		String in="\"one\"";
		CSVReader reader=new CSVReader(new StringReader(in));
		Object[]r=reader.readln();
		//record is ["one"]
		assertEquals(1, r.length);
		assertEquals("one", r[0]);
	}
	
	/** string with delimiter and escaping */
	@Test public void tstringDelimiterEscape()throws IOException{
		String in="\"\"\"Hello\"\"\"";
		CSVReader reader=new CSVReader(new StringReader(in));
		Object[]r=reader.readln();
		//record is [ "\"Hello\"" ]
		assertEquals(1, r.length);
		assertEquals("\"Hello\"", r[0]);
	}
	
	/** setDelimiter() " ' */
	@Test public void tstringSetDelimiterEscape()throws IOException{
		String in="'\"Hello\"'";
		CSVReader reader=new CSVReader(new StringReader(in));
		reader.setDelimiter('\'');
		Object[]r=reader.readln();
		//record is [ "\"Hello\"" ]
		assertEquals(1, r.length);
		assertEquals("\"Hello\"", r[0]);
	}
	
	/** setSeparator() , ; : TAB SPACE */
	@Test public void tstringSetSeparator()throws IOException{
		String in="\"a;b\";two";
		CSVReader reader=new CSVReader(new StringReader(in));
		reader.setSeparator(';');
		Object[]r=reader.readln();
		//record is [ "a;b" , "two" ]
		assertEquals(2, r.length);
		assertEquals("a;b", r[0]);
		assertEquals("two", r[1]);
	}
	
	@Test public void testClass() throws IOException {
	    CSVReader cr=getTestFile();
		Object[] line=cr.readln();
		assertEquals(String.class, line[0].getClass());
		assertEquals(Integer.class, line[1].getClass());
		assertEquals(Double.class, line[2].getClass());
		assertEquals(String.class, line[3].getClass());
		assertEquals(Boolean.class, line[4].getClass());
	}
	
	@Test public void testRead() throws IOException {
	    CSVReader cr=getTestFile();
		Object[] line=cr.readln();
		assertEquals(line[0], "a");
		assertEquals(line[1], 1);
		assertEquals(line[2], -1.5);
		assertEquals(line[3], "2010-10-15T18:15:00Z");
		assertEquals(line[4], false);
	}
	
	@Test public void fullRead() throws IOException {
	    CSVReader cr=getTestFile();
		assertEquals(Arrays.toString(cr.readln()), "[a, 1, -1.5, 2010-10-15T18:15:00Z, false]");
		assertEquals(Arrays.toString(cr.readln()), "[b, 2, 0.3333333333333333, 2012-07-17T12:42:00Z, true]");
	}
	
	private CSVReader getTestFile() throws FileNotFoundException{
		ClassLoader loader = getClass().getClassLoader();
		URL u=loader.getResource("test.csv");
		String s=u.getFile();
		File file = new File(s);
	    Reader reader=new FileReader(file);
	    return new CSVReader(reader);
	}
	
	
	// NEW FEATURES --------------------------------------------
	
	@Test public void TestReadString() throws IOException {
		String in="\"one\",\"two\r\none\",two\r\n";
		CSVReader reader=new CSVReader(new StringReader(in));
		assertEquals("one",reader.readString());
		assertEquals("two\r\none",reader.readString());
		assertEquals(null,reader.readString());
	}
	
	@Test public void TestReadBoolean() throws IOException {
		String in="t,false,two\r\n";
		CSVReader reader=new CSVReader(new StringReader(in));
		assertEquals(true, reader.readBoolean());
		assertEquals(false,reader.readBoolean());
		assertEquals(null,reader.readBoolean());
	}
}