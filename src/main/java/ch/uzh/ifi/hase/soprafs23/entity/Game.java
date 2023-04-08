package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String pin;

    public Game() {
        // generate a new random pin when creating a new game
        this.pin = generateUniquePin();
    }

    public Long getId() {
        return id;
    }

    public String getPin() {
        return pin;
    }

    private String generateUniquePin() {
        Set<String> generatedPins = new HashSet<>();
        Random random = new Random();
        String pin = "";

        while (generatedPins.size() < Math.pow(10, 6)) {
            int num = random.nextInt((int) Math.pow(10, 6));
            pin = String.format("%06d", num);

            if (!generatedPins.contains(pin)) {
                generatedPins.add(pin);
                break;
            }
        }

        return pin;
    }
}

