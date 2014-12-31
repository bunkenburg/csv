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
	
	/** Returns the file system directory path where the passed class is located. */
	String getDirectoryPath(Class<?> c){
		URL url=c.getResource(c.getName().replace(c.getPackage().getName() + ".", "") + ".class");
		String path=url.getFile();
		path=path.substring(0, path.lastIndexOf("/"));
		File f = new File(path);
		return f.getAbsolutePath();
	}

}
