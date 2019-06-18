create trigger TR_TRANSFER_MONEY_TO_SHOPS
on [dbo].[Order]
after update
as begin
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
				s.id

		declare @shopId bigint;
		declare @shopSum decimal(10, 3)

		-- idemo kroz sve prodavnice koje su ucestvovale u porudzbini i isplacujemo im novac
		open shopSumCursor
		fetch next from shopSumCursor into @shopId, @shopDiscount, @shopSum

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

			fetch next from shopSumCursor into @shopId, @shopDiscount, @shopSum

		end

		-- zatvaramo i dealociramo kursor
		close shopSumCursor	
		deallocate shopSumCursor
	end

end