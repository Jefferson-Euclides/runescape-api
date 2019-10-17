package com.runescape.runescape.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "score")
public class Score {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Integer level;

    @NotNull
    private Long xp;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Score(Category category, Player player, Integer level, Long xp) {
        this.level = level;
        this.xp = xp;
        this.player = player;
        this.category = category;
    }

    public boolean belongsToOverallCategory() {
        return belongsToCategory("Overall");
    }

    public boolean belongsToCategory(String categoryName) {
        return category.getName().equalsIgnoreCase(categoryName);
    }

    public boolean belongsToSameCategory(Score score2) {
        return this.belongsToCategory(score2.getCategory().getName());
    }
    
}

