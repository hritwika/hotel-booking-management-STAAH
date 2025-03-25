public class HotelBookingManager {
    private final List<Hotel> hotels;
    private final List<Booking> bookings;

    public HotelBookingManager(String hotelsFile, String bookingsFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.hotels = objectMapper.readValue(new File(hotelsFile), new TypeReference<List<Hotel>>() {});
        this.bookings = objectMapper.readValue(new File(bookingsFile), new TypeReference<List<Booking>>() {});
    }

    public int getAvailability(String hotelId, LocalDate startDate, LocalDate endDate, String roomType) {
        Optional<Hotel> hotelOpt = hotels.stream().filter(h -> h.id.equals(hotelId)).findFirst();
        if (hotelOpt.isEmpty()) return 0;

        Hotel hotel = hotelOpt.get();
        long totalRooms = hotel.rooms.stream().filter(r -> r.roomType.equals(roomType)).count();

        long bookedRooms = bookings.stream()
                .filter(b -> b.hotelId.equals(hotelId) && b.roomType.equals(roomType))
                .filter(b -> isOverlapping(startDate, endDate, LocalDate.parse(b.arrival), LocalDate.parse(b.departure)))
                .count();

        return (int) (totalRooms - bookedRooms);
    }

    public List<String> searchAvailability(String hotelId, int daysAhead, String roomType) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);
        List<String> availableSlots = new ArrayList<>();

        for (LocalDate start = today; start.isBefore(endDate); start = start.plusDays(1)) {
            for (int duration = 1; duration <= 7; duration++) {
                LocalDate checkOut = start.plusDays(duration);
                int availability = getAvailability(hotelId, start, checkOut, roomType);
                if (availability > 0) {
                    availableSlots.add(String.format("(%s-%s, %d)", start, checkOut, availability));
                }
            }
        }
        return availableSlots;
    }

    private boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    public static void main(String[] args) throws IOException {
        HotelBookingManager manager = new HotelBookingManager("hotels.json", "bookings.json");
        System.out.println("Availability: " + manager.getAvailability("H1", LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-03"), "SGL"));
        System.out.println("Search: " + manager.searchAvailability("H1", 365, "SGL"));
    }
}