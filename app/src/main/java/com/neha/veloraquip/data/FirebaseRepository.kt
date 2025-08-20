package com.neha.veloraquip.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.neha.veloraquip.data.models.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    fun currentUid(): String? = auth.currentUser?.uid

    // ----------------- SIGN IN AND SAVE TOKEN -----------------
    suspend fun signInIfNeeded(): String {
        val user = auth.currentUser ?: throw Exception("User must login first!")
        val token = FirebaseMessaging.getInstance().token.await()
        val userRef = db.child("users").child(user.uid)
        val snap = userRef.get().await()
        if (!snap.exists()) {
            val new = User(
                uid = user.uid,
                username = user.email ?: "User-${user.uid.take(6)}",
                profilePic = "",
                online = true,
                fcmToken = token
            )
            userRef.setValue(new).await()
        } else {
            userRef.child("fcmToken").setValue(token)
            userRef.child("online").setValue(true)
        }
        return user.uid
    }

    suspend fun setOnline(online: Boolean) {
        currentUid()?.let { db.child("users").child(it).child("online").setValue(online).await() }
    }

    private fun chatIdFor(a: String, b: String): String =
        listOf(a, b).sorted().joinToString("_")

    // ----------------- SEND MESSAGE -----------------
    suspend fun sendMessage(toUid: String, text: String, fromName: String) {
        val from = currentUid() ?: return
        val chatId = chatIdFor(from, toUid)
        val msgKey = db.child("chats").child(chatId).child("messages").push().key!!

        val message = Message(
            id = msgKey,
            senderId = from,
            receiverId = toUid,
            text = text,
            timestamp = System.currentTimeMillis(),
            seen = false
        )

        val updates = hashMapOf<String, Any>(
            "/chats/$chatId/messages/$msgKey" to message,
            "/last/$from/$chatId" to message,
            "/last/$toUid/$chatId" to message
        )

        db.updateChildren(updates).await()

        // Push to /notify for client-side notification
        db.child("notify").child(toUid).push().setValue(
            mapOf(
                "fromUid" to from,
                "fromName" to fromName,
                "text" to text,
                "ts" to ServerValue.TIMESTAMP
            )
        )
    }

    // ----------------- MESSAGE FLOW -----------------
    fun messagesFlow(otherUid: String) = callbackFlow<List<Message>> {
        val me = currentUid() ?: run { trySend(emptyList()); close(); return@callbackFlow }
        val chatId = chatIdFor(me, otherUid)
        val ref = db.child("chats").child(chatId).child("messages")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                    .sortedBy { it.timestamp }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ----------------- CONVERSATIONS FLOW -----------------
    fun conversationsFlow() = callbackFlow<List<Conversation>> {
        val me = currentUid() ?: run { trySend(emptyList()); close(); return@callbackFlow }
        val lastRef = db.child("last").child(me)
        val usersRef = db.child("users")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastMap = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                usersRef.get().addOnSuccessListener { usersSnap ->
                    val users = usersSnap.children.mapNotNull { it.getValue(User::class.java) }
                    val convs = lastMap.mapNotNull { msg ->
                        val other = if (msg.senderId == me) msg.receiverId else msg.senderId
                        val otherUser = users.firstOrNull { it.uid == other } ?: return@mapNotNull null
                        Conversation(
                            chatId = listOf(me, other).sorted().joinToString("_"),
                            otherUser = otherUser,
                            lastMessage = msg.text,
                            lastTimestamp = msg.timestamp,
                            otherOnline = otherUser.online
                        )
                    }.sortedByDescending { it.lastTimestamp }
                    trySend(convs)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        lastRef.addValueEventListener(listener)
        awaitClose { lastRef.removeEventListener(listener) }
    }

    // ----------------- USERS FLOW -----------------
    fun usersFlow() = callbackFlow<List<User>> {
        val ref = db.child("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ----------------- SIGN OUT -----------------
    suspend fun signOut() {
        currentUid()?.let {
            setOnline(false)
        }
        auth.signOut()
    }
}
