package com.jenus.bfpas.models;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    int age;
    private final Hand rightHand;

    public Person() {
        rightHand = new Hand("right");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Hand getRightHand() {
        return rightHand;
    }

    public static class Hand {
        private final String name;
        private final Finger thumb = new Finger("thumb");
        private final Finger index = new Finger("index");;
        private final Finger middle = new Finger("middle");;
        private final Finger ring = new Finger("ring");;
        private final Finger little = new Finger("little");

        private final List<Finger> fingers;

        public Hand(String name) {
            this.name = name;
            fingers = new ArrayList<>();
            fingers.add(thumb);
            fingers.add(index);
            fingers.add(middle);
            fingers.add(ring);
            fingers.add(little);
        }

        public String getName() {
            return name;
        }

        public Finger getThumb() {
            return thumb;
        }

        public Finger getIndex() {
            return index;
        }

        public Finger getMiddle() {
            return middle;
        }

        public Finger getRing() {
            return ring;
        }

        public Finger getLittle() {
            return little;
        }

        public List<Finger> getFingers() {
            return fingers;
        }
    }
}
