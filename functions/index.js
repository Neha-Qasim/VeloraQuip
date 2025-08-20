// functions/index.js
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyOnNewMessage = functions.database
    .ref("/notify/{toUid}/{pushId}")
    .onCreate(async (snapshot, context) => {
        const data = snapshot.val();
        const toUid = context.params.toUid;
        const fromName = data.fromName || "VeloraQuip";
        const text = data.text || "New message";

        // Get FCM token of the recipient
        const userSnap = await admin.database().ref(`/users/${toUid}/fcmToken`).once("value");
        const token = userSnap.val();

        if (!token) return null;

        const message = {
            token,
            data: { fromName, text },
            notification: { title: fromName, body: text }
        };

        return admin.messaging().send(message);
    });
