package uk.co.stikman.table;

import java.io.IOException;
import java.io.OutputStream;

public interface DataTableExport {

	void export(DataTable dt, OutputStream out) throws IOException;

}
