package de.cyberfacility.alpaykucuk.palaver;

public class Message {

    private String sender;
    private String empfänger;
    private String typ;
    private String data;
    private String date;

    public Message(String sender, String empfänger, String typ, String data, String date) {
        this.sender = sender;
        this.empfänger = empfänger;
        this.typ = typ;
        this.data = data;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getEmpfänger() {
        return empfänger;
    }

    public void setEmpfänger(String empfänger) {
        this.empfänger = empfänger;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
