package havis.net.ui.rf.client;

import havis.device.rf.configuration.ConnectType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;

public class SlideButton extends FocusPanel implements HasValue<ConnectType>, HasEnabled {

	private static final String BASE_STYLE = "webui-AntennaSwitch";

	private ConnectType connectType = ConnectType.FALSE;
	private boolean enabled;

	public SlideButton() {
		this.setStylePrimaryName(BASE_STYLE);
		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (enabled) {
					if (connectType == ConnectType.FALSE) {
						setValue(ConnectType.TRUE);
					} else if (connectType == ConnectType.TRUE) {
						setValue(ConnectType.FALSE);
					}
				}
			}
		});
	}

	private String getStyle(ConnectType type) {
		return type.toString().toLowerCase();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ConnectType> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public ConnectType getValue() {
		return connectType;
	}

	@Override
	public void setValue(ConnectType value) {
		this.connectType = value != null ? value : ConnectType.FALSE;
		ValueChangeEvent.fire(this, value);
		updateStyle();
	}

	@Override
	public void setValue(ConnectType value, boolean fireEvents) {
		setValue(connectType);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		updateStyle();
	}

	private void updateStyle() {
		this.removeStyleDependentName(getStyle(ConnectType.FALSE));
		this.removeStyleDependentName(getStyle(ConnectType.TRUE));

		this.setStyleDependentName(connectType.toString().toLowerCase(), true);
		this.setStyleName("disabled", !enabled);
	}
}
