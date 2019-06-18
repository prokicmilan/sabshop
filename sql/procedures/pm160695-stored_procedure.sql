create procedure SP_FINAL_PRICE
	@orderId bigint,
	@price decimal(10, 3) out
as begin
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