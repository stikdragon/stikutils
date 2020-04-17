package uk.co.stikman.table;

import java.io.Serializable;

public class DataField implements Serializable {

	private static final long	serialVersionUID	= -2639527992542401736L;
	private DataTable			table;
	private String				name;
	private AggregateMethod		aggregateMethod		= AggregateMethod.SUM;
	private int					index;
	private DataType			type				= DataType.STRING;

	public DataField() {

	}

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

}
