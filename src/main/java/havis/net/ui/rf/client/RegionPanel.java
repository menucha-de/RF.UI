package havis.net.ui.rf.client;

import java.io.IOException;
import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasConstrainedValue;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.shared.resourcebundle.ResourceBundle;

public class RegionPanel extends Composite implements HasEnabled, HasConstrainedValue<String>, HasFocusHandlers {

	@UiField(provided = true)
	ValueListBox<String> regionList = new ValueListBox<>(new Renderer<String>() {
		@Override
		public String render(String object) {
			return object;
		}

		@Override
		public void render(String object, Appendable appendable) throws IOException {
			appendable.append(object);
		}
	});

	@UiField
	Label regionLabel;

	@UiField
	Button apply;

	private String region;

	ResourceBundle res = ResourceBundle.INSTANCE;

	private static RegionPanelUiBinder uiBinder = GWT.create(RegionPanelUiBinder.class);

	interface RegionPanelUiBinder extends UiBinder<Widget, RegionPanel> {
	}

	public RegionPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("regionList")
	void onValueChange(ValueChangeEvent<String> event) {
		apply.setVisible(true);
	}

	@UiHandler("apply")
	void onApplyClick(ClickEvent event) {
		ValueChangeEvent.<String>fire(this, regionList.getValue());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return region;
	}

	@Override
	public void setValue(String value) {
		region = value;
		regionList.setValue(value);
		apply.setVisible(false);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		region = value;
		regionList.setValue(value, fireEvents);
		apply.setVisible(false);
	}

	@Override
	public void setAcceptableValues(Collection<String> values) {
		regionList.setAcceptableValues(values);
	}

	@Override
	public boolean isEnabled() {
		return regionList.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		regionLabel.setStyleName(res.css().disabledText(), !enabled);
		regionList.setEnabled(enabled);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return regionList.addDomHandler(handler, FocusEvent.getType());
	}
}
