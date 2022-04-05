package server.services;

import commons.entities.questions.MCType;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.question.Activity;
import server.database.entities.question.EstimateQuestion;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
import server.database.repositories.question.ActivityRepository;
import server.database.repositories.question.QuestionRepository;

/**
 * Generate question.
 */
@Service
@Slf4j
public class QuestionService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Provides the specified amount of questions.
     *
     * @param count The amount of questions to return.
     * @return Randomly generated questions.
     * @throws IllegalStateException If the amount of activities in the database is
     *                               not sufficient to generate any question.
     */
    public List<Question> provideQuestions(int count) throws IllegalStateException {
        // Init
        List<Question> questions = new ArrayList<>();
        final int questionTypes = 4;

        // Retrieve the acceptable activities
        List<Activity> activities = activityRepository.findQuestionAcceptable();

        // Check that there are enough activities
        if (activities.size() < 5) {
            log.error("Could not provide any question: not enough activities in the database.");
            throw new IllegalStateException("Not enough activities in the database.");
        }

        int failedAttempts = 0;
        for (int idx = 0; idx < count * questionTypes; idx++) {
            // Check failed attempts
            if (failedAttempts >= 1000) {
                log.error("Could not provide any question: not enough activities in the database.");
                throw new IllegalStateException("Not enough activities in the database.");
            }

            // Select an answer
            Activity answer = activities.get(new Random().nextInt(activities.size()));

            // Sanitize the description
            String answerDescription = sanitizeDescription(answer.getDescription());

            // Generate question
            Question newQuestion;
            if (idx % questionTypes < 3) {
                // MC questions
                MCType type;
                Activity correctOption;
                String questionText;
                switch (idx % questionTypes) {
                    case 0: {
                        // Guess the cost questions
                        type = MCType.GUESS_COST;
                        questionText = "What is the energetic cost of " + answerDescription + "?";
                        correctOption = answer;
                        break;
                    }
                    case 1: {
                        // Guess the activity questions
                        type = MCType.GUESS_ACTIVITY;
                        questionText = "Which of these activities costs " + answer.getCost() + "Wh?";
                        correctOption = answer;
                        break;
                    }
                    case 2: {
                        // Instead-of questions
                        type = MCType.INSTEAD_OF;
                        questionText = "Instead of " + answerDescription + ", you could be...";
                        correctOption = activities.stream()
                                .filter(act -> act.getCost() != answer.getCost())
                                .min((o1, o2) -> {
                                    long dist1 = Math.abs(o1.getCost() - answer.getCost());
                                    long dist2 = Math.abs(o2.getCost() - answer.getCost());
                                    return Long.compare(dist1, dist2);
                                })
                                .orElse(null);
                        break;
                    }
                    default: {
                        // Not defined questions
                        log.error("Creating an undefined question type.");
                        throw new IllegalStateException("Undefined question type.");
                    }
                }
                if (correctOption == null) {
                    // Retry with a different answer
                    failedAttempts++;
                    idx--;
                    continue;
                }

                newQuestion = new MCQuestion();
                newQuestion.setText(questionText);
                ((MCQuestion) newQuestion).setQuestionType(type);
                ((MCQuestion) newQuestion).setAnswer(correctOption);

                // Set the options
                try {
                    newQuestion.setActivities(getMCOptions(activities, correctOption, 4));
                } catch (IOException e) {
                    // Retry with a different answer
                    failedAttempts++;
                    idx--;
                    continue;
                }
            } else if (idx % questionTypes == 3) {
                // Estimate questions
                newQuestion = new EstimateQuestion();

                // Set the question
                newQuestion.setText("How much does " + answerDescription + " costs approximately?");

                // Set the activity
                newQuestion.setActivities(Set.of(answer));
            } else {
                // Not defined questions
                log.error("Creating an undefined question type.");
                throw new IllegalStateException("Undefined question type.");
            }

            // Append the new question
            questions.add(newQuestion);
        }

        // Randomize the list and return the requested amount of questions
        Collections.shuffle(questions);

        // Select only the needed questions
        questions = questions.subList(0, count);

        // Save the new questions
        questions = questions.stream()
                .map(quest -> quest = questionRepository.save(quest))
                .collect(Collectors.toList());
        return questions;
    }

    /**
     * Returns a set of activities fit for a MC question.
     * The set includes the correct answer.
     *
     * @param pool   pool of activities to choose from.
     * @param answer pre-selected correct answer of the question.
     * @param size   size of the set to return.
     * @return a set containing activities close enough to the answer, but not too similar.
     * @throws IOException if the pool is not sufficient to generate the set.
     */
    private Set<Activity> getMCOptions(List<Activity> pool, Activity answer, int size)
            throws IOException {

        // 1. collect information over the answer's order of magnitude
        long answerCost = answer.getCost();
        long minLimT = 1;
        long maxLimT = 10;
        while (answerCost >= 10) {
            answerCost /= 10;
            minLimT *= 10;
            maxLimT *= 10;
        }

        // 2. define a filter
        final long minLim = minLimT;
        final long maxLim = maxLimT;
        Function<Long, Boolean> goodChoice =
                (cost) -> cost != answer.getCost()
                        && cost >= minLim
                        && cost < maxLim;

        // 3. apply the filter
        List<Activity> options = pool.stream()
                .filter(act -> goodChoice.apply(act.getCost()))
                .collect(Collectors.toList());
        Collections.shuffle(options);
        if (options.size() < size - 1) {
            throw new IOException("Not enough activities to generate the question");
        }

        // 4. select options with distinct costs (magnitude-wise)
        Set<Activity> chosen = new HashSet<>(List.of(answer));
        for (int optIdx = 0; optIdx < options.size(); optIdx++) {
            boolean optionOk = true;
            Activity option = options.get(optIdx);
            for (Activity opt : chosen) {
                if (option.getCost() / minLim == opt.getCost() / minLim) {
                    optionOk = false;
                    break;
                }
            }
            if (optionOk) {
                chosen.add(option);
                if (chosen.size() == size) {
                    break;
                }
            }
        }

        // 5. check if enough options were selected
        if (chosen.size() < size) {
            throw new IOException("Not enough activities to generate the question");
        }

        return chosen;
    }

    /**
     * Render a description fit for question generation.
     * Removes initial capital letters and final full-stops.
     *
     * @param description the original activity description.
     * @return a sanitized description.
     */
    private String sanitizeDescription(String description) {
        // 1. remove initial upper-case letter
        description = description.substring(0, 1).toLowerCase(Locale.ROOT)
                + description.substring(1);

        // 2. remove final full-stop, if present
        if (description.substring(description.length() - 1).equals(".")) {
            description = description.substring(0, description.length() - 1);
        }

        return description;
    }
}