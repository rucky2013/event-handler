/*
 *  Copyright 2016 esbtools Contributors and/or its affiliates.
 *
 *  This file is part of esbtools.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.esbtools.eventhandler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// TODO(ahenning,khowell): There is no consensus on whether this class should exist. There is
// consensus however that we should explore moving to a more explicit expression of batchable
// operations, which would remove the need for this class, so let's do that.
public class NestedTransformableFuture<U> implements TransformableFuture<U> {
    private final TransformableFuture<TransformableFuture<U>> nestedFuture;

    public NestedTransformableFuture(TransformableFuture<TransformableFuture<U>> nestedFuture) {
        this.nestedFuture = nestedFuture;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return nestedFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return nestedFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return nestedFuture.isDone();
    }

    @Override
    public U get() throws InterruptedException, ExecutionException {
        TransformableFuture<U> nextFuture = nestedFuture.get();
        return nextFuture == null ? null : nextFuture.get();
    }

    @Override
    public U get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException {
        // TODO: Technically, we'd need to track time on first and do delta for second timeout
        // But we don't really care that much right now
        TransformableFuture<U> nextFuture = nestedFuture.get(timeout, unit);
        return nextFuture == null ? null : nextFuture.get(timeout, unit);
    }

    @Override
    public <V> TransformableFuture<V> transformSync(FutureTransform<U, V> futureTransform) {
        return nestedFuture.transformAsync(
                nextFuture -> nextFuture.transformSync(futureTransform));
    }

    @Override
    public <V> TransformableFuture<V> transformAsync(
            FutureTransform<U, TransformableFuture<V>> futureTransform) {
        return nestedFuture.transformAsync(
                nextFuture -> nextFuture.transformAsync(futureTransform));
    }

    @Override
    public TransformableFuture<Void> transformAsyncIgnoringReturn(
            FutureTransform<U, TransformableFuture<?>> futureTransform) {
        return nestedFuture.transformAsync(
                nextFuture -> nextFuture.transformAsyncIgnoringReturn(futureTransform));
    }

    /**
     * Because the result in this context is the result of a nested future, and "done" implies
     * having a result, we put off calling the callbacks until the nested future is done to
     * coincide with when this future has a result.
     */
    @Override
    public TransformableFuture<U> whenDoneOrCancelled(FutureDoneCallback callback) {
        nestedFuture.whenDoneOrCancelled(() -> {
            final TransformableFuture<U> nextFuture;
            try {
                nextFuture = nestedFuture.get();
            } catch (Exception e) {
                callback.onDoneOrCancelled();
                return;
            }
            nextFuture.whenDoneOrCancelled(callback);
        });
        return this;
    }
}
