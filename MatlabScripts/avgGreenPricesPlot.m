function avgGreenPricesPlot()
%% Create a plot with the average of green prices

cd('../General');
H = load('avgGreenPrice.txt');
I = grpstats(H, [H(:,1)]);
steps = [1:1:600];
plot(steps, I(1,2:end), steps, I(2,2:end),steps, I(3,2:end))
hold on;
xlabel('time steps')
ylabel('avg Green Prices')
title('Evolution of the avg green prices','FontSize',12)
legend('random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['avgGreenPrices', 'eps'],'epsc')
hold off;
cd('../Functions');
end

