package havis.net.ui.rf.client;

import havis.net.ui.shared.resourcebundle.ResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class FirmwarePanel extends Composite implements HasValueChangeHandlers<Boolean>, HasEnabled {

	@UiField ToggleButton install;
	@UiField TextBox version;
	@UiField Label label;
	
	ResourceBundle res = ResourceBundle.INSTANCE;
	
	private static FirmwarePanelUiBinder uiBinder = GWT
			.create(FirmwarePanelUiBinder.class);

	interface FirmwarePanelUiBinder extends UiBinder<Widget, FirmwarePanel> {
	}

	public FirmwarePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setVersion(String version) {
		this.version.setText(version);
	}
	
	public String getVersion() {
		return this.version.getText();
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.label.setStyleName(res.css().disabledText(), !enabled);
		this.install.setVisible(enabled);
	}

	@Override
	public boolean isEnabled() {
		return this.install.isEnabled();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return install.addValueChangeHandler(handler);
	}
	
	public void setButtonUp() {
		install.setValue(false);
	}
}
