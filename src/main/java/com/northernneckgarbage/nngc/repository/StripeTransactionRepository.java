package com.northernneckgarbage.nngc.repository;

import com.northernneckgarbage.nngc.entity.StripeTransactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface StripeTransactionRepository extends JpaRepository<StripeTransactions, Long> {

    @Query(value = """
              select t from StripeTransactions t inner join Customer u\s
                on t.customer.id = u.id\s
                where u.id = :id\s
""")
    Page<StripeTransactions> findAllByCustomerId(Long id, PageRequest of);

    Optional<StripeTransactions> findByTransactionId(String transactionId);
}
