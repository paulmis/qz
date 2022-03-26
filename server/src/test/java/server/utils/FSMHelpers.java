package server.utils;

import java.util.concurrent.*;

/**
 * Helpers for FSM unit test.
 */
public class FSMHelpers {
    /**
     * A mock scheduled future.
     *
     * @param <T> the type of the future.
     */
    public static class MockFuture<T> implements ScheduledFuture<T> {
        @Override
        public long getDelay(TimeUnit timeUnit) {
            return 0;
        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public int compareTo(Delayed delayed) {
            return 0;
        }
    }
}
