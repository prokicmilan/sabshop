
DROP DATABASE [SabShop]
go

CREATE DATABASE [SabShop]
CONTAINMENT = NONE
ON PRIMARY
( 
	NAME = 'SabShop', 
	FILENAME = 'C:\Program Files\Microsoft SQL Server\MSSQL14.SQLEXPRESS\MSSQL\DATA\SabShop.mdf', 
	SIZE = 8192, 
	MAXSIZE = UNLIMITED, 
	FILEGROWTH = 65536
) 
LOG ON 
( 
	NAME = 'SabShop_log', 
	FILENAME = 'C:\Program Files\Microsoft SQL Server\MSSQL14.SQLEXPRESS\MSSQL\DATA\SabShop_log.ldf', 
	SIZE = 8192, 
	MAXSIZE = UNLIMITED, 
	FILEGROWTH = 65536
) 
COLLATE SQL_Latin1_General_CP1_CI_AS
WITH 
	TRUSTWORTHY OFF,
	DB_CHAINING OFF,
	FILESTREAM( NON_TRANSACTED_ACCESS = OFF )
go

EXEC sp_db_vardecimal_storage_format [SabShop],'ON'
go

USE [SabShop]
go

EXECUTE sys.sp_cdc_disable_db 
go

ALTER DATABASE [SabShop]
SET
ONLINE,
MULTI_USER ,
READ_WRITE,
PARAMETERIZATION SIMPLE,
DATE_CORRELATION_OPTIMIZATION OFFDELAYED_DURABILITY = DISABLEDMEMORY_OPTIMIZED_ELEVATE_TO_SNAPSHOT OFF
go

ALTER DATABASE [SabShop]
SET CURSOR_CLOSE_ON_COMMIT OFF,
CURSOR_DEFAULT GLOBAL,
AUTO_CLOSE OFF,
AUTO_CREATE_STATISTICS ON,
AUTO_SHRINK OFF,
AUTO_UPDATE_STATISTICS ON,
AUTO_UPDATE_STATISTICS_ASYNC OFF,
ANSI_NULL_DEFAULT OFF,
ANSI_NULLS OFF,
ANSI_PADDING OFF,
ANSI_WARNINGS OFF,
ARITHABORT OFF,
CONCAT_NULL_YIELDS_NULL OFF,
NUMERIC_ROUNDABORT OFF,
QUOTED_IDENTIFIER OFF,
RECURSIVE_TRIGGERS OFF,
RECOVERY SIMPLE,
TORN_PAGE_DETECTION OFF,
PAGE_VERIFY CHECKSUM
go

ALTER DATABASE [SabShop]
SET ALLOW_SNAPSHOT_ISOLATION OFF
go

ALTER DATABASE [SabShop]
SET READ_COMMITTED_SNAPSHOT OFF
go

CREATE ASSEMBLY [Microsoft.SqlServer.Types]
AUTHORIZATION [sys]
FROM
'microsoft.sqlserver.types.dll'
WITH PERMISSION_SET = UNSAFE
go

ALTER ASSEMBLY [Microsoft.SqlServer.Types]
WITH
VISIBILITY = ON
go

ALTER DATABASE [SabShop]
SET ENCRYPTION OFF
go

ALTER DATABASE [SabShop]
SET CHANGE_TRACKING = OFF
go

ALTER DATABASE [SabShop]
 MODIFY FILEGROUP [PRIMARY] ReadWrite 
go

CREATE TABLE [Article]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[shopId]             bigint  NOT NULL ,
	[articleName]        varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[price]              decimal(10,3)  NOT NULL ,
	[itemsAvailable]     bigint  NOT NULL 
	CONSTRAINT [DF_Article_itemsAvailable]
		 DEFAULT  0
)
go

ALTER TABLE [Article]
	 WITH CHECK ADD CONSTRAINT [CK_Article_itemsAvailable_NotNegative] CHECK  ( itemsAvailable >= 0 )
go

ALTER TABLE [Article]
	ADD CONSTRAINT [PK_Article] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [ArticleInOrder]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[orderId]            bigint  NOT NULL ,
	[articleId]          bigint  NOT NULL ,
	[amount]             bigint  NOT NULL 
)
go

ALTER TABLE [ArticleInOrder]
	ADD CONSTRAINT [PK_ArticleInOrder] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [Buyer]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[cityId]             bigint  NOT NULL ,
	[name]               varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[balance]            decimal(10,3)  NOT NULL 
	CONSTRAINT [DF_Buyer_balance]
		 DEFAULT  0
)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [PK_Buyer] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [City]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[cityName]           varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [City]
	ADD CONSTRAINT [PK_City] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [Line]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[cityId1]            bigint  NOT NULL ,
	[cityId2]            bigint  NOT NULL ,
	[distance]           int  NOT NULL 
)
go

ALTER TABLE [Line]
	 WITH CHECK ADD CONSTRAINT [CK_distance_notNeg] CHECK  ( [distance]>(0) )
go

ALTER TABLE [Line]
	ADD CONSTRAINT [PK_Line] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [Order]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[buyerId]            bigint  NOT NULL ,
	[state]              varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
	CONSTRAINT [DF_Order_state]
		 DEFAULT  'created',
	[sentTime]           datetime2(7)  NULL ,
	[recievedTime]       datetime2(7)  NULL ,
	[cityId]             bigint  NULL 
)
go

ALTER TABLE [Order]
	 WITH CHECK ADD CONSTRAINT [CK_order_state] CHECK  ( [state]='arrived' OR [state]='sent' OR [state]='created' )
go

ALTER TABLE [Order]
	ADD CONSTRAINT [PK_Order] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [OrderPath]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[orderId]            bigint  NOT NULL ,
	[cityId]             bigint  NOT NULL ,
	[time]               bigint  NOT NULL ,
	[nextCityId]         bigint  NOT NULL 
)
go

ALTER TABLE [OrderPath]
	ADD CONSTRAINT [PK_OrderPath] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [Shop]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[cityId]             bigint  NOT NULL ,
	[balance]            decimal(10,3)  NOT NULL 
	CONSTRAINT [DF_Shop_balance]
		 DEFAULT  0,
	[shopName]           varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[discount]           decimal(10,3)  NOT NULL 
	CONSTRAINT [DF_Shop_discount]
		 DEFAULT  0
)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [PK_Shop] PRIMARY KEY  CLUSTERED ([id] ASC)
go

CREATE TABLE [Transaction]
( 
	[id]                 bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[orderId]            bigint  NOT NULL ,
	[buyerId]            bigint  NULL ,
	[shopId]             bigint  NULL ,
	[timeOfExecution]    datetime2(7)  NOT NULL ,
	[transactionAmount]  decimal(10,3)  NOT NULL ,
	[discountAmount]     decimal(10,3)  NULL ,
	[additionalDiscount] tinyint  NOT NULL 
)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [PK_Transaction] PRIMARY KEY  CLUSTERED ([id] ASC)
go


ALTER TABLE [Article] WITH CHECK 
	ADD CONSTRAINT [FK_Article_Shop] FOREIGN KEY ([shopId]) REFERENCES [Shop]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Article]
	  WITH CHECK CHECK CONSTRAINT [FK_Article_Shop]
go


ALTER TABLE [ArticleInOrder] WITH CHECK 
	ADD CONSTRAINT [FK_ArticleInOrder_Order] FOREIGN KEY ([orderId]) REFERENCES [Order]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [ArticleInOrder]
	  WITH CHECK CHECK CONSTRAINT [FK_ArticleInOrder_Order]
go

ALTER TABLE [ArticleInOrder] WITH CHECK 
	ADD CONSTRAINT [FK_ArticleInOrder_Article] FOREIGN KEY ([articleId]) REFERENCES [Article]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [ArticleInOrder]
	  WITH CHECK CHECK CONSTRAINT [FK_ArticleInOrder_Article]
go


ALTER TABLE [Buyer] WITH CHECK 
	ADD CONSTRAINT [FK_Buyer_City] FOREIGN KEY ([cityId]) REFERENCES [City]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Buyer]
	  WITH CHECK CHECK CONSTRAINT [FK_Buyer_City]
go


ALTER TABLE [Line] WITH CHECK 
	ADD CONSTRAINT [FK_Line_City] FOREIGN KEY ([cityId1]) REFERENCES [City]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Line]
	  WITH CHECK CHECK CONSTRAINT [FK_Line_City]
go

ALTER TABLE [Line] WITH CHECK 
	ADD CONSTRAINT [FK_Line_City1] FOREIGN KEY ([cityId2]) REFERENCES [City]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Line]
	  WITH CHECK CHECK CONSTRAINT [FK_Line_City1]
go


ALTER TABLE [Order] WITH CHECK 
	ADD CONSTRAINT [FK_Order_Buyer] FOREIGN KEY ([buyerId]) REFERENCES [Buyer]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Order]
	  WITH CHECK CHECK CONSTRAINT [FK_Order_Buyer]
go

ALTER TABLE [Order] WITH CHECK 
	ADD CONSTRAINT [FK_Order_City] FOREIGN KEY ([cityId]) REFERENCES [City]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Order]
	  WITH CHECK CHECK CONSTRAINT [FK_Order_City]
go


ALTER TABLE [OrderPath] WITH CHECK 
	ADD CONSTRAINT [FK_OrderPath_Order] FOREIGN KEY ([orderId]) REFERENCES [Order]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [OrderPath]
	  WITH CHECK CHECK CONSTRAINT [FK_OrderPath_Order]
go

ALTER TABLE [OrderPath] WITH CHECK 
	ADD CONSTRAINT [FK_OrderPath_City] FOREIGN KEY ([cityId]) REFERENCES [City]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [OrderPath]
	  WITH CHECK CHECK CONSTRAINT [FK_OrderPath_City]
go


ALTER TABLE [Shop] WITH CHECK 
	ADD CONSTRAINT [FK_Shop_City] FOREIGN KEY ([cityId]) REFERENCES [City]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Shop]
	  WITH CHECK CHECK CONSTRAINT [FK_Shop_City]
go


ALTER TABLE [Transaction] WITH CHECK 
	ADD CONSTRAINT  [CK_BuyerOrShopNotNull]
		CHECK  ( [buyerId] IS NOT NULL OR [shopId] IS NOT NULL ) 
go


ALTER TABLE [Transaction] WITH CHECK 
	ADD CONSTRAINT [FK_Transaction_Buyer] FOREIGN KEY ([buyerId]) REFERENCES [Buyer]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	  WITH CHECK CHECK CONSTRAINT [FK_Transaction_Buyer]
go

ALTER TABLE [Transaction] WITH CHECK 
	ADD CONSTRAINT [FK_Transaction_Shop] FOREIGN KEY ([shopId]) REFERENCES [Shop]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	  WITH CHECK CHECK CONSTRAINT [FK_Transaction_Shop]
go

ALTER TABLE [Transaction] WITH CHECK 
	ADD CONSTRAINT [FK_Transaction_Order] FOREIGN KEY ([orderId]) REFERENCES [Order]([id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	  WITH CHECK CHECK CONSTRAINT [FK_Transaction_Order]
go

CREATE FUNCTION [CalculateSystemProfit] ()  
  RETURNS decimal(10,3) 
  
AS BEGIN
	declare @sumPaidByBuyers decimal(10, 3);
	declare @sumPaidToShops decimal(10, 3);
	select @sumPaidByBuyers = sum(transactionAmount) from [Transaction] where buyerId is not null
	select @sumPaidToShops = sum(transactionAmount) from [Transaction] where shopId is not null

	if @sumPaidByBuyers is null or @sumPaidToShops is null
		return 0
	return @sumPaidByBuyers - @sumPaidToShops
END
go

CREATE PROCEDURE [SP_FINAL_PRICE] @orderId bigint , @price decimal(10,3)  OUTPUT   
   
 AS begin
	declare @totalSpentByBuyer decimal(10, 3)

	select
		@totalSpentByBuyer = sum(transactionAmount)
	from
		[Transaction]
	where
		buyerId = (select buyerId from [Order] where id = @orderId)

	declare @totalPrice decimal(10, 3)

	select
		@totalPrice = sum(a.price * aio.amount)
	from
		Article a
	join
		ArticleInOrder aio
	on
		aio.articleId = a.id
	where
		orderId = @orderId

	declare @totalDiscount decimal(10, 3)

	select
		@totalDiscount = sum(a.price * aio.amount * (s.discount / 100))
	from
		Article a
	join
		ArticleInOrder aio
	on
		aio.articleId = a.id
	join
		Shop s 
	on
		s.id = a.shopId
	where
		aio.orderId = @orderId
	and
		s.discount <> 0

	declare @finalPrice decimal(10, 3)
	set @finalPrice = @totalPrice - @totalDiscount

	if (@totalSpentByBuyer > 10000)
		set @price = @finalPrice - @finalPrice * 0.02
	else
		set @price = @finalPrice
end
go

CREATE TRIGGER dbo.tD_OrderPath ON dbo.OrderPath FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on OrderPath */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.City  dbo.OrderPath on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00028a63", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_City", FK_COLUMNS="cityId" */
    IF EXISTS (SELECT * FROM deleted,dbo.City
      WHERE
        /* %JoinFKPK(deleted,dbo.City," = "," AND") */
        deleted.cityId = dbo.City.id AND
        NOT EXISTS (
          SELECT * FROM dbo.OrderPath
          WHERE
            /* %JoinFKPK(dbo.OrderPath,dbo.City," = "," AND") */
            dbo.OrderPath.cityId = dbo.City.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.OrderPath because dbo.City exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Order  dbo.OrderPath on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_Order", FK_COLUMNS="orderId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Order
      WHERE
        /* %JoinFKPK(deleted,dbo.Order," = "," AND") */
        deleted.orderId = dbo.Order.id AND
        NOT EXISTS (
          SELECT * FROM dbo.OrderPath
          WHERE
            /* %JoinFKPK(dbo.OrderPath,dbo.Order," = "," AND") */
            dbo.OrderPath.orderId = dbo.Order.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.OrderPath because dbo.Order exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_OrderPath ON dbo.OrderPath FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on OrderPath */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.City  dbo.OrderPath on child update no action */
  /* ERWIN_RELATION:CHECKSUM="0002b4a7", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_City", FK_COLUMNS="cityId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(cityId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.City
        WHERE
          /* %JoinFKPK(inserted,dbo.City) */
          inserted.cityId = dbo.City.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.OrderPath because dbo.City does not exist.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.Order  dbo.OrderPath on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_Order", FK_COLUMNS="orderId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(orderId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Order
        WHERE
          /* %JoinFKPK(inserted,dbo.Order) */
          inserted.orderId = dbo.Order.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.OrderPath because dbo.Order does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tD_City ON dbo.City FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on City */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Order on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="000581c3", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_City", FK_COLUMNS="cityId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Order
      WHERE
        /*  %JoinFKPK(dbo.Order,deleted," = "," AND") */
        dbo.Order.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.City because dbo.Order exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Buyer on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Buyer"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Buyer_City", FK_COLUMNS="cityId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Buyer
      WHERE
        /*  %JoinFKPK(dbo.Buyer,deleted," = "," AND") */
        dbo.Buyer.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.City because dbo.Buyer exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Shop on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Shop"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Shop_City", FK_COLUMNS="cityId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Shop
      WHERE
        /*  %JoinFKPK(dbo.Shop,deleted," = "," AND") */
        dbo.Shop.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.City because dbo.Shop exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Line on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City1", FK_COLUMNS="cityId2" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Line
      WHERE
        /*  %JoinFKPK(dbo.Line,deleted," = "," AND") */
        dbo.Line.cityId2 = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.City because dbo.Line exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Line on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City", FK_COLUMNS="cityId1" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Line
      WHERE
        /*  %JoinFKPK(dbo.Line,deleted," = "," AND") */
        dbo.Line.cityId1 = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.City because dbo.Line exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.OrderPath on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_City", FK_COLUMNS="cityId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.OrderPath
      WHERE
        /*  %JoinFKPK(dbo.OrderPath,deleted," = "," AND") */
        dbo.OrderPath.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.City because dbo.OrderPath exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_City ON dbo.City FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on City */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Order on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00062787", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_City", FK_COLUMNS="cityId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Order
      WHERE
        /*  %JoinFKPK(dbo.Order,deleted," = "," AND") */
        dbo.Order.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.City because dbo.Order exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Buyer on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Buyer"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Buyer_City", FK_COLUMNS="cityId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Buyer
      WHERE
        /*  %JoinFKPK(dbo.Buyer,deleted," = "," AND") */
        dbo.Buyer.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.City because dbo.Buyer exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Shop on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Shop"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Shop_City", FK_COLUMNS="cityId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Shop
      WHERE
        /*  %JoinFKPK(dbo.Shop,deleted," = "," AND") */
        dbo.Shop.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.City because dbo.Shop exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Line on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City1", FK_COLUMNS="cityId2" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Line
      WHERE
        /*  %JoinFKPK(dbo.Line,deleted," = "," AND") */
        dbo.Line.cityId2 = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.City because dbo.Line exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Line on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City", FK_COLUMNS="cityId1" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Line
      WHERE
        /*  %JoinFKPK(dbo.Line,deleted," = "," AND") */
        dbo.Line.cityId1 = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.City because dbo.Line exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.OrderPath on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_City", FK_COLUMNS="cityId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.OrderPath
      WHERE
        /*  %JoinFKPK(dbo.OrderPath,deleted," = "," AND") */
        dbo.OrderPath.cityId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.City because dbo.OrderPath exists.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tD_Line ON dbo.Line FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on Line */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Line on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00025eaa", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City1", FK_COLUMNS="cityId2" */
    IF EXISTS (SELECT * FROM deleted,dbo.City
      WHERE
        /* %JoinFKPK(deleted,dbo.City," = "," AND") */
        deleted.cityId2 = dbo.City.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Line
          WHERE
            /* %JoinFKPK(dbo.Line,dbo.City," = "," AND") */
            dbo.Line.cityId2 = dbo.City.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Line because dbo.City exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Line on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City", FK_COLUMNS="cityId1" */
    IF EXISTS (SELECT * FROM deleted,dbo.City
      WHERE
        /* %JoinFKPK(deleted,dbo.City," = "," AND") */
        deleted.cityId1 = dbo.City.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Line
          WHERE
            /* %JoinFKPK(dbo.Line,dbo.City," = "," AND") */
            dbo.Line.cityId1 = dbo.City.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Line because dbo.City exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_Line ON dbo.Line FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Line */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Line on child update no action */
  /* ERWIN_RELATION:CHECKSUM="0002b3b8", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City1", FK_COLUMNS="cityId2" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(cityId2)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.City
        WHERE
          /* %JoinFKPK(inserted,dbo.City) */
          inserted.cityId2 = dbo.City.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Line because dbo.City does not exist.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Line on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Line"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Line_City", FK_COLUMNS="cityId1" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(cityId1)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.City
        WHERE
          /* %JoinFKPK(inserted,dbo.City) */
          inserted.cityId1 = dbo.City.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Line because dbo.City does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tD_Shop ON dbo.Shop FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on Shop */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.Shop  dbo.Transaction on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00032a7f", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Shop", FK_COLUMNS="shopId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Transaction
      WHERE
        /*  %JoinFKPK(dbo.Transaction,deleted," = "," AND") */
        dbo.Transaction.shopId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Shop because dbo.Transaction exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Shop  dbo.Article on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Article"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Article_Shop", FK_COLUMNS="shopId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Article
      WHERE
        /*  %JoinFKPK(dbo.Article,deleted," = "," AND") */
        dbo.Article.shopId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Shop because dbo.Article exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Shop on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Shop"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Shop_City", FK_COLUMNS="cityId" */
    IF EXISTS (SELECT * FROM deleted,dbo.City
      WHERE
        /* %JoinFKPK(deleted,dbo.City," = "," AND") */
        deleted.cityId = dbo.City.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Shop
          WHERE
            /* %JoinFKPK(dbo.Shop,dbo.City," = "," AND") */
            dbo.Shop.cityId = dbo.City.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Shop because dbo.City exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_Shop ON dbo.Shop FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Shop */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Shop  dbo.Transaction on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00039482", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Shop", FK_COLUMNS="shopId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Transaction
      WHERE
        /*  %JoinFKPK(dbo.Transaction,deleted," = "," AND") */
        dbo.Transaction.shopId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Shop because dbo.Transaction exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.Shop  dbo.Article on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Article"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Article_Shop", FK_COLUMNS="shopId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Article
      WHERE
        /*  %JoinFKPK(dbo.Article,deleted," = "," AND") */
        dbo.Article.shopId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Shop because dbo.Article exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Shop on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Shop"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Shop_City", FK_COLUMNS="cityId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(cityId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.City
        WHERE
          /* %JoinFKPK(inserted,dbo.City) */
          inserted.cityId = dbo.City.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Shop because dbo.City does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tD_Article ON dbo.Article FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on Article */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.Article  dbo.ArticleInOrder on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00024cd9", PARENT_OWNER="dbo", PARENT_TABLE="Article"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Article", FK_COLUMNS="articleId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.ArticleInOrder
      WHERE
        /*  %JoinFKPK(dbo.ArticleInOrder,deleted," = "," AND") */
        dbo.ArticleInOrder.articleId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Article because dbo.ArticleInOrder exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Shop  dbo.Article on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Article"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Article_Shop", FK_COLUMNS="shopId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Shop
      WHERE
        /* %JoinFKPK(deleted,dbo.Shop," = "," AND") */
        deleted.shopId = dbo.Shop.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Article
          WHERE
            /* %JoinFKPK(dbo.Article,dbo.Shop," = "," AND") */
            dbo.Article.shopId = dbo.Shop.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Article because dbo.Shop exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_Article ON dbo.Article FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Article */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Article  dbo.ArticleInOrder on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00028dee", PARENT_OWNER="dbo", PARENT_TABLE="Article"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Article", FK_COLUMNS="articleId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.ArticleInOrder
      WHERE
        /*  %JoinFKPK(dbo.ArticleInOrder,deleted," = "," AND") */
        dbo.ArticleInOrder.articleId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Article because dbo.ArticleInOrder exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.Shop  dbo.Article on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Article"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Article_Shop", FK_COLUMNS="shopId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(shopId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Shop
        WHERE
          /* %JoinFKPK(inserted,dbo.Shop) */
          inserted.shopId = dbo.Shop.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Article because dbo.Shop does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tD_Buyer ON dbo.Buyer FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on Buyer */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.Buyer  dbo.Order on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="0003260f", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_Buyer", FK_COLUMNS="buyerId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Order
      WHERE
        /*  %JoinFKPK(dbo.Order,deleted," = "," AND") */
        dbo.Order.buyerId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Buyer because dbo.Order exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Buyer  dbo.Transaction on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Buyer", FK_COLUMNS="buyerId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Transaction
      WHERE
        /*  %JoinFKPK(dbo.Transaction,deleted," = "," AND") */
        dbo.Transaction.buyerId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Buyer because dbo.Transaction exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Buyer on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Buyer"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Buyer_City", FK_COLUMNS="cityId" */
    IF EXISTS (SELECT * FROM deleted,dbo.City
      WHERE
        /* %JoinFKPK(deleted,dbo.City," = "," AND") */
        deleted.cityId = dbo.City.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Buyer
          WHERE
            /* %JoinFKPK(dbo.Buyer,dbo.City," = "," AND") */
            dbo.Buyer.cityId = dbo.City.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Buyer because dbo.City exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_Buyer ON dbo.Buyer FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Buyer */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Buyer  dbo.Order on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00038f9c", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_Buyer", FK_COLUMNS="buyerId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Order
      WHERE
        /*  %JoinFKPK(dbo.Order,deleted," = "," AND") */
        dbo.Order.buyerId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Buyer because dbo.Order exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.Buyer  dbo.Transaction on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Buyer", FK_COLUMNS="buyerId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Transaction
      WHERE
        /*  %JoinFKPK(dbo.Transaction,deleted," = "," AND") */
        dbo.Transaction.buyerId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Buyer because dbo.Transaction exists.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Buyer on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Buyer"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Buyer_City", FK_COLUMNS="cityId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(cityId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.City
        WHERE
          /* %JoinFKPK(inserted,dbo.City) */
          inserted.cityId = dbo.City.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Buyer because dbo.City does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tD_ArticleInOrder ON dbo.ArticleInOrder FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on ArticleInOrder */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.Article  dbo.ArticleInOrder on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="0002adc1", PARENT_OWNER="dbo", PARENT_TABLE="Article"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Article", FK_COLUMNS="articleId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Article
      WHERE
        /* %JoinFKPK(deleted,dbo.Article," = "," AND") */
        deleted.articleId = dbo.Article.id AND
        NOT EXISTS (
          SELECT * FROM dbo.ArticleInOrder
          WHERE
            /* %JoinFKPK(dbo.ArticleInOrder,dbo.Article," = "," AND") */
            dbo.ArticleInOrder.articleId = dbo.Article.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.ArticleInOrder because dbo.Article exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Order  dbo.ArticleInOrder on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Order", FK_COLUMNS="orderId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Order
      WHERE
        /* %JoinFKPK(deleted,dbo.Order," = "," AND") */
        deleted.orderId = dbo.Order.id AND
        NOT EXISTS (
          SELECT * FROM dbo.ArticleInOrder
          WHERE
            /* %JoinFKPK(dbo.ArticleInOrder,dbo.Order," = "," AND") */
            dbo.ArticleInOrder.orderId = dbo.Order.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.ArticleInOrder because dbo.Order exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_ArticleInOrder ON dbo.ArticleInOrder FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on ArticleInOrder */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Article  dbo.ArticleInOrder on child update no action */
  /* ERWIN_RELATION:CHECKSUM="0002d58d", PARENT_OWNER="dbo", PARENT_TABLE="Article"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Article", FK_COLUMNS="articleId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(articleId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Article
        WHERE
          /* %JoinFKPK(inserted,dbo.Article) */
          inserted.articleId = dbo.Article.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.ArticleInOrder because dbo.Article does not exist.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.Order  dbo.ArticleInOrder on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Order", FK_COLUMNS="orderId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(orderId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Order
        WHERE
          /* %JoinFKPK(inserted,dbo.Order) */
          inserted.orderId = dbo.Order.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.ArticleInOrder because dbo.Order does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tD_Transaction ON dbo.Transaction FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on Transaction */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.Order  dbo.Transaction on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="0003c6ee", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Order", FK_COLUMNS="orderId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Order
      WHERE
        /* %JoinFKPK(deleted,dbo.Order," = "," AND") */
        deleted.orderId = dbo.Order.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Transaction
          WHERE
            /* %JoinFKPK(dbo.Transaction,dbo.Order," = "," AND") */
            dbo.Transaction.orderId = dbo.Order.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Transaction because dbo.Order exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Shop  dbo.Transaction on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Shop", FK_COLUMNS="shopId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Shop
      WHERE
        /* %JoinFKPK(deleted,dbo.Shop," = "," AND") */
        deleted.shopId = dbo.Shop.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Transaction
          WHERE
            /* %JoinFKPK(dbo.Transaction,dbo.Shop," = "," AND") */
            dbo.Transaction.shopId = dbo.Shop.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Transaction because dbo.Shop exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Buyer  dbo.Transaction on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Buyer", FK_COLUMNS="buyerId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Buyer
      WHERE
        /* %JoinFKPK(deleted,dbo.Buyer," = "," AND") */
        deleted.buyerId = dbo.Buyer.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Transaction
          WHERE
            /* %JoinFKPK(dbo.Transaction,dbo.Buyer," = "," AND") */
            dbo.Transaction.buyerId = dbo.Buyer.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Transaction because dbo.Buyer exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_Transaction ON dbo.Transaction FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Transaction */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Order  dbo.Transaction on child update no action */
  /* ERWIN_RELATION:CHECKSUM="000446d0", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Order", FK_COLUMNS="orderId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(orderId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Order
        WHERE
          /* %JoinFKPK(inserted,dbo.Order) */
          inserted.orderId = dbo.Order.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Transaction because dbo.Order does not exist.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.Shop  dbo.Transaction on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Shop"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Shop", FK_COLUMNS="shopId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(shopId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Shop
        WHERE
          /* %JoinFKPK(inserted,dbo.Shop) */
          inserted.shopId = dbo.Shop.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    select @nullcnt = count(*) from inserted where
      inserted.shopId IS NULL
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Transaction because dbo.Shop does not exist.'
      GOTO error
    END
  END

  /* erwin Builtin Trigger */
  /* dbo.Buyer  dbo.Transaction on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Buyer", FK_COLUMNS="buyerId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(buyerId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Buyer
        WHERE
          /* %JoinFKPK(inserted,dbo.Buyer) */
          inserted.buyerId = dbo.Buyer.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    select @nullcnt = count(*) from inserted where
      inserted.buyerId IS NULL
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Transaction because dbo.Buyer does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER [TR_TRANSFER_MONEY_TO_SHOPS] ON Order
   WITH 
 EXECUTE AS CALLER  AFTER UPDATE 
  
  AS

begin
	declare @newState varchar(100)
	
	select @newState = [state] from inserted
	
	if @newState = 'arrived'
	begin
		-- porudzbina je stigla, isplacujemo novac prodavnicama
		declare @orderId bigint;
		select @orderId = id from inserted
		-- kreiramo kursor po prodavnicama i sumama proizvoda iz porudzbine koji poticu iz te prodavnice
		declare shopSumCursor cursor
		for
			select
				s.id,
				sum(a.price * aio.amount) - sum(a.price * aio.amount) * (s.discount / 100.00)
			from
				dbo.ArticleInOrder aio
			join
				dbo.Article a 
			on 
				aio.articleId = a.id
			join
				dbo.Shop s 
			on 
				a.shopId = s.id
			where
				aio.orderId = @orderId
			group by
				s.id,
				s.discount

		declare @shopId bigint;
		declare @shopSum decimal(10, 3)

		-- idemo kroz sve prodavnice koje su ucestvovale u porudzbini i isplacujemo im novac
		open shopSumCursor
		fetch next from shopSumCursor into @shopId, @shopSum

		while (@@fetch_status = 0)
		begin

			-- prodavnici se isplacuje ukupna suma umanjena za 5%
			declare @shopProfit decimal(10, 3)
			set @shopProfit = @shopSum - @shopSum * 0.05

			-- prodavnici se novac isplacuje u trenutku pristizanja porudzbine kod kupca
			declare @timeOfExecution datetime2(7)
			select @timeOfExecution = recievedTime from inserted
			-- upisujemo sumu u transakcioni log
			insert into [Transaction] 
				(orderId, shopId, timeOfExecution, transactionAmount, additionalDiscount)
			values (@orderId, @shopId, @timeOfExecution, @shopProfit, 0)

			-- dodajemo novac prodavnici (ovo je nebitno ali eto...)
			update Shop set balance = balance + @shopProfit where id = @shopId

			fetch next from shopSumCursor into @shopId, @shopSum

		end

		-- zatvaramo i dealociramo kursor
		close shopSumCursor	
		deallocate shopSumCursor
	end

end
 
go


ENABLE TRIGGER [TR_TRANSFER_MONEY_TO_SHOPS] ON Order
go

CREATE TRIGGER dbo.tD_Order ON dbo.Order FOR DELETE AS
/* erwin Builtin Trigger */
/* DELETE trigger on Order */
BEGIN
  DECLARE  @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)
    /* erwin Builtin Trigger */
    /* dbo.Order  dbo.Transaction on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="000568a5", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Order", FK_COLUMNS="orderId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.Transaction
      WHERE
        /*  %JoinFKPK(dbo.Transaction,deleted," = "," AND") */
        dbo.Transaction.orderId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Order because dbo.Transaction exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Order  dbo.ArticleInOrder on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Order", FK_COLUMNS="orderId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.ArticleInOrder
      WHERE
        /*  %JoinFKPK(dbo.ArticleInOrder,deleted," = "," AND") */
        dbo.ArticleInOrder.orderId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Order because dbo.ArticleInOrder exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Order  dbo.OrderPath on parent delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_Order", FK_COLUMNS="orderId" */
    IF EXISTS (
      SELECT * FROM deleted,dbo.OrderPath
      WHERE
        /*  %JoinFKPK(dbo.OrderPath,deleted," = "," AND") */
        dbo.OrderPath.orderId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30001,
             @errmsg = 'Cannot delete dbo.Order because dbo.OrderPath exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.City  dbo.Order on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_City", FK_COLUMNS="cityId" */
    IF EXISTS (SELECT * FROM deleted,dbo.City
      WHERE
        /* %JoinFKPK(deleted,dbo.City," = "," AND") */
        deleted.cityId = dbo.City.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Order
          WHERE
            /* %JoinFKPK(dbo.Order,dbo.City," = "," AND") */
            dbo.Order.cityId = dbo.City.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Order because dbo.City exists.'
      GOTO error
    END

    /* erwin Builtin Trigger */
    /* dbo.Buyer  dbo.Order on child delete no action */
    /* ERWIN_RELATION:CHECKSUM="00000000", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_Buyer", FK_COLUMNS="buyerId" */
    IF EXISTS (SELECT * FROM deleted,dbo.Buyer
      WHERE
        /* %JoinFKPK(deleted,dbo.Buyer," = "," AND") */
        deleted.buyerId = dbo.Buyer.id AND
        NOT EXISTS (
          SELECT * FROM dbo.Order
          WHERE
            /* %JoinFKPK(dbo.Order,dbo.Buyer," = "," AND") */
            dbo.Order.buyerId = dbo.Buyer.id
        )
    )
    BEGIN
      SELECT @errno  = 30010,
             @errmsg = 'Cannot delete last dbo.Order because dbo.Buyer exists.'
      GOTO error
    END


    /* erwin Builtin Trigger */
    RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.tU_Order ON dbo.Order FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Order */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Order  dbo.OrderPath on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="000123b3", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="OrderPath"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_OrderPath_Order", FK_COLUMNS="orderId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.OrderPath
      WHERE
        /*  %JoinFKPK(dbo.OrderPath,deleted," = "," AND") */
        dbo.OrderPath.orderId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Order because dbo.OrderPath exists.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.Trigger_397 ON dbo.Order FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Order */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Order  dbo.ArticleInOrder on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="000138ae", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="ArticleInOrder"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_ArticleInOrder_Order", FK_COLUMNS="orderId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.ArticleInOrder
      WHERE
        /*  %JoinFKPK(dbo.ArticleInOrder,deleted," = "," AND") */
        dbo.ArticleInOrder.orderId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Order because dbo.ArticleInOrder exists.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.Trigger_404 ON dbo.Order FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Order */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Order  dbo.Transaction on parent update no action */
  /* ERWIN_RELATION:CHECKSUM="0001344a", PARENT_OWNER="dbo", PARENT_TABLE="Order"
    CHILD_OWNER="dbo", CHILD_TABLE="Transaction"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Transaction_Order", FK_COLUMNS="orderId" */
  IF
    /* %ParentPK(" OR",UPDATE) */
    UPDATE(id)
  BEGIN
    IF EXISTS (
      SELECT * FROM deleted,dbo.Transaction
      WHERE
        /*  %JoinFKPK(dbo.Transaction,deleted," = "," AND") */
        dbo.Transaction.orderId = deleted.id
    )
    BEGIN
      SELECT @errno  = 30005,
             @errmsg = 'Cannot update dbo.Order because dbo.Transaction exists.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.Trigger_405 ON dbo.Order FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Order */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.Buyer  dbo.Order on child update no action */
  /* ERWIN_RELATION:CHECKSUM="00015d66", PARENT_OWNER="dbo", PARENT_TABLE="Buyer"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_Buyer", FK_COLUMNS="buyerId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(buyerId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.Buyer
        WHERE
          /* %JoinFKPK(inserted,dbo.Buyer) */
          inserted.buyerId = dbo.Buyer.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Order because dbo.Buyer does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go




CREATE TRIGGER dbo.Trigger_406 ON dbo.Order FOR UPDATE AS
/* erwin Builtin Trigger */
/* UPDATE trigger on Order */
BEGIN
  DECLARE  @numrows int,
           @nullcnt int,
           @validcnt int,
           @insid bigint,
           @errno   int,
           @severity int,
           @state    int,
           @errmsg  varchar(255)

  SELECT @numrows = @@rowcount
  /* erwin Builtin Trigger */
  /* dbo.City  dbo.Order on child update no action */
  /* ERWIN_RELATION:CHECKSUM="0001885f", PARENT_OWNER="dbo", PARENT_TABLE="City"
    CHILD_OWNER="dbo", CHILD_TABLE="Order"
    P2C_VERB_PHRASE="", C2P_VERB_PHRASE="", 
    FK_CONSTRAINT="FK_Order_City", FK_COLUMNS="cityId" */
  IF
    /* %ChildFK(" OR",UPDATE) */
    UPDATE(cityId)
  BEGIN
    SELECT @nullcnt = 0
    SELECT @validcnt = count(*)
      FROM inserted,dbo.City
        WHERE
          /* %JoinFKPK(inserted,dbo.City) */
          inserted.cityId = dbo.City.id
    /* %NotnullFK(inserted," IS NULL","select @nullcnt = count(*) from inserted where"," AND") */
    select @nullcnt = count(*) from inserted where
      inserted.cityId IS NULL
    IF @validcnt + @nullcnt != @numrows
    BEGIN
      SELECT @errno  = 30007,
             @errmsg = 'Cannot update dbo.Order because dbo.City does not exist.'
      GOTO error
    END
  END


  /* erwin Builtin Trigger */
  RETURN
error:
   RAISERROR (@errmsg, -- Message text.
              @severity, -- Severity (0~25).
              @state) -- State (0~255).
    rollback transaction
END

go



