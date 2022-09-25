package com.game.service;

import com.game.dto.PlayerRequestBody;
import com.game.entity.Player;
import com.game.entity.PlayerWithAllParams;
import com.game.exceptions.NotFoundException;
import com.game.exceptions.NotValidException;
import com.game.repository.PlayerRepository;
import com.game.specification.PlayerSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@Transactional(readOnly = true)
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Page<Player> findByParams(PlayerWithAllParams player, Pageable pr) {
        return playerRepository.findAll(Specification.where(PlayerSpecification.checkParams(player)), pr);
    }

    public Player findOne(Long id) {
        if (id <= 0) {
            throw new NotValidException();
        }
        if (!playerRepository.existsById(id)) {
            throw new NotFoundException();
        }
        return playerRepository.findById(id).orElse(null);
    }

    @Transactional
    public Player save(Player player) {
        setLevelAndExperianceUntilNextLevel(player);
        return playerRepository.save(player);
    }

    @Transactional
    public Player create(PlayerRequestBody player) {
        if (player.getBanned() == null) {
            player.setBanned(false);
        }
        PlayerParamsChecker.playerParamsCheckerForCreate(player);
        Player newPlayer = new Player(player.getName(), player.getTitle(), player.getRace(), player.getProfession(), new Date(player.getBirthday()), player.getBanned(), player.getExperience());
        return save(newPlayer);
    }

    @Transactional
    public Player update(Long id, PlayerRequestBody player) {
        if (player.getName() == null && player.getTitle() == null && player.getRace() == null && player.getProfession() == null && player.getBanned() == null && player.getBirthday() == null && player.getExperience() == null) {
            return findOne(id);
        }
        PlayerParamsChecker.playerParamsCheckerForUpdate(player);
        Player updatePlayer = findOne(id);
        if (player.getName() != null) {
            updatePlayer.setName(player.getName());
        }
        if (player.getTitle() != null) {
            updatePlayer.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            updatePlayer.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            updatePlayer.setProfession(player.getProfession());
        }
        if (player.getBanned() != null) {
            updatePlayer.setBanned(player.getBanned());
        }
        if (player.getBirthday() != null) {
            updatePlayer.setBirthday(new Date(player.getBirthday()));
        }
        if (player.getExperience() != null) {
            updatePlayer.setExperience(player.getExperience());
            setLevelAndExperianceUntilNextLevel(updatePlayer);
        }

        return playerRepository.save(updatePlayer);
    }

    public void setLevelAndExperianceUntilNextLevel(Player player) {
        int level = (int) ((Math.pow(2500 + 200 * player.getExperience(), 0.5) - 50) / 100);
        player.setLevel(level);
        int experienceUntilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
        player.setUntilNextLevel(experienceUntilNextLevel);
    }

    public Integer getCountOfPlayers(PlayerWithAllParams player) {
        return playerRepository.findAll(Specification.where(PlayerSpecification.checkParams(player))).size();
    }

    @Transactional
    public void delete(Long id) {
        findOne(id);
        playerRepository.deleteById(id);
    }
}
