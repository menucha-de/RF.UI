package havis.net.ui.rf.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

import havis.device.rf.configuration.InventorySettings;
import havis.net.ui.rf.client.event.HasSettingsChangeHandlers;
import havis.net.ui.rf.client.event.SettingsChangeEvent;
import havis.net.ui.rf.client.event.SettingsChangeHandler;
import havis.net.ui.rf.client.event.SettingsType;
import havis.net.ui.shared.client.ConfigurationSection;
import havis.net.ui.shared.client.widgets.CustomValueBox;
import havis.net.ui.shared.client.widgets.Util;

public class SettingsPanel extends ConfigurationSection
		implements Editor<InventorySettings>, HasSettingsChangeHandlers {

	@Path("singulationControl.transitTime")
	@UiField(provided = true)
	CustomValueBox<Short> transitTime = new CustomValueBox<>(new TextBox(), Util.getShortRenderer(),
			Util.getShortParser());

	@Path("singulationControl.QValue")
	@UiField
	ValueListBox<Short> qValue;

	@Path("singulationControl.rounds")
	@UiField
	ValueListBox<Short> rounds;

	@Path("singulationControl.session")
	@UiField
	ValueListBox<Short> session;

	@Path("rssiFilter.minRssi")
	@UiField
	ValueListBox<Short> minRSSI;

	@Path("rssiFilter.maxRssi")
	@UiField
	ValueListBox<Short> maxRSSI;

	private Driver driver = GWT.create(Driver.class);

	interface Driver extends SimpleBeanEditorDriver<InventorySettings, SettingsPanel> {
	}

	private static SettingsPanelUiBinder uiBinder = GWT.create(SettingsPanelUiBinder.class);

	interface SettingsPanelUiBinder extends UiBinder<Widget, SettingsPanel> {
	}

	@UiConstructor
	public SettingsPanel(String name) {
		super(name);
		initWidget(uiBinder.createAndBindUi(this));

		driver.initialize(this);

		for (short i = 0; i <= 15; ++i) {
			qValue.setValue(i);
		}

		for (short i = 0; i <= 10; ++i) {
			rounds.setValue(i);
		}

		for (short i = 0; i <= 3; ++i) {
			session.setValue(i);
		}

		for (short i = -64; i <= 0; ++i) {
			minRSSI.setValue(i);
			maxRSSI.setValue(i);
		}

		transitTime.setValue((short) 0);
		transitTime.setInputType("number");
		transitTime.getElement().setAttribute("min", "0");
	}

	public void setSettings(InventorySettings settings) {
		driver.edit(settings);
	}

	@UiHandler({ "session", "qValue", "transitTime", "rounds", "minRSSI", "maxRSSI" })
	void onValueChange(ValueChangeEvent<Short> event) {
		if (transitTime.getValue() < 0) {
			transitTime.setValue((short) 0);
		}
		driver.flush();
		fireEvent(new SettingsChangeEvent(SettingsType.INVENTORY));
	}

	@Override
	public HandlerRegistration addSettingsChangeHandler(SettingsChangeHandler handler) {
		return addHandler(handler, SettingsChangeEvent.getType());
	}

	public void setChangeable(boolean changable) {
		session.setEnabled(changable);
		qValue.setEnabled(changable);
		transitTime.setEnabled(changable);
		rounds.setEnabled(changable);
		minRSSI.setEnabled(changable);
		maxRSSI.setEnabled(changable);
	}
}
