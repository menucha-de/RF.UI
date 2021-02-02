package havis.net.ui.rf.client;

import havis.device.rf.capabilities.DeviceCapabilities;
import havis.device.rf.capabilities.RegulatoryCapabilities;
import havis.device.rf.configuration.AntennaConfiguration;
import havis.device.rf.configuration.AntennaProperties;
import havis.device.rf.configuration.ConnectType;
import havis.device.rf.configuration.InventorySettings;
import havis.device.rf.tag.TagData;
import havis.net.rest.rf.async.RFDeviceServiceAsync;
import havis.net.ui.rf.client.event.PowerChangeEvent;
import havis.net.ui.rf.client.event.PowerChangeHandler;
import havis.net.ui.rf.client.event.SettingsType;
import havis.net.ui.rf.client.tagrow.RSSIScalePanel;
import havis.net.ui.rf.client.tagrow.TagFoundPanel;
import havis.net.ui.rf.client.tagrow.TagIDLabel;
import havis.net.ui.shared.client.ErrorPanel;
import havis.net.ui.shared.client.list.WidgetList;
import havis.net.ui.shared.client.widgets.LoadingSpinner;
import havis.net.ui.shared.data.HttpMethod;
import havis.net.ui.shared.resourcebundle.ConstantsResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class RFHardwarePresenter {

	interface View extends IsWidget {
		void setPresenter(RFHardwarePresenter presenter);

		RegionPanel getRegionPanel();

		FirmwarePanel getFirmwarePanel();

		FlowPanel getAntennaPanel();

		SettingsPanel getSettingsPanel();

		FilterPanel getFilterPanel();

		WidgetList getTagsList();

		ToggleButton getInventoryRunning();

		HasValue<Boolean> getExpanded();

		HasText getCurrentCount();

		HasText getAbsoluteCount();

		ErrorPanel getErrorPanel();

		TagSmoothingPanel getTagSmoothingPanel();
	}

	private class Callback<T> implements MethodCallback<T> {

		@Override
		public void onFailure(Method method, Throwable exception) {
		}

		@Override
		public void onSuccess(Method method, T response) {
		}
	}

	private static final int INVENTORY_INTERVALL = 1000;
	private static final String REGION_CHANGE_WARNING = "The Ha-VIS UHF RFID Reader must only be operated "
			+ "with the approved frequency adjustment of the respective country of deployment!";
	private static final String UNEXPECTED_ERROR = "An unexpected error occurred.";
	private static final String CONNECTION_ERROR = "No active connection to RF Device.";
	private static final String PARAMETER_ERROR = "Invalid parameter!";
	private static final String LOCKED_ERROR = "RF Device is locked.";
	private static final String UNAVAILABLE_ERROR = "RF Device is currently in use.";
	private static final String FIRMWARE_VERSION = FirmwareVersion.V_2_6.getVersion();
	private static final Widget[] WIDGET_TYPE = new Widget[] {};
	private static final String UNSPECIFIED = "Unspecified";
	private static final int OVERLAY_HEIGHT = 500;

	private View view;
	private RFDeviceServiceAsync service = GWT.create(RFDeviceServiceAsync.class);
	private HashMap<Short, AntennaPanel> antennaList = new HashMap<>();
	private HashMap<String, TagRow> tagsMap = new HashMap<String, TagRow>();
	private ConstantsResource cons = ConstantsResource.INSTANCE;
	private RFHardwareData data = new RFHardwareData();
	private ErrorPanel warningPanel = null;
	private LoadingSpinner overlay = new LoadingSpinner();

	private Timer timer = new Timer() {
		@Override
		public void run() {
			inventory();
		}
	};
	
	PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback() {

		@Override
		public void setPosition(int offsetWidth, int offsetHeight) {
			int top = (OVERLAY_HEIGHT - offsetHeight) / 2;
			int left = (Window.getClientWidth() - offsetWidth) / 2;
			overlay.setPopupPosition(left, top);
		}
	};

	public RFHardwarePresenter(final View view) {
		this.view = view;
		this.view.setPresenter(RFHardwarePresenter.this);

		// Disable controls until options have been requested.
		this.view.getRegionPanel().setEnabled(false);
		this.view.getExpanded().setValue(false, true);
		this.view.getFirmwarePanel().setEnabled(false);
		this.view.getSettingsPanel().setEnabled(false);
		this.view.getFilterPanel().setEnabled(false);
		this.view.getTagSmoothingPanel().setEnabled(false);
		this.view.getInventoryRunning().setEnabled(false);
		onReset();
	}

	private void showError(int httpStatus) {
		switch (httpStatus) {
		case 400:
			view.getErrorPanel().showErrorMessage(PARAMETER_ERROR);
			break;
		case 423:
			view.getErrorPanel().showErrorMessage(LOCKED_ERROR);
			break;
		case 500:
			view.getErrorPanel().showErrorMessage(UNEXPECTED_ERROR);
			break;
		case 502:
			view.getErrorPanel().showErrorMessage(CONNECTION_ERROR);
			break;
		case 503:
			view.getErrorPanel().showErrorMessage(UNAVAILABLE_ERROR);
			break;
		default:
			break;
		}
	}

	private void loadInventorySettings() {
		data.resetSettingsChangeAllowed();
		service.optionsSingulationControl(new Callback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				data.setSettingsChangeAllowed(HttpMethod.PUT.isAllowed(method.getResponse()));
				if (data.isSettingsReady()) {
					showSettingsPanel();
					showFilterPanel();
					showTagSmoothingPanel();
				}
			}
		});
		service.getInventorySettings(new Callback<InventorySettings>() {

			@Override
			public void onSuccess(Method method, InventorySettings response) {
				data.setInventorySettings(response);
				if (data.isSettingsReady()) {
					showSettingsPanel();
					showFilterPanel();
					showTagSmoothingPanel();
				}
			}
		});
	}

	private void showSettingsPanel() {
		view.getSettingsPanel().setChangeable(data.isSettingsChangeAllowed());
		view.getSettingsPanel().setSettings(data.getInventorySettings());
		view.getSettingsPanel().setEnabled(true);
	}

	private void showFilterPanel() {
		view.getFilterPanel().setSelectionMasks(data.getInventorySettings().getSelectionMasks());
		view.getFilterPanel().filterTable.setEnabled(data.isSettingsChangeAllowed());
		view.getFilterPanel().setEnabled(true);
	}

	private void showTagSmoothingPanel() {
		view.getTagSmoothingPanel().setTagSmoothingSettings(data.getInventorySettings().getTagSmoothing());
		view.getTagSmoothingPanel().setEnabled(true);
	}

	private void loadFirmwareData() {
		data.resetFirmwareConfigurationReady();
		service.optionsInstallFirmware(new Callback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				data.setFirmwareInstallAllowed(HttpMethod.GET.isAllowed(method.getResponse()));
				if (data.isFirmwareConfigurationReady()) {
					showFirmwareConfiguration();
				}
			}
		});

		service.getDeviceCapabilities(new Callback<DeviceCapabilities>() {

			@Override
			public void onSuccess(Method method, DeviceCapabilities response) {
				data.setFirmware(response.getFirmware());
				if (data.isFirmwareConfigurationReady()) {
					showFirmwareConfiguration();
				}
			}
		});
	}

	private void showFirmwareConfiguration() {
		view.getFirmwarePanel().setVersion(data.getFirmware());
		boolean active = !FIRMWARE_VERSION.equals(data.getFirmware());
		view.getFirmwarePanel().setEnabled(data.isFirmwareInstallAllowed() && active);
	}

	public void onReset() {
		data.resetRegionDataReady();
		service.getRegion(new TextCallback() {
			@Override
			public void onSuccess(Method method, String response) {
				data.setRegion(response);
				loadRegionData();
				loadAntennaData();
				loadInventorySettings();
				loadFirmwareData();
				view.getInventoryRunning().setEnabled(true);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				showError(method.getResponse().getStatusCode());
			}
		});
	}

	public void onToggleList() {
		view.getTagsList().setVisible(view.getExpanded().getValue());
	}

	public void onRegionChange(final String newRegion) {
		overlay.setPopupPositionAndShow(posCallback);
		final String region = view.getRegionPanel().getValue();
		service.setRegion(newRegion, new Callback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				view.getRegionPanel().setEnabled(data.isRegionChangeAllowed());
				view.getRegionPanel().setValue(newRegion);
				loadAntennaData();
				overlay.hide();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				view.getRegionPanel().setValue(region);
				view.getRegionPanel().setEnabled(data.isRegionChangeAllowed());
				showError(method.getResponse().getStatusCode());
				overlay.hide();
			}
		});
	}

	private void loadRegionData() {
		service.optionsRegion(new Callback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				data.setRegionChangeAllowed(HttpMethod.PUT.isAllowed(method.getResponse()));
				if (data.isRegionDataReady()) {
					showRegionConfiguration();
				}
			}
		});

		service.getSupportedRegions(new Callback<List<String>>() {

			@Override
			public void onSuccess(Method method, List<String> response) {
				data.setRegions(response);
				if (data.isRegionDataReady()) {
					showRegionConfiguration();
				}
			}
		});
	}

	private void showRegionConfiguration() {
		view.getRegionPanel().setValue(data.getRegion());
		view.getRegionPanel().setAcceptableValues(data.getRegions());
		view.getRegionPanel().setEnabled(data.isRegionChangeAllowed());
	}

	private PowerChangeHandler powerChange = new PowerChangeHandler() {

		@Override
		public void onPowerChange(PowerChangeEvent event) {
			overlay.setPopupPositionAndShow(posCallback);
			final AntennaPanel ap = (AntennaPanel) event.getSource();
			AntennaConfiguration config = ap.getAntennaConfiguration();
			service.setAntennaConfiguration(config.getId(), ap.getAntennaConfiguration(), new Callback<Void>() {

				@Override
				public void onSuccess(Method method, Void response) {
					overlay.hide();
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
					showError(method.getResponse().getStatusCode());
					overlay.hide();
				}
			});
		}
	};

	private class ConnectTypeChangeHandler implements ValueChangeHandler<ConnectType> {
		private AntennaPanel antennaPanel;

		public ConnectTypeChangeHandler(AntennaPanel antennaPanel) {
			this.antennaPanel = antennaPanel;
		}

		@Override
		public void onValueChange(ValueChangeEvent<ConnectType> event) {
			overlay.setPopupPositionAndShow(posCallback);
			antennaPanel.setPending(true);
			final AntennaConfiguration config = antennaPanel.getAntennaConfiguration();
			final ConnectType oldValue = config.getConnect();
			config.setConnect(event.getValue());
			service.setAntennaConfiguration(config.getId(), config, new Callback<Void>() {

				@Override
				public void onSuccess(Method method, Void response) {
					for (AntennaPanel ap : antennaList.values()) {
						AntennaConfiguration apConf = ap.getAntennaConfiguration();
						if (apConf.getConnect() == ConnectType.AUTO) {
							setAntennaConnected(ap);
						}
					}
					antennaPanel.setPending(false);
					overlay.hide();
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
					config.setConnect(oldValue);
					showError(method.getResponse().getStatusCode());
					antennaPanel.setPending(false);
					overlay.hide();
				}
			});

		}
	}

	private boolean isRegionSet() {
		return !view.getRegionPanel().getValue().equals(UNSPECIFIED);
	}

	private void showAntennaConfiguration() {
		for (AntennaConfiguration conf : data.getAntennaConfigurations()) {
			AntennaPanel ap = antennaList.get(conf.getId());
			if (ap == null) {
				ap = new AntennaPanel(conf.getId(), data);
				ap.addPowerChangeHandler(powerChange);
				ap.addValueChangeHandler(new ConnectTypeChangeHandler(ap));
				antennaList.put(conf.getId(), ap);
				view.getAntennaPanel().add(ap);
			}
			ap.setPowerTable(data.getPowerTableEntries());
			ap.setAntennaConfiguration(conf);
			ap.setEnabled(isRegionSet() && data.isAntennaChangeAllowed());
			ConnectType connect = conf.getConnect() != null ? conf.getConnect() : ConnectType.AUTO;
			ap.setConnectType(connect);
			setAntennaConnected(ap);
		}
		setListHeader();
	}

	private void setAntennaConnected(final AntennaPanel antennaPanel) {
		service.getAntennaProperties(antennaPanel.getAntennaID(), new Callback<AntennaProperties>() {
			@Override
			public void onSuccess(Method method, AntennaProperties response) {
				antennaPanel.setConnected(response.isConnected());
			}
		});
	}

	private void loadAntennaData() {
		data.resetAntennaDataReady();
		service.optionsAntennaConfiguration(new Callback<Void>() {
			@Override
			public void onSuccess(Method method, Void response) {
				data.setAntennaChangeAllowed(HttpMethod.PUT.isAllowed(method.getResponse()));
				if (data.isAntennaDataReady()) {
					showAntennaConfiguration();
				}
			}
		});

		service.getRegulatoryCapabilities(new Callback<RegulatoryCapabilities>() {

			@Override
			public void onSuccess(Method method, RegulatoryCapabilities response) {
				data.setPowerTableEntries(response.getTransmitPowerTable().getEntryList());
				if (data.isAntennaDataReady()) {
					showAntennaConfiguration();
				}
			}
		});

		service.getAntennaConfigurations(new Callback<List<AntennaConfiguration>>() {

			@Override
			public void onSuccess(Method method, List<AntennaConfiguration> response) {
				data.setAntennaConfigurations(response);
				if (data.isAntennaDataReady()) {
					showAntennaConfiguration();
				}
			}
		});
	}

	private void clearInventory() {
		view.getCurrentCount().setText("0");
		view.getAbsoluteCount().setText("0");
		view.getTagsList().clear();
		tagsMap.clear();
	}

	private void setListHeader() {
		view.getTagsList().removeHeader();
		view.getTagsList().addHeaderCell(cons.foundCap());
		view.getTagsList().addHeaderCell("Transponder ID");
		for (int i = 1; i <= data.getAntennaCount(); i++) {
			view.getTagsList().addHeaderCell("RSSI " + cons.antenna() + " " + i);
		}
	}

	private void resetFound() {
		for (Entry<String, TagRow> entry : tagsMap.entrySet()) {
			entry.getValue().reset();
		}
	}

	private static class TagRow {
		private TagFoundPanel tagFoundPanel;
		private TagIDLabel tagIDLabel;
		private ArrayList<RSSIScalePanel> rssi = new ArrayList<RSSIScalePanel>();

		public TagRow(TagData tagData, int antennaCount) {
			if (tagData != null) {
				this.tagFoundPanel = new TagFoundPanel(true);
				this.tagIDLabel = new TagIDLabel(tagData.getEpc());
				for (int i = 1; i <= antennaCount; i++) {
					if (tagData.getAntennaID() == i)
						rssi.add(new RSSIScalePanel(tagData.getRssi(), true));
					else
						rssi.add(new RSSIScalePanel(0, false));
				}
			} else {
				this.tagFoundPanel = new TagFoundPanel(false);
				this.tagIDLabel = new TagIDLabel("ERROR");
				for (int i = 1; i <= antennaCount; i++) {
					rssi.add(new RSSIScalePanel(0, false));
				}
			}
		}

		public void setFound(boolean found) {
			tagFoundPanel.set(found);
		}

		public void setRSSI(short antennaID, int value) {
			int index = antennaID - 1;
			rssi.get(index).setFound(true);
			rssi.get(index).setValue(value);
		}

		public void reset() {
			tagFoundPanel.set(false);
			for (RSSIScalePanel rssiPanel : rssi) {
				rssiPanel.setFound(false);
				rssiPanel.setValue(0);
			}
		}

		public Widget[] getWidgets() {
			ArrayList<Widget> widgets = new ArrayList<Widget>();
			widgets.add(tagFoundPanel);
			widgets.add(tagIDLabel);
			for (RSSIScalePanel rssi : this.rssi) {
				widgets.add(rssi);
			}
			return widgets.toArray(WIDGET_TYPE);
		}
	}

	private void addTags(List<TagData> tags) {
		HashSet<String> epcs = new HashSet<String>();
		resetFound();
		if (tags != null) {
			for (TagData tagdata : tags) {
				StringBuilder s = new StringBuilder(tagdata.getEpc().length);
				for (byte b : tagdata.getEpc()) {
					s.append(Integer.toHexString(b));
				}
				String epcString = s.toString();
				TagRow row = tagsMap.get(epcString);
				epcs.add(epcString);

				if (row == null) {
					row = new TagRow(tagdata, data.getAntennaCount());
					view.getTagsList().addItem(row.getWidgets());
					tagsMap.put(epcString, row);
				} else {
					row.setFound(true);
					row.setRSSI(tagdata.getAntennaID(), tagdata.getRssi());
				}
			}
		}
		view.getCurrentCount().setText(String.valueOf(epcs.size()));
		view.getAbsoluteCount().setText(String.valueOf(tagsMap.size()));
	}

	private void inventory() {
		service.getTags(new Callback<List<TagData>>() {

			@Override
			public void onSuccess(Method method, List<TagData> response) {
				addTags(response);
				timer.schedule(INVENTORY_INTERVALL);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				view.getInventoryRunning().setValue(false, true);
				showError(method.getResponse().getStatusCode());
			}
		});
	}

	public void onInventory() {
		view.getRegionPanel().setEnabled(data.isRegionChangeAllowed()
				&& !view.getInventoryRunning().getValue());
		if (view.getInventoryRunning().getValue()) {
			if (!view.getTagsList().isVisible()) {
				view.getExpanded().setValue(true, true);
			}
			clearInventory();
			timer.schedule(INVENTORY_INTERVALL);
		} else {
			timer.cancel();
		}
	}

	public void onInstallFirmware() {
		overlay.setPopupPositionAndShow(posCallback);
		service.installFirmware(new Callback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				loadFirmwareData();
				overlay.hide();
				view.getFirmwarePanel().setButtonUp();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				loadFirmwareData();
				overlay.hide();
				view.getFirmwarePanel().setButtonUp();
				showError(method.getResponse().getStatusCode());
			}
		});
	}

	public void onTagSmoothingChange() {
		service.setTagSmoothingSettings(data.getInventorySettings().getTagSmoothing(), new Callback<Void>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				loadInventorySettings();
				showError(method.getResponse().getStatusCode());
			}
		});
	}
	public void onSettingsChange(SettingsType settingsType) {
		MethodCallback<Void> callback = new Callback<Void>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				loadInventorySettings();
				showError(method.getResponse().getStatusCode());
			}
		};
		switch (settingsType) {
		case FILTER:
			service.setSelectionMasks(data.getInventorySettings().getSelectionMasks(), callback);
			break;
		case INVENTORY:
			service.setSingulationControl(data.getInventorySettings().getSingulationControl(), callback);
			service.setRssiFilter(data.getInventorySettings().getRssiFilter(), callback);
			break;
		case TAG_SMOOTHING:
			data.getInventorySettings().setTagSmoothing(view.getTagSmoothingPanel().getValue());
			service.setTagSmoothingSettings(data.getInventorySettings().getTagSmoothing(), callback);
			break;
		default:
			break;
		}
	}

	public void onClickRegion() {
		if (warningPanel == null) {
			warningPanel = view.getErrorPanel();
			warningPanel.showWarningMessage(REGION_CHANGE_WARNING);
		}
	}

	public void hideWarning() {
		if (warningPanel != null) {
			warningPanel.hide();
			warningPanel = null;
		}
	}
}
