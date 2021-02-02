package havis.net.ui.rf.client;

import java.io.IOException;
import java.text.ParseException;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBox;

import havis.device.rf.configuration.SelectionMask;
import havis.net.ui.shared.client.table.CustomWidgetRow;
import havis.net.ui.shared.client.widgets.CustomListBox;
import havis.net.ui.shared.client.widgets.CustomRenderer;
import havis.net.ui.shared.client.widgets.CustomValueBox;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

public class FilterRow extends CustomWidgetRow implements ValueAwareEditor<SelectionMask> {

	private SelectionMask selectionMask;

	CustomListBox<Short> bank = new CustomListBox<Short>(new CustomRenderer<Short>() {

		@Override
		public String render(Short value) {
			FilterBank bank = FilterBank.getTagMemoryBank(value);
			if (bank != null)
				return bank.toString();
			return null;
		}
	});

	@Ignore
	TextBox bitOffsetBox = new TextBox();
	CustomValueBox<Short> bitOffset = new CustomValueBox<Short>(bitOffsetBox, new Renderer<Short>() {

		@Override
		public String render(Short object) {
			if (object == null)
				return null;
			return object.toString();
		}

		@Override
		public void render(Short object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	}, new Parser<Short>() {

		@Override
		public Short parse(CharSequence text) throws ParseException {
			if (text != null && text.toString().trim().isEmpty())
				return 0;
			try {
				return Short.parseShort(text.toString(), 10);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	});

	@Ignore
	TextBox bitLengthBox = new TextBox();
	CustomValueBox<Short> bitLength = new CustomValueBox<Short>(bitLengthBox, new Renderer<Short>() {

		@Override
		public String render(Short object) {
			if (object == null)
				return null;
			return object.toString();
		}

		@Override
		public void render(Short object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	}, new Parser<Short>() {

		@Override
		public Short parse(CharSequence text) throws ParseException {
			if (text != null && text.toString().trim().isEmpty())
				return 0;
			try {
				return Short.parseShort(text.toString(), 10);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	});

	@Ignore
	TextBox mask = new TextBox();

	public FilterRow(final Timer flushTimer) {
		for (FilterBank filterBank : FilterBank.values()) {
			bank.addItem(filterBank.getBank());
		}

		BlurHandler blur = new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				flushTimer.schedule(100);
			}
		};

		FocusHandler focus = new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if (flushTimer != null && flushTimer.isRunning()) {
					flushTimer.cancel();
				}
			}
		};

		bank.addBlurHandler(blur);
		bank.addFocusHandler(focus);
		bank.setStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableListBox());

		bitOffset.addBlurHandler(blur);
		bitOffset.addFocusHandler(focus);
		bitOffset.setStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableTextBox());
		bitOffset.setInputType("number");

		bitLength.addBlurHandler(blur);
		bitLength.addFocusHandler(focus);
		bitLength.setStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableTextBox());
		bitLength.setInputType("number");
		bitLength.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				Short value = bitLength.getValue();
				if (value > 255) {
					bitLength.setValue((short) 255);
				}
				if (value < 0) {
					bitLength.setValue((short) 0);
				}
			}
		});

		mask.addBlurHandler(blur);
		mask.addFocusHandler(focus);
		mask.setStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableTextBox());

		addColumn(bank);
		addColumn(bitOffset);
		addColumn(bitLength);
		addColumn(mask);
	}

	@Override
	public void setDelegate(EditorDelegate<SelectionMask> delegate) {
	}

	@Override
	public void flush() {

		int len = mask.getValue().length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(mask.getValue().charAt(i), 16) << 4)
					+ Character.digit(mask.getValue().charAt(i + 1), 16));
		}
		selectionMask.setMask(data);
	}

	@Override
	public void onPropertyChange(String... paths) {
	}

	@Override
	public void setValue(SelectionMask value) {
		if (value != null) {
			selectionMask = value;
			if (value.getMask() != null) {
				String maskValue = "";
				for (byte b : value.getMask()) {
					int i = b & 255;
					if (i <= 16)
						maskValue += "0";
					maskValue += Integer.toHexString(i).toUpperCase();
				}

				mask.setValue(maskValue);
			}
		}
	}
}
