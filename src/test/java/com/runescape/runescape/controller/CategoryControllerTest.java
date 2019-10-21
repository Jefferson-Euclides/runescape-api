package com.runescape.runescape.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.runescape.runescape.model.Category;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.service.BaseTest;
import com.runescape.runescape.service.CategoryService;
import com.runescape.runescape.util.Utils;

@AutoConfigureMockMvc
public class CategoryControllerTest extends BaseTest{
	
	@MockBean
	private CategoryService categoryService;
	
	@Autowired
    private MockMvc mvc;
	
	@Test
	public void getAllCategoriesShouldReturnAllCategories() throws Exception {
		
		List<Category> allCategories = Arrays.asList(new Category(generateRandomString(10)));
		
		given(categoryService.getAllCategories()).willReturn(allCategories);
		mvc.perform(get("/categories")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is(allCategories.get(0).getName())));
	}
	
	@Test
	public void insertCategoryShouldReturnCreatedCategory() throws Exception {
		Player player = new Player(generateRandomString(10));
		
		mvc.perform(post("/categories")
				.content(Utils.asJsonString(player))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
	}
	
	@Test
	public void updateCategoryShouldUpdateCategoryAndReturnOk() throws Exception {
		Category category = new Category(generateRandomString(10));
		
		mvc.perform(put("/categories/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Utils.asJsonString(category)))
				.andExpect(status().isOk());
		
	}
}
