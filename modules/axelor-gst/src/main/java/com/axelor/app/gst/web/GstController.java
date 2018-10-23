package com.axelor.app.gst.web;

import java.util.List;

import com.axelor.app.gst.service.SequenceService;
import com.axelor.common.ObjectUtils;
import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.Party;
import com.axelor.gst.db.Product;
import com.axelor.gst.db.Sequence;
import com.axelor.gst.db.repo.SequenceRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GstController {
	@Inject
	SequenceService sequenceService;

	@Inject
	SequenceRepository sequenceRepository;

	public void checkModel(ActionRequest request, ActionResponse response) {
		Sequence sequence = request.getContext().asType(Sequence.class);
		List<Sequence> sequencelist = sequenceRepository.all().fetch();
		String flashMessage;
		for (Sequence checkSequence : sequencelist) {
			if (checkSequence.getModel().getName() != null) {
				if (checkSequence.getModel().getName().equals(sequence.getModel().getName())) {
					flashMessage = String.format("sequence already existis " + checkSequence.getModel().getName());
					response.setAlert(flashMessage);
				}
			} else {
				flashMessage = String.format("Please Enter Model ");
				response.setAlert(flashMessage);
			}
		}
		response.setValue("models", sequence.getModel());
	}

	public void generateSequence(ActionRequest request, ActionResponse response) {
		Sequence sequence = request.getContext().asType(Sequence.class);
		sequence = sequenceService.setSequence(sequence);
		response.setValue("nextNumber", sequence.getNextNumber());
	}

	public void referenceForParty(ActionRequest request, ActionResponse response) {
		Party party = request.getContext().asType(Party.class);
		List<Sequence> sequenceList = sequenceRepository.all().fetch();
		String flashMessage;
		if (party.getReference() == null)
			if (!ObjectUtils.isEmpty(sequenceList)) {
				try {
					for (Sequence partySequence : sequenceList) {
						String sequenceModel = "Party";
						String sequenceName = partySequence.getModel().getName().toString();
						if (sequenceName.equals(sequenceModel)) {
							party = sequenceService.setPartySequence(partySequence);
							response.setValue("reference", party.getReference());
							// response.setValue("nextNumber", partySequence.getNextNumber());
						}
					}
				} catch (Exception e) {

					flashMessage = String.format("No sequence foundy for Party , Kindly add a sequence");
					response.setFlash(flashMessage);
				}
			} else {
				flashMessage = String.format("No sequence found , Kindly add a sequence");
				response.setFlash(flashMessage);

			}
		return;
	}

	public void referenceForInvoice(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		List<Sequence> sequenceList = sequenceRepository.all().fetch();
		String flashMessage, sequenceModel = "Invoice";
		if (invoice.getReference() == null) {
			if (!ObjectUtils.isEmpty(sequenceList)) {
				try {
					for (Sequence invoiceSequence : sequenceList) {
						String sequenceName = invoiceSequence.getModel().getName().toString();
						if (sequenceName.equals(sequenceModel)) {
							invoice = sequenceService.setInvoiceSequence(invoiceSequence);
							response.setValue("reference", invoice.getReference());
							// response.setValue("nextNumber", invoiceSequence.getNextNumber());
						}
					}
				} catch (Exception e) {
					flashMessage = String.format("No sequence found for Invoice , Kindly add a sequence");
					response.setFlash(flashMessage);
				}

			} else {
				throw new java.lang.Error("No sequence foundy , Kindly add a sequence");
			}
		}
		return;
	}

	public void productCategory(ActionRequest request, ActionResponse response) {
		Product product = request.getContext().asType(Product.class);
		product.setGstrate(product.getCategory().getGstRate());
		response.setValues(product);
	}

	public void defaultReference(ActionRequest request, ActionResponse response) {
		InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
		invoiceLine.setItems("[" + invoiceLine.getProduct().getCode() + "]" + invoiceLine.getProduct().getName());
		invoiceLine.setGstRate(invoiceLine.getProduct().getGstrate());
		invoiceLine.setHsbn(invoiceLine.getProduct().getHsbn());
		invoiceLine.setPrice(invoiceLine.getProduct().getSaleprice());
		response.setValues(invoiceLine);
	}

}
