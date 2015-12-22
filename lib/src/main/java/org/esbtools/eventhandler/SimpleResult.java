/*
 *  Copyright 2015 esbtools Contributors and/or its affiliates.
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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public final class SimpleResult<T> implements Result<T> {
    private final T result;
    private final Collection<String> errors;

    public SimpleResult(T result, Collection<String> errors) {
        this.result = result;
        this.errors = errors;
    }

    @Override
    public T get() {
        return result;
    }

    @Override
    public Collection<String> errors() {
        return Collections.unmodifiableCollection(errors);
    }

    @Override
    public String toString() {
        return "SimpleResult{" +
                "result=" + result +
                ", errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleResult<?> that = (SimpleResult<?>) o;
        return Objects.equals(result, that.result) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, errors);
    }
}
