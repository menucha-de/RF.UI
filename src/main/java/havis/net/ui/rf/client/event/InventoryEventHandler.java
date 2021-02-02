package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface InventoryEventHandler extends EventHandler {
	void onInventoryEvent(InventoryEvent event);
}
