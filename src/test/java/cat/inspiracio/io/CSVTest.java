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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.junit.Test;

public class CSVTest {

	//null
	//true false
	//integer
	//double
	//string
	@Test public void tAll() throws IOException{
		Writer w=new StringWriter();
		CSVWriter cw=new CSVWriter(w);
		cw.writeln(null, true, false, -1, 123456789, -1.3, "\"Hello\"");
		cw.close();
		String s=w.toString();
		
		StringReader r=new StringReader(s);
		CSVReader cr=new CSVReader(r);
		Object[] record=cr.readln();
		
		assertEquals(record.length, 7);
		assertEquals(record[0], null);
		assertEquals(record[1], true);
		assertEquals(record[2], false);
		assertEquals(record[3], -1);
		assertEquals(record[4], 123456789);
		assertEquals(record[5], -1.3);
		assertEquals(record[6], "\"Hello\"");
	}
	
}
