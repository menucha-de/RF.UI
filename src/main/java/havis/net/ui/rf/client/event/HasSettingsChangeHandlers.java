package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasSettingsChangeHandlers extends HasHandlers {
	HandlerRegistration addSettingsChangeHandler(SettingsChangeHandler handler);
}
