package org.soft.pc.core.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PCCoreWebServiceTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void whenGetGreeting() throws Exception {
		String result = mockMvc.perform(get("/greeting").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.content").value("First SpringBoot App!"))
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
	}
	
	@Test
	public void whenGetAllComputers() throws UnsupportedEncodingException, Exception {
		String result = mockMvc.perform(get("/computer/all").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
	}
	
	@Test
	public void whenDeleteById() throws Exception {
		String result = mockMvc.perform(get("/computer/18/delete").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
	}
	
	@Test
	public void whenAddComputer() throws Exception {
		String content = "{" + "\"trademark\":\"xxPC\"," + "\"price\":\"999.99\"," + "\"pic\":\"a.jpg\"" + "}";
		String result = mockMvc.perform(post("/computer/add").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.msg").value("Created Successfully"))
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
	}
	
	@Test
	public void whenAddComputerFailedWithNoTradeMark() throws Exception {
		String content = "{" + "\"trademark\":\"\"," + "\"price\":\"999.99\"," + "\"pic\":\"a.jpg\"" + "}";
		String result = mockMvc.perform(post("/computer/add").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)).andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
	}
	
	@Test
	public void whenAddComputerFailedAddingSameTradeMark() throws Exception {
		String content = "{" + "\"trademark\":\"xxPC\"," + "\"price\":\"999.99\"," + "\"pic\":\"a.jpg\"" + "}";
		String result = mockMvc.perform(post("/computer/add").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)).andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.msg").value("PC Already Exist"))
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
	}
	
	@Test
	public void whenTestComputerQueryById() throws Exception{
		String result = mockMvc.perform(get("/computer/48/query").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(48))
				.andExpect(jsonPath("$.trademark").value("mi PC"))
				.andExpect(jsonPath("$.price").value(1111.00))
				.andReturn().getResponse()
				.getContentAsString();
		
		System.out.println(result);
	}
	
	@Test
	public void whenUpdateComputer() throws Exception{
		String content = "{" + "\"id\":56," + "\"trademark\":\"didi\"," + "\"price\":\"111.99\"" + "}";
		String result = mockMvc.perform(post("/computer/update").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)).andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("Update Successfully"))
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
		
	}

}
