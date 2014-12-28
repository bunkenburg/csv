package cat.inspiracio.io;

import java.io.File;
import java.net.URL;

abstract class CSVTest {

	/** Returns the file system directory path where the passed class is located. */
	String getDirectoryPath(Class<?> c){
		URL url=c.getResource(c.getName().replace(c.getPackage().getName() + ".", "") + ".class");
		String path=url.getFile();
		path=path.substring(0, path.lastIndexOf("/"));
		File f = new File(path);
		return f.getAbsolutePath();
	}

}
