package com.monopoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monopoly.entities.Place;
import com.monopoly.entities.Player;
import com.monopoly.repository.PlaceRepository;
import com.monopoly.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class MainController {

	@Autowired
	PlayerRepository playerRepository;
	
	@Autowired
	PlaceRepository placeRepository;
	
	Player player1 = new Player();
	Player player2 = new Player();
	
	int count = 0;
	
	//initial addition of places to database
	@PostMapping("/add-places")
	public String addPlaces() {
		Place start = new Place("Start",0,0);
		Place oldKentRoad = new Place("Old Kent Road",60,30);
		Place whitechapelRoad = new Place("Whitechapel Road",60,30);
		Place kingsCrossstation = new Place("King's Cross station",200,100);
		Place theAngelIslington = new Place("The Angel Islington",100,50);
		Place eustonRoad = new Place("Euston Road",100,50);
		Place pentonvilleRoad = new Place("Pentonville Road",120,60);
		Place pallMall= new Place("Pall Mall",140,70);
		Place whitehall = new Place("Whitehall",140,70);
		Place northumberlandAvenue = new Place("Northumberland Avenue",160,80);
		Place maryleboneStation= new Place("Marylebone Station",200,100);
		
		placeRepository.save(start);
		placeRepository.save(oldKentRoad);
		placeRepository.save(whitechapelRoad);
		placeRepository.save(kingsCrossstation);
		placeRepository.save(theAngelIslington);
		placeRepository.save(eustonRoad);
		placeRepository.save(pentonvilleRoad);
		placeRepository.save(pallMall);
		placeRepository.save(whitehall);
		placeRepository.save(northumberlandAvenue);
		placeRepository.save(maryleboneStation);
		
		return "Places Added";
	}
	
	//create game resets, present records of previous players, a fresh start
	@PostMapping("/create-game")
	public String createGame() { 
		count = 0;
		
		player1 = playerRepository.getOne(1);
		player2 = playerRepository.getOne(2);
		
		player1.setLocation(1);
		player2.setLocation(1);
		
		player1.setMoney(1000);
		player2.setMoney(1000);
		
		player1.setName("Player1");
		player2.setName("Player2");
		
		//reset places here todo
		player1.removeAllPlaces();
		player2.removeAllPlaces();
		
		playerRepository.save(player1);
		playerRepository.save(player2);
		
		return "Game created !";
	}
	
	//roll die function
	@PostMapping("roll-die/{pid}")
	public String rollDie(@PathVariable("pid") int pid) {
		Random random = new Random();
		int max = 11;
		int min = 2;
		int dieRoll = min + random.nextInt(max);
		
		Player player = playerRepository.getOne(pid);
		
		int oldLocation = player.getLocation();
		int newLocation = oldLocation + dieRoll;
		
		while(newLocation>11) {
			newLocation = newLocation - max;
			if(newLocation<=11) {
				player.setMoney(player.getMoney()+200);
			}
		}
		
		player.setLocation(newLocation);
		playerRepository.save(player);
		
		int oppPlayerId;
		
		if(pid==1)
			oppPlayerId=2;
		else
			oppPlayerId=1;
		
		Player oppPlayer = playerRepository.getOne(oppPlayerId);
		List <Integer> oppPlayerList = new ArrayList<>();
		for(Place p : oppPlayer.getPlaces()) {
			oppPlayerList.add(p.getId());
		}
		
		List <Integer> playerList = new ArrayList<>();
		for(Place p : player.getPlaces()) {
			playerList.add(p.getId());
		}
		
		//claimed by opposite player
		if(oppPlayerList.contains(newLocation)) {
			count++;
			Place place = placeRepository.getOne(newLocation);
			int newMoney = player.getMoney() - place.getRent();
			player.setMoney(newMoney);
			playerRepository.save(player);
			if(count==50) {
				if(player.getMoney()>oppPlayer.getMoney()) {
					return ("Player1 Wins \nBalance : "+player.getMoney());
				}
				else {
					return ("Player2 Wins \nBalance : "+oppPlayer.getMoney());
				}	
			}
			else {
				if(player.getMoney()>0)
					return ("Die Rolled : "+dieRoll+"\n"+place.getName()+" already claimed  & paid rent for "+place.getRent()+".\nRemaining Balance : "+player.getMoney());
				else
					return("Balance : "+player.getMoney()+"\nYou Lose !");
			}
			
		}
		
		//unclaimed or claimed by player
		if(playerList.contains(newLocation)) {
			//already claimed
			count++;
			Place place = placeRepository.getOne(newLocation);
			playerRepository.save(player);
			if(count==50) {
				if(player.getMoney()>oppPlayer.getMoney()) {
					return ("Player1 Wins \nBalance : "+player.getMoney());
				}
				else {
					return ("Player2 Wins \nBalance : "+oppPlayer.getMoney());
				}	
			}
			else
				return ("Die Rolled : "+dieRoll+"\n"+place.getName()+" is already claimed by you.");
		}
		else {
			//deduct money and claim
			count++;
			Place place = placeRepository.getOne(newLocation);
			int newMoney = player.getMoney() - place.getBuy();
			player.setMoney(newMoney);
			player.addPlace(place);
			playerRepository.save(player);
			if(count==50) {
				if(player.getMoney()>oppPlayer.getMoney()) {
					return ("Player1 Wins \nBalance : "+player.getMoney());
				}
				else {
					return ("Player2 Wins \nBalance : "+oppPlayer.getMoney());
				}	
			}
			else {
				if(player.getMoney()>0)
					return ("Die Rolled : "+dieRoll+"\n"+place.getName()+" claimed  & bought for "+place.getBuy()+".\nRemaining Balance : "+player.getMoney());
				else
					return("Balance : "+player.getMoney()+"\nYou Lose !");
			}
		}
	}
}
