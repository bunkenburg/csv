package inspiracio.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class CSVReaderTest {
	
	@Test public void trac_6030() throws IOException {
		String filePath=getDirectoryPath(this.getClass()) + File.separator + "6030.csv";
		CSVReader reader=new CSVReader(new FileReader(filePath));

		Object[] line=reader.readln();
		assertEquals(String.class, line[0].getClass());
		assertEquals(Long.class, line[1].getClass());
		assertEquals(Double.class, line[2].getClass());
		assertEquals(String.class, line[3].getClass());
		assertEquals(Boolean.class, line[4].getClass());
	}
	
	/** Returns the file system directory path where the passed class is located. */
	public static String getDirectoryPath(Class<?> c){
		URL url=c.getResource(c.getName().replace(c.getPackage().getName() + ".", "") + ".class");
		String path=url.getFile();
		path=path.substring(0, path.lastIndexOf("/"));
		File f = new File(path);
		return f.getAbsolutePath();
	}

}