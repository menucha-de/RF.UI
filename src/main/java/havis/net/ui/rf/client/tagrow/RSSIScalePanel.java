package havis.net.ui.rf.client.tagrow;

import havis.net.ui.shared.client.list.ComparableWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RSSIScalePanel extends Composite implements ComparableWidget<RSSIScalePanel> {

	private static RSSIScalePanelUiBinder uiBinder = GWT
			.create(RSSIScalePanelUiBinder.class);

	interface RSSIScalePanelUiBinder extends UiBinder<Widget, RSSIScalePanel> {
	}

	final static int MAX = 255;
	private int rssiValue;
	private boolean found = false;
	
	@UiField
	Label value;
	
	@UiField
	SimplePanel scale;
	
	public RSSIScalePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public RSSIScalePanel(int value, boolean found) {
		this();
		this.rssiValue = value;
		this.found = found;
		setValue(value);
	}
	
	public void setFound(boolean found) {
		this.found = found;
	}
	
	public void setValue(int value) {
		if (found) {
			this.rssiValue = value;
			this.scale.setWidth(((value + 128) * 143 / 255) + "px");
			this.value.setText(Integer.toString(value));
		} else {
			this.rssiValue = -128;
			this.scale.setWidth(0 + "px");
			this.value.setText("");
		}
	}
	
	@Override
	public int compareTo(RSSIScalePanel rssi) {
		if (rssiValue < rssi.rssiValue)
			return -1;
		if (rssiValue > rssi.rssiValue)
			return 1;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RSSIScalePanel) {
			return this.rssiValue == ((RSSIScalePanel) obj).rssiValue;
		}
		return super.equals(obj);
	}
}
