package com.game.service;

import com.game.dto.PlayerRequestBody;
import com.game.exceptions.WrongParamsException;

import java.util.Date;

public class PlayerParamsChecker {
    private static final int YEAR_2000 = 2000;
    private static final int YEAR_3000 = 3000;
    private static final int MAX_EXPERIENCE = 10000000;
    private static final int MAX_NAME_LENGTH = 12;
    private static final int MAX_TITLE_LENGTH = 30;

    public static void playerParamsCheckerForCreate(PlayerRequestBody player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getBirthday() == null
                || player.getExperience() == null
                || player.getProfession() == null
                || player.getRace() == null
                || player.getName().length() > MAX_NAME_LENGTH
                || player.getTitle().length() > MAX_TITLE_LENGTH
                || player.getName().equals("")
                || checkBirthday(player.getBirthday())
                || checkExperience(player.getExperience())) {
            throw new WrongParamsException();
        }
    }

    public static void playerParamsCheckerForUpdate(PlayerRequestBody player) {
        if (player.getName() != null) {
            if (player.getName().length() > MAX_NAME_LENGTH || player.getName().equals("")) {
                throw new WrongParamsException();
            }
        }
        if (player.getTitle() != null) {
            if (player.getTitle().length() > MAX_TITLE_LENGTH) {
                throw new WrongParamsException();
            }
        }
        if (player.getBirthday() != null) {
            if (checkBirthday(player.getBirthday())) {
                throw new WrongParamsException();
            }
        }
        if (player.getExperience() != null) {
            if (checkExperience(player.getExperience())) {
                throw new WrongParamsException();
            }
        }
    }

    public static boolean checkBirthday(Long birthday) {
        if (birthday < 0) {
            return true;
        }
        int year = new Date(birthday).getYear() + 1900;
        return YEAR_2000 > year || YEAR_3000 < year;
    }

    public static boolean checkExperience(int experience) {
        return experience < 0 || experience > MAX_EXPERIENCE;
    }
}
