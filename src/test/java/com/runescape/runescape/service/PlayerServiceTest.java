package com.runescape.runescape.service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.model.Player;
import com.runescape.runescape.repository.PlayerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PlayerServiceTest {

	@Mock
	PlayerRepository playerRepository;
	
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
	
}
