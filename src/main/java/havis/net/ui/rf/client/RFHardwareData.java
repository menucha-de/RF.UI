package havis.net.ui.rf.client;

import java.util.HashMap;
import java.util.List;

import havis.device.rf.capabilities.TransmitPowerTableEntry;
import havis.device.rf.configuration.AntennaConfiguration;
import havis.device.rf.configuration.InventorySettings;

public class RFHardwareData {

	private Boolean antennaChangeAllowed = null;
	private Boolean regionChangeAllowed = null;
	private Boolean firmwareInstallAllowed = null;
	private Boolean settingsChangeAllowed = null;

	private String firmware;

	private List<TransmitPowerTableEntry> powerTableEntries;
	private List<AntennaConfiguration> antennaConfigurations;
	private InventorySettings inventorySettings;
	private HashMap<Integer, Integer> dbmToMw;

	private String region;
	private List<String> regions;

	public RFHardwareData() {
		dbmToMw = new HashMap<>();
		dbmToMw.put(0, 0);
		dbmToMw.put(8, 6);
		dbmToMw.put(9, 8);
		dbmToMw.put(10, 10);
		dbmToMw.put(11, 13);
		dbmToMw.put(12, 16);
		dbmToMw.put(13, 20);
		dbmToMw.put(14, 25);
		dbmToMw.put(15, 32);
		dbmToMw.put(16, 40);
		dbmToMw.put(17, 50);
		dbmToMw.put(18, 63);
		dbmToMw.put(19, 79);
		dbmToMw.put(20, 100);
		dbmToMw.put(21, 126);
		dbmToMw.put(22, 158);
		dbmToMw.put(23, 200);
		dbmToMw.put(24, 250);
		dbmToMw.put(25, 316);
		dbmToMw.put(26, 398);
		dbmToMw.put(27, 500);
	}

	public int getMilliWatt(int dBm) {
		return dbmToMw.get(dBm);
	}

	public List<TransmitPowerTableEntry> getPowerTableEntries() {
		return powerTableEntries;
	}

	public List<AntennaConfiguration> getAntennaConfigurations() {
		return antennaConfigurations;
	}

	public void setPowerTableEntries(List<TransmitPowerTableEntry> powerTableEntries) {
		this.powerTableEntries = powerTableEntries;
	}

	public void setAntennaConfigurations(List<AntennaConfiguration> antennaConfigurations) {
		this.antennaConfigurations = antennaConfigurations;
	}

	public boolean isAntennaChangeAllowed() {
		return antennaChangeAllowed;
	}

	public void setAntennaChangeAllowed(boolean antennaChangeAllowed) {
		this.antennaChangeAllowed = antennaChangeAllowed;
	}

	public boolean isAntennaDataReady() {
		return powerTableEntries != null && antennaConfigurations != null && antennaChangeAllowed != null;
	}

	public void resetAntennaDataReady() {
		powerTableEntries = null;
		antennaConfigurations = null;
		antennaChangeAllowed = null;
	}

	public boolean isRegionChangeAllowed() {
		return regionChangeAllowed;
	}

	public String getRegion() {
		return region;
	}

	public List<String> getRegions() {
		return regions;
	}

	public void setRegionChangeAllowed(boolean regionChangeAllowed) {
		this.regionChangeAllowed = regionChangeAllowed;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setRegions(List<String> regions) {
		this.regions = regions;
	}

	public boolean isRegionDataReady() {
		return regionChangeAllowed != null && region != null && regions != null;
	}

	public void resetRegionDataReady() {
		regionChangeAllowed = null;
		region = null;
		regions = null;
	}

	public int getAntennaCount() {
		return antennaConfigurations.size();
	}

	public boolean isFirmwareInstallAllowed() {
		return firmwareInstallAllowed;
	}

	public String getFirmware() {
		return firmware;
	}

	public void setFirmwareInstallAllowed(boolean firmwareInstallAllowed) {
		this.firmwareInstallAllowed = firmwareInstallAllowed;
	}

	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	public boolean isFirmwareConfigurationReady() {
		return firmwareInstallAllowed != null && firmware != null;
	}

	public void resetFirmwareConfigurationReady() {
		firmwareInstallAllowed = null;
		firmware = null;
	}

	public InventorySettings getInventorySettings() {
		return inventorySettings;
	}

	public void setInventorySettings(InventorySettings inventorySettings) {
		this.inventorySettings = inventorySettings;
	}

	public boolean isSettingsReady() {
		return inventorySettings != null && settingsChangeAllowed != null;
	}

	public boolean isSettingsChangeAllowed() {
		return settingsChangeAllowed;
	}

	public void setSettingsChangeAllowed(boolean settingsChangeAllowed) {
		this.settingsChangeAllowed = settingsChangeAllowed;
	}

	public void resetSettingsChangeAllowed() {
		inventorySettings = null;
		settingsChangeAllowed = null;
	}
}
