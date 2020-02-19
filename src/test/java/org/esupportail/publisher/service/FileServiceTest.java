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
