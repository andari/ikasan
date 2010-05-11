/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.filter.duplicate.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for {@link HibernateMessagePersistenceDaoImpl} using an in memory
 * database rather than mocking the hibernate template.
 * 
 * @author Summer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*
 * Application context will be loaded from location:
 * classpath:/org/ikasan/filter/duplicate/dao/MessagePersistenceDaoInMemDBTest-context.xml
 */

@ContextConfiguration
public class MessagePersistenceDaoInMemDBTest
{
    @Autowired
    private HibernateMessagePersistenceDaoImpl daoToTest;

    /**
     * Test case: DAO must return null since filter entry was not found in database
     */
    @Test public void filter_entry_not_found_returns_null()
    {
        FilterEntry result = this.daoToTest.findMessageById("find_test", "test".hashCode());
        Assert.assertNull(result);
    }

    /**
     * Test case: save given filter entry. 
     * Test case: look for newly saved message; it must be found and must be the same!
     */
    @Test public void save_new_entry_find_returns_same_entry()
    {
        //Save the entry
        int timeToLive = 1;
        FilterEntry newEntry = new DefaultFilterEntry("save_test".hashCode(), "test", timeToLive);
        this.daoToTest.save(newEntry);

        //Now lets find it..
        FilterEntry newEntryReloaded = this.daoToTest.findMessageById("test", "save_test".hashCode());

        Assert.assertNotNull(newEntryReloaded);
        Assert.assertEquals("test", newEntryReloaded.getClientId());
        Assert.assertEquals("save_test".hashCode(), newEntryReloaded.getCriteria().intValue());

        //Because testing Date sucks. Will restrict the assertions to yyyyMMdd only!
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String expectedCreatedDate = formatter.format(new Date(System.currentTimeMillis()));
        String expectedExpiryDate = formatter.format(new Date(System.currentTimeMillis() + (timeToLive * 24 * 3600 * 1000)));

        Assert.assertEquals(expectedCreatedDate, formatter.format(newEntryReloaded.getCreatedDateTime()));
        Assert.assertEquals(expectedExpiryDate, formatter.format(newEntryReloaded.getExpiry()));
    }

    /**
     * Test case: find an expired entry
     * Test case: delete the expired entry
     */
    @Test public void find_expired_entry_delete_expired_entry()
    {
        //Save an entry that expires now for the purpose of this test
        int timeToLive = 0;
        FilterEntry newEntry = new DefaultFilterEntry("expiry_test".hashCode(), "test", timeToLive);
        this.daoToTest.save(newEntry);

        List<FilterEntry> expiredMessages = this.daoToTest.findExpiredMessages();
        Assert.assertNotNull(expiredMessages);

        this.daoToTest.deleteAll(expiredMessages);
        expiredMessages = this.daoToTest.findExpiredMessages();
        Assert.assertNull(expiredMessages);
    }

    /**
     * Test case: try to save an already existing filter entry
     */
    @Test(expected=UncategorizedSQLException.class) public void save_duplicate_must_fail()
    {
        //Save the entry
        int timeToLive = 1;
        FilterEntry newEntry = new DefaultFilterEntry("save_duplicate_test".hashCode(), "test", timeToLive);
        this.daoToTest.save(newEntry);

        //Now try to save it again
        this.daoToTest.save(newEntry);
    }
}
