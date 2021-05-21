package com.monopoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monopoly.entities.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

}
