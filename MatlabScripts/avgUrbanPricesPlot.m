function avgUrbanPricesPlot()
% Create a plot with the urban cells with the average value

cd('../General');
H = load('avgUrbanPrice.txt');
J = grpstats(H, [H(:,1)]);
steps = [1:1:600];
plot(steps, J(1,2:end), steps, J(2,2:end),steps, J(3,2:end))
hold on;
xlabel('time steps')
ylabel('avg Urban Prices')
title('Evolution of the avg Urban prices','FontSize',12)
legend('random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['avgUrbanPrices', 'eps'],'epsc')
hold off;
cd('../Functions');
end

