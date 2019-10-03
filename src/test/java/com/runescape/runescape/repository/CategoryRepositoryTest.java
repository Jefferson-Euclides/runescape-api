package com.runescape.runescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.model.Category;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class CategoryRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	public void findByIdShouldReturnValidCategory() {
		Category category = new Category();
		category.setName("Test");

		entityManager.persist(category);
		entityManager.flush();

		Optional<Category> testCategory = categoryRepository.findById(category.getId());

		assertThat(testCategory.get().getName()).isEqualTo(testCategory.get().getName());
	}
	
	@Test
   public void findAllShouldReturnAllCategories() {
       Category firstCategory = new Category();
       firstCategory.setName("Yerevan");
       entityManager.persist(firstCategory);
       entityManager.flush();

       Category secondCategory = new Category();
       secondCategory.setName("Israel");
       entityManager.persist(secondCategory);
       entityManager.flush();

       Iterable<Category> arrivals = categoryRepository.findAll();
       
       List<Category> test = new ArrayList<>();
       arrivals.forEach(test::add);

       assertThat(test.size()).isEqualTo(8);
       assertThat(test.get(6)).isEqualTo(firstCategory);
       assertThat(test.get(7)).isEqualTo(secondCategory);
   }
}
