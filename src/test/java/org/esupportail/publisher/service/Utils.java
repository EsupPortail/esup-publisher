/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
