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
            //GameCopy pandemicCopy = gameCopyRepository.findById(5).orElse(null); // Owned by Alice
            
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
        
        // Add more Game Copies for John (as the demo account)
        Player john = playerRepository.findById(1).orElse(null);
        if (john != null) {
            Game ticketToRide = gameRepository.findById(6).orElse(null);
            if (ticketToRide != null) {
                GameCopy johnsCopy1 = new GameCopy();
                johnsCopy1.setGame(ticketToRide);
                johnsCopy1.setOwner(john);
                gameCopyRepository.save(johnsCopy1);
            }
            
            Game codenames = gameRepository.findById(7).orElse(null);
            if (codenames != null) {
                GameCopy johnsCopy2 = new GameCopy();
                johnsCopy2.setGame(codenames);
                johnsCopy2.setOwner(john);
                gameCopyRepository.save(johnsCopy2);
            }
            
            Game pandemic = gameRepository.findById(8).orElse(null);
            if (pandemic != null) {
                GameCopy johnsCopy3 = new GameCopy();
                johnsCopy3.setGame(pandemic);
                johnsCopy3.setOwner(john);
                gameCopyRepository.save(johnsCopy3);
            }
        }
        
        // Add more Events organized by John
        if (john != null) {
            GameCopy johnsCatanCopy = gameCopyRepository.findById(1).orElse(null);
            if (johnsCatanCopy != null) {
                Event johnsEvent1 = new Event();
                johnsEvent1.setName("Catan Tournament");
                johnsEvent1.setLocation("John's Place");
                johnsEvent1.setDescription("Join us for a competitive Catan tournament with prizes!");
                johnsEvent1.setDate(Date.valueOf(LocalDate.now().plusDays(14)));
                johnsEvent1.setStartTime(Time.valueOf("19:00:00"));
                johnsEvent1.setEndTime(Time.valueOf("23:00:00"));
                johnsEvent1.setMaxParticipants(16);
                johnsEvent1.setOrganizer(john);
                johnsEvent1.setGame(johnsCatanCopy);
                eventRepository.save(johnsEvent1);
            }
            
            GameCopy johnsTicketToRideCopy = gameCopyRepository.findByOwner(john).stream()
                .filter(copy -> copy.getGame().getName().equals("Ticket to Ride"))
                .findFirst().orElse(null);
            
            if (johnsTicketToRideCopy != null) {
                Event johnsEvent2 = new Event();
                johnsEvent2.setName("Ticket to Ride Night");
                johnsEvent2.setLocation("Community Library");
                johnsEvent2.setDescription("Learn and play Ticket to Ride with John! Beginners welcome.");
                johnsEvent2.setDate(Date.valueOf(LocalDate.now().plusDays(21)));
                johnsEvent2.setStartTime(Time.valueOf("18:00:00"));
                johnsEvent2.setEndTime(Time.valueOf("21:00:00"));
                johnsEvent2.setMaxParticipants(8);
                johnsEvent2.setOrganizer(john);
                johnsEvent2.setGame(johnsTicketToRideCopy);
                eventRepository.save(johnsEvent2);
            }
            
            GameCopy johnsCodenamesCopy = gameCopyRepository.findByOwner(john).stream()
                .filter(copy -> copy.getGame().getName().equals("Codenames"))
                .findFirst().orElse(null);
                
            if (johnsCodenamesCopy != null) {
                Event johnsEvent3 = new Event();
                johnsEvent3.setName("Codenames Party");
                johnsEvent3.setLocation("Board Game Cafe");
                johnsEvent3.setDescription("Team-based word game night! Bring your thinking caps.");
                johnsEvent3.setDate(Date.valueOf(LocalDate.now().plusDays(3)));
                johnsEvent3.setStartTime(Time.valueOf("20:00:00"));
                johnsEvent3.setEndTime(Time.valueOf("22:30:00"));
                johnsEvent3.setMaxParticipants(12);
                johnsEvent3.setOrganizer(john);
                johnsEvent3.setGame(johnsCodenamesCopy);
                eventRepository.save(johnsEvent3);
            }
        }
        
        // Add more Reviews by John
        if (john != null) {
            Game risk = gameRepository.findById(2).orElse(null);
            if (risk != null) {
                Review johnsReview1 = new Review(4, "Risk is a great classic, but can take too long sometimes. Still worth playing!", 
                    Date.valueOf(LocalDate.now().minusDays(25)), john, risk);
                reviewRepository.save(johnsReview1);
            }
            
            Game terraformingMars = gameRepository.findById(3).orElse(null);
            if (terraformingMars != null) {
                Review johnsReview2 = new Review(5, "Absolutely fantastic game with endless replayability. One of the best strategy games I've played.", 
                    Date.valueOf(LocalDate.now().minusDays(12)), john, terraformingMars);
                reviewRepository.save(johnsReview2);
            }
            
            Game ticketToRide = gameRepository.findById(6).orElse(null);
            if (ticketToRide != null) {
                Review johnsReview3 = new Review(5, "Perfect gateway game! Easy to learn but has enough strategy to keep experienced players engaged.", 
                    Date.valueOf(LocalDate.now().minusDays(8)), john, ticketToRide);
                reviewRepository.save(johnsReview3);
            }
            
            Game codenames = gameRepository.findById(7).orElse(null);
            if (codenames != null) {
                Review johnsReview4 = new Review(5, "Great party game that works with any crowd. Always leads to laughs and memorable moments!", 
                    Date.valueOf(LocalDate.now().minusDays(3)), john, codenames);
                reviewRepository.save(johnsReview4);
            }
        }
        
        // Add more Borrow Requests related to John (both as requester and requestee)
        if (john != null) {
            // John as requester
            Player bob = playerRepository.findById(6).orElse(null);
            GameCopy bobsTicketToRideCopy = gameCopyRepository.findByOwner(bob).stream()
                .filter(copy -> copy.getGame().getName().equals("Ticket to Ride"))
                .findFirst().orElse(null);
                
            if (bob != null && bobsTicketToRideCopy != null) {
                BorrowRequest johnsRequest1 = new BorrowRequest(john, bob, bobsTicketToRideCopy,
                    "I'd like to compare the Europe version with my US version. Would be great for my game night!",
                    Date.valueOf(LocalDate.now().plusDays(9)),
                    Date.valueOf(LocalDate.now().plusDays(16)));
                borrowRequestRepository.save(johnsRequest1);
            }
            
            Player charlie = playerRepository.findById(7).orElse(null);
            GameCopy charliesCodenamesCopy = gameCopyRepository.findByOwner(charlie).stream()
                .filter(copy -> copy.getGame().getName().equals("Codenames"))
                .findFirst().orElse(null);
                
            if (charlie != null && charliesCodenamesCopy != null) {
                BorrowRequest johnsRequest2 = new BorrowRequest(john, charlie, charliesCodenamesCopy,
                    "Hosting a big party next weekend and would love to have two Codenames sets to play simultaneously!",
                    Date.valueOf(LocalDate.now().plusDays(12)),
                    Date.valueOf(LocalDate.now().plusDays(15)));
                johnsRequest2.setStatus(BorrowStatus.ACCEPTED);
                borrowRequestRepository.save(johnsRequest2);
            }
            
            // John as requestee (others requesting John's games)
            Player michael = playerRepository.findById(3).orElse(null);
            GameCopy johnsPandemicCopy = gameCopyRepository.findByOwner(john).stream()
                .filter(copy -> copy.getGame().getName().equals("Pandemic"))
                .findFirst().orElse(null);
                
            if (michael != null && johnsPandemicCopy != null) {
                BorrowRequest requestToJohn1 = new BorrowRequest(michael, john, johnsPandemicCopy,
                    "Would love to try this classic cooperative game with my family this weekend.",
                    Date.valueOf(LocalDate.now().plusDays(4)),
                    Date.valueOf(LocalDate.now().plusDays(11)));
                borrowRequestRepository.save(requestToJohn1);
            }
            
            Player alice = playerRepository.findById(5).orElse(null);
            GameCopy johnsCodenamesCopy = gameCopyRepository.findByOwner(john).stream()
                .filter(copy -> copy.getGame().getName().equals("Codenames"))
                .findFirst().orElse(null);
                
            if (alice != null && johnsCodenamesCopy != null) {
                BorrowRequest requestToJohn2 = new BorrowRequest(alice, john, johnsCodenamesCopy,
                    "Having a birthday party and Codenames would be perfect! Promise to take good care of it.",
                    Date.valueOf(LocalDate.now().plusDays(8)),
                    Date.valueOf(LocalDate.now().plusDays(10)));
                requestToJohn2.setStatus(BorrowStatus.ACCEPTED);
                borrowRequestRepository.save(requestToJohn2);
            }
            
            Player laura = playerRepository.findById(4).orElse(null);
            GameCopy johnsCatanCopy = gameCopyRepository.findById(1).orElse(null);
                
            if (laura != null && johnsCatanCopy != null) {
                BorrowRequest requestToJohn3 = new BorrowRequest(laura, john, johnsCatanCopy,
                    "Want to introduce my niece and nephew to Catan during their visit. Would be much appreciated!",
                    Date.valueOf(LocalDate.now().plusDays(15)),
                    Date.valueOf(LocalDate.now().plusDays(22)));
                requestToJohn3.setStatus(BorrowStatus.DECLINED);
                borrowRequestRepository.save(requestToJohn3);
            }
            
            Player jane = playerRepository.findById(2).orElse(null);
            GameCopy johnsTicketToRideCopy = gameCopyRepository.findByOwner(john).stream()
                .filter(copy -> copy.getGame().getName().equals("Ticket to Ride"))
                .findFirst().orElse(null);
                
            if (jane != null && johnsTicketToRideCopy != null) {
                BorrowRequest completedRequest = new BorrowRequest(jane, john, johnsTicketToRideCopy,
                    "Would love to borrow this for my family reunion last month.",
                    Date.valueOf(LocalDate.now().minusDays(30)),
                    Date.valueOf(LocalDate.now().minusDays(23)));
                completedRequest.setStatus(BorrowStatus.PENDING);
                borrowRequestRepository.save(completedRequest);
            }
        }
        
        // Add some registrations for John's events
        Player jane = playerRepository.findById(2).orElse(null);
        Player michael = playerRepository.findById(3).orElse(null);
        Player laura = playerRepository.findById(4).orElse(null);
        Player alice = playerRepository.findById(5).orElse(null);
        Player bob = playerRepository.findById(6).orElse(null);
        Player charlie = playerRepository.findById(7).orElse(null);
        Player olivia = playerRepository.findById(8).orElse(null);
        // Find John's events
        Event codenamesEvent = StreamSupport.stream(eventRepository.findAll().spliterator(), false)
            .filter(event -> event.getName().equals("Codenames Party"))
            .findFirst().orElse(null);
            
        if (codenamesEvent != null) {
        if (codenamesEvent != null) {
            if (jane != null) registrationRepository.save(new Registration(new Registration.Key(jane, codenamesEvent)));
            if (michael != null) registrationRepository.save(new Registration(new Registration.Key(michael, codenamesEvent)));
            if (laura != null) registrationRepository.save(new Registration(new Registration.Key(laura, codenamesEvent)));
            if (alice != null) registrationRepository.save(new Registration(new Registration.Key(alice, codenamesEvent)));
            if (charlie != null) registrationRepository.save(new Registration(new Registration.Key(charlie, codenamesEvent)));
        }
        Event catanEvent = StreamSupport.stream(eventRepository.findAll().spliterator(), false)
            .filter(event -> event.getName().equals("Catan Tournament"))
            .findFirst().orElse(null);
            
        if (catanEvent != null) {
        if (catanEvent != null) {
            if (jane != null) registrationRepository.save(new Registration(new Registration.Key(jane, catanEvent)));
            if (bob != null) registrationRepository.save(new Registration(new Registration.Key(bob, catanEvent)));
            if (charlie != null) registrationRepository.save(new Registration(new Registration.Key(charlie, catanEvent)));
            if (olivia != null) registrationRepository.save(new Registration(new Registration.Key(olivia, catanEvent)));
        }
    }
}
        System.out.println("Data seeding complete!");
    }
}

