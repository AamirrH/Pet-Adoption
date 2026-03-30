package com.example.clover;

public class AdoptionApplication {
    private long id, petId;
    private String petName, adopterName, phone, address, housingType, hasOtherPets, experience, reason, date, status;

    public AdoptionApplication() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getPetId() { return petId; }
    public void setPetId(long petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getAdopterName() { return adopterName; }
    public void setAdopterName(String adopterName) { this.adopterName = adopterName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getHousingType() { return housingType; }
    public void setHousingType(String housingType) { this.housingType = housingType; }
    public String getHasOtherPets() { return hasOtherPets; }
    public void setHasOtherPets(String hasOtherPets) { this.hasOtherPets = hasOtherPets; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
