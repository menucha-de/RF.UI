package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PowerChangeEvent extends GwtEvent<PowerChangeHandler> {

	private static final Type<PowerChangeHandler> TYPE = new Type<PowerChangeHandler>();

	private short value;
	private short antennaID;
	
	public PowerChangeEvent(short antennaID, short value) {
		this.value = value;
		this.antennaID = antennaID;
	}

	public short getValue() {
		return value;
	}
	
	public short getAntennaID() {
		return antennaID;
	}
	
	@Override
	public Type<PowerChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PowerChangeHandler handler) {
		handler.onPowerChange(this);
	}

	public static Type<PowerChangeHandler> getType() {
		return TYPE;
	}
}
