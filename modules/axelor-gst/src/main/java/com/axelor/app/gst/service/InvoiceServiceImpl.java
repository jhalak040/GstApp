package com.axelor.app.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.common.ObjectUtils;
import com.axelor.gst.db.Address;
import com.axelor.gst.db.Contact;
import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.Party;
import com.axelor.gst.db.repo.AddressRepository;
import com.axelor.gst.db.repo.ContactRepository;
import com.axelor.gst.db.repo.PartyRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class InvoiceServiceImpl implements InvoiceService {
	@Inject
	PartyRepository partyRepository;

	/* calcualting net ammount for Invoiceline */
	@Override
	public InvoiceLine calculateInvoiceLine(InvoiceLine invoiceLine, Invoice invoice) {
		BigDecimal calculate = BigDecimal.ZERO;
		calculate = invoiceLine.getPrice().multiply((new BigDecimal(invoiceLine.getQty())));
		invoiceLine.setNetAmmount(calculate);
		if (invoice.getCompany().getAddresses().getState().getName()
				.equals(invoice.getInvoiceAddress().getState().getName())) {
			calculate = invoiceLine.getNetAmmount().multiply(invoiceLine.getGstRate().divide(BigDecimal.valueOf(2)));
			invoiceLine.setCgst(calculate);
			invoiceLine.setSgst(calculate);
		} else {
			calculate = (invoiceLine.getNetAmmount().multiply(invoiceLine.getGstRate()));
			invoiceLine.setIgst(calculate);
		}
		calculate = (invoiceLine.getNetAmmount().add(calculate));
		invoiceLine.setGrossAmount(calculate);
		return invoiceLine;
	}

	/* calculating Invoice */
	@Override
	public Invoice calculateInvoice(Invoice invoice) {
		BigDecimal netAmmount = BigDecimal.ZERO;
		BigDecimal netIgst = BigDecimal.ZERO;
		BigDecimal netRate = BigDecimal.ZERO;
		BigDecimal grossAmmount = BigDecimal.ZERO;
		if (!ObjectUtils.isEmpty(invoice.getInvoiceItems())) {
			for (InvoiceLine item : invoice.getInvoiceItems()) {
				netAmmount = netAmmount.add(item.getNetAmmount());
				invoice.setNetAmmount(netAmmount);
				netRate = netRate.add(item.getCgst());
				invoice.setNetSGST(netRate);
				invoice.setNetCGST(netRate);
				netIgst = netIgst.add(item.getIgst());
				invoice.setNetIGST(netIgst);
				grossAmmount = grossAmmount.add(item.getGrossAmount());
				invoice.setGrossAmmount(grossAmmount);
			}
		}
		return invoice;
	}

	@Override
	public Contact setValues(Party party) {
		List<Contact> contactlist = party.getContacts();
		Contact contact = new Contact();
		for (Contact partyContact : contactlist) {
			String contactType = partyContact.getType();
			if (contactType.equals(ContactRepository.CONTACT_TYPE_PRIMARY)) {
				contact.setName(partyContact.getName());
				contact.setType(partyContact.getType());
				contact.setPrimaryEmail(partyContact.getPrimaryEmail());
				contact.setPrimaryPhone(partyContact.getPrimaryPhone());
				break;
			}
		}
		return contact;
	}

	@Override
	public Address setInvoiceAddress(Party party) {
		List<Address> addressList = party.getAddresses();
		Address invoiceAddress = new Address();
		for (Address partyAddress : addressList) {
			String address = partyAddress.getType();
			if (address.equals(AddressRepository.ADDRESS_TYPE_INVOICE)) {
				invoiceAddress.setType(partyAddress.getType());
				invoiceAddress.setLine1(partyAddress.getLine1());
				invoiceAddress.setLine2(partyAddress.getLine2());
				invoiceAddress.setCity1(partyAddress.getCity1());
				invoiceAddress.setState(partyAddress.getState());
				invoiceAddress.setCountry(partyAddress.getCountry());
				invoiceAddress.setPinCode(partyAddress.getPinCode());
				break;
			} else if (address.equals(AddressRepository.ADDRESS_TYPE_DEFAULT)) {
				invoiceAddress.setType(partyAddress.getType());
				invoiceAddress.setLine1(partyAddress.getLine1());
				invoiceAddress.setLine2(partyAddress.getLine2());
				invoiceAddress.setCity1(partyAddress.getCity1());
				invoiceAddress.setState(partyAddress.getState());
				invoiceAddress.setCountry(partyAddress.getCountry());
				invoiceAddress.setPinCode(partyAddress.getPinCode());
			}
		}
		return invoiceAddress;
	}

	@Override
	public Address setShippingAddress(Party party) {

		List<Address> addressList = party.getAddresses();
		Address shippingAddress = new Address();
		for (Address partyAddress : addressList) {
			String address = partyAddress.getType();
			if (address.equals(AddressRepository.ADDRESS_TYPE_SHIPPING)) {
				shippingAddress.setType(partyAddress.getType());
				shippingAddress.setLine1(partyAddress.getLine1());
				shippingAddress.setLine2(partyAddress.getLine2());
				shippingAddress.setCity1(partyAddress.getCity1());
				shippingAddress.setState(partyAddress.getState());
				shippingAddress.setCountry(partyAddress.getCountry());
				shippingAddress.setPinCode(partyAddress.getPinCode());
				break;
			} else if (address.equals(AddressRepository.ADDRESS_TYPE_DEFAULT)) {
				shippingAddress.setType(partyAddress.getType());
				shippingAddress.setLine1(partyAddress.getLine1());
				shippingAddress.setLine2(partyAddress.getLine2());
				shippingAddress.setCity1(partyAddress.getCity1());
				shippingAddress.setState(partyAddress.getState());
				shippingAddress.setCountry(partyAddress.getCountry());
				shippingAddress.setPinCode(partyAddress.getPinCode());
				break;
			}
		}
		return shippingAddress;
	}

	@Transactional
	@Override
	public Address partyShippingAddress(Address shippingAddress, Party party) {
		Party partyList = partyRepository.all().filter("self.id= ?", party.getId()).fetchOne();
		List<Address> addressList = partyList.getAddresses();
		for (Address address : addressList) {
			if ((shippingAddress.getType()).equals(address.getType())) {
				address.setType(shippingAddress.getType());
				address.setLine1(shippingAddress.getLine1());
				address.setLine2(shippingAddress.getLine2());
				address.setCity1(shippingAddress.getCity1());
				address.setState(shippingAddress.getState());
				address.setPinCode(shippingAddress.getPinCode());
				address.setCountry(shippingAddress.getCountry());
				break;
			}
		}
		partyRepository.save(partyList);
		return shippingAddress;
	}

	@Transactional
	@Override
	public Address partyInvoiceAddress(Address invoiceAddress, Party party) {
		Party partyList = partyRepository.all().filter("self.id= ?", party.getId()).fetchOne();
		List<Address> addressList = partyList.getAddresses();
		for (Address address : addressList) {
			if ((invoiceAddress.getType()).equals(address.getType())) {
				address.setType(invoiceAddress.getType());
				address.setLine1(invoiceAddress.getLine1());
				address.setLine2(invoiceAddress.getLine2());
				address.setCity1(invoiceAddress.getCity1());
				address.setState(invoiceAddress.getState());
				address.setPinCode(invoiceAddress.getPinCode());
				address.setCountry(invoiceAddress.getCountry());
				break;
			}
		}
		partyRepository.save(partyList);
		return invoiceAddress;
	}
}
