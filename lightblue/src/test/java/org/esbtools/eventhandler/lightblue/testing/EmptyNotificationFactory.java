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

package org.esbtools.eventhandler.lightblue.testing;

import org.esbtools.eventhandler.lightblue.LightblueNotification;
import org.esbtools.eventhandler.lightblue.client.LightblueRequester;
import org.esbtools.eventhandler.lightblue.NotificationFactory;
import org.esbtools.lightbluenotificationhook.NotificationEntity;

import java.util.NoSuchElementException;

public class EmptyNotificationFactory implements NotificationFactory {
    @Override
    public LightblueNotification getNotificationForEntity(NotificationEntity entity, LightblueRequester requester) {
        throw new NoSuchElementException("No notification defined for entity: " + entity);
    }
}
