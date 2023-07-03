package mekanism.quilt;

import java.util.concurrent.Executor;

public record NetworkContext(Executor executor) {
    public void enqueueWork(Runnable runnable) {
        executor.execute(runnable);
    }
}