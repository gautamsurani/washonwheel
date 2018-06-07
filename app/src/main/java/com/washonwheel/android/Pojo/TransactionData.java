package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 25-12-2017.
 */

public class TransactionData {
    String Remark, symbol, Amount, wallet_type, type, TransactionDate;

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getWallet_type() {
        return wallet_type;
    }

    public void setWallet_type(String wallet_type) {
        this.wallet_type = wallet_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public TransactionData(String remark, String symbol, String amount, String wallet_type, String type, String transactionDate) {

        Remark = remark;
        this.symbol = symbol;
        Amount = amount;
        this.wallet_type = wallet_type;
        this.type = type;
        TransactionDate = transactionDate;
    }
    public TransactionData(){}
}
