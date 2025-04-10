package com.example.hotelbooking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class HotelBookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelBookingApplication.class, args);
    }
}

@RestController
@RequestMapping("/api")
class HotelBookingController {
    private final Map<String, Hotel> hotels;
    private final List<Booking> bookings;

    public HotelBookingController() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        hotels = objectMapper.readValue(new File("hotels.json"), new TypeReference<>() {});
        bookings = objectMapper.readValue(new File("bookings.json"), new TypeReference<>() {});
    }

    @GetMapping("/availability")
    public int checkAvailability(@RequestParam String hotelId, @RequestParam String dateRange, @RequestParam String roomType) {
        Hotel hotel = hotels.get(hotelId);
        if (hotel == null) return 0;

        List<String> dates = parseDateRange(dateRange);
        long totalRooms = hotel.countRoomsByType(roomType);
        long bookedRooms = bookings.stream()
            .filter(b -> b.getHotelId().equals(hotelId) && b.getRoomType().equals(roomType) && overlaps(dates, b.getDates()))
            .count();
        //this is for availibility endpoint
        return (int) (totalRooms - bookedRooms);
    }

    @GetMapping("/search")
    public List<String> searchAvailability(@RequestParam String hotelId, @RequestParam int daysAhead, @RequestParam String roomType) {
        List<String> availableRanges = new ArrayList<>();
        Hotel hotel = hotels.get(hotelId);
        if (hotel == null) return availableRanges;

        LocalDate today = LocalDate.now();
        for (int i = 0; i < daysAhead; i++) {
            LocalDate start = today.plusDays(i);
            LocalDate end = start.plusDays(3); 
            int availability = checkAvailability(hotelId, start.toString() + "-" + end.toString(), roomType);
            if (availability > 0) {
                availableRanges.add("(" + start + "-" + end + ", " + availability + ")");
            }
        }
        return availableRanges;
    }

    private boolean overlaps(List<String> dates, List<String> bookingDates) {
        for (String date : dates) {
            if (bookingDates.contains(date)) return true;
        }
        return false;
    }

    private List<String> parseDateRange(String dateRange) {
        if (dateRange.contains("-")) {
            String[] parts = dateRange.split("-");
            LocalDate start = LocalDate.parse(parts[0]);
            LocalDate end = LocalDate.parse(parts[1]);
            return start.datesUntil(end.plusDays(1)).map(LocalDate::toString).collect(Collectors.toList());
        } else {
            return List.of(dateRange);
        }
    }
}

class Hotel {
    private String id;
    private String name;
    private List<RoomType> roomTypes;
    private List<Room> rooms;

    public long countRoomsByType(String type) {
        return rooms.stream().filter(r -> r.getRoomType().equals(type)).count();
    }
}

class RoomType {
    private String code;
    private String description;
    private List<String> amenities;
    private List<String> features;
}

class Room {
    private String roomType;
    private String roomId;

    public String getRoomType() {
        return roomType;
    }
}

class Booking {
    private String hotelId;
    private String arrival;
    private String departure;
    private String roomType;
    private String roomRate;

    public String getHotelId() {
        return hotelId;
    }

    public String getRoomType() {
        return roomType;
    }

    public List<String> getDates() {
        LocalDate start = LocalDate.parse(arrival);
        LocalDate end = LocalDate.parse(departure);
        return start.datesUntil(end).map(LocalDate::toString).collect(Collectors.toList());
    }
}
