package object;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionInfo {
    private static int idCounter = 0;
    private int id;
    private String owner;
    private int transId;
    private int amount;
    protected String formatter =  "yyyy-MM-dd HH:mm:ss";
    private Date customTime;

    public TransactionInfo() {
    }

    public int getId() {
        return id;
    }

    public TransactionInfo( int transId, int amount, String customTime) throws ParseException {
        this.id = idCounter++;
        this.transId = transId;
        this.amount = amount;
        this.customTime = new SimpleDateFormat(formatter).parse(customTime);
    }



    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getCustomTime() {
        return customTime;
    }

    public void setCustomTime(Date customTime) {
        this.customTime = customTime;
    }
}
