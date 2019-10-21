package com.runescape.runescape.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.service.BaseTest;
import com.runescape.runescape.service.PlayerService;
import com.runescape.runescape.util.*;

@AutoConfigureMockMvc
public class PlayerControllerTest extends BaseTest{

	@MockBean
	PlayerService playerService;
	
	@Autowired
    private MockMvc mvc;
	
	ObjectMapper objectMapper;
	
	@Test
	public void getAllPlayersShouldReturnAllPlayers() throws Exception {
		
		List<Player> allPlayers = Arrays.asList(new Player(generateRandomString(10)));
		
		given(playerService.getAllPlayers()).willReturn(allPlayers);
		mvc.perform(get("/players")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is(allPlayers.get(0).getName())));
	}
	
	@Test
	public void insertPlayerShouldReturnCreatedPlayer() throws Exception {
		Player player = new Player(generateRandomString(10));
		
		mvc.perform(post("/players")
				.content(Utils.asJsonString(player))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
	}
	
	@Test
	public void updatePlayerShouldReturnUpdatedPlayer() throws Exception {
		Player player = new Player(generateRandomString(10));
		
		mvc.perform(put("/players/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Utils.asJsonString(player)))
				.andExpect(status().isOk());
		
	}
	
}
