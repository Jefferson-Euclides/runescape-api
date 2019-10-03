package com.runescape.runescape.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.runescape.runescape.model.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    Optional<Player> findById(Integer id);

    List<Player> findByNameContainingIgnoreCase(String name);

}
