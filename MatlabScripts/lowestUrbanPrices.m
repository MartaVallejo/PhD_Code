function lowestUrbanPrices()
% Create a plot with the urban cells with the lowest value

cd('../General');
H = load('minUrbanPrice.txt');
E = grpstats(H, [H(:,1)]);
steps = [1:1:600];
plot(steps, E(1,2:end), steps, E(2,2:end),steps, E(3,2:end))
hold on;
xlabel('time steps')
ylabel('min Urban Prices')
title('Evolution of the min Urban prices','FontSize',12)
legend('random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['minUrbanPrices', 'eps'],'epsc')
hold off;
cd('../Functions');
end

