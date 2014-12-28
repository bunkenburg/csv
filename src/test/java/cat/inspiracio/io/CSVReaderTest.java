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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.junit.Test;

public class CSVReaderTest extends CSVTest {

	//getCount() How many records have been read?
	//readln(): Object[]
	//setDelimiter() " '
	//setSeparator() , ; : TAB SPACE
	
	@Test public void testClass() throws IOException {
		String filePath=getDirectoryPath(this.getClass()) + File.separator + "test.csv";
		CSVReader cr=new CSVReader(new FileReader(filePath));

		Object[] line=cr.readln();
		assertEquals(String.class, line[0].getClass());
		assertEquals(Long.class, line[1].getClass());
		assertEquals(Double.class, line[2].getClass());
		assertEquals(String.class, line[3].getClass());
		assertEquals(Boolean.class, line[4].getClass());
	}
	
	@Test public void testRead() throws IOException {
		String filePath=getDirectoryPath(this.getClass()) + File.separator + "test.csv";
		CSVReader cr=new CSVReader(new FileReader(filePath));

		Object[] line=cr.readln();
		assertEquals(line[0], "a");
		assertEquals(line[1], (long) 1);
		assertEquals(line[2], -1.5);
		assertEquals(line[3], "2010-10-15T18:15:00Z");
		assertEquals(line[4], false);
	}
	
	@Test public void fullRead() throws IOException {
		String filePath=getDirectoryPath(this.getClass()) + File.separator + "test.csv";
	    Reader reader=new FileReader(filePath);
	    CSVReader cr=new CSVReader(reader);

		assertEquals(Arrays.toString(cr.readln()), "[a, 1, -1.5, 2010-10-15T18:15:00Z, false]");
		assertEquals(Arrays.toString(cr.readln()), "[b, 2, 0.3333333333333333, 2012-07-17T12:42:00Z, true]");
		reader.close();
	}
	
	@Test public void testCount() throws IOException {
		String filePath=getDirectoryPath(this.getClass()) + File.separator + "test.csv";
		assertEquals(Utils.count(filePath), 2); //No CRLF
	}
	
}