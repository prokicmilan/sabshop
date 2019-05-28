CREATE FUNCTION CalculateSystemProfit()
RETURNS decimal(10, 3)
AS
BEGIN
	declare @sumPaidByBuyers decimal(10, 3);
	declare @sumPaidToShops decimal(10, 3);
	select @sumPaidByBuyers = sum(transactionAmount) from [Transaction] where buyerId is not null
	select @sumPaidToShops = sum(transactionAmount) from [Transaction] where shopId is not null

	return @sumPaidByBuyers - @sumPaidToShops;
END
GO