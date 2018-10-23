package com.axelor.app.gst.service;

import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.Party;
import com.axelor.gst.db.Sequence;
import com.axelor.gst.db.repo.SequenceRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class SequenceServiceImpl implements SequenceService {
	@Inject
	SequenceRepository sequenceRepository;

	@Override
	public Sequence setSequence(Sequence sequence) {
		String num = "";
		int paddingNummber = sequence.getPadding() ;
		for (int i = 0; i < paddingNummber; i++) {
			num += String.valueOf(0);
		}
		if (sequence.getSuffix() == null) {
			String nextNumber = sequence.getPrefix() + num;
			sequence.setNextNumber(nextNumber);
		} else {
			String nextNumber = sequence.getPrefix() + num + sequence.getSuffix().toString();
			sequence.setNextNumber(nextNumber);
		}
		return sequence;
	}

	@Transactional(rollbackOn = { Exception.class })
	@Override
	public Party setPartySequence(Sequence partysequence) {
		Party party = new Party();
		int count, sum;
		String num = "";
		String nextNumber = partysequence.getNextNumber();
		String formatedNumber = nextNumber.substring(2, nextNumber.length() - 2);
		for (int i = 0; i < partysequence.getPadding() - 1; i++) {
			num += String.valueOf(0);
		}
		sum = Integer.parseInt(formatedNumber);
		count = sum + 1;
		String Padded = Integer.toString(count);
		String reference = partysequence.getPrefix() + num + Padded + partysequence.getSuffix().toString();
		party.setReference(reference);
		partysequence.setNextNumber(reference);
		sequenceRepository.save(partysequence);
		return party;

	}

	@Transactional(rollbackOn = { Exception.class })
	@Override
	public Invoice setInvoiceSequence(Sequence invoiceSequence) {
		Invoice invoice = new Invoice();
		int count, sum;
		String num = "";
		String nextNumber = invoiceSequence.getNextNumber();
		String formatedNumber = nextNumber.substring(2, nextNumber.length() - 2);
		for (int i = 0; i < invoiceSequence.getPadding() - 1; i++) {
			num += String.valueOf(0).length();
		}
		sum = Integer.parseInt(formatedNumber);
		count = sum + 1;
		String Padded = Integer.toString(count);
		String reference = invoiceSequence.getPrefix() + num + Padded + invoiceSequence.getSuffix().toString();
		invoice.setReference(reference);
		invoiceSequence.setNextNumber(reference);
		sequenceRepository.save(invoiceSequence);
		return invoice;

	}
}
