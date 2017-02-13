package cn.newphy.commons.data.generator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

public class FileUtils {

	public static String getWorkPath() {
		return System.getProperty("user.dir");
	}
	
	public static String readContent(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int c = 0;
			while((c = is.read(buf)) >= 0) {
				baos.write(buf, 0, c);
			}
			byte[] data = baos.toByteArray();
			baos.close();
			return new String(data, "UTF-8");
		}
		finally {
			if(is != null) {
				is.close();
			}
		}
	}
	
	public static void writeContent(File file, String content) throws IOException {
		if(!file.exists()) {
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		OutputStream os = new FileOutputStream(file);
		Writer writer = new OutputStreamWriter(os);
		try {
			writer.write(content);
			writer.flush();
		} finally {
			if(writer != null) {
				writer.close();
			}
			if(os != null) {
				os.close();
			}
		}
	}
}
