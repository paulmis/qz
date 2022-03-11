package server.database.entities;

import static org.junit.jupiter.api.Assertions.*;

import commons.entities.AnswerDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Activity;

class AnswerTest {

    private static Activity getActivity(int id) {
        Activity a = new Activity();
        a.setDescription("Activity" + (id + 1));
        a.setCost(2 + id * 4);
        return a;
    }

    @Test
    void fromDTOConstructorTest() {
        AnswerDTO dto = new AnswerDTO();
        List<Activity> choices = new ArrayList<>(List.of(
                getActivity(1),
                getActivity(2),
                getActivity(3),
                getActivity(4)
        ));
        dto.setUserChoice(choices.stream().map(Activity::getDTO).collect(Collectors.toList()));
        Answer toTest = new Answer(dto);
        assertEquals(choices, toTest.getUserChoice());
    }

    @Test
    void toDTOTest() {
        List<Activity> choices = new ArrayList<>(List.of(
                getActivity(1),
                getActivity(2),
                getActivity(3),
                getActivity(4)
        ));
        Answer toTest = new Answer();
        toTest.setUserChoice(choices);
        AnswerDTO dto = toTest.getDTO();
        assertEquals(choices.stream().map(Activity::getDTO).collect(Collectors.toList()), dto.getUserChoice());
    }
}