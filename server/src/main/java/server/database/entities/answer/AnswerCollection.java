package server.database.entities.answer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import commons.entities.utils.DTO;
import java.util.ArrayList;
import java.util.Comparator;
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
    private Set<Answer> collection = new TreeSet<>(new Comparator<Answer>() {
        @Override
        public int compare(Answer o1, Answer o2) {
            if (o1 == null || o2 == null) {
                throw new NullPointerException();
            }
            if (o1.getPlayer() == null || o2.getPlayer() == null) {
                throw new IllegalArgumentException("Only answers with a player can be compared");
            }
            return o1.getPlayer().getId().compareTo(o2.getPlayer().getId());
        }
    });

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

    /**
     * Returns the list of answers in this collection, sorted by player id.
     *
     * @return answers given by each player, sorted by player id
     */
    public List<Answer> getAnswerList() {
        // The sorting is guaranteed by the use of TreeSet
        return new ArrayList<>(collection);
    }
}
