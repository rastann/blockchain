package blockchain;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Block {
    private String hash;
    private String previousHash;
    private long timeStamp;
    private int nonce;
    private String merkleRoot;
    private ArrayList<Transaction> transactions = new ArrayList<>(); // our data will be a simple message.

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return BlockChainUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = BlockChainUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public boolean addTransaction(Transaction transaction, float minimumTransaction) {
        if (transaction == null) return false;
        if (!Objects.equals(previousHash, "0")) {
            if (!transaction.processTransaction(minimumTransaction)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction getTransaction(int index) {
        return transactions.get(index);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
