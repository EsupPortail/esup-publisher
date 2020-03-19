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

import static org.junit.Assert.assertFalse;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(PowerMockRunner.class)
@SpringBootTest(classes = Application.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@WebAppConfiguration
public class FileServiceTest {
	
	@Mock
	private FileUploadHelper publicFileUploadHelper;

	@InjectMocks
	private FileService fileService;
	
	@Test
	public void deleteInternalResource_UrlPathIsNull_ReturnFalse() {
		String urlPath = null;
		final boolean result = fileService.deleteInternalResource(urlPath);
		assertFalse(result);
	}
	
	@Test
	public void deleteInternalResource_UrlPathNotStartWithHtpp_ReturnFalse() {
		String urlPath = "http://test.com";
		final boolean result = fileService.deleteInternalResource(urlPath);
		assertFalse(result);
	}
	
	@Test
	public void deleteInternalResource_UrlPathNotStartWithHtpps_ReturnFalse() {
		String urlPath = "https://test.com";
		final boolean result = fileService.deleteInternalResource(urlPath);
		assertFalse(result);
	}
}
