package org.esupportail.publisher.service;

import java.util.Arrays;
import java.util.List;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;

public class Utils {
	
	public final static ContextKey contextKeyValue(Long id, ContextType type) {
		ContextKey contextKey = new ContextKey();
		contextKey.setKeyId(id);
		contextKey.setKeyType(type);
		return contextKey;
	}

	public final static List<ContextKey> subContextKeys(ContextKey[] ContextKeys){
		return Arrays.asList(ContextKeys);
	}
}
