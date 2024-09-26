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
package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Member;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by their email address.
     *
     * @param email the email address to search for
     * @return the Member with the given email address
     */
    Member findByEmail(String email);

    /**
     * Find all members ordered by name.
     *
     * @return a list of all members, ordered by name
     */
    List<Member> findAllByOrderByNameAsc();

    // Note: findById is already provided by JpaRepository
    // The following methods are no longer needed as they are handled by JpaRepository or the methods above:
    // - findById(Long id)
    // - findAllOrderedByName()
}