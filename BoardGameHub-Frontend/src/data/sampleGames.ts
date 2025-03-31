export const games = [
  {
    id: 1,
    name: "Catan",
    description: "A strategy board game where players collect resources and build settlements.",
    maxPlayers: 4,
    minPlayers: 3,
  },
  {
    id: 2,
    name: "Risk",
    description: "A game of global domination where players strategize to conquer territories.",
    maxPlayers: 6,
    minPlayers: 2,
  },
  {
    id: 3,
    name: "Ticket to Ride",
    description: "A railway-themed board game where players build train routes across a map.",
    maxPlayers: 5,
    minPlayers: 2,
  },
  {
    id: 4,
    name: "Carcassonne",
    description:
      "A tile-placement game where players create medieval landscapes and claim features.",
    maxPlayers: 5,
    minPlayers: 2,
  },
  {
    id: 5,
    name: "Codenames",
    description: "A word game where players try to find their team's agents using one-word clues.",
    maxPlayers: 8,
    minPlayers: 2,
  },
  {
    id: 6,
    name: "Pandemic",
    description: "A cooperative game where players work together to stop global disease outbreaks.",
    maxPlayers: 4,
    minPlayers: 2,
  },
  {
    id: 7,
    name: "Splendor",
    description: "A resource-management game where players collect gems to buy developments.",
    maxPlayers: 4,
    minPlayers: 2,
  },
  {
    id: 8,
    name: "Dixit",
    description: "A storytelling game where players use abstract illustrations to convey clues.",
    maxPlayers: 6,
    minPlayers: 3,
  },
];

export const gameCopies = [
  { id: 1, name: "player1" },
  { id: 2, name: "player2" },
  { id: 3, name: "player3" },
  { id: 4, name: "alice" },
  { id: 5, name: "bob" },
  { id: 6, name: "charlie" },
];

export const gameReviews = [
  {
    reviewId: 1,
    reviewer: "John Doe",
    reviewText: "Catan is an amazing game with great strategic depth. Highly recommend!",
    game: "Catan",
  },
  {
    reviewId: 2,
    reviewer: "Jane Smith",
    reviewText: "Risk is a classic, but it can take a long time to finish. Still fun though!",
    game: "Risk",
  },
  {
    reviewId: 3,
    reviewer: "Emily Johnson",
    reviewText:
      "Ticket to Ride is easy to learn and super fun for the whole family.Ticket to Ride is easy to learn and super fun for the whole family.Ticket to Ride is easy to learn and super fun for the whole family.",
    game: "Ticket to Ride",
  },
  {
    reviewId: 4,
    reviewer: "Michael Brown",
    reviewText: "Carcassonne is a fantastic game for those who enjoy tile-placement mechanics.",
    game: "Carcassonne",
  },
  {
    reviewId: 5,
    reviewer: "Sarah Davis",
    reviewText: "Codenames is a great party game, especially with a larger group.",
    game: "Codenames",
  },
  {
    reviewId: 6,
    reviewer: "Chris Wilson",
    reviewText: "Pandemic is intense and requires great teamwork. A must-play cooperative game.",
    game: "Pandemic",
  },
  {
    reviewId: 7,
    reviewer: "Laura Martinez",
    reviewText: "Splendor is a simple yet addictive game. Perfect for quick sessions.",
    game: "Splendor",
  },
  {
    reviewId: 8,
    reviewer: "David Lee",
    reviewText:
      "Dixit is a beautiful game with creative storytelling. Great for imaginative players.",
    game: "Dixit",
  },
];

export const sampleGameCollection = [
  {
    id: 9,
    name: "Azul",
    description: "A tile-drafting game where players decorate the walls of a royal palace.",
    maxPlayers: 4,
    minPlayers: 2,
    isBorrowed: true,
    owner: "Jerome",
  },
  {
    id: 10,
    name: "7 Wonders",
    description: "A card-drafting game where players build civilizations and compete for points.",
    maxPlayers: 7,
    minPlayers: 3,
    isBorrowed: false,
    owner: "Mubeen",
    borrowRequests: [
      {
        user: "Alice",
        startDate: "2023-11-01",
        endDate: "2023-11-07",
        comment: "Looking forward to trying this game!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
    ],
  },
  {
    id: 11,
    name: "Terraforming Mars",
    description:
      "A strategy game where players work to make Mars habitable while competing for resources.",
    maxPlayers: 5,
    minPlayers: 1,
    isBorrowed: false,
    owner: "Mubeen",
    borrowRequests: [
      {
        user: "Bob",
        startDate: "2023-11-05",
        endDate: "2023-11-12",
        comment: "Heard great things about this game!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
    ],
  },
  {
    id: 12,
    name: "Terraforming Mars",
    description:
      "A strategy game where players work to make Mars habitable while competing for resources.",
    maxPlayers: 5,
    minPlayers: 1,
    isBorrowed: true,
    owner: "Marc",
  },
  {
    id: 13,
    name: "Terraforming Mars",
    description:
      "A strategy game where players work to make Mars habitable while competing for resources.",
    maxPlayers: 5,
    minPlayers: 1,
    isBorrowed: false,
    owner: "Mubeen",
    borrowRequests: [
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
      {
        user: "Charlie",
        startDate: "2023-11-10",
        endDate: "2023-11-15",
        comment: "Excited to play this with friends!",
      },
    ],
  },
  {
    id: 14,
    name: "Terraforming Mars",
    description:
      "A strategy game where players work to make Mars habitable while competing for resources.",
    maxPlayers: 5,
    minPlayers: 1,
    isBorrowed: true,
    owner: "Deon",
  },
];
