function higherGreenPricesPlot()
% Create a plot with the collected green prices with the highest value

cd('../General');
H = load('maxGreenPrice.txt');
B = grpstats(H, [H(:,1)]);
Y = load('NonUrbanPrices.txt'); 
steps = [1:1:600];
D=Y;
D=max(D(:,2:end),[],2);
plot(steps, B(1,2:end), steps, B(2,2:end),steps, B(3,2:end), steps, D')
hold on;
xlabel('time steps')
ylabel('max Green Prices')
title('Evolution of the max Green prices','FontSize',12)
legend('random','GA','close','gather','Location','NorthWest')
handle=gcf;
saveas(handle, ['maxGreenPrices', 'eps'],'epsc')
hold off;
cd('../Functions');
end

