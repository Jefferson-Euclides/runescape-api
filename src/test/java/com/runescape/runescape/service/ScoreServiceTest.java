package com.runescape.runescape.service;

import static com.runescape.runescape.util.Utils.generateRandomPlayer;
import static com.runescape.runescape.util.Utils.generateRandomScore;
import static com.runescape.runescape.util.Utils.getRandomIntegerBetweenRange;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

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

@AutoConfigureMockMvc
public class ScoreServiceTest extends BaseTest{
	
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
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		Score scoreOne = generateRandomScore();
		
		Score scoreTwo = generateRandomScore();

		list = Arrays.asList(scoreOne, scoreTwo);
		
		when(playerRepository.existsById(randomId)).thenReturn(true);
		when(scoreRepository.findByPlayerId(randomId)).thenReturn(list);
		
		List<Score> returnedList = scoreService.getAllScoresByPlayerId(randomId);
		
		assertEquals(2, returnedList.size());
		verify(scoreRepository, times(1)).findByPlayerId(randomId);
	}

	@Test(expected = PlayerNotFoundException.class)
	public void getAllScoresByInvalidPlayerIdShouldPlayerNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		scoreService.getAllScoresByPlayerId(randomId);
		
		verify(playerRepository, times(1)).existsById(randomId);
	}
	
	@Test
	public void getScoresByCategoryIdShouldReturnTop10ScoresList() {
		List<Score> listScores = prepareScoreObjects();
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		when(categoryRepository.existsById(randomId)).thenReturn(true);
		when(scoreRepository.findTop10ByCategoryIdOrderByLevelDescXpDesc(randomId)).thenReturn(listScores);
		
		List<Score> listReturnedScores = scoreService.getScoresByCategoryId(randomId);
		
		assertEquals(10, listReturnedScores.size());
		verify(scoreRepository, times(1)).findTop10ByCategoryIdOrderByLevelDescXpDesc(randomId);
	}
	
	@Test(expected = CategoryNotFoundException.class)
	public void getScoresByInvalidCategoryIdShouldCategoryNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		scoreService.getScoresByCategoryId(randomId);
		
		verify(scoreRepository, times(1)).existsById(randomId);
	}
	
	@Test
	public void insertPlayerScoreShouldNoException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		Score score = generateRandomScore();
		
		when(playerRepository.existsById(randomId)).thenReturn(true);
		when(playerRepository.findById(randomId)).thenReturn(Optional.of(score.getPlayer()));
		when(scoreRepository.existsById(randomId)).thenReturn(true);
		when(scoreRepository.save(score)).thenReturn(score);
		
		doNothing().when(spy).updatePlayerOverallScore(randomId);
		spy.insertPlayerScore(score, randomId); 

		//Verification
		ArgumentCaptor<Score> captor = ArgumentCaptor.forClass(Score.class);
		verify(scoreRepository).save(captor.capture());
		
		assertEquals(score.getCategory().getName(), captor.getValue().getCategory().getName());
		assertEquals(score.getPlayer().getName(), captor.getValue().getPlayer().getName());
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void insertPlayerScoreInvalidPlayerIdShouldPlayerNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		Score score = generateRandomScore();
		
		scoreService.insertPlayerScore(score, null);
		
		verify(playerRepository, times(randomId)).existsById(1);
	}
	
	@Test(expected = ScoreExistsException.class)
	public void insertPlayerScoreAlreadyExistsShouldScoreExistsException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		Score score = generateRandomScore();
		
		when(playerRepository.existsById(randomId))
				.thenReturn(true);
		when(scoreRepository.existsByPlayerIdAndCategoryName(randomId, score.getCategory().getName()))
				.thenReturn(true);
		
		scoreService.insertPlayerScore(score, randomId);
		
		verify(scoreRepository, times(1))
				.existsByPlayerIdAndCategoryName(randomId, score.getCategory().getName());
	}
	
	@Test
	public void updatePlayerScoreShouldNoException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		Score score = generateRandomScore();
		
		when(playerRepository.existsById(randomId)).thenReturn(true);
		when(scoreRepository.existsById(randomId)).thenReturn(true);
		when(playerRepository.findById(randomId)).thenReturn(Optional.of(score.getPlayer()));
		when(scoreRepository.findById(randomId)).thenReturn(Optional.of(score));
		when(scoreRepository.save(score)).thenReturn(score);
		
		doNothing().when(spy).updatePlayerOverallScore(randomId);
		spy.updatePlayerScore(score, randomId, randomId); 

		verify(scoreRepository, times(1)).save(score);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void updatePlayerScoreInvalidPlayerIdShouldPlayerNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		Score score = generateRandomScore();
		
		when(scoreRepository.existsById(randomId)).thenReturn(true);
		
		scoreService.updatePlayerScore(score, randomId, randomId);
	}
	
	@Test(expected = ScoreNotFoundException.class)
	public void updatePlayerScoreInvalidScoreIdShouldScoreNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		Score score = generateRandomScore();
		
		scoreService.updatePlayerScore(score, randomId, randomId);
	}
	
	@Test
	public void detelePlayerScoreShouldNoException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		when(scoreRepository.existsById(randomId)).thenReturn(true);
		when(playerRepository.existsById(randomId)).thenReturn(true);
		
		doNothing().when(spy).updatePlayerOverallScore(randomId);
		spy.deletePlayerScore(randomId, randomId);
	
		verify(spy, times(1)).deletePlayerScore(randomId, randomId);
	}
	
	@Test(expected = ScoreNotFoundException.class)
	public void detelePlayerScoreInvalidScoreIdShouldScoreNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		scoreService.deletePlayerScore(null, randomId);
		
		verify(scoreRepository, times(1)).existsById(randomId);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void detelePlayerScoreInvalidPlayerIdShouldPlayerNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		when(scoreRepository.existsById(randomId)).thenReturn(true);
		
		scoreService.deletePlayerScore(randomId, null);
		
		verify(scoreRepository, times(1)).existsById(randomId);
	}
	
	@Test
	public void updatePlayerOverallScoreShouldNoException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		Score scoreOne = generateRandomScore();
		Score scoreTwo = generateRandomScore();
		Score scoreThree = generateRandomScore();
		
		scoreOne.setCategory(new Category(OVERALL_CATEGORY));
		scoreTwo.setCategory(new Category(OVERALL_CATEGORY));
		scoreThree.setCategory(new Category(OVERALL_CATEGORY));
		
		List<Score> listScore = Arrays.asList(scoreOne, scoreTwo, scoreThree);
		
		when(playerRepository.existsById(randomId)).thenReturn(true);
		when(scoreRepository.findByPlayerId(randomId)).thenReturn(listScore);
		
		scoreService.updatePlayerOverallScore(randomId);
		
		verify(scoreRepository, times(1)).save(scoreOne);
	}
	
	@Test(expected = ScoreExistsException.class)
	public void updatePlayerOverallScoreWithoutOverallCategoryShouldScoreExistsException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		List<Score> listScore = prepareScoreObjects();
		
		when(playerRepository.existsById(randomId)).thenReturn(true);
		when(scoreRepository.findByPlayerId(randomId)).thenReturn(listScore);
		
		scoreService.updatePlayerOverallScore(randomId);
	}
	
	@Test
	public void addOverallScoreShouldNoExceptions() {
		Score score = generateRandomScore();
		
		score.setLevel(0);
		score.setXp(0L);
		
		when(categoryRepository.findByName("Overall")).thenReturn(Optional.of(score.getCategory()));
		
		scoreService.addOverallScore(score.getPlayer());
		
		verify(scoreRepository, times(1)).save(score);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void addOverallScoreWithoutOverallCategoryShouldPlayerNotFoundException() {
		Player player = generateRandomPlayer();
		
		scoreService.addOverallScore(player);
		
		verify(categoryRepository, times(1)).findByName(player.getName());
	}

	private List<Score> prepareScoreObjects() {
		Score scoreOne = generateRandomScore();
		Score scoreTwo = generateRandomScore();
		Score scoreThree = generateRandomScore();
		Score scoreFour = generateRandomScore();
		Score scoreFive = generateRandomScore();
		Score scoreSix = generateRandomScore();
		Score scoreSeven = generateRandomScore();
		Score scoreEight = generateRandomScore();
		Score scoreNine = generateRandomScore();
		Score scoreTen = generateRandomScore();

		return Arrays.asList(scoreOne, scoreTwo, scoreThree, scoreFour, scoreFive, 
				scoreSix, scoreSeven, scoreEight, scoreNine, scoreTen);
	}

}
