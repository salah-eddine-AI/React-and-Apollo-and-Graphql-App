package ma.projet.graph.repositories;

import ma.projet.graph.entities.Transaction;
import ma.projet.graph.entities.TypeTransaction;
import ma.projet.graph.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Fetch all transactions for a specific account
    List<Transaction> findByCompte(Compte compte);

    // Sum of transactions by type (DEPOT or RETRAIT)
    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.type = :type")
    Double sumByType(@Param("type") TypeTransaction typeTransaction);

    // Count all transactions
    long count();
}
