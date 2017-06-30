
delete from Addresses;

delete from CustomerInfo;

delete from Games;

delete from Offers;

delete from PendingGames;

delete from TradeGames;

delete from TradeInfo;

delete from TradeOrders;

delete from Users;

delete from Wishes;

insert into Addresses(addressID, userID, receiver,phone,region,address)
    values( 0, 0, 'Alpha', 1234, 'America', 'goo');

insert into Addresses(addressID, userID, receiver,phone,region,address)
    values( 1, 1, 'Beta', 5678, 'HongKong', 'googoo');

insert into CustomerInfo(userID, email, phone,rating)
    values( 0, '000@gmail.com', 1234, 1)

insert into CustomerInfo(userID, email, phone,rating)
    values( 1, '111@gmail.com', 5678, 2)

insert into CustomerInfo(userID, email, phone,rating)
    values( 2, '222@gmail.com', 9012, 3)

insert into Games(gameID,title,platform,language,genre,evaluatePoint)
    values( 0, 'ORC', 'ps4', 'English', 'ARPG', 100)

insert into Games(gameID,title,platform,language,genre,evaluatePoint)
    values( 1, 'DOA', 'xbox', 'Japanese', 'ACT', 200)

insert into Offers( userID, gameID, points, status, createtime)
    values( 0, 0, 10, 1, '2011-05-09 11:49:45')

insert into Offers( userID, gameID, points, status, createtime)
    values( 1, 1, 10, 2, '2011-05-10 11:49:45')

insert into PendingGames( PendingGamesID, proposerID, reviwerID, title, platform, language, genre, status)
    values( 2, 0, 1, 'WAR3', 'pc', 'English', 'STG', 0)

insert into TradeGames( tradeGameID, tradeOrderID, gameID, trackingNumber, points, status)
    values( 0, 1, 0, '1111', 100, 0)

  insert into TradeInfo( tradeGameID, senderID, receiverID, fromAddressID, toAddressID, transferAddressID)
    values( 0, 0, 1, 0, 1, 9)

  insert into TradeOrders( tradeOrderID, createtime, status)
    values( 1, '2011-05-11 11:49:45', 1 )

insert into Users( userID, username, password, role)
    values( 0,  'Alpha', '00000000', 0)

insert into Users( userID, username, password, role)
    values( 1,  'Beta', '11111111', 0)

insert into Users( userID, username, password, role)
    values( 2,  'Gamma', '22222222', 1)

insert into Wishes( userID, gameID, points, status, createtime)
    values( 0, 1, 100, 0, '2011-05-12 11:49:45')
    
insert into Wishes( userID, gameID, points, status, createtime)
    values( 1, 0, 100, 1, '2011-05-13 11:49:45')
