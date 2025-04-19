Chat History Persistence, Navigation, and UI Enhancements was done.
#Feature Added:
	•	Home screen now scrollable, dynamically lists all saved chat.
	•	AI model selection dropdown and “Enter” button added to Home screen for starting new sessions.
	•	On clicking “Enter”, user is prompted to enter their API key for the selected model.
	•	A new chat session is launched, starting with a conversation ID.
	•	Chat save button implemented to save full conversation data.

 #Persistent Chat History (Room Integration)

Room database and all necessary files were created for data storage and retrieval:
	•	ChatMessageEntity.kt → Entity representing a single user/AI message.
	•	ChatDao.kt → DAO interface for reading and writing messages.
	•	ConversationEntity.kt → Represents a full conversation (ID, title, model).
	•	ConversationDao.kt → Insert, update, delete, and fetch conversations.
	•	ChatDatabase.kt → Room database class, now includes both entities and DAOs.
	•	Gradle synced with all required Room dependencies.

# Reactive State & Data Flow
	•	collectAsState() used to feed Room data directly into UI.
	•	Messages and conversation lists auto-update as the DB changes.
	•	UI reflects all saved sessions and reacts live to new data.

# Code Cleanup & Organization as necessary.

# UI Improvements & Responsiveness
	•	Polished Home and Chat layouts for mobile-friendliness.
	•	Ensured balanced spacing between dropdown and enter button.
	•	Used fillMaxWidth() and weighted layout where appropriate.
	•	Fonts, padding, alignment all tuned for visual clarity and usability.

 # Additional Functionalities
	•	Delete all chats feature implemented was made functional.
	•	Conversation list now shows:
	•	Title
	•	Model used
