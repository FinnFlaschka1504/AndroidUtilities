package com.finn.androidUtilitiesExample;

import com.finn.androidUtilities.ParentClass;

import java.util.UUID;

public class Player extends ParentClass {
    public Player(String name) {
        this.name = name;
        uuid = "player_" + UUID.randomUUID().toString();
    }
}
