function higherUrbanPricesPlot( )
% Create a plot with the urban cells with the highest value

cd('../General');
H = load('maxUrbanPrice.txt');
F = grpstats(H, [H(:,1)]);
steps = [1:1:600];
plot(steps, F(1,2:end), steps, F(2,2:end),steps, F(3,2:end))
hold on;
xlabel('time steps')
ylabel('max Urban Prices')
title('Evolution of the max Urban prices','FontSize',12)
legend('random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['maxUrbanPrices', 'eps'],'epsc')
hold off;
cd('../Functions');
end

