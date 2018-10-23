package com.axelor.app.gst.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.axelor.app.AppSettings;
import com.axelor.app.gst.service.InvoiceService;
import com.axelor.common.ObjectUtils;
import com.axelor.gst.db.Invoice;
import com.axelor.gst.db.InvoiceLine;
import com.axelor.gst.db.Product;
import com.axelor.gst.db.repo.InvoiceRepository;
import com.axelor.gst.db.repo.ProductRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class InvoiceGstController {
	@Inject
	InvoiceService invoiceService;

	@Inject
	InvoiceRepository invoiceRepository;

	@Inject
	ProductRepository productRepository;

	public void calculateInvoiceLine(ActionRequest request, ActionResponse response) {
		InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
		Invoice invoice = request.getContext().getParent().asType(Invoice.class);
		String flashMessage;
		try {
			if (invoice.getCompany().getAddresses() == null || invoice.getInvoiceAddress() == null) {
				flashMessage = String.format("Company or invoice Address not found");
				response.setError(flashMessage);
				// response.setNotify(flashMessage);

			} else {

				if (invoice.getCompany().getAddresses().getState() == null) {
					flashMessage = String.format(" company State not found");
					response.setPending(flashMessage);
				} else if (invoice.getInvoiceAddress().getState() == null) {
					flashMessage = String.format(" Invoice State not found");
					response.setPending(flashMessage);

				} else {
					invoiceLine = invoiceService.calculateInvoiceLine(invoiceLine, invoice);
					response.setValue("netAmmount", invoiceLine.getNetAmmount());
					response.setValue("igst", invoiceLine.getIgst());
					response.setValue("cgst", invoiceLine.getCgst());
					response.setValue("sgst", invoiceLine.getSgst());
					response.setValue("grossAmount", invoiceLine.getGrossAmount());
				}
			}
		} catch (Exception e) {
			flashMessage = String.format("Enter Company  and  Party Address");
			response.setError(flashMessage);

		}
	}

	public void calculateInvoice(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		try {
			invoice = invoiceService.calculateInvoice(invoice);
			response.setValue("netAmmount", invoice.getNetAmmount());
			response.setValue("netCGST", invoice.getNetCGST());
			response.setValue("netSGST", invoice.getNetSGST());
			response.setValue("netIGST", invoice.getNetIGST());
			response.setValue("grossAmmount", invoice.getGrossAmmount());
		} catch (Exception e) {
		}
	}

	public void invoiceCreate(ActionRequest request, ActionResponse response) {
		@SuppressWarnings("unchecked")
		List<Integer> idList = (List<Integer>) request.getContext().get("_ids");
		String ids = "";
		if (ObjectUtils.notEmpty(idList)) {
			List<InvoiceLine> invoicelist = new ArrayList<InvoiceLine>();
			for (Integer id : idList) {
				ids = ids + id.toString() + ",";
				List<Product> productlist = productRepository.all().filter("self.id= ?", id).fetch();
				for (Product product2 : productlist) {
					InvoiceLine invoiceLineFromProduct = new InvoiceLine();
					invoiceLineFromProduct.setProduct(product2);
					invoiceLineFromProduct.setItems(product2.getName() + " " + product2.getCode());
					invoiceLineFromProduct.setGstRate(product2.getGstrate());
					invoiceLineFromProduct.setHsbn(product2.getHsbn());
					invoiceLineFromProduct.setPrice(product2.getSaleprice());
					invoicelist.add(invoiceLineFromProduct);
				}
			}
			ids = ids.substring(0, ids.length() - 1);
			request.getContext().put("ids", ids);
			response.setView(ActionView.define("form").model("com.axelor.gst.db.Invoice").add("form", "form-Invoice")
					.context("_invoiceItems", invoicelist).map());
			System.out.println(invoicelist);
		} else {
			response.setView(
					ActionView.define("form").model("com.axelor.gst.db.Invoice").add("form", "form-Invoice").map());
		}
	}

	public void generateReport(ActionRequest request, ActionResponse response) {
		try {
			@SuppressWarnings("unchecked")
			List<Integer> idList = (List<Integer>) request.getContext().get("_ids");
			String ids = "";
			System.out.println(idList.toString());
			for (Integer id : idList) {
				ids = ids + id.toString() + ",";
			}
			ids = ids.substring(0, ids.length() - 1);
			System.out.println(ids);
			request.getContext().put("ids", ids);
		} catch (Exception e) {
			response.setAlert("please select Invoice");
		}
	}

	public void path(ActionRequest request, ActionResponse response) {
		String imagePath = AppSettings.get().getPath("file.upload.dir", "");
		imagePath = imagePath + File.separator;
		request.getContext().put("img", imagePath);
		// System.out.println(imagePath);
		// return imagePath;
	}

}
