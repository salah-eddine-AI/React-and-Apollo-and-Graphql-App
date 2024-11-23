package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import ma.projet.graph.entities.*;
import ma.projet.graph.repositories.CompteRepository;

import ma.projet.graph.repositories.TransactionRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

     final CompteRepository compteRepository;
     final TransactionRepository transactionRepository;

    // Query to get all accounts
    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    // Query to get a specific account by ID
    @QueryMapping
    public Compte compteById(@Argument Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Compte %s not found", id)));
    }

    // Mutation to save a new account
    @MutationMapping
    public Compte saveCompte(@Argument Compte compte) {
        return compteRepository.save(compte);
    }

    // Query to calculate total statistics for account balances
    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count(); // Total number of accounts
        double sum = compteRepository.sumSoldes(); // Total sum of balances
        double average = count > 0 ? sum / count : 0; // Average balance

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }

    // Query to get accounts by type
    @QueryMapping
    public List<Compte> comptesParType(@Argument TypeCompte typeCompte) {
        return compteRepository.findByType(typeCompte);
    }

    // Mutation to delete an account by ID
    @MutationMapping
    public Boolean deleteCompte(@Argument Long id) {
        if (compteRepository.existsById(id)) {
            compteRepository.deleteById(id);
            return true;
        } else {
            return false;
           // throw new RuntimeException(String.format("Compte %s not found", id));
        }
    }

    // Mutation to add a transaction
    @MutationMapping
    public Transaction addTransaction(@Argument TransactionRequest transactionRequest) {
        Compte compte = compteRepository.findById(transactionRequest.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found"));

        Transaction transaction = new Transaction();
        transaction.setMontant(transactionRequest.getMontant());
        transaction.setDate(transactionRequest.getDate());
        transaction.setType(transactionRequest.getType());
        transaction.setCompte(compte);

        return transactionRepository.save(transaction);
    }

    // Query to get all transactions for a specific account
    @QueryMapping
    public List<Transaction> compteTransactions(@Argument Long id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte not found"));
        return transactionRepository.findByCompte(compte);
    }

    // Query to calculate global statistics on transactions
    @QueryMapping
    public Map<String, Object> transactionStats() {
        long count = transactionRepository.count(); // Total number of transactions
        Double sumDepots = transactionRepository.sumByType(TypeTransaction.DEPOT); // Total deposits
        Double sumRetraits = transactionRepository.sumByType(TypeTransaction.RETRAIT); // Total withdrawals

        // If the sumByType returns null, assign default value of 0.0
        sumDepots = (sumDepots != null) ? sumDepots : 0.0;
        sumRetraits = (sumRetraits != null) ? sumRetraits : 0.0;

        return Map.of(
                "count", count,
                "sumDepots", sumDepots,
                "sumRetraits", sumRetraits
        );
    }

}
