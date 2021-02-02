package havis.net.ui.rf.client;

import havis.device.rf.configuration.ConnectType;
import havis.device.rf.configuration.TagSmoothingSettings;
import havis.net.ui.rf.client.event.HasSettingsChangeHandlers;
import havis.net.ui.rf.client.event.SettingsChangeEvent;
import havis.net.ui.rf.client.event.SettingsChangeHandler;
import havis.net.ui.rf.client.event.SettingsType;
import havis.net.ui.shared.client.ConfigurationSection;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TagSmoothingPanel extends ConfigurationSection implements
		HasSettingsChangeHandlers, LeafValueEditor<TagSmoothingSettings> {
	private static TagSmoothingPanelUiBinder uiBinder = GWT
			.create(TagSmoothingPanelUiBinder.class);
	private boolean automaticChange = true;

	interface TagSmoothingPanelUiBinder extends
			UiBinder<Widget, TagSmoothingPanel> {
	}

	@UiField
	TextBox glimpsedTimeout;
	@UiField
	TextBox observedCountThreshold;
	@UiField
	TextBox observedTimeThreshold;
	@UiField
	TextBox lostTimeout;

	@Ignore
	@UiField
	SlideButton tagSmoothingSwitch;

	private Driver driver = GWT.create(Driver.class);

	interface Driver extends
			SimpleBeanEditorDriver<TagSmoothingSettings, TagSmoothingPanel> {
	}

	@UiConstructor
	public TagSmoothingPanel(String name) {
		super(name);
		initWidget(uiBinder.createAndBindUi(this));

		driver.initialize(this);

		// set state so that background of switch is set
		setConnectType(ConnectType.FALSE);
		tagSmoothingSwitch
				.addValueChangeHandler(new ValueChangeHandler<ConnectType>() {

					@Override
					public void onValueChange(
							ValueChangeEvent<ConnectType> event) {
						// ignore constructor call
						if (!automaticChange) {
							fireEvent(new SettingsChangeEvent(SettingsType.TAG_SMOOTHING));
						}
					}
				});
		tagSmoothingSwitch.setEnabled(true);
		tagSmoothingSwitch.getElement().getStyle().setCursor(Cursor.POINTER);
	}

	@UiHandler({ "glimpsedTimeout" })
	void onGlimspedTimeoutChange(ChangeEvent event) {
		if (!isInteger(glimpsedTimeout.getValue())) {
			if (!glimpsedTimeout.getValue().isEmpty()) {
				glimpsedTimeout.setValue("");
				CustomMessageWidget
						.show("Value for glimpsed timeout should be a numeric number greater than 0",
								MessageType.ERROR);
			}
		}

		if (!automaticChange) {
			fireEvent(new SettingsChangeEvent(SettingsType.TAG_SMOOTHING));
		}

		driver.flush();
	}

	@UiHandler({ "observedCountThreshold" })
	void onObservedCountThresholdChange(ChangeEvent event) {
		if (!isInteger(observedCountThreshold.getValue())) {
			if (!observedCountThreshold.getValue().isEmpty()) {
				observedCountThreshold.setValue("");
				CustomMessageWidget
						.show("Value for observed count threshold should be a numeric number greater than 0",
								MessageType.ERROR);
			}
		}

		if (!automaticChange) {
			fireEvent(new SettingsChangeEvent(SettingsType.TAG_SMOOTHING));
		}

		driver.flush();
	}

	@UiHandler({ "observedTimeThreshold" })
	void onObservedTimeThresholdChange(ChangeEvent event) {
		if (!isInteger(observedTimeThreshold.getValue())) {
			if (!observedTimeThreshold.getValue().isEmpty()) {
				observedTimeThreshold.setValue("");
				CustomMessageWidget
						.show("Value for observed time threshold should be a numeric number greater than 0",
								MessageType.ERROR);
			}
		}

		if (!automaticChange) {
			fireEvent(new SettingsChangeEvent(SettingsType.TAG_SMOOTHING));
		}

		driver.flush();
	}

	@UiHandler({ "lostTimeout" })
	void onLostTimeoutChange(ChangeEvent event) {
		if (!isInteger(lostTimeout.getValue())) {
			if (!lostTimeout.getValue().isEmpty()) {
				lostTimeout.setValue("");
				CustomMessageWidget
						.show("Value for lost timeout should be a numeric number greater than 0",
								MessageType.ERROR);
			}
		}

		if (!automaticChange) {
			fireEvent(new SettingsChangeEvent(SettingsType.TAG_SMOOTHING));
		}

		driver.flush();
	}

	private boolean isInteger(String value) {
		Integer i = null;
		try {
			i = Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return false;
		}

		if (i == null || i.intValue() < 0)
			return false;
		return true;
	}

	public void setTagSmoothingSettings(TagSmoothingSettings settings) {
		driver.edit(settings);
	}

	@Override
	public HandlerRegistration addSettingsChangeHandler(
			SettingsChangeHandler handler) {
		return addHandler(handler, SettingsChangeEvent.getType());
	}

	@Override
	public void setValue(TagSmoothingSettings settings) {
		// set flag for ignoring all change handler when filling editor
		automaticChange = true;
		if (settings != null) {
			glimpsedTimeout
					.setValue(settings.getGlimpsedTimeout() != null ? settings
							.getGlimpsedTimeout().toString() : "");
			observedCountThreshold.setValue(settings
					.getObservedCountThreshold() != null ? settings
					.getObservedCountThreshold().toString() : "");
			observedTimeThreshold
					.setValue(settings.getObservedTimeThreshold() != null ? settings
							.getObservedTimeThreshold().toString() : "");
			lostTimeout.setValue(settings.getLostTimeout() != null ? settings
					.getLostTimeout().toString() : "");

			setConnectType(ConnectType.valueOf(String.valueOf(
					settings.isEnabled()).toUpperCase()));
		} else {
			glimpsedTimeout.setValue("");
			observedCountThreshold.setValue("");
			observedTimeThreshold.setValue("");
			lostTimeout.setValue("");

			setConnectType(ConnectType.FALSE);
		}
		automaticChange = false;
	}

	@Override
	public TagSmoothingSettings getValue() {
		Integer glimpsedTimeoutValue = isInteger(glimpsedTimeout.getValue()) ? Integer
				.valueOf(glimpsedTimeout.getValue()) : null;
		Integer observedCountThresholdValue = isInteger(observedCountThreshold
				.getValue()) ? Integer.valueOf(observedCountThreshold
				.getValue()) : null;
		Integer observedTimeThresholdValue = isInteger(observedTimeThreshold
				.getValue()) ? Integer
				.valueOf(observedTimeThreshold.getValue()) : null;
		Integer lostTimeoutValue = isInteger(lostTimeout.getValue()) ? Integer
				.valueOf(lostTimeout.getValue()) : null;

		TagSmoothingSettings settings = new TagSmoothingSettings(
				glimpsedTimeoutValue, observedCountThresholdValue,
				observedTimeThresholdValue, lostTimeoutValue);

		String state = tagSmoothingSwitch.getValue().name();
		if (state.toLowerCase().equals(Boolean.toString(Boolean.TRUE))
				|| state.toLowerCase().equals(Boolean.toString(Boolean.FALSE)))
			settings.setEnabled(Boolean.parseBoolean(state));
		else {
			settings.setEnabled(false);
		}
		return settings;
	}

	public void setConnectType(ConnectType connectType) {
		tagSmoothingSwitch.setValue(connectType);
	}
}
