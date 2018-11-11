package blockchain;

public class TransactionInput {
    private String transactionOutputId;
    private TransactionOutput unspentTransactionOutput;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    public void setUnspentTransactionOutput(TransactionOutput transactionOutput) {
        unspentTransactionOutput = transactionOutput;
    }

    public TransactionOutput getUnspentTransactionOutput() {
        return unspentTransactionOutput;
    }

    public String getTransactionOutputId() {
        return transactionOutputId;
    }
}
