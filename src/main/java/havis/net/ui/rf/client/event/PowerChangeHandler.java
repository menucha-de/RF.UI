package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface PowerChangeHandler extends EventHandler {
	void onPowerChange(PowerChangeEvent event);
}
