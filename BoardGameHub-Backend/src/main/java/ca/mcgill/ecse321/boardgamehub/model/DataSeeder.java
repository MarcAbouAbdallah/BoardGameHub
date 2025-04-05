package ca.mcgill.ecse321.boardgamehub.model;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardgamehub.repo.ReviewRepository;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;

import java.time.LocalDate;
import java.sql.Time;
import java.sql.Date;
import java.util.stream.StreamSupport;

@Component
public class DataSeeder implements CommandLineRunner {

    // Flag to control whether data should be seeded
    private final boolean shouldSeedData = true;  // Set to false to disable seeding

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
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BorrowRequestRepository borrowRequestRepository;
    

    @Override
    public void run(String... args) throws Exception {
        // Only proceed with seeding if the flag is set to true
        if (!shouldSeedData) {
            System.out.println("Data seeding is disabled");
            return;
        }

        System.out.println("Starting to seed data...");
        System.out.println(playerRepository.count());
        System.out.println(gameRepository.count());
        System.out.println(gameCopyRepository.count());
        System.out.println(eventRepository.count());
        System.out.println(registrationRepository.count());
        System.out.println(reviewRepository.count());
        System.out.println(borrowRequestRepository.count());

        
        // Seed Players if none exist
        if (playerRepository.count() == 0) {
            Player john = new Player();
            john.setName("John Smith");
            john.setEmail("john.smith@test.com");
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
            Game catan = new Game("Catan", 4, 3, "A strategy game where players collect resources and build settlements to earn points.", "https://www.calendarclub.ca/cdn/shop/files/EB0D1F84-48D3-48C1-890B-3879B397011A_5926567e-0a4e-471c-8af1-f7b64bbfdf0f.jpg?v=1727169556&width=2048");
            gameRepository.save(catan);

            Game risk = new Game("Risk", 6, 2, "A classic game of global domination where players conquer territories through strategic battles.", "https://assetsio.gnwcdn.com/risk-board-game-layout.jpg?width=1200&height=1200&fit=bounds&quality=70&format=jpg&auto=webp");
            gameRepository.save(risk);

            Game terraformingMars = new Game("Terraforming Mars", 5, 1, "A sci-fi strategy game where players work to make Mars habitable by managing resources and projects.", "https://i5.walmartimages.com/asr/b0b87b20-865c-48e2-81d2-70d9d836089a_1.9e2be516e00b0604a255a3050796a4d6.jpeg?odnHeight=768&odnWidth=768&odnBg=FFFFFF");
            gameRepository.save(terraformingMars);

            Game monopoly = new Game("Monopoly", 8, 2, "A classic real estate trading game where players buy, sell, and trade properties to bankrupt opponents.", "https://cdn.shoplw.ca/images/2022/06/650-0701-2.jpeg");
            gameRepository.save(monopoly);

            Game gloomhaven = new Game("Gloomhaven", 4, 2, "A cooperative dungeon-crawling adventure game with tactical combat and a branching storyline.", "https://m.media-amazon.com/images/I/81ZWsTe5YHL.jpg");
            gameRepository.save(gloomhaven);

            Game ticketToRide = new Game("Ticket to Ride", 5, 2, "A railway-themed board game where players collect cards to claim train routes across a map.", "https://i.ebayimg.com/images/g/~0UAAOSwXlNhUz9u/s-l1200.jpg");
            gameRepository.save(ticketToRide);

            Game codenames = new Game("Codenames", 8, 2, "A word association game where players give clues to help their team guess the correct words.", "https://i5.walmartimages.com/asr/71eb2489-1cc9-4f4e-aee0-2f9a3226bc88.78ee7e533ef6f388e53b67246b2a8975.png?odnHeight=768&odnWidth=768&odnBg=FFFFFF");
            gameRepository.save(codenames);

            Game pandemic = new Game("Pandemic", 4, 2, "A cooperative game where players work together to stop the spread of diseases and save the world.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLqv0R_SNtkXRG22ZmCE6F0GJNgIcMqcNE98341qM46BExtX-eCk1ubUlzeFx1lOC9JKo&usqp=CAU");
            gameRepository.save(pandemic);
        }
        
        // Seed Game Copies if none exist
        if (gameCopyRepository.count() == 0) {
            Game catan = gameRepository.findById(1).orElse(null);
            Player john = playerRepository.findById(1).orElse(null);
            if (catan != null && john != null) {
                GameCopy copy1 = new GameCopy();
                copy1.setGame(catan);
                copy1.setOwner(john);
                gameCopyRepository.save(copy1);
            }

            Game risk = gameRepository.findById(2).orElse(null);
            Player jane = playerRepository.findById(2).orElse(null);
            if (risk != null && jane != null) {
                GameCopy copy2 = new GameCopy();
                copy2.setGame(risk);
                copy2.setOwner(jane);
                gameCopyRepository.save(copy2);
            }

            Game gloomhaven = gameRepository.findById(5).orElse(null);
            Player laura = playerRepository.findById(4).orElse(null);
            if (gloomhaven != null && laura != null) {
                GameCopy copy3 = new GameCopy();
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
        
        // Seed more Game Copies if only a few exist
        if (gameCopyRepository.count() <= 3) {
            // Add more copies of existing games
            
            Game terraformingMars = gameRepository.findById(3).orElse(null);
            Player michael = playerRepository.findById(3).orElse(null);
            if (terraformingMars != null && michael != null) {
                GameCopy copy4 = new GameCopy();
                copy4.setGame(terraformingMars);
                copy4.setOwner(michael);
                gameCopyRepository.save(copy4);
            }
            
            Game pandemic = gameRepository.findById(8).orElse(null);
            Player alice = playerRepository.findById(5).orElse(null);
            if (pandemic != null && alice != null) {
                GameCopy copy5 = new GameCopy();
                copy5.setGame(pandemic);
                copy5.setOwner(alice);
                gameCopyRepository.save(copy5);
            }
            
            Game ticketToRide = gameRepository.findById(6).orElse(null);
            Player bob = playerRepository.findById(6).orElse(null);
            if (ticketToRide != null && bob != null) {
                GameCopy copy6 = new GameCopy();
                copy6.setGame(ticketToRide);
                copy6.setOwner(bob);
                gameCopyRepository.save(copy6);
            }
            
            Game codenames = gameRepository.findById(7).orElse(null);
            Player charlie = playerRepository.findById(7).orElse(null);
            if (codenames != null && charlie != null) {
                GameCopy copy7 = new GameCopy();
                copy7.setGame(codenames);
                copy7.setOwner(charlie);
                gameCopyRepository.save(copy7);
            }
            
            Game monopoly = gameRepository.findById(4).orElse(null);
            Player olivia = playerRepository.findById(8).orElse(null);
            if (monopoly != null && olivia != null) {
                GameCopy copy8 = new GameCopy();
                copy8.setGame(monopoly);
                copy8.setOwner(olivia);
                gameCopyRepository.save(copy8);
            }
        }
        
        // Seed Reviews if none exist
        if (reviewRepository.count() == 0) {
            // Get some players and games
            Player john = playerRepository.findById(1).orElse(null);
            Player jane = playerRepository.findById(2).orElse(null);
            Player michael = playerRepository.findById(3).orElse(null);
            Player laura = playerRepository.findById(4).orElse(null);
            Player alice = playerRepository.findById(5).orElse(null);
            
            Game catan = gameRepository.findById(1).orElse(null);
            Game risk = gameRepository.findById(2).orElse(null);
            Game terraformingMars = gameRepository.findById(3).orElse(null);
            Game monopoly = gameRepository.findById(4).orElse(null);
            Game pandemic = gameRepository.findById(8).orElse(null);
            
            // Create reviews
            if (john != null && catan != null) {
                Review review1 = new Review(5, "One of my favorite games! The resource management and strategy are fantastic.", Date.valueOf(LocalDate.now().minusDays(15)), john, catan);
                reviewRepository.save(review1);
            }
            
            if (jane != null && catan != null) {
                Review review2 = new Review(4, "Great game but can be frustrating when you don't get the resources you need.", Date.valueOf(LocalDate.now().minusDays(10)), jane, catan);
                reviewRepository.save(review2);
            }
            
            if (michael != null && risk != null) {
                Review review3 = new Review(5, "Risk is a classic! I love the diplomatic elements and long-term strategy.", Date.valueOf(LocalDate.now().minusDays(30)), michael, risk);
                reviewRepository.save(review3);
            }
            
            if (laura != null && terraformingMars != null) {
                Review review4 = new Review(5, "Such an immersive game with so many ways to win. The theme is executed perfectly.", Date.valueOf(LocalDate.now().minusDays(5)), laura, terraformingMars);
                reviewRepository.save(review4);
            }
            
            if (alice != null && monopoly != null) {
                Review review5 = new Review(3, "A classic but can take too long. Fun with the right people though.", Date.valueOf(LocalDate.now().minusDays(45)), alice, monopoly);
                reviewRepository.save(review5);
            }
            
            if (john != null && pandemic != null) {
                Review review6 = new Review(5, "The cooperative gameplay is refreshing. Really challenging but rewarding when you win!", Date.valueOf(LocalDate.now().minusDays(20)), john, pandemic);
                reviewRepository.save(review6);
            }
        }
        
        // Seed Borrow Requests if none exist
        if (borrowRequestRepository.count() == 0) {
            // Get some players and game copies
            Player john = playerRepository.findById(1).orElse(null);
            Player jane = playerRepository.findById(2).orElse(null);
            Player michael = playerRepository.findById(3).orElse(null);
            Player laura = playerRepository.findById(4).orElse(null);
            Player alice = playerRepository.findById(5).orElse(null);
            
            GameCopy catanCopy = gameCopyRepository.findById(1).orElse(null); // Owned by John
            GameCopy riskCopy = gameCopyRepository.findById(2).orElse(null); // Owned by Jane
            GameCopy gloomhavenCopy = gameCopyRepository.findById(3).orElse(null); // Owned by Laura
            GameCopy terraformingMarsCopy = gameCopyRepository.findById(4).orElse(null); // Owned by Michael
            GameCopy pandemicCopy = gameCopyRepository.findById(5).orElse(null); // Owned by Alice
            
            // Create borrow requests with different statuses
            
            // Pending requests
            if (jane != null && john != null && catanCopy != null) {
                BorrowRequest request1 = new BorrowRequest(jane, john, catanCopy, 
                        "I've heard great things about Catan and would love to try it!", 
                        Date.valueOf(LocalDate.now().plusDays(3)), 
                        Date.valueOf(LocalDate.now().plusDays(10)));
                borrowRequestRepository.save(request1);
            }
            
            if (michael != null && jane != null && riskCopy != null) {
                BorrowRequest request2 = new BorrowRequest(michael, jane, riskCopy, 
                        "Planning a game night this weekend, Risk would be perfect!", 
                        Date.valueOf(LocalDate.now().plusDays(5)), 
                        Date.valueOf(LocalDate.now().plusDays(8)));
                borrowRequestRepository.save(request2);
            }
            
            // Approved request
            if (alice != null && laura != null && gloomhavenCopy != null) {
                BorrowRequest request3 = new BorrowRequest(alice, laura, gloomhavenCopy, 
                        "My group wants to start a Gloomhaven campaign.", 
                        Date.valueOf(LocalDate.now().plusDays(7)), 
                        Date.valueOf(LocalDate.now().plusDays(28)));
                request3.setStatus(BorrowStatus.ACCEPTED);
                borrowRequestRepository.save(request3);
            }
            
            // Rejected request
            if (laura != null && michael != null && terraformingMarsCopy != null) {
                BorrowRequest request4 = new BorrowRequest(laura, michael, terraformingMarsCopy, 
                        "Would love to borrow this for my birthday party!", 
                        Date.valueOf(LocalDate.now().plusDays(2)), 
                        Date.valueOf(LocalDate.now().plusDays(4)));
                request4.setStatus(BorrowStatus.DECLINED);
                borrowRequestRepository.save(request4);
            }
            
        }
        System.out.println("Data seeding complete!");
    }
}