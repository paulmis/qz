package server.database.entities.answer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import commons.entities.utils.DTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.utils.BaseEntity;

/**
 * Relation entity to collect all answers for a given question.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class AnswerCollection extends BaseEntity {

    public AnswerCollection() {
        // Make sure the id is set at creation
        this.id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    @Override
    public DTO getDTO() {
        return null;
    }

    /**
     * Collection of answers represented by this class.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "answerCollection", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> collection = new TreeSet<>();

    /**
     * The game these answers refer to.
     */
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private Game game;

    /**
     * Add an answer to the collection, answer's player is maintained unique.
     *
     * @param answer answer to add
     * @return true if the answer was added successfully, false otherwise
     */
    public boolean addAnswer(Answer answer) {
        // Check if player is present
        GamePlayer player = answer.getPlayer();
        if (player == null) {
            return false;
        }

        // Retrieve previous answer from the same user and remove it
        Optional<Answer> oldAnswer = collection
                .stream().filter(ans -> player.equals(ans.getPlayer())).findFirst();

        if (oldAnswer.isPresent()) {
            // Update previous answer
            oldAnswer.get().setResponse(answer.getResponse());
        } else {
            // Add new answer
            if (!collection.add(answer)) {
                return false;
            }
            answer.setAnswerCollection(this);
        }
        return true;
    }

    public List<Answer> getAnswerList() {
        return collection.stream().sorted().collect(Collectors.toList());
    }
}
