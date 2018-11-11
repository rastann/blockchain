package blockchain;

import java.util.HashMap;

public class UnspentTransaction {

    private HashMap<String, TransactionOutput> unspentTransaction; //list of all unspent transactions.

    public UnspentTransaction() {
        unspentTransaction = new HashMap<>();
    }

    public void add(String id, TransactionOutput transactionOutput) {
        unspentTransaction.put(id, transactionOutput);
    }

    public void remove(String id) {
        unspentTransaction.remove(id);
    }

    public TransactionOutput get(String id) {
        return unspentTransaction.get(id);
    }

    public HashMap<String, TransactionOutput> get() {
        return unspentTransaction;
    }
}
