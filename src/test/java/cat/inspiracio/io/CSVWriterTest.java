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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class CSVWriterTest extends CSVTest{
	
	//close()
	//setSeparator()  , ; : TAB SPACE
	//write(Object... fields)
	//writeln(Object... fields)
	
	@Test public void testWriter() throws IOException {
		String filePath = getDirectoryPath(this.getClass()) + File.separator + "test.csv";
		FileWriter writer = new FileWriter(filePath);
		CSVWriter cw= new CSVWriter(writer);
	    cw.writeln('a', 1, -1.5, "2010-10-15T18:15:00Z", false);
	    cw.write('b', 2, 0.33333333333333333333333333333333,"2012-07-17T12:42:00Z",true);
	    writer.close();
	}	
	
}