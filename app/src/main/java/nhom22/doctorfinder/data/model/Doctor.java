package nhom22.doctorfinder.data.model;

public class Doctor {
    private String name;
    private String specialty;
    private String rating;
    private String experience;
    private String distance;
    private String status;

    public Doctor(String name, String specialty, String rating,
                  String experience, String distance, String status) {
        this.name = name;
        this.specialty = specialty;
        this.rating = rating;
        this.experience = experience;
        this.distance = distance;
        this.status = status;
    }

    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getRating() { return rating; }
    public String getExperience() { return experience; }
    public String getDistance() { return distance; }
    public String getStatus() { return status; }
}