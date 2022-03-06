package server.database.entities;

import commons.entities.UserDTO;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import server.database.entities.game.GamePlayer;
import server.database.entities.utils.BaseEntity;


/**
 * User entity - describes an user in the context of the entire application.
 */

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Entity
public class User extends BaseEntity<UserDTO> {
    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public User(UserDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * User's nickname.
     */
    @Column(nullable = false, unique = true)
    @NonNull private String username;

    /**
     * email - string used for authentication purposes representing the email of the user.
     */
    @Column(nullable = false, unique = true)
    @NonNull private String email;

    /**
     * password - string representing user's salted password.
     */
    @Column(nullable = false)
    @NonNull private String password;

    /**
     * score - integer representing a player's total score.
     */
    @Column
    private int score = 0;

    /**
     * id - random unique uuid assigned to a certain player.
     */
    @Column
    private int gamesPlayed = 0;

    /**
     * Relation to player entities for each individual game.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GamePlayer> gamePlayers = Collections.synchronizedSet(new HashSet<>());

    @Override
    public UserDTO getDTO() {
        return new ModelMapper().map(this, UserDTO.class);
    }
}
