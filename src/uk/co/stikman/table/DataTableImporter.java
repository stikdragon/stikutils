package uk.co.stikman.table;

import java.io.IOException;
import java.io.InputStream;

public interface DataTableImporter {
	void inport(DataTable dt, InputStream data) throws IOException;

}
