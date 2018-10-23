package com.axelor.app.gst.repo;

import java.util.Map;

import com.axelor.gst.db.Party;
import com.axelor.gst.db.repo.PartyRepository;

public class PartyRepo extends PartyRepository {
	@Override
	public Map<String, Object> populate(Map<String, Object> json, Map<String, Object> context) {
		if (!context.containsKey("json-enhance")) {
			return json;
		}
		try {
			Long id = (Long) json.get("id");
			Party party = find(id);
			json.put("addresses", party.getAddresses().get(0));
			json.put("contacts", party.getContacts().get(0));

		} catch (Exception e) {
		}

		return json;
	}
}
