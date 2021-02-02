package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasInventoryEventHandlers extends HasHandlers {
	HandlerRegistration addRegionChangeEventHandler(RegionChangeEventHandler handler);
}
