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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

public class CSVWriterTest extends CSVTest{
	
    String NL=System.getProperty("line.separator");

    /** string */
    @Test public void tstring() throws IOException{
		Writer writer = new StringWriter();
		CSVWriter cw= new CSVWriter(writer);
		cw.writeln("bla");
	    String s=writer.toString();
	    assertEquals(s, "\"bla\"" + NL);
    }
    
    /** string with escape */
    @Test public void tstringEscape() throws IOException{
		Writer writer = new StringWriter();
		CSVWriter cw= new CSVWriter(writer);
		cw.writeln("\"hello\"");
	    String s=writer.toString();
	    assertEquals(s, "\"\"\"hello\"\"\"" + NL);
    }
    
    /** null */
    @Test public void tnull() throws IOException{
		Writer writer = new StringWriter();
		CSVWriter cw= new CSVWriter(writer);
		cw.writeln("bla", null);
	    String s=writer.toString();
	    assertEquals(s, "\"bla\",\"null\"" + NL);
    }

    /** true false */
    @Test public void tboolean() throws IOException{
		Writer writer = new StringWriter();
		CSVWriter cw= new CSVWriter(writer);
		cw.writeln(true, false);
	    String s=writer.toString();
	    assertEquals(s, "\"true\",\"false\"" + NL);
    }
    
    /** integer */
    @Test public void tinteger() throws IOException{
		Writer writer = new StringWriter();
		CSVWriter cw= new CSVWriter(writer);
		cw.writeln(-1234567890, 0, 12);
	    String s=writer.toString();
	    assertEquals(s, "\"-1234567890\",\"0\",\"12\"" + NL);
    }

    /** double */
    @Test public void tdouble() throws IOException{
		Writer writer = new StringWriter();
		CSVWriter cw= new CSVWriter(writer);
		cw.writeln(-12345678.9, 0.0, 1.2);
	    String s=writer.toString();
	    assertEquals(s, "\"-1.23456789E7\",\"0.0\",\"1.2\"" + NL);
    }
    
    /** setDelimiter() " ' */
    @Test public void tdelimiter() throws IOException{
		Writer writer=new StringWriter();
		CSVWriter cw=new CSVWriter(writer);
		cw.setDelimiter('\'');
		cw.writeln(-1, 0, 1);
	    String s=writer.toString();
	    assertEquals(s, "\'-1\',\'0\',\'1\'" + NL);
    }
    
	/** setSeparator()  , ; : TAB SPACE */
    @Test public void tseparator() throws IOException{
		Writer writer=new StringWriter();
		CSVWriter cw=new CSVWriter(writer);
		cw.setDelimiter('\'');
		cw.setSeparator(';');
		cw.writeln(-1, 0, 1);
	    String s=writer.toString();
	    assertEquals(s, "\'-1\';\'0\';\'1\'" + NL);
    }

	@Test public void testWriter() throws IOException {
		Writer writer = new StringWriter();
		CSVWriter cw= new CSVWriter(writer);
	    cw.writeln('a', 1, -1.5, "2010-10-15T18:15:00Z", false);
	    cw.write('b', 2, 0.33333333333333333333333333333333,"2012-07-17T12:42:00Z",true);
	    cw.close();
	    String s=writer.toString();
	    String e="\"a\",\"1\",\"-1.5\",\"2010-10-15T18:15:00Z\",\"false\"" + NL + "\"b\",\"2\",\"0.3333333333333333\",\"2012-07-17T12:42:00Z\",\"true\"";
	    assertEquals(s, e);
	}	
	
}