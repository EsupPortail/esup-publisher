/**
 *
 */
package org.esupportail.publisher.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 17 juil. 2014
 */
@NoRepositoryBean
public interface AbstractRepository<M , ID extends Serializable> extends JpaRepository<M, ID>, QueryDslPredicateExecutor<M> {}
