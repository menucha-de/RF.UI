package havis.net.ui.rf.client;

import org.fusesource.restygwt.client.Defaults;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import havis.net.ui.rf.client.event.SettingsChangeEvent;
import havis.net.ui.shared.client.ErrorPanel;
import havis.net.ui.shared.client.list.WidgetList;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

public class RFHardware extends Composite implements EntryPoint, RFHardwarePresenter.View {

	@UiField
	FlowPanel configRows;
	@UiField
	RegionPanel regionPanel;
	@UiField
	FlowPanel stackContent;
	@UiField
	FlowPanel antennas;
	@UiField
	FilterPanel filters;
	@UiField
	SettingsPanel settings;
	@UiField
	FlowPanel inventoryPanel;
	@UiField
	TagSmoothingPanel tagSmoothingPanel;
	@UiField
	FirmwarePanel firmwarePanel;
	@UiField
	WidgetList tagsList;
	@UiField
	InlineLabel countCurrent;
	@UiField
	InlineLabel countAbsolute;
	@UiField
	ToggleButton inventoryButton;
	@UiField
	ToggleButton expandList;

	private RFHardwarePresenter presenter;

	private static RFHardwareUiBinder uiBinder = GWT.create(RFHardwareUiBinder.class);

	interface RFHardwareUiBinder extends UiBinder<Widget, RFHardware> {
	}

	public RFHardware() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		Defaults.setDateFormat(null);
		new RFHardwarePresenter(this);
		ResourceBundle.INSTANCE.css().ensureInjected();
	}

	@UiHandler("regionPanel")
	public void onSelectRegion(ValueChangeEvent<String> e) {
		presenter.onRegionChange(e.getValue());
	}

	@UiHandler("regionPanel")
	public void onClickRegion(FocusEvent e) {
		// TODO: ???
		presenter.onClickRegion();
	}

	@UiHandler("expandList")
	public void onToggleListChange(ValueChangeEvent<Boolean> e) {
		presenter.onToggleList();
	}

	@UiHandler("inventoryButton")
	public void onInventoryClick(ValueChangeEvent<Boolean> e) {
		presenter.onInventory();
	}

	@UiHandler("firmwarePanel")
	public void onInstallFirmware(ValueChangeEvent<Boolean> e) {
		if (e.getValue()) {
			presenter.onInstallFirmware();
		}
	}

	@UiHandler({ "settings", "filters", "tagSmoothingPanel" })
	public void onSettingsChange(SettingsChangeEvent e) {
		presenter.onSettingsChange(e.getSettingsType());
	}

	@Override
	public void setPresenter(RFHardwarePresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public RegionPanel getRegionPanel() {
		return regionPanel;
	}

	@Override
	public FlowPanel getAntennaPanel() {
		return antennas;
	}

	@Override
	public WidgetList getTagsList() {
		return tagsList;
	}

	@Override
	public ToggleButton getInventoryRunning() {
		return inventoryButton;
	}

	@Override
	public HasValue<Boolean> getExpanded() {
		return expandList;
	}

	@Override
	public HasText getCurrentCount() {
		return countCurrent;
	}

	@Override
	public HasText getAbsoluteCount() {
		return countAbsolute;
	}

	@Override
	public FirmwarePanel getFirmwarePanel() {
		return firmwarePanel;
	}

	@Override
	public FilterPanel getFilterPanel() {
		return filters;
	}

	@Override
	public SettingsPanel getSettingsPanel() {
		return settings;
	}

	@Override
	public ErrorPanel getErrorPanel() {
		return new ErrorPanel(0, 0);
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel.get().add(this);
		Defaults.setByteArraysToHexString(true);
	}

	@Override
	public TagSmoothingPanel getTagSmoothingPanel() {
		return tagSmoothingPanel;
	}
}