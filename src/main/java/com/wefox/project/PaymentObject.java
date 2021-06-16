package com.wefox.project;

import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "payments")
public class PaymentObject {

    @Id
    private int account_id;
    public int getAccount_id() {return account_id;}
    public void setAccountId(int account_id) {
        this.account_id = account_id;
    }

    @Column(name = "payment_id")
    private String payment_id;
    public String getPayment_id() {
        return payment_id;
    }
    public void setPaymentId(String payment_id) {
        this.payment_id = payment_id;
    }

    @Column(name = "payment_type")
    private String payment_type;
    public String getPayment_type() {
        return payment_type;
    }
    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    @Column(name = "amount")
    private int amount;
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Column(name = "credit_card")
    private String credit_card;
    public String getCredit_card() {
        return credit_card;
    }
    public void setCredit_card(String credit_card) {
        this.credit_card = credit_card;
    }

    public PaymentObject() {
    }

    public Map<String, Object> getPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("payment_id", getPayment_id());
        payload.put("account_id", getAccount_id());
        payload.put("payment_type", getPayment_type());
        payload.put("credit_card", getCredit_card());
        payload.put("amount", getAmount());
        return payload;
    }

    public void populate(String message) {
        JSONObject paymentRecord = new JSONObject(message);
        this.account_id = (int) paymentRecord.get("account_id");
        this.payment_id = paymentRecord.get("payment_id").toString();
        this.payment_type = paymentRecord.get("payment_type").toString();
        this.credit_card = paymentRecord.get("credit_card").toString();
        this.amount = (int) paymentRecord.get("amount");
        // this.delay = paymentRecord.get("delay").toString();
    }
}