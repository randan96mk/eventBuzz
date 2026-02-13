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

        // ========== BANGALORE (Bengaluru) ==========
        // Music
        add(event("51", "Bangalore Jazz Festival", "International jazz artists perform under the stars at Jayamahal Palace grounds.", categories[0], 12.9896, 77.5929, "Jayamahal Palace, Palace Rd", "Bangalore", 2, "https://picsum.photos/seed/blr-jazz/400/300"))
        add(event("52", "Indie Music Night at Fandom", "Live indie rock and alternative bands at Bangalore's favorite music venue.", categories[0], 12.9352, 77.6245, "Fandom at Gilly's, Koramangala", "Bangalore", 1, null))
        add(event("53", "Carnatic Music Concert", "Classical Carnatic music recital featuring renowned vocalists and instrumentalists.", categories[0], 12.9719, 77.5937, "Chowdiah Memorial Hall, Malleswaram", "Bangalore", 5, "https://picsum.photos/seed/blr-carnatic/400/300"))

        // Sports
        add(event("54", "Cubbon Park Morning Run", "Weekly 5K group run through the green trails of Cubbon Park. All fitness levels.", categories[1], 12.9763, 77.5929, "Cubbon Park Main Gate", "Bangalore", 0, "https://picsum.photos/seed/blr-run/400/300"))
        add(event("55", "Badminton Tournament", "Open singles and doubles tournament at the Padukone-Dravid Centre.", categories[1], 12.9256, 77.6028, "Padukone-Dravid Centre, JP Nagar", "Bangalore", 4, null))
        add(event("56", "Cricket Gully League Finals", "Neighborhood cricket league championship match. Come cheer your local team!", categories[1], 12.9698, 77.7500, "Whitefield Sports Ground", "Bangalore", 6, "https://picsum.photos/seed/blr-cricket/400/300"))

        // Food & Drink
        add(event("57", "VV Puram Food Street Walk", "Guided food walk through Bangalore's legendary VV Puram food street. 10+ stops.", categories[2], 12.9480, 77.5730, "VV Puram Food Street", "Bangalore", 1, "https://picsum.photos/seed/blr-food/400/300"))
        add(event("58", "Craft Beer Crawl", "Hop between Bangalore's best microbreweries in Koramangala and Indiranagar.", categories[2], 12.9716, 77.6412, "Toit Brewpub, Indiranagar", "Bangalore", 3, null))
        add(event("59", "South Indian Cooking Class", "Learn to make dosa, idli, sambar and chutney from scratch. All ingredients provided.", categories[2], 12.9352, 77.6245, "Culinary Academy, Koramangala", "Bangalore", 7, "https://picsum.photos/seed/blr-dosa/400/300"))

        // Tech
        add(event("60", "Bangalore Startup Pitch Night", "10 early-stage startups pitch to VCs and angel investors. Networking and drinks.", categories[4], 12.9352, 77.6245, "91springboard, Koramangala", "Bangalore", 2, "https://picsum.photos/seed/blr-startup/400/300"))
        add(event("61", "Android Dev Meetup", "Deep dive into Jetpack Compose animations and Material 3. Hands-on workshop.", categories[4], 12.9772, 77.6377, "Google Office, Indiranagar", "Bangalore", 5, null))
        add(event("62", "AI/ML Conference Bangalore", "Full-day conference on generative AI, LLMs, and practical ML applications.", categories[4], 12.9570, 77.7009, "NIMHANS Convention Centre", "Bangalore", 10, "https://picsum.photos/seed/blr-ai/400/300"))

        // Arts
        add(event("63", "Rangoli Art Exhibition", "Traditional and contemporary rangoli art displayed by 30+ artists.", categories[3], 12.9857, 77.5912, "Karnataka Chitrakala Parishath", "Bangalore", 3, "https://picsum.photos/seed/blr-rangoli/400/300"))
        add(event("64", "Kannada Theater Night", "Award-winning Kannada play performed by Ranga Shankara troupe.", categories[3], 12.9170, 77.6006, "Ranga Shankara, JP Nagar", "Bangalore", 4, null))

        // Outdoor
        add(event("65", "Nandi Hills Sunrise Trek", "Early morning trek to Nandi Hills for a spectacular sunrise. Bus from city included.", categories[5], 13.3702, 77.6835, "Nandi Hills, Chikballapur", "Bangalore", 2, "https://picsum.photos/seed/blr-nandi/400/300"))
        add(event("66", "Lalbagh Botanical Walk", "Guided walk through the historic Lalbagh gardens. Learn about rare plant species.", categories[5], 12.9507, 77.5848, "Lalbagh Botanical Garden", "Bangalore", 1, null))

        // Community
        add(event("67", "Bangalore Comic Con", "Pop culture extravaganza with cosplay, comics, gaming, and celebrity guests.", categories[6], 12.9889, 77.7196, "KTPO Convention Centre, Whitefield", "Bangalore", 8, "https://picsum.photos/seed/blr-comiccon/400/300"))
        add(event("68", "Church Street Flea Market", "Vintage clothing, handmade jewelry, art prints, and street food every Sunday.", categories[6], 12.9754, 77.6070, "Church Street, MG Road", "Bangalore", 0, null))

        // ========== MUMBAI ==========
        // Music
        add(event("69", "Bollywood Night at Juhu", "Dance to Bollywood hits with live DJs on the beach. Food stalls and fire dancers.", categories[0], 19.0883, 72.8262, "Juhu Beach", "Mumbai", 2, "https://picsum.photos/seed/mum-bollywood/400/300"))
        add(event("70", "Sufi Music Evening", "Soulful Sufi and Qawwali performances at the historic Haji Ali precinct.", categories[0], 18.9827, 72.8089, "Haji Ali Dargah Area", "Mumbai", 5, null))

        // Sports
        add(event("71", "Marine Drive Morning Yoga", "Free sunrise yoga session along the Queen's Necklace. Mats provided.", categories[1], 18.9432, 72.8235, "Marine Drive Promenade", "Mumbai", 0, "https://picsum.photos/seed/mum-yoga/400/300"))
        add(event("72", "Mumbai Marathon Practice Run", "Official practice run for the Tata Mumbai Marathon. 10K route through SoBo.", categories[1], 18.9256, 72.8242, "CST to Worli Sea Link Route", "Mumbai", 3, null))

        // Food & Drink
        add(event("73", "Mohammed Ali Road Food Walk", "Legendary street food experience — kebabs, nihari, malpua, and falooda.", categories[2], 18.9580, 72.8345, "Mohammed Ali Road", "Mumbai", 1, "https://picsum.photos/seed/mum-kebab/400/300"))
        add(event("74", "Vada Pav Championship", "Mumbai's best vada pav makers compete. Public tasting and voting.", categories[2], 19.0176, 72.8562, "Dadar West", "Mumbai", 4, null))
        add(event("75", "Colaba Cafe Hopping", "Guided tour of Mumbai's iconic cafes — Leopold, Theobroma, and hidden gems.", categories[2], 18.9067, 72.8147, "Colaba Causeway", "Mumbai", 2, "https://picsum.photos/seed/mum-cafe/400/300"))

        // Arts
        add(event("76", "Kala Ghoda Art Walk", "Self-guided art walk through galleries, street art, and installations in Kala Ghoda.", categories[3], 18.9286, 72.8324, "Kala Ghoda Art District", "Mumbai", 1, "https://picsum.photos/seed/mum-kalaghoda/400/300"))
        add(event("77", "Prithvi Theatre Festival", "Week-long theater festival featuring Hindi, Marathi, and English plays.", categories[3], 19.0710, 72.8354, "Prithvi Theatre, Juhu", "Mumbai", 6, null))

        // Tech
        add(event("78", "Fintech Mumbai Meetup", "Payments, UPI, and digital banking — talks from industry leaders.", categories[4], 19.0654, 72.8687, "BKC Tech Hub, Bandra", "Mumbai", 3, "https://picsum.photos/seed/mum-fintech/400/300"))

        // Outdoor
        add(event("79", "Sanjay Gandhi Park Nature Walk", "Guided nature trail through the national park. Spot butterflies, birds, and deer.", categories[5], 19.2147, 72.9106, "Sanjay Gandhi National Park", "Mumbai", 2, "https://picsum.photos/seed/mum-park/400/300"))

        // Community
        add(event("80", "Bandra Fair", "Annual celebration with rides, food stalls, music, and cultural performances.", categories[6], 19.0509, 72.8274, "Mount Mary Church, Bandra", "Mumbai", 7, "https://picsum.photos/seed/mum-fair/400/300"))

        // ========== NEW DELHI ==========
        // Music
        add(event("81", "Qutub Festival", "Classical music and dance under the Qutub Minar. Annual cultural celebration.", categories[0], 28.5245, 77.1855, "Qutub Minar Complex", "New Delhi", 5, "https://picsum.photos/seed/del-qutub/400/300"))
        add(event("82", "Hauz Khas Live Music Night", "Indie bands perform at the lakeside venue in Hauz Khas Village.", categories[0], 28.5494, 77.2001, "Hauz Khas Village", "New Delhi", 1, null))

        // Sports
        add(event("83", "Delhi Half Marathon", "Annual half marathon through Rajpath and India Gate. 20,000+ runners.", categories[1], 28.6129, 77.2295, "India Gate, Rajpath", "New Delhi", 10, "https://picsum.photos/seed/del-marathon/400/300"))
        add(event("84", "Morning Cricket at Lodhi Garden", "Casual morning cricket match in the beautiful Lodhi Garden grounds.", categories[1], 28.5931, 77.2197, "Lodhi Garden", "New Delhi", 0, null))

        // Food & Drink
        add(event("85", "Chandni Chowk Food Trail", "Explore Old Delhi's 400-year-old food legacy — paranthas, chaat, jalebis, and lassi.", categories[2], 28.6506, 77.2302, "Chandni Chowk", "New Delhi", 1, "https://picsum.photos/seed/del-chaat/400/300"))
        add(event("86", "Khan Market Wine Tasting", "Premium wine tasting event featuring Indian and international vineyards.", categories[2], 28.6005, 77.2274, "Khan Market", "New Delhi", 4, null))
        add(event("87", "Dilli Haat Craft & Food Fest", "Handicrafts from every Indian state paired with regional cuisines.", categories[2], 28.5733, 77.2076, "Dilli Haat, INA", "New Delhi", 2, "https://picsum.photos/seed/del-haat/400/300"))

        // Arts
        add(event("88", "National Gallery Exhibition", "Retrospective of modern Indian art featuring works from 1940s to present.", categories[3], 28.6110, 77.2390, "National Gallery of Modern Art", "New Delhi", 3, "https://picsum.photos/seed/del-gallery/400/300"))
        add(event("89", "Kathak Dance Performance", "Classical Kathak recital at the India Habitat Centre.", categories[3], 28.5874, 77.2221, "India Habitat Centre, Lodhi Rd", "New Delhi", 6, null))

        // Tech
        add(event("90", "Delhi NCR Developer Summit", "Full-day tech summit covering cloud, DevOps, and mobile development.", categories[4], 28.4595, 77.0266, "Cyber Hub, Gurugram", "New Delhi", 7, "https://picsum.photos/seed/del-tech/400/300"))

        // Outdoor
        add(event("91", "Yamuna Biodiversity Park Walk", "Guided eco-walk through the biodiversity park. Bird watching and nature talks.", categories[5], 28.7041, 77.2025, "Yamuna Biodiversity Park", "New Delhi", 1, "https://picsum.photos/seed/del-yamuna/400/300"))

        // Community
        add(event("92", "Connaught Place Sunday Market", "Books, vinyl records, vintage finds, and street performances every Sunday.", categories[6], 28.6315, 77.2167, "Connaught Place Inner Circle", "New Delhi", 0, null))

        // ========== MYSURU (Mysore) ==========
        // Music
        add(event("93", "Mysore Palace Concert", "Classical music concert in the palace courtyard during the illumination evening.", categories[0], 12.3052, 76.6552, "Mysore Palace", "Mysuru", 3, "https://picsum.photos/seed/mys-palace/400/300"))

        // Sports
        add(event("94", "Yoga at Mysore Shala", "Traditional Ashtanga yoga practice at one of the world-famous Mysore shalas.", categories[1], 12.3156, 76.6502, "Gokulam, Mysore", "Mysuru", 0, "https://picsum.photos/seed/mys-yoga/400/300"))
        add(event("95", "Chamundi Hill Run", "5K uphill run/walk to the Chamundeshwari Temple. Stunning city views.", categories[1], 12.2724, 76.6700, "Chamundi Hill Base", "Mysuru", 2, null))

        // Food & Drink
        add(event("96", "Mysore Dosa & Filter Coffee Walk", "Taste the authentic Mysore masala dosa at iconic local joints. 5 stops.", categories[2], 12.2958, 76.6394, "Devaraja Market Area", "Mysuru", 1, "https://picsum.photos/seed/mys-dosa/400/300"))
        add(event("97", "Mysore Pak Making Workshop", "Learn to make the legendary Mysore Pak sweet from master confectioners.", categories[2], 12.3050, 76.6554, "Guru Sweet Mart, Sayyaji Rao Rd", "Mysuru", 4, null))

        // Arts
        add(event("98", "Mysore Painting Exhibition", "Traditional Mysore-style paintings with gold leaf work by local artisans.", categories[3], 12.3052, 76.6552, "Jaganmohan Palace Art Gallery", "Mysuru", 2, "https://picsum.photos/seed/mys-painting/400/300"))
        add(event("99", "Sandalwood Craft Workshop", "Hands-on sandalwood carving workshop with master craftsmen.", categories[3], 12.2990, 76.6460, "Cauvery Arts & Crafts Emporium", "Mysuru", 5, null))

        // Outdoor
        add(event("100", "Brindavan Gardens Evening Visit", "Musical fountain show and gardens lit up beautifully at sunset.", categories[5], 12.4218, 76.5731, "Brindavan Gardens, KRS Dam", "Mysuru", 3, "https://picsum.photos/seed/mys-brindavan/400/300"))
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
            currency = if (lat > 30.0 || lat < 6.0) "USD" else "INR",
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
        val defaultLocation = Location(12.9716, 77.5946) // Bangalore
        val searchFilter = filter.copy(query = query, radiusMeters = filter.radiusMeters.coerceAtLeast(50000))
        return getNearbyEvents(defaultLocation, searchFilter)
    }

    override fun getCategories(): Flow<List<Category>> {
        return flowOf(categories)
    }
}
