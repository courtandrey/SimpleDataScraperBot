# SimpleDataScraperBot

Telegram bot that watches external sites on a per-user schedule and pushes **new** results into the user's chat.

Users describe what they want through a guided dialog (`/add`), then start a polling loop (`/init`). Every cycle scrapes the
configured sources, drops everything the user has already been told about, stores the rest and sends it out. A read-only
GraphQL API exposes the scraped movies and a command audit log.

Spring Boot 3.5 · Java 17 · Maven · MySQL 8.

---

## Usage

| Command | What it does |
| --- | --- |
| `/start` | Registers the user |
| `/add` | Guided dialog that builds one request (source → filters) |
| `/init` | Asks for a latency in minutes, then starts the polling loop for that chat |
| `/stop` | Stops your polling loops — a cycle already in progress runs to the end first |
| `/delete` | Deletes one of your requests, by the id from `/show` |
| `/show` | Lists your requests with their ids |
| `/showAll` | Admin: lists every registered request and its owner |
| `/showAllProcesses` | Admin: lists running loops |
| `/deleteAdmin`, `/stopAdmin` | Admin: delete a request / stop a loop for any chat |

`/add` starts with the source menu:

```
Choose one of avalable scrapers:
1. NLHousing
2. Latvijas Pasts
3. IMDB movie
4. NL Jobs
```

| Source | Asked for | Produces |
| --- | --- | --- |
| NL Housing | region/city, min price, max price, pets allowed, sites | `RentalOffering` (name, url, price, date, city, pets, deposit, status) |
| Latvijas Pasts | parcel reference number | `LatvijasPastsStatus` (reference, general status, status, place, date) |
| IMDB | genre, min votes, country code, release date | `Movie` (name, url, country, duration, rating, release year) |
| NL Jobs | keyword, sites | `JobOffering` (name, url, city, updated at) |

Filter values are passed to the sites as typed. IMDB in particular is case-sensitive: `Horror` and `US` work, `horror`
and `us` do not.

## Scrapers

43 scrapers are registered. Each one is a connector (fetch) + a parser (turn a page into entities) + a
`PageScrapingFunction` configuration (paging), and declares which requests it accepts via `rightScraperToRequest`.

### NL housing — `observer/scraper/core/nlhousing`

The number is the id users type at the "choose websites" step. It is `ordinal() + 1` of `NLHousingSite`, so **new sites
must be appended at the end** — inserting one in the middle renumbers every site after it.

| # | Site | Scraper | # | Site | Scraper |
| --- | --- | --- | --- | --- | --- |
| 1 | Rooms and Houses | `RoomsAndHousesScraper` | 20 | Househunting | `HousehuntingScraper` |
| 2 | Funda | `FundaScraper` | 21 | Hurenbijwooove | `HurenbijwoooveScraper` |
| 3 | Pararius | `ParariusScraper` | 22 | Perfect Rent | `PerfectRentScraper` |
| 4 | Kamernet | `KamernetScraper` | 23 | Rental Rotterdam | `RentalRotterdamScraper` |
| 5 | Housing Anywhere | `HousingAnywhereScraper` | 24 | Rotterdam Wonen | `RotterdamWonenScraper` |
| 6 | Verhuurtbeter | `VerhuurtbeterScraper` | 25 | Tweel Wonen | `TweelWonenScraper` |
| 7 | Wonenbijbouwinvest | `WonenbijbouwinvestScraper` | 26 | Riva Rentals | `RivaRentalsScraper` |
| 8 | Vbtverhuurmakelaars | `VbtverhuurmakelaarsScraper` | 27 | Rotterdam Rental Service | `RotterdamRentalServiceScraper` |
| 9 | Liveresidential | `LivresidentialScraper` | 28 | City Bird | `CityBirdScraper` |
| 10 | Vanderlinden | `VanderlindenScraper` | 29 | Rent an Apartment | `RentAnApartmentScraper` |
| 11 | Shepvastgoedmanagers | `ShepvastgoedmanagersScraper` | 30 | Wonenhartje | `WonenhartjeScraper` |
| 12 | Ikwilhuren | `IkwilhurenScraper` | 31 | Densvastgoed | `DensvastgoedScraper` |
| 13 | Rotterdam Apartments | `RotterdamApartmentsScraper` | 32 | Domica | `DomicaScraper` |
| 14 | Vhpn | `VhpnScraper` | 33 | Max Rental | `MaxRentalScraper` |
| 15 | Kolpavvs | `KolpavvsScraper` | 34 | Viadaan | `ViadaanScraper` |
| 16 | Interhouse | `InterhouseScraper` | 35 | Gapph | `GapphScraper` |
| 17 | Woonzeker | `WoonzekerScraper` | 36 | Maasstadwoningverhuur | `MaasstadwoningverhuurScraper` |
| 18 | Nationaal Grondbezit | `NationaalGrondbesitScraper` | 37 | Vgwgroup | `VgwgroupScraper` |
| 19 | realestate.nl | `RealEstateNLScraper` | | | |

Caveats carried in the enum's display names: Rooms and Houses is very slow and rarely useful, Househunting is an
inefficient scraper, Nationaal Grondbezit ignores city filtering, Kamernet and Housing Anywhere apply a search radius of
their own, and Vanderlinden and Interhouse only cover Rotterdam/Amsterdam.

### NL jobs — `observer/scraper/core/nljob`

Same id rule (`NLJobSite.ordinal() + 1`), same append-only constraint.

| # | Site | Scraper |
| --- | --- | --- |
| 1 | Just Eat Takeaway | `JustEatTakeawayScraper` |
| 2 | Albert Heijn | `AlbertHeijnScraper` |
| 3 | Databricks | `DatabricksScraper` |
| 4 | GoDutch | `GoDutchScraper` |

### Standalone

| Source | Scraper | Endpoint |
| --- | --- | --- |
| Latvijas Pasts | `observer/scraper/core/lv/LatvijasPastsScraper` | `mans.pasts.lv/tracking-api` |
| IMDB | `observer/scraper/core/movie/ImdbScraper` | `caching.graphql.imdb.com` (`AdvancedTitleSearch`) |

### Deprecated

`HHScraper` (hh.ru) and `HabrCareerScraper` (career.habr.com), along with their shared `VacancyScraper` base, are
`@Deprecated`: the bot no longer offers them in `/add` and `ScraperConfig` does not register them, so existing vacancy
requests in the database are never scraped. Neither has been verified to still work against the current site. The
classes, parsers, request entities and dialog steps are all still on disk in case a source is revived.

---

## Implementation notes

### Scraping pipeline

```
CycledProcess (raw Thread) → SendNewDataStrategy → DataManager → ScraperFactory
    → each Scraper → DataUpdater → DataCleaner → persist → messages to the chat
```

- `CycledProcess` loops `strategy.execute()` then sleeps for the latency given to `/init` (minutes → ms), and removes
  itself from `ProcessManager` on exit. `/stop` sets a flag and interrupts.
- `ProcessManager` holds `Map<chatId, List<Process>>`, so loops are per chat, not global.
- `ScraperFactory` fans out over all scrapers in a `parallelStream`, handing each only the requests it accepts.

### PageScrapingFunction

`observer/scraper/core/PageScrapingFunction` is the shared paging engine used by 42 of the 43 registered scrapers —
`LatvijasPastsScraper` is the exception, since one reference number is a single fetch. It is a
fluent config object — `singlePage`, `sameOutput`, `withFallbackConnector`, `withFilteringCondition`,
`withStopPaginationPredicate`, `withDataCallback`, `withReqDataPostProcessing`, `withSendingOutCondition` — that keeps
requesting pages until the accumulated result set stops growing (or the stop predicate fires). New scrapers configure
this rather than write their own loop; 12 of the current scrapers are `singlePage(true)` because their source returns
everything at once.

### Connectors and parsers

`observer/scraper/core/connector` holds the `IConnector` implementations. Each handles the HTTP call and the per-site
rewriting of the paging URL or request body:

| Connector | Used by | Notes |
| --- | --- | --- |
| `GETConnector` | most housing sites, 3 of 4 job sites, Latvijas Pasts | plain `HttpURLConnection` |
| `POSTConnector` | Kamernet, Interhouse, Vanderlinden, Vbtverhuurmakelaars, Viadaan, Just Eat Takeaway, IMDB | JSON/GraphQL APIs; the body, not the URL, carries paging |
| `GETClientConnector` | Rooms and Houses, Ikwilhuren | Apache `HttpClient`, for sites that need cookies or redirect handling |
| `JsoupConnector` | Funda | fetches through Jsoup with custom headers |

Parsers live under `observer/scraper/core/parser/**` and turn a raw page string into `Data` entities.

### Processee

`observer/Processee<T>` wraps a scraped item together with a deferred validity predicate and a callback. `getValid()`
runs the callback and applies the predicate; `callAndReset()` fires callbacks once, during persistence. This is how a
scraper attaches "only notify if …" logic (and expensive per-item detail fetches, as Funda does) that must run *after*
deduplication rather than for every item on every page.

### Deduplication

"Already seen" means **a `RequestToData` row exists for this request + data**, not merely that the data exists. Two users
watching the same site each get their own notification for the same listing.

`service/DataCleaner` groups incoming items by concrete `Data` subclass and request id, then batches an `IN (...)`
lookup (500 at a time) per type to drop what is already linked. **Every new `Data` type needs its own branch here plus a
repository query** — without one nothing is deduplicated and users get repeat notifications every cycle.
`observer/DataUpdater` then persists the survivors and writes the join rows in a single `@Transactional` call.

### Persistence

- `Request` and `Data` are abstract `@Entity` classes with `InheritanceType.TABLE_PER_CLASS`, joined many-to-many
  through the explicit `RequestToData` entity.
- Schema is managed by Hibernate `ddl-auto: update`. There are no migrations; entity changes mutate the live schema.
- Entities are read inside a transaction but rendered to Telegram outside it, so
  `utility/HibernateInitializationFunction` reflectively force-initializes lazy associations. Annotate a field with
  `@HibernateInitIgnore` to keep it lazy — used on the `requestToData` back-references so the whole graph is not pulled.
- `entity/repository/` has two `@NoRepositoryBean` bases (`DataRepository<T>`, `RequestRepository<T extends Request>`)
  plus `CommonRequestRepository` for polymorphic queries across all request subtypes.

### Dialog state machine

`bot/SimpleDataScraperBot` extends `TelegramLongPollingCommandBot`.

- `StateRegistry` keeps an in-memory `Map<chatId, LinkedList<State>>`. **It is process-local and unbounded** — dialogs do
  not survive a restart and are evicted only by `forget()`.
- A message matching a dialog-starting command (`/add`, `/init`, `/delete`, `/deleteAdmin`, `/stopAdmin`) opens a
  dialog; subsequent non-command messages advance it.
- `StepMappingFunction` maps `(stepId, dialogType)` to a `StepFunction`. Each step returns a `StepResponse(id, name)`
  where `id` is the next step and `name` is the prompt to send back. `StepLinkingFunction` advances the registry, sends
  the prompt, and on `TERMINAL_RESPONSE` runs the final action and forgets the chain.
- **Step ids are effectively field names.** They follow `Step<source><n>Add`, where the first digit is the source —
  3x = NL housing, 4x = Latvijas Pasts, 5x = IMDB, 6x = NL jobs (1x and 2x belonged to the deprecated vacancy sources).
  `bot/request/*RequestFunction` classes read the finished dialog as a `Map<stepId, Message>`, so renumbering a step
  breaks the matching request function.
- The menu number typed at step 0 is a *separate* numbering: `AddRequestCommand` lists 1–4, `Step0Add` maps those to
  step ids 30/40/50/60, and the same number is the switch key in `SimpleDataScraperBot.RequestWrapper`. Changing the
  menu means changing all three.
- `BaseCommand.sendAnswer` chunks output at 1500 characters because of Telegram's message limit.

### Spring wiring quirks

- `ProcessManager` is a `@Configuration` whose `@Bean` methods take arguments and are `@Scope("prototype")`. Calling
  `processManager.cycledProcess(...)` / `sendNewDataStrategy(...)` goes through the CGLIB config proxy, which is how the
  `new`-ed instances still get field injection. Constructing these classes directly with `new` leaves those fields null.
- `ScraperConfig` is a `@Configuration`, but the scrapers are plain objects rather than beans; `getScrapers()` builds the
  list on demand and registers every `NLHousingSite` / `NLJobSite` automatically.
- `app.vacancy.parsing-mode` (`main`/`extra`) only ever controlled the vacancy scrapers, so it currently has no effect:
  it is read by the deprecated factory methods that `getScrapers()` no longer calls.
- `CommandConfiguration.getCommands()` is the registration list — a command bean missing from it is never registered.
- The `TelegramBotsApi` bean in `configuration/Configuration.java` is `@Profile("prod")`. Under any other profile the bot
  is constructed but never registered with Telegram, which is how to work on the GraphQL/JPA side without a live token.

### GraphQL and audit

Schema: `src/main/resources/graphql/schema.graphqls` (one file, both domains). Served on port 8090 at `/graphql`, with
GraphiQL and introspection enabled and WebSocket subscriptions on the same path. Actuator exposes `health` and `info`.

- `audit/CommandAspect` is an AspectJ `@Before` on `bot.command.*.processMessage(..)`. It derives `CommandName` from the
  command's class simple name by stripping the `Command` suffix, so **a new command class needs a matching `CommandName`
  constant and a matching value in the GraphQL enum**, or its calls are silently unaudited.
- `AuditLogService.record` is `@Async @Transactional` and emits into a Reactor `Sinks.Many` backing the `auditLogAdded`
  subscription. The sink is per-instance, so subscribers only see events from the pod that handled the command.
- `MovieController` uses keyset pagination (`Window` / `ScrollSubrange`) with `MovieSpecifications` built from the
  `MovieFilter` input, and `@BatchMapping` to avoid N+1 on `Movie.requests`.
- Query depth/complexity limits in `MovieGraphQlConfig` are `@ConditionalOnProperty` on introspection being *disabled*,
  so they are inactive in the current configuration.

---

## Running

Required environment: `BOT_NAME`, `BOT_TOKEN` (Telegram) and `SDSB_DBURL`, `SDSB_USERNAME`, `SDSB_PASSWORD` (MySQL). The
default active profile is `prod` and resolves all of them; there is no dev profile with defaults.

```bash
./mvnw clean compile        # build
./mvnw package              # jar → target/simpledatascraperbot-0.0.1-SNAPSHOT.jar
./mvnw spring-boot:run      # run locally (needs the env vars above)

docker compose up --build   # app + MySQL 8
```

`pom.xml` and `system.properties` target Java 17 while the Dockerfile and GitLab CI build on Temurin 21 — keep sources
at a 17-compatible level. There is no `src/test` yet, so `./mvnw test` runs nothing.

## Deployment

- `.gitlab-ci.yml` — build → test → package → docker (pushes `$CI_REGISTRY_IMAGE:$CI_COMMIT_REF_SLUG`) → manual `deploy`
  that scp's `docker-compose.yaml` to a host over SSH.
- `charts/simpledatascraperbot/` — Helm chart, with either an in-cluster MySQL StatefulSet or `externalDatabase`.
  Secrets supply `BOT_TOKEN`, `BOT_NAME`, `SDSB_USERNAME`, `SDSB_PASSWORD`; `SDSB_DBURL` is templated by the
  `sdsb.jdbcUrl` helper.
- `terraform/` — AWS EKS + RDS + VPC, then installs the chart through the Helm provider. `kind-config.yaml` is for local
  kind clusters.
- The container runs a read-only rootfs as non-root uid 10001; the Dockerfile uses Spring Boot layertools extraction.

## Adding a new source

For a new **Dutch housing site** or **NL jobs site**, write the scraper and parser and add one `NLHousingSite` /
`NLJobSite` enum constant **at the end** — `ScraperConfig` picks it up automatically and the dialog already renders it.

Anything else needs the full path:

1. `entity/request/<domain>/XRequest extends Request`, and if the shape is new, `entity/data/XData extends Data` plus
   repository and service.
2. A parser under `observer/scraper/core/parser/**` and a scraper under `observer/scraper/core/**` implementing
   `Scraper<T>` through `PageScrapingFunction`.
3. Register the scraper in `ScraperConfig.getScrapers()`.
4. Wire the dialog: a branch in `Step0Add`, new `Step<n>*Add` classes, entries in
   `StepMappingFunction.getAddStepFunction`, a `RequestTransformation`, a branch in
   `SimpleDataScraperBot.RequestWrapper.getRequestTransformation`, and a menu line in `AddRequestCommand`.
5. Add a dedupe branch in `DataCleaner.getUnique`.
