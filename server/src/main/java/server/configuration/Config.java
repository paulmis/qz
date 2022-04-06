/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package server.configuration;

import commons.entities.questions.MCType;
import java.util.Random;
import lombok.Generated;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.database.entities.question.EstimateQuestion;
import server.database.entities.question.MCQuestion;

/**
 * Configuration of the quiz application.
 */
@Generated
@Configuration
public class Config {

    /** Retrieve a new PRNG object.
     *
     * @return Newly-generated PRNG object
     */
    @Bean
    public Random getRandom() {
        return new Random();
    }

    /**
     * Get a new task scheduler.
     *
     * @return A new thread pool task scheduler.
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(8);
        scheduler.setThreadNamePrefix("quiz-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    /**
     * Structure representing a question class and its subtypes.
     */
    public static class QuestionType {
        public Class questionType;
        public Enum questionSubtype;

        public QuestionType(Class type, Enum subtype) {
            this.questionType = type;
            this.questionSubtype = subtype;
        }

        public QuestionType(Class type) {
            this(type, null);
        }
    }

    /**
     * Array of enabled question types.
     */
    public static final QuestionType[] enabledQuestionTypes = {
        new QuestionType(MCQuestion.class, MCType.GUESS_COST),
        new QuestionType(MCQuestion.class, MCType.GUESS_ACTIVITY),
        new QuestionType(MCQuestion.class, MCType.INSTEAD_OF),
        new QuestionType(EstimateQuestion.class),
    };

    /**
     * Number of attempts in generating a question from the activities before giving up.
     */
    public static final int questionGenerationAttempts = 1000;
}