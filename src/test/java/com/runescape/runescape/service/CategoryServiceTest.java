package com.runescape.runescape.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.runescape.runescape.exceptions.CategoryNotFoundException;
import com.runescape.runescape.exceptions.OverallCategoryCannotBeDeletedException;
import com.runescape.runescape.model.Category;
import com.runescape.runescape.repository.CategoryRepository;

@AutoConfigureMockMvc
public class CategoryServiceTest extends BaseTest{

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
		Category categoryOne = generateRandomCategory();
		Category categoryTwo = generateRandomCategory();
		Category categoryThree = generateRandomCategory();
        
		list = Arrays.asList(categoryOne, categoryTwo, categoryThree);
         
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
		Optional<Category> optionalCategory = Optional.of(generateRandomCategory());
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		Mockito.<Optional<Category>> when(categoryRepository.findById(randomId)).thenReturn(optionalCategory);
		
		Category returnedCategory = categoryService.getCategoryById(randomId);
		
		assertEquals(optionalCategory.get().getName(), returnedCategory.getName());
	}
	
	@Test(expected = CategoryNotFoundException.class)
	public void getCategoryByIdNotExistsShouldReturnCategoryNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		categoryService.getCategoryById(randomId);
	}
	
	@Test
	public void saveCategoryShouldNoException() {
		Category category = generateRandomCategory();
		
		when(categoryRepository.save(category)).thenReturn(category);
		
		categoryService.insertCategory(category);
		
		//Verification
		ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
		verify(categoryRepository).save(captor.capture());
		
		assertEquals(category.getName(), captor.getValue().getName());
	}
	
	@Test(expected = NullPointerException.class)
	public void saveCategoryWithNullShouldNullPointerException() {
		categoryService.insertCategory(null);
	}
	
	@Test
	public void updateCategoryShouldChangeName() {
		Category category = generateRandomCategory();
		Integer randomId = getRandomIntegerBetweenRange(1, 10);

		when(categoryRepository.findById(randomId)).thenReturn(Optional.of(category));
		
		categoryService.updateCategory(category, randomId);
		
		verify(categoryRepository, times(1)).findById(randomId);
	}
	
	@Test(expected = CategoryNotFoundException.class)
	public void updateCategoryInvalidIdShouldCategoryNotFoundException() {
		Category category = generateRandomCategory();
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		categoryService.updateCategory(category, randomId);

		verify(categoryRepository, times(1)).findById(randomId);
	}
	
	@Test
	public void deteleCategoryShouldNoException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		when(categoryRepository.existsById(randomId)).thenReturn(true);
		
		categoryService.deleteCategory(randomId);
	
		verify(categoryRepository, times(1)).deleteById(randomId);
	}
	
	@Test(expected = OverallCategoryCannotBeDeletedException.class)
	public void deteleCategoryOverallShouldOverallCategoryCannotBeDeletedException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		when(categoryRepository.existsById(randomId)).thenReturn(true);
		
		categoryService.deleteCategory(randomId);
	}
	
	@Test(expected = CategoryNotFoundException.class)
	public void deleteInvalidCategoryShouldCategoryNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		categoryService.deleteCategory(randomId);
		
		verify(categoryRepository, times(1)).existsById(randomId);
	}
	
}
