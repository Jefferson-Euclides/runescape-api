package com.runescape.runescape.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.runescape.runescape.model.Category;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.service.CategoryService;
import com.runescape.runescape.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
	
	@MockBean
	private CategoryService categoryService;
	
	@Autowired
    private MockMvc mvc;
	
	@Test
	public void getAllCategoriesShouldReturnAllCategories() throws Exception {
		
		Category test = new Category("Test");
		List<Category> allCategories = new ArrayList<>();
		
		allCategories.add(test);
		
		given(categoryService.getAllCategories()).willReturn(allCategories);
		mvc.perform(get("/categories")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is(test.getName())));
	}
	
	@Test
	public void insertCategoryShouldReturnCreatedCategory() throws Exception {
		Player player = new Player();
		player.setName("Test");
		
		mvc.perform(post("/categories")
				.content(Utils.asJsonString(player))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
	}
	
	@Test
	public void updateCategoryShouldUpdateCategoryAndReturnOk() throws Exception {
		Category category = new Category();
		category.setName("Test");
		
		mvc.perform(put("/categories/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Utils.asJsonString(category)))
				.andExpect(status().isOk());
		
	}
}
