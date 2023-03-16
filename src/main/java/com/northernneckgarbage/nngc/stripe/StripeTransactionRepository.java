package com.northernneckgarbage.nngc.stripe;

import com.northernneckgarbage.nngc.entity.StripeTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface StripeTransactionRepository extends JpaRepository<StripeTransactions, Long> {

    @Query(value = """
              select t from StripeTransactions t inner join Customer u\s
                on t.customer.id = u.id\s
                where u.id = :id\s
""")
    List<StripeTransactions> findAllByCustomerId(Long id);

    Optional<StripeTransactions> findByTransactionId(String transactionId);
}
