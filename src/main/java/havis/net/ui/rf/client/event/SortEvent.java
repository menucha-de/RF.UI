package havis.net.ui.rf.client.event;

import havis.net.ui.shared.client.list.SortOrder;

import com.google.gwt.event.shared.GwtEvent;

public class SortEvent extends GwtEvent<SortEventHandler> {

	private static final Type<SortEventHandler> TYPE = new Type<SortEventHandler>();
	
	private int column;
	private SortOrder order;
	
	public SortEvent(int column, SortOrder order) {
		this.column = column;
		this.order = order;
	}
	
	@Override
	public Type<SortEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SortEventHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortEventHandler handler) {
		handler.onSort(this);
	}

	public int getColumn() {
		return column;
	}

	public SortOrder getOrder() {
		return order;
	}

}
