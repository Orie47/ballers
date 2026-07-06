Ballers 🏀

Pickup sports, near you. Find pickup games happening around you, filter by sport, join with one tap, and rate the people you played with.

Ballers is a full-stack web app: a Spring Boot REST API backed by PostgreSQL, and a React frontend with a live map. It was built as a focused portfolio project to demonstrate backend design, relational data modeling, and the kinds of tradeoffs that come up in real systems (proximity search, concurrency, and query performance).


What it does


Browse a location-aware list of nearby games, sorted nearest-first.
Filter games by sport (basketball, soccer, tennis, volleyball).
See every game as a pin on an interactive map.
Join a game — with a hard rule that a full game can't be over-filled.
Post your own game and say how many players you still need.
Rate the players you played with, feeding into their profile rating.



Architecture

┌──────────────┐        HTTP / JSON        ┌──────────────┐       JPA / SQL       ┌──────────────┐
│   Frontend   │  ───────────────────────► │   Backend    │  ───────────────────► │   Database   │
│              │                           │              │                       │              │
│ React + Vite │  ◄─────────────────────── │ Spring Boot  │  ◄─────────────────── │ PostgreSQL   │
│  + Leaflet   │        JSON responses     │  REST API    │        result sets    │              │
└──────────────┘                           └──────────────┘                       └──────────────┘

Inside the backend, each request flows through a clean three-layer separation:

Controller  →  Service  →  Repository  →  Database
 (HTTP)        (rules)      (data access)

DTOs sit at the boundary so the API never exposes JPA entities directly.


Tech stack

LayerTechnologyFrontendReact, Vite, Leaflet (OpenStreetMap), axios, Tailwind CSSBackendJava, Spring Boot, Spring Web, Spring Data JPA (Hibernate), Bean ValidationDatabasePostgreSQL (run via Docker)Build / toolingMaven (backend), npm (frontend)API docsspringdoc-openapi (Swagger UI)


Data model

Four core tables and their relationships:


users — id, username, gender, height_cm, skill_level, rating, lat, lng
games — id, sport, lat, lng, start_time, players_needed, host_id, version
game_participants — game_id, user_id, joined_at (join table)
ratings — id, rater_id, rated_id, game_id, score


Relationships:


A game has one host (games.host_id → users.id) — many-to-one.
A game has many players and a player joins many games — many-to-many, resolved through the game_participants join table.
A rating links a rater, a rated player, and the game it happened in.



API

MethodEndpointDescriptionPOST/api/usersCreate a userGET/api/users/{id}Get a user's profilePOST/api/gamesPost a new gameGET/api/games?sport=&lat=&lng=&radius=&page=List nearby games, filtered and sorted by distancePOST/api/games/{id}/joinJoin a game (returns 409 if full)POST/api/ratingsRate a player

Interactive docs are available at /swagger-ui.html when the backend is running.


Running locally

Prerequisites


Java 21+
Node.js 20+
Docker Desktop


1. Start the database

bashdocker run --name ballers-db \
  -e POSTGRES_PASSWORD=pickup123 \
  -e POSTGRES_DB=ballers \
  -p 5432:5432 \
  -d postgres:16

(On later runs, just docker start ballers-db.)

2. Start the backend

bashcd backend
DB_PASSWORD=pickup123 ./mvnw spring-boot:run

The API comes up on http://localhost:8080. Swagger UI: http://localhost:8080/swagger-ui.html.
On first launch, the app seeds ~20 sample users and ~20 games around Tel Aviv so the map isn't empty.


The database password is passed in as an environment variable and is never committed to the repo. See backend/.env.example for the expected variables.



3. Start the frontend

bashcd frontend
npm install
npm run dev

Open http://localhost:5173.


Design decisions & tradeoffs

This section captures the interesting engineering choices — the "why," not just the "what."

Proximity search: bounding box + Haversine

Finding nearby games is the app's core query and its main performance concern. A naive approach computes the exact distance to every game in the database on each request — an O(n) full-table scan that's fine at small scale but degrades as the dataset grows.

Instead, the query runs in two stages: a cheap bounding-box filter in the SQL WHERE clause (lat BETWEEN ? AND ? / lng BETWEEN ? AND ?) narrows the candidate set using indexed columns, and only that small subset gets the precise Haversine distance calculation and sort in Java.

How I'd scale it further: a spatial extension like PostGIS with a GiST index, geohashing, or a dedicated geo-search engine (e.g. Elasticsearch geo queries).

Concurrency: optimistic locking on join

Two people trying to grab the last spot at the same moment is a classic race condition — both could read "1 spot left" and both write, over-filling the game. The Game entity carries a @Version field, so Hibernate performs an optimistic-lock check: the first write succeeds and bumps the version; the second write's version check fails, the transaction is rejected, and that user cleanly receives a 409 Conflict instead of a corrupted game.

Alternatives considered: pessimistic locking (SELECT ... FOR UPDATE) or a database check constraint. Optimistic locking is the right fit here because contention on any single game is low.

Query performance: fixing the N+1 problem

Listing games initially triggered an N+1 query pattern — one query for the games, then a separate query per game to load its participants (and per participant for ratings). This was fixed with JOIN FETCH / entity graphs so the related data loads in a single query, which dropped the query count dramatically under load.

DTOs at the API boundary

The API returns DTOs rather than JPA entities. This avoids lazy-loading serialization errors, prevents over-exposing internal fields, and decouples the API contract from the database schema so the two can evolve independently.

Read-heavy by nature

Users browse games far more often than they create them, so the system is heavily read-oriented. Natural next steps for scale would be caching hot game-list queries (e.g. Redis), read replicas, and cursor-based pagination.


With more time


Authentication (JWT) and real user accounts
Real device geolocation instead of a fixed origin point
Live updates via WebSockets when a game fills up
Database migrations (Flyway) instead of Hibernate auto-DDL
Automated tests (unit + integration)
docker-compose for one-command startup of the whole stack
CI/CD pipeline
