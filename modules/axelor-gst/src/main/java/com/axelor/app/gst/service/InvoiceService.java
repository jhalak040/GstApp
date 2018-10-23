package com.axelor.app.gst.service;

import com.axelor.gst.db.Address;
import com.axelor.gst.db.Contact;
import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.Party;

public interface InvoiceService {
	public InvoiceLine calculateInvoiceLine(InvoiceLine invoiceLine, Invoice invoice);

	public Invoice calculateInvoice(Invoice invoice );

	public Contact setValues(Party party);

	public Address setInvoiceAddress(Party party);

	public Address setShippingAddress(Party party);

	public Address partyShippingAddress(Address shippingAddress, Party party );

	public Address partyInvoiceAddress(Address invoiceAddress, Party party);

//	public Address modifiedInvoiceAddress(Address shippingAddress, Address invoiceAddress);

}
