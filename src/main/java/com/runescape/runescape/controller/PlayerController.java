package com.runescape.runescape.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.runescape.runescape.dto.PlayerComparisonDTO;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.model.Score;
import com.runescape.runescape.service.CategoryService;
import com.runescape.runescape.service.PlayerService;
import com.runescape.runescape.service.ScoreService;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayerById(
    		@PathVariable(value = "id") Integer id) {
        return playerService.getPlayerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player insertPlayer(
    		@RequestBody Player player) {
        return playerService.insertPlayer(player);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePlayer(
    		@RequestBody Player player,
    		@PathVariable(value = "id") Integer id) {
        playerService.updatePlayer(player, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(
    		@PathVariable(value = "id") Integer id) {
        playerService.deletePlayer(id);
    }

    @GetMapping("/{id}/scores")
    @ResponseStatus(HttpStatus.OK)
    public List<Score> getAllScoresByPlayerId(
    		@PathVariable(value = "id") Integer id) {
        return scoreService.getAllScoresByPlayerId(id);
    }

    @PostMapping("/{id}/scores")
    @ResponseStatus(HttpStatus.CREATED)
    public Score insertPlayerScore(
    		@PathVariable(value = "id") Integer id,
    		@RequestBody Score score) {
        return scoreService.insertPlayerScore(score, id);
    }

    @PutMapping("/{id}/scores/{scoreId}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePlayerScore(
    		@PathVariable(value = "id") Integer id,
    		@RequestBody Score score,
    		@PathVariable(value = "scoreId") Integer scoreId) {
        scoreService.updatePlayerScore(score, id, scoreId);
    }

	@DeleteMapping("/{id}/scores/{scoreId}")
	@ResponseStatus(HttpStatus.OK)
	public void deletePlayerScore(
			@PathVariable(value = "id") Integer id,
			@PathVariable(value = "scoreId") Integer scoreId) {
		scoreService.deletePlayerScore(scoreId, id);
	}

    // OPTIONAL: Have the ability to compare two players
    @GetMapping("/{id1}/compare/{id2}")
    @ResponseStatus(HttpStatus.OK)
    public List<PlayerComparisonDTO> compareToPlayer(
    		@PathVariable(value = "id1") Integer id1,
    		@PathVariable(value = "id2") Integer id2) {
        return playerService.compareToPlayer(id1, id2);
    }

    // OPTIONAL: Have the ability to do a search by player name
    @GetMapping("/search/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> searchPlayerByName(
    		@PathVariable(value = "name") String name) {
        return playerService.searchPlayerByName(name);
    }

}

