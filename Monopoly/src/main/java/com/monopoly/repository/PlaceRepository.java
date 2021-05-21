package com.monopoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monopoly.entities.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {

}
