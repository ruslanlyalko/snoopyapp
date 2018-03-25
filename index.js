const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.tokenUpdatedAt = functions.database.ref("USERS/{userId}/token")
.onWrite(event => {	
	const userRef = event.data.ref.parent;
	const tokenUpdatedAt = userRef.child('tokenUpdatedAt');
	const userId = event.params.userId;

	date = new Date();
	var formatDate =  date.getDate()+"-"+(date.getMonth()+1)+" "+date.getHours()+":"+date.getMinutes();
	return tokenUpdatedAt.set(formatDate).then(() => {
		return console.log('Token just updated by user: ', userId );	
	});
});

exports.commentsWatcher = functions.database.ref("MESSAGES_COMMENTS/{messageId}/{commentId}")
.onCreate(event => {
	const commentObj = event.data.val();
	const root = event.data.ref.root;	
	const messageId = event.params.messageId;
	const userAvaRef = event.data.ref.child('userAvatar');
	const messageRef = root.child(`/MESSAGES/${messageId}`); 

	return messageRef.once('value').then( dialogSnap => {
		var messageObj = dialogSnap.val();
		// update last Comment
		dialogSnap.ref.child('lastComment').set(commentObj.userName + ": " + commentObj.message);
		// update updaetedAt
		dialogSnap.ref.child('updatedAt').set(commentObj.date);
		const usersRef = root.child(`USERS`);
		return usersRef.once('value').then(snapshot => {	
			var tokens = [];

			
			snapshot.forEach(child1 => {	
				childObj = child1.val();	
			
				if(childObj.userId !== commentObj.userId && childObj.receiveNotifications )
				{
					if (childObj.userIsAdmin){
						console.log("Update notifications for ", childObj.userName);						
						root.child(`/USERS_NOTIFICATIONS`).child(`/${childObj.userId}/${messageId}/key`).set(`${messageId}`);
						if(childObj.token ){
							console.info("Token Added for User: " + childObj.userName +", Token: ", childObj.token);
							tokens.push(childObj.token);		
						}			
					} else {
						dialogSnap.child('Members').forEach(member =>{
							if(childObj.userId === member.key){				
								// update Notifications
								console.log("Update notifications for ", childObj.userName);						
								root.child(`/USERS_NOTIFICATIONS`).child(`/${childObj.userId}/${messageId}/key`).set(`${messageId}`);
								if(childObj.token ){
									console.info("Token Added for User: " + childObj.userName +", Token: ", childObj.token);
									tokens.push(childObj.token);		
								}							
							} 	

						})
					}
				}	
			});
		
			// send Push
			var payload = {
				data:{						
					title: messageObj.title1,
					message: commentObj.userName + ": " + commentObj.message,			
					messageKey: messageId,
					senderId: commentObj.userId,
					senderName: commentObj.userName,
					receiverName: childObj.userName,
					type: "COMMENT"
				}
			};
			return sendNessagesViaFCM(tokens, payload);			
		});
	});
});


exports.reportsWatcher = functions.database.ref("REPORTS/{year}/{month}/{day}/{reportId}")
.onWrite(event => {
	var reportObj = event.data.val();
	var aTitle = "Звіт оновлено";	
	var aMessage = " Оновила ";	
	const root = event.data.ref.root;			

	if(!event.data.exists() && event.data.previous.exists()){
		console.log("Report removed ");
		reportObj = event.data.previous.val();
		aTitle = "Звіт видалено";
		aMessage = " Видалила ";
	} else
	if(event.data.exists() && !event.data.previous.exists()){
		console.log("Report created ");		
		aTitle = "Звіт створено";
		aMessage = " Створила ";
	} else{
		console.log("Report updated");		
	}
	
	var tokens = [];
	return root.child(`/USERS`).once('value').then(snapshot => {
		snapshot.forEach(user => {	
			userObj = user.val();
			if(userObj.userIsAdmin && userObj.token && userObj.userId !== reportObj.userId){	
				console.info("Token Added for User: " + userObj.userName +", userId: ", userObj.userId);									
				tokens.push(userObj.token);
			}			
		});//end for
		// send Push
		var payload = {
			data:{						
				title: aTitle,
				message: reportObj.userName + " звіт за " + reportObj.date + " - " + reportObj.total+"грн",				
				reportUserName: reportObj.userName,
				reportUserId: reportObj.userId,
				reportDate: reportObj.date,
				type: "REPORT"
			}
		};
		return sendNessagesViaFCM(tokens, payload);			
	}); 

});

exports.expenseWatcher = functions.database.ref("COSTS/{year}/{month}/{reportId}")
.onWrite(event => {
	var reportObj = event.data.val();
	var aTitle = "Витрату оновлено";
	var aMessage = " оновила: ";
	const root = event.data.ref.root;			

	if(!event.data.exists() && event.data.previous.exists()){
		console.log("Expense removed ");
		reportObj = event.data.previous.val();
		aTitle = "Витрату видалено";
		aMessage = " видалила: ";
	} else
	if(event.data.exists() && !event.data.previous.exists()){
		console.log("Expense created ");		
		aTitle = "Витрату створено";
		aMessage = " створила: ";
	} else{
		console.log("Expense updated");		
	}
	
	var tokens = [];
	return root.child(`/USERS`).once('value').then(snapshot => {
		snapshot.forEach(user => {	
			userObj = user.val();
			if(userObj.userIsAdmin && userObj.token && userObj.userId !== reportObj.userId){	
				console.info("Token Added for User: " + userObj.userName +", userId: ", userObj.userId);									
				tokens.push(userObj.token);
			}			
		});//end for
		// send Push
		var payload = {
			data:{						
				title: aTitle,
				message: reportObj.userName + aMessage + reportObj.title1+" "+ reportObj.date + " " + reportObj.time,				
				expenseUserName: reportObj.userName,
				expenseUserId: reportObj.userId,
				expenseDate: reportObj.date,
				type: "EXPENSE"
			}
		};
		return sendNessagesViaFCM(tokens, payload);			
	}); 

});


function sendNessagesViaFCM(tokens, payload){
	if(tokens.length > 0)
		return  admin.messaging().sendToDevice(tokens, payload)
			.then(response => {
				console.log("Successfully sent push: ", response);
				return 0;
			})
			.catch(error => {
				console.log("Error sending push: ", error);
			});			
	else 
		return console.log("No tokens to sned push");
}
