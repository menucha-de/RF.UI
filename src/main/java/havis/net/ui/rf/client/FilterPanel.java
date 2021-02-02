package havis.net.ui.rf.client;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import havis.device.rf.configuration.SelectionMask;
import havis.net.ui.rf.client.event.SettingsChangeEvent;
import havis.net.ui.rf.client.event.SettingsChangeHandler;
import havis.net.ui.rf.client.event.SettingsType;
import havis.net.ui.rf.client.event.HasSettingsChangeHandlers;
import havis.net.ui.shared.client.ConfigurationSection;
import havis.net.ui.shared.client.table.CreateRowEvent;
import havis.net.ui.shared.client.table.CustomTable;
import havis.net.ui.shared.client.table.DeleteRowEvent;

public class FilterPanel extends ConfigurationSection implements HasSettingsChangeHandlers {

	@UiField
	CustomTable filterTable;

	private Timer flushTimer = new Timer() {

		@Override
		public void run() {
			driver.flush();
			fireEvent(new SettingsChangeEvent(SettingsType.FILTER));
		}
	};
	private Driver driver = GWT.create(Driver.class);

	interface Driver extends SimpleBeanEditorDriver<List<SelectionMask>, ListEditor<SelectionMask, FilterRow>> {
	}

	ListEditor<SelectionMask, FilterRow> filterEditor = ListEditor.of(new FilterEditorSource());

	private class FilterEditorSource extends EditorSource<FilterRow> {
		@Override
		public FilterRow create(int index) {
			FilterRow row = new FilterRow(flushTimer);
			filterTable.addRow(row);
			return row;
		}

		@Override
		public void dispose(FilterRow subEditor) {
			filterTable.deleteRow(subEditor);
		}
	}

	private static FilterPanelUiBinder uiBinder = GWT.create(FilterPanelUiBinder.class);

	interface FilterPanelUiBinder extends UiBinder<Widget, FilterPanel> {
	}

	@UiConstructor
	public FilterPanel(String name) {
		super(name);
		initWidget(uiBinder.createAndBindUi(this));
		driver.initialize(filterEditor);
		filterTable.setHeader(Arrays.asList("Bank", "Offset", "Length", "Mask"));
		filterTable.setColumnWidth(0, 7, Unit.EM);
		filterTable.setColumnWidth(1, 7, Unit.EM);
		filterTable.setColumnWidth(2, 7, Unit.EM);
		filterTable.setColumnWidth(3, 24, Unit.EM);
	}

	public void setSelectionMasks(List<SelectionMask> masks) {
		driver.edit(masks);
	}

	public List<SelectionMask> getSelectionMasks() {
		return filterEditor.getList();
	}

	@UiHandler("filterTable")
	void onCreateRow(CreateRowEvent event) {
		if (filterEditor.getList().size() < 4) {
			filterEditor.getList().add(new SelectionMask());
			driver.flush();
			fireEvent(new SettingsChangeEvent(SettingsType.FILTER));
		}
	}

	@UiHandler("filterTable")
	void onDeleteRow(DeleteRowEvent event) {
		filterEditor.getList().remove(event.getIndex());
		driver.flush();
		fireEvent(new SettingsChangeEvent(SettingsType.FILTER));
	}

	@Override
	public HandlerRegistration addSettingsChangeHandler(SettingsChangeHandler handler) {
		return addHandler(handler, SettingsChangeEvent.getType());
	}
}
