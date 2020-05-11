package de.cyberfacility.alpaykucuk.palaver;

public class GPSMessage extends Message {

    String längengrad;
    String breitengrad;

    public GPSMessage(String sender, String empfänger, String typ, String data, String date) {
        super(sender, empfänger, typ, data, date);
    }

    public void generateLängenUndBreitenGrad() {

        String[] separated = getData().split(":");
        String[] seperatedGEO = separated[1].split(",");
        breitengrad = seperatedGEO[0];
        längengrad = seperatedGEO[1];
        System.out.println("B:" + breitengrad + " L:" + längengrad);
    }
}
