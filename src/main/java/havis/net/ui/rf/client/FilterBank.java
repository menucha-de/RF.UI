package havis.net.ui.rf.client;

public enum FilterBank {
	RESERVED((short) 0, "Reserved"),
	EPC_UII((short) 1, "EPC/UII"),
	TID((short) 2, "TID"),
	USER((short) 3, "User");

	/**
	 * {@link FilterBank} value
	 */
	private short bank;
	/**
	 * {@link FilterBank} meaning
	 */
	private String meaning;

	private FilterBank(short bank, String meaning) {
		this.bank = bank;
		this.meaning = meaning;
	}

	/**
	 * Returns the {@link #bank}
	 * 
	 * @return {@link #bank}
	 */
	public short getBank() {
		return bank;
	}

	/**
	 * Returns the {@link #meaning}
	 * 
	 * @return {@link #meaning}
	 */
	public String getMeaning() {
		return meaning;
	}

	/**
	 * Returns the corresponding {@link FilterBank} or null
	 * 
	 * @param bank
	 * @return the corresponding {@link FilterBank}
	 */
	public static FilterBank getTagMemoryBank(Short bank) {
		if (bank != null) {
			for (FilterBank t : FilterBank.values()) {
				if (t.bank == bank)
					return t;
			}
		}
		return null;
	}
	
	/**
	 * Returns the corresponding {@link FilterBank}
	 * 
	 * @param meaning
	 * @return the corresponding {@link FilterBank}
	 */
	public static FilterBank getTagMemoryBank(CharSequence meaning) {
		if (meaning != null) {
			for (FilterBank t : FilterBank.values()) {
				if (t.meaning.equals(meaning))
					return t;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return meaning;
	}
}
