package com.game.controller;


import com.game.entity.Player;
import com.game.entity.PlayerWithAllParams;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.WrongParamsException;
import com.game.service.PlayerParamsChecker;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> getAllPlayers(@RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String title,
                                                      @RequestParam(required = false) Race race,
                                                      @RequestParam(required = false) Profession profession,
                                                      @RequestParam(required = false) Long after,
                                                      @RequestParam(required = false) Long before,
                                                      @RequestParam(required = false) Boolean banned,
                                                      @RequestParam(required = false) Integer minExperience,
                                                      @RequestParam(required = false) Integer maxExperience,
                                                      @RequestParam(required = false) Integer minLevel,
                                                      @RequestParam(required = false) Integer maxLevel,
                                                      @RequestParam(required = false) PlayerOrder order,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                      @RequestParam(required = false, defaultValue = "3") Integer pageSize) {
        //Заглушка епт, почему не работает через дефолтвалуе хз, Санчасть разберется пожалуй
        if (order == null) {
            order = PlayerOrder.ID;
        }
        PlayerWithAllParams player = new PlayerWithAllParams(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);

        PageRequest pr = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Page<Player> players = playerService.findByParams(player, pr);

        //Пытался сделать через jpaspecexecutor, но никак не фильтрует, придется делать через стрим,
        //хотя каждый раз доставать всех игроков вряд ли очень сильно ускоряет работу приложения
        //да и мне сколько блять таких фильтров тогда написать надо, короче если надо будет конечно, то напишу, но ебнусь

        //P.S. таки сделал, так что Санни(порш из тачек) может здесь не выдумывать свои заумные штукенцЫи
        //Page<Player> players = playerService.findAll(pr);

        return new ResponseEntity<>(players.getContent(), HttpStatus.OK);
        //return new ResponseEntity<>(players.getContent(),HttpStatus.OK);
        //return new ResponseEntity<>(players.getContent().stream().filter(player1 -> Objects.equals(player1.getName(), player.getName())).collect(Collectors.toList()), HttpStatus.OK);

    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> getCountOfPlayers(@RequestParam(required = false) String name,
                                                     @RequestParam(required = false) String title,
                                                     @RequestParam(required = false) Race race,
                                                     @RequestParam(required = false) Profession profession,
                                                     @RequestParam(required = false) Long after,
                                                     @RequestParam(required = false) Long before,
                                                     @RequestParam(required = false) Boolean banned,
                                                     @RequestParam(required = false) Integer minExperience,
                                                     @RequestParam(required = false) Integer maxExperience,
                                                     @RequestParam(required = false) Integer minLevel,
                                                     @RequestParam(required = false) Integer maxLevel) {
        PlayerWithAllParams player = new PlayerWithAllParams(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        // По-любому можно сделать как-то лучше, но особо не придумал, проблема в том, что два раза применяю спецификацию,
        // пробовал через внешнюю переменную как-то тоже не пошло, похоже очень на то что это хуйня из под коня, но работает
        //будем надеяться, что Саныч разберется с этим персом
        return new ResponseEntity<>(playerService.getCountOfPlayers(player), HttpStatus.OK);
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return new ResponseEntity<>(playerService.findOne(id), HttpStatus.OK);
    }

    @PostMapping("/rest/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        //НАДО ПЕРЕДЕЛЫВАТЬ РЕКУЕСТБАДИ ЧТОБЫ ТАМ БЫЛ НЕ ПЛЭЕР А ПЛЭЕРРЕКУЕСТБАДИ ПАСКУДА
        if (player.getBirthday().getTime()<=0){
            return new ResponseEntity<>(playerService.findOne(player.getId()), HttpStatus.BAD_REQUEST);
        }
        Player newPlayer = new Player(player.getName(), player.getTitle(), player.getRace(), player.getProfession(), player.getBirthday(), player.isBanned(), player.getExperience());
        try {
            PlayerParamsChecker.playerParamsChecker(newPlayer);
        } catch (WrongParamsException e) {
            return new ResponseEntity<>(playerService.findOne(newPlayer.getId()), HttpStatus.BAD_REQUEST);
        }
        playerService.safe(newPlayer);
        return new ResponseEntity<>(playerService.findOne(newPlayer.getId()), HttpStatus.OK);
    }

    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        playerService.findOne(id);
        Player updatePlayer = new Player(player.getName(), player.getTitle(), player.getRace(), player.getProfession(), player.getBirthday(), player.isBanned(), player.getExperience());
        return new ResponseEntity<>(playerService.update(id, updatePlayer), HttpStatus.OK);
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long id) {
        playerService.findOne(id);
        playerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
