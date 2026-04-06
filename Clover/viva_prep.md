# 🌿 CLOVER — VIVA PREPARATION GUIDE
## Everything Your Teacher Can Ask — With Answers

---

# PART 1: PROJECT OVERVIEW QUESTIONS

**Q: What is your app?**
A: Clover is a pet adoption app built in Java for Android. Users can browse pets (dogs, cats, birds, rabbits), submit adoption applications, take a pet care quiz, share pet info, call/locate shelters, export certificates, and manage their profile. It works 100% offline using SQLite and SharedPreferences.

**Q: What language and tools are used?**
A: Java for logic, XML for UI layouts, SQLite for database, SharedPreferences for small key-value storage, Android SDK (API 24-36), Gradle build system.

**Q: How many Activities and what are they?**
A: 8 Activities:
1. SplashActivity — splash screen with 2-sec delay
2. RegistrationActivity — first-time user setup
3. MainActivity — home screen, browse pets
4. PetDetailActivity — one pet's full details
5. AdoptionFormActivity — adoption application form
6. MyApplicationsActivity — view/withdraw applications
7. QuizActivity — pet care quiz
8. SettingsActivity — profile, export, reset

**Q: How many model classes?**
A: 2 — `Pet.java` (holds pet data) and `AdoptionApplication.java` (holds application data). They are POJOs with private fields + getters/setters.

**Q: What is the package name?**
A: `com.example.clover`

**Q: What is minSdk and targetSdk?**
A: minSdk = 24 (Android 7.0), targetSdk = 36 (latest).

---

# PART 2: ANDROID FUNDAMENTALS

**Q: What is an Activity?**
A: One screen in the app. Each Activity = one Java class + one XML layout.

**Q: What is `setContentView(R.layout.activity_splash)`?**
A: Loads the XML layout file `activity_splash.xml` and displays it on screen.

**Q: What is `findViewById(R.id.btnSubmit)`?**
A: Finds a UI element in the loaded XML by its ID and returns a reference so Java code can interact with it.

**Q: What is R?**
A: An auto-generated class that maps every resource (layout, ID, color, string, drawable) to a unique integer. Never edited manually.

**Q: What is `this`?**
A: Refers to the current Activity instance. Used as Context for methods like `Toast.makeText(this, ...)`.

**Q: What is casting — `(TextView) findViewById(...)`?**
A: `findViewById` returns a generic View. Casting to `(TextView)` tells Java it's specifically a TextView so we can use `.setText()`.

**Q: What is `extends AppCompatActivity`?**
A: Our class inherits from AppCompatActivity, which gives us access to Activity features like `onCreate`, `setContentView`, `startActivity`.

**Q: What is `@Override`?**
A: Tells the compiler we are replacing the parent class's version of this method with our own.

**Q: What is `super.onCreate(savedInstanceState)`?**
A: Calls the parent class's `onCreate()`. Must be called first so Android can do its internal setup.

**Q: What is `Bundle savedInstanceState`?**
A: Android passes this to save/restore the screen's state when the screen is rotated or recreated.

**Q: What is a lambda `v -> { ... }`?**
A: Shorthand for an anonymous inner class with one method. Same as `new View.OnClickListener() { @Override public void onClick(View v) { ... } }`.

---

# PART 3: ACTIVITY LIFECYCLE

**Q: Name all 7 lifecycle methods in order.**
A: `onCreate()` → `onStart()` → `onResume()` → RUNNING → `onPause()` → `onStop()` → `onDestroy()`. Plus `onRestart()` (after stop, before start again).

**Q: When is each called?**
| Method | When |
|---|---|
| `onCreate()` | Activity created for the first time — initialize here |
| `onStart()` | Activity becoming visible |
| `onResume()` | Activity in foreground and interactive |
| `onPause()` | Another activity coming to foreground |
| `onStop()` | Activity no longer visible |
| `onDestroy()` | Activity being destroyed |
| `onRestart()` | Activity restarting after being stopped |

**Q: Where is lifecycle demonstrated?**
A: `SplashActivity.java` — all 7 methods overridden with `Log.d()` calls. `MainActivity.java` also logs lifecycle events.

**Q: What is `Log.d(TAG, "message")`?**
A: Prints a debug message to Logcat. TAG is a constant label (`"CloverLifecycle"`) used to filter our logs.

**Q: Why do you use `onResume()` in MainActivity?**
A: Because it runs every time the screen becomes visible — including when returning from another screen. This refreshes the pet counts and list after changes.

---

# PART 4: LAYOUTS

**Q: What 3 layout types are used?**
A: LinearLayout (arranges children in a line — vertical/horizontal), RelativeLayout (positions children relative to each other), ConstraintLayout (constrains elements to edges of parent/siblings — most flexible).

**Q: Where is each used?**
A: LinearLayout — pet list, bottom nav, form fields. RelativeLayout — stats cards on home. ConstraintLayout — outer container for splash, home, detail, form.

**Q: What is `layout_weight="1"`?**
A: Distributes remaining space proportionally. 4 nav buttons with weight=1 each get 25% width.

**Q: What is `visibility="gone"` vs `invisible`?**
A: GONE = hidden AND takes no space. INVISIBLE = hidden but still occupies space.

**Q: What is ScrollView?**
A: A container that makes its content scrollable when content is taller than the screen.

**Q: What is LayoutInflater?**
A: Converts an XML layout into a real View object in memory. Used to create pet cards dynamically in a loop.

---

# PART 5: INTENTS

**Q: What is an Intent?**
A: A message object used to request an action — either open a screen or ask the OS to do something.

**Q: Explicit vs Implicit Intent?**
A: **Explicit** — specifies the exact Activity: `new Intent(this, PetDetailActivity.class)`. Used for in-app navigation.
**Implicit** — specifies an ACTION: `new Intent(Intent.ACTION_DIAL, Uri.parse("tel:123"))`. Android finds an app that handles it.

**Q: How do you pass data between Activities?**
A: Sending: `intent.putExtra("pet_id", 5L)`. Receiving: `getIntent().getLongExtra("pet_id", -1)`.

**Q: List all implicit intents in the app.**
A: 1) `ACTION_SEND` — share pet info via WhatsApp/Email. 2) `ACTION_DIAL` — open phone dialer. 3) `ACTION_VIEW` with `geo:` URI — open Google Maps. 4) `ACTION_SEND` — share adoption report.

**Q: What is `Intent.createChooser()`?**
A: Shows a popup letting the user choose which app to use (WhatsApp, Gmail, etc).

**Q: What is `finish()`?**
A: Destroys the current Activity. User can't press Back to return.

**Q: What are `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`?**
A: Clears the entire back stack and starts fresh. Used during Reset to restart the app.

---

# PART 6: SHARED PREFERENCES

**Q: What is SharedPreferences?**
A: Lightweight key-value storage. Stored as XML file on device. Persists across app restarts. Good for small data like settings.

**Q: What data is stored?**
A: `is_first_launch` (boolean), `user_name` (String), `user_phone` (String), `user_city` (String), `pet_preference` (String), `quiz_high_score` (int).

**Q: How to write?**
A: `getSharedPreferences("CloverPrefs", MODE_PRIVATE).edit().putString("user_name", "Rahul").apply();`

**Q: How to read?**
A: `getSharedPreferences("CloverPrefs", MODE_PRIVATE).getString("user_name", "Default");` — second arg is default if key missing.

**Q: `apply()` vs `commit()`?**
A: `apply()` saves in background (async, preferred). `commit()` saves immediately and blocks the UI thread.

**Q: What is `MODE_PRIVATE`?**
A: Only this app can access the file. No other app can read it.

---

# PART 7: SQLite DATABASE

**Q: What is SQLite?**
A: Lightweight relational database stored as a single file on device. No server needed.

**Q: How many tables? Name and describe them.**
A: 2 tables.
`pets` — _id, name, type, breed, age, size, temperament, description, emoji, is_adopted (0/1).
`applications` — _id, pet_id, pet_name, adopter_name, phone, address, housing_type, has_other_pets, experience, reason, date, status.

**Q: What is `SQLiteOpenHelper`?**
A: Android's helper class for DB creation/versioning. Override `onCreate()` (creates tables first time) and `onUpgrade()` (recreates when version changes).

**Q: What is AUTOINCREMENT?**
A: Database auto-assigns incrementing IDs (1, 2, 3...) for each new row.

**Q: What is ContentValues?**
A: A map of column→value pairs for INSERT/UPDATE operations. Like a shopping basket.

**Q: What is a Cursor?**
A: A pointer that walks through query results row by row. Use `moveToFirst()`, `moveToNext()`, `getString(index)`.

**Q: What CRUD operations exist?**
A: **C**reate: `insertApplication()` — INSERT into applications table.
**R**ead: `getAllPets()`, `getPetsByType()`, `getPetById()`, `getAllApplications()`, `getAvailableCount()`.
**U**pdate: `markAdopted()` — sets is_adopted=1.
**D**elete: `deleteApplication()` — remove one app. `deleteAll()` — wipe everything + reseed pets.

**Q: What is `?` in SQL queries?**
A: A placeholder that prevents SQL injection. Actual value is passed in a separate string array.

**Q: What does `getReadableDatabase()` vs `getWritableDatabase()` do?**
A: Readable = for SELECT queries. Writable = for INSERT, UPDATE, DELETE.

**Q: What does `seedPets()` do?**
A: Pre-loads 10 sample pets into the database when it's first created.

**Q: What does `deleteAll()` do?**
A: Deletes all applications, deletes all pets, then re-inserts 10 sample pets.

**Q: Explain `cursorToPet(Cursor c)`.**
A: Reads one row from the Cursor and creates a Pet object. `c.getLong(0)` = column 0 (_id), `c.getString(1)` = column 1 (name), etc. `c.getInt(9) == 1` converts 0/1 to boolean.

---

# PART 8: EVENT HANDLING & FORM VALIDATION

**Q: How do you handle button clicks?**
A: `button.setOnClickListener(v -> { code })`. The `v` is the View (button) clicked.

**Q: What is TextWatcher?**
A: An interface that monitors EditText changes in real-time. Has 3 methods: `beforeTextChanged`, `onTextChanged`, `afterTextChanged`. We use `afterTextChanged` to call `validateForm()`.

**Q: How does form validation work?**
A: A TextWatcher is attached to all 4 text fields + listeners on both RadioGroups. Every change triggers `validateForm()` which checks:
- Name not empty
- Phone exactly 10 digits
- Address not empty
- One housing RadioButton selected
- One other-pets RadioButton selected
- Reason at least 10 characters
If ALL true → button enabled (orange). If ANY false → button disabled (grey).

**Q: What does `.trim()` do?**
A: Removes leading/trailing whitespace. `"  Rahul  ".trim()` → `"Rahul"`.

**Q: What does `setError("message")` do?**
A: Shows a red error message on the EditText field.

**Q: What does `requestFocus()` do?**
A: Moves the cursor to that field so user can fix the error.

---

# PART 9: RadioGroup & RadioButton

**Q: What is a RadioGroup?**
A: A container for RadioButtons where only ONE can be selected at a time.

**Q: Where used?**
A: Adoption form — Housing Type (Apartment/House/Farm), Other Pets (Yes/No). Quiz — 5 questions with 3 options each.

**Q: How to check if something is selected?**
A: `rgHousing.getCheckedRadioButtonId()` — returns -1 if nothing selected.

**Q: How to get selected text?**
A: `RadioButton rb = findViewById(rgHousing.getCheckedRadioButtonId()); String text = rb.getText().toString();`

---

# PART 10: AlertDialog

**Q: What is AlertDialog?**
A: A popup window for confirmation. Has title, message, positive button (Yes), negative button (Cancel).

**Q: Where is it used?**
A: 1) Adoption form — confirm submit. 2) My Applications — confirm withdraw. 3) Quiz — confirm submit. 4) Settings — confirm reset.

**Q: How to create?**
```java
new AlertDialog.Builder(this)
    .setTitle("Confirm")
    .setMessage("Are you sure?")
    .setPositiveButton("Yes", (d, w) -> { /* action */ })
    .setNegativeButton("Cancel", (d, w) -> d.dismiss())
    .show();
```

**Q: What is `d.dismiss()`?**
A: Closes the dialog.

**Q: What is `String.format()`?**
A: Replaces `%1$s` placeholder with a value. `String.format("Adopt %1$s?", "Buddy")` → `"Adopt Buddy?"`.

---

# PART 11: Notifications

**Q: What is a Notification?**
A: A message in the phone's status bar, visible even when app is not open.

**Q: What is a NotificationChannel?**
A: Required on Android 8.0+ (API 26). A category that lets users control notification settings per type. Created using `NotificationChannel(id, name, importance)`.

**Q: What is PendingIntent?**
A: An Intent that executes LATER when the notification is tapped. Like a sealed envelope Android opens when the time comes.

**Q: What does `FLAG_IMMUTABLE` mean?**
A: The PendingIntent can't be modified after creation. Required for Android 12+.

**Q: What does `setAutoCancel(true)` do?**
A: Notification disappears when the user taps it.

**Q: Where are notifications used?**
A: 1) After submitting adoption application — "Application Submitted!". 2) After quiz — "Quiz Results Ready!".

---

# PART 12: Internal Storage

**Q: What is Internal Storage?**
A: Private file storage on the device. Only this app can access files. Located at `/data/data/com.example.clover/files/`.

**Q: Where is it used?**
A: Settings → "Export Certificate" button writes `clover_certificate.txt` to internal storage.

**Q: How to write a file?**
```java
FileOutputStream fos = openFileOutput("file.txt", Context.MODE_PRIVATE);
fos.write("text".getBytes());
fos.close();
```

**Q: What is `.getBytes()`?**
A: Converts String to byte array because FileOutputStream writes bytes, not Strings.

**Q: What is try-catch?**
A: File operations can fail (disk full, etc). `try` attempts it, `catch` handles the error gracefully without crashing.

---

# PART 13: Spinner (Dropdown)

**Q: What is a Spinner?**
A: A dropdown menu. Options come from a string-array in strings.xml via `android:entries`.

**Q: How to get selected value?**
A: `spinner.getSelectedItem().toString()`

**Q: Where used?**
A: Registration — pet preference (Any/Dog/Cat/Bird/Rabbit). Home — filter pets. Adoption form — experience level.

---

# PART 14: Toast vs AlertDialog

**Q: Difference between Toast and AlertDialog?**
A: Toast = auto-disappearing popup, no user interaction. AlertDialog = stays until user presses a button, requires interaction.

---

# PART 15: LINE-BY-LINE CODE — EVERY JAVA FILE

---

## FILE: SplashActivity.java

| Line | Code | Meaning |
|---|---|---|
| 1 | `package com.example.clover;` | File belongs to clover package |
| 3-8 | `import ...;` | Import required Android classes |
| 10 | `public class SplashActivity extends AppCompatActivity` | SplashActivity IS an Activity (inherits from AppCompatActivity) |
| 11 | `private static final String TAG = "CloverLifecycle";` | Constant label for Logcat messages. static=shared, final=unchangeable |
| 14 | `protected void onCreate(Bundle savedInstanceState)` | Called when Activity is created first time |
| 15 | `super.onCreate(savedInstanceState);` | Let Android do its internal setup |
| 16 | `Log.d(TAG, "SplashActivity: onCreate()");` | Print debug log to Logcat |
| 17 | `setContentView(R.layout.activity_splash);` | Show the splash XML layout on screen |
| 19 | `new Handler().postDelayed(() -> {` | Run the code inside after 2000ms (2 seconds) |
| 20 | `SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);` | Open the key-value storage file |
| 21 | `boolean first = prefs.getBoolean("is_first_launch", true);` | Read is_first_launch. Default true if key doesn't exist |
| 22 | `startActivity(new Intent(this, first ? RegistrationActivity.class : MainActivity.class));` | If first=true go to Registration, else go to Main. Ternary operator + Explicit Intent |
| 23 | `finish();` | Destroy splash so user can't go back to it |
| 24 | `}, 2000);` | The 2-second delay value |
| 27-32 | `onStart/onResume/onPause/onStop/onDestroy/onRestart` | Lifecycle methods — each logs its name to demonstrate lifecycle |

---

## FILE: RegistrationActivity.java

| Line | Code | Meaning |
|---|---|---|
| 13 | `setContentView(R.layout.activity_registration);` | Load registration layout |
| 15-18 | `EditText etName = findViewById(R.id.etRegName);` etc. | Get references to input fields and Spinner |
| 20 | `findViewById(R.id.btnGetStarted).setOnClickListener(v -> {` | When "Get Started" button is clicked, run this code |
| 21 | `String name = etName.getText().toString().trim();` | Get typed text, convert to String, remove spaces |
| 25 | `if (name.isEmpty()) { etName.setError("Enter your name"); etName.requestFocus(); return; }` | Validation — if empty, show error, move cursor there, stop |
| 26 | `phone.isEmpty() \|\| phone.length() != 10` | Validation — phone empty OR not 10 digits |
| 29 | `SharedPreferences.Editor editor = getSharedPreferences("CloverPrefs", MODE_PRIVATE).edit();` | Open SharedPreferences for writing |
| 30 | `editor.putBoolean("is_first_launch", false);` | Mark registration complete so splash skips it next time |
| 31-34 | `editor.putString("user_name", name);` etc. | Save all user data |
| 35 | `editor.apply();` | Commit changes (async, in background) |
| 37 | `Toast.makeText(this, "Welcome to Clover, " + name + "! 🐾", Toast.LENGTH_LONG).show();` | Show welcome popup |
| 38-39 | `startActivity(new Intent(this, MainActivity.class)); finish();` | Go to Home, destroy Registration |

---

## FILE: MainActivity.java

| Line | Code | Meaning |
|---|---|---|
| 14 | `private DatabaseHelper db;` | Database instance variable |
| 15 | `private LinearLayout petList;` | Container where pet cards are added dynamically |
| 25 | `db = new DatabaseHelper(this);` | Create database (first time creates tables + seeds 10 pets) |
| 26-31 | `petList = findViewById(...)` etc. | Get references to all UI elements |
| 33-34 | `tvUserName.setText(prefs.getString("user_name", "User"));` | Show user's name from SharedPreferences |
| 36-39 | `spinnerFilter.setOnItemSelectedListener(...)` | When user picks filter option (All/Dog/Cat/Bird/Rabbit), reload pet list |
| 37 | `loadPets(p.getItemAtPosition(pos).toString());` | Gets selected text and passes to loadPets |
| 41 | `setOnClickListener(v -> {})` | Home nav — does nothing (already on home) |
| 42-44 | `startActivity(new Intent(this, ...))` | Nav buttons — open Apps/Quiz/Settings via Explicit Intents |
| 48-55 | `onResume()` | Refreshes counts and pet list every time screen becomes visible |
| 51 | `String.valueOf(db.getAvailableCount())` | Convert integer count to String for setText |
| 58 | `filter.equals("All") ? db.getAllPets() : db.getPetsByType(filter)` | Ternary — get all pets or filter by type |
| 59 | `petList.removeAllViews();` | Clear existing cards to prevent duplicates |
| 62-63 | `tvEmpty.setVisibility(View.VISIBLE)` | Show "no pets" message if list is empty |
| 68 | `LayoutInflater.from(this).inflate(R.layout.item_pet_card, petList, false);` | Create one card View from XML template |
| 69 | `((TextView) item.findViewById(R.id.tvPetEmoji)).setText(pet.getEmoji());` | Cast to TextView, find INSIDE the card, set emoji |
| 75-81 | `if (pet.isAdopted())` | Change status text and color based on adoption state |
| 83-87 | `item.setOnClickListener(v -> { ... putExtra("pet_id", pet.getId()) ... })` | Click card → open detail with pet's ID attached |
| 88 | `petList.addView(item);` | Add finished card to the container |

---

## FILE: PetDetailActivity.java

| Line | Code | Meaning |
|---|---|---|
| 15 | `long petId = getIntent().getLongExtra("pet_id", -1);` | Receive pet_id sent from MainActivity. -1 = default if missing |
| 16 | `if (petId == -1) { finish(); return; }` | Safety check — close if no valid ID |
| 19 | `Pet pet = db.getPetById(petId);` | Fetch full pet data from SQLite |
| 22-29 | `((TextView) findViewById(...)).setText(pet.get...())` | Display all pet info on screen |
| 32-35 | `if (pet.isAdopted()) { btnAdopt.setEnabled(false); ... }` | Disable adopt button if already adopted |
| 37-43 | `btnAdopt.setOnClickListener → putExtra pet_id, pet_name, pet_emoji` | Explicit Intent to AdoptionFormActivity with 3 data pieces |
| 46-55 | `btnSharePet → ACTION_SEND + createChooser` | Implicit Intent — share pet info via WhatsApp/Email/etc |
| 57-59 | `btnCall → ACTION_DIAL + Uri.parse("tel:...")` | Implicit Intent — open dialer |
| 61-64 | `btnLocate → ACTION_VIEW + Uri.parse("geo:...")` | Implicit Intent — open Google Maps |

---

## FILE: AdoptionFormActivity.java

| Line | Code | Meaning |
|---|---|---|
| 16 | `private static final String CH_ID = "clover_channel";` | Notification channel ID |
| 29-31 | `petId = getIntent().getLongExtra(...)` etc. | Receive pet data from PetDetailActivity |
| 45-47 | `etName.setText(prefs.getString("user_name", ""));` | Pre-fill name and phone from SharedPreferences |
| 49 | `createNotificationChannel();` | Create notification channel (required for Android 8+) |
| 51-55 | `TextWatcher watcher = new TextWatcher() { afterTextChanged → validateForm() }` | Watches every keystroke and runs validation |
| 56-59 | `etName.addTextChangedListener(watcher);` (×4 fields) | Attach watcher to all 4 text fields |
| 60-61 | `rgHousing.setOnCheckedChangeListener((g, id) -> validateForm());` | RadioGroup changes also trigger validation |
| 66-76 | `validateForm()` | Checks all 6 conditions with && (AND). Enables/disables button + changes background |
| 70 | `rgHousing.getCheckedRadioButtonId() != -1` | -1 means nothing selected. != -1 means something IS selected |
| 78-86 | `showConfirmDialog()` | AlertDialog — "Are you sure?" Yes → submitApplication(). Cancel → dismiss |
| 88-114 | `submitApplication()` | Creates AdoptionApplication object, fills from form, inserts to SQLite, marks pet adopted, shows toast, sends notification, closes screen |
| 98 | `RadioButton rbHousing = findViewById(rgHousing.getCheckedRadioButtonId());` | Get selected RadioButton's text |
| 106 | `new SimpleDateFormat("yyyy-MM-dd"...).format(new Date())` | Get today's date as formatted string |
| 108 | `long id = db.insertApplication(app);` | INSERT into database. Returns row ID (>0 = success) |
| 117-131 | `sendNotification()` | Creates PendingIntent → builds notification → sends it |
| 120 | `PendingIntent.FLAG_IMMUTABLE` | Required for Android 12+ |
| 128 | `setAutoCancel(true)` | Notification disappears when tapped |
| 133-139 | `createNotificationChannel()` | Only on Android 8+ (Build.VERSION_CODES.O). Creates channel with HIGH importance |

---

## FILE: MyApplicationsActivity.java

| Line | Code | Meaning |
|---|---|---|
| 25-28 | `onResume() → loadApplications()` | Refresh list every time screen appears |
| 31 | `List<AdoptionApplication> apps = db.getAllApplications();` | SELECT all from applications table |
| 32 | `appsList.removeAllViews();` | Clear existing cards |
| 34-36 | `if (apps.isEmpty())` | Show empty message if no applications |
| 42 | `inflate(R.layout.item_application, appsList, false)` | Create one application card from XML template |
| 44 | `db.getPetEmojiByName(app.getPetName())` | Get pet's emoji by querying pets table |
| 51-60 | `switch (app.getStatus())` | Set status text color: Pending=amber, Approved=green, default=red |
| 62-73 | `btnWithdraw.setOnClickListener → AlertDialog → deleteApplication → loadApplications` | Confirm withdrawal, delete from SQLite, refresh list |

---

## FILE: QuizActivity.java

| Line | Code | Meaning |
|---|---|---|
| 14 | `CH_ID = "clover_quiz_channel"` | Separate notification channel for quiz |
| 22-26 | `rgQ1 = findViewById(R.id.rgQ1);` (×5) | Get all 5 RadioGroup references |
| 31-33 | `if (rgQ1.getCheckedRadioButtonId() == -1 \|\| ...)` | Check if ANY question unanswered |
| 34-35 | `Toast ... return;` | Show error toast and stop if unanswered |
| 38-43 | `AlertDialog → submitQuiz()` | Confirm before submitting |
| 48 | `int score = 0;` | Initialize score counter |
| 49-53 | `if (rgQ1.getCheckedRadioButtonId() == R.id.rbQ1A) score++;` | Check each answer. Correct = +1 |
| 55-57 | `if (score > high) prefs.edit().putInt("quiz_high_score", score).apply();` | Save new high score if beaten |
| 59 | `findViewById(R.id.scoreCard).setVisibility(View.VISIBLE);` | Show the hidden score card |
| 60 | `setText(score + " / 5")` | Display score |
| 62-65 | `if/else if/else` | Set feedback message based on score |
| 68-69 | `setEnabled(false); setText("Submitted ✓")` | Disable button after submission |
| 74-87 | `sendScoreNotification(score)` | Send notification with quiz results |

---

## FILE: SettingsActivity.java

| Line | Code | Meaning |
|---|---|---|
| 21-24 | `db = new DatabaseHelper(this); etName = findViewById...` | Init database and find form fields |
| 26-29 | `etName.setText(prefs.getString("user_name", ""));` | Load saved profile data into fields |
| 31-40 | `btnSaveProfile → validate → prefs.edit().putString() → apply()` | Save updated profile to SharedPreferences |
| 42 | `btnExport → exportCertificate()` | Export button click |
| 44 | `btnShare → shareReport()` | Share button click |
| 46-61 | `btnReset → AlertDialog → deleteAll() + clear SharedPrefs + restart` | Reset everything with confirmation |
| 53 | `prefs.edit().clear().putBoolean("is_first_launch", true).apply()` | Clear all prefs, set first_launch=true |
| 55-57 | `FLAG_ACTIVITY_NEW_TASK \| FLAG_ACTIVITY_CLEAR_TASK` | Clear entire back stack, restart fresh |
| 64-93 | `exportCertificate()` | Build text with StringBuilder, write to internal file with FileOutputStream |
| 66 | `StringBuilder sb = new StringBuilder();` | Efficiently builds large strings |
| 86 | `openFileOutput("clover_certificate.txt", Context.MODE_PRIVATE)` | Create file in app's private internal storage |
| 87 | `fos.write(sb.toString().getBytes());` | Convert string to bytes and write |
| 90 | `catch (IOException e)` | Handle file write errors gracefully |
| 95-109 | `shareReport()` | Build summary text + Implicit Intent ACTION_SEND + createChooser |

---

## FILE: DatabaseHelper.java

| Line | Code | Meaning |
|---|---|---|
| 11 | `extends SQLiteOpenHelper` | Inherits DB creation/versioning from Android's helper |
| 12-13 | `DB_NAME = "clover.db"; DB_VERSION = 1;` | Database file name and version |
| 15 | `super(context, DB_NAME, null, DB_VERSION)` | Pass db config to parent. null = no CursorFactory |
| 18-22 | `onCreate()` | Called ONCE when DB first created. Creates 2 tables + seeds data |
| 19 | `CREATE TABLE pets (...)` | SQL to create pets table with all columns |
| 20 | `CREATE TABLE applications (...)` | SQL to create applications table |
| 21 | `seedPets(db)` | Insert 10 sample pets |
| 24-29 | `onUpgrade()` | If DB_VERSION changes, drop both tables and recreate |
| 31-42 | `seedPets()` | Calls insertPet() 10 times with sample data |
| 44-49 | `insertPet()` | Creates ContentValues, puts all columns, calls db.insert |
| 51 | `getAllPets()` | SELECT * FROM pets ORDER BY is_adopted ASC — shows available first |
| 53 | `getPetsByType("Dog")` | SELECT WHERE type=? — filter by type. ? prevents SQL injection |
| 55-61 | `getPetById(id)` | SELECT WHERE _id=? — get one pet. Returns null if not found |
| 63-67 | `getAvailableCount()` | SELECT COUNT(*) WHERE is_adopted=0 — count available pets |
| 69-73 | `markAdopted(petId)` | UPDATE pets SET is_adopted=1 WHERE _id=? |
| 75-81 | `queryPets()` | Helper — runs SQL, loops through Cursor with do-while, builds List<Pet> |
| 79 | `if (c.moveToFirst()) { do { list.add(cursorToPet(c)); } while (c.moveToNext()); }` | Move to first row, process it, keep going until no more rows |
| 83-86 | `cursorToPet(c)` | Converts one Cursor row to Pet object. c.getLong(0)=_id, c.getString(1)=name, etc. c.getInt(9)==1 converts 0/1 to boolean |
| 88-96 | `insertApplication()` | INSERT into applications table. Returns new row ID |
| 98-104 | `getAllApplications()` | SELECT * FROM applications ORDER BY _id DESC — newest first |
| 106-110 | `getApplicationCount()` | SELECT COUNT(*) FROM applications |
| 112-115 | `deleteApplication(id)` | DELETE FROM applications WHERE _id=? |
| 117-122 | `deleteAll()` | Delete all apps, delete all pets, re-insert 10 pets |
| 124-131 | `cursorToApp(c)` | Converts one Cursor row to AdoptionApplication object |
| 133-137 | `getPetEmojiByName(name)` | SELECT emoji FROM pets WHERE name=? — returns "🐾" if not found |

---

# PART 16: PRACTICAL MAPPING

| # | Practical | Feature | Files |
|---|---|---|---|
| P1 | GUI, Fonts, Colors, Lifecycle | Warm theme, lifecycle logging | SplashActivity, MainActivity, colors.xml, themes.xml |
| P2 | LinearLayout, RelativeLayout, ConstraintLayout | Home uses all 3 nested | activity_main.xml |
| P3 | Event Handlers, Enable/Disable Button, Toast | Form validation, submit button | AdoptionFormActivity |
| P4 | ConstraintLayout, Input Validation | Form layout, name/phone/address/reason checks | AdoptionFormActivity, RegistrationActivity |
| P5 | Explicit + Implicit Intents | Navigation + Share/Call/Map | PetDetailActivity, MainActivity |
| P6 | RadioButtons, AlertDialog, Notifications, PendingIntent | Housing/Pets radio, confirm dialogs, notifications | AdoptionFormActivity, QuizActivity |
| P7 | SharedPreferences, Internal Storage | User profile storage, certificate file export | RegistrationActivity, SettingsActivity |
| P8 | SQLite CRUD | pets + applications tables, insert/select/update/delete | DatabaseHelper, all Activities |

---

# PART 17: RESOURCES QUICK REFERENCE

**colors.xml** — primary=#FF6B35 (orange), accent=#00BFA5 (teal), background=#FFF8F0 (cream), text_primary=#3E2723 (dark brown).

**strings.xml** — All app text externalized. Has 3 string-arrays for Spinners: pet_preferences, filter_types, experience_levels.

**Drawables** — btn_primary.xml (orange rounded rect), btn_accent.xml (teal), btn_disabled.xml (grey), card_bg.xml (white rounded rect), circle_bg.xml (orange circle), splash_bg.xml (orange gradient).

**themes.xml** — Base theme extends Material3 NoActionBar. Sets colorPrimary, statusBarColor, windowBackground.

---

*🌿 Clover Viva Prep — Generated for SL-II Lab*
