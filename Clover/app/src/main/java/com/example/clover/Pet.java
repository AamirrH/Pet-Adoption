package com.example.clover;

public class Pet {
    private long id;
    private String name, type, breed, age, size, temperament, description, emoji;
    private boolean isAdopted;

    public Pet() {}

    public Pet(long id, String name, String type, String breed, String age, String size,
               String temperament, String description, String emoji, boolean isAdopted) {
        this.id = id; this.name = name; this.type = type; this.breed = breed;
        this.age = age; this.size = size; this.temperament = temperament;
        this.description = description; this.emoji = emoji; this.isAdopted = isAdopted;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getTemperament() { return temperament; }
    public void setTemperament(String temperament) { this.temperament = temperament; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public boolean isAdopted() { return isAdopted; }
    public void setAdopted(boolean adopted) { isAdopted = adopted; }
}
