package com.runescape.runescape.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.exceptions.PlayerNotFoundException;
import com.runescape.runescape.model.Category;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.model.Score;
import com.runescape.runescape.repository.CategoryRepository;
import com.runescape.runescape.repository.PlayerRepository;
import com.runescape.runescape.repository.ScoreRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ScoreServiceTest {
	
	@Mock
	ScoreRepository scoreRepository;
	
	@Mock
	CategoryRepository categoryRepository;
	
	@Mock
	PlayerRepository playerRepository;
	
	@InjectMocks
	ScoreService scoreService;
	
	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void getAllScoresByPlayerIdShouldReturnScoresList() {
		List<Score> list = new ArrayList<Score>();
		Score scoreOne = new Score(new Category("CategoryOne"), new Player("PlayerOne"), 10, 100000L);
		Score scoreTwo = new Score(new Category("CategoryTwo"), new Player("PlayerTwo"), 20, 200000L);

		list = Arrays.asList(scoreOne, scoreTwo);
		
		when(playerRepository.existsById(1)).thenReturn(true);
		when(scoreRepository.findByPlayerId(1)).thenReturn(list);
		
		List<Score> returnedList = scoreService.getAllScoresByPlayerId(1);
		
		assertEquals(2, returnedList.size());
		verify(scoreRepository, times(1)).findByPlayerId(1);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void getAllScoresByInvalidPlayerIdShouldPlayerNotFoundException() {
		scoreService.getAllScoresByPlayerId(1);
		
		verify(playerRepository, times(1)).existsById(1);
	}
	
	@Test
	public void getScoresByCategoryIdShouldReturnTop10ScoresList() {
		List<Score> listScores = prepareScoreObjects();
		
		when(categoryRepository.existsById(1)).thenReturn(true);
		when(scoreRepository.findTop10ByCategoryIdOrderByLevelDescXpDesc(1)).thenReturn(listScores);
		
		List<Score> listReturnedScores = scoreService.getScoresByCategoryId(1);
		
		assertEquals(10, listReturnedScores.size());
		verify(scoreRepository, times(1)).findTop10ByCategoryIdOrderByLevelDescXpDesc(1);
	}

	public List<Score> prepareScoreObjects() {
		List<Score> list = new ArrayList<Score>();
		
		Score scoreOne = new Score(new Category("CategoryOne"), new Player("PlayerOne"), 10, 100000L);
		Score scoreTwo = new Score(new Category("CategoryTwo"), new Player("PlayerTwo"), 20, 200000L);
		Score scoreThree = new Score(new Category("CategoryThree"), new Player("PlayerThree"), 30, 100000L);
		Score scoreFour = new Score(new Category("CategoryFour"), new Player("PlayerFour"), 40, 200000L);
		Score scoreFive = new Score(new Category("CategoryFive"), new Player("PlayerFive"), 50, 100000L);
		Score scoreSix = new Score(new Category("CategorySix"), new Player("PlayerSix"), 60, 200000L);
		Score scoreSeven = new Score(new Category("CategorySeven"), new Player("PlayerSeven"), 70, 100000L);
		Score scoreEight = new Score(new Category("CategoryEight"), new Player("PlayerEight"), 10, 200000L);
		Score scoreNine = new Score(new Category("CategoryNine"), new Player("PlayerNine"), 10, 100000L);
		Score scoreTen = new Score(new Category("CategoryTen"), new Player("PlayerTen"), 20, 200000L);
		

		return list = Arrays.asList(scoreOne, scoreTwo, scoreThree, scoreFour, scoreFive, 
				scoreSix, scoreSeven, scoreEight, scoreNine, scoreTen);
	}

}
