package com.example.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UploadFileTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("http://localhost:8080/UploadFile");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("sample.xml");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addBinaryBody("file", is, ContentType.DEFAULT_BINARY, "SampleFile");
		//
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		try {
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			InputStream in = httpEntity.getContent();
			String body = IOUtils.toString(in);
			System.out.println(body);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
