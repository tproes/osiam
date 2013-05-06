/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.ng.resourceserver.dao;

import org.osiam.ng.resourceserver.entities.GroupEntity;
import org.osiam.ng.resourceserver.entities.InternalIdSkeleton;
import org.osiam.ng.scim.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;


@Repository
@Transactional
public class GroupDAO extends GetInternalIdSkeleton implements GenericDAO<GroupEntity>{

    @Override
    public void create(GroupEntity group) {
        findAndAddMembers(group);
        em.persist(group);

    }

    private void findAndAddMembers(GroupEntity group) {
        Set<InternalIdSkeleton> skeletons = new HashSet<>(group.getMembers());
        for (InternalIdSkeleton i : skeletons) {
            InternalIdSkeleton skeleton = getInternalIdSkeleton(i.getId().toString());
            group.getMembers().remove(i);
            group.getMembers().add(skeleton);
        }
    }


    @Override
    public GroupEntity getById(String id) {
        try {
            return getInternalIdSkeleton(id);
        } catch (ClassCastException c) {
            LOGGER.log(Level.WARNING, c.getMessage(), c);
            throw new ResourceNotFoundException("Resource " + id + " is not a Group.");
        }
    }

    public void delete(String id) {
        GroupEntity groupEntity = getById(id);
        em.remove(groupEntity);
    }

    @Override
    public List<GroupEntity> search(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GroupEntity update(GroupEntity entity) {
        findAndAddMembers(entity);
        return em.merge(entity);
    }
}
