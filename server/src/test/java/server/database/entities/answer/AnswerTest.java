package server.database.entities.answer;

import static org.junit.jupiter.api.Assertions.*;
import static server.utils.TestHelpers.getUUID;

import commons.entities.AnswerDTO;
import java.util.ArrayList;
import java.util.List;
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
        List<Long> choices = new ArrayList<>(List.of(
                getActivity(1).getCost(),
                getActivity(2).getCost(),
                getActivity(3).getCost(),
                getActivity(4).getCost()
        ));
        dto.setResponse(choices);
        Answer toTest = new Answer(dto);
        assertEquals(choices, toTest.getResponse());
    }

    @Test
    void toDTOTest() {
        List<Long> choices = new ArrayList<>(List.of(
                getActivity(1).getCost(),
                getActivity(2).getCost(),
                getActivity(3).getCost(),
                getActivity(4).getCost()
        ));
        Answer toTest = new Answer();
        toTest.setResponse(choices);
        AnswerDTO dto = toTest.getDTO();
        assertEquals(choices, dto.getResponse());
    }
}