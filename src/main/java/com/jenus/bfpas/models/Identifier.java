package com.jenus.bfpas.models;

public class Identifier {
    private Person person;
    private String finger;

    public Identifier() {
    }
    public Identifier(Person person, String finger) {
        this.person = person;
        this.finger = finger;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }
}
