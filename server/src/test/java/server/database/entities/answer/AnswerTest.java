package server.database.entities.answer;

import static org.junit.jupiter.api.Assertions.*;
import static server.TestHelpers.getUUID;

import commons.entities.AnswerDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import server.database.entities.question.Activity;

class AnswerTest {

    private static Activity getActivity(int id) {
        Activity a = new Activity();
        a.setId(getUUID(id));
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
        dto.setResponse(choices.stream().map(Activity::getDTO).collect(Collectors.toList()));
        Answer toTest = new Answer(dto);
        assertEquals(choices, toTest.getResponse());
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
        toTest.setResponse(choices);
        AnswerDTO dto = toTest.getDTO();
        assertEquals(choices.stream().map(Activity::getDTO).collect(Collectors.toList()), dto.getResponse());
    }
}