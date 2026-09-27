package org.example.model;

//En medlem i biblioteket är en vanlig klass, eftersom antalet
//aktiva lån förändras över tid och objektet har ett föränderligt tillstånd.
public class Member {

    public static final int MAX_LOANS = 3;

    private final int id;
    private String name;
    private int activeLoans;

    public Member(int id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Namnet får inte vara tomt.");
        }
        this.id = id;
        this.name = name.trim();
        this.activeLoans = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Namnet får inte vara tomt.");
        }
        this.name = name.trim();
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    /** metoden avgör om medlemmen får låna fler böcker. */
    public boolean canBorrowMore() {
        return activeLoans < MAX_LOANS;
    }

    /** registrerar ett nytt lån på medlemmen. */
    public void registerLoan() {
        if (!canBorrowMore()) {
            throw new IllegalStateException(name + " har redan maximalt antal lån (" + MAX_LOANS + ").");
        }
        activeLoans++;
    }

    /** Registrerar att medlemmen lämnat boken tillbaka. */
    public void registerReturn() {
        if (activeLoans > 0) {
            activeLoans--;
        }
    }

    @Override
    public String toString() {
        return "#" + id + " " + name + " (aktiva lån: " + activeLoans + "/" + MAX_LOANS + ")";
    }
}
