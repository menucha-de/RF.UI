package havis.net.ui.rf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class RegionChangeEvent extends GwtEvent<RegionChangeEventHandler> {

	private static final Type<RegionChangeEventHandler> TYPE = new Type<RegionChangeEventHandler>();
	
	private String region;
	
	public RegionChangeEvent(String region) {
		this.region = region;
	}
	
	public String getRegion() {
		return region;
	}
	
	@Override
	public Type<RegionChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RegionChangeEventHandler handler) {
		handler.onRegionChange(this);
	}

	public static Type<RegionChangeEventHandler> getType() {
		return TYPE;
	}

}
