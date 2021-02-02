package havis.net.ui.rf.client;

public enum FirmwareVersion {
	V_1_0("1.0"), V_1_1("1.1"), V_1_5("1.5"), V_1_7("1.7"), V_2_6("2.6");
	
	private String version;
	private FirmwareVersion(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
}
