package com.runescape.runescape.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.runescape.runescape.dto.PlayerComparisonDTO;
import com.runescape.runescape.exceptions.PlayerNotFoundException;
import com.runescape.runescape.model.Category;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.model.Score;
import com.runescape.runescape.repository.CategoryRepository;
import com.runescape.runescape.repository.PlayerRepository;
import com.runescape.runescape.repository.ScoreRepository;

@Service
public class PlayerService {

    Logger logger = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ScoreRepository scoreRepository;
    
    public Player save(Player player) {
    	return playerRepository.save(player);
    }

    public List<Player> getAllPlayers() {
        logger.info("Getting all players");
        
        List<Player> players = new ArrayList<>();
        
        playerRepository.findAll().forEach(players::add);
        
        return players;
    }
    
    public Player getPlayerById(Integer id) {
        logger.info("Getting player with id: " + id);
        
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public Player insertPlayer(Player newPlayer) {
    	newPlayer = playerRepository.save(newPlayer);
        logger.info("Inserted player with id: " + newPlayer.getId());

        Optional<Category> overallCategory = categoryRepository.findByName("Overall");
        Player newPlayerAuxiliar = newPlayer;
        
        Score overallScore = new Score(overallCategory.orElseThrow(
                () -> new PlayerNotFoundException(newPlayerAuxiliar.getId())
        ), newPlayer, 0, 0L);
        
        scoreRepository.save(overallScore);
        
        logger.info("Inserted overall score for player");
        return newPlayer;
    }

    public void updatePlayer(Player player, Integer id) {
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }
        
        Player tempPlayer = playerRepository.findById(id)
        		.orElseThrow(() -> new PlayerNotFoundException(id));
        
        tempPlayer.setName(player.getName());
        playerRepository.save(tempPlayer);
        
        logger.info("Updated player with id: " + id);
    }

    public void deletePlayer(Integer id) {
        playerRepository.deleteById(id);
        logger.info("Deleted player with id: " + id);
    }
 
    public List<Player> searchPlayerByName(String name) {
        return playerRepository.findByNameContainingIgnoreCase(name);
    }

    public List<PlayerComparisonDTO> compareToPlayer(Integer id1, Integer id2) {
        if (!playerRepository.existsById(id1)) {
            throw new PlayerNotFoundException(id1);
        }if (!playerRepository.existsById(id2)) {
            throw new PlayerNotFoundException(id2);
        }
        List<Score> scores1 = scoreRepository.findByPlayerId(id1);
        List<Score> scores2 = scoreRepository.findByPlayerId(id2);
        
        List<PlayerComparisonDTO> playersComparisons = scores1.stream()
                .filter(s1 -> scores2.stream()
                        .anyMatch(s2 -> s2.belongsToSameCategory(s1)))
                .map(s1 -> {
                    Score s2 = scores2.stream()
                            .filter(s3 -> s3.belongsToSameCategory(s1))
                            .findFirst()
                            .get();
                    return new PlayerComparisonDTO(s1, s2);
                }).collect(Collectors.toList());
        
        return playersComparisons;
    }

}

