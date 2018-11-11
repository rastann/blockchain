package blockchain;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockChainValidation {

    private static boolean checkCurrentBlockHash(Block currentBlock) {
        if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
            System.out.println("#Current Hashes not equal");
            return false;
        }
        return true;
    }

    private static boolean checkPreviousBlockHash(Block currentBlock, Block previousBlock) {
        if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
            System.out.println("#Previous Hashes not equal");
            return false;
        }
        return true;
    }

    private static boolean hashIsSolved(Block currentBlock, int difficulty, String hashTarget) {
        if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
            System.out.println("#This block hasn't been mined");
            return false;
        }
        return true;
    }

    private static boolean checkTransactionSignature(Transaction currentTransaction, int transactionIndex) {
        if (!currentTransaction.verifiySignature()) {
            System.out.println("#Signature on Transaction(" + transactionIndex + ") is Invalid");
            return false;
        }
        return true;
    }

    private static boolean checkTransactionInputAndOutput(Transaction transaction, int transactionIndex) {
        if (transaction.getInputsValue() != transaction.getOutputsValue()) {
            System.out.println("#Inputs are note equal to outputs on Transaction(" + transactionIndex + ")");
            return false;
        }
        return true;
    }

    private static boolean checkReferencedInputTransaction(TransactionInput transactionInput, TransactionOutput transactionOutput, int transactionIndex) {
        if (transactionOutput == null) {
            System.out.println("#Referenced input on Transaction(" + transactionIndex + ") is Missing");
            return false;
        }
        if (transactionInput.getUnspentTransactionOutput().getValue() != transactionOutput.getValue()) {
            System.out.println("#Referenced input Transaction(" + transactionIndex + ") value is Invalid");
            return false;
        }
        return true;
    }

    private static boolean checkTransactionRecipient(Transaction currentTransaction, int transactionIndex) {
        if (currentTransaction.getOutput(0).getRecipient() != currentTransaction.getRecipient()) {
            System.out.println("#Transaction(" + transactionIndex + ") output recipient is not who it should be");
            return false;
        }
        if (currentTransaction.getOutput(1).getRecipient() != currentTransaction.getSender()) {
            System.out.println("#Transaction(" + transactionIndex + ") output 'change' is not sender.");
            return false;
        }
        return true;
    }

    public static boolean isValid(int difficulty, Transaction genesisTransaction, ArrayList<Block> blockchain) {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.getOutput(0).getId(), genesisTransaction.getOutput(0));

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if (!checkCurrentBlockHash(currentBlock) || !checkPreviousBlockHash(currentBlock, previousBlock) || !hashIsSolved(currentBlock, difficulty, hashTarget)) {
                return false;
            }
            for (int transactionIndex = 0; transactionIndex < currentBlock.getTransactions().size(); transactionIndex++) {
                Transaction currentTransaction = currentBlock.getTransaction(transactionIndex);
                if (!checkTransactionSignature(currentTransaction, transactionIndex) || !checkTransactionInputAndOutput(currentTransaction, transactionIndex)) {
                    return false;
                }
                for (TransactionInput transactionInput : currentTransaction.getInputs()) {
                    if (!checkReferencedInputTransaction(transactionInput, tempUTXOs.get(transactionInput.getTransactionOutputId()), transactionIndex)) {
                        return false;
                    }
                    tempUTXOs.remove(transactionInput.getTransactionOutputId());
                }
                currentTransaction.getOutputs().forEach(output -> tempUTXOs.put(output.getId(), output));
                if(!checkTransactionRecipient(currentTransaction, transactionIndex)) {
                    return false;
                }
            }
        }
        return true;
    }
}
