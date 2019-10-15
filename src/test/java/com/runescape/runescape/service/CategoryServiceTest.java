package com.runescape.runescape.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.exceptions.CategoryNotFoundException;
import com.runescape.runescape.exceptions.OverallCategoryCannotBeDeletedException;
import com.runescape.runescape.model.Category;
import com.runescape.runescape.repository.CategoryRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryServiceTest {

	@Mock
	CategoryRepository categoryRepository;
	
	@InjectMocks
	CategoryService categoryService;
	
	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	
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
	
	@Test
	public void getAllCategoriesEmptyListShouldReturnEmptyList() {
		
		List<Category> list = Collections.emptyList();
         
        when(categoryRepository.findAll()).thenReturn(list);
         
        List<Category> categoryList = categoryService.getAllCategories();
		
        assertEquals(0, categoryList.size());
        verify(categoryRepository, times(1)).findAll();
	}
	
	@Test
	public void getCategoryByIdShouldReturnCategory() {

		Optional<Category> optionalCategory = Optional.of(new Category("Overall"));
		
		Mockito.<Optional<Category>> when(categoryRepository.findById(1)).thenReturn(optionalCategory);
		
		Category returnedCategory = categoryService.getCategoryById(1);
		
		assertEquals("Overall", returnedCategory.getName());
	}
	
	@Test(expected = CategoryNotFoundException.class)
	public void getCategoryByIdNotExistsShouldReturnCategoryNotFoundException() {
		
		categoryService.getCategoryById(1);
		
	}
	
	@Test
	public void saveCategoryShouldNoException() {

		Category category = new Category("Test");
		
		categoryService.insertCategory(category);
		
		//Verification
		ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
		verify(categoryRepository).save(captor.capture());
		
		assertEquals("Test", captor.getValue().getName());
	}
	
	@Test(expected = NullPointerException.class)
	public void saveCategoryWithNullShouldNullPointerException() {

		categoryService.insertCategory(null);
		
	}
	
	@Test
	public void updateCategoryShouldChangeName() {

		Category category = new Category("Update Test");

		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
		
		categoryService.updateCategory(category, 1);
		
		verify(categoryRepository, times(1)).findById(1);

	}
	
	@Test(expected = NullPointerException.class)
	public void updateNullCategoryShouldNullPointerException() {

		Category category = new Category(null);
		
		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
		
		categoryService.updateCategory(category, 1);

		verify(categoryRepository, times(1)).findById(1);
	}
	
	@Test(expected = CategoryNotFoundException.class)
	public void updateCategoryInvalidIdShouldCategoryNotFoundException() {

		Category category = new Category("Test");

		categoryService.updateCategory(category, 1);

		verify(categoryRepository, times(1)).findById(1);
	}
	
	@Test
	public void deteleCategoryShouldNoException() {
		Category category = new Category("Test");
		
		when(categoryRepository.existsById(2)).thenReturn(true);
		
		categoryService.deleteCategory(2);
	
		verify(categoryRepository, times(1)).delete(category);
	}
	
	@Test(expected = OverallCategoryCannotBeDeletedException.class)
	public void deteleCategoryOverallShouldOverallCategoryCannotBeDeletedException() {
		Category category = new Category("Overall");
		
		when(categoryRepository.existsById(1)).thenReturn(true);
		
		categoryService.deleteCategory(1);
	
		verify(categoryRepository, times(1)).delete(category);
	}
	
	@Test(expected = CategoryNotFoundException.class)
	public void deleteInvalidCategoryShouldCategoryNotFoundException() {
	
		categoryService.deleteCategory(999);
		
		verify(categoryRepository, times(1)).existsById(999);
	}
	
}
