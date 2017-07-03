/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/6/27 16:32:08                           */
/*==============================================================*/

drop table if exists CustomerInfo;

drop table if exists Offers;

drop table if exists PendingGames;

drop table if exists TradeInfo;

drop table if exists Wishes;

drop table if exists TradeGames;

drop table if exists TradeOrders;

drop table if exists Games;

drop table if exists Addresses;

drop table if exists Users;
/*==============================================================*/
/* Table: Addresses                                             */
/*==============================================================*/
create table Addresses
(
   addressID            int not null auto_increment,
   userID               int,
   receiver             varchar(255),
   phone                varchar(127),
   region               varchar(255),
   address              varchar(255),
   primary key (addressID)
);

/*==============================================================*/
/* Table: CustomerInfo                                          */
/*==============================================================*/
create table CustomerInfo
(
   userID               int not null,
   email                varchar(255),
   phone                varchar(127),
   rating               int,
   primary key (userID)
);

/*==============================================================*/
/* Table: Games                                                 */
/*==============================================================*/
create table Games
(
   gameID               int not null auto_increment,
   title                varchar(1024),
   platform             varchar(1024),
   language             varchar(1024),
   genre                varchar(1024),
   evaluatePoint        int,
   primary key (gameID)
);

/*==============================================================*/
/* Table: Offers                                                */
/*==============================================================*/
create table Offers
(
   userID               int not null,
   gameID               int not null,
   points               int,
   status               int,
   createtime           timestamp,
   primary key (userID, gameID)
);

/*==============================================================*/
/* Table: PendingGames                                          */
/*==============================================================*/
create table PendingGames
(
   PendingGamesID       int not null auto_increment,
   proposerID           int,
   reviewerID           int,
   title                varchar(1024),
   platform             varchar(1024),
   language             varchar(1024),
   genre                varchar(1024),
   status               int,
   primary key (PendingGamesID)
);

/*==============================================================*/
/* Table: TradeGames                                            */
/*==============================================================*/
create table TradeGames
(
   tradeGameID          int not null auto_increment,
   tradeOrderID         int,
   gameID               int not null,
   trackingNumber       varchar(1024),
   points               int,
   status               int,
   primary key (tradeGameID)
);

/*==============================================================*/
/* Table: TradeInfo                                             */
/*==============================================================*/
create table TradeInfo
(
   tradeGameID          int not null,
   senderID             int,
   receiverID           int,
   fromAddressID        int not null,
   toAddressID          int not null,
   transferAddressID    int,
   primary key (tradeGameID)
);

/*==============================================================*/
/* Table: TradeOrders                                           */
/*==============================================================*/
create table TradeOrders
(
   tradeOrderID         int not null auto_increment,
   createtime           timestamp,
   status               int,
   primary key (tradeOrderID)
);

/*==============================================================*/
/* Table: Users                                                 */
/*==============================================================*/
create table Users
(
   userID               int not null auto_increment,
   username             varchar(31),
   password             varchar(31),
   role                 int,
   primary key (userID),
   key AK_usernameIdentifier (username)
);

/*==============================================================*/
/* Table: Wishes                                                */
/*==============================================================*/
create table Wishes
(
   userID               int not null,
   gameID               int not null,
   points               int,
   status               int,
   createtime           timestamp,
   primary key (userID, gameID)
);

alter table Addresses add constraint FK_hasAddress foreign key (userID)
      references Users (userID) on delete restrict on update restrict;

alter table CustomerInfo add constraint FK_userInfo foreign key (userID)
      references Users (userID) on delete restrict on update restrict;

alter table Offers add constraint FK_Offers foreign key (userID)
      references Users (userID) on delete restrict on update restrict;

alter table Offers add constraint FK_Offers2 foreign key (gameID)
      references Games (gameID) on delete restrict on update restrict;

alter table PendingGames add constraint FK_Relationship_6 foreign key (reviewerID)
      references Users (userID) on delete restrict on update restrict;

alter table PendingGames add constraint FK_audit foreign key (proposerID)
      references Users (userID) on delete restrict on update restrict;

alter table TradeGames add constraint FK_GameAsTradeGame foreign key (gameID)
      references Games (gameID) on delete restrict on update restrict;

alter table TradeGames add constraint FK_GamesInOrder foreign key (tradeOrderID)
      references TradeOrders (tradeOrderID) on delete restrict on update restrict;

alter table TradeInfo add constraint FK_ReceiveAddress foreign key (toAddressID)
      references Addresses (addressID) on delete restrict on update restrict;

alter table TradeInfo add constraint FK_Receiver foreign key (receiverID)
      references Users (userID) on delete restrict on update restrict;

alter table TradeInfo add constraint FK_SendAddress foreign key (fromAddressID)
      references Addresses (addressID) on delete restrict on update restrict;

alter table TradeInfo add constraint FK_SendTradeGame foreign key (tradeGameID)
      references TradeGames (tradeGameID) on delete restrict on update restrict;

alter table TradeInfo add constraint FK_Sender foreign key (senderID)
      references Users (userID) on delete restrict on update restrict;

alter table TradeInfo add constraint FK_TransferAddress foreign key (transferAddressID)
      references Addresses (addressID) on delete restrict on update restrict;

alter table Wishes add constraint FK_Wishes foreign key (userID)
      references Users (userID) on delete restrict on update restrict;

alter table Wishes add constraint FK_Wishes2 foreign key (gameID)
      references Games (gameID) on delete restrict on update restrict;

