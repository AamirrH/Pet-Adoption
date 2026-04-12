package com.example.clover;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "clover.db";
    private static final int DB_VERSION = 3;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pets (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, breed TEXT NOT NULL, age TEXT NOT NULL, size TEXT NOT NULL, temperament TEXT NOT NULL, description TEXT NOT NULL, emoji TEXT NOT NULL, is_adopted INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE applications (_id INTEGER PRIMARY KEY AUTOINCREMENT, pet_id INTEGER NOT NULL, pet_name TEXT NOT NULL, adopter_name TEXT NOT NULL, phone TEXT NOT NULL, address TEXT NOT NULL, housing_type TEXT NOT NULL, has_other_pets TEXT NOT NULL, experience TEXT NOT NULL, reason TEXT NOT NULL, date TEXT NOT NULL, status TEXT DEFAULT 'Pending')");
        db.execSQL("CREATE TABLE notifications (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, message TEXT NOT NULL, type TEXT NOT NULL, pet_name TEXT, application_id INTEGER, is_read INTEGER DEFAULT 0, created_at TEXT NOT NULL)");
        seedPets(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {
        if (o < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS notifications (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, message TEXT NOT NULL, type TEXT NOT NULL, pet_name TEXT, application_id INTEGER, is_read INTEGER DEFAULT 0, created_at TEXT NOT NULL)");
        }
    }

    private void seedPets(SQLiteDatabase db) {
        insertPet(db, "Buddy", "Dog", "Golden Retriever", "2 years", "Large", "Friendly, Playful", "A lovable Golden Retriever who enjoys fetch and cuddling. Great with kids and other dogs.", "🐕");
        insertPet(db, "Whiskers", "Cat", "Persian", "1 year", "Medium", "Calm, Affectionate", "A fluffy Persian cat who loves lounging on laps. Very gentle and quiet.", "🐈");
        insertPet(db, "Tweety", "Bird", "Cockatiel", "6 months", "Small", "Chirpy, Social", "A cheerful Cockatiel that loves to whistle tunes and sit on your shoulder.", "🐦");
        insertPet(db, "Snowball", "Rabbit", "Holland Lop", "8 months", "Small", "Gentle, Curious", "An adorable Holland Lop with floppy ears. Loves exploring and being petted.", "🐇");
        insertPet(db, "Rocky", "Dog", "German Shepherd", "3 years", "Large", "Loyal, Protective", "A well-trained German Shepherd. Very loyal and great as a guard dog.", "🐕");
        insertPet(db, "Luna", "Cat", "Siamese", "2 years", "Medium", "Elegant, Vocal", "A beautiful Siamese cat with striking blue eyes. Loves to talk!", "🐈");
        insertPet(db, "Mango", "Bird", "Parrot", "1 year", "Medium", "Talkative, Colorful", "A vibrant green parrot that can mimic words. Very entertaining!", "🐦");
        insertPet(db, "Coco", "Rabbit", "Mini Rex", "4 months", "Small", "Playful, Soft", "A tiny Mini Rex rabbit with incredibly soft fur. Very playful.", "🐇");
        insertPet(db, "Daisy", "Dog", "Beagle", "1 year", "Medium", "Energetic, Friendly", "An energetic Beagle who loves outdoor adventures and sniffing everything.", "🐕");
        insertPet(db, "Shadow", "Cat", "Black Cat", "3 years", "Medium", "Independent, Mysterious", "A sleek black cat with golden eyes. Independent but secretly loves belly rubs.", "🐈");
    }

    private void insertPet(SQLiteDatabase db, String name, String type, String breed, String age, String size, String temp, String desc, String emoji) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("type", type); v.put("breed", breed); v.put("age", age);
        v.put("size", size); v.put("temperament", temp); v.put("description", desc); v.put("emoji", emoji);
        db.insert("pets", null, v);
    }

    public List<Pet> getAllPets() { return queryPets("SELECT * FROM pets ORDER BY is_adopted ASC, _id ASC", null); }

    public List<Pet> getPetsByType(String type) { return queryPets("SELECT * FROM pets WHERE type = ? ORDER BY is_adopted ASC", new String[]{type}); }

    public Pet getPetById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM pets WHERE _id = ?", new String[]{String.valueOf(id)});
        Pet p = null;
        if (c.moveToFirst()) p = cursorToPet(c);
        c.close(); db.close(); return p;
    }

    public int getAvailableCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM pets WHERE is_adopted = 0", null);
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close(); db.close(); return count;
    }

    public void markAdopted(long petId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues(); v.put("is_adopted", 1);
        db.update("pets", v, "_id = ?", new String[]{String.valueOf(petId)}); db.close();
    }

    public void markNotAdopted(long petId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues(); v.put("is_adopted", 0);
        db.update("pets", v, "_id = ?", new String[]{String.valueOf(petId)}); db.close();
    }

    private List<Pet> queryPets(String sql, String[] args) {
        List<Pet> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, args);
        if (c.moveToFirst()) { do { list.add(cursorToPet(c)); } while (c.moveToNext()); }
        c.close(); db.close(); return list;
    }

    private Pet cursorToPet(Cursor c) {
        return new Pet(c.getLong(0), c.getString(1), c.getString(2), c.getString(3),
                c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getInt(9) == 1);
    }

    public long insertApplication(AdoptionApplication a) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("pet_id", a.getPetId()); v.put("pet_name", a.getPetName()); v.put("adopter_name", a.getAdopterName());
        v.put("phone", a.getPhone()); v.put("address", a.getAddress()); v.put("housing_type", a.getHousingType());
        v.put("has_other_pets", a.getHasOtherPets()); v.put("experience", a.getExperience());
        v.put("reason", a.getReason()); v.put("date", a.getDate());
        long id = db.insert("applications", null, v); db.close(); return id;
    }

    public List<AdoptionApplication> getAllApplications() {
        List<AdoptionApplication> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM applications ORDER BY _id DESC", null);
        if (c.moveToFirst()) { do { list.add(cursorToApp(c)); } while (c.moveToNext()); }
        c.close(); db.close(); return list;
    }

    public int getApplicationCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM applications", null);
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close(); db.close(); return count;
    }

    public int getApplicationCountByStatus(String status) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM applications WHERE status = ?", new String[]{status});
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close(); db.close(); return count;
    }

    public void updateApplicationStatus(long id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("status", status);
        db.update("applications", v, "_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteApplication(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("applications", "_id = ?", new String[]{String.valueOf(id)}); db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("applications", null, null);
        db.delete("notifications", null, null);
        db.delete("pets", null, null);
        seedPets(db); db.close();
    }

    private AdoptionApplication cursorToApp(Cursor c) {
        AdoptionApplication a = new AdoptionApplication();
        a.setId(c.getLong(0)); a.setPetId(c.getLong(1)); a.setPetName(c.getString(2));
        a.setAdopterName(c.getString(3)); a.setPhone(c.getString(4)); a.setAddress(c.getString(5));
        a.setHousingType(c.getString(6)); a.setHasOtherPets(c.getString(7)); a.setExperience(c.getString(8));
        a.setReason(c.getString(9)); a.setDate(c.getString(10)); a.setStatus(c.getString(11));
        return a;
    }

    public String getPetEmojiByName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT emoji FROM pets WHERE name = ?", new String[]{name});
        String emoji = "🐾"; if (c.moveToFirst()) emoji = c.getString(0); c.close(); db.close(); return emoji;
    }

    public List<String> getAllCityNames() {
        // Returns distinct city names from applications for AutoComplete suggestions
        List<String> cities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT address FROM applications", null);
        if (c.moveToFirst()) {
            do { cities.add(c.getString(0)); } while (c.moveToNext());
        }
        c.close(); db.close();
        return cities;
    }

    // ==================== Notification Methods ====================

    public long insertNotification(String title, String message, String type, String petName, long applicationId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("message", message);
        v.put("type", type);
        v.put("pet_name", petName);
        v.put("application_id", applicationId);
        v.put("created_at", new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date()));
        long id = db.insert("notifications", null, v);
        db.close();
        return id;
    }

    public List<com.example.clover.Notification> getAllNotifications() {
        List<com.example.clover.Notification> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM notifications ORDER BY _id DESC", null);
        if (c.moveToFirst()) {
            do {
                com.example.clover.Notification n = new com.example.clover.Notification();
                n.setId(c.getLong(0));
                n.setTitle(c.getString(1));
                n.setMessage(c.getString(2));
                n.setType(c.getString(3));
                n.setPetName(c.getString(4));
                n.setApplicationId(c.getLong(5));
                n.setRead(c.getInt(6) == 1);
                n.setCreatedAt(c.getString(7));
                list.add(n);
            } while (c.moveToNext());
        }
        c.close(); db.close();
        return list;
    }

    public int getUnreadNotificationCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM notifications WHERE is_read = 0", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close(); db.close();
        return count;
    }

    public void markNotificationAsRead(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("is_read", 1);
        db.update("notifications", v, "_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void markAllNotificationsAsRead() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("is_read", 1);
        db.update("notifications", v, null, null);
        db.close();
    }

    public void deleteAllNotifications() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("notifications", null, null);
        db.close();
    }
}
