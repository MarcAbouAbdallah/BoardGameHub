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
            Player player1 = new Player();
            player1.setName("player1");
            player1.setEmail("player1@example.com");
            player1.setPassword("password");
            player1 = playerRepository.save(player1);
            
            Player player2 = new Player();
            player2.setName("player2");
            player2.setEmail("player2@example.com");
            player2.setPassword("password");
            player2 = playerRepository.save(player2);
            
            Player player3 = new Player();
            player3.setName("player3");
            player3.setEmail("player3@example.com");
            player3.setPassword("password");
            player3 = playerRepository.save(player3);
            
            Player alice = new Player();
            alice.setName("alice");
            alice.setEmail("alice@example.com");
            alice.setPassword("password");
            alice = playerRepository.save(alice);
            
            Player bob = new Player();
            bob.setName("bob");
            bob.setEmail("bob@example.com");
            bob.setPassword("password");
            bob = playerRepository.save(bob);
            
            Player charlie = new Player();
            charlie.setName("charlie");
            charlie.setEmail("charlie@example.com");
            charlie.setPassword("password");
            charlie = playerRepository.save(charlie);
        }
        
        // Seed Games if none exist
        if (gameRepository.count() == 0) {
            Game catan = new Game("Catan", 4, 3, "A strategy board game where players collect resources and build settlements.", null);
            catan = gameRepository.save(catan);
            
            Game risk = new Game("Risk", 6, 2, "A game of global domination where players strategize to conquer territories.", null);
            risk = gameRepository.save(risk);
            
            Game ticketToRide = new Game("Ticket to Ride", 5, 2, "A railway-themed board game where players build train routes across a map.", null);
            ticketToRide = gameRepository.save(ticketToRide);
            
            Game carcassonne = new Game("Carcassonne", 5, 2, "A tile-placement game where players create medieval landscapes and claim features.", null);
            carcassonne = gameRepository.save(carcassonne);
            
            Game codenames = new Game("Codenames", 8, 2, "A word game where players try to find their team's agents using one-word clues.", null);
            codenames = gameRepository.save(codenames);
            
            Game pandemic = new Game("Pandemic", 4, 2, "A cooperative game where players work together to stop global disease outbreaks.", null);
            pandemic = gameRepository.save(pandemic);
            
            Game splendor = new Game("Splendor", 4, 2, "A resource-management game where players collect gems to buy developments.", null);
            splendor = gameRepository.save(splendor);
            
            Game dixit = new Game("Dixit", 6, 3, "A storytelling game where players use abstract illustrations to convey clues.", null);
            dixit = gameRepository.save(dixit);
        }
        
        // Seed Game Copies if none exist
        if (gameCopyRepository.count() == 0) {
            // For Catan, assign a copy to player1
            Game catan = gameRepository.findById(1).orElse(null);
            Player player1 = playerRepository.findById(1).orElse(null);
            if (catan != null && player1 != null) {
                GameCopy copy1 = new GameCopy();
                copy1.setIsAvailable(true);
                copy1.setGame(catan);
                copy1.setOwner(player1);
                gameCopyRepository.save(copy1);
            }
            
            // For Risk, assign a copy to player2
            Game risk = gameRepository.findById(2).orElse(null);
            Player player2 = playerRepository.findById(2).orElse(null);
            if (risk != null && player2 != null) {
                GameCopy copy2 = new GameCopy();
                copy2.setIsAvailable(true);
                copy2.setGame(risk);
                copy2.setOwner(player2);
                gameCopyRepository.save(copy2);
            }
            
            // For Ticket to Ride, assign a copy to player3
            Game ticketToRide = gameRepository.findById(3).orElse(null);
            Player player3 = playerRepository.findById(3).orElse(null);
            if (ticketToRide != null && player3 != null) {
                GameCopy copy3 = new GameCopy();
                copy3.setIsAvailable(true);
                copy3.setGame(ticketToRide);
                copy3.setOwner(player3);
                gameCopyRepository.save(copy3);
            }
            
            // For Carcassonne, assign a copy to alice (player id 4)
            Game carcassonne = gameRepository.findById(4).orElse(null);
            Player alice = playerRepository.findById(4).orElse(null);
            if (carcassonne != null && alice != null) {
                GameCopy copy4 = new GameCopy();
                copy4.setIsAvailable(true);
                copy4.setGame(carcassonne);
                copy4.setOwner(alice);
                gameCopyRepository.save(copy4);
            }
            
            // For Codenames, assign a copy to bob (player id 5)
            Game codenames = gameRepository.findById(5).orElse(null);
            Player bob = playerRepository.findById(5).orElse(null);
            if (codenames != null && bob != null) {
                GameCopy copy5 = new GameCopy();
                copy5.setIsAvailable(true);
                copy5.setGame(codenames);
                copy5.setOwner(bob);
                gameCopyRepository.save(copy5);
            }
            
            // For Pandemic, assign a copy to charlie (player id 6)
            Game pandemic = gameRepository.findById(6).orElse(null);
            Player charlie = playerRepository.findById(6).orElse(null);
            if (pandemic != null && charlie != null) {
                GameCopy copy6 = new GameCopy();
                copy6.setIsAvailable(true);
                copy6.setGame(pandemic);
                copy6.setOwner(charlie);
                gameCopyRepository.save(copy6);
            }
        }

        // Seed Events if none exist
        if (eventRepository.count() == 0) {
            // Event 1: Organizer = player1, using the first game copy
            Player organizer1 = playerRepository.findById(1).orElse(null);
            Iterable<GameCopy> allCopies = gameCopyRepository.findAll();
            GameCopy sampleCopy = StreamSupport.stream(allCopies.spliterator(), false)
                    .findFirst().orElse(null);
            Event event1 = null;
            if (organizer1 != null && sampleCopy != null) {
                event1 = new Event();
                event1.setName("Sample Event for " + sampleCopy.getGame().getName());
                event1.setLocation("Main Hall");
                event1.setDescription("Join us for an exciting session of " + sampleCopy.getGame().getName() + "!");
                event1.setDate(Date.valueOf(LocalDate.now().plusDays(7)));
                event1.setStartTime(Time.valueOf("18:00:00"));
                event1.setEndTime(Time.valueOf("20:00:00"));
                event1.setMaxParticipants(10);
                event1.setOrganizer(organizer1);
                event1.setGame(sampleCopy);
                event1 = eventRepository.save(event1);
            }

            // Event 2: Organizer = player2, using the second game copy
            Player organizer2 = playerRepository.findById(2).orElse(null);
            Iterable<GameCopy> allCopies2 = gameCopyRepository.findAll();
            GameCopy sampleCopy2 = StreamSupport.stream(allCopies2.spliterator(), false)
                    .skip(1).findFirst().orElse(null);
            Event event2 = null;
            if (organizer2 != null && sampleCopy2 != null) {
                event2 = new Event();
                event2.setName("Sample Event 2 for " + sampleCopy2.getGame().getName());
                event2.setLocation("Main Hall 2");
                event2.setDescription("Join us for an exciting session of " + sampleCopy2.getGame().getName() + "!");
                event2.setDate(Date.valueOf(LocalDate.now().plusDays(10)));
                event2.setStartTime(Time.valueOf("19:00:00"));
                event2.setEndTime(Time.valueOf("21:00:00"));
                event2.setMaxParticipants(8);
                event2.setOrganizer(organizer2);
                event2.setGame(sampleCopy2);
                event2 = eventRepository.save(event2);
            }
            
            // Event 3: Organizer = alice (player id 4), using the third game copy
            Player organizer3 = playerRepository.findById(4).orElse(null);
            Iterable<GameCopy> allCopies3 = gameCopyRepository.findAll();
            GameCopy sampleCopy3 = StreamSupport.stream(allCopies3.spliterator(), false)
                    .skip(2).findFirst().orElse(null);
            Event event3 = null;
            if (organizer3 != null && sampleCopy3 != null) {
                event3 = new Event();
                event3.setName("Sample Event 3 for " + sampleCopy3.getGame().getName());
                event3.setLocation("Conference Room");
                event3.setDescription("Don't miss out on a fun session of " + sampleCopy3.getGame().getName() + "!");
                event3.setDate(Date.valueOf(LocalDate.now().plusDays(5)));
                event3.setStartTime(Time.valueOf("17:00:00"));
                event3.setEndTime(Time.valueOf("19:00:00"));
                event3.setMaxParticipants(12);
                event3.setOrganizer(organizer3);
                event3.setGame(sampleCopy3);
                event3 = eventRepository.save(event3);
            }
        }
        
        // Seed Registrations if none exist
        if (registrationRepository.count() == 0) {
            // For Event 1, register player2 and alice
            Event event1 = eventRepository.findById(1).orElse(null);
            Player player2 = playerRepository.findById(2).orElse(null);
            Player alice = playerRepository.findById(4).orElse(null);
            if (event1 != null && player2 != null) {
                Registration reg1 = new Registration(new Registration.Key(player2, event1));
                registrationRepository.save(reg1);
            }
            if (event1 != null && alice != null) {
                Registration reg2 = new Registration(new Registration.Key(alice, event1));
                registrationRepository.save(reg2);
            }
            
            // For Event 2, register player1 and bob
            Event event2 = eventRepository.findById(2).orElse(null);
            Player player1 = playerRepository.findById(1).orElse(null);
            Player bob = playerRepository.findById(5).orElse(null);
            if (event2 != null && player1 != null) {
                Registration reg3 = new Registration(new Registration.Key(player1, event2));
                registrationRepository.save(reg3);
            }
            if (event2 != null && bob != null) {
                Registration reg4 = new Registration(new Registration.Key(bob, event2));
                registrationRepository.save(reg4);
            }
            
            // For Event 3, register charlie and player3
            Event event3 = eventRepository.findById(3).orElse(null);
            Player charlie = playerRepository.findById(6).orElse(null);
            Player player3 = playerRepository.findById(3).orElse(null);
            if (event3 != null && charlie != null) {
                Registration reg5 = new Registration(new Registration.Key(charlie, event3));
                registrationRepository.save(reg5);
            }
            if (event3 != null && player3 != null) {
                Registration reg6 = new Registration(new Registration.Key(player3, event3));
                registrationRepository.save(reg6);
            }
        }
    }
}