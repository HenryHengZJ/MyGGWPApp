var firebase = require ('firebase');

firebase.initializeApp({
	serviceAccount: "./myggapp-service-account.json",
	databaseURL: "https://my-gg-app.firebaseio.com"
});

var ref = firebase.database().ref('Node-Client');
var messageRef = ref.child('messages');

var messageRef = messageRef.push();

console.log(messageRef.key);

messageRef.set({
	name:'Heng',
	admin:true,
	count:1,
	text: 'Hey guys'
});
