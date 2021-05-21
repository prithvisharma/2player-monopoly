package com.monopoly.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "player")
public class Player {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	private int money;
	
	private int location;
	
	@OneToMany
	@JoinTable(name = "player_place",
	          joinColumns = @JoinColumn(name = "player_id"),
	          inverseJoinColumns = @JoinColumn(name = "place_id"))
	private Set <Place> places = new HashSet<>();

	public Player(String name, int money, int location) {
		super();
		this.name = name;
		this.money = money;
		this.location = location;
	}
	
	public void addPlace(Place place) {
		this.places.add(place);
	}
	
	public void removeAllPlaces() {
		this.places.clear();
	}
}
