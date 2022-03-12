package server.database.entities;

import commons.entities.UserDTO;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
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
@Table(name = "user_details")
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
    @NonNull protected String username;

    /**
     * email - string used for authentication purposes representing the email of the user.
     */
    @Column(nullable = false, unique = true)
    @NonNull protected String email;

    /**
     * password - string representing user's salted password.
     */
    @Column(nullable = false)
    @NonNull protected String password;

    /**
     * score - integer representing a player's total score.
     */
    @Column
    protected int score = 0;

    /**
     * id - random unique uuid assigned to a certain player.
     */
    @Column
    protected int gamesPlayed = 0;

    /**
     * Relation to player entities for each individual game.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<GamePlayer> gamePlayers = Collections.synchronizedSet(new HashSet<>());

    @Override
    public UserDTO getDTO() {
        return new ModelMapper().map(this, UserDTO.class);
    }
}
