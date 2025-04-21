/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.repository;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Member} entities.
 * Provides methods to perform CRUD operations and custom queries on members.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by email address.
     * 
     * @param email the email to search for
     * @return the optional member with the given email
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Check if a member with the given email exists.
     * 
     * @param email the email to check
     * @return true if a member with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all members ordered by name in ascending order.
     * 
     * @return a list of all members ordered by name
     */
    List<Member> findAllByOrderByNameAsc();
    
    /**
     * Find members whose name contains the given string (case insensitive).
     * 
     * @param name the name pattern to search for
     * @return a list of matching members
     */
    @Query("SELECT m FROM Member m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY m.name ASC")
    List<Member> findByNameContainingIgnoreCaseOrderByNameAsc(@Param("name") String name);
}
