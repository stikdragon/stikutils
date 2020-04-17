package uk.co.stikman.table;

public class DataField {

	private DataTable		table;
	private String			name;
	private AggregateMethod	aggregateMethod	= AggregateMethod.SUM;
	private int				index;
	private DataType		type			= DataType.STRING;
	private String			display			= null;
	private int				maxDisplayWidth	= -1;
	private Alignment		alignment		= Alignment.LEFT;

	DataField(DataTable table, String name) {
		this.table = table;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	void setIndex(int index) {
		this.index = index;
	}

	public DataTable getTable() {
		return table;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public AggregateMethod getAggregateMethod() {
		return aggregateMethod;
	}

	public void setAggregateMethod(AggregateMethod aggregateMethod) {
		this.aggregateMethod = aggregateMethod;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	/**
	 * If <code>null</code> then it has the same name as {@link #getName()}
	 * 
	 * @return
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * If <code>null</code> then it has the same name as {@link #getName()}
	 * 
	 * @return
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * Returns {@link #getName()}, or {@link #getDisplay()} if it's set instead
	 * 
	 * @return
	 */
	public String getActualFieldDisplay() {
		return display == null ? name : display;
	}

	/**
	 * If <code>-1</code> then it has no defined width
	 * 
	 * @return
	 */
	public int getMaxDisplayWidth() {
		return maxDisplayWidth;
	}

	/**
	 * If <code>-1</code> then it has no defined width
	 * 
	 * @return
	 */
	public void setMaxDisplayWidth(int displayWidth) {
		this.maxDisplayWidth = displayWidth;
	}

	/**
	 * This should not be called by users of DataTable. Use
	 * {@link DataTable#renameField}
	 * 
	 * @param newname
	 */
	void setName(String newname) {
		this.name = newname;
	}

	public void setValue(DataRecord rec, String value) {
		switch (type) {
		case DOUBLE:
			rec.setValue(index, Double.parseDouble(value));
			break;
		case INT:
			rec.setValue(index, Integer.parseInt(value));
			break;
		case STRING:
			rec.setValue(index, value);
			break;
		}
	}

	public void setValue(DataRecord rec, int value) {
		switch (type) {
		case DOUBLE:
			rec.setValue(index, (double) value);
			break;
		case INT:
			rec.setValue(index, value);
			break;
		case STRING:
			rec.setValue(index, Integer.toString(value));
			break;
		}
	}

	public void setValue(DataRecord rec, double value) {
		switch (type) {
		case DOUBLE:
			rec.setValue(index, value);
			break;
		case INT:
			rec.setValue(index, value);
			break;
		case STRING:
			rec.setValue(index, Double.toString(value));
			break;
		}
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

}

