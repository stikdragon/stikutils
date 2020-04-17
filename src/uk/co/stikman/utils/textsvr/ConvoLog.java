package uk.co.stikman.utils.textsvr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ConvoLog {
	private OutputStreamWriter	osw;

	public ConvoLog(File f) {
		super();
		if (f.exists())
			f.delete();
		try {
			osw = new OutputStreamWriter(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized void log(String pre, String s) {
		try {
			osw.write(pre + ": " + s.length() + " - " + s);
			osw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized void logNoSize(String pre, String s) {
		try {
			osw.write(pre + ": " + s);
			osw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public void close() throws IOException {
		osw.flush();
		osw.close();
	}

}
