package com.runescape.runescape.service;

import java.security.SecureRandom;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.runescape.runescape.model.Category;
import com.runescape.runescape.model.Player;
import com.runescape.runescape.model.Score;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseTest {
	
	protected static final String OVERALL_CATEGORY = "Overall";
	protected static final Integer OVERALL_ID = 1;
	
	private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    
    private static SecureRandom random = new SecureRandom();
    
    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);

        }

        return sb.toString();

    }
	
	public static Integer getRandomIntegerBetweenRange(Integer min, Integer max){
	    Integer x = (int) ((Math.random()*((max-min)+1))+min);
	    return x;
	}
	
	public static Score generateRandomScore() {
		return new Score(new Category(generateRandomString(10)),
				new Player(generateRandomString(10)),
				getRandomIntegerBetweenRange(1, 10),
				(long) getRandomIntegerBetweenRange(1, 10));
	}
	
	public static Player generateRandomPlayer() {
		return new Player(generateRandomString(10));
	}
	
	public static Category generateRandomCategory() {
		return new Category(generateRandomString(10));
	}

}
