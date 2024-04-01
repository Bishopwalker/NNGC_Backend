package com.northernneckgarbage.repository;

import com.northernneckgarbage.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = """
      select t from Token t inner join Customer u\s
      on t.customer.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Long id);

    @Query(value = "SELECT * FROM token WHERE customer_id = :id " +
            "AND (expired = false OR revoked = false)", nativeQuery = true)
    List<Token> findAllValidTokenByUserNative(Long id);

    Optional<Token> findByToken(String token);


}