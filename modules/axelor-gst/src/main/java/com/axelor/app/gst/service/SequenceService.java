package com.axelor.app.gst.service;

import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.Party;
import com.axelor.gst.db.Sequence;

public interface SequenceService {
	
	//public Sequence checkModel(Sequence sequence);
	
	public Sequence setSequence(Sequence sequence);
	
	public Party setPartySequence(Sequence partysequence);

	public Invoice setInvoiceSequence(Sequence invoiceSequence);

}
