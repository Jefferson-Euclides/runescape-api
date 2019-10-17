package com.runescape.runescape.service;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.exceptions.CategoryNotFoundException;
import com.runescape.runescape.exceptions.PlayerNotFoundException;
import com.runescape.runescape.exceptions.ScoreExistsException;
import com.runescape.runescape.exceptions.ScoreNotFoundException;
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
	
	@Spy @InjectMocks
	ScoreService spy;
	
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
	
	@Test(expected = CategoryNotFoundException.class)
	public void getScoresByInvalidCategoryIdShouldCategoryNotFoundException() {
		scoreService.getScoresByCategoryId(1);
		
		verify(scoreRepository, times(1)).existsById(1);
	}
	
	@Test
	public void insertPlayerScoreShouldNoException() {
		Player player = new Player("PlayerOne"); 
		Score score = new Score(new Category("CategoryOne"), player, 10, 100000L);
		
		when(playerRepository.existsById(1)).thenReturn(true);
		when(playerRepository.findById(1)).thenReturn(Optional.of(player));
		when(scoreRepository.existsById(1)).thenReturn(true);
		when(scoreRepository.save(score)).thenReturn(score);
		
		doNothing().when(spy).updatePlayerOverallScore(1);
		spy.insertPlayerScore(score, 1); 

		//Verification
		ArgumentCaptor<Score> captor = ArgumentCaptor.forClass(Score.class);
		verify(scoreRepository).save(captor.capture());
		
		assertEquals("CategoryOne", captor.getValue().getCategory().getName());
		assertEquals("PlayerOne", captor.getValue().getPlayer().getName());
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void insertPlayerScoreInvalidPlayerIdShouldPlayerNotFoundException() {
		Player player = new Player("PlayerOne"); 
		Score score = new Score(new Category("CategoryOne"), player, 10, 100000L);
		
		scoreService.insertPlayerScore(score, null);
		
		verify(playerRepository, times(1)).existsById(1);
	}
	
	@Test(expected = ScoreExistsException.class)
	public void insertPlayerScoreAlreadyExistsShouldScoreExistsException() {
		Player player = new Player("PlayerOne"); 
		Score score = new Score(new Category("CategoryOne"), player, 10, 100000L);
		
		when(playerRepository.existsById(1)).thenReturn(true);
		when(scoreRepository.existsByPlayerIdAndCategoryName(1, score.getCategory().getName())).thenReturn(true);
		scoreService.insertPlayerScore(score, 1);
		
		verify(scoreRepository, times(1)).existsByPlayerIdAndCategoryName(1, score.getCategory().getName());
	}
	
	@Test
	public void updatePlayerScoreShouldNoException() {
		Player player = new Player("PlayerOne"); 
		Score score = new Score(new Category("CategoryOne"), player, 10, 100000L);
		
		when(playerRepository.existsById(1)).thenReturn(true);
		when(scoreRepository.existsById(1)).thenReturn(true);
		when(playerRepository.findById(1)).thenReturn(Optional.of(player));
		when(scoreRepository.findById(1)).thenReturn(Optional.of(score));
		when(scoreRepository.save(score)).thenReturn(score);
		
		doNothing().when(spy).updatePlayerOverallScore(1);
		spy.updatePlayerScore(score, 1, 1); 

		verify(scoreRepository, times(1)).save(score);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void updatePlayerScoreInvalidPlayerIdShouldPlayerNotFoundException() {
		Player player = new Player("PlayerOne"); 
		Score score = new Score(new Category("CategoryOne"), player, 10, 100000L);
		
		when(scoreRepository.existsById(1)).thenReturn(true);
		
		scoreService.updatePlayerScore(score, 1, 1);
	}
	
	@Test(expected = ScoreNotFoundException.class)
	public void updatePlayerScoreInvalidScoreIdShouldScoreNotFoundException() {
		Player player = new Player("PlayerOne"); 
		Score score = new Score(new Category("CategoryOne"), player, 10, 100000L);
		
		scoreService.updatePlayerScore(score, 1, 1);
	}
	
	@Test
	public void detelePlayerScoreShouldNoException() {
		when(scoreRepository.existsById(1)).thenReturn(true);
		when(playerRepository.existsById(1)).thenReturn(true);
		
		doNothing().when(spy).updatePlayerOverallScore(1);
		spy.deletePlayerScore(1, 1);
	
		verify(spy, times(1)).deletePlayerScore(1, 1);
	}
	
	@Test(expected = ScoreNotFoundException.class)
	public void detelePlayerScoreInvalidScoreIdShouldScoreNotFoundException() {
		scoreService.deletePlayerScore(null, 1);
		
		verify(scoreRepository, times(1)).existsById(1);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void detelePlayerScoreInvalidPlayerIdShouldPlayerNotFoundException() {
		when(scoreRepository.existsById(1)).thenReturn(true);
		
		scoreService.deletePlayerScore(1, null);
		
		verify(scoreRepository, times(1)).existsById(1);
	}
	
	@Test
	public void updatePlayerOverallScoreShouldNoException() {
		Score scoreOne = new Score(new Category("Overall"), new Player("PlayerOne"), 10, 100000L);
		Score scoreTwo = new Score(new Category("Overall"), new Player("PlayerOne"), 20, 200000L);
		Score scoreThree = new Score(new Category("CategoryThree"), new Player("PlayerOne"), 30, 100000L);
		
		List<Score> listScore = Arrays.asList(scoreOne, scoreTwo, scoreThree);
		
		when(playerRepository.existsById(1)).thenReturn(true);
		when(scoreRepository.findByPlayerId(1)).thenReturn(listScore);
		
		scoreService.updatePlayerOverallScore(1);
		
		verify(scoreRepository, times(1)).save(scoreOne);
	}
	
	@Test(expected = ScoreExistsException.class)
	public void updatePlayerOverallScoreWithoutOverallCategoryShouldScoreExistsException() {
		List<Score> listScore = prepareScoreObjects();
		
		when(playerRepository.existsById(1)).thenReturn(true);
		when(scoreRepository.findByPlayerId(1)).thenReturn(listScore);
		
		scoreService.updatePlayerOverallScore(1);
	}
	
	@Test
	public void addOverallScoreShouldNoExceptions() {
		Category category = new Category("Overall");
		Player player = new Player("Player One");
		Score score = new Score(category, player, 0 , 0L);
		
		when(categoryRepository.findByName("Overall")).thenReturn(Optional.of(category));
		
		scoreService.addOverallScore(player);
		
		verify(scoreRepository, times(1)).save(score);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void addOverallScoreWithoutOverallCategoryShouldPlayerNotFoundException() {
		Player player = new Player("Player One");
		
		scoreService.addOverallScore(player);
		
		verify(categoryRepository, times(1)).findByName("Overall");
	}

	public List<Score> prepareScoreObjects() {
		List<Score> list;
		
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
