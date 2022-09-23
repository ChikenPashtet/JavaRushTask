package com.game.dto;

import com.game.entity.Profession;
import com.game.entity.Race;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

public class PlayerRequestBody {
    private Long id;
    private String name;
    private String title;
    private Race race;
    private Profession profession;
    private int experience;
    private Long birthday;
    private Boolean banned;

}
