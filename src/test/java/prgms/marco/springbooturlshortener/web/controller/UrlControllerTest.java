package prgms.marco.springbooturlshortener.web.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import prgms.marco.springbooturlshortener.dto.CreateShortUrlReq;
import prgms.marco.springbooturlshortener.service.UrlService;

@WebMvcTest(controllers = {UrlController.class})
class UrlControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private UrlService urlService;

	@Test
	@DisplayName("Short URL 생성 - 성공")
	void testCreateShortenUrlSuccess() throws Exception {
		String shortUrl = "shortUrl";
		String originUrl = "http://www.naver.com";
		CreateShortUrlReq createShortUrlReq = new CreateShortUrlReq(originUrl);

		given(urlService.createShortUrl(createShortUrlReq.getOriginUrl())).willReturn(shortUrl);

		mockMvc.perform(post("/api/v1/urls")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createShortUrlReq)))
			.andExpect(status().isCreated())
			.andExpect(header().string("location", "/api/v1/urls/" + shortUrl))
			.andExpect(jsonPath("$.shortUrl").value(shortUrl));
	}

	@Test
	@DisplayName("Short URL 생성 - 실패")
	void testCreateShortenUrlFail() throws Exception {
		//given
		CreateShortUrlReq createShortUrlReq = new CreateShortUrlReq("invalid url format");

		//when, then
		mockMvc.perform(post("/api/v1/urls")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createShortUrlReq)))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("Short Url을 Origin Url 변환")
	void testConvertShortUrlToOrigin() throws Exception {
		// given
		String shortUrl = "shortUrl";
		String originUrl = "http://www.naver.com";
		given(urlService.findOriginUrlByShortUrl(shortUrl)).willReturn(originUrl);

		//when, then
		mockMvc.perform(get("/api/v1/urls/" + shortUrl))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string("location", originUrl));
	}
}
