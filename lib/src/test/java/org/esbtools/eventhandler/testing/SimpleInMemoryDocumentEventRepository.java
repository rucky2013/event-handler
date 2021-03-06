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

package org.esbtools.eventhandler.testing;

import org.esbtools.eventhandler.DocumentEvent;
import org.esbtools.eventhandler.DocumentEventRepository;
import org.esbtools.eventhandler.FailedDocumentEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleInMemoryDocumentEventRepository implements DocumentEventRepository {
    private final List<DocumentEvent> documentEvents = Collections.synchronizedList(new ArrayList<>());
    private final List<DocumentEvent> published = Collections.synchronizedList(new ArrayList<>());
    private final List<FailedDocumentEvent> failed = Collections.synchronizedList(new ArrayList<>());
    private boolean failOnAddingDocumentEvents;
    private boolean considerNoTransactionsActive;

    public List<DocumentEvent> getDocumentEvents() {
        return documentEvents;
    }

    public List<DocumentEvent> getPublishedEvents() {
        return published;
    }

    public List<FailedDocumentEvent> getFailedEvents() {
        return failed;
    }

    public void failOnAddingDocumentEvents() {
        failOnAddingDocumentEvents = true;
    }

    public void passOnAddingDocumentEvents() {
        failOnAddingDocumentEvents = false;
    }

    public void considerNoTransactionsActive() {
        considerNoTransactionsActive = true;
    }

    @Override
    public void addNewDocumentEvents(Collection<? extends DocumentEvent> documentEvents) throws Exception {
        if (documentEvents.isEmpty()) return;

        if (failOnAddingDocumentEvents) {
            throw new RuntimeException("Simulated failure");
        }

        this.documentEvents.addAll(documentEvents);
    }

    @Override
    public List<? extends DocumentEvent> retrievePriorityDocumentEventsUpTo(int maxEvents) throws Exception {
        maxEvents = maxEvents > documentEvents.size() ? documentEvents.size() : maxEvents;
        List<DocumentEvent> retrieved = new ArrayList<>(documentEvents.subList(0, maxEvents));
        documentEvents.removeAll(retrieved);
        return retrieved;
    }

    @Override
    public void ensureTransactionActive(DocumentEvent event) throws Exception {
        if (considerNoTransactionsActive) {
            throw new Exception("Simulated transaction lock lost for event: " + event);
        }
    }

    @Override
    public void markDocumentEventsPublishedOrFailed(
            Collection<? extends DocumentEvent> events,
            Collection<FailedDocumentEvent> failures) throws Exception {
        published.addAll(events);
        failed.addAll(failures);
    }
}
