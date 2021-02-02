package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SettingsChangeHandler extends EventHandler {

	void onSettingsChange(SettingsChangeEvent event);
}
