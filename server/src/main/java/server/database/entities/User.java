package server.database.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import commons.entities.auth.UserDTO;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends BaseEntity<UserDTO> {
    /**
     * User's name.
     */
    @Size(min = 3, max = 20)
    @Column(nullable = false, unique = true)
    @NonNull protected String username;

    /**
     * email - string used for authentication purposes representing the email of the user.
     */
    @Email
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    @NonNull protected String email;

    /**
     * password - string representing user's salted password.
     */
    @Column(nullable = false)
    @NonNull protected String password;

    /**
     * The filepath to the user profile picture.
     */
    @Column(nullable = false)
    @NonNull private String profilePic = "";

    /**
     * score - integer representing a player's total score.
     */
    protected int score = 0;

    /**
     * number of games played by a certain user.
     */
    protected int gamesPlayed = 0;

    /**
     * Relation to player entities for each individual game.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<GamePlayer> gamePlayers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Construct a new entity from a DTO ignoring score, gamesPlayed and gamePlayer fields.
     *
     * @param dto DTO to map to entity.
     */
    public User(UserDTO dto) {
        this(dto.getUsername(), dto.getEmail(), dto.getPassword());
        if (dto.getProfilePic() != null && !dto.getProfilePic().isBlank()) {
            this.profilePic = dto.getProfilePic();
        }
    }

    @Override
    public UserDTO getDTO() {
        return new ModelMapper().map(this, UserDTO.class);
    }
}
