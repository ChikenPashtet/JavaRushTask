package com.game.controller;


import com.game.dto.PlayerRequestBody;
import com.game.entity.Player;
import com.game.entity.PlayerWithAllParams;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (order == null) {
            order = PlayerOrder.ID;
        }
        PlayerWithAllParams player = new PlayerWithAllParams(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);

        PageRequest pr = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Page<Player> players = playerService.findByParams(player, pr);

        return new ResponseEntity<>(players.getContent(), HttpStatus.OK);
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
        return new ResponseEntity<>(playerService.getCountOfPlayers(player), HttpStatus.OK);
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return new ResponseEntity<>(playerService.findOne(id), HttpStatus.OK);
    }

    @PostMapping("/rest/players")
    public ResponseEntity<Player> createPlayer(@RequestBody PlayerRequestBody player) {
        return new ResponseEntity<>(playerService.create(player), HttpStatus.OK);
    }

    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody PlayerRequestBody player) {
        return new ResponseEntity<>(playerService.update(id, player), HttpStatus.OK);
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Long id) {
        playerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
