package ca.mcgill.ecse321.boardgamehub.model;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;

import java.time.LocalDate;
import java.sql.Time;
import java.sql.Date;
import java.util.stream.StreamSupport;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameCopyRepository gameCopyRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Seed Players if none exist
        if (playerRepository.count() == 0) {
            Player john = new Player();
            john.setName("John Smith");
            john.setEmail("john.smith@example.com");
            john.setPassword("password");
            john = playerRepository.save(john);

            Player jane = new Player();
            jane.setName("Jane Doe");
            jane.setEmail("jane.doe@example.com");
            jane.setPassword("password");
            jane = playerRepository.save(jane);

            Player michael = new Player();
            michael.setName("Michael Johnson");
            michael.setEmail("michael.johnson@example.com");
            michael.setPassword("password");
            michael = playerRepository.save(michael);

            Player laura = new Player();
            laura.setName("Laura Williams");
            laura.setEmail("laura.williams@example.com");
            laura.setPassword("password");
            laura = playerRepository.save(laura);

            Player alice = new Player();
            alice.setName("Alice Brown");
            alice.setEmail("alice.brown@example.com");
            alice.setPassword("password");
            alice = playerRepository.save(alice);

            Player bob = new Player();
            bob.setName("Bob Miller");
            bob.setEmail("bob.miller@example.com");
            bob.setPassword("password");
            bob = playerRepository.save(bob);

            Player charlie = new Player();
            charlie.setName("Charlie Davis");
            charlie.setEmail("charlie.davis@example.com");
            charlie.setPassword("password");
            charlie = playerRepository.save(charlie);

            Player olivia = new Player();
            olivia.setName("Olivia Wilson");
            olivia.setEmail("olivia.wilson@example.com");
            olivia.setPassword("password");
            olivia = playerRepository.save(olivia);
        }
        
        // Seed Games if none exist
        if (gameRepository.count() == 0) {
            Game catan = new Game("Catan", 4, 3, "...", null);
            gameRepository.save(catan);

            Game risk = new Game("Risk", 6, 2, "...", null);
            gameRepository.save(risk);

            Game terraformingMars = new Game("Terraforming Mars", 5, 1, "...", null);
            gameRepository.save(terraformingMars);

            Game monopoly = new Game("Monopoly", 8, 2, "...", null);
            gameRepository.save(monopoly);

            Game gloomhaven = new Game("Gloomhaven", 4, 2, "...", null);
            gameRepository.save(gloomhaven);

            Game ticketToRide = new Game("Ticket to Ride", 5, 2, "...", null);
            gameRepository.save(ticketToRide);

            Game codenames = new Game("Codenames", 8, 2, "...", null);
            gameRepository.save(codenames);

            Game pandemic = new Game("Pandemic", 4, 2, "...", null);
            gameRepository.save(pandemic);
        }
        
        // Seed Game Copies if none exist
        if (gameCopyRepository.count() == 0) {
            Game catan = gameRepository.findById(1).orElse(null);
            Player john = playerRepository.findById(1).orElse(null);
            if (catan != null && john != null) {
                GameCopy copy1 = new GameCopy();
                copy1.setIsAvailable(true);
                copy1.setGame(catan);
                copy1.setOwner(john);
                gameCopyRepository.save(copy1);
            }

            Game risk = gameRepository.findById(2).orElse(null);
            Player jane = playerRepository.findById(2).orElse(null);
            if (risk != null && jane != null) {
                GameCopy copy2 = new GameCopy();
                copy2.setIsAvailable(true);
                copy2.setGame(risk);
                copy2.setOwner(jane);
                gameCopyRepository.save(copy2);
            }

            Game gloomhaven = gameRepository.findById(5).orElse(null);
            Player laura = playerRepository.findById(4).orElse(null);
            if (gloomhaven != null && laura != null) {
                GameCopy copy3 = new GameCopy();
                copy3.setIsAvailable(true);
                copy3.setGame(gloomhaven);
                copy3.setOwner(laura);
                gameCopyRepository.save(copy3);
            }
        }

        // Seed Events if none exist
        if (eventRepository.count() == 0) {
            Player eventOrg1 = playerRepository.findById(1).orElse(null); // John
            GameCopy copyForEvent1 = gameCopyRepository.findById(1).orElse(null);
            if (eventOrg1 != null && copyForEvent1 != null) {
                Event event1 = new Event();
                event1.setName("John's Game Night");
                event1.setLocation("Community Center");
                event1.setDescription("Join John for an epic session of " + copyForEvent1.getGame().getName());
                event1.setDate(Date.valueOf(LocalDate.now().plusDays(5)));
                event1.setStartTime(Time.valueOf("18:00:00"));
                event1.setEndTime(Time.valueOf("20:00:00"));
                event1.setMaxParticipants(10);
                event1.setOrganizer(eventOrg1);
                event1.setGame(copyForEvent1);
                eventRepository.save(event1);
            }

            Player eventOrg2 = playerRepository.findById(2).orElse(null); // Jane
            GameCopy copyForEvent2 = gameCopyRepository.findById(2).orElse(null);
            if (eventOrg2 != null && copyForEvent2 != null) {
                Event event2 = new Event();
                event2.setName("Jane's Strategy Meetup");
                event2.setLocation("Conference Room B");
                event2.setDescription("Come strategize with Jane playing " + copyForEvent2.getGame().getName());
                event2.setDate(Date.valueOf(LocalDate.now().plusDays(7)));
                event2.setStartTime(Time.valueOf("18:30:00"));
                event2.setEndTime(Time.valueOf("21:30:00"));
                event2.setMaxParticipants(8);
                event2.setOrganizer(eventOrg2);
                event2.setGame(copyForEvent2);
                eventRepository.save(event2);
            }

            Player eventOrg3 = playerRepository.findById(4).orElse(null); // Laura
            GameCopy copyForEvent3 = gameCopyRepository.findById(3).orElse(null);
            if (eventOrg3 != null && copyForEvent3 != null) {
                Event event3 = new Event();
                event3.setName("Laura's Gloomhaven Evening");
                event3.setLocation("Board Game Cafe");
                event3.setDescription("Tackle dungeons and monsters in Gloomhaven with Laura!");
                event3.setDate(Date.valueOf(LocalDate.now().plusDays(10)));
                event3.setStartTime(Time.valueOf("19:00:00"));
                event3.setEndTime(Time.valueOf("23:00:00"));
                event3.setMaxParticipants(4);
                event3.setOrganizer(eventOrg3);
                event3.setGame(copyForEvent3);
                eventRepository.save(event3);
            }
        }
        
        // Seed Registrations if none exist
        if (registrationRepository.count() == 0) {
            Event firstEvent = eventRepository.findById(1).orElse(null);
            Player jane = playerRepository.findById(2).orElse(null);
            Player laura = playerRepository.findById(4).orElse(null);
            if (firstEvent != null && jane != null) {
                Registration reg1 = new Registration(new Registration.Key(jane, firstEvent));
                registrationRepository.save(reg1);
            }
            if (firstEvent != null && laura != null) {
                Registration reg2 = new Registration(new Registration.Key(laura, firstEvent));
                registrationRepository.save(reg2);
            }

            Event secondEvent = eventRepository.findById(2).orElse(null);
            Player john = playerRepository.findById(1).orElse(null);
            if (secondEvent != null && john != null) {
                Registration reg3 = new Registration(new Registration.Key(john, secondEvent));
                registrationRepository.save(reg3);
            }
        }
    }
}