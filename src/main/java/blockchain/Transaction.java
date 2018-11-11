package blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Objects;

public class Transaction {
    private String transactionId; // this is also the hash of the transaction.
    private PublicKey sender; // senders address/public key.
    private PublicKey recipient; // Recipients address/public key.
    private float value;
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    private ArrayList<TransactionInput> inputs = new ArrayList<>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return BlockChainUtil.applySha256(
                BlockChainUtil.getStringFromKey(sender) +
                        BlockChainUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = BlockChainUtil.getStringFromKey(sender) + BlockChainUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = BlockChainUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifiySignature() {
        String data = BlockChainUtil.getStringFromKey(sender) + BlockChainUtil.getStringFromKey(recipient) + Float.toString(value);
        return BlockChainUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction(float minimumTransaction) {
        if (!verifiySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        inputs.forEach(transactionInput -> transactionInput.setUnspentTransactionOutput(Client.UTXOs.get(transactionInput.getTransactionOutputId())));

        if (getInputsValue() < minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        outputs.forEach(transactionOutput -> Client.UTXOs.put(transactionOutput.getId(), transactionOutput));

        inputs.stream().map(TransactionInput::getUnspentTransactionOutput).filter(Objects::nonNull).forEach(transactionInput -> Client.UTXOs.remove(transactionInput.getId()));

        return true;
    }

    public float getInputsValue() {
        return inputs.stream().map(i -> i.getUnspentTransactionOutput().getValue()).filter(Objects::nonNull).reduce(0f, (a, b) -> a + b);
    }

    public float getOutputsValue() {
        return outputs.stream().map(TransactionOutput::getValue).reduce(0f, (a, b) -> a + b);
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public float getValue() {
        return value;
    }

    public ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    public ArrayList<TransactionOutput> getOutputs() {
        return outputs;
    }

    public TransactionOutput getOutput(int index) {
        return outputs.get(index);
    }

    public TransactionOutput getOutputTransaction(int index) {
        return outputs.get(index);
    }

    public void addToOutputs(TransactionOutput transactionOutput) {
        outputs.add(transactionOutput);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
