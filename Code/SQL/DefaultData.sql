SET SQL_SAFE_UPDATE=0;

delete from CustomerInfo;

delete from Offers;

delete from Wishes;

delete from PendingGames;

delete from TradeGames;

delete from TradeInfo;

delete from TradeOrders;

delete from Addresses;

delete from Users;

delete from Games;


insert into Users( userID, username, password, role)
    values( 0,  'a', '00', 0);

insert into Users( userID, username, password, role)
    values( 0,  'Beta', '11111111', 0);

insert into Users( userID, username, password, role)
    values( 0,  'Gamma', '22222222', 1);
    
insert into CustomerInfo(userID, email, phone,rating)
    values( 1, '000@gmail.com', 1234, 1);

insert into CustomerInfo(userID, email, phone,rating)
    values( 2, '111@gmail.com', 5678, 2);

insert into CustomerInfo(userID, email, phone,rating)
    values( 3, '222@gmail.com', 9012, 3);

insert into Addresses(addressID, userID, receiver,phone,region,address)
    values( 0, 1, 'Alpha', 1234, 'America', 'goo');

insert into Addresses(addressID, userID, receiver,phone,region,address)
    values( 0, 2, 'Beta', 5678, 'HongKong', 'googoo');

insert into Games(gameID,title,platform,language,genre,evaluatePoint)
    values( 0, 'ORC', 'ps4', 'English', 'ARPG', 100);

insert into Games(gameID,title,platform,language,genre,evaluatePoint)
    values( 0, 'DOA', 'xbox', 'Japanese', 'ACT', 200);

insert into Offers( userID, gameID, points, status, createtime)
    values( 1, 1, 10, 1, '2011-05-09 11:49:45');

insert into Offers( userID, gameID, points, status, createtime)
    values( 2, 2, 10, 0, '2011-05-10 11:49:45');

insert into PendingGames( PendingGamesID, proposerID, reviewerID, title, platform, language, genre, status)
    values( 0, 1, 1, 'WAR3', 'pc', 'English', 'STG', 0);

insert into tradeinfo( tradeGameId, senderID, receiverId, fromAddressID, toAddressID, transferAddressID)
	values( 2, 3, 1, 2, 1, 2);

insert into tradeinfo(tradeGameID, senderID, receiverID, fromAddressID, toAddressID, transferAddressID)
	values( 1, 2, 1, 2, 1, 2);
    
insert into Wishes( userID, gameID, points, status, createtime)
    values( 1, 1, 100, 0, '2011-05-12 11:49:45');
    
insert into Wishes( userID, gameID, points, status, createtime)
    values( 2, 2, 100, 1, '2011-05-13 11:49:45');
