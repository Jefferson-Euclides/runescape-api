package com.runescape.runescape.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

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

@Service
public class ScoreService {

    Logger logger = LoggerFactory.getLogger(ScoreService.class);

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    CategoryRepository categoryRepository;

    public List<Score> getScoresByCategoryId(Integer id) {
        logger.info("Getting scores for category with id: " + id);
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        return scoreRepository.findTop10ByCategoryIdOrderByLevelDescXpDesc(id);
    }

    public List<Score> getAllScoresByPlayerId(Integer id) {
        logger.info("Getting scores for player with id: " + id);
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }
        return scoreRepository.findByPlayerId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    public Score insertPlayerScore(Score score, Integer playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException(playerId);
        }
        if (scoreRepository.existsByPlayerIdAndCategoryName(playerId, score.getCategory().getName())) {
            throw new ScoreExistsException( playerId, score.getCategory().getName(), true);
        }
        
        Player player = playerRepository.findById(playerId)
        		.orElseThrow(() -> new PlayerNotFoundException(playerId));
        
        score.setPlayer(player);
        score = scoreRepository.save(score);
        logger.info("Inserted score for player with id: " + playerId);

        updatePlayerOverallScore(playerId);
        return score;
    }

    public void updatePlayerScore(Score score, Integer playerId, Integer scoreId) {
        if (!scoreRepository.existsById(scoreId)) {
            throw new ScoreNotFoundException(scoreId);
        }
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException(playerId);
        }
        
        Player player = playerRepository.findById(playerId)
        		.orElseThrow(() -> new PlayerNotFoundException(playerId));
        Score updatedScore = scoreRepository.findById(scoreId)
        		.orElseThrow(() -> new ScoreNotFoundException(scoreId));
        
        updatedScore.setPlayer(player);
        updatedScore.setCategory(score.getCategory());
        updatedScore.setLevel(score.getLevel());
        updatedScore.setXp(score.getXp());
        
        scoreRepository.save(updatedScore);
        
        logger.info("Inserted score for player with id: " + playerId);

        updatePlayerOverallScore(playerId);
    }

    public void deletePlayerScore(Integer scoreId, Integer playerId) {
        if (!scoreRepository.existsById(scoreId)) {
            throw new ScoreNotFoundException(scoreId);
        }
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException(playerId);
        }
        scoreRepository.deleteById(scoreId);
        logger.info("Inserted score with id: " + scoreId + " for player with id: " + playerId);

        updatePlayerOverallScore(playerId);
    }

    protected void updatePlayerOverallScore(Integer playerId) {
        List<Score> playerScores = getAllScoresByPlayerId(playerId);
        Predicate<Score> belongsToOverallCategory = Score::belongsToOverallCategory;
        Score overallScore = playerScores.stream()
                .filter(belongsToOverallCategory)
                .findAny()
                .orElseThrow(() -> new ScoreExistsException(playerId, "Overall", false));
        Integer totalLevel = playerScores.stream()
                .filter(belongsToOverallCategory.negate())
                .mapToInt(Score::getLevel)
                .sum();
        Long totalXp = playerScores.stream()
                .filter(belongsToOverallCategory.negate())
                .mapToLong(Score::getXp)
                .sum();
        overallScore.setLevel(totalLevel);
        overallScore.setXp(totalXp);
        scoreRepository.save(overallScore);

        logger.info("Updated Overall score for player with id: " + playerId);
    }
    
    public void addOverallScore(Player player) {
    	Optional<Category> overallCategory = categoryRepository.findByName("Overall");
        Player newPlayerAuxiliar = player;
        
        Score overallScore = new Score(overallCategory.orElseThrow(
                () -> new PlayerNotFoundException(newPlayerAuxiliar.getId())
        ), player, 0, 0L);
        
        scoreRepository.save(overallScore);
        logger.info("Inserted overall score for player");
    }

}
