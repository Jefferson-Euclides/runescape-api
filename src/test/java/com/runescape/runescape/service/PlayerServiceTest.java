package com.runescape.runescape.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.runescape.runescape.util.Utils.*;

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

import com.runescape.runescape.exceptions.PlayerNotFoundException;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.repository.PlayerRepository;

@AutoConfigureMockMvc
public class PlayerServiceTest extends BaseTest{

	@Mock
	PlayerRepository playerRepository;
	
	@Mock
	ScoreService scoreService;
	
	@InjectMocks
	PlayerService playerService;
	
	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void getAllPlayersShouldReturnAllPlayers() {
		List<Player> list = new ArrayList<Player>();
		Player playerOne = new Player(generateRandomString(10));
        Player playerTwo = new Player(generateRandomString(10));
        Player playerThree = new Player(generateRandomString(10));
         
        list = Arrays.asList(playerOne, playerTwo, playerThree);
         
        when(playerRepository.findAll()).thenReturn(list);
         
        List<Player> playerList = playerService.getAllPlayers();
		
        assertEquals(3, playerList.size());
        verify(playerRepository, times(1)).findAll();
	}
	
	@Test
	public void getAllPlayersEmptyListShouldReturnEmptyList() {
		List<Player> list = Collections.emptyList();
         
        when(playerRepository.findAll()).thenReturn(list);
         
        List<Player> playerList = playerService.getAllPlayers();
		
        assertEquals(0, playerList.size());
        verify(playerRepository, times(1)).findAll();
	}
	
	@Test
	public void getPlayerByIdShouldReturnPlayer() {
		Optional<Player> optionalPlayer = Optional.of(new Player(generateRandomString(10)));
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		Mockito.<Optional<Player>> when(playerRepository.findById(randomId)).thenReturn(optionalPlayer);
		
		Player returnedPlayer = playerService.getPlayerById(randomId);
		
		assertEquals(optionalPlayer.get().getName(), returnedPlayer.getName());
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void getCategoryByIdNotExistsShouldReturnPlayerNotFoundException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		playerService.getPlayerById(randomId);
	}
	
	@Test
	public void savePlayerShouldNoException() {
		Player player = new Player(generateRandomString(10));
		
		when(playerRepository.save(player)).thenReturn(player);
		doNothing().when(scoreService).addOverallScore(player);
		
		playerService.insertPlayer(player);
		
		//Verification
		ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
		verify(playerRepository).save(captor.capture());
		
		assertEquals(player.getName(), captor.getValue().getName());
	}
	
	@Test(expected = NullPointerException.class)
	public void savePlayerWithNullShouldNullPointerException() {
		playerService.insertPlayer(null);
	}
	
	@Test
	public void updatePlayerShouldChangeName() {
		Player player = new Player(generateRandomString(10));
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		when(playerRepository.existsById(randomId)).thenReturn(true);
		when(playerRepository.findById(randomId)).thenReturn(Optional.of(player));
		
		playerService.updatePlayer(player, randomId);
		
		verify(playerRepository, times(1)).findById(randomId);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void updatePlayerInvalidIdShouldPlayerNotFoundException() {
		Player player = new Player("Test");
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		
		playerService.updatePlayer(player, randomId);

		verify(playerRepository, times(1)).existsById(randomId);
	}
	
	@Test
	public void searchPlayerByNameShouldReturnPlayer() {
		Player playerOne = new Player(generateRandomString(10));
		
		when(playerRepository.findByNameContainingIgnoreCase(playerOne.getName()))
				.thenReturn(Arrays.asList(playerOne));
		
		List<Player> finalList = playerService.searchPlayerByName(playerOne.getName());
		
		assertEquals(1, finalList.size());
		assertEquals(playerOne, finalList.get(0));
		verify(playerRepository, times(1)).findByNameContainingIgnoreCase(playerOne.getName());
	}
	
	@Test
	public void searchPlayerByNullNameShouldEmptyList() {
		when(playerRepository.findByNameContainingIgnoreCase(null)).thenReturn(Collections.emptyList());
		
		List<Player> list = playerService.searchPlayerByName(null);
		
		assertEquals(Collections.emptyList(), list);
		verify(playerRepository, times(1)).findByNameContainingIgnoreCase(null);
	}
	
	@Test
	public void detelePlayerShouldNoException() {
		Integer randomId = getRandomIntegerBetweenRange(1, 10);
		playerService.deletePlayer(randomId);
	
		verify(playerRepository, times(1)).deleteById(randomId);
	}
	
}
