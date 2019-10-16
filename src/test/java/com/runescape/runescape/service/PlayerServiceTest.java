package com.runescape.runescape.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.exceptions.PlayerNotFoundException;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.repository.PlayerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PlayerServiceTest {

	@Mock
	PlayerRepository playerRepository;
	
	@Mock
	ScoreService scoreService;
	
	@InjectMocks
	PlayerService playerService;
	
	@Test
	public void getAllPlayersShouldReturnAllPlayers() {
		List<Player> list = new ArrayList<Player>();
		Player playerOne = new Player("John");
        Player playerTwo = new Player("Alex");
        Player playerThree = new Player("Steve");
         
        list.add(playerOne);
        list.add(playerTwo);
        list.add(playerThree);
         
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
		Optional<Player> optionalPlayer = Optional.of(new Player("Player One"));
		
		Mockito.<Optional<Player>> when(playerRepository.findById(1)).thenReturn(optionalPlayer);
		
		Player returnedPlayer = playerService.getPlayerById(1);
		
		assertEquals("Player One", returnedPlayer.getName());
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void getCategoryByIdNotExistsShouldReturnPlayerNotFoundException() {
		playerService.getPlayerById(1);
	}
	
	@Test
	public void savePlayerShouldNoException() {
		Player player = new Player(1, "Player One");
		
		when(playerRepository.save(player)).thenReturn(player);
		doNothing().when(scoreService).addOverallScore(player);
		
		playerService.insertPlayer(player);
		
		//Verification
		ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
		verify(playerRepository).save(captor.capture());
		
		assertEquals("Player One", captor.getValue().getName());
	}
	
	@Test(expected = NullPointerException.class)
	public void savePlayerWithNullShouldNullPointerException() {
		playerService.insertPlayer(null);
	}
	
	@Test
	public void updatePlayerShouldChangeName() {
		Player player = new Player("Update Player");

		when(playerRepository.existsById(1)).thenReturn(true);
		when(playerRepository.findById(1)).thenReturn(Optional.of(player));
		
		playerService.updatePlayer(player, 1);
		
		verify(playerRepository, times(1)).findById(1);
	}
	
	@Test(expected = PlayerNotFoundException.class)
	public void updatePlayerInvalidIdShouldPlayerNotFoundException() {
		Player player = new Player("Test");

		playerService.updatePlayer(player, 1);

		verify(playerRepository, times(1)).existsById(1);
	}
	
	@Test
	public void searchPlayerByNameShouldReturnPlayer() {
		List<Player> listPlayers = new ArrayList<Player>();
		List<Player> returnedList = new ArrayList<Player>();
		
		//Adding the entire list
		Player playerOne = new Player("Player One");
		Player playerTwo = new Player("Player Two");
		Player playerThree = new Player("Dont");

		listPlayers.add(playerOne);
		listPlayers.add(playerTwo);
		listPlayers.add(playerThree);

		//Adding the return list
		listPlayers.remove(playerThree);
		returnedList.addAll(listPlayers);
		
		when(playerRepository.findByNameContainingIgnoreCase("Player")).thenReturn(returnedList);
		
		List<Player> finalList = playerService.searchPlayerByName("Player");
		
		assertEquals(2, finalList.size());
		assertEquals(playerOne, finalList.get(0));
		assertEquals(playerTwo, finalList.get(1));
		verify(playerRepository, times(1)).findByNameContainingIgnoreCase("Player");
	}
	
	@Test(expected = NullPointerException.class)
	public void searchPlayerByNullNameShouldReturnNullPointerException() {
		playerService.searchPlayerByName(null);
	}
	
	@Test
	public void detelePlayerShouldNoException() {
		playerService.deletePlayer(1);
	
		verify(playerRepository, times(1)).deleteById(1);
	}
	
}
