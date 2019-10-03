package com.runescape.runescape.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.model.Category;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.repository.CategoryRepository;
import com.runescape.runescape.repository.PlayerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryServiceTest {

	@Mock
	CategoryRepository categoryRepository;
	
	@InjectMocks
	CategoryService categoryService;
	
	@Test
	public void getAllCategoriesShouldReturnAllCategories() {
		
		List<Category> list = new ArrayList<Category>();
		Category categoryOne = new Category("TestOne");
		Category categoryTwo = new Category("TestTwo");
		Category categoryThree = new Category("TestThree");
         
        list.add(categoryOne);
        list.add(categoryTwo);
        list.add(categoryThree);
         
        when(categoryRepository.findAll()).thenReturn(list);
         
        List<Category> categoryList = categoryService.getAllCategories();
		
        assertEquals(3, categoryList.size());
        verify(categoryRepository, times(1)).findAll();
	}
	
}
