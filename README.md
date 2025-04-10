# hotel-booking-management-STAAH

Hotel Booking System
It is a Spring Boot application that manages hotel room availability and reservations. It reads hotel and booking data from JSON files and allows users to check room availability and search for available dates.

**Prerequisites**
Before you begin, ensure you have the following installed:

Java 17 (or Java 8/11)

Maven (ensure it's configured in the system)

Git (for cloning the repository)

**Setup Instructions**
1. Clone the Repository
git clone <repository-url>
cd hotel-booking-system
2. Build the Project
Run the following command in the project root directory:
mvn clean install
3. Run the Application
mvn spring-boot:run
4. JSON Files Setup
Ensure you have hotels.json and bookings.json in the project root with the following structure:

hotels.json
[
    {
        "id": "H1",
        "name": "Hotel California",
        "roomTypes": [
            {
                "code": "SGL",
                "description": "Single Room",
                "amenities": ["WiFi", "TV"],
                "features": ["Non-smoking"]
            },
            {
                "code": "DBL",
                "description": "Double Room",
                "amenities": ["WiFi", "TV", "Minibar"],
                "features": ["Non-smoking", "Sea View"]
            }
        ],
        "rooms": [
            { "roomType": "SGL", "roomId": "101" },
            { "roomType": "SGL", "roomId": "102" },
            { "roomType": "DBL", "roomId": "201" },
            { "roomType": "DBL", "roomId": "202" }
        ]
    }
]

bookings.json

[
    {
        "hotelId": "H1",
        "arrival": "2024-09-01",
        "departure": "2024-09-03",
        "roomType": "DBL",
        "roomRate": "Prepaid"
    },
    {
        "hotelId": "H1",
        "arrival": "2024-09-02",
        "departure": "2024-09-05",
        "roomType": "SGL",
        "roomRate": "Standard"
    }
]

**API Endpoints**

1. Check Availability
GET /api/availability?hotelId=H1&dateRange=20240901-20240903&roomType=SGL
Response:
3
(Indicates 3 available rooms for the given date range and room type)

2. Search Available Dates
GET /api/search?hotelId=H1&daysAhead=365&roomType=SGL
Response:

["(20241101-20241103, 2)", "(20241203-20241210, 1)"]
(Indicates availability for specific date ranges)

**Running Tests**
To run unit tests, execute:

mvn test

**Deployment**
You can build a JAR file and run it manually:

mvn clean package
java -jar target/hotel-booking-system.jar
