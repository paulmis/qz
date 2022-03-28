package client.communication.game;

import commons.entities.AnswerDTO;

/**
 * The interface for the answer handler.
 * Handles the click option event.
 */
public interface AnswerHandler {
    /**
     * Handle function of the interface.
     * Deals with the click of one of the buttons.
     */
    void handle(AnswerDTO answer);
}