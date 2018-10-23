package com.axelor.app.gst;

import com.axelor.app.AxelorModule;
import com.axelor.app.gst.repo.PartyRepo;
import com.axelor.app.gst.repo.ProductRepo;
import com.axelor.app.gst.service.InvoiceServiceImpl;
import com.axelor.app.gst.service.InvoiceService;
import com.axelor.app.gst.service.SequenceServiceImpl;
import com.axelor.app.gst.service.SequenceService;
import com.axelor.gst.db.repo.PartyRepository;
import com.axelor.gst.db.repo.ProductRepository;

public class GstModule extends AxelorModule {

	@Override
	protected void configure() {
		bind(SequenceService.class).to(SequenceServiceImpl.class);
		bind(InvoiceService.class).to(InvoiceServiceImpl.class);
		bind(PartyRepository.class).to(PartyRepo.class);
		bind(ProductRepository.class).to(ProductRepo.class);
	}

}
