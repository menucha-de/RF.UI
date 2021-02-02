package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SettingsChangeEvent extends GwtEvent<SettingsChangeHandler> {

	private static final Type<SettingsChangeHandler> TYPE = new Type<SettingsChangeHandler>();
	
	private SettingsType settingsType;

	public SettingsChangeEvent(SettingsType settingsType) {
		super();
		this.settingsType = settingsType;
	}

	@Override
	public Type<SettingsChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SettingsChangeHandler handler) {
		handler.onSettingsChange(this);
	}

	public static Type<SettingsChangeHandler> getType() {
		return TYPE;
	}

	public SettingsType getSettingsType() {
		return settingsType;
	}
}
