    package nhom22.doctorfinder.model;

    public class Doctor {
        private String id;
        private String name;
        private String specialty;
        private String degree;
        private String hospital;
        private String description;
        private float rating;
        private int reviewCount;
        private boolean isOnline;
        private boolean hasAvailableSlot;
        private boolean isTopDoctor;
        private int experienceYears;
        private String avatarUrl;
        private String doctorType; // Hospital, Clinic, Online
        private double similarityScore;
        private String matchedImageUrl;


        public Doctor(String id, String name, String specialty, String degree,
                      String hospital, float rating, int reviewCount,
                      boolean isOnline, int experienceYears, String doctorType) {
            this.id = id;
            this.name = name;
            this.specialty = specialty;
            this.degree = degree;
            this.hospital = hospital;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.isOnline = isOnline;
            this.experienceYears = experienceYears;
            this.doctorType = doctorType;
            this.hasAvailableSlot = true;
            this.isTopDoctor = rating >= 4.8;
        }

        public double getSimilarityScore() { return similarityScore; }
        public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }

        public String getMatchedImageUrl() { return matchedImageUrl; }
        public void setMatchedImageUrl(String matchedImageUrl) { this.matchedImageUrl = matchedImageUrl; }
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getSpecialty() { return specialty; }
        public String getDegree() { return degree; }
        public String getHospital() { return hospital; }
        public String getDescription() { return description; }
        public float getRating() { return rating; }
        public int getReviewCount() { return reviewCount; }
        public boolean isOnline() { return isOnline; }
        public boolean hasAvailableSlot() { return hasAvailableSlot; }
        public boolean isTopDoctor() { return isTopDoctor; }
        public int getExperienceYears() { return experienceYears; }
        public String getAvatarUrl() { return avatarUrl; }
        public String getDoctorType() { return doctorType; }

        // Setters
        public void setDescription(String description) { this.description = description; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public void setOnline(boolean online) { isOnline = online; }
        public void setAvailableSlot(boolean availableSlot) { hasAvailableSlot = availableSlot; }

        public String getFormattedInfo() {
            return specialty + " • " + degree + " • " + experienceYears + " năm KN";
        }

        public String getStarString() {
            int stars = (int) Math.round(rating);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(i < stars ? "★" : "☆");
            }
            return sb.toString();
        }
    }
