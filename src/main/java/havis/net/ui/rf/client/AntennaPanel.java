package havis.net.ui.rf.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import havis.device.rf.capabilities.TransmitPowerTableEntry;
import havis.device.rf.configuration.AntennaConfiguration;
import havis.device.rf.configuration.ConnectType;
import havis.net.ui.rf.client.event.HasPowerChangeHandlers;
import havis.net.ui.rf.client.event.PowerChangeEvent;
import havis.net.ui.rf.client.event.PowerChangeHandler;
import havis.net.ui.shared.resourcebundle.ConstantsResource;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

public class AntennaPanel extends Composite
		implements HasPowerChangeHandlers, HasValueChangeHandlers<ConnectType>, HasEnabled {

	private static ConstantsResource cons = ConstantsResource.INSTANCE;
	private static final int MAX_SCALE = 150;

	private List<TransmitPowerTableEntry> table;
	private AntennaConfiguration antennaConfiguration;

	private class PowerTimer extends Timer {

		private boolean increase;

		public void setIncrease(boolean increase) {
			this.increase = increase;
		}

		@Override
		public void run() {
			if (increase) {
				increase();
			} else {
				decrease();
			}
		}
	}

	private PowerTimer mouseTimer = new PowerTimer();

	@UiField
	Label antennaLabel;
	@UiField
	Button minus;
	@UiField
	Button plus;
	@UiField
	SimplePanel scale;
	@UiField
	TextBox dbmValue;
	@UiField
	TextBox mwValue;
	@UiField
	AntennaSwitch antennaSwitch;
	@UiField
	FlowPanel powerScale;

	ResourceBundle res = ResourceBundle.INSTANCE;
	private RFHardwareData data;
	private boolean mouseDown;
	private short powerIndex;
	private boolean changed;

	private static AntennaPanelUiBinder uiBinder = GWT.create(AntennaPanelUiBinder.class);

	interface AntennaPanelUiBinder extends UiBinder<Widget, AntennaPanel> {
	}

	public AntennaPanel(short antennaID, RFHardwareData data) {
		initWidget(uiBinder.createAndBindUi(this));
		this.data = data;
		setAntennaID(antennaID);
	}

	private void setAntennaID(short antennaID) {
		antennaLabel.setText(cons.antenna() + " " + antennaID);
	}

	private int getMaxIndex() {
		if (table != null) {
			return table.size() - 1;
		}
		return 0;
	}

	@UiHandler({ "minus", "plus" })
	void onPowerMouseDown(MouseDownEvent event) {
		if (!mouseDown) {
			mouseDown = true;
			boolean increase = event.getSource().equals(plus);
			if (increase)
				increase();
			else
				decrease();
			mouseTimer.setIncrease(increase);
			mouseTimer.scheduleRepeating(300);
		}
	}

	private void fireChange() {
		if (mouseDown) {
			mouseTimer.cancel();
			mouseDown = false;
			if (changed) {
				fireEvent(new PowerChangeEvent(antennaConfiguration.getId(), powerIndex));
				changed = false;
			}
		}
	}

	@UiHandler({ "minus", "plus" })
	void onPowerMouseUp(MouseUpEvent event) {
		fireChange();
	}

	@UiHandler({ "minus", "plus" })
	void onPowerMouseOut(MouseOutEvent event) {
		fireChange();
	}

	private void decrease() {
		short powerIndex = antennaConfiguration.getTransmitPower();
		if (powerIndex > 0) {
			antennaConfiguration.setTransmitPower(--powerIndex);
			setValue(powerIndex);
			changed = true;
		}
	}

	private void increase() {
		short powerIndex = antennaConfiguration.getTransmitPower();
		if (powerIndex < getMaxIndex()) {
			antennaConfiguration.setTransmitPower(++powerIndex);
			setValue(powerIndex);
			changed = true;
		}
	}

	public void setValue(short index) {
		double width;
		int maxIndex = table.size() - 1;
		short transmitPower = table.get(index).getTransmitPower();

		if (transmitPower == 0) {
			powerScale.removeStyleName("active");
			powerScale.removeStyleName("full");
			powerScale.addStyleName("empty");
			width = 0;
		} else {
			if (index < maxIndex) {
				powerScale.removeStyleName("empty");
				powerScale.removeStyleName("full");
				powerScale.addStyleName("active");
				width = (double) MAX_SCALE / (double) (maxIndex - 1) * (double) index;
			} else {
				powerScale.removeStyleName("active");
				powerScale.removeStyleName("empty");
				powerScale.addStyleName("full");
				width = (double) MAX_SCALE / (double) maxIndex * (double) index;
			}
		}

		scale.setWidth(width + "px");
		dbmValue.setValue(String.valueOf(transmitPower), true);
		mwValue.setValue(String.valueOf(data.getMilliWatt(transmitPower)));
	}

	public short getValue() {
		return antennaConfiguration.getTransmitPower();
	}

	public void setPowerTable(List<TransmitPowerTableEntry> powerTable) {
		table = powerTable;
	}

	public void setConnected(boolean connected) {
		antennaSwitch.setConnected(connected);
	}

	@Override
	public void setEnabled(boolean value) {
		antennaLabel.setStyleName(res.css().disabledText(), !value);
		powerScale.setStyleName(res.css().disabledButton(), !value);
		minus.setEnabled(value);
		plus.setEnabled(value);
		antennaSwitch.setEnabled(value);
	}

	@Override
	public boolean isEnabled() {
		return minus.isEnabled();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ConnectType> handler) {
		return antennaSwitch.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addPowerChangeHandler(PowerChangeHandler handler) {
		return this.addHandler(handler, PowerChangeEvent.getType());
	}

	public AntennaConfiguration getAntennaConfiguration() {
		return antennaConfiguration;
	}

	public void setAntennaConfiguration(AntennaConfiguration antennaConfiguration) {
		this.antennaConfiguration = antennaConfiguration;
		setValue(antennaConfiguration.getTransmitPower());
	}

	public short getAntennaID() {
		return antennaConfiguration.getId();
	}

	public void setConnectType(ConnectType connectType) {
		antennaSwitch.setValue(connectType);
	}

	public void setPending(boolean value) {
		antennaSwitch.setPending(value);
	}
}
