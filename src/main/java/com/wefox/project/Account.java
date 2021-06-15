package com.wefox.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account")
public class Account {
    
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

    public Account() {
    }
}