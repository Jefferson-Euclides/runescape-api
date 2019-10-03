package com.runescape.runescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.model.Player;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class PlayerRepositoryTest {
	
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private PlayerRepository PlayerRepository;

	@Test
	public void findByIdShouldReturnValidPlayer() {
		Player Player = new Player();
		Player.setName("Test");

		entityManager.persist(Player);
		entityManager.flush();

		Optional<Player> testPlayer = PlayerRepository.findById(Player.getId());

		assertThat(testPlayer.get().getName()).isEqualTo(testPlayer.get().getName());
	}
	
	@Test
   public void findAllShouldReturnAllPlayers() {
       Player firstPlayer = new Player();
       firstPlayer.setName("Yerevan");
       entityManager.persist(firstPlayer);
       entityManager.flush();

       Player secondPlayer = new Player();
       secondPlayer.setName("Israel");
       entityManager.persist(secondPlayer);
       entityManager.flush();

       Iterable<Player> arrivals = PlayerRepository.findAll();
       
       List<Player> test = new ArrayList<>();
       arrivals.forEach(test::add);

       assertThat(test.size()).isEqualTo(12);
       assertThat(test.get(10)).isEqualTo(firstPlayer);
       assertThat(test.get(11)).isEqualTo(secondPlayer);
   }
}
