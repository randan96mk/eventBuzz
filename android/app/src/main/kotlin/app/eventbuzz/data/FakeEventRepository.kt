package app.eventbuzz.data

import app.eventbuzz.domain.model.Category
import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.model.EventFilter
import app.eventbuzz.domain.model.Location
import app.eventbuzz.domain.model.SortOption
import app.eventbuzz.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class FakeEventRepository @Inject constructor() : EventRepository {

    private val categories = listOf(
        Category(1, "Music", "music", "#E91E63", "music_note"),
        Category(2, "Sports", "sports", "#4CAF50", "sports"),
        Category(3, "Food & Drink", "food-drink", "#FF9800", "restaurant"),
        Category(4, "Arts", "arts", "#9C27B0", "palette"),
        Category(5, "Tech", "tech", "#2196F3", "computer"),
        Category(6, "Outdoor", "outdoor", "#8BC34A", "park"),
        Category(7, "Community", "community", "#00BCD4", "groups"),
    )

    private val now = Instant.now()

    private val fakeEvents: List<Event> = buildList {
        // Music events
        add(event("1", "Jazz Night at Blue Note", "Live jazz performances featuring local and international artists. Enjoy smooth melodies with dinner and drinks.", categories[0], 40.7128, -74.0060, "Blue Note Jazz Club, 131 W 3rd St", "New York", 1, null))
        add(event("2", "Summer Rock Festival", "Three-day rock festival featuring 20+ bands across multiple stages. Food vendors and camping available.", categories[0], 40.7282, -73.7949, "Central Park, NYC", "New York", 3, "https://picsum.photos/seed/rock/400/300"))
        add(event("3", "Electronic Music Rave", "Underground electronic music event with top DJs spinning house, techno, and trance.", categories[0], 40.7484, -73.9857, "Warehouse 42, Brooklyn", "New York", 5, null))
        add(event("4", "Classical Concert", "Beethoven's Symphony No. 9 performed by the city philharmonic orchestra.", categories[0], 40.7725, -73.9835, "Lincoln Center", "New York", 7, "https://picsum.photos/seed/classical/400/300"))
        add(event("5", "Open Mic Night", "Share your musical talent or enjoy performances from local artists. All genres welcome.", categories[0], 40.7308, -73.9973, "The Bitter End, Bleecker St", "New York", 2, null))
        add(event("6", "Reggae Beach Party", "Chill vibes, reggae beats, and ocean breeze. Featuring live bands and drum circles.", categories[0], 40.5731, -73.9712, "Brighton Beach", "Brooklyn", 10, "https://picsum.photos/seed/reggae/400/300"))
        add(event("7", "Hip Hop Showcase", "Rising hip hop artists battle it out in this monthly showcase. DJs and MCs.", categories[0], 40.6892, -73.9857, "BAM Howard Gilman Opera House", "Brooklyn", 4, null))

        // Sports events
        add(event("8", "Marathon Training Run", "Weekly group training run for the upcoming city marathon. All levels welcome.", categories[1], 40.7694, -73.9736, "Central Park West Entrance", "New York", 1, "https://picsum.photos/seed/marathon/400/300"))
        add(event("9", "Beach Volleyball Tournament", "4v4 tournament on the sand. Register your team or come watch and cheer.", categories[1], 40.5780, -73.9663, "Coney Island Beach", "Brooklyn", 6, null))
        add(event("10", "Yoga in the Park", "Free outdoor yoga session led by certified instructor. Bring your own mat.", categories[1], 40.7829, -73.9654, "Sheep Meadow, Central Park", "New York", 2, "https://picsum.photos/seed/yoga/400/300"))
        add(event("11", "Basketball Pickup Games", "Join the community pickup basketball games. Show up and play. All skill levels.", categories[1], 40.6951, -73.9497, "Prospect Park Courts", "Brooklyn", 0, null))
        add(event("12", "Cycling Group Ride", "50-mile scenic group ride through the borough. Helmets required.", categories[1], 40.7580, -73.8856, "Flushing Meadows", "Queens", 8, "https://picsum.photos/seed/cycling/400/300"))
        add(event("13", "Boxing Class", "High-intensity boxing workout. Gloves provided. No experience needed.", categories[1], 40.7210, -73.9880, "Gleason's Gym, DUMBO", "Brooklyn", 3, null))
        add(event("14", "Soccer League Finals", "Season championship game for the local recreational soccer league.", categories[1], 40.7500, -73.8464, "Flushing Meadows Fields", "Queens", 5, "https://picsum.photos/seed/soccer/400/300"))

        // Food & Drink events
        add(event("15", "Street Food Festival", "Over 50 food vendors from around the world. Live cooking demonstrations and tastings.", categories[2], 40.7527, -73.9772, "Bryant Park", "New York", 2, "https://picsum.photos/seed/streetfood/400/300"))
        add(event("16", "Wine Tasting Evening", "Sample wines from 15 vineyards. Paired with artisan cheeses and charcuterie.", categories[2], 40.7230, -73.9934, "SoHo Wine Cellar", "New York", 4, null))
        add(event("17", "Brunch Cook-Off", "Amateur chefs compete to make the best brunch dish. Public tasting and voting.", categories[2], 40.7185, -73.9567, "Williamsburg Food Hall", "Brooklyn", 6, "https://picsum.photos/seed/brunch/400/300"))
        add(event("18", "Craft Beer Festival", "50+ craft breweries pouring their finest. Live music and food trucks.", categories[2], 40.6880, -73.9790, "Industry City", "Brooklyn", 9, null))
        add(event("19", "Sushi Making Workshop", "Learn the art of sushi from a master chef. All ingredients and tools provided.", categories[2], 40.7580, -73.9855, "Midtown Culinary Studio", "New York", 3, "https://picsum.photos/seed/sushi/400/300"))
        add(event("20", "Farmers Market", "Fresh produce, baked goods, and artisan foods from local farmers.", categories[2], 40.7359, -74.0005, "Union Square Greenmarket", "New York", 1, null))
        add(event("21", "Coffee Cupping Experience", "Explore the flavors of single-origin coffee. Guided tasting with expert roasters.", categories[2], 40.6869, -73.9900, "Red Hook Coffee Roasters", "Brooklyn", 5, "https://picsum.photos/seed/coffee/400/300"))

        // Arts events
        add(event("22", "Gallery Opening Night", "New exhibition featuring contemporary abstract art. Meet the artist. Complimentary wine.", categories[3], 40.7208, -73.9960, "White Cube Gallery, SoHo", "New York", 2, "https://picsum.photos/seed/gallery/400/300"))
        add(event("23", "Pottery Workshop", "Hands-on pottery class. Learn wheel throwing and glazing techniques.", categories[3], 40.7190, -73.9564, "Clay Studio Williamsburg", "Brooklyn", 4, null))
        add(event("24", "Street Art Walking Tour", "Guided tour of the best murals and graffiti art. Cameras encouraged.", categories[3], 40.7139, -73.9610, "Bushwick Collective", "Brooklyn", 1, "https://picsum.photos/seed/streetart/400/300"))
        add(event("25", "Film Screening & Discussion", "Indie film premiere followed by Q&A with the director.", categories[3], 40.6862, -73.9738, "Nitehawk Cinema, Park Slope", "Brooklyn", 7, null))
        add(event("26", "Life Drawing Class", "Open figure drawing session with live model. All skill levels. Bring supplies.", categories[3], 40.7290, -73.9918, "NYU Art Studio", "New York", 3, "https://picsum.photos/seed/drawing/400/300"))
        add(event("27", "Photography Exhibition", "Award-winning photojournalism from around the world. Free admission.", categories[3], 40.7614, -73.9776, "International Center of Photography", "New York", 6, null))
        add(event("28", "Theater Improv Night", "Improvisational comedy and theater. Audience participation encouraged.", categories[3], 40.7317, -73.9928, "UCB Theatre", "New York", 2, "https://picsum.photos/seed/improv/400/300"))

        // Tech events
        add(event("29", "AI & Machine Learning Meetup", "Monthly gathering of AI enthusiasts. Lightning talks and networking.", categories[4], 40.7425, -73.9880, "Google NYC, Chelsea", "New York", 3, "https://picsum.photos/seed/ai/400/300"))
        add(event("30", "Startup Demo Day", "10 startups pitch their products to investors and the public. Q&A and networking.", categories[4], 40.7400, -74.0020, "WeWork Hudson Yards", "New York", 5, null))
        add(event("31", "Hackathon Weekend", "48-hour coding challenge. Build something amazing. Prizes for top teams.", categories[4], 40.6928, -73.9865, "Brooklyn Navy Yard", "Brooklyn", 8, "https://picsum.photos/seed/hackathon/400/300"))
        add(event("32", "Kotlin Conf Meetup", "Deep dive into Kotlin Multiplatform and Compose. Hands-on code labs.", categories[4], 40.7505, -73.9934, "JetBrains Office, Times Square", "New York", 4, null))
        add(event("33", "Cybersecurity Workshop", "Learn to protect your digital life. Hands-on exercises with security tools.", categories[4], 40.7372, -73.9919, "Flatiron School", "New York", 2, "https://picsum.photos/seed/security/400/300"))
        add(event("34", "Robotics Exhibition", "Interactive exhibition showcasing cutting-edge robotics and automation.", categories[4], 40.7484, -73.9879, "Jacob Javits Center", "New York", 10, null))
        add(event("35", "Web3 & Blockchain Talk", "Exploring the future of decentralized applications and blockchain technology.", categories[4], 40.7283, -73.7918, "Queens Tech Hub", "Queens", 6, "https://picsum.photos/seed/web3/400/300"))

        // Outdoor events
        add(event("36", "Sunrise Hike", "Early morning group hike to catch the sunrise. Moderate difficulty.", categories[5], 40.7934, -73.9518, "Fort Tryon Park", "New York", 1, "https://picsum.photos/seed/hike/400/300"))
        add(event("37", "Kayaking on the Hudson", "Guided kayaking adventure on the Hudson River. Equipment provided.", categories[5], 40.7286, -74.0128, "Pier 26 Boathouse", "New York", 3, null))
        add(event("38", "Bird Watching Walk", "Guided walk to spot local and migratory bird species. Binoculars provided.", categories[5], 40.7700, -73.9700, "The Ramble, Central Park", "New York", 2, "https://picsum.photos/seed/birds/400/300"))
        add(event("39", "Community Garden Day", "Help plant and maintain the community garden. Learn about urban farming.", categories[5], 40.6782, -73.9442, "Crown Heights Garden", "Brooklyn", 0, null))
        add(event("40", "Outdoor Movie Night", "Classic film screened outdoors under the stars. Bring blankets and snacks.", categories[5], 40.7500, -73.9700, "Bryant Park Film Festival", "New York", 5, "https://picsum.photos/seed/outdoor-movie/400/300"))
        add(event("41", "Stargazing Session", "Telescope-assisted stargazing led by an amateur astronomer. Weather permitting.", categories[5], 40.6602, -73.9690, "Prospect Park Nethermead", "Brooklyn", 7, null))
        add(event("42", "Nature Photography Walk", "Capture the beauty of autumn foliage. Tips from professional photographers.", categories[5], 40.7829, -73.9554, "Conservatory Garden", "New York", 4, "https://picsum.photos/seed/nature/400/300"))

        // Community events
        add(event("43", "Neighborhood Block Party", "Annual block party with music, food, games, and face painting for kids.", categories[6], 40.6830, -73.9410, "Franklin Ave, Crown Heights", "Brooklyn", 2, "https://picsum.photos/seed/block-party/400/300"))
        add(event("44", "Volunteer Beach Cleanup", "Help keep our beaches clean. Bags and gloves provided. Free t-shirt.", categories[6], 40.5764, -73.9610, "Brighton Beach Boardwalk", "Brooklyn", 1, null))
        add(event("45", "Book Club Meetup", "Monthly discussion of the selected book. This month: a contemporary fiction masterpiece.", categories[6], 40.6880, -73.9815, "Brooklyn Public Library", "Brooklyn", 3, "https://picsum.photos/seed/bookclub/400/300"))
        add(event("46", "Language Exchange Cafe", "Practice foreign languages with native speakers over coffee. 20+ languages.", categories[6], 40.7440, -73.9910, "Cafe Lingua, Chelsea", "New York", 0, null))
        add(event("47", "Flea Market", "Vintage finds, handmade crafts, and unique treasures from 100+ vendors.", categories[6], 40.6743, -73.9771, "Brooklyn Flea, Prospect Park", "Brooklyn", 4, "https://picsum.photos/seed/flea/400/300"))
        add(event("48", "Neighborhood Town Hall", "Open forum to discuss local issues with community leaders and elected officials.", categories[6], 40.7607, -73.9250, "Astoria Community Center", "Queens", 6, null))
        add(event("49", "Pet Adoption Fair", "Find your new best friend. Dogs, cats, and small animals available for adoption.", categories[6], 40.7430, -73.9890, "Madison Square Park", "New York", 2, "https://picsum.photos/seed/pets/400/300"))
        add(event("50", "Cultural Dance Festival", "Performances and workshops featuring dances from around the world.", categories[6], 40.6712, -73.9636, "Prospect Park Bandshell", "Brooklyn", 8, "https://picsum.photos/seed/dance/400/300"))
    }

    private fun event(
        id: String,
        title: String,
        description: String,
        category: Category,
        lat: Double,
        lng: Double,
        address: String,
        city: String,
        daysFromNow: Int,
        imageUrl: String?,
    ): Event {
        return Event(
            id = id,
            title = title,
            description = description,
            category = category,
            location = Location(lat, lng),
            address = address,
            city = city,
            startDate = now.plus(daysFromNow.toLong(), ChronoUnit.DAYS),
            endDate = now.plus(daysFromNow.toLong(), ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS),
            imageUrl = imageUrl,
            ticketUrl = null,
            priceMin = if (id.toInt() % 3 == 0) null else (id.toInt() * 5).toDouble(),
            priceMax = if (id.toInt() % 3 == 0) null else (id.toInt() * 10).toDouble(),
            currency = "USD",
            tags = listOf(category.slug, city.lowercase()),
            images = emptyList(),
            distanceMeters = null,
        )
    }

    private fun withDistance(event: Event, userLocation: Location): Event {
        val distanceMeters = haversineMeters(
            userLocation.latitude, userLocation.longitude,
            event.location.latitude, event.location.longitude,
        )
        return event.copy(distanceMeters = distanceMeters)
    }

    private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    override fun getNearbyEvents(location: Location, filter: EventFilter): Flow<List<Event>> {
        var events = fakeEvents.map { withDistance(it, location) }

        filter.categorySlug?.let { slug ->
            events = events.filter { it.category.slug == slug }
        }

        filter.query?.let { query ->
            if (query.isNotBlank()) {
                events = events.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true
                }
            }
        }

        events = events.filter { (it.distanceMeters ?: 0.0) <= filter.radiusMeters }

        events = when (filter.sortBy) {
            SortOption.DISTANCE -> events.sortedBy { it.distanceMeters }
            SortOption.DATE -> events.sortedBy { it.startDate }
            SortOption.POPULAR -> events.sortedByDescending { it.priceMax ?: 0.0 }
        }

        return flowOf(events)
    }

    override fun getEventBubbles(location: Location, filter: EventFilter): Flow<List<Event>> {
        return getNearbyEvents(location, filter)
    }

    override suspend fun getEventById(id: String): Event {
        return fakeEvents.find { it.id == id }
            ?: throw NoSuchElementException("Event with id $id not found")
    }

    override fun searchEvents(query: String, filter: EventFilter): Flow<List<Event>> {
        val defaultLocation = Location(40.7128, -74.0060) // NYC
        val searchFilter = filter.copy(query = query, radiusMeters = filter.radiusMeters.coerceAtLeast(50000))
        return getNearbyEvents(defaultLocation, searchFilter)
    }

    override fun getCategories(): Flow<List<Category>> {
        return flowOf(categories)
    }
}
