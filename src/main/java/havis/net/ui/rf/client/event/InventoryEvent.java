package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class InventoryEvent extends GwtEvent<InventoryEventHandler> {

	private static final Type<InventoryEventHandler> TYPE = new Type<InventoryEventHandler>();
	
	private boolean running;
	
	public InventoryEvent(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}

	@Override
	public Type<InventoryEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(InventoryEventHandler handler) {
		handler.onInventoryEvent(this);
	}

	public static Type<InventoryEventHandler> getType() {
		return TYPE;
	}

}
