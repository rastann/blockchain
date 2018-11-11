package blockchain;

import java.security.PublicKey;

public class TransactionOutput {
    private String id;
    private PublicKey recipient;
    private float value;

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.id = BlockChainUtil.applySha256(BlockChainUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

    public String getId() {
        return id;
    }

    public float getValue() {
        return value;
    }

    public PublicKey getRecipient() {
        return recipient;
    }
}
