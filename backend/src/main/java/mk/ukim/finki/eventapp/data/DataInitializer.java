package mk.ukim.finki.eventapp.data;

import jakarta.transaction.Transactional;
import mk.ukim.finki.eventapp.model.Event;
import mk.ukim.finki.eventapp.model.Location;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.enumerations.Role;
import mk.ukim.finki.eventapp.model.enumerations.Status;
import mk.ukim.finki.eventapp.model.enumerations.Type;
import mk.ukim.finki.eventapp.repository.EventRepository;
import mk.ukim.finki.eventapp.repository.LocationRepository;
import mk.ukim.finki.eventapp.repository.UserRepository;
import mk.ukim.finki.eventapp.model.enumerations.UserStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    public DataInitializer(UserRepository userRepository, EventRepository eventRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0) {
            // Create users with roles CREATOR and STUDENT
            User creatorUser = new User();
            creatorUser.setUsername("creator1");
            creatorUser.setName("Alice");
            creatorUser.setSurname("Creator");
            creatorUser.setPassword("password123");
            creatorUser.setEmail("alice.creator@example.com");
            creatorUser.setRole(Role.CREATOR);
            creatorUser.setStatus(UserStatus.LOGGED_OUT);
            userRepository.save(creatorUser);

            User studentUser1 = new User();
            studentUser1.setUsername("student1");
            studentUser1.setName("Bob");
            studentUser1.setSurname("Student");
            studentUser1.setPassword("password123");
            studentUser1.setEmail("bob.student@example.com");
            studentUser1.setRole(Role.STUDENT);
            studentUser1.setStatus(UserStatus.LOGGED_OUT);
            userRepository.save(studentUser1);

            User studentUser2 = new User();
            studentUser2.setUsername("student2");
            studentUser2.setName("Charlie");
            studentUser2.setSurname("Student");
            studentUser2.setPassword("password123");
            studentUser2.setEmail("charlie.student@example.com");
            studentUser2.setRole(Role.STUDENT);
            studentUser2.setStatus(UserStatus.LOGGED_OUT);
            userRepository.save(studentUser2);

            if(locationRepository.count() == 0) {
                locationRepository.saveAll(List.of(
                        new Location(1L,"University Stadium", 1500, 21.4280, 41.9981),
                        new Location(2L,"Main Hall", 1000, 21.4281, 41.9982),
                        new Location(3L,"Auditorium", 300, 21.4282, 41.9983),
                        new Location(4L,"Art Studio", 50,  21.4283, 41.9984),
                        new Location(5L,"Open Grounds", 200, 21.4284, 41.9985),
                        new Location(6L,"Lecture Hall 1", 100, 21.4285, 41.9986)
                ));
            }

            if(eventRepository.count() == 0){
                // Create list of events with organizer as String and assign same creatorUser as creator (conceptually)
                List<Event> events = new ArrayList<>();

                events.add(Event.builder()
                        .id(1L)  // Added id
                        .name("Football Match")
                        .description("Inter-university football tournament semi-finals.")
                        .imageUrl("https://www.fisu.net/app/uploads/2023/10/paulista_university1.jpg")
                        .startTime(LocalDateTime.of(2024, 12, 29, 15, 0))
                        .endTime(LocalDateTime.of(2024, 12, 29, 17, 0))
                        .location(locationRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Location not found")))
                        .price(5)
                        .capacity(500)
                        .status(Status.AVAILABLE)
                        .type(Type.SPORTS)
                        .createdAt(LocalDateTime.now())
                        .organizer("Faculty of Sports")
                        .creator(creatorUser)
                        .rating(4.5)
                        .build());

                events.add(Event.builder()
                        .id(2L)  // Added id
                        .name("Freshers Party")
                        .description("Welcoming all the freshers with music, dance, and games.")
                        .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTE7luLlquO6JGKRCKH8jvKJ3x8ic4gOS4jxg&s")
                        .startTime(LocalDateTime.of(2024, 12, 29, 19, 0))
                        .endTime(LocalDateTime.of(2024, 12, 29, 23, 0))
                        .location(locationRepository.findById(2L).orElseThrow(() -> new IllegalArgumentException("Location not found")))
                        .price(10)
                        .capacity(300)
                        .status(Status.AVAILABLE)
                        .type(Type.PARTY)
                        .createdAt(LocalDateTime.now())
                        .organizer("Student Affairs")
                        .creator(creatorUser)
                        .rating(4.0)
                        .build());

                events.add(Event.builder()
                        .id(3L)  // Added id
                        .name("Tech Conference 2024")
                        .description("Discussing the future of AI and machine learning.")
                        .imageUrl("https://example.com/tech_conference.jpg")
                        .startTime(LocalDateTime.of(2024, 12, 29, 10, 0))
                        .endTime(LocalDateTime.of(2024, 12, 29, 16, 0))
                        .location(locationRepository.findById(3L).orElseThrow(() -> new IllegalArgumentException("Location not found")))
                        .price(15)
                        .capacity(200)
                        .status(Status.AVAILABLE)
                        .type(Type.CONFERENCE)
                        .createdAt(LocalDateTime.now())
                        .organizer("Faculty of Computer Science")
                        .creator(creatorUser)
                        .rating(4.8)
                        .build());

                events.add(Event.builder()
                        .id(4L)  // Added id
                        .name("Painting Workshop")
                        .description("Learn painting techniques from professionals.")
                        .imageUrl("https://example.com/painting_workshop.jpg")
                        .startTime(LocalDateTime.of(2024, 12, 29, 14, 0))
                        .endTime(LocalDateTime.of(2024, 12, 29, 17, 0))
                        .location(locationRepository.findById(4L).orElseThrow(() -> new IllegalArgumentException("Location not found")))
                        .price(10)
                        .capacity(50)
                        .status(Status.IN_PROGRESS)
                        .type(Type.WORKSHOP)
                        .createdAt(LocalDateTime.now())
                        .organizer("Faculty of Arts")
                        .creator(creatorUser)
                        .rating(4.2)
                        .build());

                events.add(Event.builder()
                        .id(5L)  // Added id
                        .name("Cultural Fest")
                        .description("Experience a blend of music, dance, and traditions.")
                        .imageUrl("https://example.com/cultural_fest.jpg")
                        .startTime(LocalDateTime.of(2024, 12, 29, 17, 0))
                        .endTime(LocalDateTime.of(2024, 12, 29, 22, 0))
                        .location(locationRepository.findById(5L).orElseThrow(() -> new IllegalArgumentException("Location not found")))
                        .price(0)
                        .capacity(1000)
                        .status(Status.AVAILABLE)
                        .type(Type.CULTURAL)
                        .createdAt(LocalDateTime.now())
                        .organizer("Cultural Committee")
                        .creator(creatorUser)
                        .rating(4.9)
                        .build());

                events.add(Event.builder()
                        .id(6L)  // Added id
                        .name("Guest Lecture on Space Exploration")
                        .description("Hear from NASA scientists about the future of space exploration.")
                        .imageUrl("https://example.com/guest_lecture.jpg")
                        .startTime(LocalDateTime.of(2024, 12, 29, 15, 0))
                        .endTime(LocalDateTime.of(2024, 12, 29, 16, 30))
                        .location(locationRepository.findById(6L).orElseThrow(() -> new IllegalArgumentException("Location not found")))
                        .price(0)
                        .capacity(150)
                        .status(Status.AVAILABLE)
                        .type(Type.EDUCATIONAL)
                        .createdAt(LocalDateTime.now())
                        .organizer("Faculty of Science")
                        .creator(creatorUser)
                        .rating(4.7)
                        .build());

                // Save all events at once
                eventRepository.saveAll(events);
            }
        }
    }
}
