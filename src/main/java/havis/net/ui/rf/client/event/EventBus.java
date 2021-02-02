package havis.net.ui.rf.client.event;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class EventBus {

	private EventBus() {
		
	}
	
	private static final SimpleEventBus INSTANCE = GWT.create(SimpleEventBus.class);
	
	public static SimpleEventBus get() {
		return INSTANCE;
	}
}
