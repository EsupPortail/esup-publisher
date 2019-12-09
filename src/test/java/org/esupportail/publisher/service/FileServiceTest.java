package org.esupportail.publisher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.service.FileService;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(PowerMockRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
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
