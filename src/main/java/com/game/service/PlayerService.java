package com.game.service;

import com.game.entity.Player;
import com.game.entity.PlayerWithAllParams;
import com.game.exceptions.NotFoundException;
import com.game.exceptions.NotValidException;
import com.game.exceptions.WrongParamsException;
import com.game.repository.PlayerRepository;
import com.game.specification.PlayerSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Page<Player> findAll(Pageable pr){
        return playerRepository.findAll(pr);
    }
    public List<Player> findAll(){
        return playerRepository.findAll();
    }

    public Page<Player> findByParams(PlayerWithAllParams player, Pageable pr){
        return playerRepository.findAll(Specification.where(PlayerSpecification.checkParams(player)), pr);
    }

    public Player findOne(Long id){
        if (id <= 0){
            throw new NotValidException();
        }
        if (!playerRepository.existsById(id)){
            throw new NotFoundException();
        }
        return playerRepository.findById(id).orElse(null);
    }
    @Transactional
    public void safe(Player player){
        setLevelAndExperianceUntilNextLevel(player);
        playerRepository.save(player);
    }

    @Transactional
    public Player update(Long id, Player player){
        player.setId(id);
        setLevelAndExperianceUntilNextLevel(player);
        return playerRepository.save(player);
    }

    public void setLevelAndExperianceUntilNextLevel(Player player){
        int level = (int) ((Math.pow(2500+200*player.getExperience(),0.5)-50)/100);
        player.setLevel(level);
        int experienceUntilNextLevel = 50*(level+1)*(level+2)-player.getExperience();
        player.setUntilNextLevel(experienceUntilNextLevel);
    }
    public Integer getCountOfPlayers(PlayerWithAllParams player){
        return playerRepository.findAll(Specification.where(PlayerSpecification.checkParams(player))).size();
    }
    @Transactional
    public void delete(Long id){
        playerRepository.deleteById(id);
    }
}
