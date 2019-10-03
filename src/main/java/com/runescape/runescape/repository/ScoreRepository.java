package com.runescape.runescape.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.runescape.runescape.model.Score;

public interface ScoreRepository extends CrudRepository<Score, Integer> {

    List<Score> findTop10ByCategoryIdOrderByLevelDescXpDesc(Integer id);

    List<Score> findByPlayerId(Integer id);

    boolean existsByPlayerIdAndCategoryName(Integer id, String categoryName);

}

